제네릭 메서드의 작성법은 제네릭 타입 작성법과 비슷하다. 아래는 두 집합(Set)의 합 집합을 반환하는 문제가 있는 메서드다.
```java
public static Set union(Set s1, Set s2) {
	Set result = new HashSet(s1); // Type safety : unchecked call... 경고
	result.addAll(s2); // Type Safety..
	return result;
}
```

컴파일은 되지만 경고가 발생한다. 메서드를 타입 안전하게 만들어야 경고가 사라진다. 메서드 선언에서의 세 집합(입력 2개, 반환 1개)의 원소 타입을 타입 매개변수로 명시하고, 메서드 안에서도 이 타입 매개변수만 사용하게 수정하면 된다.**타입 매개변수의 목록은 메서드의 제한자와 반환 타입에서 온다.** 아래의 코드는 타입 매개변수 목록은 < E>이고 반환 타입은 Set< E>이다.
```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
	Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```
이 메서드는 경고도 없고, 타입 안전하며 쓰기도 좋다. 이 메서드를 사용한 예제는 다음과 같다.
```java
public static void main(String[] args) {
    Set<String> guys = Set.of("톰", "딕", "해리");
    Set<String> stooges = Set.of("래리", "모에", "컬리");
    Set<String> aflCio = union(guys, stooges);
    System.out.println(aflCio);
}
```
union 메서드는 집합 3개(입력 2개, 반환 1개)의 타입이 모두 같아야 한다. 이를 한정적 와일드카드 타입을 이용하여 더 유연하게 개선할 수 있다.

불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있다. 제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화할 수 있다. 하지만 이렇게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다. 이 패턴을 제네릭 싱글턴 팩터리라 하며, Collections.reverseOrder 같은 함수 객체나 Coollections.emptySet 같은 컬렉션용으로 사용한다.

항등함수(identify funciton: 입력 값 수정 없이 그대로 반환하는 함수)를 담은 클래스를 만들어보자.(자바라이브러리의 Function.identity가 동일한 기능을 한다) 항등함수 객체는 상태가 없으니 요청할 때마다 새로 생성하는 것은 낭비다. 자바의 제네릭이 실체화되면다면 항등함수를 타입별로 하나씩 만들어야 했겠지만, 소거 방식을 사용한 덕에 제네릭 싱글턴 하나면 충분하다.
```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}
```
IDENTITY_FN을 UnaryOperator< T>으로 형변환하려면 비검사 형변환 경고가 발생한다. T가 어떤 타입이든 UnaryOperator< Object>는 UnaryOperator< T>가 아니기 때문이다. 하지만 항등함수의 특성상 입력값 그대로 반환하기 때문에 T가 어떤 타입이든 UnaryOperator< T>를 사용해도 무방하다.
```java
public static void main(String[] args) {
    String[] strings = { "삼베", "대마", "나일론" },
    UnaryOperator<String> sameString = identityFunction();
    for (Number n : numbers) {
        System.out.println(sameString.apply(s));
    }

    Number[] numbers = { 1, 2.0, 3L };
    UnaryOperator<Number> sameNumber = identityFunction();
    for (Number n : numbers) {
        System.out.println(sameNumber.apply(n));
    }
}
```
위의 코드는 제네릭 싱글턴 코드을 실제로 사용하는 모습이다 형변환을 하지 않아도 별다른 오류가 발생하지 않는다.

자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다. 재귀적 타입 한정(recursive type bound)라는 개념이다. 예로 Comparable 인터페이스와 함께 쓰인다.
```java
public interface Comparable<T> {
    int compareTo(T o);
}
```
여기서 타입 매개변수 T는 Comparable< T>를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다. 실제로 거의 모든 타입은 자신과 같은 타입의 원소와만 비교할 수 있다.

Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소를 정렬 혹은 검색하거나, 최솟값 등을 구하는 식으로 사용된다. 이 기능을 수행하려면 컬렉션에 담긴 모든 원소가 상호 비교될 수 있어야 한다.
```java
public static <E extends Comparable<E>> E max(Collection<E> c);
```
재귀적 타입 한정(E extends Comparable< E>>) 이용해 상호 비교할 수 있음을 표현했다. 타입 한정은 모든 타입 E는 자신과 비교할 수 있다 라고 읽을 수 있다.(Collection의 E타입의 원소들은 자기자신과 비교할 수 있다. 예로 List 모든 요소들이 비교 가능하다는것을 표현하는것. E가 자신을 포함한 수식 Comparable< E>에 의해 한정 (extends) 되는것.)
```java
 public static <E extends Comparable<E>> E max(Collection<E> c) {
     if (c.isEmpty()) {
         throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
     }

     E result = null;
     for (E e : c) {
         if (result == null || e.compareTo(result) > 0) {
             result = Objects.requireNonNull(e);
         }
     }

     return result;
 }
```