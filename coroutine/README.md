# 스레드와 코루틴

### 배울 내용

- 스레드 심화
- 코루틴 및 일시 중단 함수 소개
- 코루틴 시작하기
- 작업
- 코루틴의 내부 동작 이해하기
- 구조화된 동시성

JVM이 제공하는 가장 기본적인 동시성 모델은 **스레드**다. 스레드를 사용하면 두 코드를 동시에(하지만 병렬적이지는 않을 수도 있다) 실행해서 여러 개의 CPU 코어를 더욱 잘 활용할 수 있게 된다.

하나의 프로세스 내에 수백 개의 스레드가 만들어질 수 있다.

프로세스와 달리 스레드 간에 손쉽게 자료를 공유할 수 있다.

먼저 자바에서 스레드를 2개 생성하는 방법을 배워보자. 각 스레드는 0부터 100까지의 수를 출력한다.

```kotlin
for (int t = 0; t < 2; t++) {
            int finalT = t;
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Thread " + finalT + ": " + i);
                }
            }).start();
        }
```

---

> ‘병렬’은 특정 시점에 실제로 여러 작업이 동시에 실행된다는 뜻으로, parallel을 번역한 것이다.
’동시’는 한 작업이 모두 끝나고 나서야 다른 작업을 시작하는 순차 실행과 대비되는 개념으로, concurrent를 번역한 것이다. 방법에는 여러 코어를 갖고 병렬 실행하는 방법, 프로세서 자원을 시간적으로 분할해 한 작업이 끝나기 전에도 다른 작업을 수행하고 있도록 하는 방법 등이 있다.
>

```kotlin
Thread 1: 7
Thread 0: 6
Thread 0: 7
Thread 1: 8
Thread 1: 9
Thread 1: 10
Thread 1: 11
Thread 0: 8
Thread 0: 9
Thread 0: 10
```

출력은 실행할 때마다 달라지며 언제 스레드 전환이 일어날지도 알 수 없다는 점에 유의하자.

위의 코드를 코틀린으로 작성하면 다음과 같다.

```kotlin
repeat(2) { t ->
        thread {
            for (i in 1..100) {
                println("T$t: $i")
            }
        }
    }
```

코틀린에서는 스레드 생성을 도와주는 함수 덕분에 보일러플레이트 코드를 덜 작성할 수 있다. 자바에서처럼 스레드를 시작하기 위해 start() 함수를 호출하지 않아도 된다. 스레드는 기본적으로 생성 즉시 실행된다. 만약 나중에 실행하고자 한다면 start 매개변수를 false로 설정한다.

```kotlin
val t = thread(start = false)
...
// 나중에
t.start()
```

자바의 **데몬 스레드**도 유용한 개념이다. 데몬 스레드는 실행 중에도 JVM이 종료될 수 있기 때문에 중요하지 않은 배경 작업을 수행할 때 매우 유용하다.

자바의 스레드 API는 유창한(fluent) API가 아니기 떄문에 먼저 스레드를 변수에 할당한뒤 데몬 스레드로 설정해 줘야 한다. 코틀린에서는 훨씬 간단하게 할 수 있다.

```kotlin
thread(isDaemon = true) {
        for (i in 1..1_000_000) {
            println("데몬 스레드: $i")
        }
    }
```

이 스레드는 100만까지 출력하도록 설정했지만 실제로는 얼마 출력 못하고 출력을 멈출 것이다. 데몬 스레드이기 때문이다. 부모 스레드가 멈추면 모든 데몬 스레드도 함께 종료된다.

## 스레드 안정성

오로지 스레드 안정성만 다루는 책도 있다. 그럴만도 하다. 스레드 안정성이 지켜지지 않아서 발생하는 동시성 버그는 잡아내기 가장 어려운 축에 속하기 때문이다. 게다가 재현도 쉽지 않다.

다음의 예제부터 실행하자. 이 코드에서는 counter 변수를 증가시키는 스레드를 10만개 만든다. 최종적으로 counter 변수의 값을 확인하기 전에 먼저 모든 스레드가 작업을 끝냈다는 것을 확인하기 위해 CountDownLatch를 사용할 것이다.

```kotlin
var counter = 0
    val latch = CountDownLatch(100_000)
    repeat(100) {
        thread {
            repeat(1000) {
                counter++
                latch.countDown()
            }
        }
    }
    latch.await()
    println("Counter $counter")
```

