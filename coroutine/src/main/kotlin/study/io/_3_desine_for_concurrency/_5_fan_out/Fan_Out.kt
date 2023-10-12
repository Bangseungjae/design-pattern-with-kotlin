package study.io._3_desine_for_concurrency._5_fan_out

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val workChannel = generateWork()

        val workers = List(10) { id ->
            doWork(id, workChannel)
        }
    }
}

fun CoroutineScope.generateWork(): ReceiveChannel<String> = produce {
    for (i in 1..10_000) {
        send("${i}쪽")
    }
    close()
}

fun CoroutineScope.doWork(
    id: Int,
    channel: ReceiveChannel<String>,
) = launch(Dispatchers.Default) {
    for (p in channel) {
        println("${id}번 일꾼이 ${p}을 처리")
    }
}
