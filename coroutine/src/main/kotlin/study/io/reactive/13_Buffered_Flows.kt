package study.io.reactive

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val numberFlow: Flow<Int> = flow {
            println("새로운 구독자!")
            (1..10).forEach {
                println("Sending $it")
                emit(it)
            }
        }

        (1..4)  .forEach { coroutineId ->
            delay(5000)
            numberFlow.buffer().collect {number ->
                delay(1000)
                println("${coroutineId}번 코루틴에서 $number 수신")
            }

        }
    }
}