이 코드가 올바른 값을 출력하지 않는 것은 ++ 연산자가 원자적이지 않은 까닭에 경합 조건이 발생했기 때문이다. 더 많은 스레드가 counter 변수의 값을 증가시키려 할수록 경합이 발생할 확률은 올라간다.

자바와 달리 코틀린에는 synchronized 키워드가 없다. 코틀린을 설계한 사람들은 어떤 특정한 동시성 모델에만 맞춰서 언어를 만들면 안 된다고 생각했기 때문이다. 대신 synchronized() 함수를 사용할 수 있다.

```kotlin

            repeat(1000) {
                synchronized(latch) {
                    counter++
                    latch.countDown()
                }
            }
```

이제 이 코드는 예상대로 100,000을 출력한다.

자바의 synchrozied 키워드가 그립다면 코틀린에서는 @Synchronized 애노테이션을 사용할 수 있다. 자바의 volatile 키워드도 @Volatile 애노테이션으로 대체됐다.

| 자바 | 코틀린 |
| --- | --- |
| synchronized void doSomething() | @Synchronized fun doSomething |
| volatile int sharedCounter = 0; | @Volatile var sharedCounter: Int = 0 |

Synchromized와 Volatile이 키워드가 아닌 애노테이션인 이유는 코틀린이 JVM 이외의 다른 플랫폼으로도 컴파일될 수 있기 때문이다. 반면 synchromized 메서드와 volatile 변수는 JVM에만 존재한다.

## 왜 스레드는 값비싼가?

스레드는 스택을 만들기 위해 1MB의 램 공간이 필요하다. 따라서 스레드를 너무 많이 만들면 운영체제에 너무 자주 접근하게 될 뿐만 아니라 메모리도 많이 소요된다.

Excutors API를 사용하면 한 번에 실행 가능한 스레드의 개수를 제한할 수 있다. 이 API는 자바5에서 처음 도입되었다.

## 코루틴 소개

코틀린은 자바의 스레드 모델에 더해서 **코루틴** 모델을 제공한다. 코루틴은 가벼운 스레드라고 생각하면 된다.

먼저 알아야 할 점은 코루틴이 언어의 내장 기능이 아니라는 것이다. 단지 젯브레인스에서 제공하는 하나의 라이브러리라일 뿐이다. 따라서 코루틴을 사용하려면 그래들 설정파일(build.gradle.kts)에 이를 명시적으로 줘야 한다.

```kotlin
dependencies {
	// ...
	// coroutine
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```
<br/>

### 코틀린 시작하기

각 코루틴은 어떤 변수의 값을 증가시킨 뒤 IO 작업을 모사하기 위해 잠깐 잠들었다가 깨어나서 다시 변수의 값을증가시킨다.

```kotlin
fun main() {
    val latch = CountDownLatch(10_000)
    val c = AtomicInteger()
    val start = System.currentTimeMillis()
    for (i in 1..10_000) {
        GlobalScope.launch {
            c.incrementAndGet()
            delay(100)
            c.incrementAndGet()
            latch.countDown()
        }
    }
    latch.await(10, TimeUnit.SECONDS)

    println("${c.get() / 2}개의 코루틴을 ${System.currentTimeMillis() - start }밀리초 동안 수행}")
}
```

코루틴을 시작하는 첫 번째 방법은 launch() 함수를 사용하는 것이다.

이 코드에서 눈여겨봐야 하는 또 하나는 delay() 함수 호출이다. 데이터베이스나 네트워크 위치에서 자료를 가져오는 것과 같은 IO 작업을 모사하기 위해 delay() 함수를 사용했다.

Thread.sleep() 함수와 마찬가지로 delay()도 현재 코루틴을 잠들게 한다. 그러나 Thread.sleep()과 다른 점이 있다. 코루틴이 잠들어 있는 동안 다른 코루틴은 정상적으로 실행될 수 있다는 것이다. 이건 delay() 함수에 suspend 키워드가 붙어 있기 때문이다.

이 코드를 실행하면 200밀리초 남짓밖에 걸리지 않는 것을 볼 수 있다. 스레드를 사용하면 20초가 걸리거나 메모리 초과 오류가 발생했을 것이다. 코드도 별로 많이 고치지 않았다.

