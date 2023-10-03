# 플라이웨이트 패턴(Flyweight Pattern)

## → 경량 패턴

경량 객체란

- 매우 가벼운 객체라는 뜻으로 상태를 갖지 않는 객체를 말한다.

```kotlin
package ch3.flyweight

import java.io.File

fun main() {
    // Flyweight allows us to create much more objects that otherwise possible
    val snails = List(10_000) {
        TansanianSnail()
    }
}

enum class Direction {
    LEFT,
    RIGHT
}

class TansanianSnail {
    val directionFacing = Direction.LEFT

    // This is the Flyweight we're using
    val sprites = SnailSprites.sprites

    fun getCurrentSprite(): File {
        return when (directionFacing) {
            Direction.LEFT -> sprites[0]
            Direction.RIGHT -> sprites[1]
        }
    }

    // More information about the state of a snail comes here

    // This may include its health, for example
}

object SnailSprites {
    val sprites = List(8) { i ->
        java.io.File(
            when (i) {
                0 -> "snail-left.jpg"
                1 -> "snail-right.jpg"
                in 2..4 -> "snail-move-left-${i - 1}.jpg"
                else -> "snail-move-right${(4 - i)}.jpg"
            }
        )
    }
}
```
