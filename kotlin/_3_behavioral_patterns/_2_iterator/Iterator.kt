package _3_behavioral_patterns._2_iterator

import _2_structural_patterns._3_bridge.RegularLegs
import _2_structural_patterns._3_bridge.Rifle
import _2_structural_patterns._3_bridge.StormTrooper
import _2_structural_patterns._3_bridge.Trooper


fun main() {
    val platoon = Squad(
        Squad(
            StormTrooper(Rifle(), RegularLegs()),
        ),
        Squad(
            StormTrooper(Rifle(), RegularLegs()),
        ),
        Squad(
            Squad(
                StormTrooper(Rifle(), RegularLegs()),
            ),
            Squad(
                StormTrooper(Rifle(), RegularLegs()),
            ),
        ),
    )

    // For loop range must have an iterator method
    for (trooper in platoon) {
        println(trooper)
    }
}


class TrooperIterator(private val units: List<Trooper>) : Iterator<Trooper> {
    private var i = 0
    private var iterator: Iterator<Trooper> = this

    override fun hasNext(): Boolean {
        if (i >= units.size) {
            return false
        }
        if (i == units.size - 1) {
            if (iterator != this) {
                return iterator.hasNext()
            }
        }
        return true
    }

    override fun next(): Trooper {
        if (iterator != this) {
            if (iterator.hasNext()) {
                return iterator.next()
            } else {
                i++
                iterator = this
            }
        }
        return when (val e = units[i]) {
            is Squad -> {
                iterator = e.iterator()
                this.next()
            }
            else -> {
                i++
                e
            }
        }

    }
}


class Squad(private val units: List<Trooper>) : Trooper {
    constructor(vararg units: Trooper) : this(units.toList())
    override fun move(x: Long, y: Long) {
        for (u in units) {
            u.move(x, y)
        }
    }
    override fun attackRebel(x: Long, y: Long) {
        for (u in units) {
            u.attackRebel(x, y)
        }
    }

    operator fun iterator(): Iterator<Trooper> = TrooperIterator(units)
}
