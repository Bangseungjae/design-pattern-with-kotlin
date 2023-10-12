package study.io.coroutine

import kotlinx.coroutines.*
import kotlin.random.Random

fun main() = runBlocking {
    val coroutine = async {
        withTimeout(500) {
            try {
                val time = Random.nextLong(1000)
                println("수행하는 데에 $time 밀리초가 걸립니다.")
                delay(time)
                println("프로필 정보를 반환합니다.")
                "프로필"
            } catch (e: TimeoutCancellationException) {
                e.printStackTrace()
            }
        }
    }
    val result = try {
        coroutine.await()
    } catch (e: TimeoutCancellationException) {
        "프로필 없음"
    }
    println(result)
}
