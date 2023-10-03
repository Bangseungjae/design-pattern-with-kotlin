# 책임 사슬 패턴(Chain Of Responsibility Pattern)

지독한 소프트웨어  아키텍트가 있다. 그는 특히 사람들과 말 섞기를 싫어한다. 그래서 아이보리 타워(자주 가는 카페 이름)에 앉아서 작은 웹 어플리케이션을 하나 만들었다. 어떤 개발자가 질문이 생기면 직접 그에게 물어볼 수 없다. 대신 이 웹 애플리케이션을 통해서 적절한 요청을 보내야 한다. 아키텍트는 답할 가치가 있는 질문에만 답변할 것이다.

**필터 사슬**(filter chain)은 웹 서버에서 보편적으로 사용되는 개념이다. 어떤 요청이 들어오면 일반적으로 다음의 세 가지를 만족하기를 기대한다.

- 매개변수가 이미 검증됐다.
- 사용자가 이미 인증됐다(가능한 경우).
- 사용자의 역할과 권한 정보가 주어졌고 수행하기 위한 인가(authorization)가 이뤄졌다.

이에 따라 처음 작성한 코드는 다음과 같다.

```kotlin
data class Request(val email: String, val question: String) {
    fun isKnownEmail(): Boolean {
        return true
    }

    fun isFromJuniorDeveloper(): Boolean {
        return false
    }
}

fun handleRequest(r: Request) {
    // 유효성 검사
    if (r.email.isEmpty() || r.question.isEmpty()) {
        return
    }

    // 인증
    // 이 사용자가 누구인지 알아낸다.
    if (r.isKnownEmail()) {
        return
    }

    // 인가
    // 주니어 개발자의 요청은 자동으로 무시된다.
    if (r.isFromJuniorDeveloper()) {
        return
    }

    println("모르겠네요. StackOverflow는 검색해 보셨나요?")
}
```

다소 지저분하긴 해도 잘 동작한다.

그런데 어떤 개발자가 2개의 질문을 동시에 보내는 것을 발견했다. 이를 막으려면 함수에 로직을 추가해야 한다. 이를 막으려면 함수에 로직을 추가해야 한다. 그런데 명색이 소프트웨어 아키텍트이지 않은가! 동작을 다른 곳으로 위임하는 더 좋은 방법이 없을까?

**책임 사슬**(chain of responsibility) 디자인 패턴은 복잡한 로직을 여러 개의 작은 단계(사슬 고리)로 쪼갠다. 그리고 각 단계가 그 결과에 따라 계속해서 다음 단계로 진행할지, 또는 처리를 끝내고 결과를 반환할지 결정한다.

<br/>


다음과 같은 인터페이스를 구현해보면서 시작해 보자.

```kotlin
interface Handler {
    fun handle(request: Request): Response
}
```

개발자의 질문에 대한 괴짜 아키텍트의 답변이 어떤 모습일지 이야기한 적은 없다. 이 책임 사슬은 너무 길고 복잡해서 답변까지 갈 일이 잘 없기 때문이다. 하지만 응답은 다음과 같이 생겼다고 치자.

```kotlin
data class Response(val answer: String)
```

자바 스타일로 구현하면 로직의 각 부분을 다음과 같이 handler 안에 넣을 것이다.

```kotlin
class BasicValidationHandler(private val next: Handler) : Handler {
	override fun handle(request: Request): Response {
		if (request.email.isEmpty() || request.question.isEmpty()) {
			throw IllegalArgumentException()
		}
		return next.handle(request)
	}
}
```

메서드가 하나뿐인 인터페이스를 구현하는 것을 볼 수 있다. 이 메서드가 바로 객체가 수행해야 하는 동작이다.

다른 필터들도 크게 다르지 않다. 필터를 만들고 나면 원하는 순서대로 조합하면 된다.

```kotlin
fun main() {
    val req = Request("developer@company.com", "왜 갑자기 빌드가 안 돼죠?")

    val chain = BasicValidationHandler(
        KnownEmailHandler(
            JuniorDeveloperFilterHandler(
                AnswerHandler()
            )
        )
    )
}
```

이보다 더 좋은 방법이 당연히 있다. 여기는 코틀린 세계다.

여기서는 책임 사슬을 나타내기 위한 함수를 하나 정의해 보자.

```kotlin
typealias Handler = (request: Request) -> Response
```

어떤 요청을 받아서 응답을 반환하는 간단한 기능을 구현하기 위해 별도의 클래스나 인터페이스를 만들지 않아도 된다. 값으로서의 함수를 사용해서 인증을 구현하면 다음과 같다.

```kotlin
val authentication = fun(next: Handler) =
    fun(request: Request): Response {
        if (!request.isKnownEmail()) {
            throw IllegalArgumentException()
        }
        return next(request)
    }

val basicValidation = fun(next: Handler) =
    fun(request: Request): Response {
        if (request.email.isEmpty() || request.question.isEmpty()) {
            throw IllegalArgumentException()
        }
        return next(request)
    }

val finalResponse = fun() =
    fun(request: Request): Response {
        return Response("I don't know")
    }
```

여기서 authentication은 함수를 받아서 새로운 함수를 반환한다. 이 패턴을 사용하면 쉽게 함수들을 조합할 수 있다.

```kotlin
fun main() {
    val req = Request("developer@company.com", "왜 갑자기 빌드가 안 돼죠?")
    val chain = basicValidation(authentication(finalResponse()))
    val res = chain(req)
    println(res)
}
```

어떤 방법을 사용할지 선택하는 것은 개발자의 몫이다. 가령 라이브러리나 프레임워크처럼 다른 개발자가 기능을 확장할 가능성이 높다면 함수보다는 인터페이스를 사용해서 명시적으로 코드를 작성하는 쪽이 더 적합할 것이다.

함수를 사용하면 코드를 더 간결하게 할 수 있기 때문에 단지 코드를 작은 단위로 쪼개는 것이 목적이라면 함수를 사용하는 것이 더 좋을 수 있다.

이 패턴은 실무에서도 많이 등장한다. 예를 들어 많은 웹 서버 프레임워크는 인증이나 인가, 로깅, 심지어 라우팅과 같은 횡당 관심사(cross-cutting concerns)를 다루기 위해 책임 사슬 패턴을 사용한다. 간혹 필터나 미들웨어라는 용어를 쓰기도 하는데, 결국 책임 사슬 패턴을 의미하는 것은 똑같다.
