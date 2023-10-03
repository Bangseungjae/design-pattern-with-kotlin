# 중개인 패턴(Mediator Pattern)

우리의 게임 개발 팀이 심각한 문제에 맞닥뜨렸다. 품질 보증 담당자가 없다는 것이다.

최근 마이클이 케니(Kenny)라는 앵무새를 데리고 왔다. 그리고 케니는 품질 보증 업무를 담당하게 됐다.

```kotlin
interface QA {
    fun doesMyCodeWork(): Boolean
}

interface Parrot {
    fun isEating(): Boolean
    fun isSleep(): Boolean
}

object Kenny : QA, Parrot {
    // 앵무새 스케줄에 따라 인터페이스 메서드 구현
}
```

```kotlin
object Me
class MyCompany(private val productManager: ProductManager) {
    val cto = Me
    val qa = Kenny
    fun taskCompleted() {
        if (!qa.isEating() && !qa.isEating()) {
            println(qa.doesMyCodeWork())
        }
    }
}
```

케니가 궁금한 것이 생기면 물어볼 수 있도록 내 전화번호를 알려 줬다.

```kotlin
object Kenny : QA, Parrot {
    // 앵무새 스케줄에 따라 인터페이스 메서드 구현
    val developer = Me
		...
}
```

케니는 성실한 앵무새다. 하지만 게임에 버그가 너무 많은 나머지 품질 보증 담당 앵무새를 한 마리 더 고용해야 할 것 같다. 새 앵무새의 이름은 브래드(Brad)다. 케니가 한가할 때는 프로젝트를 더 잘 아는 케니에게 일을 맡긴다. 그러나 케니가 바쁘면 브래드가 한가한지 살핀 후 그에게 일을 준다.

```kotlin
class MyCompany(private val productManager: ProductManager) {
		...
    val qa2 = Brad
    fun taskCompleted() {
        ...
        } else if (!qa2.isEating() && !qa.isSleep()) {
            println(qa2.doesMyCodeWork())
        }
    }
}
```

경험이 부족한 브래드는 보통 케니에게 먼저 물어본다. 또한 케니는 브래드에게도 내 번호를 알려 줬다.

```kotlin
object Brad : QA, Parrot {
    val senior = Kenny
    val developer = Me
}
```

이번엔 브래드가 조지(George)를 데려왔다. 조지는 부엉이라 케니나 브래드와는 다른 시간에 잔다. 즉 조지는 밤에 코드를 확인할 수 있다.

조지는 항상 케니와 내게 질문을 한다.

```kotlin
object George : QA, Owl {
    val developer = Me
    val mate = Kenny
    // ...
}
```

문제는 조지가 열혈 축구 팬이라는 점이다. 조지를 부를 때는 혹시 경기를 보고 있는 것은 아닌지 먼저 확인해야 한다.

```kotlin
class MyCompany(private val productManager: ProductManager) {
	 // ...
    val qa3 = George
    fun taskCompleted() {
				// ...
        } else if (!qa3.isWatchingFootball()) {
            println(qa3.doesMyCodeWork())
        }
    }
}
```

어느 날 케니는 평소와 달리 조지에게 질문을 하려고 한다. 조지는 굉장히 박식한 부엉이이기 때문이다.

```kotlin
object Kenny : QA, Parrot {
    val developer = Me
    val peer = George
    ...
}
```

이번에는 샌드라(Sandra)가 등장했다. 그런데 그는 QA업무가 아니라 카피라이터 일을 맡았다.

```kotlin
interface Copywriter {
    fun areAllTextsCorrect(): Boolean
}

interface Kiwi

object Sandra : Copywriter, Kiwi {
    override fun areAllTextsCorrect(): Boolean {
        return true
    }
}
```

주요 릴리스가 있지 않는 한 그를 귀찮게 하지 않으려고 한다.

```kotlin
class MyMind {
    val translator = Sandra
    fun taskCompleted(isMajorRelease: Boolean) {
        // ...
        if (isMajorRelease) {
            println(translator.areAllTextsCorrect())
        }
    }
}
```

여기에는 몇 가지 문제가 있다.

- 첫째, 모든 이름을 기억하느라 머리가 터질 지경이다. 독자들도 그럴 것이다.
- 둘째, 직원들과 의사소통을 어떻게 해야 하는지 일일이 기억해야 한다. 직원을 호출하기 전에 일을 시킬 수 있는 상태인지 항상 확인해야 한다.
- 셋째, 조지는 모든 것을 케니에게 물어보고, 케니는 조지에게 물어본다. 다행히 지금까지는 케니에게 질문이 생겼을 때 조지는 축구 경기를 보고 있었다. 그리고 조지에게 질문이 생겼을 때는 케니가 잠들어 있었다. 만약 그렇지 않았다면 둘은 전화기를 잡고 영원히 서로에게 질문을 하고 있었을 것이다.
- 넷째, 가장 골치 아픈 것은 케니가 곧 새로운 ParrotPi라는 회사를 창업해서 팀을 떠날 예정이라는 것이다. 그렇게 되면 얼마나 많은 코드를 변경해야 할지 생각해 보라!

