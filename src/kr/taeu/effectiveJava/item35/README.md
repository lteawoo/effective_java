# ordinal 메서드 대신 인스턴스 필드를 사용하라
대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응된다. 그리고 모든 열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal이라는 메서드를 제공한다.
```java
/*
 * ordinal의 잘못된 사용
 */
public enum Ensemble {
  SOLO, DUET, TRIO, QUARTET, QUINTET,
  SEXTET, SEPTET, OCTET, NONET, DECTET;

  public int numberOfMusicians() {
    return ordinal() + 1;
  }
}
```
상수 선언 순서를 바꾸는 순간 numberOfMusicians()가 오동작하고, 이미 사용중인 정수와 값이 같은 상수는 추가할 방법이 없다.(8중주(octet)이 이미 있으니 똑같이 8명이 연주하는 복4중주(double quartet)는 추가할 수 없다)

또한 값을 중간에 비워 둘 수 도 없다. 12명이 연주하는 3중주, 4중주를 추가한다고 해보자, 그러려면 중간에 11짜리 상수도 채워야하는데, 해당 연주는 일컫는 이름이 없다.

## 해결책
열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지말고, 인스턴스 필드에 저장하자.
```java
public enum Ensemble2 {
  SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
  SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
  NONET(9), DECTET(10), TRIPLE_QUARTET(12);

  private final int numberOfMusicians;
  Ensemble2(int size) {
    this.numberOfMusicians = size;
  }

  public int numberOfMusicians() {
    return numberOfMusicians;
  }
}
```
Enum api 문서에는 이렇게 기술되어 있다.
> 대부분 프로그래머는 이 메서드를 쓸 일이 없다. 이 메서드는 EnumSet과 EnumMap과 같이 열거 타입기반의 범용 자료구조에 쓸 목적으로 설계되었다.

따라서 이런 용도가 아니라면 ordinal 메서드는 절대 사용하지 말자.