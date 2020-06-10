# Comparable을 고현할지 고려하라
Comparable은 단순 동치성 비교에 더해 순서까지 비교할 수 있으며, 제네릭하다. Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서가 있음을 뜻한다.

예컨대 다음 프로그램은 명령줄 인수들을 (중복은 제거하고) 알파벳순으로 출력한다. String이 Comparable을 구현한 덕분이다.
```java
/*
 * 명령줄 인수들을 중복을 제거하고 알파벳순으로 출력하는 프로그램
 */
public class WordList1 {
  public static void main(String[] args) {
    Set<String> s = new TreeSet<>();
    Collections.addAll(s, args);
    System.out.println(s);
  }
}
```
사실상 자바 플랫폼 라이브러리의 모든 값 클래스와 열거타입이 Comparable을 구현했다. 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.(좁쌀만한 노력으로 코끼리만 한 효과를 누릴 수 있다)
```java
public interface Comparable<T> {
    int compareTo(T t);
}
```
equals는 모든 객체에 대해 전역 동치관계를 부여했지만, compareTo는 타입이 다른 객체를 신경 쓰지 않아도 된다. 다른 타입의 객체가 주어지면 단순히 ClassCastException을 던져도 되며, 대부분 그렇게 한다.

# 규약
**compareTo는 이 객체와 주어진 객체의 순서를 비교한다.**
1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.(첫 번째 객체가 두 번째 객체보다 작으면 두 번째가 첫 번째 보다 커야한다.)
2. 추이성을 보장해야 한다. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.
3. 크기가 같은 객체끼리는 어떤 객체와 비교하더라도 항상 같아야 한다는 뜻이다.

위 규약은 compareTo 메서드로 수행하는 동치성 검사도 equals 규약과 똑같이 반사성, 대칭성, 추이성을 충족해야 함이다. 그래서 주의사항도 똑같다. 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가했다면 compareTo 규약을 지킬 방법이 없다.(우회 법은 아이템10과 동일하게 뷰 메서드를 제공하면 된다.)

정렬된 컬렉션들은 동치성을 비교할 때 equals 대신 compareTo를 사용한다 주의하자 compareTo와 equals가 일관되지 않는 BigDecimal 클래스로 예를 들어보면, 빈 HashSet 인스턴스를 생성하고 new BigDecimal("1.0")과 new BigDecimal("1.00")을 차례로 추가한다. 이 두 BigDecimal은 equals 메서드로 비교하면 서로 다르기 때문에 HashSet은 원소를 2개 갖게 된다. 하지만 HashSet 대신 TreeSet을 사용하게 되면 원소는 하나만 갖는다. compareTo 메서드로 비교하면 두 BigDecimal 인스턴스가 똑같기 때문이다.(BigDecimal의 equals는 값과 스케일이 동일한 경우에만 동일한 것으로 간주)

Comparable은 타입을 인수로 받는 제네릭 인터페이스이므로 comparaTo 메서드의 인수 타입은 컴파일타임에 정해진다. 입력 인수의 타입을 확인하거나 형변환할 필요가 없다는 뜻이다. 인수의 타입이 잘못됐다면 컴파일 자체가 되지 않는다.

compareTo 메서드는 각 필드가 동치인지를 비교하는게 아니라 그 순서를 비교한다, 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다. Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다. 비교자는 직접만들거나 자바가 제공하는 비교자를 사용하자
```java
  /*
   * 자바가 제공하는 비교자를 사용한다.(Comparator를 이용)
   */
  @Override
  public int compareTo(CaseInsensitiveString cis) {
    return String.CASE_INSENSITIVE_ORDER.compare(s,  cis.s);
  }
```
기본 타입 클래스의 비교는 박싱된 기본 타입 클래스(Short 등..)들에 새로 추가된 정적 메서드인 compare를 이용하자. compareTo 메서드에서 관계 연산자인 >와 <를 사용하는 방식은 거추장스럽고 오류를 유발한다.