코루틴을 일시 중단하더라도 그 스레드는 멈추지 않는다. 스레드가 멈추지 않는다는 것은 엄청난 장점이다. OS 스레드를 덜 쓰고도 더 많은 일을 할 수 있기 때문이다.

이 코드를 IntelliJ IDEA 에서 실행하면 GlobalScope가 주의해야 하는 API(delicate API)로 표시되는 것을 볼 수 있다. GlobalScope의 내부 동작을 잘 이해하지 못한다면 실제 프로젝트에서는 사용하면 안 된다는 뜻이다. 함부로 사용하면 자원 누출이 발생할 수 있다.

방금 다룬 launch() 함수는 아무것도 반환하지 않는 코루틴을 시작한다. 반면에 async() 함수는 어떤 값을 반환하는 코루틴을 시작한다.

launch() 함수 호출은 반환형이 Unit인 함수를 호출하는 것과 같다. 그러나 대부분의 함수는 어떤 결과를 반환한다. 그래서 async() 함수가 존재한다. 코루틴을 띄우는 것은 똑같지만 작업 대신 Deferred<T>를 반환한다. 이때 T는 나중에 얻게 될 반환값의 타입을 나타낸다.

예를 들어 다음 함수는 UUID를 비동기적으로 생성해 반환하는 코루틴을 시작한다.

```kotlin
fun fastUuidAsync() = GlobalScope.async {
        UUID.randomUUID()
    }

    println(fastUuidAsync())
```

main 메서드에서 다음 함수를 실행하면 예상한 결과가 출력되지 않는다. 즉 UUID가 아니라 다음과 같은 문구가 출력된다.

```kotlin
DeferredCoroutine{Active}@3cd1f1c8
```

코루틴이 반환한 객체를 작업(job)이라고 부른다. 이것이 무엇이며 어떻게 사용해야 하는지 알아보자.

## 작업

비동기적으로 어떤 일을 시작했을 때 그 결과를 **작업**이라고 부른다. 마치 Thread 객체가 실제 OS 스레드를 표현하는 것과 같이 Job 객체는 실제 코루틴을 나타낸다.

```kotlin
val job: Job = fastUuidAsync()
println(job)
```

작업의 생애 주기는 단순하다. 다음의 네 가지 상태를 가질 수 있다.

- **신규**(new): 생성됐으나 시작되지 않음
- **활성**(active): launch() 함수 등에 의해 시작됨. 기본 상태
- **완료**(completed): 모든 것이 순조롭게 진행됨
- **취소**(cancled): 무언가 잘못됨

자식이 있는 작업은 다음과 같이 다 가지 상태를 추가로 갖는다.

- **완료 중**(completing): 완료하기 전에 자식이 실행되기를 기다리는 중
- **취소 중**canceling): 취소하기 전에 자식이 실행되기를 기다리는 중

앞의 예제에서 출력된 작업은 활성(Active) 상태에 있었다. 즉 아직 UUID 생성을 마치지 못한 상태다.

값을 갖는 작업을 Deferred라고 한다.

```kotlin
val job: Deferred<UUID> = fastUuidAsync()
```

작업이 완료되기를 기다려서 실제 반환값을 가져오려면 await() 함수를 사용한다.

```kotlin
val job: Deferred<UUID> = fastUuidAsync()
println(job.await())
```

그러나 이 코드는 다음과 같은 오류 메시지를 내며 컴파일되지 않는다.

```kotlin
> Suspend function 'await' should be called only from a coroutine or another suspend function
```

메시지에서 알 수 있듯이 main() 함수가 suspend 함수도 코루틴도 아니기 때문이다. 때문에 오류가 발생한다.

다음과 같이 `runBlocking` 함수로 코드를 감싸면 문제가 해결된다.

```kotlin
runBlocking {
        val job: Deferred<UUID> = fastUuidAsync()
        println(job.await())
    }
```

runBlocking 함수는 모든 코루틴이 끝날 때까지 메인 스레드를 중지시킨다.

`runBlocking` 함수는 모든 코루틴이 끝날 때까지 메인 스레드를 중지시킨다. 이 함수는 브리지 패턴을 구현하고 있는데, 일반 코드와 코루틴을 사용하는 코드 사이에 다리를 놓아준다.

## 코루틴의 내부 동작 이해하기

### 특징

- 코루틴은 가벼운 스레드와 같다. 스레드에 비해 더 적은 자원을 사용한다.
- 코루틴은 스레드를 통째로 멈추지 않고 자기 자신만 실행을 중단할 수 있다. 그동안 스레드는 다른 코드를 실행할 수 있다.

