# 생성패턴(Creational Patterns) 이란?
생성패턴은 객체의 생성에 관련된 패턴으로 객체의 생성절차를 추상화하는 패턴이다.
객체를 생성-합성하는 방법 / 객체 표현방법과 시스템을 분리한다.

## 생성패턴 특징
생성패턴은 시스템이 어떤 구체 클래스(구체적인 클래스)를 사용하는지에 대한 정보를 캡슐화한다.

생성패턴은 이들 클래스의 인스턴스들이 어떻게 만들고 어떻게 서로 맞붙는지에 대한 부분을 완전히 가린다.

즉, 객체의 생성과 조합을 캡슐화해 특정 객체가 생성되거나 변경되어도 프로그램 구조에 영향을 크게 받지 않도록 유연성을 제공한다.

## 생성패턴 종류
생성패턴에는 아래와 같은 디자인 패턴이 존재한다.

추상 팩토리 패턴(Abstract Factory Pattern)
: 동일한 주제의 다른 팩토리를 묶어 준다.

빌더 패턴(Builder Pattern)
: 생성(construction)과 표기(representation)를 분리해 복잡한 객체를 생성한다.

팩토리 메서드 패턴(Factory Method Pattern)
: 생성할 객체의 클래스를 국한하지 않고 객체를 생성한다.

프로토타입 패턴(Prototype Pattern)
: 기존 객체를 복제함으로써 객체를 생성한다.

싱글턴 패턴(Singleton pattern)
: 한 클래스에 한 객체만 존재하도록 제한한다.