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
1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.(첫 번째 객체가 두 번째 객체보다 작으면 두 번째가 첫 번째 보다 커야한다.)
2. 추이성을 보장해야 한다. 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.
3. 크기가 같은 객체끼리는 어떤 객체와 비교하더라도 항상 같아야 한다는 뜻이다.

위 규약은 compareTo 메서드로 수행하는 동치성 검사도 equals 규약과 똑같이 반사성, 대칭성, 추이성을 충족해야 함이다. 그래서 주의사항도 똑같다. 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가했다면 compareTo 규약을 지킬 방법이 없다.(우회 법은 아이템10과 동일하게 뷰 메서드를 제공하면 된다.)

정렬된 컬렉션들은 동치성을 비교할 때 equals 대신 compareTo를 사용한다 주의하자 compareTo와 equals가 일관되지 않는 BigDecimal 클래스로 예를 들어보면, 빈 HashSet 인스턴스를 생성하고 new BigDecimal("1.0")과 new BigDecimal("1.00")을 차례로 추가한다. 이 두 BigDecimal은 equals 메서드로 비교하면 서로 다르기 때문에 HashSet은 원소를 2개 갖게 된다. 하지만 HashSet 대신 TreeSet을 사용하게 되면 원소는 하나만 갖는다. compareTo 메서드로 비교하면 두 BigDecimal 인스턴스가 똑같기 때문이다.(BigDecimal의 equals는 값과 스케일이 동일한 경우에만 동일한 것으로 간주)


