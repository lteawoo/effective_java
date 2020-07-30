package kr.taeu.effectiveJava.item35;

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
