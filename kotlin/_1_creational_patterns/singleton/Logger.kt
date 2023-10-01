package _1_creational_patterns.singleton

object Logger {
    val name = "Joy"
    var age = 10
    init {
        println("${name}가 싱글톤 객체에 처음 접근했습니다. age = ${age++}")
    }
}
