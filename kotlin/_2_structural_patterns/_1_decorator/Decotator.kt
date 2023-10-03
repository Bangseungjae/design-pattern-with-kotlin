package _2_structural_patterns._1_decorator


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
