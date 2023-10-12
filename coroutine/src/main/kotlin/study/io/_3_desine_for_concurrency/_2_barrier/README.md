# 장벽 패턴(Barrier Pattern)

장벽(barrier) 디자인 패턴을 사용하면 프로그램을 잠시 멈추고 여러 개의 동시성 작업이 완료되기를 기다릴 수 있다. 일반적으로는 여러 곳에서 자료를 가져올 때 장벽 패턴을 사용한다.

예를 들어 다음과 같은 클래스가 있다고 하자.

```kotlin
data class FavoriteCharacter(
    val name: String,
    val catchphrase: String,
    val picture: ByteArray = Random.nextBytes(42)
)
```

여기서 catchphrase 데이터와 picture 데이터를 각각 서로 다른 서비스를 통해 받아 와야 한다고 가정하자. 다음과 같이 두 데이터를 비동기적으로 받아 오려고 한다.

```kotlin
fun CoroutineScope.getCatchphraseAsync(
    characterName: String,
) = async {
    // 네트워크를 연결하낟고 생각
    // ...
}

fun CoroutineScope.getPicture(
    characterName: String,
) = async {
    // 네트워크를 연결하낟고 생각
    // ...
}
```

두 데이터를 받아오는 동시성 코드를 가장 기본적인 방법으로 구현하면 다음과 같다.

```kotlin
suspend fun fetchFavoriteCharacter(name: String) = coroutineScope { 
    val catchphrase = getCatchphraseAsync(name).await()
    val picture = getPicture(name).await()
    FavoriteCharacter(name, catchphrase, picture)
}
```

하지만 이 코드에는 큰 문제가 있다. catchphrase 데이터 수신을 완료하기 전까지는 picture 데이터를 받아오지 못한다는 것이다. 즉 이 코드는 불필요한 순차성을 갖고있다. 어떻게 개선할 수 있는지 살펴보자.

### 데이터 클래스를 장벽으로 사용하기

앞의 코드를 조금 바꿔서 다음과 같이 작성할 수 있다.

```kotlin
suspend fun fetchFavoriteCharacter(name: String) = coroutineScope {
    val catchphrase = getCatchphraseAsync(name)
    val picture = getPicture(name)
    FavoriteCharacter(name, catchphrase.await(), picture.await())
}
```

이렇게 await 함수 호출을 데이터 클래스 생성자로 옮기면 코루틴을 동시에 시작하고 모든 코루틴이 완료되기를 기다리게 된다. 원하던 대로다.

데이터 클래스를 장벽으로 사용하면 좋은 점이 또 있다. 쉽게 분해가 가능하다는 점이다.

```kotlin
val (name, catchphrase, _) = fetchFavoriteCharacterCorrect("이니고 몬토야")
        println("$name: $catchphrase")
```

여러 비동기 작업이 서로 다른 타입의 결과를 반환할 때는 이 방법을 사용하면 좋다. 하지만 같은 타입의 결과를 기다려야 하는 상황도 있다.

예를 들어 마이클(게임 개발 팀의 카나리아), 테일러(바리스타), 그리고 내가 가장 좋아하는 영화 주인공을 이야기하는 코드를 작성해 보자.

```kotlin
object Michael {
    suspend fun getFavoriteCharacter() = coroutineScope { 
        async { 
            FavoriteCharacter("터미네이터", "Hasta ls vista, baby")
        }
    }
}

object Taylor {
    suspend fun getFavoriteCharacter() = coroutineScope { 
        async { 
            FavoriteCharacter("돈 비토 코를레오네", "그 자에게 거절할 수 없는 제안을 하겠다.")
        }
    }
}

object Me {
    suspend fun getFavoriteCharacter() = coroutineScope { 
        async { 
            // 이미 대답을 준비했지!
            FavoriteCharacter("이니고 몬토야", "안녕, 난 이니고 몬토야다.")
        }
    }
}
```

이번에는 새 객체가 거의 비슷하다. 반환값의 내용만 다를 뿐이다.

이 경우에는 결과를 수집하기 위해 리스트를 사용할 수 있다.

```kotlin
val characters: List<Deferred<FavoriteCharacter>> = listOf(
    Me.getFavoriteCharacter(),
    Taylor.getFavoriteCharacter(),
    Michael.getFavoriteCharacter(),
)
```

리스트의 타입을 눈여겨보라. 지연된 FavoriteCharacter를 모아 놓은 리스트다. 이런 자료 구조에 대해서는 awaitAll() 이라는 함수를 호출할 수 있는데, 이 함수는 장벽의 기능을 한다.

```kotlin
println(characters.awaitAll())
```

이처럼 같은 타입을 갖는 여러 비동기 작업을 사용하며 다음 단계로 넘어가기 전에 모든 작업이 완료되기를 원한다면 `awaitAll()` 함수를 사용하라.

장벽 디자인 패턴을 여러 비동기 작업이 한 곳으로 모이도록 한다. 다음에 살펴볼 패턴은 여러 작업의 실행을 추상화하는 것을 도와준다.