클래스의 핵심필드가 여러개라면 어느 것을 먼저 비교하냐가 중요하다. 가장 핵심적인 필드부터 비교하자. 비교 결과가 0이 아니면, 순서가 결정되면 거기서 끝이다. 그 결과를 곧장 반환하자. 가장 핵심이 되는 필드가 똑같다면, 똑같지 않은 필드를 찾을때 까지 그 다음으로 중요한 필드를 비교해나가자.
```java
  /*
   * 중요한 필드부터 차례대로 비교해 나간다.
   */
  @Override
  public int compareTo(PhoneNumber pn) {
    int result = Short.compare(areaCode, pn.areaCode); // 가장 중요한 필드
    if (result == 0) {
      result = Short.compare(prefix, pn.prefix); // 두 번째로 중요한 필드
      if (result == 0) {
        result = Short.compare(lineNum, pn.lineNum); // 세 번째로 중요한 필드
      }
    }
    return result;
  }
```
자바 8에서는 Comparator 생성 메서드를 이용하여 간결하게 연쇄적으로 compareTo 메서드를 구현 할 수 있다. 하지만 약간의 성능 저하가 뒤따른다.
```java
  private static final Comparator<PhoneNumber2> COMPARATOR = 
      Comparator.comparingInt((PhoneNumber2 pn) -> pn.areaCode)
        .thenComparingInt(pn -> pn.prefix)
        .thenComparingInt(pn -> pn.lineNum);
  
  @Override
  public int compareTo(PhoneNumber2 pn) {
    return COMPARATOR.compare(this, pn);
  }
```
클래스를 초기화 할때 비교자 생성 메서드 2개를 이용해 비교자를 생성한다. 그 첫 번째인 comparingInt는 객체 참조를 int 타입 키에 매핑하는 키 추출 함수를 인수로 받아((PhoneNumber2 pn) -> { return pn.areaCode }), 그 키를 기준으로 순서를 정하는 비교자를 반환하는 정적 메서드다. 예를 보면 comparingInt는 람다를 인수로 받아 PhoneNumber에서 추출한 지역 코드를 기준으로 전화번호의 순서를 정하는 Comparator< PhoneNumber >를 반환한다.

두 전화번호의 지역번호가 같을 수 있으니, 다음 비교는 두번째 비교자 생성 메서드인 thenComparingInt가 수행한다. 해당 메서드는 Comparator의 인스턴스 메서드로, int 키 추출자 함수를 입력 받아 다시 비교자를 반환한다.

이런 방식으로 Comparator는 수많은 보조 생성 메서드들이 존재한다. long과 double용으로는 comparingInt, thenComparingInt의 변형 메서드가 존재하고, short 처럼 int보다 작은 정수 타입에는 int용 버전을 사용하면 된다. 이런식으로 자바의 모든 숫자용 기본 타입을 모두 커버한다.

객체 참조용 비교자 생성 메서드도 준비되어 있다. 우선, comparing이라는 정적 메서드 2개가 다중정의 되어 있다.
1. 키 추출자를 받아서 그 키의 자연적 순서를 사용한다.
2. 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다.

또한 thenComparing이란 인스턴스 메서드가 3개 다중정의되어 있다.
1. 비교자 하나만 인수로 받아 그 비교자로 부차 순서(comparing으로 1차 순서를 정하고..)를 정한다.
2. 키 추출자를 인수로 받아 그 키의 자연적 순서로 보조 순서를 정한다.
3. 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다.

이따금 값의 차를 기준으로 첫번째 값이 두번째 값보다 작으면 음수를, 같으면 0을, 크면 양수를 반환하는 compareTo나 compare 메서드와 마주할 것이다.
```java
  static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
      return o1.hashCode() - o2.hashCode();
    }
  }
```
## 해시코드 값의 차를 기준으로 하는 비교자 - 추이성을 위배한다
정수 오버플로를 일으키거나, 부동소수점 계산 방식에 따른 오류를 낼 수 있다. 월등히 빠르지도 않으니 다음 2가지 방식 중 하나를 사용하자.
1. 정적 compare 메서드를 활용한 비교자
```java
  static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
      return Integer.compare(o1.hashCode(), o2.hashCode());
    }
  }
```
2. 비교자 생성메서드를 활용한 비교자
```java
  static Comparator<Object> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
```