그런에 코루틴은 어떻게 동작하는 것일까?

예를 들어 설명하기 위해 사용자의 프로필을 만들어내는 다음의 함수를 살펴보자.

```kotlin
fun profileBlocking(id: String): Profile {
    // 1초 소요
    val bio = fetchBioOverHttpBlocking(id)
    // 100밀리초 소요
    val pic = fetchPictureFromDBBlocking(id)
    // 500밀리초 소요
    val friends = fetchFriendsFromDBBlocking(id)
    return Profile(bio, pic, friends)
}
```

이 함수가 완료되는 데에는 약 1.6초가 걸린다. 모든 작업이 순차적으로 이뤄지기 때문에 이 함수를 실행하는 스레드는 계속 멈춰 있을 것이다.

이 함수를 코루틴을 이용해서 다시 작성하면 다음과 같다.

```kotlin
suspend fun profileBlocking(id: String): Profile {
    // 1초 소요
    val bio = fetchBioOverHttpAsync(id)
    // 100밀리초 소요
    val pic = fetchPictureFromDBAsync(id)
    // 500밀리초 소요
    val friends = fetchFriendsFromDBAsync(id)
    return Profile(bio.await(), pic.await(), friends.await())
}
```

suspend 키워드를 붙이지 않으면 이 비동기 함수는 컴파일 되지 않는다. suspend 키워드가 무슨 의미인지는 다음 절에서 설명할 것이다.

각각의 비동기 함수가 어떻게 생겼는지 알아보기 위해 하나만 예를 들어보자.

```kotlin
fun fetchFriendFromDBAsync(id: String) = GlobalScope.async {
    delay(500)
    emptyList<String>()
}
```

이제 스레드 전체가 멈추도록 구현한 순차 실행 버전과 코루틴을 사용한 버전의 성능을 비교해 보자.

앞서 했던 것처럼 각 함수를 runBlocking 함수로 감싼 뒤 measureTimeMills를 사용해 소요 시간을 잴 것이다.

```kotlin
fun main() {
    runBlocking {
        val t1 = measureTimeMillis {
            Blocking.profile("123")
        }

        val t2 = measureTimeMillis {
            Async().profile("123")
        }
        println("Blocking code: $t1")
        println("Async: $t2")
    }
}
```

출력

```kotlin
Blocking code: 1635
Async: 1027
```

동시에 실행되는 여러 코루틴의 총 실행 시간은 가장 긴 코루틴의 실행 시간과 같은 것을 확인할 수 있다. 반면 순차적으로 실행한 경우엔 모든 함수의 실행 시간을 더한 것이 전체 실행 시간이 된다.

두 예제를 이해했으니 이제 같은 코드를 다른 방법으로 작성해보자.

각 함수에 suspend 키워드를 붙여서 일시 중단 함수(suspendable function)를 만들 것이다.

```kotlin
suspend fun fetchFriendsFromDB(id: String): List<String> {
    delay(500)
    return emptyList()
}
```

이 예제를 실행해 보면 순차 실행 버전과 비슷한 시간이 걸릴 것이다. 그렇다면 왜 일시 중단 함수를 사용할까?

일시 중단 함수는 스레드를 멈추지 않는다. 함수 하나의 성능은 달라지지 않지만, 더 큰 그림을 봤을 때 같은 수의 스레드로 더 많은 요청을 처리할 수 있게 된다. 코틀린이 일시 중단 함수로 표시된 코드를 똑똑하게 재작성해 주는 덕이다.

코틀린 컴파일러는 suspend 키워드를 발견하면 함수를 쪼개서 재작성한다. 재작성된 코드는 다음과 같다.

```kotlin
fun profile(state: Int, id: String, context: ArrayList<Any>): Profile {
            when (state) {
                0 -> {
                    context += fetchBioOverHttp(id)
                    profile(1, id, context)
                }
                1 -> {
                    context += fetchPictureFromDB(id)
                    return profile(2, id, context)
                }
                2 -> {
                    context += fetchFriendsFromDB(id)[0]
                    profile(3, id, context)
                }
                3 -> {
                    val (bio, picture, friends) = context
                    return Profile(bio as String, picture as ByteArray?, friends as List<String>)
                }
                else -> {
                    val (bio, picture, friends) = context
                    return Profile(bio as String, picture as ByteArray?, friends as List<String>)
                }
            }
        }
```

