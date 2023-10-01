package _1_creational_patterns.builder

import java.lang.RuntimeException

class MailBuilder {
    private var to: List<String> = listOf()
    private var cc: List<String> = listOf()
    private var title: String = ""
    private var message: String = ""
    private var important: Boolean = false


    class Mail internal constructor(
        val to: List<String>,
        val cc: List<String>?,
        val title: String?,
        val message: String?,
        val important: Boolean,
    )

    fun build(): Mail {
        if (to.isEmpty()) {
            throw RuntimeException("To 속성이 비어 있습니다.")
        }
        return Mail(to, cc, title, message, important)
    }

    fun message(message: String): MailBuilder {
        this.message = message
        return this
    }
    // 각 속성에 대해서 모두 구견해야 함
}
