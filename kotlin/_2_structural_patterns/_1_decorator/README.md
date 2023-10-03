# 데코레이터 패턴(Decorator Pattern)

프로토타입 패턴은 같은 클래스의 인스턴스면서 속성이 조금 다른 객체를 쉽게 생성할 수 있다.

속성이 아닌 동작이 조금씩 다른 클래스를 여럿 만들어야 한다면 어떻게 할까?

코틀린에서는 함수가 일급 객체이기 때문에 프로토타입 디자인 패턴을 사용해서도 이 목적을 달성할 수 있다.

하지만 조금 다른 접근법을 설명하고자 한다.

```kotlin
데코레이터 디자인 패턴을 구현하면 코드 사용자가 어떤 기능을 추가할지 자유롭게 선택할 수 있다.
```

## 클래스에 기능 추가하기

```kotlin
open class StarTrekRepository {
    private val starshipCaptains = mutableMapOf("USS 엔터프라이즈 " to "장뢱 피카드")

    open fun getCaptain(starshipName: String): String {
        return starshipCaptains[starshipName] ?: "알 수 없음"
    }

    open fun addCaptain(starshipName: String, captainName: String) {
        starshipCaptains[starshipName] = captainName
    }
}
```

어느 날 관리자가 긴급 요구 사항을 갖고 왔다. 이제부터 선장을 검색하면 반드시 콘솔에 로그를 남겨야 한다는 것이다. 간단해 보이는 작업이지만 한 가지 제약 사항이 있다. StarTrekRepository 클래스를 직접 수정하면 안된다는 것이다. 다른 사람들도 이 클래스를 이용하는데 그들은 로그 기록을 원치 않기 때문이다.

상속 후 오브라이드하여 잘 구현하였다. 그런데 이후 검증 요구사항이 추가되었다. 그러자 클래스 이름을 LoggingGetCaptainValidationgAddAcaptainStarTrekRepository로 지어야 할 것 같다..

이런 종류의 문제는 의외로 매우 흔하게 볼 수 있는데, 곧 다지안 패턴이 필요하다는 뚜렷한 징후다.

데코레이터 패턴의 목적은 객체에 새로운 동작을 동적으로 추가하는 것이다.

먼저 StarTrekRepository를 인터페이스로 변경해 보자

```kotlin
interface StarTrekRepository {
    fun getCaptain(starshipName: String): String
    fun addCaptain(starshipName: String, captain: String)
}
```

이제 기존과 동일한 로직을 사용해서 인터페이스를 구현한다.

```kotlin
class DefaultTrekRepository : StarTrekRepository {
    private val starshipCaptains = mutableMapOf("USS 엔터프라이즈 " to "장뢱 피카드")

    override fun getCaptain(starshipName: String): String {
        return starshipCaptains[starshipName] ?: "알 수 없음"
    }

    override fun addCaptain(starshipName: String, captainName: String) {
        starshipCaptains[starshipName] = captainName
    }
}
```

다음으로는 이 구체 클래스를 상속받는 대신 인터페이스를 구현하고 by라는 새로운 키워드를 사용할 것이다.

```kotlin
class LoggingGetCaptain(
    private val repository: StarTrekRepository
): StarTrekRepository by repository {
    override fun getCaptain(starshipName: String): String {
        println("$starshipName 함선의 선장을 조회 중입니다.")
        return repository.getCaptain(starshipName)
    }
}
```

by 키워드는 인터페이스 구현을 다른 객체에 위임한다. 그래서 인터페이스에 선언된 함수를 하나도 구현할 필요가 없었던 것이다. 이 인스턴스가 감싸고 있는 다른 객체가 기본적으로 모든 구현을 대신한다.

여기서는 클래스의 시그니처가 어떤 의미인지 주의 깊게 살펴봐야 한다. 데코레이터 패턴을 구현할 때 필요한 요소는 다음과 같다.

