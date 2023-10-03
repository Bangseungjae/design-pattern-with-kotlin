# 해석기 패턴(Interpreter Pattern)

해석기 패턴은 다른 디자인 패턴에 비해 비교적 복잡한 편에 속한다.

## 도메인 특화 언어

모든 개발자는 여러 개의 언어나 하위 언어를 다룬다. 평범한 개발자라고 해도 2개 이상의 언어를 사용한다. 메이븐(Maven)이나 그래들 같은 프로젝트 빌드 도구를 생각해 보라. 빌드 스크립트는 특정한 문법을 갖는 하나의 언어라고 할 수 있다. 정해진 문법에 맞게 작성해야만 한다.

SQL이나 NoSQL 데이터베이스용 언어와 같은 쿼리 언어도 있다. 안드로이드 개발자는 XML 레이아웃을 일종의 프로그래밍 언어라고 할 수도 있다.

직접 정의한 언어를 사용해서 테스트를 수행하는 테스트 프레임워크도 있다. **큐컴버**가 그중 하나다.

이런 언어를 도메인 특화 언어(DSL, Domain-Specific Language)라고 부른다. DSL은 언어 안의 언어로써 특정 도메인에서 사용하기 위해 만든 언어다.

## 나만의 언어 만들기

이 절에서는 ‘SQL을 위한 도메인 특화 언어’를 간단하게 정의할 것이다. 형식이나 문법은 따로 정의하지 않고 다음의 예시로 대신할 것이다.

```cpp
val sql = select("name, age") {
  from("users") {
    where("age > 25")
  }  // from
} // select
```

이 언어의 목적은 SQL의 가독성을 높이고 흔히 저지르는 실수(오타)를 방지하는 것이다. 따라서 컴파일 시에 유효성도 검사해 주고 자동 완성도 지원할 것이다.

위 코드는 다음과 같은 출력을 낸다.

```cpp
SELECT name, age FROM users WHERE age > 25
```

가장 쉬운 부분인 select 함수부터 구현해 보자.

```kotlin
fun select(columns: String, from: SelectClause.() -> Unit): SelectClause {
    return SelectClause(columns).apply(from)
}
```

단일식 함수 표기법으로도 작성할 수 있지만 여기선 명확성을 위해 일부러 길게 작성했다. 이 함수는 매개변수를 2개 받는다. 첫 번째는 단순한 String 타입이고, 두 번째는 아무것도 받지 않고 아무것도 반환하지 않는 함수다.

가장 흥미로운 부분은 다음과 같이 람다 함수의 수신자(receiver)를 지정했다는 점이다.

```kotlin
SelectClause.()->Unit
```

꽤나 그럴듯한 속임수이므로 집중해서 읽어 보길 바란다. 1장과 2장에서 다뤘던 확장 함수를 기억 속에서 꺼내 보자. 위의 코드는 사실 다음 코드와 같다.

```kotlin
(SelectClause)->Unit
```

인수를 받지 않는 줄 알았던 람다 함수가 사실은 인수 하나를 받고 있었던 것이다. 그 인수는 SelectClause 타입의 객체다.

두 번째로 살펴볼 속임수는 2장에서 봤던 `apply()` 함수에 있다. 다음 코드를 보자.

```kotlin
SelectClause(columns).apply(from)
```

이는 다음 코드와 동일하다.

```kotlin
val selectClause = SelectClause(columns)
from(selectClause)
return selectClause
```

이 코드가 수행하는 일을 자세히 나타내면 다음과 같다.

1. SelectClause 객체를 초기화한다. 이 객체는 생성자에 인수를 1개 받는 간단한 객체다.
2. from() 함수에 SelectClause 인스턴스를 전달해 호출한다.
3. SelectClause 인스턴스를 반환한다.

DSL 예제를 다시 한번 살펴보자.

```kotlin
select("name, age") {
  this@select.from("users", {
    where("age > 25")
  }
}
```

이번에는 함수의 수신자를 명시적으로 나타냈다. 즉 from() 함수는 SelectClause 객체의 from() 함수를 호출할 것이다.

이 from() 함수가 어떻게 생겼을지 추측해 보라. 첫 번쨰 인수로는 String을 받고 두 번째 인수로는 다른 람다 함수를 받는다.

```kotlin
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
```

lateinit 키워드는 굉장히 위험할 수 있기 때문에 사용할 때는 주의를 기울여야 한다.

다시 예제 코드로 돌아가 보자. 이 코드가 하는 일은 다음과 같다.

1. FromClause 인스턴스를 생성한다.
2. FromClause를 SelectClause의 멤버로 저장한다.
3. FromClause 인스턴스를 where 람다 함수로 전달한다.
4. FromCluase 인스턴스를 반환한다.

이제 어느 정도 감이 잡힐 것이다.

```kotlin
select("name, age", {
        this@select.from("suers", {
            this@from.where("age > 25")
        })
    })
```

이 코드는 무슨 뜻일까? from() 메서드를 이해했다면 이 메서드는 식은 죽 먹기다.

FromClause에는 String 타입의 인수 1개를 받는 where() 메서드가 있으리라 추측할 수 있다.

```kotlin
class FromClause(private val table: String) {
    private lateinit var where: WhereClause

    fun where(conditions: String) = this.apply {
        where = WhereClause(conditions)
    }

    override fun toString(): String = "FROM $table $where"
}
```

이번에는 apply 함수를 사용해서 짧은 버전으로 작성해 봤다.

코드는 간단하다. 입력으로 받은 문자열로 WhereClause 객체를 초기화하고 그것을 반환한다.

```kotlin
class WhereClause(private val conditions: String) {
    override fun toString(): String = "WHERE $conditions"
}
```

WhereClause 클래스는 전달받은 조건문을 WHERE 키워드와 출력하는 역할을 한다.

```kotlin
class FromClause(private val table: String) {
// 다른 코드는 생략
    override fun toString(): String = "FROM $table $where"
}
```

FromClause 클래스는 FROM 키워드와 함께 테이블명 및 WhereClause의 출력 결과를 출력한다.

```kotlin
class SelectClause(private val columns: String) {
		// 다른 코드는 생략
    override fun toString(): String = "SELECT $columns $from"
}
```

SelectClause 클래스는 SELECT 키워드, 칼럼명, FromClause가 출력한 내용 전체를 출력한다.

## 접미 호출

```kotlin
select("name, age") {
        this@select.from("suers") {
            this@from.where("age > 25")
        } // from
    } // select
```

select 함수는 인수를 2개 받아야 하는데(문자열과 람다 함수) 람다 함수가 괄호 안쪽에 있지 않고 바깥으로 나온 것을 볼 수 있다.

이러한 용법을 **접미 호출**이라고 하며 굉장히 흔하게 사용하는 문법이다. 어떤 함수의 마지막 인수가 함수라면 이 함수는 괄호 바깥에 둬도 된다.

이렇게 하면 문법이 훨씬 깔끔해진다.

## 전체 코드

```kotlin
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
```

결과

```
SELECT name, age FROM uers WHERE age > 25
```

<br/>

## 정리

해석기 디자인 패턴과 타입 안전한 DSL을 만들 수 있도록 지원하는 코틀린의 기능은 매우 강력해 보인다. 그러나 큰 힘에는 큰 책임이 따르는 법이다. 언어 안에서 새로운 언어를 만들어야 할 만큼 복잡한 일에만 해석기 패턴을 사용하라. 그렇지 않다면 코틀린의 기본 문법만으로도 충분할 것이다.
