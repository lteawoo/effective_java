package kr.taeu.effectiveJava.item13;

/*
 * 가변 상태를 참조하지 않는 클래스용 clone 메서드
 */
public class PhoneNumber1 implements Cloneable{
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

  @Override
  public String toString() {
    return String.format("%03d-%03d-%04d", this.areaCode, this.prefix, this.lineNum);
  }
  
  /*
   * 원본 필드와 똑같은 값을갖는 복제본을 반환한다.
   */
  @Override
  public PhoneNumber1 clone() {
	  try {
		  return (PhoneNumber1) super.clone();
	  } catch (CloneNotSupportedException e) {
		  throw new AssertionError(); // 일어날 수 없는 일이다.
	  }
  }
}
