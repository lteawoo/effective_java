# equals는 일반 규약을 지켜 재정의하라
equals 메서드는 재정의하기 쉬워 보이지만 잘못하면 끔찍한 결과를 초래한다. 문제를 회피하는 가장 쉬운 길은 아예 재정의하지 않는 것이다. 그냥 두면 그 클래스의 인스턴스는 오직 자기 자신과만 같게 된다. 그러니 다음에서 열거한 상황 중 하나에 해당한다면 재정의하지 않는 것이 최선이다.
* 각 인스턴스가 본질적으로 고유하다 -> 값을 표현하는 것이 아닌 동작하는 개체를 표현하는 클래스가 여기에 해당한다.(ex: Thread 등)
* 인스턴스의 '논리적 동치성(logical equality)'을 검사할 일이 없다 -> java.util.regex.Pattern의 equals를 재정의해서 두 Pattern의 인스턴스가 같은 정규표현식을 나타내는지를 검사하는 논리적 동치성을 검사하는 방법도 있다.
* 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다 -> 대부분의 Set 구현체는 AbstractSet이 구현한 equals를 상속받아 쓰고, List 구현체들은 AbstractList로부터, Map 구현체들은 AbstractMap으로 부터부터 상속받아 그대로 사용한다.
* 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다 -> 위험을 철저히 회피하고 싶고, equals가 실수로라도 호출되는 걸 막고 싶으면 다음과 같이 막자.
```java
    @Override public boolean equals(Object o) {
        throw new AssertionError(); // 호출 금지!
    }
```
그렇다면 재정의 해야할 때는 언제일까? 객체 식별성(두 객체가 물리적으로 같은가)이 아니라 논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의하지 않았을 때다. 주로 값 클래스가 해당된다.(값 클래스 : Integer, String 같은 값을 표현하는 클래스)

하지만 값 클래스라 해도 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장 하는 인스턴스 통제 클래스(정적 팩터리 메서드, 싱글톤 등으로 통제되는 인스턴스)라면 equals를 재정의하지 않아도 된다. Enum도 여기에 해당한다. 이런 클래스들은 어짜피 논리적으로 같은 인스턴스가 2개 이상 만들어지지 않으니 논리적 동치성, 객체 식별성이 사실상 똑같은 의미가 된다. 따라서 Object의 equals가 논리적 동치성까지 확인해준다고 볼 수 있다.

equals 메서드를 재정의할 땐 다음과 같은 규약을 꼭 지켜야한다.(Object 명세에 적혀있다.)
* equals 메서드는 동치관계(equivalence relation)를 구현하며, 다음을 만족한다.
* 반사성(reflexivity): null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다.
* 대칭성(symmetry): null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true다.
* 추이성(transitivity): null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true다.
* 일관성(consistency): null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
* null-아님: null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.