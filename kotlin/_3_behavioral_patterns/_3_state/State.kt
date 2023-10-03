package _3_behavioral_patterns._3_state

interface WhatCanHappen {
    fun seeHero()
    fun getHit(pointsOfDamage: Int)
    fun calmAgain()
}

class Snail {
    internal var healthPoints = 10
    internal var mood: Mood = Still(this)
}

//class Snail : WhatCanHappen {
//    private var healthPoints = 10
//    internal var mood: Mood = Still(this)
//
//    override fun seeHero() {
//        mood = when (mood) {
//            is Still -> Aggressive
//            else -> mood
//        }
//    }
//
//    override fun getHit(pointsOfDamage: Int) {
//        healthPoints -= pointsOfDamage
//
//        mood = when {
//            (healthPoints <= 0) -> Dead
//            mood is Aggressive -> Retreating
//            else -> mood
//        }
//    }
//
//    override fun calmAgain() {
//        TODO("Not yet implemented")
//    }
//}

sealed class Mood : WhatCanHappen {
    // 여기에 다양한 추상 메서드를 선언한다.(draw() 등).
}

class Still(private val snail: Snail) : Mood() {
    override fun seeHero() {
        snail.mood = Aggressive(snail)
    }

    override fun getHit(pointsOfDamage: Int) {
        snail.healthPoints -= pointsOfDamage

        snail.mood = when {
            (snail.healthPoints <= 0) -> Dead(snail)
            snail.mood is Aggressive -> Retreating(snail)
            else -> snail.mood
        }
    }

    override fun calmAgain() {
        snail.mood = Still(snail)
    }
}
class Aggressive(snail: Snail) : Mood() {
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
class Retreating(snail: Snail) : Mood() {
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

class Dead(snail: Snail) : Mood() {
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
