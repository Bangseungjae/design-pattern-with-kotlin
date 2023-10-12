package study.io._3_desine_for_concurrency._1_deferred_value

import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.random.Random

fun main() {
    runBlocking {
        val value = valueAsync()
        println(value.await())
    }
}


suspend fun valueAsync(): Deferred<String> = coroutineScope {
    val deferred = CompletableDeferred<String>()
    launch {
        delay(100)
        if (Random.nextBoolean()) {
            deferred.complete("OK")
        } else {
            deferred.completeExceptionally(
                RuntimeException()
            )
        }
    }
    deferred
}
