package kr.taeu.effectiveJava.item17;

// 불변 복소수 클래스
public class Complex1 {
  private final double re;
  private final double im;
  
  public Complex1(double re, double im) {
    this.re = re;
    this.im = im;
  }
  
  // 실수부와 허수부 값을 반환하는 접근자 메서드
  public double realPart()    { return re; }
  public double imaginaryPart() { return im; }
  
  /*
   * 새로운 Complex 인스턴스를 만들어 반환하는 사칙연산 메서드들
   * 이처럼 피연산자에 함수를 적용해 그 결과를 반환하지만, 피연산자 자체는 그대로인 프로그래밍 패턴을 함수형 프로그래밍이라고 한다.
   * 또한 메서드 이름이 add같은 동사 대신 plus같은 전치사를 사용한 이유는 해당 메서드가 값을 변경하지 않는다는 사실을 강조하려는 의도다.
   * BigInteger와 BigDecimal에선 이러한 명명규칙을 따르지 않아 많은사라이 잘못사용해 오류를 발생하는 일이 자주 있다.
   */
  
  public Complex1 plus(Complex1 c) {
    return new Complex1(re + c.re, im + c.im);
  }
  
  public Complex1 minus(Complex1 c) {
    return new Complex1(re - c.re, im - c.im);
  }
  
  public Complex1 times(Complex1 c) {
    return new Complex1(re * c.re - im * c.im,
         re * c.im - im * c.re);
  }
  
  public Complex1 dividedBy(Complex1 c) {
    double tmp = c.re * c.re + c.im * c.im;
    return new Complex1((re * c.re + im * c.im) / tmp,
        (im * c.re - re * c.im) / tmp);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Complex1)) {
      return false;
    }
    Complex1 c = (Complex1) o;
    
    // == 대신 compare를 사용하는 이유는? -> 특별한 부동소수값을 다뤄야하기 때문
    return Double.compare(c.re, re) == 0 && Double.compare(c.im,  im) == 0;
  }
  
  @Override
  public int hashCode() {
    return 31 * Double.hashCode(re) + Double.hashCode(im);
  }
  
  @Override
  public String toString() {
    return "(" + re + " + " + im + "i)";
  }
}
