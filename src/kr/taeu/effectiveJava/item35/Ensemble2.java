package kr.taeu.effectiveJava.item35;

/*
 * 열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지 말고,
 * 인스턴스 필드에 저장하자.
 */
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
