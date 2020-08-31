# 표준 함수형 인터페이스를 사용하라
자바가 람다를 지원하면서 API를 작성하는 모범 사례로 크게 바뀌었다. 예컨대 상위 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 템플릿 메서드 패턴의 매력이 크게 줄었다. 이를 대체하는 현대적인 해법은 같은 효과의 함수 객체를 받는 정적 패터리나 생성자를 제공하는 것이다.

이 내용을 일반화해서 말하면 함수 객체를 매개변수로 받는 생성자와 메서드를 더 많이 만들어야 한다. 이때 함수형 매개변수 타입을 올바르게 선택해야 한다.

LinkedHashMap을 생각해보자. 이 클래스의 protected 메서드인 removeEldestEntry를 재정의하면 캐시로 사용할 수 있다. 맵에 새로운 키를 추가하는 put 메서드는 이 메서드를 호출하여 true가 반환되면 맵에서 가장 오래된 원소를 제거한다. 예컨대 removeEldestEntry를 다음처럼 재정의하면 맵에 원소가 100개가 될 때까지 커지다가, 그 이상이 되면 새로운 키가 더해질 때마다 가장 오래된 원소를 하나씩 제거한다. 즉, 가장 최근 원소 100개를 유지한다.
```java
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > 100;
  }
```
잘 동작하지만 람다를 사용하면 훨씬 잘 해낼 수 있다. LinkedHashMap을 오늘날 다시 구현한다면 함수 객체를 받는 정적패터리나 생성자를 제공했을 것이다.

removeEldestEntry 선언을 보면 이 함수 객체는 Map.Entry\<K, V>를 받아 boolean을 반환해야 할 것 같지만, 꼭 그렇지는 않다. removeEldestEntry는 size()를 호출해 맵 안의 원소 수를 알아내는데, removeEldestEntry가 인스턴스 메서드라서 가능한 방식이다. 하지만 생성자에 넘기는 함수 객체는 이 맵의 인스턴스 메서드가 아니다. 팩터리나 생성자를 호출할 때는 맵의 인스턴스가 존재하지 않기 때문이다. 따라서 맵은 자기 자신도 함수 객체에 건네줘야 한다. 이를 반영한 함수형 인터페이스는 다음처럼 선언할 수 있다.
```java
// 불필요한 함수형 인터페이스 - 대신 표준 함수형 인터페이스를 사용하라
@FunctionalInterface
interface EldestEntryRemovalFunction<K, V> {
  boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}
```
이 인터페이스도 잘 동작하기는 하지만, 굳이 사용할 이유는 없다. 자바 표준 라이브러리에 이미 같은 모양의 인터페이스가 준비되어 있기 때문이다. java.util.function 패키지를 보면 다양한 용도의 표준 함수형 인터페이스가 담겨 있다. **필요한 용도에 맞는 게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라** 그러면 API가 다루는 개념의 수가 줄어들어 익히기 더 쉬워진다.

또한 표준 함수형 인터페이스들은 유용한 디폴트 메서드를 많이 제공하므로 다른 코드와의 상호운용성도 크게 좋아질 것이다. 예컨데 Predicate 인터페이스는 프레디키트(predicate)들을 조합하는 메서드를 제공한다. 앞의 LinkedhashMap 예에서는 직접만든 EldestEntryRemovalFunction 대신 표준 인터페이스인 BiPredicate\<Map<K, V>, Map.Entry\<K, V>>를 사용할 수 있다.

java.util.function 패키지에는 총 43개의 인터페이스가 담겨 있다. 전부 기억하긴 어렵지만 기본 인터페이스 6개만 기억하면 나머지를 충분히 유추해 낼 수 있다. 이 기본 인터페이스들은 모두 참조 타입용이다. 하나씩 살펴보자

### Operator 인터페이스 - 반환값과 인수의 타입이 같은 함수
* 인수가 1개인 UnaryOperator와 2개인 BinaryOperator로 나뉜다.
* UnaryOperator\<T>
* 시그니처 : T apply(T t)
* Ex : String::toLowerCase
* BinaryOperator\<T>
* 시그니처 : T apply(T t1, T t2)
* Ex : BigInteger::add
### Predicate 인터페이스 - 인수 하나를 받아 boolean을 반환하는 함수
* Predicate\<T>
* 시그니처 : boolean test(T t)
* Ex : Collection::isEmpty
### Function 인터페이스 - 인수와 반환 타입이 다른 함수
* Function\<T, R>
* 시그니처 : R apply(T t)
* Ex : Arrays::asList
### Supplier 인터페이스 - 인수를 받지 않고 값을 반환(혹은 제공)하는 함수
* Supplier\<T>
* 시그니처 : T get()
* Ex : Instant::now
### Consumer 인터페이스 - 인수를 하나 받고 반환값은 없는(특히 인수를 소비하는) 함수
* Consumer\<T>
* 시그니처 : void accept(T t)
* Ex : System.out::println

## 기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자
표준 함수형 인터페이스 대부분은 기본 타입만 지원한다. 박싱된 기본 타입을 넣으면 계산량이 많을때 성능이 처참히 느려질 수 있다.

# 코드를 직접 작성해야 할 때
표준 인터페이스 중 필요한 용도에 맞는 게 없다면 직접 작성해야 한다. 예를 들어 매개변수 3개를 받는 Predicate라든가 검사 예외를 던지는 경우가 있을 수 있다. 그런데 구조적으로 똑같더라도 직접 작성해야만 할 때가 있다.

Comparator\<T> 인터페이스는 구조적으로는 ToIntBiFunction\<T, U>와 동일하다. 심지어 자바 라이브러리에 Comparator\<\T>를 추가 할 당시 ToIntBiFunction\<T, U>가 이미 존재했더라도 사용하면 안됐다. Comparator가 독자적인 인터페이스로 살아남아야 하는 이유가 몇개 있다.
1. API에서 굉장히 자주 사용되는데, 지금의 이름이 그 용도를 아주 훌륭히 설명해준다.
2. 구현하는 쪽에서 반드시 지켜야 할 규약을 담고 있다.
3. 비교자들을 변환하고 조합해주는 유용한 디폴트 메서드들을 듬뿍 담고 있다.

이상의 Comparator 특성을 정리하면 다음과 같다 이중 하나 이상을 만족한다면 전용 함수형 인터페이스를 구현해야 하는건 아닌지 고민해야 한다.
1. 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
2. 반드시 따라야 하는 규약이 있다.
3. 유용한 디폴트 메서드를 제공할 수 있다

## 직접만든 함수형 인터페이스에는 항상 @FunctionalInterface 애너테이션을 사용하라.
작성하기로 했다면 자신이 '인터페이스'를 작성한다는 것을 명심하고 아주 주의해서 설계해야 한다. EldestEntryRemovalFunction 인터페이스에 @FunctionalInterface 애너테이션이 달려 있음에 주목하자, 람다용으로 설계되었다는 것을 의미하고 누군가 실수로 메서드를 추가하지 못하게 막아준다.

# 함수형 인터페이스를 API에서 사용할 때의 주의점
서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의해서는 안된다. 클라이언트에게 불피룡한 모호함만 안겨줄 뿐이며, 이 모호함으로 인해 실제로 문제가 일어난ㄴ다.

ExecutorService의 submit 메서드는 Callable\<T>를 받는 것과 Runnable을 받는 것을 다중정의 했다. 그래서 올바른 메서드를 알려주기 위해 형변환 해야 할 때가 왕왕 생긴다.

이런 문제를 피하는 가장 쉬운 방법은 서로 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중정의를 피하는 것이다.
