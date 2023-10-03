package _3_behavioral_patterns._10_template_method


fun main() {
    runSchedule(
        afterLunch = fun() {
            println("Discuss my lunch with boss' secretary")
            println("Read something not related to work")
        }, beforeLunch = fun() {
            println("Look for my next trip destination")
            println("Read StackOverflow")
        }, bossHook = { println("Boss: Can we talk privately?") }
    )
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
