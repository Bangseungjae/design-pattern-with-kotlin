# 뮤택스(Mutex)

상호 배제(mutual exclusion)라는 뜻의 **뮤텍스**는 여러 코루틴이 동시에 접근할 수 있는 공유 상태를 보호하기 위해 사용한다.

늘 사용하는 공포의 ‘숫자 세기’ 예제로 시작해 보자. 다음 코드에서는 여러 동시성 작업이 하나의 counter 변수의 값을 바꾸려고 한다.

```kotlin
fun main() {
    runBlocking {
        var counter = 0
        val jobs = List(10) {
            async(Dispatchers.Default) {
                repeat(1000) {
                    counter++
                }
            }
        }
        jobs.awaitAll()
        println(counter)
    }
}
```

이미 예상하겠지만 출력되는 counter 변수의 값은 10,000보다 작다. 상당히 당황스럽다.

이 문제를 해결하기 위해 한 번에 오로지 하나의 코루틴만 변수에 접근할 수 있도록 하는 락(lock)을 도입할 수 있다. 락을 이요애하면 작업을 원자적으로 만들 수 있다.

각 코루틴은 락을 통해 counter 변수의 소유권을 얻으려고 시도할 것이다. 만약 다른 코루틴이 counter의 값을 바꾸고 있다면 잠시 기다렸다가 다시 락을 시도한다. counter를 모두 업데이트한 뒤에는 반드시 락을 해제해서 다른 코루틴도 변수에 접근할 수 있도록 해야 한다.

```kotlin
var counter = 0
val mutex = Mutex()
val jobs = List(10) {
    async(Dispatchers.Default) {
        repeat(1000) {
            mutex.lock()
            counter++
            mutex.unlock()
        }
    }
}
```

이제 이 코드는 항상 10,000이라는 올바른 값을 출력한다.

코틀린 뮤텍스는 자바와 다르다. 자바에서 뮤텍스에 lock()을 호출하면 락을 걸 수 있을 때까지 스레드가 멈춘다. 반면 코틀린에서는 스레드가 아닌 코루틴이 멈춘다. 덕분에 동시성이 향상되고 락에 드는 비용이 줄어든다.

간단한 경우에는 이런 코드로도 충분하다. 하지만 `lock()`과 `unlock()` 사이의 임계 영역이 예외를 던진다면 어떻게 될까?

그런 경우에는 코드를 `try…catch` 블록으로 감싸야 한다. 매우 성가신 일이다.

```kotlin
try {
    mutex.lock()
    counter++
} finally {
    mutex.unlock()
}
```

finally 블록을 빼먹으면 락이 해제되지 않을 것이다. 그러면 락이 해제되기를 기다리는 모든 코루틴은 더 이상 진행이 불가능하며 데드락이 발생할 수도 있다.

```kotlin
mutex.withLock { 
    counter++
}
```

딱 이런 상황에 사용할 수 있도록 코틀린은 `withLock()` 함수를 제공한다.
