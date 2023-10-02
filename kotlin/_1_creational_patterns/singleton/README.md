# 싱글톤 패턴

일반적으로 어떤 클래스가 있으면 원하는 만큼 인스턴스를 만들어 낼 수 있다. 예를 들어 나와 당신이 좋아하는 영화의 목록을 리스트로 만들어 보자.

```kotlin
val myFavoriteMovies = listOf("블랙 호크 다운", "블레이드 러너")
val yourFavoriteMovies = listOf(...)
```

별 문제없이 List 인스턴스를 원하느 만큼 만들 수 있다. 대부분의 클래스는 이렇게 인스턴스를 가질 수 있다.

다음에는 <분노의 질주> 영화 시리즈 중 최고를 뽑아 보자.

```kotlin
val myFavoriteQuickAndAngryMovies = listOf()
val yourFavoriteQuickAndAngryMovies = listOf()
```

둘 다 빈 리스트이며, 따라서 완전 동일하다. 그리고 불변 리스트이기 때문에 항상 빈 상태를 유지할 수 있을 것이다. <분노의 질주> 같은 시리즈에서 최고의 영화를 뽑을 수 있을 리 만무하다.

**Equals 메서드**를 사용하면 두 인스턴스가 완전히 같다는 것을 확인할 수 있다. 그렇다면 메모리에 인스턴스를 여럿 둘 필요가 전혀 없다. 빈 리스트를 가리키는 모든 참조가 하나의 인스턴스를 가리키면 좋을 것이다. 생각해 보면 null도 그렇다. 모든 null은 동일하다.

이게 싱글톤 패턴의 주요 개념이다.

싱그톤 다지안 패턴의 요구 사항 두 가지는 다음과 같다.

- 시스템에 인스턴스가 딱 하나만 존재해야 한다.
- 시스템의 모둔 부분에서 인스턴스에 접근할 수 있어야 한다.

자바를 비롯한 여러 언어에서 이 요구 사항을 만족시키는 것은 꽤나 복잡하다.(Spring Framework를 쓰면 간단하긴 하다..) 먼저 클래스의 생성자를 private으로 만들어서 새로운 인스턴스가 생성돼지 않도록 해야 한다.

그리고 인스턴스 생성이 게으르고 스레드 안전하며 성능을 저해하지 않도록 해야 한다.

각 요구 사항을 자세히 설명하면 다음과 같다.

- 게으른 인스턴스 생성: 프로그램이 시작되자마자 싱글톤 인스턴스가 만들어지면 안된다. 인스턴스 생성에 많은 비용이 들 수 잇기 때문이다. 인스턴스 생성은 필요한 첫 순간에 이뤄져야 한다.
- 스레드 안전한 인스턴스 생성: 두 스레드가 동시에 싱글톤 객체를 생성하려고 할 때 두 스레드가 같은 인스턴스를 획득해야 한다.
- 고성능의 인스턴스 생성: 많은 스레드가 동시에 싱글톤 객체를 생성하려고 할 때 스레드를 너무 오래 기다리게 하면 안 된다. 잘못하면 실행이 중단될 수 있다.

자바나 C++에서 이를 만족하는 것은 만만찮은 일이며 상당히 많은 코드가 필요하다.

코틀린에는 싱글톤 객테 생성을 쉽게 할 수 잇도록 object라는 키워드가 도입됐다. 스칼라에도 같은 키워드가 존재한다. object 키워드를 사용하면 위의 모든 요구 사항을 만족하는 싱글톤 객체를 구현할 수 있다.

중요

```kotlin
object 키워드에는 싱글톤 객체 생성 말고도 다른 쓰임새가 있다.
```

싱글톤 객체는 일반적인 클래스와 동일한 방법으로 선언하되 생성자는 정의하지 않는다. 싱글톤 객체는 직접 인스턴스화 할 수 없기 때문이다.

```kotlin
object NoMoviesList
```

이제 NoMoviesList는 코드 어디서든 접근할 수 있으며 딱 1개의 객체만 존재할 것이다.

```kotlin
val myFavoriteQuickAndAngryMovies = NoMoviesList
val yourFavoriteQuickAndAngryMovies = NoMoviesLis
println(myFavoriteQuickAndAngryMovies === yourFavoriteQuickAndAngryMovies)
// true
```

싱글톤 패턴으로 만든 빈 영화 리스트를 전달하면 다음과 같이 컴파일이 실패한다.

```kotlin
printMovies(myFavoriteQuickAndAngryMovies){
// Type mistmatch: inferred type is NoMiviesList but
// List<String> was expected
}
```

영화 리스트 출력 함수는 인수로 문자열 리스트만 받을 수 있는데 NoMoviesList가 문자열 리스트라는 정보는 아무 데도 없기 대문에 오류가 발생 한다.

다행히 코틀린에서는 싱글톤 객체도 인스턴스를 구현할 수 있다. 그리고 제네릭 리스트를 나타내는 인터페이스가 존재한다.

```kotlin
object NoMoviesList : List<String>
```

이제 필수 함수를 구현하라는 메시지가 뜰 것이다. 싱글톤 객체에 본문을 추가해서 필수 함수를 구현해 보자.

```kotlin
object NoMoviesList : List<String> {
	override val size = 0
  override fun contains(element: String) = false
  ... // 다른 함수 구현
}
```

다른 함수 구현은 독자에게 맡기겠다. 그러나 꼭 구현하지 않아도 된다. 타입에 관계없이 코틀린의 `emptyList()` 함수를 사용하면 되기 때문이다.

```kotlin
printMovies(emptyList())
```

싱글톤 객체는 클래스와 결정적으로 다른 부분이 하나 있다. 생성자를 가질 수 없다는 점이다. 싱글톤 객체 초기화가 필요하다면 다음과 같이 Init 블록을 사용할 수 있다.

```kotlin
object Logger{
	init {
		println("싱글톤 객체에 처음 접근했습니다.")
		// 여기에 초기화 로직을 작성
  }
// 다른 코드는 여기에 작성
}
```

만약 싱글톤 객체에 한 번도 접근하지 않았다면 초기화 로직은 실행되지 않고, 따라서 자원이 절약된다. 이를 **게으른 초기화**라고 부른다.
![img.png](img.png)

```kotlin
fun main() {
    var a = Logger
    var b = Logger
}
```
![img_1.png](img_1.png)
