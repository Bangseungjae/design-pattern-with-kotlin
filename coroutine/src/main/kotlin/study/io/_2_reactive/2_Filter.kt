package study.io.reactive

fun main() {
    val numbers: IntRange = 1..100
    val notFizzbuzz = mutableListOf<Int>()
    for (n in numbers) {
        if (n % 3 == 0 || n % 5 == 0) {
            notFizzbuzz.add(n)
        }
    }
    val filtered: List<Int> = (1..100).filter { it % 3 == 0 || it % 5 == 0 }
}
