# 사이드킥 채널 패턴(Sidekick Channel Pattern)

**사이드킥 채널** 디자인 패턴을 사용하면 일꾼의 작업 일부를 조수 일꾼에게 넘길 수 있다.

지금까지 select를 이용해서 값을 ‘받기만’ 했다. 하지만 select를 이용해서 값을 다른 채널로 ‘보내는’ 것도 가능하다. 다음 예제를 보자.

```kotlin
runBlocking {
    val batman = actor<String> {
        for (c in channel) {
            println("베트맨이 ${c}을(를) 처리하고 있습니다.")
            delay(100)
        }
    }
}
```

다음으로는 배트맨보다는 조금 느린 robin이라는 행위자 코루틴을 만들 것이다. 이 코루틴은 초당 4개의 메시지를 처리한다.

```kotlin
val robin = actor<String> {
    for (c in channel) {
        println("로빈이 ${c}을(를) 처리하고 있습니다.")
        delay(250)
    }
}
```

이제 슈퍼히어로가 그의 조수를 2개의 행위자 코루틴으로 구현했다. 당연히 슈퍼히어로가 더 경험이 많기 때문에 보통 사이드킥보다 짧은 시간 안에 빌런(villain)을 처리할 수 있다.

하지만 슈퍼히어로 혼자선 빌런을 상대하기에 손이 모자랄 때도 있다. 그럴 때 그의 조수가 등장한다. 5명의 빌런을 약간의 시간차를 둬서 배트맨과 로빈 앞에 보내고 어떤 일이 일어나는지 보자.

```kotlin
val epicFight = launch {
    for (villain in listOf("조커", "베인", "펭귄", "리들러", "킬러 크록")) {
        val result = select<Pair<String, String>> {
            batman.onSend(villain) {
                "배트맨" to villain
            }
                robin.onSend(villain) {
                    "로빈" to villain
                }
            }
            delay(90)
            println(result)
        }
    }
```

여기서 select의 타입 매개변수는 채널로 ‘보내는 값’이 아니라 블록의 ‘반환값’을 가리킨다. 그래서 Pair<String, String>을 썻다.

코드의 출력은 다음과 같다.

```
베트맨이 조커을(를) 처리하고 있습니다.
(배트맨, 조커)
로빈이 베인을(를) 처리하고 있습니다.
(로빈, 베인)
베트맨이 펭귄을(를) 처리하고 있습니다.
(배트맨, 펭귄)
베트맨이 리들러을(를) 처리하고 있습니다.
(배트맨, 리들러)
로빈이 킬러 크록을(를) 처리하고 있습니다.
(로빈, 킬러 크록)
```

사이드킥 채널을 사용하면 기본 처리 로직을 사용할 수 잆을 때 대체 로직을 사용하도록 할 수 있다. 지속적인 스트림을 처리해야 하는데 처리기의 규모를 쉽게 늘릴 수 없을 때 사이드킥 채널을 사용하는 것을 고려하자.
