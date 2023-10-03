# 전략 패턴(Strategy Pattern)

전략 디자인 패턴의 목표는 객체의 동작을 런타임에 변경하는 것이다.

모든 무기를 캐릭터가 바라보는 방향으로 탄환을 발사하는 게임을 만들어보자.

탄환이 한 종류뿐이였다면 정적 팩토리 메서드 패턴을 사용해서 다음과 같이 간단하게 구현할 수 있었을 것이다.

```kotlin
package ch4

enum class Direction {
    LEFT, RIGHT
}

data class Projectile(
    private var x: Int,
    private var y: Int,
    private var direction: Direction,
)

class OurHero {
    private var direction = Direction.LEFT
    private var x: Int = 42
    private var y: Int = 173

    fun shoot(): Projectile {
        return Projectile(x, y, direction)
    }
}
```

그러나 마이클은 최소한 세 종류의 탄환을 구현하기 원한다.

- 콩알총: 직선으로 날아가는 콩알탄을 발사. 캐릭터가 기본으로 장착
- 석류탄: 마치 수류탄처럼 적에게 맞으면 폭발
- 바나나: 화면 끝에 닿으면 부메랑처럼 되돌아온다.

## 과일 무기 구현하기

자바에서는 변경되는 부분을 추상화하는 인터페이스를 만든다. 과일 무기 예제에서는 변경되는 부분이 무기이기 때문에 다음과 같이 Weapon 인터페이스를 선언한다.

```kotlin
interface Weapon {
    fun shoot(
        x: Int,
        y: Int,
        direction: Direction,
    ): Projectile
}
```

인터페이스 구현

```kotlin
interface Weapon {
    fun shoot(
        x: Int,
        y: Int,
        direction: Direction,
    ): Projectile
}

// 직선으로 날아가는 콩알총
class Peashooter : Weapon {
    override fun shoot(
        x: Int,
        y: Int,
        direction: Direction
    ): Projectile = Projectile(x, y, direction)
}

// 화면 끝에 닿으면 되돌아오는 바나나
class Banana : Weapon {
    override fun shoot(x: Int, y: Int, direction: Direction): Projectile = 
				Projectile(x, y, direction)
}
// 비슷한 방법으로 다른 구현체도 추가할 수 있다.
```

이 게임의 모든 무기는 동일한 인터페이스를 구현한다. 즉 항상 shoot 메서드를 덮어쓴다.

주인공 캐릭터는 무기 객체에 대한 참조를 들고 있을 것이다. 처음에는 콩알총을 들고있다.

```kotlin
private var currentWeapon: Weapon = Peashooter()
```

실제 발사 동작은 들고 있는 무기 객체에 위임한다.

```kotlin
fun shoot(): Projectile = currentWeapon.shoot(x, y, direction)
```

이제 다른 무기를 장착하는 기능만 구현하면 된다.

```kotlin
fun equip(weapon: Weapon) {
        currentWeapon = weapon
    }
```

이것이 전략 패턴의 전부다. 전략 패턴을 사용하면 알고리듬(예제이서의 무기)을 교체할 수 있다.

<br/>

## 일급 객체로서의 함수

코틀린에서는 이렇게 많이 작성하지 않고 더 효율적으로 같은 기능을 구현할 수 있다.

코틀린의 함수가 **일급 객체**라는 사실 덕분이다.

코틀린에서는 다음과 같이 손쉽게 변수에 함수를 저장할 수 있다.

```kotlin
var square = fun(x: Int): Long {
    return (x * x).toLong()
}
```

먼저 모든 무기를 정의할 네임스페이스를 만들 것이다. 이를 위해서는 object 키워드를 사용하면 된다.

꼭 네임스페이스를 만들어야 하는 것은 아니지만 좀 더 안전한 방법이다. 각 무기 대신 하나의 함수를 정의할 수 있다.

```kotlin
object Weapons {
    // 직선으로 날아가는 콩알총
    fun peashooter(
        x: Int,
        y: Int,
        direction: Direction,
    ) = Projectile(x, y, direction)
    
    // 화면 끝에 닿으면 되돌아오는 바나나
    fun banana(
        x: Int,
        y: Int,
        direction: Direction,
    ) = Projectile(x, y, direction)
    
    // 비슷한 방법으로 다른 구현체도 추가할 수 있다.
}
```

보다시피 인터페이스를 구현하는 대신 같은 매개변수와 반환형을 갖는 함수를 여러 개 구현했다.

가장 흥미로운 부분은 주인공 객체다. OutHero 클래스는 이제 2개의 값을 갖는데 둘 다 함수다.

```kotlin
class OurHero {
    private var direction = Direction.LEFT
    private var x: Int = 42
    private var y: Int = 173

    var currentWeapon = Weapons::peashooter

    fun shoot(): Projectile = currentWeapon(x, y, direction)
}
```

기본 무기를 한 번 발사하고 다른 무기로 교체해서 다시 발사하는 코드

```kotlin
fun main() {
    val hero = OurHero()
    hero.shoot()
    hero.currentWeapon = Weapons::banana
    hero.shoot()
}
```

메서드 참조 연산자(::)가 처음 등장했다. 이 연산자를 사용하면 메서드를 호출하는 대신 마치 변수처럼 참조할 수 있다.

전략 패턴은 런타임에 애플리케이션의 동작을 변경하고 싶을 때 매우 유용하게 사용할 수 있다. 예를 들어 초과 예약을 허용하는 비행 예약 시스템을 만든다고 치자. 비행 하루 전까지는 초과 예약을 허용했다가 그 이후로는 초과 예약을 받지 않도록 하고 싶다. 이럴 때는 비행 하루 전에 전략을 교체하면 된다.
