# 옵셔널 반환은 신중히 하라
자바 8 전에는 메서드가 특정 조건에서 값을 반환할 수 없을 때의 선택지가 두 가지 있었다.
1. 예외 - 스택 추적 전체를 캡처하므로 비용이 만만치 않다, 진짜 예외적인 상황에서만 사용해야 한다.
2. null을 반환하면 별도의 null처리 코드를 추가해야 한다.

자바 8로 올라가면서 선택지가 생겼다. Optional<T>이다. null이 아닌 T타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.
아무것도 담지 않은 옵셔널은 '비었다'라고 한다. 반대로 어떤 값을 담은 옵셔널은 '비지 않았다'고 한다. 옵셔널은 원소를 최대 1개 가질 수
 있는 '불변' 컬렉션이다. Optional<T>가 Collection<T>를 구현하지는 않았지만, 원칙적으로는 그렇다.

보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할때 T 대신 Optional<T>를 반환하도록 선언하면 된다.
옵셔널을 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 적다.
```java
// 컬렉션에서 최댓값을 구한다(컬렉션이 비었으면 예외를 던진다).
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty)) {
        throw new IllegalArgumentException("빈 컬렉션");
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
위의 코드를 Optional<E>를 반환하는 코드로 변경하면 다음과 같다.
```java
// 컬렉션에서 최댓값을 구해 Optional<E>를 반환
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    if (c.isEmpty)) {
        return Optional.empty();
    }
    E result = null;
    for (E e : c) {
        if (result == null || e.compareTo(result) > 0) {
            result = Objects.requireNonNull(e);
        }
    }
    
    return Optional.of(result);
}
```
옵셔널을 반환하도록 구현하는 건 어렵지 않다. 적절한 정적 팩터리를 사용해 반환하면 된다. 위의 코드에서는 2가지 팩터리를 사용했다.
1. 빈옵셔널 - Optional.empty()
2. 값이 든 옵셔널 - Optional.of(value) 여기에 null을 넣으면 NPE 예외가 발생하니 주의

스트림의 종단 연산 중 상당수가 옵셔널을 반환한다. 위의 코드를 스트림 버전으로 작성하면 다음과 같다.
```java
// 컬렉션에서 최댓값을 구해 Optional<E>를 반환
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    return c.stream().max(Comparator.naturalOrder());
}
```
그렇다면 null을 반환하거나 예외를 던지는 대신 옵셔널 반환을 선택해야하는 기준은 무엇인가? **옵셔널은 검사 예외와 취지가 비슷하다**

즉, 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다. 비검사 예외를 던지거나 null을 반환한다면 API 사용자가 그 사실을 인지하지 못해 결과가 끔찍할수있다.

하지만 검사예외를 던지면 클라이언트에서는 반드시 이에 대처하는 코드를 작성해야한다.
```java
// 옵셔널 활용1 - 기본값을 정해둘 수 있다.
String lastWordInLexicon = max(words).orElse("단어 없음...");
```
또는 상황에 맞게 예외를 던질 수 있다. 실제 예외가 아니라 예외 팩터리를 건넨 것에 주목
```java
Toy toy = max(toys).orElseThrow(TemperTanTrumException::new);
```
옵셔널에 값이 항상 채워져 있다고 확신한다면 그냥 곧바로 값을 꺼내 사용하는 선택지도 있다. 하지만 판단이 잘못됐다면 NPE가 발생한다.
```java
Element lastNobleGas = max(Elements.NOBLE_GASES).get();
```
