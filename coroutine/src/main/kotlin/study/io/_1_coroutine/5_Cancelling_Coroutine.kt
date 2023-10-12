package study.io.coroutine

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException

fun main() = runBlocking {
    val cancellable: Job = launch {
        try {
            for (i in 1..10_000) {
                println("취소 가능: $i")
                yield()
            }
        } catch (e: CancellationException) {
            e.printStackTrace()
        }
    }

    val notCancellable: Job = launch {
        for (i in 1..10_000) {
            if (i % 100 == 0) {
                println("취소 불가능 $i")
            }
        }
    }

    println("취소 가능 코루틴을 취소 중")
    cancellable.cancel()
    println("취소 불가능 코루틴을 취소 중")
    notCancellable.cancel()
    cancellable.join()
    notCancellable.join()
}
