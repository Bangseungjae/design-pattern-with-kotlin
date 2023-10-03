package _3_behavioral_patterns._1_strategy

fun main() {
    val hero = OurHero()
    hero.shoot()
    hero.currentWeapon = Weapons::banana
    hero.shoot()
}

enum class Direction {
    LEFT, RIGHT
}

data class Projectile(
    private var x: Int,
    private var y: Int,
    private var direction: Direction,
)

var square = fun(x: Int): Long {
    return (x * x).toLong()
}

class OurHero {
    private var direction = Direction.LEFT
    private var x: Int = 42
    private var y: Int = 173

    var currentWeapon = Weapons::peashooter

    fun shoot(): Projectile = currentWeapon(x, y, direction)
}

interface Weapon {
    fun shoot(
        x: Int,
        y: Int,
        direction: Direction,
    ): Projectile
}

// 직선으로 날아가는 콩알총
class Peashooter : Weapon {
    override fun shoot(
        x: Int,
        y: Int,
        direction: Direction
    ): Projectile = Projectile(x, y, direction)
}

// 화면 끝에 닿으면 되돌아오는 바나나
class Banana : Weapon {
    override fun shoot(x: Int, y: Int, direction: Direction): Projectile = Projectile(x, y, direction)
}
// 비슷한 방법으로 다른 구현체도 추가할 수 있다.


object Weapons {
    // 직선으로 날아가는 콩알총
    fun peashooter(
        x: Int,
        y: Int,
        direction: Direction,
    ): Projectile = Projectile(x, y, direction)

    // 화면 끝에 닿으면 되돌아오는 바나나
    fun banana(
        x: Int,
        y: Int,
        direction: Direction,
    ): Projectile = Projectile(x, y, direction)

    // 비슷한 방법으로 다른 구현체도 추가할 수 있다.
}
