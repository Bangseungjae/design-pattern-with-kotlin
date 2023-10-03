package _3_behavioral_patterns._8_memento

fun main() {
    val michael = Manager()
    michael.think("코코넛 대포를 구현해야 해.")
    michael.think("커피를 좀 마셔야겠어.")
    val memento: Manager.Memory = michael.saveThatThought()
    with(michael) {
        think("아니면 차를 마실까?")
        think("아냐, 파인애플 대포를 구현해야 해")
    }
    michael.printThoughts()
    michael.`내가 그때 무슨 생각을 하고 있었지?`(memento)
    michael.printThoughts()
}

class Manager {
    private var thoughts = mutableListOf<String>()

    fun printThoughts() {
        println(thoughts)
    }

    inner class Memory(private val mindState: List<String>) {
        fun restore() {
            thoughts = mindState.toMutableList()
        }
    }

    fun saveThatThought(): Memory {
        return Memory(thoughts.toList())
    }

    fun `내가 그때 무슨 생각을 하고 있었지?`(memory: Memory) {
        memory.restore()
    }

    fun think(thought: String) {
        thoughts.add(thought)
        if (thoughts.size > 2) {
            thoughts.removeFirst()
        }
    }
}
