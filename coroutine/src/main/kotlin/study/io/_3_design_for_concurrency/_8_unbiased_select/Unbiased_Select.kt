package study.io._3_design_for_concurrency._8_unbiased_select

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.selectUnbiased

fun main() {
    runBlocking {
        val firstOption = fastProducer("분노의 질주 7")
        val secondOption = fastProducer("리벤저")

        delay(10)
        val movieToWatch = selectUnbiased<String> {
            firstOption.onReceive { it }
            secondOption.onReceive{ it }
        }
        println(movieToWatch)
    }
}

fun CoroutineScope.fastProducer(
    movieName: String
) = produce(capacity = 1) {
    send(movieName)
}
