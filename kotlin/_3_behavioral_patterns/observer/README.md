# 관찰자 패턴(Observer Pattern)

## 관찰자 패턴이란?

먼저 발행자(publisher)가 하나 있고, 이를 구독하는 여러 구독자(subscriber)가 있다. 각각 주제에(topic)과 관찰자(observer)라고 부른다.

발행자에게 어떤 일이 발생하면 모든 구독자가 그 사실을 알게 된다.

**중개인** 패턴 처럼 보일 수 있지만 조금 다르다. 구독자는 런타임에 구독 및 구독 취소가 가능하다.

전통적으로는 모든 구독자가 특정 이벤트를 나타내는 인터페이스를 구현하면 발행자가 이벤트가 발생했음을 알리기 위해 이 인터페이스를 사용하는 방식으로 구현한다. 하지만 코틀린에는 고차 함수가 있기 때문에 더 간단히 구현할 수 있다. 발행자는 여전히 구독 및 구독 취소를 할 수 있는 창구를 마련해야 한다.

## 예제: 동물 합창단

동물들이 모여 합창단을 만들기로 했다. 고양이가 지휘자로 뽑혔다.

문제는 동물들이 자바 세상을 탈출했기 때문에 공통의 인터페이스를 갖고 있지 않다는 점이다. 대신 모두가 울음소리를 내는 제각각의 방법을 갖고 있다.

```kotlin
class Bat {
    fun screech() {
        println("Eeeeeee")
    }
}

class Turkey {
    fun gobble() {
        println("Gob-gob")
    }
}

class Dog {
    fun bark() {
        println("Woof")
    }

    fun howl() {
        println("Auuuu")
    }
}
```

다행히 고양이가 그저 노래를 못해서 지휘자로 뽑힌 것은 아니었다. 고양이는 이 책 내용을 잘 따라올 만큼 똑똑하기도 하다. 그래서 코틀린 세계에서는 함수를 받는 것도 가능하다는 사실을 이해하고 있다.

```kotlin
class Cat {
	fun joinChoir(whatToCall: ()->Unit) {
		//...
	}

	fun leaveChoir(whatNotToCall: ()->Unit){
		//...
	}
}
```

메서드 참조 연산자(::) 활용

```kotlin
val catTheConductor = Cat()

    val bat = Bat()
    val dog = Dog()
    val turkey = Turkey()

    catTheConductor.joinChoir(bat::screech)
    catTheConductor.joinChoir(dog::howl)
    catTheConductor.joinChoir(dog::bark)
    catTheConductor.joinChoir(turkey::gobble)
```

고양이는 이제 모든 구독자를 어떻게든 저장해야 한다. 맵을 사용하면 될 것 같다. 그럼 무엇이 키(key)가 돼야 할까? 함수 자체를 키로 쓰면 된다.

```kotlin
class Cat {
    private val participants = mutableMapOf<() -> Unit, () -> Unit>()

    fun joinChoir(whatToCall: () -> Unit) {
        participants[whatToCall] = whatToCall
    }
		// ...
}
```

() → Unit 이 너무 많이 등장해서 어지럽다면 typealias를 통해 더 의미 있는 이름을 지어줄 수 있다는 점을 기억하라

방금 박쥐가 합창단을 탈퇴하기로 했다.

```kotlin
class Cat {
    private val participants = mutableMapOf<() -> Unit, () -> Unit>()
		//...
    fun leaveChoir(whatNotToCall: () -> Unit) {
        participants.remove(whatNotToCall)
    }
		//...
}
```

박쥐가 해야 할 일은 다음과 같이 구독할 때 전달했던 함수를 다시 전달하는 것 뿐이다.

```kotlin
catTheConductor.leaveChoir(bat::screech)
```

이게 바로 맵을 사용한 이유다. 이제 고양이는 모든 합창단원을 불러 모아 노래를 시킬 수 있다.(울음소리 내기)

```kotlin
typealias Times = Int
class Cat {
		//...
    fun conduct() {
        for (p in participants.values) {
            p()
        }
    }
}
```

