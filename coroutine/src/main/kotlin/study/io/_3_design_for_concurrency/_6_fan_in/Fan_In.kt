package study.io._3_design_for_concurrency._6_fan_in

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import study.io._3_design_for_concurrency._5_fan_out.generateWork

fun main() {
    runBlocking {
        val workChannel = generateWork()
        val resultChannel = Channel<String>()
        val works = List(10) {
            doWorkAsync(workChannel, resultChannel)
        }
        resultChannel.consumeEach {
            println(it)
        }
    }
}

private fun CoroutineScope.doWorkAsync(
    channel: ReceiveChannel<String>,
    resultChannel: Channel<String>,
) = async(Dispatchers.Default) {
    for (p in channel) {
        resultChannel.send(p.repeat(2))
    }
}
