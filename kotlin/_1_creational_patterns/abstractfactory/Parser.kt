package _1_creational_patterns.abstractfactory

import java.lang.RuntimeException

class Parser {

    companion object {

        fun server(propertyStrings: List<String>): ServerConfiguration {
            val parsedProperties = mutableListOf<Property>()
            for (p in propertyStrings) {
                parsedProperties += property(p)
            }
            return ServerConfigurationImpl(parsedProperties)
        }

        fun property(prop: String): Property {
            val (name, value) = prop.split(":")

            return when (name) {
                "port" -> IntProperty(name, value.trim().toInt())
                "environment" -> StringProperty(name, value.trim())
                else -> throw RuntimeException("알 수 없는 속성: $name")
            }

        }
    }
}

fun main() {
    val environment = Parser.server(listOf("port: 8080", "environment: production"))
    println(environment)
}
