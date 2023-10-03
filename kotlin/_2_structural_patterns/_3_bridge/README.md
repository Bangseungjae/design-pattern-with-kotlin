# 브릿지 패턴(Bridge Pattern)

어댑터 디자인 패턴을 사용하면 레거시 코드를 쉽게 사용할 수 있다.

브릿지 디자인 패턴은 상속을 남용하는 것을 막아준다.

은하제국 지상국 소속의 각종 트루퍼(보병)을 관리하는 시스템을 만든다고 하자.

먼저 트루퍼를 나타내는 인터페이스를 하나 정의할 것이다.

```kotlin
interface Trooper {
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y: Long)
}
```

```kotlin
class StormTrooper : Trooper {
    override fun move(x: Long, y: Long) {
        // 보통 속도로 이동
    }

    override fun attackRebel(x: Long, y: Long) {
        // 대부분 빗나감
    }
}

class ShockTrooper : Trooper {

    override fun move(x: Long, y: Long) {
        // 일반적인 StormTrooper보다는 느리게 이동
    }

    override fun attackRebel(x: Long, y: Long) {
        // 명중할 때도 있음
    }
}
```

더 강한 버전도 있다.

```kotlin
class RiotControlTrooper : StormTrooper() {
    override fun attackRebel(x: Long, y: Long) {
        // 전기 충격 곤봉을 가졌다. 물러서!
    }
}

class FlameTrooper : ShockTrooper() {
    override fun attackRebel(x: Long, y: Long) {
        // 화염방사기를 사용한다. 위험!
    }
}
```

다른 트루퍼보다 빠르게 이동할 수 있는 스카우트 트루퍼도 있다.

```kotlin
class ScoutTrooper : ShockTrooper() {
    override fun move(x: Long, y: Long) {
        // 빠른 속도로 이동
    }
}
```

정의할 클래스가 정말 많다.

어느날 친애하는 디자이너가 다가와서 새로운 요청 사항을 전달한다. 모든 스톰트루퍼가 소리를 지를 수 있어야 하며 내용은 각각 달라야 한다는 것이다. 별 생각 없이 인터페이스에 새 함수를 추가한다.

```kotlin
interface Infantry {
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y: Long)
    fun shout(): String
}
```

shout() 함수를 추가하는 순간 이 인터페이스를 구현하는 모든 클래스가 컴파일 되지 않는다. 심지어 개수가 적지 않다.

### 변경 사항에 다리 놓기

브리지 디자인 패턴의 핵심 아이디어는 클래스 계층 구조를 얕게 만듦으로써 시스템에서 구체 클래스에 수를 줄이는 것이다. 뿐만 아니라 부모 클래스를 수정했을 때 자식 클래스에서 발견하기 어려운 버그가 발생하는 현상을 뜻하는 ‘깨지기 쉬운 클래스 문제’를 예방하는데 도움이 된다.

먼저 왜 이렇게 복잡한 클래스 계층이 생겼는지 알아보자. 그 이유는 서로 무관한 두 가지의 독립적인 속성이 있기 때문이다. 바로 무기 종류와 이동 속도다.

상속을 사용하지 않고 이 속성들을 생성자로 전달하기를 원한다고 하자. 이 클래스는 지금까지 사용한 인터페이스를 똑같이 구현한다.

```kotlin
typealias PointsOfDamage = Long
typealias Meters = Int

data class StormTrooper(
    private val weapon: Weapon,
    private val legs: Legs,
) : Trooper {

    override fun move(x: Long, y: Long) {
        legs.move(x, y)
    }
    override fun attackRebel(x: Long, y: Long) {
        weapon.attack(x, y)
    }
}

interface Weapon {
    fun attack(x: Long, y: Long): PointsOfDamage
}

interface Legs {
    fun move(x: Long, y: Long): Meters
}
```

메서드에서 Long이나 Int를 반환하지 않고 Meter와 PointOfDaemage를 반환하고 있는 것에 주목하라. 이 기능을 **타입 별칭**이라고 한다.

이점

- 코드에 의미가 더 잘 드러난다. 반환값의 ‘의미’가 무엇인지 정확하게 표현할 수 있다.
- 코드가 더 간결해진다. 타입 별칭을 사용하면 복잡한 제네릭 표현식을 숨길 수 있다.

### 상수

StormTrooper 클래스로 다시 돌아가 보자. Weapon과 Legs 인터페이스를 실제로 구현할 차례다.

먼저 StormTrooper의 보통 데미지와 보통 속도를 미터법 단위를 사용해서 정의하자.

```kotlin
const val RIFLE_DAMAGE = 3L
const val REGULAR_SPEED: Meters = 1
```

상수는 컴파일 시에 결정되는 값이기 때문에 매우 효율적이다.

이제 인터페이스를 구현해 보자.

```kotlin
class Rifle : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE
}
class Flamethrower : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE * 2
}
class Batton : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE * 3
}
```

그 다음에는 이동에 관한 인터페이스를 구현하자.

```kotlin
class RegularLegs : Legs {
    override fun move(x: Long, y: Long): Meters = REGULAR_SPEED
}
class AthleticLegs : Legs {
    override fun move(x: Long, y: Long): Meters = REGULAR_SPEED * 2
}
```

드디어 복잡한 클래스 계층 구조 없이도 동일한 기능을 구현할 수 있게 됐다.

```kotlin
fun main() {
    val stormTrooper = StormTrooper(Rifle(), RegularLegs())
    val flameTrooper = StormTrooper(Flamethrower(), RegularLegs())
    val scoutTrooper = StormTrooper(Rifle(), AthleticLegs())
}
```

현업에서 브리지 패턴은 의존성 주입과 함께 사용될 때가 많다. 예를 들어 브리지 패턴을 사용하면 실제 데이터베이스를 사용하는 구현체를 손쉽게 목(mock)객체로 대체할 수 있다. → 테스트 코드 작성 쉬워짐
