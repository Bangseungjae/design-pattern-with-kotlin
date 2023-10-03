package _3_behavioral_patterns._4_command


fun main() {
    val trooper = Trooper()
    trooper.appendMove(20, 0)
        .appendMove(20, 20)
        .appendMove(5, 20)
        .executeOrders()
}

class Trooper {
    private val orders = mutableListOf<Pair<Command, Command>>()

//    fun addOrder(order: Command) {
//        this.orders.add(order)
//    }

    fun appendMove(x: Int, y: Int) = apply {
        orders.add(moveGenerator(this, x, y) to moveGenerator(this, 0 - x, 0 - y))
    }

    // 외부 코드에서 가끔씩 실행하는 함수
    fun executeOrders() {
        while (orders.isNotEmpty()) {
            val order: Pair<() -> Unit, () -> Unit> = orders.removeFirst()
            order.first()
        }
    }
    fun move(x: Int, y: Int) {
        println("Moving to $x:$y")
    }
}

typealias Command = () -> Unit

val moveGenerator = fun(
    trooper: Trooper,
    x: Int,
    y: Int
): Command {
    return fun() {
        trooper.move(x, y)
    }
}
