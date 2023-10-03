# 기억 패턴(Memento Pattern)

중개인 패턴에서 본 마이클은 너무 바쁘다. 질문이 있어도 마이클을 만나기 어렵다. 겨우 만나서 질문을 하려고 하면 대강 대답하고 회의가 있다면서 달려간다.

어제는 게임에 어떤 무기를 추가하면 좋을지 마이클에게 물었다. 마이클은 아주 분명하게 코코넛 대포를 추가해야 한다고 말했다.  그런데 오늘 코코넛 대포 기능을 구현해서 보여 줬더니 화가 나서 짹짹거리는 것이다. 그런데 오늘 코코넛 대포가 아니라 파인애플 대포를 구현해 오라고 한다.

마이클이 말하는 것을 녹음해 놨다가, 마이클이 너무 집중을 하지 않아서 회의가 산으로 간다 싶을 때 틀어 주면 좋을 것 같다.

문제 상황을 정리해 보자. 먼저 마이클은 고유한 생각을 갖고 있다.

```kotlin
class Manager {
    private var thoughts = mutableListOf<String>()
		...
}
```

마이클은 동시에 두 가지 생각만 할 수 있다는 것이 문제다.

```kotlin
class Manager {
    private var thoughts = mutableListOf<String>()

    fun think(thought: String) {
        thoughts.add(thought)
        if (thoughts.size > 2) {
            thoughts.removeFirst()
        }
    }
}
```

생각이 두 가지를 넘어가면 처음 생각은 잊어버린다.

```kotlin
    michael.think("코코넛 대포를 구현해야 해.")
    michael.think("커피를 좀 마셔야겠어.")
    think("아니면 차를 마실까?") // 코코넛 대포는 잊는다.
    think("아냐, 파인애플 대포를 구현해야 해") // 커피를 마시려던 것을 잊는다.
```

이렇게 기록해 놓고 봐도 마이클이 무슨 말을 하는지 이해하기 힘들다(아무것도 반환하지 않기 때문이다).

게다가 녹음을 하더라도 마이클은 그냥 했던 말일 뿐이지 진짜 그런 의미는 아니었다고 할 수 있다.

기억(memento) 디자인 패턴은 외부에서 변경 불가능하며(그래야 마이클이 발뺌할 수 없을 테니) 객체 내부에서만 사용하는 내부 상태를 저장해 둠으로써 이 문제를 해결한다.

코틀린에서는 내부 클래스를 활용해서 기억 패턴을 구현할 수 있다.

```kotlin
class Manager {
		...
    inner class Memory(private val mindState: List<String>) {
        fun restore() {
            thoughts = mindState.toMutableList()
        }
    }
}
```

이 코드에서는 클래스에 inner라는 새로운 키워드를 붙여서 내부 클래스를 만들었다. 이 키워드를 생략하면 중첩 클래스가 되는데, 자바의 정적 중첩 클래스(static nested class)와 비슷하다. 반면 내부 클래스는 바깥쪽 클래스의 비공개 필드에 접근할 수 있다.

그래서 Memory 클래스는 Manager 클래스의 내부 상태를 쉽게 변경할 수 있다.

```kotlin
fun saveThatThought(): Memory {
        return Memory(thoughts.toList())
    }
```

비로소 마이클의 생각을 객체에 담아 둘 수 있다.

```kotlin
val michael = Manager()
    michael.think("코코넛 대포를 구현해야 해.")
    michael.think("커피를 좀 마셔야겠어.")
    val memento: Manager.Memory = michael.saveThatThought()
    michael.think("아니면 차를 마실까?")
    michael.think("아냐, 파인애플 대포를 구현해야 해.")
```

이제 이전에 했던 생각으로 돌아갈 방법이 필요하다.

```kotlin
class Manager {
    ...

    fun `내가 그때 무슨 생각을 하고 있었지?`(memory: Memory) {
        memory.restore()
    }
		...
}
```

함수 이름에 공백이나 한글 등 특수한 문자도 사용 가능하다는 것을 볼 수 있다. 함수 이름을 백틱으로 감싸기만 하면 된다. 일반적으로 특수 문자가 들어간 함수 이름을 짓는 것은 별로 좋은 생각이 아니다. 하지만 필요한 경우도 있다.

이제 남은 일은 과거에 했던 생각으로 되돌아가기 위해 memento를 사용하는 것이다.

```kotlin
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
```

```
[아니면 차를 마실까?, 아냐, 파인애플 대포를 구현해야 해]
[코코넛 대포를 구현해야 해., 커피를 좀 마셔야겠어.]
```

마지막 줄의 함수 호출은 코코넛 대포를 생각하던 때로 마이클의 생각을 되돌릴 것이다.(하필이면..)

with라는 표준 함수를 사용해서 michael.think()를 반복하는 일을 피했다는 것을 눈여겨보라. 이 함수는 어떤 코드 블록에서 같은 객체를 여러 번 사용해야 할 때 반복을 피하기 위해서 사용할 수 있다.

실무에서 기억 패턴을 볼 일은 많지 않을 것이다. 하지만 이전의 상태를 복원할 필요가 있는 애플리케이션에서는 유용하게 사용할 수 있다.

전체 코드

```kotlin
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
```
