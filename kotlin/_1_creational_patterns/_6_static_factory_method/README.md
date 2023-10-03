# 정적 팩토리 메소드 패턴(Static Factory Method Pattern)

## 정적 팩토리 메서드(Static Factory Method)란?

여기서 팩토리라는 용어가 조금 생소할 수 있다. GoF 디자인 패턴 중 팩토리 패턴에서 유래한 이 단어는 객체를 생성하는 역할을 분리하겠다는 취지가 담겨있다. (팩토리 패턴, 팩토리 클래스에 대해 더 알고 싶다면 이 글을 참고하길 바란다.)

다시 말해, 정적 팩토리 메서드란 객체 생성의 역할을 하는 클래스 메서드라는 의미로 요약해볼 수 있다.

```kotlin
class Server private constructor(port: Long) {
```

생성자를 private으로 만든다.

```kotlin
    companion object {
        fun withPort(port: Long): Server {
            return Server(port)
        }
    }
```
별도의 생성자를 만들어준다.

자바에서의 사용 예시는 LocalTime의 of와 같다.
생성에 필요한 별도 로직을 생성자 메서드에 추가해 줄 수도 있다.
