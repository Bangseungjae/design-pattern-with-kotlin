package study.io.reactive

fun main() {
    val seq: Sequence<Long> = generateSequence(1L) { it + 1 }

    (1..100).asSequence()

    val fibSeq: Sequence<Int> = sequence {
        var a = 0
        var b = 1
        yield(a)
        yield(b)
        while (true) {
            yield(a + b)
            val t = a
            a = b
            b += t
        }
    }
    println(fibSeq.take(10).toList())
}
