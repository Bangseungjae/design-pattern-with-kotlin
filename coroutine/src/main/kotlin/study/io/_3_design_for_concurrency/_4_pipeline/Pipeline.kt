package study.io._3_design_for_concurrency._4_pipeline

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.concurrent.TimeUnit

fun main() {
    runBlocking {
        val pagesProducer = producePages()
        val domProducer = produceDom(pagesProducer)
        val titleProducer = produceTitles(domProducer)
        titleProducer.consumeEach {
            println(it)
        }
        delay(2000)
        pagesProducer.cancel()
    }
}

fun CoroutineScope.producePages(): ReceiveChannel<String> = produce {
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

fun CoroutineScope.produceDom(pages: ReceiveChannel<String>): ReceiveChannel<Document> = produce {
    fun parseDom(page: String): Document {
        // 실제로는 DOM 라이브러리를 사용해서 문자열을 DOM으로 변환해야 한다.
        return Document(page)
    }

    for (p in pages) {
        send(parseDom(p))
    }
}

fun CoroutineScope.produceTitles(parsedPages: ReceiveChannel<Document>): ReceiveChannel<String> = produce {
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

data class Document(val html: String) {
    fun getElementsByTagName(tag: String): List<Document> {
        return listOf(Document("Some title"))
    }
}
