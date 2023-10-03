# 어댑터 패턴(Adapter Pattern)
어댑터(adapter) 다자인 패턴은 어떤 인터페이스를 다른 인터페이스로 변환하고자 할 때 사용한다.

이른바 돼지코라고 하는 전원 플러그 어댑터나 USB 어댑터를 생각하면 이해하기 쉽다.

### 예시

한밤중 휴대폰 배터리가 없어 USB-C 타입 충전기를 찾고있다. 그러나 호텔에 있는 충전기는 USB-A타입이다. 어떻게 할까? 한밤중이지만 미니 USB에서 USB-C로 변환하는 어댑터를 구해 본다.

이제 똑같은 원리가 어떻게 코드에 적용되는지 알아보자.

PlugTypeF는 전원을 Int로 간주한다. 값이 1이면 전원이 공급되는 것이며 다른 값이면 끊어지는 것이다.

```kotlin
interface PlugTypeF {
    val hasPower: Int
}
```

PlugTypeA는 전원을 String으로 다룬다. 값이 TRUE이면 전원이 공급되는 상태를, FALSE면 공급되지 않는 상태를 의미한다.

```kotlin
interface PlugTypeA {
    val hasPower: String // "TRUE" 또는 "FALSE"
}
```

UsbMini 클래스는 전원을 enum으로 관리한다.

```kotlin
interface UsbMini {
    val hasPower: Power 
}

enum class Power {
    TRUE, FALSE
}
```

마지막으로 UsbTypeC에서는 전원이 Boolean 값이다.

```kotlin
interface UsbTypeC {
    val hasPower: Boolean
}
```

최종적인 목표는 전원 값이 F타입 플러그에서 휴대전화까지 흐르게 하는 것이다. 이 시나리오는 다음의 함수로 표현된다.

```kotlin
fun cellPhone(chargeCable: UsbTypeC) {
    if (chargeCable.hasPower) {
        println("충전 중입니다!")
    } else {
        println("전원이 연결되지 않습니다.")  
    }
}
```

먼저 F 타입 전원 콘센트를 나타내는 코드를 구현해 보자. PlugTytpeF 객체를 반환하는 함수가 될 것이다.

```kotlin
// 전원 콘센트는 PlugTypeF 인터페이스 노출
fun krPowerOutlet(): PlugTypeF {
    return object : PlugTypeF {
        override val hasPower = 1
    }
}
```

익명 클래스를 만드는데 object 키워드를 사용했다. 익명 클래스란 즉성에서 만든 클래스로, 보통 인터페이스를 즉석에서 구현할 때 활용한다.

휴대전화 충전기는 plugtypeA를 입력으로 받아 UsbMini를 반환한다.

```kotlin
// 충전기는 plugTypeA 인터페이스를 입력받고 UsbMini 인터페이스를 노출
fun charger(plug: PlugTypeA): UsbMini {
    return object : UsbMini {
        override val hasPower: Power = Power.valueOf(plug.hasPower)
    }
}
```

다음으로 cellPhone, charger, krPowerOutlet 함수를 모두 연결해 보자.

```kotlin
    cellPhone(
        // Type mismatch: inferred type is UsbMini but UsbTypeC was expected
        charger(
            // Type mismatch: inferred type is USPlug but EUPlug was expected
            usPowerOutlet()
        )
    )
```

타입 오류가 2개 발생하는 것을 볼 수 있다. 어댑터 디자인을 사용하면 이런 문제를 해결할 수 있다.

## 기존 코드에 어댑터 패턴 적용

필요한 어댑터는 두 가지. 하나는 전원 플러그 어댑터이고 다른 하나는 USB 포트 어댑터다.

자바에서는 보통 이런 상황에서 클래스를 2개 만들 것이다. 하지만 코틀린에서는 클래스를 만들지 않고 확장 함수를 사용한다.

F타입 콘센트에 A타입 플러그를 사용할 수 있도록 해주는 어댑터를 다음과 같이 확장 함수로 구현할 수 있다.

```kotlin
fun PlugTypeF.toPlugTypeA(): PlugTypeA {
    val hasPower = if (this.hasPower == 1) "TRUE" else "FALSE"
    return object : PlugTypeA {
        // 전원 연결
        override val hasPower = hasPower
    }
}
```

미니 USB와 USB-C 사이의 어댑터도 비슷한 방법으로 구현할 수 있다.

```kotlin
fun UsbMini.toUsbTypeC(): UsbTypeC {
    val hasPower = this.hasPower == Power.TRUE
    return object : UsbTypeC {
        override val hasPower = hasPower
    }
}
```

마지막으로 어댑터를 사용해서 다음과 같은 모둔 함수를 하나로 연결할 수 있다.

```kotlin
cellPhone(
        charger(
            krPowerOutlet().toPlugTypeA()
        ).toUsbTypeC()
    )
```

## 실제 코드에서 사용되는 어댑터 패턴

이미 어댑터 디자인 패턴을 본 적이 많이 있을 것이다. 일반적으로는 개념과 구현 사이의 변환을 수행하기 위해 어댑터 패턴을 사용한다. JVM의 집합 자료 구조 개념과 스트림(stream) 개념을 예로 들어 보자.

```kotlin
val list = listOf("a", "b", "c")
```

스트림이란 게으른 원소 집합이다. 그런데 스트림을 입력으로 받는 함수에 리스트를 전달할 수 없다. 의미상으로는 아무 문제도 없음에도 그렇다.

```kotlin
fun printStream(stream: Stream<String>) {
	stream.forEach(e -> println(e))
}
printStream(list) //컴파일 오류
```

다행히 집합 자료 구조에는 .stream()이라는 어댑터 메서드가 존재한다.

```kotlin
printStream(list.stream()) // 성공적으로 변환된다.
```
