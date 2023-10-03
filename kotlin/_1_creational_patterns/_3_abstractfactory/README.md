# 추상 팩토리 패턴

추상 팩토리란 팩토리를 만들어 내는 팩토리이다. 이것이 전부다.

팩토리는 다른 클래스를 만들어 낼 수 있는 함수나 클래스다. 즉 추상 팩토리란 여러 팩토리 메서드를 감싸는 클래스다.

추상 팩토리가 무엇인지 이해했다고 하더라도 이것을 어디에 써야 하는지 궁금할 것이다. 현업에서 추상 팩토리 디자인 패턴은 프레임워크나 라이브러리가 파일에서 구성 설정을 읽어 들일 때 자주 사용한다. **스프링 프레임워크**가 좋은 예다.

```yaml
server:
  port: 8080
enviroment: production
```

지금부터 할 일은 이 설정 파일을 읽어서 객체를 생성하는 것이다.

```kotlin
interface Property {
    val name: String
    val value: Any
}
```

이제 데이터 클래스 대신 인터페이스를 반환할 것이다. 왜 이렇게 해야 하는지는 나중에 알게 될 것이다.

```kotlin
interface ServerConfiguration {
    val properties: List<Property>
}
```

```kotlin
data class IntProperty(
    override val name: String,
    override val value: Any
) : Property

data class StringProperty(
    override val name: String,
    override val value: Any
) : Property

data class ServerConfigurationImpl(
    override val properties: List<Property>
) : ServerConfiguration
```

첫 번째 팩토리 메서드를 작성해 보자. 이 메서드는 문자열로 된 Property 객체를 생성한다.

```kotlin
class Parser {

    companion object {
        fun property(prop: String): Property {
            val (name, value) = prop.split(":")

            return when (name) {
                "port" -> IntProperty(name, value.trim().toInt())
                "enviroment" -> StringProperty(name, value.trim())
                else -> throw RuntimeException("알 수 없는 속성: $name")
            }

        }
    }
}
```
