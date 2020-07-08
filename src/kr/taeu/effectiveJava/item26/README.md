# 로 타입은 사용하지 말라
## 클래스와 인터페이스 선언에 타입 매개변수가 쓰이면, 이를 제네릭 클래스 혹은 제네릭 인터페이스라 한다.
예컨대 List 인터페이스는 원소의 타입을 나타내는 타입 매개변수 E를 받는다. 그래서 이 인터페이스의 완전한 이름은 List<E>지만 짧게 그냥 List라고도 자주 쓴다. 제네릭 클래스와 제네릭 인터페이스를 통틀어 제네릭 타입이라 한다.

## 각각의 제네릭 타입은 일련의 매개변수화 타입(parameterized type)을 정의한다.
먼저 클래스(혹은 인터페이스) 이름이 나오고, 이어서 꺾쇠괄호 안에 실제 타입 매개변수들을 나열한다. 예컨대 List<String>은 원소의 타입이 String인 리스트를 뜻하는 매개변수화 타입이다. 여기서 String이 정규(formal)타입 매개변수 E에 해당하는 실제(actual) 타입 매개변수다.

## 제네릭 타입을 하나 정의하면 그에 딸린 로 타입(raw type)도 함께 정의된다.
로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다. 예컨대 List<E>의 로 타입은 List다. 로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데, 제네릭이 도래하기 전 코드와 호환되도록 하기 위한 궁여지책이라 할 수 있다.

제네릭을 지원하기 전에는 컬렉션을 다음 과 같이 선언했다. 자바 9에서도 여전히 동작하지만 좋은 예라고 볼 순 없다.
```java
/*
 * 컬렉션의 로타입 - 따라하지 말것
 */
// Stamp 인스턴스만 취급한다.
private final Collection stamps = ...;

// 실수로 동전을 넣는다.
stamps.add(new Coin(...)); // "unchecked call" 경고를 날린다.
```
위 코드를 사용하면 실수로 도장(Stamp) 대신 Coin을 넣어도 아무 오류없이 컴파일되고 실행된다. 컬렉션에서 동전을 다시 꺼내기 전에는 오류를 알아채지 못한다.
```java
/*
 * 반복자의 로 타입 - 따라하지 말것
 */
for (Iterator i = stamps.iterator(); i.hasNext();) {
    Stamp stamp = (Stamp) i.next(); // ClassCastException을 던진다.
    stamp.cancel();
}
```
오류는 가능한 한 발생 즉시, 이상적으로는 컴파일할 때 발견하는 것이 좋다. 이 예에서는 오류가 발생하고 한 참 뒤인 런타임에야 알아챌 수 있는데, 이렇게 되면 런타임에 문제를 겪는 코드와 원인을 제공한 코드가 물리적으로 상당히 떨어져 있을 가능성이 커진다.

제네릭을 활용하면 이 정보가 주석이 아닌 타입 선언 자체에 녹아든다.
```java
/*
 * 매개변수화된 컬렉션 타입 - 타입 안정성 확보
 */
private final Collection<Stamp> stamps = ...;
```
stamps에는 Stamp의 인스턴스만 넣어야 함을 컴파일러가 인지하게 된다. 따라서 아무런 경고가 없다면 의도대로 동작할 것을 보증한다. 컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다. Stamp용 컬렉션에 Coin을 넣는다는 예가 억지스러워 보이겠지만. 현업에서도 종종 일어나는 일이다. 예컨대 BigDecimal용 컬렉션에 BigInteger를 넣는 실수는 그리 억지같지 않을 것이다.

