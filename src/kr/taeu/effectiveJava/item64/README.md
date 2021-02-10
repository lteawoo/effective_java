# 객체는 인터페이스를 사용해 참조하라
이전에 매개변수 타입으로 클래스가 아닌 인터페이스를 사용하라고 하였다, 이를 확장하면 객체는 클래스가 아닌 인터페이스로 참조하라고 할 수 있다

즉 **적합한 인터페이스만 있으면 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언하라.**
```java
// 좋은 예, 인터페이스를 타입으로 사용
Set<Item> itemSet = new LinkedHashSet<>();

// 나쁜 예, 클래스를 타입으로 사용
LinkedHashSet<Item> itemSet = new LinkedHashSet<>();
```
인터페이스를 타입으로 사용하는 습관을 길러두면 프로그램이 훨씬 유연해진다, 나중에 구현 클래스를 교체하고자 한다면
그저 새 클래스의 생성자(혹은 다른 정적 팩터리)를 호출해주기만 하면 된다.
```java
Set<Item> itemSet = new HashSet<>();
```
하지만 원래의 클래스가 인터페이스의 일반 규약 이외의 특별한 기능을 제공하며, 주변 코드가 이 기능에 기대어 작동한다면
새로운 클래스도 반드시 같은 기능을 제공해야한다.

예를 들어 LinkedHashSet이 따르는 순서 정책을 가정하고 동작하는 상황에서 이를 HashSet으로 변경하면 문제가 생길수 있다. HashSet은 반복자의 순회 순서를 보장하지 않기 때문이다.

구현 타입을 바꾸려 하는 동기는 무엇일까? 원래 것보다 성능이 좋거나 멋진 신기능을 제공하기 때문일 수도 있다.
예를 들어 HashMap을 참조하던 변수가 있다, 이를 EnumMap으로 바꾸면 속도가 빨라지고 순회 순서도 키의 순서와 같아진다 대신 키가 열거 타입일 때만 사용할 수 있다.

한편 키 타입과 상관없이 사용할 수 있는 LinkedHashMap으로 바꾼다면 성능은 비슷하게 유지하면서 순회 순서를 보장할 수 있다.

## 적합한 인터페이스가 없다면 당연히 클래스로 참조해야한다
String과 BigInteger같은 클래스가 그렇다. 값 클래스를 여러가지로 구현될수 있다고 설계하는 일은 거의 없다.

따라서 final인 경우가 많고 상응하는 인터페이스가 별도로 존재하는 경우가 드물다.

적합한 인터페이스가 없는 또 다른 경우는 클래스 기반으로 작성된 프레임워크가 제공하는 객체들이다. 
이런 경우라도 특정 구현 클래스보다는 (보통은 추상 클래스인) 기반 클래스를 사용하는 것이 좋다. OutputStream 등 java.io 패키지의 여러 클래스가 이렇다.

그리도 마지막으로 인터페이스에는 없는 특별한 메서드를 제공하는 클래스들이다. 예를 들어 PriorityQueue 클래스는 Queue 인터페이스에는 없는 comparator 메서드를 제공한다.

클래스 타입을 직접 사용하는 경우는 이런 추가 메서드를 사용하는 경우로 최소화 해야하고 절대 남발하지 말아야 한다.

**적합한 인터페이스가 없다면 클래스의 계층구조 중 필요한 기능을 만족하는 가장 덜 구체적인(상위의) 클래스를 타입으로 사용하자.**