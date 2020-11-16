# null이 아닌 빈 컬렉션이나 배열을 반환하라
```java
// 컬렉션이 비었으면 null을 반환한다. - 하지말자
private final List<Cheese> cheesesInStock = ...;

/**
 * @return 매장 안의 모든 치즈목록을 반환한다. 단 재고가 하나도 없으면 null을 반환한다.
 **/
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? null : new ArrayList<>(chessesInStock);
}
```
이 코드처럼 null을 반환한다면, 클라이언트는 이 null 상황을 처리하는 코드를 추가로 작성해야 한다.
```java
List<Cheese> cheeses = shop.getCheeses();
if (chesses != null && chesses.contains(Cheese.STILTON)) { 
    System.out.println("치즈닷");
}
```
컬렉션이나 배열 같은 컨테이너가 비었을 때 null을 반환하는 메서드를 사용할 때면 항시 이와 같은 방어 코드를 넣어줘야 한다. 클라이언트에서 방어 코드를 빼먹으면 오류가 발생할 수 있다.

실제로 객체가 0개일 가능성이 거의 없는 상황에서는 수년 뒤에야 오류가 발생하곤한다. 한편 null을 반환하려면 반환하는 쪽에서도 이 상황을 특별히 취급해줘야 해서 코드가 더 복잡해진다.

때로는 빈 컨테이너를 할당하는 데도 비용이 드니 null을 반환하는 쪽이 낫다는 주장도 있다. 하지만 이는 두 가지 면에서 틀린 주장이다.

1. 분석 결과 이 할당이 성능 저하의 주범이락 확인되지 않는 한 이 정도의 성능 차이는 신경 쓸 수준이 못된다.
2. 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.
```java
// 빈 컬렉션을 반환하는 올바른 예
public List<Cheese> getCheeses() {
    return new ArrayList<>(cheesesInStock);
}
```
가능성은 작지만, 사용 패턴에 따라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어뜨릴 수도 있다. 다행히 해법은 간단하다. 매번 똑같은 빈 '불변' 컬렉션을 반환하자. 알다시피 불변 객체는 자유롭게 공유해도 안전하다.

Colletions.emptyList메서드가 그러한 예다. 집합이 필요하면 Collections.emptySet, 맵은 Collections.emptyMap을 사용하면 된다. 단, 이 역시 최적화에 해당하니 꼭 필요할 때만 사용하자. 최적화가 필요하다고 판단되면 전 후를 측정하여 더 나은 쪽을 사용하자
```java
// 최적화 - 빈컬렉션을 매번 새로 할당하지 않도록 했다.
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheesesInStock);
}
```
배열을 쓸때도 마찬가지다. 절대 null을 반환하지 말고 길이가 0인 배열을 반환하자. 보통은 단순히 정확한 길이의 배열을 반환하기만 하면 된다. 그 길이가 0일 수도 있을 뿐이다.
```java
// 길이가 0일 수도 있는 배열을 반환하는 올바른 방법
public Cheese[] getCheeses() {
    return cheesesInStock.toArray(new Cheese[0]);
}
```
이 방식이 성능을 떨어뜨릴 것 같다면 길이 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다. 길이 0인 배열은 모두 불변이기 때문이다.
```java
// 최적화 - 빈 배열을 매번 새로 할당하지 않도록 했다.
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```
이 최적화 버전의 getCheeses는 항상 EMPTY_CHEESE_ARRAY를 인수로 넘겨 toArray를 호출한다. 

> \<T> T[] List.toArray(T[] a) 메서드는 주어진 배열 a가 충분히 크면 a안에 원소를 담아 반환하고, 그렇지 않으면 T[] 타입 배열을 새로 만들어 그 안에 원소를 담아 반환한다. 따라서 위의 코드의 경우 원소가 하나라도 있다면 Cheese[] 타입의 배열을 새로 생성해 반환하고, 원소가 0개면 EMPTY_CHEESE_ARRAY를 반환한다.

따라서 cheesesInStock이 비엇을 때면 언제나 EMPTY_CHEESE_ARRAY를 반환하게 된다. 단순히 성능을 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는 건 추천하지 않는다.
```java
// 배열을 미리 할당하면 성능이 나빠진다.
return cheesesInStock.toArray(new Cheese[cheesesInStock.size()]);
```