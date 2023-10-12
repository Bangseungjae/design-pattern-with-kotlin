# 데이터 흐름 제어(Data Flow Control)

동시성 문법인 채널(channel)과 흐름(flow)를 배울 것이다.

또한 집합 자료 구조(collection)와 함께 사용할 수 있는 고차 함수도 간략히 살펴볼 것이다.

채널이나 흐름과 굉장히 비슷한 API를 갖고 있기 때문이다.

이번에 다룰 내용

- 반응형 프로그래밍의 원칙
- 집합 자료 구조를 위한 고차 함수
- 동시성 자료 구조
- 순서열
- 채널
- 흐름

## 반응형 프로그래밍의 원칙

반응형 프로그래밍은 자료 스트림에 대한 조작으로서 로직을 표현하는 프로그래밍 패러다임으로, 함수형 프로그래밍을 기반으로 한다. 반응형 프로그래밍의 기초적인 개념은 ‘리액티브 선언문’(https://www.reactivemanifesto.org/ko)에 잘 정리돼 있다.

이 선언문에 의하면 의하면 반응형 프로그램은 다음의 특성을 모두 지녀야 한다.

- 응답성(responsive)
- 회복성(resilient)
- 유연성(elastic)
- 메시지 주도(message-driven)

# 리액티브 시스템은:

**응답성(Responsive): "** [시스템](https://www.reactivemanifesto.org/ko/glossary#System) 이 가능한 한 즉각적으로 응답하는 것을 응답성이 있다고 합니다. 응답성은 사용자의 편의성과 유용성의 기초가 되지만, 그것뿐만 아니라 문제를 신속하게 탐지하고 효과적으로 대처할 수 있는 것을 의미합니다. 응답성 있는 시스템은 신속하고 일관성 있는 응답 시간을 제공하고, 신뢰할 수 있는 상한선을 설정하여 일관된 서비스 품질을 제공합니다. 이러한 일관된 동작은 오류 처리를 단순화하고, 일반 사용자에게 신뢰를 조성하고, 새로운 상호작용을 촉진합니다.

**탄력성(Resilient):** 시스템이 [장애](https://www.reactivemanifesto.org/ko/glossary#Failure) 에 직면하더라도 응답성을 유지 하는 것을 탄력성이 있다고 합니다. 탄력성은 고가용성 시스템, 미션 크리티컬 시스템에만 적용되지 않습니다. 탄력성이 없는 시스템은 장애가 발생할 경우 응답성을 잃게 됩니다. 탄력성은 [복제](https://www.reactivemanifesto.org/ko/glossary#Replication), 봉쇄, [격리](https://www.reactivemanifesto.org/ko/glossary#Isolation), [위임](https://www.reactivemanifesto.org/ko/glossary#Delegation)에 의해 실현됩니다. 장애는 각각의 [구성 요소](https://www.reactivemanifesto.org/ko/glossary#Component) 에 포함되며 구성 요소들은 서로 분리되어 있기 때문에 이는 시스템이 부분적으로 고장이 나더라도, 전체 시스템을 위험하게 하지 않고 복구 할 수 있도록 보장합니다. 각 구성 요소의 복구 프로세스는 다른(외부의) 구성 요소에 위임되며 필요한 경우 복제를 통해 고가용성이 보장됩니다. 구성 요소의 클라이언트는 장애를 처리하는데에 압박을 받지 않습니다.

- **위임**(delegation): 콜센터에서 이렇게 말할 수 있다. “저희는 현재 인터넷 속도 문제를 해결하기 어렵습니다. 다른 곳으로 연결해 드리겠습니다.”
- **복제**(replication): 그러고 나서 이렇게 말할 수도 있다. “현재 많은 고객님이 대기 중이신 관계로 상담원을 추가 배치할 예정입니다.” 이는 다음 절의 유연성 원칙과도 연결된다.
- **격리**(containment) 또는 **고립**(isolation): 결국 자동응답기에서 이렇게 말한다. “기다기리 어려우시면 전화번호를 남겨 주시기 바랍니다. 상담이 가능할 때 전화 드리겠습니다.” 격리란 이 시스템이 갖고 있는 확장성 문제에서 고객을 분리시키는 것이다. 반면 고립이란 시스템에 통화 불안정이라는 문제가 있더라도 신경쓰지 않는 것이다.

**유연성(Elastic):** 시스템이 작업량이 변화하더라도 응답성을 유지하는 것을 유연성이라고 합니다. 리액티브 시스템은 입력 속도의 변화에 따라 이러한 입력에 할당된 [자원](https://www.reactivemanifesto.org/ko/glossary#Resource)을 증가시키거나 감소키면서 변화에 대응합니다. 이것은 시스템에서 경쟁하는 지점이나 중앙 집중적인 병목 현상이 존재하지 않도록 설계하여, 구성 요소를 샤딩하거나 복제하여 입력을 분산시키는 것을 의미합니다. 리액티브 시스템은 실시간 성능을 측정하는 도구를 제공하여 응답성 있고 예측 가능한 규모 확장 알고리즘을 지원합니다. 이 시스템은 하드웨어 상품 및 소프트웨어 플랫폼에 비용 효율이 높은 방식으로 [유연성](https://www.reactivemanifesto.org/ko/glossary#Elasticity) 을 제공합니다.

**메시지 구동(Message Driven):** 리액티브 시스템은 [비동기](https://www.reactivemanifesto.org/ko/glossary#Asynchronous) [메시지 전달](https://www.reactivemanifesto.org/ko/glossary#Message-Driven) 에 의존하여 구성 요소 사이에서 느슨한 결합, 격리, [위치 투명성](https://www.reactivemanifesto.org/ko/glossary#Location-Transparency) 을 보장하는 경계를 형성합니다. 이 경계는 [장애](https://www.reactivemanifesto.org/ko/glossary#Failure) 를 메시지로 지정하는 수단을 제공합니다. 명시적인 메시지 전달은 시스템에 메시지 큐를 생성하고, 모니터링하며 필요시 [배압](https://www.reactivemanifesto.org/ko/glossary#Back-Pressure) 을 적용함으로써 유연성을 부여하고, 부하 관리와 흐름제어를 가능하게 합니다. 위치 투명 메시징을 통신 수단으로 사용하면 단일 호스트든 클러스터를 가로지르든 동일한 구성과 의미를 갖고 장애를 관리할 수 있습니다. [논블로킹](https://www.reactivemanifesto.org/ko/glossary#Non-Blocking) 통신은 수신자가 활성화가 되어 있을 때만 [자원](https://www.reactivemanifesto.org/ko/glossary#Resource) 을 소비할 수 있기 때문에 시스템 부하를 억제할 수 있습니다.

## 집합 자료 구조를 위한 고차 함수

집합 자료 구조에 적용할 수 있는 모든 함수를 다루지는 못할 것이다. 가장 자주 사용되는 몇 가지만 설명할 것이다.

### 원소 매핑

각 문자에 대응하는 ASCII 값을 출력하는 코드를 작성해 보자.

```kotlin
val letters = 'a'..'z'
    val ascii = mutableListOf<Int>()
    for (l in letters) {
        ascii.add(l.toInt())
    }
```

간단한 작업임에도 꽤 많은 코드를 작성해야 하는 것을 볼 수 있다. 출력 리스트를 가변형으로 만들어야 하는 것도 문제다.

이번에는 map() 함수를 사용해서 같은 일을 하는 코드를 작성해 보자.

```kotlin
val result: List<Int> = ('a'..'z').map { it.toInt() }
```

보다시피 훨씬 짧게 구현할 수 있다. 가변 리스트를 선언할 필요도 없고 직접 for-in 반복문을 작성할 필요도 없다.

### 원소 필터링

예를 들어 1부터 100까지 범위의 수가 있을 때 3이나 5의 배수만 얻어 내고자 한다.

절차지향적으로 작성한 함수는 다음과 같이 생겼을 것이다.

```kotlin
val numbers: IntRange = 1..100
    val notFizzbuzz = mutableListOf<Int>()
    for (n in numbers) {
        if (n % 3 == 0 || n % 5 == 0) {
            notFizzbuzz.add(n)
        }
    }
```

함수형 버전에서는 filter() 함수를 사용한다.

```kotlin
val filtered: List<Int> = (1..100).filter { it % 3 == 0 || it % 5 == 0 }
```

이번에도 코드가 훨씬 간결해진 것을 볼 수 있다. 함수형 코드에서는 ‘무엇’이 필요한지만 표현한다. 즉 기준에 맞는 원소를 뽑아내라는 것만 코드에 표현하고 이를 ‘어떻게’할지(가령 if표현식을 이용하는 것)는 감춘다.

### 원소 검색

자료 구조에서 어떤 조건을 만족하는 첫 번째 원소를 찾는 것도 매우 일반적인 작업이다. 3과 5의 공약수를 찾는 함수는 다음과 같이 작성할 수 있을 것이다.

```kotlin
fun findFizzbuzz(numbers: List<Int>): Int? {
    for (n in numbers) {
        if (n % 3 == 0 && n % 5 == 0) {
            return n
        }
    }
    return null
}
```

find 함수를 사용하면 같은 일을 하는 함수를 다음과 같이 작성할 수 있다.

```kotlin
val found: Int? = (1..100).find { it % 3 == 0 && it % 5 == 0 }
```

절차지향적 스타일로 작성한 함수에서와 마찬가지로 find 함수는 조건을 만족하는 원소가 없을 때 null을 반환한다.

### 각 원소에 대해 코드 실행

앞서 소개한 모든 함수는 한 가지 특징이 있다. 함수의 결과가 스트림이라는 것이다. 하지만 모든 고차 함수가 스트림을 반환하는 것은 아니다. 어떤 함수는 Unit이나 숫자 같은 값 하나만 반환할 수 있다. 이런 함수를 종결 함수(terminator function)라고 한다.

이번에는 종결 함수를 하나 소개할 것이다. 종결 함수는 새로운 자료 구조를 반환하지 않고 다른 것을 반환한다. 따라서 종결 함수에는 다른 함수를 이어서 호출을 할 수 없다.

forEach() 함수는 Unit 타입을 반환한다. Unit 타입이란 자바의 void와 비슷하며 쓸모 있는 값을 반환하지 않음을 의미한다. 그래서 forEach() 함수는 보통의 for 반복문과 흡사하다.

```kotlin
val numbers = (0..5)

    numbers.map { it * it } // 연쇄 가능
        .filter { it < 20 } // 연쇄 가능
        .forEach{ println(it) } // 연쇄 종결
```

forEach() 함수는 전통적인 for 반복문에 비해 성능 면에서 약간 뒤처질 수 있다는 점을 유념하자.

`forEachIndexed()` 라는 함수도 있다. 이 함수를 사용하면 집합 자료 구조의 원소 값과 함께 인덱스 값도 얻을 수 있다.

```kotlin
numbers.map { it * it }
        .forEachIndexed{ index, value -> print("$index:$value, ") }
```

결과

```kotlin
0:0, 1:1, 2:4, 3:9, 4:16, 5:25,
```

코틀린 1.1 버전부터 onEach()라는 함수도 제공한다. 이 함수는 입력 자료 구조를 그대로 다시 반환하기 때문에 더 유용하게 쓸 수 있다.

```kotlin
numbers.map { it * it }
        .filter { it < 20 }
        .sortedDescending()
        .onEach { println(it) } // 이제 연쇄 가능
        .filter { it > 5 }
```

보다시피 종결 함수가 아니다.

### 원소의 총합

`forEach()` 함수처럼 `reduce()` 함수도 종결 함수다. 하지만 쓸모 없는 Unit으로 끝나지 않고 자료 구조의 원소 자료형과 동일한 타입의 값을 하나 반환한다.

`reduce()`를 실제로 사용하는 방법을 이해하기 위해서 1과 100사이의 모든 수를 더하는 코드를 작성해 보자.

```kotlin
val numbers = 1..100
var sum = 0
for (n in numbers) {
    sum += n
}
```

이번에는 reduce를 이용해 같은 기능을 하는 코드를 작성해 보자.

```kotlin
val reduced: Int = (1..100).reduce{ sum, n -> sum + n }
```

원소의 합을 저장하는 가변 변수를 선언하지 않아도 된다는 사실에 주목하라. 앞서 본 고차 함수와는 달리 `reduce()` 함수는 2개의 입력을 취한다. 첫 번째 인수는 누적 변수로, 절차지향적 코드의 sum과 같다. 두 번째 인수는 다음 원소를 가리킨다.

### 중첩 제거

다른 자료 구조를 원소로 갖는 자료 구조를 만날 때가 있다. 예를 들어 다음과 같은 코드를 보자.

```kotlin
val listOfLists: List<List<Int>> = listOf(listOf(1, 2), listOf(3, 4, 5), listOf(6, 7, 8))
```

그런데 만약 이 자료 구조를 중첩이 없는 하나의 리스트로 바꾸고 싶다면 어떻게 할까?

변환한 리스트를 출력해 보면 다음과 같을 것이다.

```kotlin
> [1, 2, 3, 4, 5, 6, 7, 8]
```

한 가지 방법은 입력 리스트를 순회하며 가변 리스트의 addAll 함수를 사용하는 것이다.

```kotlin
val flattened = mutableListOf<Int>()
    for (list in listOfLists) {
        flattened.addAll(list)
}
```

더 좋은 방법은 `flatMap()` 함수를 사용하는 것이다. 하는 일은 같다.

```kotlin
val flat: List<Int> = listOfLists.flatMap { it }
```

이 경우에는 `flatten()` 함수를 사용하면 더 간단하게도 만들 수 있다.

```kotlin
val flat: List<Int> = listOfLists.flatten()
```

하지만 일반적으로 `flatMap()` 함수가 더 쓸모가 많다. 각 원소 자료 구조에 다른 함수를 적용할 수 있기 때문이다.

마치 **어댑터**(adapter) 패턴과 비슷하다.

## 동시성 자료구조 소개

가장 중요한 동시성 자료 구조는 채널과 흐름이다. 하지만 이 두가지를 다루기 전에 우선 **순서열**(sequence)이라는 자료 구조를 먼저 살펴봐야 한다. 순서열 자체는 동시성 자료구조가 아니지만 동시성의 세계로 들어가는 다리를 놓아줄 것이다.

### 순서열

자바 개발자들은 자바 8버전에서야 스트림 API의 등장과 함께 처음으로 집합 자료 구조용 고차 함수를 사용할 수 있게 됐다.

스트리모가 집합 자료 구조와 사이에 중요한 차이가 있다. 집합 자료구조와는 달리 스트림은 무한히 길 수 있다는 점이다. 코틀린은 JVM에 한정되지 않으며 자바6까지의 하위 호환성을 보장하기 때문에 무한한 스트림을 지원하기 위한 다른 방법이 필요했다. 이렇게 탄생한 자료 구조는 자바의 스트림과 이름 충돌을 피하기 위해 **순서열**이라는 이름을 얻었다.

새 순서열은 `generateSequence()` 함수를 사용해서 만든다. 예를 들어 다음 함수는 무한한 순서열을 생성한다.

```kotlin
val seq: Sequence<Long> = generateSequence(1L) { it + 1 }
```

첫 번째 인수로는 순서열의 초깃값을, 두 번째 인수로는 이전 값을 이용해 다음 값을 만들어 내는 람다 함수를 전달한다. `generateSequence` 함수의 반환형은 `Sequence`다.

일반적인 자료 구조나 범위도 `asSequence()` 함수를 이용해 순서열로 변환할 수 있다.

```kotlin
(1..100).asSequence()
```

더 복잡한 로직을 이용해 순서열을 만들어야 한다면 다음과 같이 `sequence()` 함수를 사용할 수 있다.

```kotlin
val fibSeq: Sequence<Int> = sequence {
        var a = 0
        var b = 1
        yield(a)
        yield(b)
        while (true) {
            yield(a + b)
            val t = a
            a = b
            b += t
        }
    }
```

이 예제는 피보나치 수의 순서열을 만드는데 yield 함수를 이용해 다음 값을 반환한다.

순서열의 값을 사용할 때마다 마지막으로 `yield()`가 호출된 시점부터 코드의 실행이 재개될 것이다.

순서열이라는 개념 자체는 그다지 유용해 보이지 않을 수 있지만 순서열과 집합 자료 구조의 차이를 이해하는 것은 굉장히 중요하다. 순서열은 게으르며 집합 자료 구조는 부지런하다.

따라서 집합 자료 구조의 크기가 일정 수준을 넘어가면 고차 함수를 적용했을 때 보이지 않는 비용이 발생한다. 대부분의 고차 함수는 불변성을 유지하기 위해 자료 구조를 복사해서 사용할 것이기 때문이다.

이 차이를 이해하기 위해서 다음 코드를 살펴보자. 먼저 백만 개의 수가 들어 있는 리스트를 만들고, 각 수를 제곱하는 데에 어느 정도의 시간이 걸리는지 측정해 보자. 집합 자료 구조를 이용해서 측정한 뒤에 순서열을 이용해서 측정해 볼 것이다.

```kotlin
    val numbers = (1..1_000_000).toList()
    println(measureTimeMillis {
        numbers.map {
            it * it
        }.take(1).forEach { it }
    }) // ~50ms

    println(measureTimeMillis {
        numbers.asSequence().map {
            it * it
        }.take(1).forEach { it }
    })// ~5ms
```

`take()`를 이용해 이 함수를 이용해서 첫 번째 원소만 취해서 계산을 수행하도록 했다.

실행 결과를 보면 순서열을 이용한 코드가 훨씬 빠를 것이다. 이것은 순서열이 각 원소에 적용하는 연산을 게으르게 실행하기 때문이다. 즉 전체 리스트에서 원소 하나에 대해서만 제곱을 계산했다.

반면 집합 자료 구조에 적용한 함수는 전체 리스트에 대해 연산을 수행한다. 즉 모든 수의 제곱을 먼저 계산해서 새 자료 구조에 넣은 다음, 그 결과에서 첫 번째 수만 취한 것이다.

순서열과 채널, 흐름은 반응형 프로그래밍의 원칙을 따르기 때문에 이 원칙의 의미를 잘 이해하고 넘어갈 필요가 있다. 반응형 프로그래밍의 원칙은 꼭 함수형 프로그래밍에만 적용되는 것이 아니다. 객체지향적인 코드나 절차지향적 코드를 작성하면서도 반응형 프로그래밍을 할 수 있다. 하지만 역시 함수형 프로그래밍의 기초를 배우고 나면 반응형 프로그래밍을 더 쉽게 이해할 수 있다.

## 채널

코루틴 간에 통신을 하기 위해서는 어떻게 할까?

자바에서는 `wait()/notify()/notifyAll()` 패턴을 사용하거나 java.util.concerrent 패키지에 잘 갖춰진 여러 클래스(예를 들어 BlockingQueue)를 사용해서 스레드 간 통신을 한다.

눈치챘겠지만 코틀린에는 `wait()`이나 `notify()` 메서드가 없다. 대신 코틀린은 **채널**(channel)을 사용한다. 채널은 BlockingQueue와 매우 비슷하지만 스레드가 아닌 코루틴을 멈춘다. 스레드를 멈추는 것보다 훨씬 적은 비용으로 말이다.

다음의 단계를 통해 채널과 코루틴을 생성할 것이다.

1. 먼저 채널을 하나 만든다.

```kotlin
val chan = Channel<Int>()
```

채널은 타입을 갖는다. 이 채널은 정수만 받을 수 있다.

1. 다음으로는 이 채널에서 값을 읽는 코루틴을 생성한다.

```kotlin
launch {
    for (c in chan) {
        println(c)
    }
}
```

채널에서 값을 읽으려면 그냥 for-in 반복문을 사용하면 된다.

1. 이제 이 채널로 값을 전송해 보자. `send()` 함수를 사용하면 된다.

```kotlin
(1..10).forEach {
    chan.send(it)
}
chan.close()
```

1. 마지막으로 채널을 닫는다. 채널이 닫히면 이 채널에서 값을 읽던 코루틴은 for-in 반복문을 빠져나온다.

이런 통신 형태를 **순차 프로세스 통신**(Communicating Sequential Processing) 또는 줄여서 **CSP**라고 부른다.

보다시피 채널을 사용하면 여러 코루틴 간에 편리하고 타입 안전하게 통신이 가능하다. 하지만 채널을 직접 정의해 줘야 한다. 이어지는 두 절에서는 더욱 간단하게 채널을 다루는 법을 알아볼 것이다.

### 생성자 코루틴

채널에 값을 공급하는 코루틴이 필요하다면 produce() 함수를 사용해 생성자 **코루틴**(producer coroutine)을 만들 수 있다. 공급하고자 하는 값의 타입이 T라고 할 때 `produce()` 함수는 내부적으로 `RecieveChannel<T>`을 갖고 있는 코루틴을 생성한다.

위의 예제를 `produce()`로 다시 작성하면 다음과 같다.

```kotlin
fun main() {
    runBlocking {
        val chan = produce {
            (1..10).forEach {
                send(it)
            }
        }
        launch {
            for (c in chan) {
                println(c)
            }
        }
    }
}
```

produce() 블록 내에서는 바로 `send()` 함수를 사용해서 채널에 값을 공급할 수 있다는 것에 주목하자.

채널의 값을 소비하는 코루틴에서는 for-in 반복문 대신 `consumeEach()` 함수도 쓸 수 있다.

```kotlin
launch {
    chan.consumeEach {
        println(it)
    }
}
```

### 행위자 코루틴

**행위자 코루틴**(actor coroutine)을 만드는 `actor()` 함수는 `produce()` 함수와 마찬가지로 내부에 채널을 갖고 있는 코루틴을 생성한다. 다만 생산자가 채널에 값을 제공하는 역할을 했다면 행위자는 채널에서 값을 가져오는 역할을 한다.

```kotlin
runBlocking { 
    val actor = actor<Int> {
        channel.consumeEach {
            println(it)
        }
    }
    (1..10).forEach {
        actor.send(it)
    }
}
```

이 예제에서 메인 함수는 값을 생산하고 행위자는 채널을 통해 소비한다.

처음 봤던 예제와 비슷하지만 채널과 코루틴을 각각 만드는 대신 둘을 결합해 한 번에 만들었다는 점이 다르다.

### 버퍼가 있는 채널

채널을 명시적으로 또는 암시적으로 생성한 지금까지의 모든 예제는 버퍼가 없는 버전의 채널을 사용했다.

버퍼가 무엇인지 이해하기 위해 앞의 예제를 조금 바꿔 보자.

```kotlin
val actor = actor<Long> {
    var prev = 0L
    channel.consumeEach {
        println(it - prev)
        prev = it
        delay(100)
    }
}
```

여기서 actor객체는 앞의 예제와 비슷하다. 타임스탬프 값을 받아서 직전 타임 스탬프 값과의 차이를 반환한다. 그러고 나서 다음 값을 읽기 전에 약간의 시간 지연을 줬다.

메인 함수에서 이 actor 객체에 순서열이 아닌 현재 타임스탬프를 전송해 보자.

```kotlin
repeat(10) {
    actor.send(System.currentTimeMillis())
}
actor.close().also { println("전송 완료") }
```

이제 이 코드의 출력을 살펴보면 다음과 같다.

```text
102
101
101
102
전송 완료
```

채널이 다음 값을 받을 준비가 될 때까지 생성자가 멈춰 있는 것을 볼 수 있다. 즉 actor 객체가 자신이 준비될 때까지 다음 값을 전송하지 말라는 의미의 배압을 생산자에게 가하는 것이다.

이제 actor의 정의를 살짝 바꿔 보자

```kotlin
val actor = actor<Long>(capacity = 10) {
    ...
}
```

모든 채널에는 용량(capacity)이 있다. 용량의 기본값은 0이다. 즉 채널에 들어 있는 값이 소비되기 전까지는 아무도 채널에 새로운 값을 전송할 수 없다는 것이다.

이 코드를 다시 실행해 보면 완전히 다른 값을 얻는다.

```text
buffered 전송 완료
buffered: 1696752836908
buffered: 1
buffered: 0
buffered: 0
```

생산자는 더 이상 소비자를 기다리지 않는다. 채널에 버퍼가 생겼기 때문이다. 따라서 생산자는 보낼 수 있는 최대한의 속도로 메시지를 전송하고, 행위자는 자신의 속도에 맞춰 이를 처리할 수 있다.

capacity는 생산자 채널에서도 비슷한 방법으로 지정할 수 있다.,

```kotlin
val chan = produce(capacity = 10) {
    (1..10).forEach {
        send(it)
    }
}
```

뿐만 아니라 직접 채널을 선언할 때도 다음과 같이 용량을 지정할 수 있다.

```kotlin
val chan = Channel<Int>(10)
```

버퍼가 있는 채널은 생산자와 소비자 간의 결합을 끊어 줄 수 있는 매우 강력한 도구다. 하지만 세심한 주의를 기울여 사용해야 한다. 채널의 용량이 크다면 그 만큼 메모리 소비량도 많을 것이기 때문이다.

채널은 상대적으로 저수준의 동시성 도구다. 다음 절에서는 다른 유형의 스트림을 살펴볼 텐에 이 유형은 채널보다 더 높은 수준의 추상화를 제공해 준다.

## 흐름

**흐름**(flow)는 차가운 비동기 스트림으로, 관찰자 디자인 패턴을 구현하고 있다.

잠시 기억을 되살려 보자. 관찰자 디자인 패턴에는 2개의 메서드가 필요하다. 하나는 소비자가 메시지를 구독하는 `subscribe()` 함수이고, 다른 하나는 모든 구독자에게 메시지를 전송하는 `publish()` 함수다.

Flow 객체에서 `publish()`에 해당하는 함수는 `emit()`이라고 부르고, `subscribe()`에 해당하는 함수는 `collect()`라고 부른다.

다음과 같이 flow() 함수를 사용하면 새 흐름을 만들 수 있다.

```kotlin
val numbersFlow: Flow<Int> = flow {
	...
}
```

flo생성자 내에서는 `emit()` 함수를 사용해 모든 수신자에게 새 값을 전송할 수 있다.

예를 들어 다음 코드는 flow 생성자를 이용해 10개의 수를 전송한다.

```kotlin
flow {
      (1..10).forEach {
          println("Sending $it")
          emit(it)
    }
}
```

메시지를 전송하는 법은 알았으니 이제 흐름을 구독하는 방법을 살펴보자.

구독을 하려면 flow객체에 있는 `collect()` 함수를 사용해야 한다.

```kotlin
numbersFlow.collect { number ->
    println("$number 수신")
}
```

이 코드를 실행해 보면 수신자가 흐름에서 수신한 모든 수를 출력하는 것을 볼 수 있다.

여타 반응형 프레임워크나 라이브러리와는 달리 수신자에게 예외를 던지는 별도의 문법은 존재하지 않는다. 그냥 다음과 같이 일반적인 throw 식을 사용하면 된다.

```kotlin
flow {
    (1..10).forEach {
        ...
        if (it == 9) {
           throw RuntimeException()
        }
    }
}
```

수신 측에서 예외를 처리하려면 그냥 `collect()` 함수를 `try/catch` 블록으로 감싸면 된다.

```kotlin
try {
                    numbersFlow.collect { number ->
                        delay(1000)
                        println("Coroutine $coroutineId received $number")
                    }
                } catch (e: Exception) {
                    println("Coroutine $coroutineId got an error")
                }
```

흐름은 채널과 마찬가지로 코루틴을 일시 중단시킨다. 하지만 흐름은 동시성 자료 구조가 아니다. 또한 흐름은 배압을 지원하지만 사용자가 완전히 통제할 수 있다. 이것이 무슨 뜻인지 이해하기 위해서 여러 구독자가 하나의 흐름에서 값을 읽는 다음 예제를 살펴보자.

```kotlin
(1..4).forEach { coroutineId ->
    delay(5000)
    launch(Dispatchers.Default) {
        try {
            numbersFlow.collect { number ->
                delay(1000)
                println("${coroutineId}번 코루틴에서 $number 수신")
            }
        } catch (e: Exception) {
            println("Coroutine $coroutineId got an error")
        }
    }
}
```

이 코드의 결과를 보자.

```text
...
Sending 6
2번 코루틴에서 1 수신
Sending 2
1번 코루틴에서 6 수신
Sending 7
2번 코루틴에서 2 수신
Sending 3
1번 코루틴에서 7 수신
Sending 8
...
```

이 결과를 통해 두 가지 중요한 사실을 알 수 있다.

- **흐름은 차가운 스트림이다.**  구독자마다 처음부터 새로 시작한다는 뜻이다. 앞의 예제에서 각 구독자는 1부터 시작해서 모든 수를 받을 수 있었다.
- **흐름은 배압을 사용한다.** 즉 이전에 전송된 값을 수신 측에서 처리하기 전까지는 다음 수를 정송하지 않는다. 버퍼 없는 채널과 비슷하며 버퍼 있는 채널과는 다르다. 버퍼가 있는 채널에서는 생산자가 소비자의 소비 속도보다 더 빠르게 값을 전송할 수 있었다.

이제 필요에 따라 두 가지 속성을 바꾸는 방법을 알아보자.

### 버퍼 있는 흐름

생산자에게 바로 배압을 가하고 싶지 않을 때도 있다. 가령 메모리가 충분하다면 처음부터 배압을 가할 필요가 없다. 이를 위해서 각 소비자는 buffer() 함수를 사용해 흐름에 버퍼를 만들 수 있다.

```kotlin
numberFlow.buffer().collect {number ->
    delay(1000)
    println("${coroutineId}번 코루틴에서 $number 수신")
}
```

이 코드의 출력은 이전 예제와 매우 다르다.

```text
...
Sending 7
Sending 8
Sending 9
Sending 10
1번 코루틴에서 1 수신
1번 코루틴에서 2 수신
1번 코루틴에서 3 수신
1번 코루틴에서 4 수신
...
```

버퍼를 사용함녀 버퍼가 가득 차기 전까지는 소비자로부터 배압 없이 값을 만들어 낼 수 있다. 그리고 소비자는 자신의 속도에 맞춰서 값을 처리할 수 있다. 이러한 동작은 버퍼 있는 채널과 비슷하다. 사실 버퍼 있는 흐름은 내부적으로 버퍼 있는 채널을 사용해서 구현돼 있다.

각 메시지를 처리하는 데에 시간이 오래 걸린다면 버퍼 있는 채널을 쓰는 것이 좋다. 예를 들어 휴대전화에 있는 이미지를 업로드하는 작업을 생각해 보자. 당연히 이미지의 크기에 따라 업로드에 걸리는 시간은 다를 것이다. 이미지 업로드가 완료될 때까지 사용자 인터페이스를 멈추면 안 된다. 사용자 경험을 저해할 뿐만 아니라 반응형 프로그래밍의 원칙에도 어긋나기 때문이다.

대신 메모리 공간에 맞게 버퍼를 사용하면 사용자 인터페이스를 멈추지 않고도 네트워크 속도에 맞춰 업로드를 진행할 수 있다. 단 버퍼가 가득 차면 사용자 인터페이스가 멈출 것이다.

이미지는 절대 손실되면 안 되는 자료다. 하지만 손실을 감수할 수 있는 유형의 자료도 있다. 다음 절에서는 이런 자료를 다루는 방법을 살펴보자.

### 흐름 뭉개기

주가 변동 정보를 1초에 10번 생산하는 흐름이 있다고 하자. 사용자 인터페이스는 이 흐름을 활용해 최신 주가 정보를 표시해야 한다. 이런 흐름을 나타내기 위해 단순하게 0.1초마다 1씩 증가하는 수의 흐름을 만들어 보자.

```kotlin
val stock: Flow<Int> = flow { 
    var i = 0
    while (true) {
        emit(++i)
        delay(100)
    }
}
```

하지만 사용자 인터페이스까지 0.1초마다 갱신할 필요는 없다. 1초에 한 번 갱신해도 충분하다. 이전 절에서 했던 것처럼 단순히 collect() 함수를 사용하면 항상 생산자보다 과거의 정보를 얻게 될 것이다.

```kotlin
var seconds = 0
stock.collect { number ->
    delay(1000)
    seconds++
    println("${seconds}초 -> $number 수신")
}
```

```kotlin
1초 -> 1 수신
2초 -> 2 수신
3초 -> 3 수신
4초 -> 4 수신
5초 -> 5 수신
```

이렇게 출력되면 안 된다. 이런 식으로 동작하는 이유는 흐름에 배압이 작용해서 흐름을 느리게 만들었기 때문이다. 이전 절에서처럼 10개의 값을 저장하는 버퍼를 만들 수도 있다. 하지만 사용자 인터페이스의 갱신 속도가 흐름의 속도보다 10배나 느리기 때문에 10개 중에 9개 값을 버리는 로직이 필요할 것이다.

더 나은 방법은 흐름을 뭉개는 것이다. 흐름을 뭉갠다는 것은 모든 메시지를 저장하지 않는다는 뜻이다. 오직 맨 마지막 값만 저장한다. 다음 코드처럼 구현하면 된다.

```kotlin
stock.conflate().collect { number ->
    delay(1000)
    seconds++
    println("${seconds}초 -> $number 수신")
}
```

출력을 보자.

```text
5초 -> 40 수신
6초 -> 50 수신
7초 -> 60 수신
8초 -> 70 수신
9초 -> 80 수신
10초 -> 89 수신
11초 -> 99 수신
```

기대한 대로 값이 출력되는 것을 볼 수 있다. 주가가 평균적으로 초당 10씩 증가한다.

이 흐름은 절대 코루틴을 중단시키지 않을 것이며 구독자는 늘 흐름의 최신 값을 받게 될 것이다.
