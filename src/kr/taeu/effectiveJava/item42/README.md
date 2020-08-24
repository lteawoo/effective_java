# 익명 클래스보다는 람다를 사용하라
함수 객체(function object) : 자바에서 함수 타입을 표현할 때 추상 메서드를 하나만 담은 인터페이스(드물게는 추상 클래스), 특정 함수나 동작을 나타내는데 사용한다.

과거(jdk 1.1)에는 익명 클래스로 함수 객체를 만들었다.
```java
// 익명 클래스의 인스턴스를 함수 객체로 사용 - 낡은 기법
Collections.sort(words, new Comparator<String>() {
  public int compare(String s1, String s2) {
    return Integer.compare(s1.length(), s2.length());
  }
});
```
전략 패턴처럼 함수 객체를 사용하는 과거 객체 지향 디자인 패턴에는 익명 클래스면 충분했다. 이 코드에서 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻하며, 문자열을 정렬하는 구체적인 전략을 익명 클래스로 구현했다. 하지만 익명 클래스 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았다.

자바 8에 와서 추상 메서드 하나짜리 인터페이스는 특별한 의미를 인정받았다. 지금은 함수형 인터페이스라 부르는 이 인터페이스들의 인스턴스를 람다식(lambda expression)을 사용해 만들 수 있게 되었다.
```java
// 람다식을 함수 객체로 사용 - 익명 클래스 대체
Collections.sort(words,
      (s1, s2) -> Integers.compare(s1.length(), s2.length()));
```
여기서 람다, 매개변수(s1, s2), 반환값의 타입은 각각 (Comparator\<String>), String, int지만 코드에서는 언급이 없다. 컴파일러가 문맥을 살펴 타입을 추론해준 것이다. 타입 추론 규칙은 너무 방대하며 복잡하다. **타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.** 그런 다음 컴파일러가 "타입을 알 수 없다"는 오류를 낼 때만 해당 타입을 명시하면 된다.

람다 자리에 비교자 생성 메서드를 사용하면 이 코드를 더 간결히 만들 수 있다.
```java
Collections.sort(words, comparingInt(String::length));
```
더 나아가 자바 8때 List 인터페이스에 추가된 sort 메서드를 이용하면 더욱 짧아진다.
```java
words.sort(comparingInt(String::length));
```
아이템 34의 Operation 열거 타입을 예로 들어보자, apply 메서드의 동작이 상수마다 달라야 해서 상수별 클래스 몸체를 사용해 각 상수에서 apply 메서드를 재정의한 것이 생각나는가?
```java
public enum Operation3 {
  PLUS("+") {
    public double apply(double x, double y) {
      return x + y;
    }
  },
  MINUS("-") {
    public double apply(double x, double y) {
      return x - y;
    }
  },
  TIMES("*") {
    public double apply(double x, double y) {
      return x * y;
    }
  },
  DIVIDE("/") {
    public double apply(double x, double y) {
      return x / y;
    }
  };

  private final String symbol;

  Operation3(String symbol) {
    this.symbol = symbol;
  }

  private static final Map<String, Operation3> stringToEnum = Stream.of(values())
      .collect(toMap(Object::toString, e -> e));

  // 지정한 문자열에 해당하는 Operation을 반환한다.
  public static Optional<Operation3> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
  }

  @Override
  public String toString() {
    return symbol;
  }

  public abstract double apply(double x, double y);
}
```
아이템34에서는 상수별 클래스 몸체를 구현하는 방식보다는 열거 타입에 인스턴스 필드를 두는 편이 낫다고 했다. 람다를 이용하면 후자의 방식, 즉 열거 타입의 인스턴스 필드를 이용하는 방식으로 상수별로 다르게 동작하는 코드를 쉽게 구현 할 수 있다.

단순히 각 열거 타입 상수의 동작을 람다로 구현해 생성자에 넘기고, 생성자는 이 람다를 인스턴스 필드로 저장해둔다. 그런 다음 apply 메서드에서 필드에 저장된 람다를 호출하기만 하면 된다.
```java
// 함수 객체(람다)를 인스턴스 필드에 저장해 상수별 동작을 구현한 열거 타입
public enum Operation2 {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation2(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    private static final Map<String, Operation2> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    public static Optional<Operation2> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }

    @Override
    public String toString() {
        return symbol;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```
> DoubleBinaryOperation 인터페이스는 다양한 함수 인터페이스 중 하나로(java.util.function 패키지) Double 타입 2를 받아 Double 타입 결과를 돌려준다.

람다 기반 Operation 열거 타입을 보면 상수별 클래스 몸체는 더 이상 사용할 이유가 없다고 느낄지 모르지만, 꼭 그렇지는 않다. 메서드나 클래스와 달리, **람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.**

람다가 길거나 읽기 어렵다면 더 간단히 줄여보거나 람다를 쓰지 않는 쪽으로 리팩터링 하기 바란다.

열거 타입 생성자에 넘겨지는 인수들의 타입도 컴파일타임에 추론된다. 따라서 열거 타입 생성자 안의 람다는 열거 타입의 인스턴스 멤버에 접근 할 수 없다(인스턴스는 런타임에 만들어지기 때문) 따라서 상수별 동작을 단 몇 줄로 구현하기 어렵거나, 인스턴스 필드나 메서드를 사용해야 한다면 상수별 클래스 몸체를 사용해야 한다.

## 람다가 대체할 수 없는 곳이 있다.
1. 람다는 함수형 인터페이스에서만 쓰인다. 예컨대 추상클래스의 인스턴스를 만들 때 람다를 쓸수 없으니 익명 클래스를 써야한다.
2. 비슷하게 추상 메서드가 여러 개인 인터페이스의 인스턴스를 만들 때도 마찬가지다
3. 람다는 자신을 참조 할 수 없다. 람다에서의 this 키워드는 바깥 인스턴스를 가리킨다. 반면 익명 클래스의 this는 익명 클래스의 인스턴스 자신을 가리킨다.

람다도 익명 클래스처럼 직렬화 형태가 구현별로(가령 가상머신별로) 다를 수 있다. 따라서 람다를 직렬화하는 일은 극히 삼가해야 한다.(익명 클래스도 마찬가지) **직렬화 해야만 하는 함수 객체가 있다면(가령 Comparator) private 정적 충첩 클래스의 인스턴스를 사용하자**

