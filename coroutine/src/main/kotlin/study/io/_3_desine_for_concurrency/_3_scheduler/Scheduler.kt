package study.io._3_desine_for_concurrency._3_scheduler

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

fun main() {
    runBlocking {
        // 이렇게 하면 부모 코루틴의 분배기를 사용한다.
        launch {
            // main을 출력
            println(Thread.currentThread().name)
        }
        launch(Dispatchers.Default) {
            // DefaultDispatcher-worker-1을 출력
            println(Thread. currentThread().name)
        }

        async(Dispatchers.IO) {
            for (i in 1..1000) {
                println(Thread.currentThread().name)
                yield()
            }
        }

        val myDispatcher = Executors
            .newFixedThreadPool(4)
            .asCoroutineDispatcher()

        val forkJoinPool = ForkJoinPool(4).asCoroutineDispatcher()

        repeat(1000) {
            launch(forkJoinPool) {
                println(Thread.currentThread().name)
            }
        }
        myDispatcher.close()
        forkJoinPool.close()
    }
}
