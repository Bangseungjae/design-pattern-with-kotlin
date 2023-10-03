# 방문자 패턴(Visitor Pattern)

이 패턴을 사용하면 트리 구조의 복잡한 구조에서 데이터를 추출하거나 각 노드에 동작을 추가할 수 있다.

데코레이터 패턴이 객체 하나의 동작을 바꾼다면 방문자 패턴은 자료 구조에 포함된 여러 객체의 동작을 바꾼다.

개발자들에게 최신 기술을 다루는 글의 링크를 모아 매주 이메일을 보내려고 한다. 물론 직접 기술 블로그를 읽지는 않을 것이다. 그는 그냥 유명한 기술 사이트 몇 개에서 링크를 수집하고자 한다.

## 크롤러 작성하기

다음과 같은 자료 구조를 보자. 반복자 디자인 패턴을 배울 때 다뤘던 것과 비슷하다.

```kotlin
val page = Page(
        Container(
            Image,
            Link,
            Image
        ),
        Table,
        Link,
        Container(
            Table,
            Link
        ),
        Container(
            Image,
            Container(
                Image,
                Link
            )
        )
    )
```

Page는 다른 HTML 원소를 담고 있는 역할을 한다(하지만 HtmlElement 자체를 들고 있지는 못한다). Container는 다른 Container나 Table, Link, Image와 같은 원소를 들고 있을 수 있다.

Image는 src속성에 이미지 링크 주소를 들고 있고, Link는 href 속성에 링크 주소를 들고 있다.

괴짜 아키텍트는 이 객체에서 모든 URL을 추출하기 원한다.

먼저 이 객체 트리의 최상위 객체를 입력으로 받고 (이 예제에서는 Page 객체가 된다) 모든 링크 주소의 리스트를 반환하는 함수를 작성할 것이다.

```kotlin
fun collectLinks(page: Page) {
    // 임시 변수 필요 없음
    return LinksCrawler().run { 
        page.accept(this)
        this.links
    }
}
```

run을 사용하면 블록의 반환값을 정할 수 있다. 이 코드에서는 수집한 links 객체를 반환할 것이다. run 블록 내에서 this는 수신 객체(지금은 LinkCrawler)를 가리킨다.

자바에서는 일반적으로 새 기능을 받기 위한 메서드를 클래스마다 구현하는 방식으로 방문자 패턴을 구현한다. 여기서도 비슷한 방법을 사용할 것이다. 다만 모든 클래스에 메서드를 구현하지 않고, 내부에 다른 원소를 담고 있는 Container와 Page 클래스에만 구현할 것이다.

```kotlin
private fun Container.accept(feature: LinksCrawler) {
    feature.visit(this)
}

// 단일식 함수로 다음과 같이 줄여 쓸 수도 있다.
private fun Page.accept(feature: LinksCrawler) = feature.visit(this)
```

이제 구현해야 하는 기능 클래스(LinksCrawler)는 내부적으로 집합 자료 구조를 들고 있어야 하며, 외부에서 읽을 수 있도록 이를 노출해야 한다. 자바라면 해당 멤버에 대해 설정자 함수는 정의하지 않고, 접근자 함수만 정의할 것이다. 코틀린은 다음과 같이 뒷받침 필드 없이 값을 선언할 수 있다.

```kotlin
class LinksCrawler {
    private var _links = mutableListOf<String>()

    val links
        get() = _links.toList()
		//...
}
```

외부에 노출되는 자료 구조는 불변이길 원하기 때문에 toList() 함수를 호출했다.

<br/>

중요

```
트리 형태의 자료 구조에서 반복을 수행할 때는 반복자 패턴을 사용하면 더욱 간단하게 구현할 수 있다.
```

Container 클래스가 입력으로 들어오면 그냥 하위 원소를 계속해서 방문한다.

```kotlin
class LinksCrawler {
		//...
    fun visit(page: Page) {
        visit(page.elements)
    }

    fun visit(container: Container) = visit(container.elements)
		//...
}
```

