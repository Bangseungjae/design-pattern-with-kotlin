package study.io.reactive

import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    nonBuffered()
    buffered()
}

fun nonBuffered() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        val actor = actor<Long> {
            var prev = 0L
            channel.consumeEach {
                println("nonBuffered: ${it - prev}")
                prev = it
                delay(100)
            }
        }
        repeat(10) {
            actor.send(System.currentTimeMillis())
        }
        actor.close().also { println("none buffered 전송 완료") }
    }
    println("none buffered duration: ${System.currentTimeMillis() - startTime}")
}

fun buffered() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        val actor = actor<Long>(capacity = 10) {
            var prev = 0L
            channel.consumeEach {
                println("buffered: ${it - prev}")
                prev = it
                delay(100)
            }
        }
        repeat(10) {
            actor.send(System.currentTimeMillis())
        }
        actor.close().also { println("buffered 전송 완료") }
    }
    println("buffered duration: ${System.currentTimeMillis() - startTime}")
}
