# 비편향 select

## 비편향 select

select 구문을 사용할 때는 순서가 중요하다. 기본적으로 select는 편향적이기 때문이다. 만약 두 이벤트가 동시에 발생하면 select는 첫 번째 구문을 선택한다.

다음 예제를 통해 조금 더 자세히 알아보자.

이번에는 생산자를 하나만 만들 것이다. 이 생산자는 다음에 시청해야 할 영화 제목을 채널로 보낸다.

```kotlin
fun CoroutineScope.fastProducer(
    movieName: String
) = produce(capacity = 1) {
    send(movieName)
}
```

채널에 0이 아닌 capacity를 설정했기 때문에 코루틴이 실행되면 곧바로 값을 얻을 수 있을 것이다.

이제 이 생산자를 2개 시작하고, select 식을 사용해서 어떤 영화가 선택되는지 보자.

```kotlin
fun main() {
    runBlocking {
        val firstOption = fastProducer("분노의 질주 7")
        val secondOption = fastProducer("리벤저")

        delay(10)
        val movieToWatch = select<String> {
            firstOption.onReceive { it }
            secondOption.onReceive{ it }
        }
        println(movieToWatch)
    }
}
```

이 코드는 아무리 실행해도 <분노의 질주 7>이 항상 이긴다. select 구문은 두 값이 동시에 준비되는 경우 항상 둘 중 첫 번째 채널을 선택하기 때문이다.

이번에는 select 대신 selectUnbiased 구문을 사용해 보자.

```kotlin
...
val movieToWatch = selectUnbiased<String> {
    firstOption.onReceive { it }
    secondOption.onReceive{ it }
}
...
```

이 코드를 실행하면 <분노의 질주 7>이 출력될 때도 있고 <리벤저>가 출력될 때도 있을 것이다. 그냥 select 구문과 달리 selectUnbiased는 순서를 무시한다. 여러 결과가 동시에 준비되면 임의로 하나를 선택한다.
