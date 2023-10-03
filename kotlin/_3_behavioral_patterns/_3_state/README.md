# 상태 패턴(State Pattern)

상태 패턴은 처음에 다뤘던 전략  패턴의 일종이라 생각할 수 있다. 다만 전략 패턴에서는 외부의 클라이언트가 전략을 교체하는 반면, 상태 패턴에서의 상태는 오로지 입력에 의해 내부적으로 변경된다.

클라이언트가 전략 패턴을 사용하면서 나눈 대화를 살펴보자.

- **클라이언트**: 여기 새로운 할 일이 있어. 이제부터는 이 일을 해줘.
- **전략**: 네, 알겠습니다.
- **클라이언트**: 늘 군말 없이 따라 줘서 마음에 들어.

하지만 다음의 대화는 좀 다르다.

- **클라이언트**: 새로운 입력을 가져왔어.
- **상태**: 글쎼요. 동작을 바꿀 수도 있습니다. 아닐 수도 있고요.

사용자는 심지어 어떤 입력이 거부될 수 있다는 것까지도 염두에 둬야 한다.

- **클라이언트**: 이 입력을 잘 처리해 봐.
- **상태**: 무슨 말인지 모르겠네요! 저 바쁜 거 안보이세요? 전략 패턴에게나 시키세요!

대체 클라이언트가 이런 상태 패턴을 사용해야 할 이유는 무엇일까? 그것은 상태 패턴을 사용하면 모든 것을 통제 아래에 둘 수 있기 때문이다.

2차원 게임 예시로 돌아가자. 육식성 달팽이는 이제 너무 많이 당한 듯하다. 계속 콩과 바나나를 쏴 대자 새로운 던전이 펼쳐졌다. 여기서는 달패이가 움직인다.!

객체(이 경우에는 게임의 몬스터)가 동작을 바꿔야 할 때 상태 패턴이 어떤 도움이 되는지 알아보자.

기본적으로 달팽이는 가만히 서 있으면서 달팽이 에너지를 모은다. 그러다가 주인공이 가까이 오면 주인공에게 공격적으로 달려든다.

주인공이 달팽이에게 데미지를 입히면 달팽이는 뒤로 물러나서 상처를 핥는다.
그리고 다시 공격하기를 어느 한쪽이 죽을 때까지 반복한다.

먼저 달팽이의 생애 동안 일어나는 일을 다음과 같이 선언한다.

```kotlin
interface WhatCanHappen {
    fun seeHero()
    fun getHit(pointsOfDamage: Int)
    fun calmAgain()
}
```

달패이 객체는 이 인터페이스를 구현함으로써 자신에게 발생하는 이벤트를 인식하고 이에 반응한다.

```kotlin
class Snail : WhatCanHappen {
    private var healthPoints = 10
    override fun seeHero() {
        TODO("Not yet implemented")
    }

    override fun getHit(pointsOfDamage: Int) {
        TODO("Not yet implemented")
    }

    override fun calmAgain() {
        TODO("Not yet implemented")
    }
}
```

이제 달팽이의 상태를 나타내는 Mood 클래스를 선언해 보자. 이 클래스는 sealed 키워드를 붙여서 봉인 클래스(sealed class)로 만들 것이다.

```kotlin
sealed class Mood {
    // 여기에 다양한 추상 메서드를 선언한다.(draw() 등).
}
```

봉인 클래스는 추상 클래스이면서 인스턴스화가 불가능하다. 왜 이런 클래스를 사용하는지는 곧 알게 될 것이다. 먼저 여러 상태를 선언해보자.

```kotlin
object Still : Mood()
object Aggressive : Mood()
object Retreating : Mood()
object Dead : Mood()
```

각 달팽이의 상태를 나타낸다.

상태 패턴에서 Snail 클래스는 맥락(context)의 역할을 한다. Snail 클래스가 현재 상태를 저장한다는 뜻이다. 따라서 Snail 클래스에 다음과 같이 상태를 저장하기 위한 멤버를 선언한다.

```kotlin
class Snail : WhatCanHappen {
    private var healthPoints = 10
    private var mood: Mood = Still
    // 이전 코드와 동일
}
```

이제 달패이가 주인공 캐릭터를 봤을 때 어떤 행동을 하는지 정의하자.

```kotlin
override fun seeHero() {
        mood = when (mood) {
            is Still -> Aggressive
        }
    }
```

이 코드는 컴파일되지 않는다. Mood가 봉인 클래스로 선언했기 때문이다. 코틀린은 마치 enum의 원소가 몇 개인지 알고 있는 것처럼 봉인 클래스를 상속하는 클래스가 몇 개인지 알고 있다. 따라서 when에는 반드시 모든 경우가 나열돼야 한다.

나머지의 경우에 대해서는 else를 사용해서 상태 변화가 없음을 나타낼 수 있다.

```kotlin
override fun seeHero() {
        mood = when (mood) {
            is Still -> Aggressive
            else -> mood
        }
    }
```

달팽이가 데미지를 입었을 때는 죽었는지 검사할 필요가 있다. 이때도 when을 사용하되 인자를 전달하지 않는다.

```kotlin
override fun getHit(pointsOfDamage: Int) {
        healthPoints -= pointsOfDamage

        mood = when {
            (healthPoints <= 0) -> Dead
            mood is Aggressive -> Retreating
            else -> mood
        }
    }
```

여기서 Is 키워드는 자바의 instanceof와 같은 역할을 하지만 훨씬 더 간결하다.

<br/>

## 큰 규모의 상태 패턴

앞서 설명한 방법으로 필요한 로직은 거의 다 구현할 수 있다. 그러나 필요한 로직의 규모가 커지면 다른 접근 방법을 사용하는 경우도 있다.

그런 경우 Snail 객체가 더 간결해진다.

```kotlin
class Snail {
    internal var healthPoints = 10
    internal var mood: Mood = Still(this)
}
```

`mood`를 `internal`로 선언했다는 점에 주목하라. 이렇게 하면 같은 패키지에 있는 다른 클래스가 이 변수를 변경할 수 있다. 이번에는 `Snail` 클래스를 대신해서 `Mood` 클래스가 `WhatCanHappen`을 구현할 것이다.

```kotlin
class Still(private val snail: Snail) : Mood() {
    override fun seeHero() {
        snail.mood = Aggressive
    }

    override fun getHit(pointsOfDamage: Int) {
        snail.healthPoints -= pointsOfDamage

        snail.mood = when {
            (snail.healthPoints <= 0) -> Dead
            snail.mood is Aggressive -> Retreating
            else -> snail.mood
        }
    }

    override fun calmAgain() {
        snail.mood = Still(snail)
    }
}
```

이제 상태 객체가 생성자에서 맥락 객체(Snail)를 받는다는 점에 주목하라.

상대적으로 작성해야 하는 코드의 양이 적다면 첫 번째 방법을 사용하라. 상태 변화가 많다면 두 번째 방법을 사용하라. 실제 코드에서 상태 패턴이 널리 사용되는 한 가지 예는 코틀린의 **코루틴**이다.
