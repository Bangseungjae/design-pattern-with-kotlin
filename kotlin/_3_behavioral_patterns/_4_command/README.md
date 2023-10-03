# 명령 패턴(Command Pattern)

명령 디자인 패턴을 사용하면 객체 내부에 동작을 캡슐화해서 넣어 둔 뒤 나중에 실행되도록 할 수 있다. 동작 실행을 지연시키면 한꺼번에 동작을 실행할 수도 있고, 심지어 동작의 실행 타이밍을 세밀하게 조절할 수도 있다.

이전 트루퍼 관리 시스템 예시로 돌아가 보자. 앞의 예제에서 attack과 move 함수를 다음과 같이 구현했다고 하자.

```kotlin
class Stormtrooper(...) {
	fun attack(x: Long, y: Long) {
		println("($x, $y) 공격 중")
		// 여기에 실제 코드 구현
	}

	fun move(x: Long, y: Long) {
		println("($x, $y)로 이동 중")
		// 여기에 실제 코드 구현
	}
}
```

이제 트루퍼가 한 번에 기억할 수 있는 명령이 하나밖에 되지 않는다는 문제를 해결해야 한다. 간단히 말해 다음과 같다. 예를 들어 어떤 트루퍼가 화면의 좌상단을 나타내는 (0, 0)에 있다고 하자. 만약 (20, 0) 명령과 (20, 20)으로 이동하라는 move(20, 20) 명령을 연달아 내리면 이 객체는 (0, 0)에서 (20, 20)으로 직선으로 이동할 것이다. 결국 반란군을 만나 전멸할지도 모른다.

```text
[스톰트루퍼](0, 0) -> 기대하는 이동 방향 -> (20, 0)
                                        ||
     [반란군][반란군]                       ||
  [반란군][반란군][반란군]
     [반란군][반란군]                      (20, 20)
                                        (5, 20)
```

<br/>

```kotlin
class Trooper {
    private val orders = mutableListOf<Any>()

    fun addOrder(order: Any) {
        this.orders.add(order)
    }
    // 여기에 구현 추가
}
```

다음으로 리스트에 대해 반복을 수행하며 명령을 실행하기 원한다.

```kotlin
class Trooper {
		... 
    // 외부 코드에서 가끔씩 실행하는 함수
    fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order = orders.removeFirst()
            order.execute() // 현재는 컴파일 오류 발생
        }
    }
}
```

집합 자료 구조를 마치 큐 처럼 사용할 수 있도록 하는 `removeFirst()` 함수도 있다.

명령 디자인 패턴이 낯설더라도 이 코드가 동작하도록 하려면 execute()라는 함수 하나를 갖는 인터페이스를 구현하면 된다는 것 정도는 짐작할 수 있다.

```kotlin
interface Command {
    fun execute()
}
```

어떤 종류의 명령이든 이 인터페이스를 필요에 맞게 구현한다.

위 코드는 아래의 코드와 동일하다.

```kotlin
interface Command: Unit {
    fun execute()
}
```

위 코드는 아래와 같이 더욱 간결하게 작성할 수 있다.

```kotlin
() -> Unit
```

이제 Command라고 하는 인터페이스 대신 typealias를 사용한 별칭을 만들 것이다.

```kotlin
typealias Command = () -> Unit
```

이제 Commad 인터페이스는 필요 없어졌으므로 지워도 된다.

이번에는 다음 코드가 컴파일 되지 않는다.

```kotlin
command.execute() // Unresolved reference: execute
```

execute()는 직접 인터페이스를 정의할 때 붙여 줬던 이름이기 때문이다. 코틀린에서 함수 객체를 호출할 때는 Invoke()를 사용한다.

```kotlin
command.invoke() // 컴파일 성공
```

invoke()를 생략할 수도 있다. 그러면 다음과 같은 코드가 된다.

```kotlin
fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order = orders.removeFirst()
            order()
        }
    }
```

썩 괜찮다. 하지만 현재 Command는 매개변수를 받을 수 없다. 함수에 인수가 있으면 어떻게 할까?

Command의 시그니처를 변경해서 2개의 매개변수를 받도록 하는 것도 한 가지 방법이다.

```kotlin
(x: Int, y: Int) -> Unit
```

그러나 만약 어떤 명령은 인수를 받지 않고,  어떤 명령은 하나나 둘, 또는 그 이상의 인수를 받는다면? 각 명령이 Invoke()에 무엇을 전달해야 하는지 기억해야 할 것이다.

더 좋은 방법이 있다!

