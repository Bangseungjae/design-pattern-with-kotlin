package study.io._3_desine_for_concurrency._2_barrier

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    runBlocking {
        println(
            measureTimeMillis {
                fetchFavoriteCharacterWrong("bsj")
            }
        )
        println(
            measureTimeMillis {
                fetchFavoriteCharacterCorrect("bsj")
            }
        )

        val (name, catchphrase, _) = fetchFavoriteCharacterCorrect("이니고 몬토야")
        println("$name: $catchphrase")

        val characters: List<Deferred<FavoriteCharacter>> = listOf(
            Me.getFavoriteCharacter(),
            Taylor.getFavoriteCharacter(),
            Michael.getFavoriteCharacter(),
        )
        println(characters.awaitAll())
    }
}

data class FavoriteCharacter(
    val name: String,
    val catchphrase: String,
    val picture: ByteArray = Random.nextBytes(42)
)

fun CoroutineScope.getCatchphraseAsync(
    characterName: String,
) = async {
    // 네트워크를 연결하낟고 생각
    delay(100)
    when (characterName) {
        "이니고 몬토야" -> "안녕 나는 몬토야야."
        else -> "이름을 못찾았어요."
    }
}

fun CoroutineScope.getPicture(
    characterName: String,
) = async {
    // 네트워크를 연결하낟고 생각
    delay(100)
    when (characterName) {
        else -> Random.nextBytes(42)
    }
}

suspend fun fetchFavoriteCharacterWrong(name: String): FavoriteCharacter = coroutineScope {
    val catchphrase = getCatchphraseAsync(name).await()
    val picture = getPicture(name).await()
    FavoriteCharacter(name, catchphrase, picture)
}

suspend fun fetchFavoriteCharacterCorrect(name: String) = coroutineScope {
    val catchphrase = getCatchphraseAsync(name)
    val picture = getPicture(name)
    FavoriteCharacter(name, catchphrase.await(), picture.await())
}

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