## 중개인

중개인 디자인 패턴은 통제욕이 강한 괴짜와도 같다. 그는 다른 객체끼리 직접 대화하는 것을 두고 보지 못한다.

모두가 중개인과 대화해야 한다. 왜 그래야 하는가? 객체 간 결합도를 낮추기 위해서다. 모든 객체는 다른 객체를 직접 아는 대신 중개인 하나만 알면 된다.

```kotlin
interface ProductManager {
    fun isAllGood(majorRelease: Boolean): Boolean
}
```

마이클만 모든 새 직원에 대한 정보를 갖는다.

```kotlin
object Michael : Canary, ProductManager {
    private val kenny = Kenny(this)
    private val bard = Brad(this)
    override fun isAllGood(majorRelease: Boolean): Boolean {
        if (!kenny.isEating() && !kenny.isSleep()) {
            println(kenny.doesMyCodeWork())
        } else if (!bard.isEating() && !bard.isSleep()) {
            println(bard.doesMyCodeWork())
        }
        return true
    }
}
```

중개인은 굉장히 간단한 인터페이스를 제공함으로써 여러 객체 간의 복잡한 상호작용을 캡슐화한다.

마이클 객체만 알면 다른 직원 관리는 마이클이 할 것이다.

```kotlin
class MyCompany(private val manager: ProductManager) {
    fun taskCompleted(isMajorRelease: Boolean) {
        println(manager.isAllGood(isMajorRelease))
    }
}
```

또한 모든 직원이 마이클의 전화번호만 알고 있도록 하고, 내 전화번호는 바꿔 버렸다.

```kotlin
class Brad(private val productManager: ProductManager) : QA, Parrot {
    // Me에 대한 참조 제거
	//...
}
```

이제 누군가 질문거리가 생기면 먼저 마이클에게 연락할 것이다.

```kotlin
class Kenny(private val productManager: ProductManager) : QA, Parrot {
    // 조지를 비롯한 다른 직원에 대한 참조 제거
		...
}
```

## 중개인 패턴의 두 가지 유형

- 엄격한 중개인
- 느슨한 중개인

방금 본 유형이 엄격한 중개인에 해당한다. 원하는 바를 중개인에게 이야기하면 중개인이 바로 응답해 준다.

반면 느슨한 중개인을 활용할 때는 중개인에게 무슨 일이 일어났는지만 전달하고 즉시 답을 얻지는 않는다. 대신 중개인이 응답할 내용이 생기면 그때 객체를 따로 호출한다.

전체 코드

```kotlin
package _3_behavioral_patterns

fun main() {
    val productManager = Michael
    val company = MyCompany(productManager)
    company.taskCompleted(true)
}

interface QA {
    fun doesMyCodeWork(): Boolean
}

interface Parrot {
    fun isEating(): Boolean
    fun isSleep(): Boolean
}

object Michael : Canary, ProductManager {
    private val kenny = Kenny(this)
    private val bard = Brad(this)
    override fun isAllGood(majorRelease: Boolean): Boolean {
        if (!kenny.isEating() && !kenny.isSleep()) {
            println(kenny.doesMyCodeWork())
        } else if (!bard.isEating() && !bard.isSleep()) {
            println(bard.doesMyCodeWork())
        }
        return true
    }
}

interface Canary {

}

interface Manager {
    fun isAllGood(majorRelease: Boolean): Boolean
}

interface ProductManager {
    fun isAllGood(majorRelease: Boolean): Boolean
}

interface Copywriter {
    fun areAllTextsCorrect(): Boolean
}

interface Kiwi

object Sandra : Copywriter, Kiwi {
    override fun areAllTextsCorrect(): Boolean {
        return true
    }
}

class Kenny(private val productManager: ProductManager) : QA, Parrot {
    // 조지를 비롯한 다른 직원에 대한 참조 제거
    override fun isEating(): Boolean {
        return false
    }

    override fun isSleep(): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesMyCodeWork(): Boolean {
        return true
    }
}

class Brad(private val productManager: ProductManager) : QA, Parrot {
    // Me에 대한 참조 제거
    override fun isEating(): Boolean {
        return false
    }

    override fun isSleep(): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesMyCodeWork(): Boolean {
        return true
    }
}
interface Owl

object Me

class MyCompany(private val manager: ProductManager) {
    fun taskCompleted(isMajorRelease: Boolean) {
        println(manager.isAllGood(isMajorRelease))
    }
}
```

<br/>

## 중개인 패턴 사용 시 주의 사항

갑자기 마이클이 매우 중요한 직원이 됐다. 모든 직원이 마이클을 알고, 그가 아니면 직원 간 의사소통을 관리할 수 있는 직원이 없다. 자칫 전지전능한 ‘신(God) 객체’가 될 수도 있다. 신 객체를 만드는 것은 안티 패턴 중 하나다.

아무리 마이클이 중요한 객체이더라도 그가 무엇을 해야 하며 무엇을 하면 안 되는지를 명확하게 정의하라(그중에서도 중개인이 하면 안되는 일을 명확하게 정의하는 것이 중요하다).
