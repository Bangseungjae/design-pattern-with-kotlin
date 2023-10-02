# 빌더 패턴(Builder Pattern)

이 패턴을 사용하면 복잡한 객체를 보다 쉽게 만들 수 있다.

이메일의 속성

- 숫신자 주소(최소 1개)
- 참조(선택 사항)
- 제목(선택 사항)
- 본문(선택 사항)
- 중요 표시(선택 사항)

```kotlin
data class Mail_V1(
    val to: List<String>,
    val cc: List<String>?,
    val title: String?,
    val message: String?,
    val important: Boolean,
)
```

관리자에게 보내는 이메일을 하나 작성해 보자

```kotlin
fun main() {
    val mail = Mail_V1(
        listOf("manager@company.com"),  // To
        null,   // CC
        "Ping ",    // Title
        null,   // Message
        true    // Important
    )
}
```

클래스를 수정해야 한다면 어떻게 될까?

먼저 컴파일이 되지 않을 것이다. 또한 주석도 함께 수정해야 한다. 즉 생성자에 인수가 너무 많으면 코드가 금세 지저분해진다.

빌더 디자인 패턴은 이런 문제를 해결하기 위해 탄생했다. 빌더 패턴을 사용하면 객체 생성과 인수 할당을 분리함으로써 복잡한 객체를 차근차근 만들 수 있다.

먼저 Mail 클래스를 감싸고 있는 MailBuilder 클래스를 만들어 보자.

```kotlin
class MailBuilder {
    private var to: List<String> = listOf()
    private var cc: List<String> = listOf()
    private var title: String = ""
    private var message: String = ""
    private var important: Boolean = false
}
```

```kotlin
class Mail internal constructor(
    val to: List<String>,
    val cc: List<String>?,
    val title: String?,
    val message: String?,
    val important: Boolean,
)
```

빌더는 생성하고자 하는 클래스와 정확히 동일한 속성을 갖는다. 하지만 가변 속성이라는 점이 다르다.

생성자에 internal 접근 제한자가 붙어 있는 것에 주목하라. 이는 모듈 내의 모든 코드에서 Mail 클래스에 접근할 수 있다는 뜻이다.

클래스 생성을 완료하기 위해 `build()` 함수를 구현해 보자.

```kotlin
fun build(): Mail {
        if (to.isEmpty()) {
            throw RuntimeException("To 속성이 비어 있습니다.")
        }
        return Mail(to, cc, title, message, important)
    }
```

각 속성을 설정할 수 있는 함수도 구현할 것이다.

```kotlin
fun message(message: String): MailBuilder {
        this.message = message
        return this
    }
    // 각 속성에 대해서 모두 구견해야 함
```

이제 다음과 같이 빌더를 사용해서 이메일을 생성할 수 있다.

```kotlin
val mail = MailBuilder().to(listOf("hello@hello.com")).title("안녕?").build()
```

새로운 값을 설정한 후 현재 MailBuilder 객체를 가리키는 참조를 반환함으로써 연속해서 다른 속성을 설정하는 함수를 호출하는 메서드 사슬을 만들 수 있다.

이 빌더 패턴은 잘 동작하지만 두 가지 문제가 있다.

- 만들고자 하는 클래스의 속성을 빌더에도 똑같이 나열해야 한다.
- 속성마다 값을 설정하기 위한 함수를 선언해야 한다.

코틀린에는 더 실용적인 방법이 두 가지 더 있다.

## 유창한 설정자

여기선 추가적인 클래스를 만들지 않고 데이터 클래스의 생성자에게 필수적인 필드만 입력으로 받을 것이다. 필수적이지 않은 나머지 필드는 비공개로 선언하고 각각에 대해 설정자를 구현할 것이다.
```kotlin
data class Mail_v2(
    val to: List<String>
    private var _message: String? = null,
    private val _cc: List<Stirng>? = null,
    private var _title: String? = null,
    private var _important: Boolean? = null
) {
    fun message(message: String) = apply {
        _message = message
    }
    // 다른 모든 필드에 대해서 같은 방법으로 구현
    // ...
}
```

아예 설정자를 구현하지 않는 방법도 있다. 위에서 사용된 apply() 함수를 객체 자체에 사용하는 것이다. apply() 함수는 코틀린의 모든 객체에 존재하는 확장 함수다. 다만 이 방법은 선택적 필드가 아닌 변수로 선언돼 있을 때만 가능하다.

이 방법을 사용하면 이메일 객체를 다음과 같이 생성할 수 있다.
```kotlin
val mail = Mail_V2("hello@mail.com").apply {
    message = "어떤 메시지"
    title = "Apply"
}
```
괜찮은 방법이다. 작성해야 하는 코드도 많이 줄었다. 하지만 이 방법에도 몇 가지 단점이 있다.

- 선택적 인수를 모두 가변 필드로 선언해야 한다. 그러나 가능하다면 가변 필드보다는 스레드 안전하며 값을 추적하기 용이한 불변 필드를 사용하는 것이 낫다.
- 모든 선택적 인수가 null값을 가질 수 있다. 코틀린의 null안정성 탓에 변수에 접근할 때마다 값이 null 인지를 확인해야 한다.
- 문법이 너무 장황하다. 각 필드에 대해 똑같은 패턴을 계속 반복해야 한다.

이제 이 문제를 해결하는 마지막 방법을 살펴보자

## 기본 인수

코틀린은 생성자와 함수의 매개변수에 기본값을 설정할 수 있다.

```kotlin
data class Mail_V3(
    val to: List<String>,
    val cc: List<String> = listOf(),
    val title: String = "",
    val message: String = "",
    val important: Boolean = false,
)
```

타입 뒤에 = 연산자를 사용해 기본 인수를 설정한다. 이렇게 하면 생성자가 모든 인수를 받을 수는 있지만 모든 인수를 항상 필수적으로 전달할 필요는 없게 된다.

따라서 본문이 없는 이메일을 생성하고 싶다면 다음과 같이 할 수 있다.

```kotlin
val mail = Mail_V3(listOf("manager@company.com"), listOf(), "안녕")
```

하지만 참조 목록에 아무도 없다는 것을 나타내기 위해 빈 리스트를 전달해야 하는 것에 주목하라. 다소 불편한 부분이다.

만약 중요 플래그만 설정해서 이메일을 보내고 싶다면 어떻게 할까?

코틀린은 명명 인수라는 기능을 제공한다.

```kotlin
val mail = Mail_V3(
        title = "안녕",
        message = "잘 지내니?",
        to = listOf("my@dear.cat")
    )
```

명명 인수를 사용하면 코틀린에서 복잡한 객체를 사용하는 것이 한결 쉬워진다. 따라서 빌더 디자인 패턴을 사용할 일은 거의 없다.
