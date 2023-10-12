package study.io._3_design_for_concurrency._10_sidekick_channel

import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select

fun main() {
    runBlocking {
        val batman = actor<String> {
            for (c in channel) {
                println("베트맨이 ${c}을(를) 처리하고 있습니다.")
                delay(100)
            }
        }

        val robin = actor<String> {
            for (c in channel) {
                println("로빈이 ${c}을(를) 처리하고 있습니다.")
                delay(250)
            }
        }

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
    }
}
