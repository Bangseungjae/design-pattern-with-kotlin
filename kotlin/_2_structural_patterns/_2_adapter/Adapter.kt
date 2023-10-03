package _2_structural_patterns._2_adapter


fun main() {
    cellPhone(
        charger(
            krPowerOutlet().toPlugTypeA()
        ).toUsbTypeC()
    )
}

fun cellPhone(chargeCable: UsbTypeC) {
    if (chargeCable.hasPower) {
        println("충전 중입니다!")
    } else {
        println("전원이 연결되지 않습니다.")
    }
}

fun PlugTypeF.toPlugTypeA(): PlugTypeA {
    val hasPower = if (this.hasPower == 1) "TRUE" else "FALSE"
    return object : PlugTypeA {
        // 전원 연결
        override val hasPower = hasPower
    }
}

fun UsbMini.toUsbTypeC(): UsbTypeC {
    val hasPower = this.hasPower == Power.TRUE
    return object : UsbTypeC {
        override val hasPower = hasPower
    }
}


// 전원 콘센트는 PlugTypeF 인터페이스 노출
fun krPowerOutlet(): PlugTypeF {
    return object : PlugTypeF {
        override val hasPower = 1
    }
}

// 충전기는 plugTypeA 인터페이스를 입력받고 UsbMini 인터페이스를 노출
fun charger(plug: PlugTypeA): UsbMini {
    return object : UsbMini {
        override val hasPower: Power = Power.valueOf(plug.hasPower)
    }
}

interface PlugTypeF {
    val hasPower: Int
}

interface PlugTypeA {
    val hasPower: String // "TRUE" 또는 "FALSE"
}

interface UsbMini {
    val hasPower: Power
}

enum class Power {
    TRUE, FALSE
}

interface UsbTypeC {
    val hasPower: Boolean
}


