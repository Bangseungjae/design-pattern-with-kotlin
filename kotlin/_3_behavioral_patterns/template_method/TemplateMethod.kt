package _3_behavioral_patterns.template_method


fun main() {

}

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
}
