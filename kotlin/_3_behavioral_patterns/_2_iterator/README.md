# 반복자 패턴(Iterator Pattern)

합성 패턴을 배우면서 그 자체로는 다소 불안전하다는 사실을 언급했다. 이제 나머지 반쪽을 찾을 시간이다.

```kotlin
val platoon = Squad(
	Trooper(),
	Squad(
		Trooper(),
	),
	Trooper(),
	Squad(
		Trooper(),
		Trooper(),
	),
	Trooper()
)
```

for-in문을 사용해서 소대에 소속된 모든 트루퍼를  출력할 수 있다면 좋을 것이다.

```kotlin
for (trooper in platoon){
	println(trooper)
}
```

코드는 컴파일되지 않지만 코틀린 컴파일러에서 다음과 같은 유용한 힌트를 얻을 수 있다.

```kotlin
For loop range must have an iterator method
```

For-in 구문에 사용하는 범위는 반복자 메서드를 가져야한다고 친절히 안내하고 있다.

문제는 이렇다.

합성 패턴으로 만든 소대 객체의 구조는 평면적이지 않다. 즉 소대에 포함된 객체가 또 다른 객체를 담고 있을 수 있다. 하지만 이런 경우에도 복잡성을 추상화시키고 소대를 그냥 트루퍼 객체의 리스트처럼 사용하고 싶을 수 있다. 이게 반복자 패턴이 하는 일이다. 즉 복잡한 자료구조를 단순한 리스트 형태로 평면화하는 것이다.

For-in 반복문에서 Squad 객체를 사용하려면 interator()라는 특수 함수를 구현해야 한다. 이 함수는 특수 함수이기 때문에 operator 키워드를 사용해야 한다.

```kotlin
operator fun iterator() = object: Iterator<Trooper> {
        override fun hasNext(): Boolean {
            // 반복할 원소가 남아 있는가?
        }

        override fun next(): Trooper {
            // 다음 트루퍼 객체를 반환한다.
        }
    }
```

1. 먼저 반복자의 상태가 필요하다. 이 상태에는 마지막에 반환한 원소를 저장한다.

```kotlin
operator fun iterator() = object: Iterator<Trooper> {
        private var i = 0

```

2. 다음으로는 언제 반복문을 멈춰야 할지 알려 줘야 한다.

```kotlin
        override fun hasNext(): Boolean {
            return i < units.size
        }
```

3. 마지막으로 어떤 원소를 반환할지 알아야 한다. 간단한 경우에는 그냥 현재 원소를 반환하고 다음 원소로 이동하면 된다.

<br/>

```kotlin
override fun next(): Trooper = units[i++]
```

Squad 같은 경우에는 더 복잡하다. 다음 전체 코드를 참고하라.

```kotlin
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

```
<br/>
결과

```kotlin
StormTrooper(weapon=_2_structural_patterns._3_bridge.Rifle@16b98e56, legs=_2_structural_patterns._3_bridge.RegularLegs@7ef20235)
StormTrooper(weapon=_2_structural_patterns._3_bridge.Rifle@27d6c5e0, legs=_2_structural_patterns._3_bridge.RegularLegs@4f3f5b24)
StormTrooper(weapon=_2_structural_patterns._3_bridge.Rifle@15aeb7ab, legs=_2_structural_patterns._3_bridge.RegularLegs@7b23ec81)
StormTrooper(weapon=_2_structural_patterns._3_bridge.Rifle@6acbcfc0, legs=_2_structural_patterns._3_bridge.RegularLegs@5f184fc6)
```
