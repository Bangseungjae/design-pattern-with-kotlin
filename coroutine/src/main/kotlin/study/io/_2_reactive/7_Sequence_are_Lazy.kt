package study.io.reactive

import kotlin.system.measureTimeMillis

fun main() {
    val numbers = (1..1_000_000).toList()
    println(measureTimeMillis {
        numbers.map {
            it * it
        }.take(1).forEach { it }
    }) // ~50ms

    println(measureTimeMillis {
        numbers.asSequence().map {
            it * it
        }.take(1).forEach { it }
    })// ~5ms

    println(measureTimeMillis {
        numbers.map {
            it * it
        }.forEach { it }
    }) // ~50ms

    println(measureTimeMillis {
        numbers.asSequence().map {
            it * it
        }.forEach { it }
    })// ~20ms
}
