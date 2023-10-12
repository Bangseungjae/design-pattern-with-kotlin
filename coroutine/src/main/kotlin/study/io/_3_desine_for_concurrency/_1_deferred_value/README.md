# 값 지연 패턴(Deferred Value Pattern)

**값 지연**(deferred value) 디자인 패턴은 비동기 계산 로직이 결과를 반환하는 대신 결과값을 가리키는 참조를 반환하도록 한다. **자바**와 **스칼라**의 **Future**, **자바스크립트**의 **Promise**가 모두 값 지연 패턴을 구현한다.

이미 코틀린에서의 값 지연 사용례를 봤다. `async()` 함수가 Deffered라는 타입을 반환하는 것을 기억할 것이다. 이 타입이 바로 값 지연 패턴을 구현한다.

흥미로운 것은 Deffered가 3장에서 배운 **프록시** 패턴과 **상태 패턴**도 구현하고 있다는 점이다.

비동기 계산의 결과를 담는 통을 만들기 위해서는 CompletableDeferred의 생성자를 사용한다.

```kotlin
val deferred = CompletableDeferred<String>()
```

Deffered에 실제 결과가 담기도록 하려면 `complete()` 함수를 사용한다. 만약 계산 과정에서 오류가 발생하면 `completeExceptionally()` 함수를 통해 호출자에게 예외를 전달할 수 있다. 이해를 돕기 위해 다음과 같이 비동기 계산 결과를 반환하는 함수를 작성해 보자. 이 함수는 절반의 확률로 OK로 문자열로 반환하고 다른 절반의 확률로 예외를 던진다.

```kotlin
suspend fun valueAsync(): Deferred<String> = coroutineScope {
    val deferred = CompletableDeferred<String>()
    launch {
        delay(100)
        if (Random.nextBoolean()) {
            deferred.complete("OK")
        } else {
            deferred.completeExceptionally(
                RuntimeException()
            )
        }
    }
    deferred
}
```

Deferred는 거의 즉시 반환되는 것을 볼 수 있다. 그 이후에 launch를 이용해 비동기 계산을 시작하는데 delay() 함수를 통해 시간이 걸리는 계산을 모사한다.

계산이 비동기적으로 이뤄지므로 결괏값을 바로 얻을 수는 없다. 결과가 준비될 때까지 기다리려면 `await()` 함수를 사용한다.

```kotlin
runBlocking {
    val value = valueAsync()
    println(value.await())
}
```

반드시 `complete()`나 `completeExceptionally()` 함수로 Deferred에 값을 채워 줘야 한다는 점에 유의하라. 그렇지 않으면 프로그램은 결과가 준비될 때까지 기약 없이 기다릴 수도 있다. 결괏값이 필요 없어지면 지연된 계산을 취소할 수도 있다. 그냥 cancel() 함수를 호출하면 된다.

```kotlin
deferred.cancel()
```

사실 지연된 값을 직접 만들 일은 별로 없다. 대개 async() 함수가 반환한 값을 사용하는 것으로 충분하다.
