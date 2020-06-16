package kr.taeu.effectiveJava.item17;

/*
 * 생성자 대신 정적 팩터리를 사용한 불변 클래스
 * 자신을 상속하지 못하게 하는 유연한 방법
 */
public class Complex2 {
  private final double re;
  private final double im;
  
  // 1.생성자를 private으로 둔다.
  private Complex2(double re, double im) {
    this.re = re;
    this.im = im;
  }
  
  // 2.public 정적 팩터리를 제공한다.
  public static Complex2 valueOf(double re, double im) {
    return new Complex2(re, im);
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
  
  public Complex2 plus(Complex2 c) {
    return new Complex2(re + c.re, im + c.im);
  }
  
  public Complex2 minus(Complex2 c) {
    return new Complex2(re - c.re, im - c.im);
  }
  
  public Complex2 times(Complex2 c) {
    return new Complex2(re * c.re - im * c.im,
         re * c.im - im * c.re);
  }
  
  public Complex2 dividedBy(Complex2 c) {
    double tmp = c.re * c.re + c.im * c.im;
    return new Complex2((re * c.re + im * c.im) / tmp,
        (im * c.re - re * c.im) / tmp);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Complex2)) {
      return false;
    }
    Complex2 c = (Complex2) o;
    
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
