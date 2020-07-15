# 한정적 와일드카드를 사용해 API 유연성을 높이라
매개변수화 타입은 불공변(invariant)이다. 즉, 서로 다른 타입 type1과 type2가 있을 때 List&lt;Type1&gt;은 List&lt;Type2&gt;의 하위 타입도 상위 타입도 아니다. List&lt;String&gt;은 List&lt;Object&gt;의 하위 타입이 아니라는 뜻인데, List&lt;Object&gt;에는 어떤 객체든 넣을 수 있지만,  List&lt;String&gt;에는 문자열만 넣을 수 있다. 즉, List&lt;String&gt;은 List&lt;Object&gt;가 하는 일을 제대로 수행하지 못하니 하위 타입이 될수 없다.(리스코프 치환 원칙 위배)

불공변 방식보다 유연한 무언가 필요 할때가 있다.
```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
}
```
여기에 일련의 원소를 스택에 넣는 메서드를 추가해야 한다고 해보자.
```java
public void pushAll(Iterable<E> src) {
    for (E e : src) {
        push(e);
    }
}
```
깨끗히 컴파일 되지만 완벽하지 않다. Iterable src의 원소 타입이 스택의 원소 타입과 일치하면 잘 작동한다. 하지만 Stack&lt;Number&gt;로 선언하고 Iterable&lt;Integer&gt;타입으로 pushAll(intVal)을 호출하면 어떻게 될까?
```java
Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers = ...;
numberStack.pushAll(integers);
```
Integer는 Number의 하위 타입이니 잘 작동한다. 논리적으로도 잘 동작해야할 것같다. 하지만 실제로는 매개변수화 타입은 불공변이기 때문에 `Iterable<Integer> cannot be converted to Iterable<Number>`에러가 난다.

자바는 이런 상황에 대처할 수 있는 한정적 와일드카드타입이라는 특별한 매개변수화 타입을 지원한다. pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 하며, 와일드 카드 타입 Iterable&lt;? extends E&gt;가 정확히 이런 뜻이다.
```java
public void pushAll(Iterable<? extends E> src) {
    for (E e : src) {
        push(e);
    }
}
```
Stack 클래스는 물론 이 코드를 사용하는 클라이언트도 깨끗히 컴파일되어 타입 안전하다는 걸 알 수 있다.

이제 pushAll과 짝을 이루는 popAll을 작성 해보자.
```java
public void popAll(Collection<E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
```
여기서도 마찬가지로 컬렉션의 원소타입이 스택의 원소 타입과 일치하면 문제가 없지만 Stack&lt;Number&gt;의 원소를 Object용 컬렉션으로 옮긴다고 해보자.
```java
Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = ...;
numberStack.popAll(objects);
```
컴파일하면 Collections&lt;Object&gt;는 Collections&lt;Number&gt;의 하위 타입이 아니다라는 오류가 난다. 이번에도 와일드카드 타입으로 해결할 수 있다. popAll의 입력 매개변수의 타입이 'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다.(모든 타입은 자기자신의 상위 타입이다.)
```java 
public void popAll(Collection<? super E> dst) {
    while (!isEmpty) {
        dst.add(pop());
    }
}
```
포인트는 유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드 카입을 사용하자는 것. 한편, 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다. 타입을 정확하게 정해야하는 상황이므로 이때는 쓰면 안된다. 아래의 공식은 어떤 와일드 카드를 사용해야 하는지 기억하는데 도움이 된다.
> 펙스(PECS): producer-extends, consumer-super
매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고, 소비자라면 <? super T>를 사용하라. 위의 공식을 겟풋원칙(Get and Put Principle)이라고도 부른다.

위의 공식을 기억하고, 앞 전에 소개한 메서드와 생성자 선언을 살펴보자, 아이템 28의 chooser 생성자는 다음과 같이 선언했다.
```java
public Chooser(Collection<T> choices)
```
이 생성자로 넘겨지는 컬렉션은 T타입의 값을 생산하기만 하니 T를 확장하는 와일드 카드 타입을 사용해 선언해야 한다.
```java
public Chooser(Collection<? extends T> choices)
```
이럴 경우 실질적인 차이가 생기는가?, 그렇다. Chooser&lt;Number&gt;의 생성자에 List&lt;Integer&gt;를 넘기고 싶다고 해보자, 수정 전 생성자로는 컴파일조차 되지 않겠지만, 한정적 와일드카드 타입으로 선언한 수정 후 생성자에서는 문제가 사라진다.

이전에 작성해던 max 메서드에 주목해보자. 원래 버전의 선언은 다음과 같다.
```java
public static <E extends Comparable<E>> E max(List<E> list)
```
와일드카드를 이용하여 다듬으면 다음과 같다.
```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```
입력 매개변수에서는 E 인스턴스를 생산하므로 extends로 수정하였다. 그리고 타입 매개변수 E는 좀 난해하다. 원래 선언에서는 E가 Comparable&lt;E&gt;를 확장한다고 했다. 이때 Comparable&lt;E&gt;는 E 인스턴스를 소비한다(그리고 선후 관계를 뜻하는 정수를 생산한다.) 그래서 매개변수화 타입 Comparable&lt;E&gt;를 한정적 와일드카드 타입ㅇ니 Comparable<? super E>로 대체 했다.

타입 매개변수와 와일드카드에는 공통되는 부분이 있어서, 메서드를 정의 할 때 둘 중 어느 것을 사용해도 괜찮을 때가 많다. 예를 들어 주어진 리스트에서 명시한 두 인덱스와 아이템들을 교환(swap)하는 정적 메서드를 두 방식 모두로 정의해보자. 다음 코드에서 첫 번째는 비한정적 타입 매개변수를 사용했고 두 번째는 비한정적 와일드카드를 사용했다.
```java
public static <E> void swap(List<E> list, int i , int j);
public static void swap(List<?> list, int i , int j);
```
어떤 선언이 더 나을까? 더 나은 이유는 무엇일까? public API라면 간단한 두 번째가 낫다. 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스와 원소들을 교환해 줄 것이다. 신경 써야 할 매개변수도 없다.

기본 규칙은 **메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체하라.** 이때 비한정적 타입 매개변수라면 비한정적 와일드카드로, 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다.

하지만 두 번째 swap 메서드에는 문제가 있다. 다음과 같이 아주 직관적으로 구현한 코드가 컴파일 되지 않는다.
```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```
이 코드를 컴파일하면 에러가 나온다. list.get(i)를 list.set할 수 없다. 원인은 리스트의 타입이 List&lt;?&gt;인데 List&lt;?&gt;에는 null외에는 어떤 값도 넣을 수 없다는 데 있다. 다행히 형변환이나 리스트의 로 타입을 사용하지 않고도 해결할 길이 있다. 바로 와일드카드 타입의 실제 타입을 알려주는 메서드를 private 도우미 메서드로 따로 작성하여 활용하는 방법이다.
```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(list i, j);
}

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
private static <E> volid swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```
swapHelper 메서드는 리스트가 List&lt;E&gt;임을 알고 있다. 즉, 이 리스트에서 꺼낸 값은 항상 E이고, E 타입의 값이라면 이 리스트에 넣어도 안전함을 알고 있다.