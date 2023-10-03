package _3_behavioral_patterns._6_interpreter

fun main() {
    val sql = select("name, age") {
        this@select.from("uers") {
            this@from.where("age > 25")
        }
    }
    println(sql)
}

fun select(columns: String, from: SelectClause.() -> Unit): SelectClause {
    return SelectClause(columns).apply(from)
}

class SelectClause(private val columns: String) {
    private lateinit var from: FromClause
    fun from(
        table: String,
        where: FromClause.() -> Unit
    ): FromClause {
        this.from = FromClause(table)
        return this.from.apply(where)
    }
    override fun toString(): String = "SELECT $columns $from"
}

class FromClause(private val table: String) {
    private lateinit var where: WhereClause
    fun where(conditions: String) = this.apply {
        where = WhereClause(conditions)
    }

    override fun toString(): String = "FROM $table $where"
}

class WhereClause(private val conditions: String) {
    override fun toString(): String = "WHERE $conditions"
}
