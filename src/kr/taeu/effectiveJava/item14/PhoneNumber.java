package kr.taeu.effectiveJava.item14;

/*
 * 핵심 필드를 compareTo로 비교해나가는 PhoneNumber 클래스
 */
public class PhoneNumber implements Comparable<PhoneNumber>{
  private final short areaCode, prefix, lineNum;
  
  public PhoneNumber(int areaCode, int prefix, int lineNum) {
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
    if (!(o instanceof PhoneNumber)) {
      return false;
    }
    PhoneNumber pn = (PhoneNumber)o;
    return pn.lineNum == lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
  }

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
}
