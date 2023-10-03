package _1_creational_patterns.factorymethod

class Server private constructor(port: Long) {
    init {
        println("$port 포트에서 서버가 시작됐습니다.")
    }

    companion object {
        fun withPort(port: Long) = Server(port)
    }
}


fun main() {
//    val server1 = Server(8080) // 컴파일 실패
    val server2 = Server.withPort(8080) // 성공!
}
