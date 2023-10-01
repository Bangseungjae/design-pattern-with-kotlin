package _1_creational_patterns.builder

data class Mail_V1(
    val to: List<String>,
    val cc: List<String>?,
    val title: String?,
    val message: String?,
    val important: Boolean,
)