## 로 타입을 쓰면 제네릭이 안겨주는 안정성과 표현력을 모두 잃게 된다.
List 같은 로 타입은 사용해서는 안 되나, List<Object>처럼 임의 객체를 허용하는 매개변수화 타입은 괜찮다. 로 타입인 List와 매개변수화 타입인 List<Object>의 차이는? List는 완전히 제네릭에서 발을 뺀 것이고, List<Object>는 모든 타입을 허용한다는 의사를 컴파일러에 명확히 전달한 것이다. 매개변수로 List를 받는 메서드에 List<String>을 넘길 수 있지만, List<Object>를 받는 메서드에는 넘길 수 없다. 이는 제네릭의 하위 타입 규칙 때문이다. 즉, List<String>은 로 타입인 List의 하위 타입이지만, List<Object>의 하위 타입은 아니다. 그 결과, List<Object> 같은 매개변수화 타입을 사용할 때와 달리 List 같은 로 타입을 사용하면 타입 안정성을 잃게 된다.
```java
/*
 * 런타임에 실패한다. - unsafeAdd 메서드가 로 타입(List)를 사용
 */
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
}

private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```
위 코드는 컴파일은 되지만, Type safety: The method add(Object) belongs to the raw type List. References to generic type List<E> should be parameterized 와 같은 경고가 표시된다. 그리고 실행 시 strings.get(0)의 결과를 형변환 하려 할 때 ClassCastException을 던진다. Integer를 String으로 변환하려 시도한 것이다.

로 타입인 List를 매개변수화 타입인 List<Object>로 바꾼 다음 다시 컴파일해보자. 이번에는 컴파일할 때 오류가 발생한다.

이 쯤되면 원소의 타입을 몰라도 되는 로 타입을 쓰고 싶어질 수 있다. 2개의 집합(Set)을 받아 공통 원소를 반환하는 메서드를 작성한다고 해보자.
```java
static int numElementsInCommon(Set s1, Set s2) {
    int result = 0;
    for (Object o1 : s1) {
        if (s2.contains(o1)) {
            result++;
        }
    }
    return result;
}
```
이 메서드는 동작은 하지만 로 타입을 사용해 안전하지 않다. 따라서 비한정적 와일드카드 타입(unbounded wildcard type)을 사용하는 게 좋다. 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표(?)를 사용하자. 예컨대 제네릭 타입인 Set<E>의 비한정적 와일드 타입은 Set<?>이다. 이것이 어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 Set 타입이다. 다음은 비한정적 와일드카드 타입을 사용해 numElementsInCommon을 다시 선언한 모습이다.
```java
static int numElementsInCommon(Set<?> s1, Set<?> s2) {
    ...
}
```
비한정적 와일드카드 타입인 Set<?>와 로 타입인 Set의 차이는 무엇일까? 물음표가 무언가 멋진 일을 해주는 걸까? 특징을 간단히 말하자면 와일드 카드 타입은 안전하고, 로 타입은 안전하지 않다. 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다. 반면, Collection<?>에는 (null 외에는) 어떤 원소도 넣을 수 없다. 다른 원소를 넣으려 하면 컴파일할때 에러가 난다.(incompatible types..)

로 타입을 쓰지 말라는 규칙에도 소소한 예외가 몇가지 있다. class 리터럴에는 로 타입을 써야 한다. 자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다(배열과 기본 타입은 허용한다.) List.class, String[].class, int.class는 허용하고 List<String>.class와 List<?>.class는 허용하지 않는다.

두 번째 예외는 instanceof 연산자와 관련이 있다. 런타임에는 제네릭 타입정보가 지워지므로 instanceof 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다. 그리고 로 타입이든 비한정적 와잍드카드 타입이든 instanceof는 완전히 똑같이 동작한다. 비한정적 와일드카드 타입의 꺾쇠괄호와 물음표는 아무런 역할 없이 코드만 지저분하게 만드므로, 차라리 로 타입을 쓰는 편이 깔끔하다. 다음은 제네릭 타입에 instanceof를 사용하는 올바른 예다.
```java
if (o instanceof Set) { // 로 타입
    Set<?> s = (Set<?>) o;  // 와일드카드 타입
}
```
**o 타입이 Set임을 확인한 다음 와일드카드 타입인 Set<?>로 형변환해야 한다.(로 타입인 Set이 아니다.) 이는 검사 형변환(Checked cast)이므로 경고가 뜨지 않는다.**

