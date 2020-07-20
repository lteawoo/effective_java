# 제네릭과 가변인수를 함께 쓸 때는 신중하라
가변인수(varargs)는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해주는데, 구현 방식에 허점이 있다. 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다. 그런데 내부로 감춰야 했을 이 배열을 그만 클라이언트에 노출하는 문제가 생겼다. 그 결과 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

실체화 불가 타입은 런타임에는 컴파일타임보다 타입 관련 정보를 적게 담고 있음을 배웠다. 그리고 거의 모든 제네릭과 매개변수화 타입은 실체화되지 않는다. 메서드를 선언할 때 실체화 불가 타입으로 varargs 매개변수를 선언하면 컴파일러가 경고를 보낸다. 가변인수 메서드를 호출할 때도 varargs 매개변수가 실체화 불가 타입으로 추론되면, 그 호출에 대해서도 경고를 낸다. 경고 형태는 대략 다음과 같다.
```java
[unchecked] Possible heap pollution from parameterized vararg type List<String>
```
매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙오염이 발생한다.
```java
static void dangerous(List<String>... stringLists) {
  List<Integer> intList = List.of(42);
  Object[] objects = stringLists;
  objects[0] = intList;             // 힙 오염 발생
  String s = stringLists[0].get(0); // ClassCastException
}
```
형변환하는 곳이 보이지 않는데도 인수를 건네 호출하면 ClassCastException이 발생한다. 마지막 줄에 컴파일러가 생성한 (보이지 않는) 형변환이 숨어 있기 때문이다. 이처럼 타입 안정성이 깨지니 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.

메서드가 타입 안전한지는 어떻게 확신할 수 있을까 가변 인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열이 만들어진다는 사실을 기억하자. 메서드가 이 배열에 아무것도 저장하지 않고(그 매개변수들을 덮어쓰지 않고) 그 배열의 참조가 밖으로 노출되지 않는다면(신뢰할 수 없는 코드가 배열에 접근할 수 없다면) 타입 안전하다.

이때 varargs 매개변수 배열에 아무것도 저장하지 않고도 타입 안전성을 깰수도 있으니 주의해야 한다. 가변인수로 넘어온 매개변수들을 배열에 담아 반환하는 제네릭 메서드다.
```java
static <T> T[] toArray(T... args) {
  return args;
}
```
이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타입에 결정되는데, 그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단 할 수 있다. 따라서 자신의 varargs 매개변수 배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 쪽의 콜스택까지 전이하는 결과를 낳을 수 있다.

구체적인 예를 보자
```java
static <T> T[] pickTwo(T a, T b, T c) {
  switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return toArray(a, b);
    case 1: return toArray(a, c);
    case 2: return toArray(b, c);
  }
  throw new AssertionError(); // 도달할 수 없다.
}
```
이 메서드는 제네릭 가변인수를 받는 toArray 메서드를 호출한다는 점만 빼면 위험하지도 않고 경고도 내지 않을 것 이다.

이 메서드를 본 컴파일러는 toArray에 넘길 T 인스턴스 2개를 담을 varargs 매개변수 배열을 만드는 코드를 생성한다. 이 코드가 만드는 배열의 타입은 Object[]인데, pickTwo에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다. 그리고 toArray 메서드가 돌려준 이 배열이 그대로 pickTwo를 호출한 클라이언트까지 전달된다. 즉, pickTwo는 항상 Object[] 타입 배열을 반환한다.
```java
public static void main(String[] args) {
  String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```
아무런 문제가 없는 메서드이니 별다른 경고 없이 컴파일된다. 하지만 실행하면 ClassCastException을 던진다. 바로 pickTwo의 반환값을 attributes에 저장하기 위해 String[]로 형변환하는 코드를 컴파일러가 자동 생성한다는 점을 놓쳤다. Object[]는 String[]의 하위 타입이 아니므로 이 형변환은 실패한다.

이 예는 **제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다**는 점을 다시 한번 상기시킨다. 단 예외가 두 가지 있다.
1. @SafeVarargs로 제대로 애노테이트된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
2. 그저 이 배열 내용의 일부 함수를 호출만 하는(varargs를 받지 않는) 일반 메서드에 넘기는 것도 안전하다.

다음은 제네릭 varargs 메개변수를 안전하게 사용하는 전형적인 예다.
```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
  List<T> result = new ArrayList<>();
  for (List<? extends T> list : lists) {
    result.addAll(list);
  }
  return result;
}
```
item28의 조언에 따라 varargs 매개변수를 List 매개변수로 바꿀 수도 있다. 이 방식을 앞서의 flatten 메서드에 적용하면 다음과 같다.
```java
static <T> List<T> flatten(List<List<? extends T>> lists) {
  List<T> result = new ArrayList<>();
  for (List<? extends T> list : lists) {
    result.addAll(list);
  }
  return result;
}
```
정적 팩터리 메서드인 List.of를 활용하면 다음 코드와 같이 이 메서드에 임의 개수의 인수를 넘길 수 있다. 이렇게 사용하는게 가능한 이유는 List.of에도 @SafeVarargs 애너테이션이 달려 있기 때문이다.
```java
audience = flatten(List.of(friends, romans, countrymen));
```
또한 toArray처럼 varargs 메서드를 안전하게 작성하는 게 불가능한 상황에서도 쓸 수 있다. 이 toArray의 List 버전이 바로 List.of다.
```java
static <T> List<T> pickTwo(T a, T b, T c) {
  switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return List.of(a, b);
    case 1: return List.of(a, c);
    case 2: return List.of(b, c);
  }
  throw new AssertionError(); // 도달할 수 없다.
}
```
main 메서드는 다음과 같다.
```java
public static void main(String[] args) {
  List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
}
```