# 타입 안전 이종 컨테이너를 고려하라
하나의 컨테이너에서는 매개변수화할 수 있는 타입의 수가 제한된다. 예컨대 Set에는 원소의 타입을 뜻하는 단 하나의 타입 매개변수만 있으면 되며, Map에는 키와 값의 타입을 뜻하는 2개만 필요한 식이다.

하지만 더 유연한 수단이 필요할 때도 종종 있다. 예컨대 데이터베이스의 행은 임의 개수의 열을 가질 수 있는데, 모두 열을 타입 안전하게 이용할 수 있다면 멋질 것이다. 컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하면 된다. 이렇게 하면 제네릭 타입 시스템이 값의 타입이 키와 같음을 보장해줄 것이다. 이러한 설계 방식을 타입 안전 이종 컨테이너 패턴(type safe heterogeneous container pattern) 이라 한다.

예로 타입별로 즐겨 찾는 인스턴스를 저장하고 검색할 수 있는 Favorites 클래스를 생각해보자, 각 타입의 Class 객체를 매개변수화한 키 역할로 사용하면 되는데, 이 방식이 동작하는 이유는 class의 클래스가 제네릭이기 때문이다. class 리터럴의 타입은 Class가 아닌 Class< T>다. 예컨대 String.class의 타입은 Class< String>이고 Integer.class의 타입은 Class< Integer>이다.

한편, 컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴을 타입 토큰(type token)이라 한다.
```java
// 타입 안전 이종 컨테이너 패턴 - api
public class Favorites {
  public <T> void putFavorite(Class<T> type, T instance);
  public <T> T getFavorite(Class<T> type);
}
```
```java
// 타입 안전 이종 컨테이너 패턴 - 클라이언트
public static void main(String[] args) {
  Favorites f = new Favorites();

  f.putFavorite(String.class, "Java");
  f.putFavorite(Integer.class, 0xcafebabe);
  f.putFavorite(Class.class, Favorites.class);

  String favoriteString = f.getFavorite(String.class);
  int favoriteInteger = f.getFavorite(Integer.class);
  Class<?> favoriteClass = f.getFavorite(Class.class);

  System.out.println("%s %x %s\n", favoriteString, favoriteInteger, favoriteClass.getName());
}
```
Favorites 인스턴스는 타입 안전하다. String을 요청했는데 Integer를 반환하는 일은 절대 없다. 또한 모든 키의 타입이 제각각이라, 일반적인 맵과 달리 여러 가지 타입의 원소를 담을  수 있다. 따라서 Favorites는 타입 안전 이종 컨테이너라 할만 한다.
```java
// 타입 안전 이종 컨테이너 - 구현
public class Favorites {
  private Map<Class<?>, Object> favorites = new HashMap<>();

  public <T> void putFavorite(Class<T> type, T instance) {
    favorites.put(Objects.requireNonNull(type), instance);
  }

  public<T> T getFavorite(Class<T> type) {
    return type.cast(favorites.get(type));
  }
}
```
와일드카드 타입이 중첩되었고, 맵이 아니라 키가 와일드카드 타입인 것이다. 이는 모든 키가 서로 다른 매개변수화타입일 수 있다는 뜻이다.

맵의 값 타입은 단순히 Object이다. 이 맵은 키와 값 사이의 타입 관계를 보증하지 않는다는 말이다. 즉, 모든 값이 키로 명시한 타입임을 보증하지 않는다.

putFavorites 구현은 아주 쉽다. 주어진 Class 객체와 즐겨찾기 인스턴스를 favorites에 추가해 관계를 지으면 끝이다

getFavorites 구현은 주어진 Class 객체에 해당하는 값을 favorites 맵에서 꺼낸다. 이 객체가 바로 반환해야 할 객체가 맞지만, 잘못된 컴파일타임 타입을 가지고 있다. 이 객체의 타입은(favorites 맵의 값 타입인) Object이나, 우리는 이를 T로 바꿔 반환해야 한다.

따라서 Class의 cast 메서드를 이용해 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환한다. cass 메서드는 형변환 연산자의 동적 버전이다. Class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 그대로 반환하고, 아니면 ClassCastException을 던진다.

cast의 반환 타입은 Class 객체의 타입 매개변수와 같다.
```java
public class Class<T> {
  T cast(Object obj);
}
```
Favorites가 타입 불변식을 어기는 일이 없도록 보장하려면 putFavorites 메서드에서 인수로 주어진 instance의 타입이 type으로 명시한 타입과 같은지 확인하면 된다.
```java
public <T> void putFavorite(Class<T> type, T instance) {
  favorites.put(Objects.requireNonNull(type), type.cast(instance));
}
```
때로는 이 메서드들이 허용하는 타입을 제한하고 싶을 수 있는데, 한정적 타입 토큰을 활용하면 가능하다. 한정적 타입 토큰이란 단순히 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰이다.

애너테이션 api는 한정적 타입 토큰을 적극적으로 사용한다.
```java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```
여기서 annotationType 인수는 애너테이션 타입을 뜻하는 한정적 타입 토큰이다.

Class< ?> 타입의 객체가 있고, 이를 (getAnnotation처럼) 한정적 타입 토큰을 받는 메서드에 넘기려면 어떻게 해야 할까? 객체를 Class<? extends Annnotation>으로 형변환 할 수 있지만 이는 비검사 이므로 경고가 출력된다.

Class 클래스의 asSubclass메서드를 사용하면된다. 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.
```java
static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) {
  Class<?> annotationType = null; // 비한정적 타입 토큰
  try {
    annotationType = Class.forName(annotationTypeName);
  } catch (Exception e) {
    throw new IllegalArgumentException(e);
  }
  return element.getAnnotation(annotationType.asSubClass(Annotation.class));
}
```