**함수 생성기(function gernerator)**를 사용하는 것이다. 함수 생성기란 다른 함수를 반환하는 함수를 뜻한다. 자바스크립트를 사용해 본 적이 있다면 시야를 제한하고 상태를 기억하기 위해 클로저를 사용하는 것이 일반적이라는 점을 알 것이다. 여기서도 같은 방법을 사용한다.

```kotlin
val moveGenerator = fun(
    trooper: Trooper,
    x: Int,
    y: Int
): Command {
    return fun() {
        trooper.move(x, y)
    }
}
```

적절한 인수를 전달하면 moveGenerator 함수는 또 다른 함수를 반환한다. 이 함수는 알 맞은 때에 언제든 호출할 수 있으며, 다음의 세 가지를 기억하고 있다.

- 어떤 메서드를 호출할지
- 어떤 인수를 사용할지
- 어떤 객체에 대해 사용할지

이제 Trooper 클래스에는 다음과 같은 메서드가 필요할 것이다.

```kotlin
fun appendMove(x: Int, y: Int) = apply { 
        orders.add(moveGenerator(this, x, y))
    }
```

이 함수를 사용하면 다음과 같이 **유창한 문법(fluent syntax)**을 사용해서 아름다운 코드를 작성할 수 있다.

```kotlin
fun main() {
    val trooper = Trooper()
    trooper.appendMove(20, 0)
        .appendMove(20, 20)
        .appendMove(5, 20)
        .executeOrders()
}
```

다른 함수를 입력으로 받거나 반환하는 함수를 **고차 함수**(higher-order funtion)라고 한다.

전체 코드

```kotlin
package ch4

fun main() {
    val trooper = Trooper()
    trooper.appendMove(20, 0)
        .appendMove(20, 20)
        .appendMove(5, 20)
        .executeOrders()
}

class Trooper {
    private val orders = mutableListOf<Command>()

    fun addOrder(order: Command) {
        this.orders.add(order)
    }

    fun appendMove(x: Int, y: Int) = apply {
        orders.add(moveGenerator(this, x, y))
    }

    // 외부 코드에서 가끔씩 실행하는 함수
    fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order = orders.removeFirst()
            order()
        }
    }
    fun move(x: Int, y: Int) {
        println("Moving to $x:$y")
    }
}

typealias Command = () -> Unit

val moveGenerator = fun(
    trooper: Trooper,
    x: Int,
    y: Int
): Command {
    return fun() {
        trooper.move(x, y)
    }
}
```

## 명령 실행 취소

지금까지의 내용과 직접적인 관련은 없지만, 명령 디자인 패턴의 장점 중 하나는 명령 실행을 되돌릴 수 있다는 점이다. 이런 실행 취소를 구현하고 싶다면 어떻게 해야 할까?

실행 취소는 일반적으로 상당히 까다로운 기능이다. 다음 세 가지 중 하나에 해당하기 때문이다.

- 이전 상태를 반환(클라이언트가 여럿인 경우 많은 메모리가 필요하기 때문에 불가능)
- 차이점을 게산(구현하기 까다로움)
- 역연산 정의(항상 가능하지는 않음)

예제에서 ‘(0, 0)에서 (0, 20)으로 이동’ 명령의 반대는 ‘어딘가에서 (0, 0)으로 이동’일 것이다. 다음과 같이 명령의 쌍을 저장해서 이를 구현할 수 있다.

```kotlin
private val commands = mutableListOf<Pair<Command, Command>>()
```

명령 취소를 포함한 수정된 전체 코드

```kotlin
package ch4

fun main() {
    val trooper = Trooper()
    trooper.appendMove(20, 0)
        .appendMove(20, 20)
        .appendMove(5, 20)
        .executeOrders()
}

class Trooper {
    private val orders = mutableListOf<Pair<Command, Command>>()

//    fun addOrder(order: Command) {
//        this.orders.add(order)
//    }

    fun appendMove(x: Int, y: Int) = apply {
        orders.add(moveGenerator(this, x, y) to moveGenerator(this, 0 - x, 0 - y))
    }

    // 외부 코드에서 가끔씩 실행하는 함수
    fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order: Pair<() -> Unit, () -> Unit> = orders.removeFirst()
            order.first()
        }
    }
    fun move(x: Int, y: Int) {
        println("Moving to $x:$y")
    }
}

typealias Command = () -> Unit

val moveGenerator = fun(
    trooper: Trooper,
    x: Int,
    y: Int
): Command {
    return fun() {
        trooper.move(x, y)
    }
}
```

<br/>

## 정리

명령패턴은 명령을 매개 변수로서 받아서 실행한다.

명령은 인터페이스 대신 typealias로 두면 더욱 간단하다.

명령에 어떤 매개 변수가 올지 모를 수 있다. 인터페이스의 함수를 추가하지 말고 함수 생성기를 사용하자.
