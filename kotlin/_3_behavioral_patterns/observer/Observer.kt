package _3_behavioral_patterns.observer


fun main() {
    val catTheConductor = Cat()
    val bat = Bat()
    val dog = Dog()
    val turkey = Turkey()

    val lowMessage = LowMessage(1)
    val highMessage = HighMessage(2)

    catTheConductor.joinChoir { bat.screech(lowMessage) }
    catTheConductor.joinChoir { dog.bark(highMessage) }
    catTheConductor.joinChoir { dog.howl(highMessage) }
    catTheConductor.joinChoir { turkey.gobble(lowMessage) }

    catTheConductor.conduct(1)
}

enum class SoundPitch { HIGH, LOW }
interface Message {
    val repeat: Times
    val pitch: SoundPitch
}

data class LowMessage(override val repeat: Times) : Message {
    override val pitch: SoundPitch = SoundPitch.LOW
}

data class HighMessage(override val repeat: Times) : Message {
    override val pitch: SoundPitch = SoundPitch.HIGH
}

class Bat {
    fun screech(message: Message) {
        for (i in 1..message.repeat) {
            when (message) {
                is HighMessage -> {
                    println("${message.pitch} 이----")
                }

                else -> println("낼 수 없는 소리에요 :(")
            }
        }
    }
}

class Turkey {
    fun gobble(message: Message) {
        for (i in 1..message.repeat) {
            println("${message.pitch} 꾸륵꾸륵")
        }
    }
}

class Dog {
    fun bark(message: Message) {
        for (i in 1..message.repeat) {
            println("${message.pitch} Woof")
        }
    }

    fun howl(message: Message) {
        for (i in 1..message.repeat) {
            println("${message.pitch} Auuuu")
        }
    }
}


typealias Times = Int

class Cat {
    private val participants = mutableMapOf<(Int) -> Unit, (Int) -> Unit>()

    fun joinChoir(whatToCall: (Int) -> Unit) {
        participants[whatToCall] = whatToCall
    }

    fun leaveChoir(whatNotToCall: (Int) -> Unit) {
        participants.remove(whatNotToCall)
    }

    fun conduct(n: Times) {
        for (p in participants.values) {
            p(n)
        }
    }
}