리허설이 잘 끝났다. 그런데 고양이가 모든 반복문을 수행하기는 너무 벅차 보인다. 그래서 합창단원들에게 역할을 위임하려고 한다. 그건 식은 죽 먹기다.

```kotlin
class Cat {
    private val participants = mutableMapOf<(Int) -> Unit, (Int) -> Unit>()

    fun joinChoir(whatToCall: (Int) -> Unit) {
			//...
    }

    fun leaveChoir(whatNotToCall: (Int) -> Unit) {
			//...
    }

    fun conduct(n: Times) {
        for (p in participants.values) {
            p(n)
        }
    }
}
```

합창단원들은 새로운 인수를 받기 위해 함수를 조금 수정해야 한다. 예를 들어 칠면조(Turkey)는 다음과 같이 함수를 수정해야 한다.

```kotlin
class Turkey {
    fun gobble(repeat: Times) {
        for (i in 1..repeat) {
            println("꾸륵꾸륵")
        }
    }
}
```

그런데 약간 문제가 있다. 만약 고양이가 단원들에게 어떤 소리(높은 소리 또는 낮은 소리)를 낼지 지시해야 한다면? 다시 모든 구독자 함수와 고양이의 구현을 변경해야 할 것이다.

<br/>
발행자를 설계할 때는 여러 속성을 갖는 하나의 데이터 클래스를 전달하도록 하라. 그렇게 하지 않고 여러 개의 자료를 전달하면 새 속성이 추가될 때마다 구독자의 코드를 모두 고쳐야 할 것이다.

```kotlin
enum class SoundPitch {HIGH, LOW}
data class Message(
    val repeat: Times,
    val pitch: SoundPitch
)
class Bat {
    fun screech(message: Message) {
        for (i in 1..message.repeat) {
            println("${message.pitch} 이----")
        }
    }
}
```

여기서는 울음소리의 높낮이를 나타내기 위해 enum을 사용하고, 높낮이와 반복 횟수를 캡슐화하기 위해 데이터 클래스를 사용했다.

<br/>
전달하는 메시지 객체는 반드시 불변 객체로 만들어아. 그렇게 하지 않으면 이상한 동작을 보게 될 것이다. 같은 발행자가 여러 종류의 메시지를 발행하려면 어떻게 할까? 이때는 스마트 캐스팅을 사용할 수 있다.

```kotlin
interface Message {
    val repeat: Times,
    val pitch: SoundPitch
}

data class LowMessage(override val repeat: Times) : Message {
    override val pitch: SoundPitch = SoundPitch.LOW
}

data class HighMessage(override val repeat: Times) : Message {
    override val pitch: SoundPitch = SoundPitch.HIGH
}

class Bat {
    fun screech(message: Message) {
        for (i in 1..message.repeat) {
            when (message) {
                is HighMessage -> {
                    println("${message.pitch} 이----")
                }
                else -> println("낼 수 없는 소리에요 :(")
            }
        }
    }
}
```

<br/>

main code
```kotlin
fun main() {
    val catTheConductor = Cat()
    val bat = Bat()
    val dog = Dog()
    val turkey = Turkey()

    val lowMessage = LowMessage(1)
    val highMessage = HighMessage(2)

    catTheConductor.joinChoir { bat.screech(lowMessage) }
    catTheConductor.joinChoir { dog.bark(highMessage) }
    catTheConductor.joinChoir { dog.howl(highMessage) }
    catTheConductor.joinChoir { turkey.gobble(lowMessage) }

    catTheConductor.conduct(1)
}
```

결과

```
낼 수 없는 소리에요 :(
HIGH Woof
HIGH Woof
HIGH Auuuu
HIGH Auuuu
LOW 꾸륵꾸륵
```

관찰자 패턴은 굉장히 쓸모가 많다. 이 패턴의 강점은 유연성이다. 발행자는 호출할 함수의 시그니처 외에는 구독자에 관해 아무것도 알 필요가 없다. 반응형 프레임워크에서도 널리 사용되며 안드로이드 UI이벤트도 모두 구독자로 구현돼 있다.
