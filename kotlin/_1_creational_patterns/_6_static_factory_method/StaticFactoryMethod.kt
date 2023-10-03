<<<<<<< HEAD
package _1_creational_patterns._6_static_factory_method
=======
package _1_creational_patterns.static_factory_method
>>>>>>> d60cac6aea89fab96d8930d18e0b372dcb2e587a

fun main() {
    Server.withPort(8080)
}

class Server private constructor(port: Long) {

    init {
        println("Server started on port $port")
    }

    companion object {
        fun withPort(port: Long): Server {
            return Server(port)
        }
    }
}