코틀린이 재작성한 코드는 주어진 함수를 여러 단계로 쪼개기 위해 4장에서 배웠던 **상태 패턴**을 사용한다. 이를 통해 상태 기계(state machine)의 각 단계를 실행할 때 코루틴을 실행하는 스레드 자원을 놓아줄 수 있다.

앞서 작성한 비동기 코드와는 달리 상태 기계 자체는 순차적이며 모든 단계를 수행하는데에 순차 코드의 동일한 시간이 걸린다는 점을 기억하라.

다만 이 예제에서 중요한 부분은 어떤 단계도 스레드를 멈추지 않는다는 것이다.

## 코루틴 취소하기

자바 개발자라면 스레드를 중지하는 일이 생각처럼 쉽제 않다는 것을 알 것이다.

예를 들어 Thread.stop()는 사용이 권고되지 않는(deprecated) 메서드다. `Thread.interrupt()`함수도 있지만 모든 스레드가 이 플래그를 검사하는 것은 아니다. volatile 플래그 사용하는 것도 권고되는 방법 중 하나지만 여간 성가신 일이 아니다.

스레드 풀을 사용한다면 Future 객체를 얻을 것이고, 여기에는 cancel(booleanmayInterruptIfRunning) 메서드가 있다. 코틀린에서는 launch() 함수가 작업을 반환한다.

이 작업은 취소할 수 있다. 하지만 이전 예제에서와 동일한 규칙이 적용된다. 코루틴이 다른 일시 중단 함수를 호출하거나 yield 함수를 호출하지 않는 이상 cancel()은 무시될 것이다.

## 타임아웃 설정하기

다음과 같은 상황을 상정해 보자. 만약 어떤 경우 사용자의 프로필을 받아오는 데에 너무 오랜 시간이 걸린다면? 만약 프로필을 가져오는 데에 0.5초 이상 걸리면 그냥 프로필을 표시하지 않기로 결정했다면?

그럴 때는 `withTimeout()` 함수를 사용할 수 있다.

```kotlin
fun main() = runBlocking {
    val coroutine = async {
        withTimeout(500) {
            try {
                val time = Random.nextLong(1000)
                println("수행하는 데에 $time 밀리초가 걸립니다.")
                delay(time)
                println("프로필 정보를 반환합니다.")
                "프로필"
            } catch (e: TimeoutCancellationException) {
                e.printStackTrace()
            }
        }
    }
}
```

이 코루틴은 완료되는 데에 0 ~ 1000밀리초가 걸리는데 500 밀리초의 타임아웃을 걸었다.

즉 50%의 확률로 작업이 완료되지 못할 것이다.

코루틴에 await을 호출하고 무슨 일이 일어나는지 살펴보자.

```kotlin
val result = try {
        coroutine.await()
    } catch (e: TimeoutCancellationException) {
        "프로필 없음"
    }
    println(result)
```

여기선 코틀린의 try가 식이라는 사실 덕분에 즉시 값을 반환할 수 있다.

이 코루틴이 타임아웃 이전에 값을 반환하면 result의 값은 ‘프로필’이 된다. 그렇지 못하면 TimeoutCancellationException을 받게 되고 result의 값은 ‘프로필 없음’이 된다.

타임아웃을 try-catch식과 함께 사용하면 외부 시스템과 안전하게 상호작용하기 위한 강력한 도구가 된다.

## 분배기

지금까지 `runBlocking` 함수를 사용해서 코루틴은 메인 스레드에서 실행됐다.

다음 코드를 통해 확인할 수 있다.

```kotlin
runBlocking { 
        launch {
            println(Thread.currentThread().name) // "main"이 출력된다.
        }
    }
```

반면 `GlobalScope`로 실행한 코루틴은 `DefaultDispatcher`라는 것 위에서 실행된다.

```kotlin
GlobalScope.launch {
            println("GlobalScope.launch: ${Thread.currentThread().name}")
        }
```

이 코드의 출력은 다음과 같다.

```kotlin
GlobalScope.launch: DefaultDispatcher-worker-1
```

`DefaultDispatcher`는 수명이 짧은 코루틴을 실행하기 위한 스레드 풀이다.