부모 클래스를 봉인 클래스로 선언하면 컴파일러에게 더욱 도움이 된다.

```kotlin
sealed class HtmlElement

class Container(val elements: MutableList<HtmlElement> = mutableListOf()) : HtmlElement() {

    constructor(vararg units: HtmlElement) : this(mutableListOf()) {
        for (u in units) {
            this.elements.add(u)
        }
    }
}

object Image : HtmlElement() {
    val src: String
        get() = "https://some.image"
}

object Link : HtmlElement() {
    val href: String
        get() = "https://some.link"
}

object Table : HtmlElement()
```

제일 흥미로운 부분은 트리 구조의 잎(말단)에 해당하는 부분을 처리하느 로직이다.

```kotlin
class LinksCrawler {
	//...
	private fun visit(elements: List<HtmlElement>) {
	        for (e in elements) {
	            when (e) {
	                is Container -> e.accept(this)
	                is Link -> _links.add(e.href)
	                is Image -> _links.add(e.src)
	                else -> {
	               }
	            }
	        }
		  }
		//...
}
```

여기도 코틀린의 스마트 캐스팅이 사용된다.

원소가 Link 타입이라는 것을 확인한 다음에는 타입 안전하게 href 속성에 접근할 수 있다는 점을 확인하라. 컴파일러가 캐스팅을 알아서 해주기 때문이다. Image원소도 마찬가지다.

어쨋든 원하는 기능은 잘 구현했지만, 이 패턴이 얼마나 쓸모 있는지는 논란의 대상이다. 예제에서 볼 수 있다시피 코드가 장황해질 뿐만 아니라 기능을 받아들이는 클래스와 방문자 클래스 사이에 강한 결합이 생기기 때문이다.

<br/>
전체 코드

```kotlin
package _3_behavioral_patterns.visitor

fun main() {

    val page = Page(
        Container(
            Image,
            Link,
            Image
        ),
        Table,
        Link,
        Container(
            Table,
            Link
        ),
        Container(
            Image,
            Container(
                Image,
                Link
            )
        )
    )

    val collectLinks = collectLinks(page)
    collectLinks.forEach(::println)
}

class LinksCrawler {
    private var _links = mutableListOf<String>()

    val links
        get() = _links.toList()

    fun visit(page: Page) {
        visit(page.elements)
    }

    fun visit(container: Container) = visit(container.elements)

    private fun visit(elements: List<HtmlElement>) {
        for (e in elements) {
            when (e) {
                is Container -> e.accept(this)
                is Link -> _links.add(e.href)
                is Image -> _links.add(e.src)
                else -> {
                }
            }
        }
    }
}

private fun Container.accept(feature: LinksCrawler) {
    feature.visit(this)
}

// 단일식 함수로 다음과 같이 줄여 쓸 수도 있다.
private fun Page.accept(feature: LinksCrawler) = feature.visit(this)

fun collectLinks(page: Page): List<String> =
    // 임시 변수 필요 없음
    LinksCrawler().run {
        page.accept(this)
        this.links
    }

class Page(val elements: MutableList<HtmlElement> = mutableListOf()) {
    constructor(vararg elements: HtmlElement) : this(mutableListOf()) {
        for (s in elements) {
            this.elements.add(s)
        }
    }

}

sealed class HtmlElement

class Container(val elements: MutableList<HtmlElement> = mutableListOf()) : HtmlElement() {

    constructor(vararg units: HtmlElement) : this(mutableListOf()) {
        for (u in units) {
            this.elements.add(u)
        }
    }
}

object Image : HtmlElement() {
    val src: String
        get() = "https://some.image"
}

object Link : HtmlElement() {
    val href: String
        get() = "https://some.link"
}

object Table : HtmlElement()
```

결과

```text
https://some.image
https://some.link
https://some.image
https://some.link
https://some.link
https://some.image
https://some.image
https://some.link
```
