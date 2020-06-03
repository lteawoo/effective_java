package kr.taeu.effectiveJava.item12;

/*
 * 포맷을 명시하지 않기로한 PhoneNumber 클래스
 */
public class PhoneNumber1 {
  private final short areaCode, prefix, lineNum;
  
  public PhoneNumber1(int areaCode, int prefix, int lineNum) {
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
    if (!(o instanceof PhoneNumber1)) {
      return false;
    }
    PhoneNumber1 pn = (PhoneNumber1)o;
    return pn.lineNum == lineNum && pn.prefix == prefix && pn.areaCode == areaCode;
  }
  
  /**
   * 이 전화번호의 문자열 표현을 반환한다.
   * 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
   * XXX는 지역코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
   * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
   * 
   * 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
   * 앞에서부터 0으로 채워나간다. 예컨대 가입자 번호가 123이라면
   * 전화번호의 마지막 네 문자는 "0123"이 된다.
   */
  @Override
  public String toString() {
    return String.format("%03d-%03d-%04d", this.areaCode, this.prefix, this.lineNum);
  }
}
