package _2_structural_patterns.bridge

interface Trooper {
    fun move(x: Long, y: Long)
    fun attackRebel(x: Long, y: Long)
}

fun main() {
    val stormTrooper = StormTrooper(Rifle(), RegularLegs())
    val flameTrooper = StormTrooper(Flamethrower(), RegularLegs())
    val scoutTrooper = StormTrooper(Rifle(), AthleticLegs())
}

typealias PointsOfDamage = Long
typealias Meters = Int

data class StormTrooper(
    private val weapon: Weapon,
    private val legs: Legs,
) : Trooper {

    override fun move(x: Long, y: Long) {
        legs.move(x, y)
    }
    override fun attackRebel(x: Long, y: Long) {
        weapon.attack(x, y)
    }
}

interface Weapon {
    fun attack(x: Long, y: Long): PointsOfDamage
}

interface Legs {
    fun move(x: Long, y: Long): Meters
}

const val RIFLE_DAMAGE = 3L
const val REGULAR_SPEED: Meters = 1

class Rifle : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE
}
class Flamethrower : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE * 2
}
class Batton : Weapon {
    override fun attack(x: Long, y: Long): PointsOfDamage = RIFLE_DAMAGE * 3
}

class RegularLegs : Legs {
    override fun move(x: Long, y: Long): Meters = REGULAR_SPEED
}
class AthleticLegs : Legs {
    override fun move(x: Long, y: Long): Meters = REGULAR_SPEED * 2
}
