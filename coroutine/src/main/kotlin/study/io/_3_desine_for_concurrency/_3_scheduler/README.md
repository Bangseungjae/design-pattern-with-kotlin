# 스케줄러 패턴(Scheduler Pattern)

스케줄러 디자인 패턴의 목적은 실행의 대상(무엇)과 방법(어떻게)을 분리하고, 실행에 소요되는 자원 사용을 최적화 하는 것이다.

코틀린에서는 분배기(dispatcher)가 스케줄러 디자인 패턴을 구현하고 있다. 즉 분배기는 코루틴(무엇)과 그것이 실행되는 스레드 풀(어떻게)을 분리한다.

`launch()`나 `async()` 같은 코루틴 생성기를 호출할 때 어떤 분배기를 사용할지 지정할 수 있다.

다음 예제는 분배기를 명시적으로 지정하는 코드다.

```kotlin
runBlocking {
        // 이렇게 하면 부모 코루틴의 분배기를 사용한다.
        launch {
            // main을 출력
            println(Thread.currentThread().name)
        }
        launch(Dispatchers.Default) {
            // DefaultDispatcher-worker-1을 출력
            println(Thread. currentThread().name)
        }
    }
```

기본 분배기는 사용 중인 스레드 풀에 있는 CPU 개수만큼 스레드를 생성한다. 다음 코드에서처럼 **입출력 분배기**도 사용할 수 있다.

```kotlin
async {
    for (i in 1..1000) {
        println(Thread.currentThread().name)
        yield()
    }
}
```

이 코드의 출력은 다음과 같다.

```
DefaultDispatcher-worker-1
DefaultDispatcher-worker-2
DefaultDispatcher-worker-2
DefaultDispatcher-worker-2
DefaultDispatcher-worker-2
DefaultDispatcher-worker-2
```

입출력 분배기는 실행 시간이 오래 걸리거나 다른 코드의 실행을 막고 있을 법한 작업을 수행하기 위해 사용한다. 이 분배기에는 최대 64개의 스레드가 만들어질 수 있다. 앞의 예제는 그다지 오래 걸리지 않았기 때문에 입출력 분배기가 많은 스레드르 만들지 않아도 됐다. 출력된 worker 스레드의 수가 적은 것도 이 때문이다.

```kotlin
val forkJoinPool = ForkJoinPool(4).asCoroutineDispatcher()

repeat(1000) {
    launch(forkJoinPool) {
        println(Thread.currentThread().name)
    }
}
```

직접 분배기를 만들었다면 다른 곳에서 재활용할 계획이 아닌 이상 `close()`를 호출해서 자원을 해제해야 한다. 분배기는 꽤 많은 자원을 붙들고 있기 때문이다.