- 데코레이션(새로운 동작)을 추가할 대상 객체를 입력으로 받는다.
- 대상 객체에 대한 참조를 계속 유지한다.
- 데코레이터 클래스의 메서드가 호출되면 들고 있는 대상 객체의 동작을 변경할지 또는 처리를 위임할지 결정한다.
- 대상 객체에서 인터페이스를 추출하거나 또는 해당 클래스가 이미 구현하고 있는 인터페이스를 사용한다.

데코레이터의 메서드에서는 더 이상 super 키워드를 사용하지 않는다는 점에 유의하라. super를 사용하려고 하면 오류가 발생할 것이다. 클래스를 상속받는게 아니기 때문이다.

두 번째 데코레이터를 작성해 보자.

```kotlin
class ValidatingAdd(
    private val repository: StarTrekRepository,
) : StarTrekRepository by repository{
    private val maxNameLength = 7
    override fun addCaptain(starshipName: String, captainName: String) {
        require(captainName.length < maxNameLength) {
            "${captainName}의 이름이 7자를 넘습니다."
        }
        repository.addCaptain(starshipName, captainName)
    }
}
```

구현 클래스를 사용하는 법을 살펴보자.

```kotlin
fun main() {
    val starTrekRepository = DefaultTrekRepository()
    val withValidating = ValidatingAdd(starTrekRepository)
    val withLoggingAndValidating = LoggingGetCaptain(withValidating)
    withLoggingAndValidating.getCaptain("USS 엔터프라이즈")
    withLoggingAndValidating.addCaptain("USS 보이저", "캐서린 제인웨이")
}
```

결과

```
USS 엔터프라이즈 함선의 선장을 조회 중입니다.
```

## 연산자 오버로딩

다음과 같이 맵처럼 사용할 수 있다면 편리할 것이다.

```kotlin
withLoggingAndValidating["USS 엔터프라이즈"]
withLoggingAndValidating["USS 보이저"] = "캐서린 제인웨이"
```

코틀린에선 이는 그리 어려운 일이 아니다. 먼저 인터페이스를 다음과 같이 변경한다.

```kotlin
interface StarTrekRepository {
    operator fun get(starshipName: String): String
    operator fun set(starshipName: String, captain: String)
}
```

함수 선언부 앞에 operator 키워드를 추가했다.

오버로딩 코틀린 참고

https://kotlinlang.org/docs/operator-overloading.html#binary-operations

전체 코드

```kotlin
fun main() {
    val starTrekRepository = DefaultTrekRepository()
    val withValidating = ValidatingAdd(starTrekRepository)
    val withLoggingAndValidating = LoggingGetCaptain(withValidating)
//    withLoggingAndValidating.getCaptain("USS 엔터프라이즈")
//    withLoggingAndValidating.addCaptain("USS 보이저", "캐서린 제인웨이")
    withLoggingAndValidating["USS 엔터프라이즈"]
    withLoggingAndValidating["USS 보이저"] = "캐서린 제인웨이"
}

interface StarTrekRepository {
    operator fun get(starshipName: String): String
    operator fun set(starshipName: String, captain: String)
}

class DefaultTrekRepository : StarTrekRepository {
    private val starshipCaptains = mutableMapOf("USS 엔터프라이즈 " to "장뢱 피카드")

    override fun get(starshipName: String): String {
        return starshipCaptains[starshipName] ?: "알 수 없음"
    }

    override fun set(starshipName: String, captainName: String) {
        starshipCaptains[starshipName] = captainName
    }
}

class LoggingGetCaptain(
    private val repository: StarTrekRepository
): StarTrekRepository by repository {
    override fun get(starshipName: String): String {
        println("$starshipName 함선의 선장을 조회 중입니다.")
        return repository.get(starshipName)
    }
}

class ValidatingAdd(
    private val repository: StarTrekRepository,
) : StarTrekRepository by repository{
    private val maxNameLength = 7
    override fun set(starshipName: String, captainName: String) {
        require(captainName.length < maxNameLength) {
            "${captainName}의 이름이 7자를 넘습니다."
        }
        repository.set(starshipName, captainName)
    }
}
```
