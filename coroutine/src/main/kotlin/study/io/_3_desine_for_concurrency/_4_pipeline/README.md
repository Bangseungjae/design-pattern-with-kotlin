# 파이프라인 패턴(Pipeline Pattern)

파이프라인(pipeline) 디자인 패턴은 다양한 종류의 작업을 큰 규모로 처리할 때 도움이 된다. 여러 CPU에 서로 다른 복잡도를 갖는 작업을 분배하는 상황에서 파이프라인 패턴은 작업을 더 작은 동시성 작업으로 쪼갠다. 다음 예제를 보며 더 자세히 알아보자.

HTML 페이지는 이미 해석된 상태로 제공된다고 가정했다. 웹 페이지 스트림을 처리하는 로직을 설계해 보겠다.

먼저 일정한 주기에 따라 뉴스 페이지를 가져올 것이다. 여기에는 생산자를 사용한다.

```kotlin
fun CoroutineScope.producePages() = produce {
    fun getPages(): List<String> {
        // 실제 뉴스 페이지 내용을 가져오는 코드라고 치자.
        return listOf(
            "<html><body><h1>좋은 내용</h1></body></html>",
            "<html><body><h1>더 많은 내용</h1></body></html>",
        )
    }
    val pages = getPages()

    while (this.isActive) {
        for (p in pages) {
            send(p)
        }
        delay(TimeUnit.SECONDS.toMillis(5))
    }
}
```

isActive 플래그는 코루틴이 계속 실행되는 동안은 true값을 갖다가 코루틴 실행이 취소되면 false가 된다. 오랫동안 실행되는 반복문이 있다면 그 안에서 isActive가 true인지 반드시 확인하는 것이 좋다. 그래야 필요할 때 반복문을 멈출 수 있다.

새로운 글이 올라오면 다음 단계로 글 내용을 보낸다. 기술 뉴스가 수시로 올라오는 것은 아니기에 delay()를 이용해서 일정한 간격을 두고 새 글이 있는지 검사한다. 실제 코드라면 아마도 몇 분에서 몇 시간에 한 번씩 검사하면 충분할 것이다.

다음 단계는 HTML 문자열로 DOM(document Object Model)을 생성하는 일이다. 이번에도 생산자를 이용할 것이다. 이 생산자는 채널을 하나 입력받는데, 먼저 구현한 첫 번째 생산자에게 자료를 받기 위함이다.

```kotlin
fun CoroutineScope.produceDom(pages: ReceiveChannel<String>) = produce {
    fun parseDom(page: String): Document {
        // 실제로는 DOM 라이브러리를 사용해서 문자열을 DOM으로 변환해야 한다.
        return Document(page)
    }

    for (p in pages) {
        send(parseDom(p))
    }
}
```

for 반복문을 사용해서 채널이 열려 있는 동안 채널의 값을 얻어올 수 있다. 따로 콜백 함수를 정의하지 않고도 비동기적 자료 제공자로부터 자료를 가져오는 매우 세련된 방법이다.

세 번째로 구현할 함수는 DOM을 받아서 각 문서의 제목을 추출하는 역할을 한다.

```kotlin
fun CoroutineScope.produceTitles(parsedPages: ReceiveChannel<Document>) = produce {
    fun getTitles(dom: Document): List<String> {
        return dom.getElementsByTagName("h1").map { 
            it.toString()
        }
    }

    for (page in parsedPages) {
        for (t in getTitles(page)) {
            send(t)
        }
    }
}
```

제목을 추출하기 위해서 getElementsByTagName(”h1”)를 사용했다. h1태그가 발견되면 그 내용을 문자열로 변환한다.

이제 지금까지 만든 코루틴을 하나의 파이프라인으로 연결할 차례다.

### 파이프라인 연결하기

파이프라인의 구성 요소는 모두 알아봤으니 이제 하나로 연결하는 방법을 알아보자.

```kotlin
runBlocking {
    val pageProducer = producePages()
    val domProducer = produceDom(pageProducer)
    val titleProducer = produceTitles(domProducer)
    titleProducer.consumeEach {
        println(it)
    }
}
```

이렇게 만들어진 파이프라인은 다음과 같이 생겼다.

```text
Input=>pagesProducer=>domProducer=>titleProducer=>Output
```

파이프라인은 긴 처리 과정을 세부 단계로 쪼개는 멋진 기법이다. 파이프라인을 구성하는 코루틴이 모두 순수 함수라는 점에 주목하자. 쉽게 이해하고 테스트할 수 있다는 뜻이다.

연결된 파이프라인의 첫 번째 코루틴에 `cancel()`을 호출하면 전체 파이프라인을 멈출 수 있다.
