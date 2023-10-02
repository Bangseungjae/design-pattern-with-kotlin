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
        return false
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
        return false
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

