package kr.taeu.effectiveJava.item14;

import java.util.Comparator;

/*
 * Comparator 인터페이스가 비교자 생성 메서드(comparator construction method)와 연쇄 방식으로 비교자를 생성할 수 있게 되었다.
 */
public class PhoneNumber2 implements Comparable<PhoneNumber2>{
  private final short areaCode, prefix, lineNum;
  
  public PhoneNumber2(int areaCode, int prefix, int lineNum) {
    this.areaCode = rangeCheck(areaCode, 999, "지역코드");
    this.prefix = rangeCheck(prefix, 999, "프리픽스");
    this.lineNum = rangeCheck(lineNum, 9999, "가입자 번호");
  }
  
  private static short rangeCheck(int val, int max, String arg) {
    if (val < 0 || val > max) {
      throw new IllegalArgumentException(arg + ": " + val);
    }
    return (short) val;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } 
    if (!(o instanceof PhoneNumber2)) {
      return false;
    }
    PhoneNumber2 pn = (PhoneNumber2)o;
    return pn.lineNum == lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
  }

  /*
   * Comparator 생성 메서드를 이용한 방법
   */
  private static final Comparator<PhoneNumber2> COMPARATOR = 
      Comparator.comparingInt((PhoneNumber2 pn) -> pn.areaCode)
        .thenComparingInt(pn -> pn.prefix)
        .thenComparingInt(pn -> pn.lineNum);
  
  @Override
  public int compareTo(PhoneNumber2 pn) {
    return COMPARATOR.compare(this, pn);
  }
}
