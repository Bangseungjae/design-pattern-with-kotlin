# 템플릿 메서드 패턴(Template Method Pattern)
<br/>

어떤 게으름뱅이들은 예술적인 게으름을 보여 주기도 한다. 나도 그중 하나다. 내 하루 일과는 다음과 같다.

1. 오전 8시 ~ 오전 9시: 사무실 도착
2. 오전 9시 ~ 오전 10시: 커피 마시기
3. 오전 10시 ~ 오후 12시: 회의 참석 또는 코드 리뷰
4. 오후 12시 ~ 오후 1시: 점심 식사
5. 오후 1시 ~ 오후 4시: 회의 참석 또는 코드 리뷰
6. 오후 4시: 슬그머니 퇴근

어떤 일정은 절대 바뀌지 않지만 어떤 일정은 유동적이다. 실제로 회의 참석이라고 돼있는 2개의 시간대에 몇 개의 회의가 잡힐지는 알 수 없다.

실제로 회의 참석이라고 돼있는 2새의 시간대에 몇 개의 회의가 잡힐지는 알 수 없다.

이 두  시간대를 준비 시간과 정리 시간으로 사용해서 유동적인 일과를 표현해 보기로 했다. 마침 그 시간에는 점심 식사가 있다. 아키텍트에게 점심 식사는 신성한 일과이디 때문이다.

자바 식으로 이를 구현하는 법은 뻔하다. 먼저 추상 클래스를 하나 만든다. 그리고 직접 구현하고자 하는 메서드에는 모두 private을 붙인다.

```kotlin
abstract class DayRoutine {
	private fun arrivetoWork() {
		println("부장님 안녕하세요! 저는 가끔 사무실에 등장합니다!!")
	}

	private fun drinkCoffee() {
		println("오늘은 커피 맛이 좋군.")
	}
// ...

	private fun goToLunch() {
		println("햄버거랑 감자튀김 세트요!")
	}

// ...
	private fun goHome() {
		// 아무도 알아채지 못 하는 게 핵심이다.
		// 절대로 조용히 해야 한다!
		println()
	}

}
```

매일 바뀌는 일과를 나타내는 메서드에는 모두 abstract 메서드가 붙어야 한다.

```kotlin
abstract class DayRoutine {
	// ...
	abstract fun doBeforeLunch()
  // ...
  abstract fun doAfterLunch()	
  // ...
}
```

유동적이긴 하지만 기본 구현을 작성하고 싶은 함수는 public 함수로 남겨 둔다.

```kotlin
abstract class DayRoutine {
	// ...
	open fun bossHook() {
		// 부장님이 불러 세우지 않기를
	}
	// ...
}
```

코틀린에서는 public이 기본 접근 제한자라는 것을 기억하라.

마지막으로 알고리듬을 실행하는 메서드가 필요하다. 이 메서드는 기본적으로 final이다.

```kotlin
abstract class DayRoutine {
	// ...
	fun runSchedule() {
		arriveToWork()
    drinkCoffee()
    beforeLunch()
    goToLunch()
    afterLunch()
		goHome()
	}
	// ...
}
```

이제 월요일 일과가 필요하다면 빠진 부분만 구현하면 된다.

```kotlin
class MondaySchedule : DayRoutine() {
	override fun doBeforeLunch() {
		println("쓸 데 없는 회의")
		println("코드 리뷰. 이게 무슨 코드죠?")
	}
	override fun doAfterLunch() {
		println("랄프와 회의")
		println("다른 아키텍트와 농담 따먹기")
	}
	override fun bossHook() {
		println("잠깐 사무실로 좀 올래요?")
	}
}
```

코틀린에는 조금 더 나은 방법이 있을까? 지금까지처럼은 코틀린은 간결성을 선사한다. 앞서 다른 패턴과 마찬가지로 함수를 사용하면 된다.

이 일과에는 유동적인 부분이 세 곳 있다. 필수 일과가 2개, 필수적이지 않은 일과가 1개 있다.

```kotlin
fun runSchedule(
    beforeLunch: () -> Unit,
    afterLunch: () -> Unit,
    bossHook: (() -> Unit)? = fun() { println() },
) {
// ...
}
```

3개의 다른 함수를 인수로 받는 함수를 작성할 것이다. 처음 둘은 필수 인수이며, 세 번째 인수는 아예 전달하지 않을 수도 있고 null을 전달해서 명시적으로 실행되지 않기를 원한다는 것을 나타낼 수도 있다.

```kotlin
fun runSchedule(...){
    arriveToWork()
    drinkCoffee
    beforeLunch()
    goToLunch()
    afterLunch()
    bossHook?.let { it() }
    goHome
}
```

bossHook은 null일 수 있기 때문에 null이 아닐 경우에만 실행해야 한다. 이를 위해 다음과 같은 문법을 사용한다.

```kotlin
?.let{ it() }
```

그런데 항상 직접 구현해 줘야 하는 다른 함수들은 어떻게 할까?

코틀린에는 지역 함수(local funtion)라는 개념이 있다. 다른 함수 안에 있는 함수를 말한다.

```kotlin
fun runSchedule(
    beforeLunch: () -> Unit,
    afterLunch: () -> Unit,
    bossHook: (() -> Unit)? = fun() { println() },
) {
    fun arriveToWork() {
        println("부장님 안녕하세요! 저는 가끔 사무실에 등장합니다!")
    }

    val drinkCoffee = { println("오늘은 커피 맛이 좋군.") }

    fun goToLunch() = println("햄버거랑 감자튀김 세트요!")

    val goHome = fun() {
        println("조용히 퇴근~")
    }

    arriveToWork()
    drinkCoffee
    beforeLunch()
    goToLunch()
    afterLunch()
    bossHook?.let { it() }
    goHome
```

모든 지역 함수를 선언하는 유효한 방법이다. 어떻게 정의하든 똑같이 호출할 수 있다.

지역 함수는 부모 함수 내에서만 접근 가능하며, 공통된 로직을 외부에 노출하지 않고서도 함수로 추출할 수 있는 멋진 방법이다.

이제 코드의 뼈대가 완성됐다. 알고리듬의 뼈대를 정의하되 세부 동작은 나중에 다른 누군가가 결정하도록 하는 것, 이것이 템플릿 메서드(Template method) 패턴의 요지다.
