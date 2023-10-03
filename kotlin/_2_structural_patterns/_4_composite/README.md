# 합성 패턴(Composite Pattern)

장 전체가 객체 합성에 관한 내용이다.

이전 절에서 사용했던 트루퍼 예제를 이어가 보자. 은하제국의 장교들은 아무리 좋은 장비를 갖추더라도 제대로 된 지휘 체제가 없기 때문에 반란군에 맞서 싸우기 어렵다는 사실을 발견했다.

지휘 체계를 개선하기 위해 제국은 분대라는 개념을 도입한다.

- 분대는 종류에 관계없이 1명 이상의 트루퍼로 구성되며, 명령이 주어지면 마치 한몸처럼 똑같이 움직여야 한다.

분대를 나타내는 Squad 클래스는 당연히 Trooper 객체의 집합으로 이뤄진다.

```kotlin
class Squad(val units: List<Trooper>)
```

먼저 분대를 2개만 만들어 보자.

```kotlin
val bobaFett = StormTrooper(Rifle(), RegularLegs())
val squad = Squad(listOf(bobaFett.copy(), bobaFett.copy(), bobaFett.copy()))
```

분대가 한몸처럼 움직이도록 하기 위해 move와 attack이라는 메서드를 추가할 것이다.

```kotlin
class Squad(val units: List<Trooper>) {
    fun move(x: Long, y: Long) {
        for (u in units) {
            u.move(x, y)
        }
    }
    fun attack(x: Long, y: Long) {
        for (u in units) {
            u.attackRebel(x, y)
        }
    }
}
```

Trooper 인터페이스에 새로움 함수를 추가하면 어떻게 될까? 다음 코드를 살펴보자

```kotlin
interface Trooper {
	fun move(x: Long, y: Long)
	fun attackRebel(x: Long, y: Long)
	fun retreat()
}
```

컴파일이 깨지지는 않는 것 같다. 그러나 Squad 클래스는 더 이상 한몸처럼 움직일 수 없다. 개별 트루퍼에게 있는 메서드가 분대에는 존재하지 않기 때문이다.

이런 문제를 방지하기 위해 Squad 클래스가 소속 분대원과 마찬가지로 Trooper 인터페이스를 구현하도록 하면 어떻게 되는지 살펴보자.

```kotlin
class Squad(private val units: List<StormTrooper>): Trooper {...}
```

Trooper 인터페이스를 변경함에 따라 이제 retreat 함수도 구현해야 하며, 나머지 2개 함수에는 override 키워드를 추가해야 한다.

이 문제를 해결하는 더 편리한 방법을 살펴보자. 이 방법을 사용하면 동일한 객체를 생성할 수 있지만 결과적으로 더 사용하기 편리한 합성 객체가 만들어진다.

## 부 생성자

트루퍼 리스트를 생성자에 전달하는 대신 리스트로 감싸지 않고 트추퍼 객체들은 직접 전달하고 싶을 수도 있다.

```kotlin
val squad = Squad(bobaFett.copy(), bobaFett.copy(), bobaFett.copy())
```

한 가지 방법은 Squad 클래스에 **부 생성자**를 추가하는 것이다.

지금까지 항상 클래스의 주 생성자만 사용했다. 주 생성자는 클래스 이름 뒤에 선언하는 생성자다. 하지만 더 많은 생성자를 정의하는 것도 가능하다. 부 생성자는 클래스 본문 안에 constructor 키워드를 사용해서 정의한다.

```kotlin
class Squad(val units: List<Trooper>) {  
    constructor(): this(listOf())
    constructor(t1: Trooper): this(listOf(t1))
    constructor(t1: Trooper, t2: Trooper): this(listOf(t1, t2))
}
```

부 생성자는 반드시 주 생성자를 호출해야 한다는 점에 유의하라. 자바에서 super 키워드를 사용하는 것과 비슷하다.

## varargs 키워드

위 방법은 누가 몇개의 인수를 전달할지 미리 알 길이 없기 때문에 좋은 방법은 아니다.

자바의 가변인수를 떠올렸을 것이다. 자바에서는 줄임표를 사용해 가변 인수를 선언한다. 예를 들면 Trooper… units와 같다.

코틀린에서는 같은 목적으로 varargs 키워드를 사용한다.

```kotlin
class Squad(val units: List<Trooper>) {
    constructor(vararg units: Trooper):
            this(units.toList())
...
```

이제 굳이 리스트로 감싸지 않고도 여러 트루퍼 객체를 사용해서 분대를 만들 수 있다.

```kotlin
val squad = Squad(bobaFett.copy(), bobaFett.copy(), bobaFett.copy())
```

## 합성 객체로 이뤄진 합성 객체 중첩하기

합성 디자인 패턴에는 재미있는 부분이 또 있다. 여러 트루퍼 객체를 담고 있는 분대를 만들 수 있다는 것을 확인했다. 그런데 여러 분대로 이뤄진 분대도 만들 수 있다.

```kotlin
val platoon = Squad(Squad(), Squad())
```

이제 소대에 명령을 내리면 분대에 명령을 내릴 때와 같은 방식으로 동작할 것이다. 이 패턴을 사용하면 아무리 복잡한 트리 구조라도 만들어 낼 수 있으며 모든 노드에 어떤 동작을 지시할 수 있다.

합성 디자인 패턴은 반복자(iterator) 디자인 패턴과 짝을 이룬다.

따라서 반복자 패턴을 배우기 전까지는 다소 불안해 보일 수 있다.

### 현업에서의 사용

합성 디자인 패턴은 사용자 인터페이스 프레임워크에서 널리 사용된다. 예를 들어 안드로이드의 Group 위젯은 합성 디자인 패턴을 구현하고 있다.

계층 구조에 속한 모든 객체가 같은 인터페이스를 구현하는 한, 계층 구조가 아무리 깊더라도 최상위 객체만 접근할 수 있다면 모든 하위 객체도 제어할 수 있다.