`launch()`나 `async()` 등의 코루틴 생성기에는 기본 인수가 여럿 있는데 그중 하나가 코루틴을 실행할 분배기를 지정한다. 다른 분배기를 사용하고 싶다면 코루틴을 만들 때 인수로 분배기를 전달하면 된다.

```kotlin
launch(Dispatchers.Default) {
            println("launch(Dispatchers.Default): ${Thread.currentThread().name}")
        }
```

```kotlin
runBlocking {
        launch {
            println(Thread.currentThread().name) // "main"이 출력된다.
        }
        GlobalScope.launch {
            println("GlobalScope.launch: ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) {
            println("launch(Dispatchers.Default): ${Thread.currentThread().name}")
        }

    }
```

해당 코드의 실행 결과

```kotlin
launch(Dispatchers.Default): DefaultDispatcher-worker-2
GlobalScope.launch: DefaultDispatcher-worker-1
main
```

지금까지 살펴봤던 `Main`이나 `Default` 분배기 말고 IO라는 분배기도 있다. 이 분배기는 오래 걸리는 작업을 처리하기 위해 사용한다. 다른 분배기와 마찬가지로 코루틴을 생성할 때 인수로 IO 분배기를 특정해 사용할 수 있다.

```kotlin
async(Dispatchers.IOJ) {
	// 오래 걸리는 작업
}
```

## 구조화된 동시성

어떤 코루틴 안에서 다른 코루틴을 띄우는 것은 굉장히 흔한 일이다.

**구조화된 동시성**(structured concurreny)의 첫 번째 규칙은 다음과 같다. 부모 코루틴은 반드시 모든 자식 코루틴이 완료될 때까지 기다려야 한다. 이 규칙을 지키지 않으면 자원 누출이 발생할 수 있다. 구조화된 동시성 개념이 없는 언어에는 자원 누출이 잘 일어나는 것도 이 때문이다.

```kotlin
fun main() = runBlocking {
    val parent = launch(Dispatchers.Default) {
        val children = List(10) {childId ->
            launch {
                for (i in 1..1_000_000) {
                    UUID.randomUUID()
                    if (i % 100_000 == 0) {
                        println("$childId - $i")
                        yield()
                    }
                }
            }
        }
    }

}
```

이제 자식 코루틴 중 하나가 시작하고 잠시 뒤에 예외를 던지도록 해보자.

```kotlin
...
if (i % 100_000 == 0) {
    println("$childId - $i")
    yield()
}
if (childId == 8 && i == 300_000) {
    throw RuntimeException("예외 발생")
}
...
```

이 코드를 실행하면 재미있는 일이 일어난다. 예외가 발생한 코루틴만 종료되는 것이 아니라 함께 실행된 다른 자식 코루틴이 모두 함께 종료된다.

여기서는 예외가 처리되지 않고 부모 코루틴까지 올라가서 부모 코루틴의 실행을 취소한다. 이때 부모 코루틴은 다른 모든 자식 코루틴을 종료한다. 자원 누출을 미연에 방지하기 위함이다.

보통은 이런 식의 동작을 기대할 것이다. 하지만 만약 자식 코루틴에서 발생한 예외 때문에 부모 코루틴이 종료되지 않도록 하려면 supervisorScope를 사용하면 된다.

```kotlin
val parent = launch(Dispatchers.Default) {
        supervisorScope {
            val children = List(10) { childId ->
						...
}
```

supervisorScope를 사용하면 코루틴 중 하나가 실패하더라도 부모는  영향을 받지 않는다.

하지만 여전히 cancel() 함수를 통해 코루틴을 취소하면 모든 자식 코루틴도 함께 취소된다.

이제 고조화된 동시성의 이점까지 살펴봤으니 초반에 언급한 내용을 다시 살펴보자. GlobalScope가 주의해야하는 API 라는 사실 말이다. GlobalScope는 launch()나 async()와 동일한 방법으로 사용할 수 있지만 구조화된 동시성의 원칙을 준수하지 않는다. 그래서 GlobalScope를 잘못된 방법으로 사용하면 자원 누출이 발생할 수 있다.

## 요약

코틀린은 자바에 비해 간단한 문법으로 스레드를 만들 수 있다. 그러나 스레드는 메모리 측면에서 오버헤드가 있고 때로는 성능에도 영향을 미친다. 코루틴을 사용하면 이런 문제를 해결할 수 있다. 따라서 코틀린에서 동시성이 필요할 때는 항상 코루틴을 사용하라.
