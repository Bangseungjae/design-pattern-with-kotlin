package study.io._3_design_for_concurrency._7_racing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlin.random.Random

fun main() {
    runBlocking {
        val winner = select<Pair<String, String>> {
            preciseWeather().onReceive { preciseWeatherResult ->
                preciseWeatherResult
            }
            weatherToday().onReceive { weatherTodayResult ->
                weatherTodayResult
            }
        }
        println(winner)
    }
}

fun CoroutineScope.preciseWeather() = produce {
    delay(Random.nextLong(100))
    send("Precise Weather" to "+25c")
}

fun CoroutineScope.weatherToday() = produce {
    delay(Random.nextLong(100))
    send("Weather Today" to "+24c")
}
