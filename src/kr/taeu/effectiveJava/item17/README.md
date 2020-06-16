# 변경 가능성을 최소화하라.
불변 클래스란 **인스턴스의 내부값을 수정할 수 없는 클래스**다. 불변 인스턴스의 정보는 고정되어 객체가 파괴되는 순간까지 절대 달라지지 않는다. 자바 플랫폼 라이브러리에도 다양한 불변 클래스가 있다. String, 기본 타입의 박싱된 클래스들, BigInteger, BigDecimal이 여기 속한다.

불변 클래스로 설계된 이유는 가변 클래스보다 설계와 구현, 사용이 쉬우며, 오류가 생길 여지가 적고 훨씬 안전하다.

## 불변 클래스 생성 규칙
* 객체의 상태를 변경하는 메서드(변경자-setter)를 제공하지 않는다.
* 클래스를 확장할 수 없도록 한다. 하위 클래스에서 부주의하게 혹은 나쁜 의도로 객체의 상태를 변하게 만드는 사태를 막아준다.
* 모든 필드를 final로 선언한다. 시스템이 강제하는 수단을 이용해 설계자의 의도를 명확히 드러내는 방법이다. 새로 생성된 인스턴스를 동기화 없이 다른 스레드로 건네도 문제없이 동작하게끔 보장하는 데도 필요하다.
* 모든 필드를 private로 선언한다. 필드가 참조하는 가변 객체를 클라이언트에서 직접 접근해 수정하는 일을 막아준다. public final로 선언하면 변경은 못하지만 다음 릴리스때 변경에 문제가 생긴다.
* 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. 클래스에 가변 객체를 참조하는 필드가 하나라도 있다면 클라이언트에서 그 객체의 참조를 얻을 수 없도록 해야 한다.

```java
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
```
복소수를 표현하는 불변 클래스이다. 실수부와 허수부 값을 반환하는 접근자 메서드, 사칙연산 메서드 그리고 Object의 메서드 몇 개를 재정의 했다. 위의 사칙연산 메서드들처럼 피연산자에 함수를 적용해 그 결과를 반환하지만, 피연산자 자체는 그대로인 프로그래밍 패턴을 함수형 프로그래밍이라고 한다. 또한 메서드 이름이 add같은 동사 대신 plus같은 전치사를 사용한 이유는 해당 메서드가 값을 변경하지 않는다는 사실을 강조하려는 의도다. BigInteger와 BigDecimal에선 이러한 명명규칙을 따르지 않아 많은사라이 잘못사용해 오류를 발생하는 일이 자주 있다.

함수형 프로그래밍 방식(위의 방식)으로 프로그래밍하면 불변이 되는 영역의 비율이 높아지는 장점을 누릴 수 있다. 불변 객체는 단순하다. 모든 생성자가 클래스 불변식(class invariant)을 보장한다면 그 클래스를 사용하는 프로그래머가 다른 노력을 들이지 않더라도 영원히 불변으로 남는다.

불변 객체는 근본적으로 스레드 세이프하다. 그리하여 동기화할 필요가 없다. 여러 스레드가 동시에 사용하더라도 절대 훼손되지 않는다. 따라서 불변 객체는 안심하고 공유 할 수 있다. 그러므로 불변 클래스라면 한번 만든 인스턴스를 최대한 재활용하기를 권한다.

가장 쉬운 재활용 방법은 자주 쓰이는 값들을 상수(public static final)로 제공하는 것이다. 예컨대 Complex 클래스는 다음 상수들을 제공할 수 있다.
```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```
**불변 클래스는 정적 팩터리 방식으로 제공할 수 있다.** 박싱된 기본 타입 클래스 전부와 BigInteger가 여기 속한다. 이런 정적 팩터리를 사용하면 여러 클라이언트가 인스턴스를 공유하여 메모리사용량과 GC 비용이 줄어든다. 새로운 클래스를 설계할 때 public 생성자 대신 정적 팩터리를 만들어두면, 클라이언트를 수정하지 않고도 필요에 따라 캐시 기능을 나중에 덧붙일 수 있다.

**불변 객체를 자유롭게 공유할 수 있다는 점은 방어적 복사도 필요 없다는 결론으로 자연스럽게 이어진다.** 그러니 clone 메서드나 복사 생성자를 제공하지 않는 것이 좋다. String 클래스의 복사 생성자는 이 사실을 잘 이해하지 못한 자바 초창기 때 만들어진 것으로, 되도록 사용하지 말자

**불변 객체끼리는 내부 데이터를 공유할 수 있다.** 예컨대 BigInteger 클래스는 내부에서 값의 부호(sign)와 크기(magnitude)를 따로 표현한다. 부호에는 int 변수를, 크기(절댓값)에는 int 배열을 사용하는 것이다. 한편 negate 메서드는 크기가 같고 부호만 반대인 새로운 BigInteger를 생성하는데, 이때 배열은 비록 가변이지만 복사하지 않고 원본 인스턴스와 공유하여서, 새로 만든 BigInteger 인스턴스도 원본 인스턴스가 가리키는 내부 배열을 그대로 가리킨다.

**객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다.** 불변요소로 이루어진 객체라면 구조가 아무리 복잡하여도 불변식을 유지하기 훨씬 수월하다. 좋은 예로, 불변 객체는 맵의 키나, 집합(Set)의 원소로 쓰기에 안성맞춤이다. 맵이나 집합은 안에 담긴 값이 바뀌면 불변식이 허물어지는데, 불변 객체를 사용하면 그런 걱정은 하지 않아도 된다.

불변 객체는 그 자체로 실패 원자성(failure atomicity: 메서드에서 예외가 발생한 후에도 그 객체는 여전히 전과 같은 유효한 상태여야 한다는 성질) 을 제공한다.

불변 클래스에도 단점은 있다. 값이 다르면 반드시 독립된 객체로 만들어야 한다는 것이다. 값의 가짓수가 많다면 이들을 모두 만드는 데 큰 비용을 치러야 한다. 예컨대 백만 비트짜리 BigInteger에서 비트 하나를 바꿔야 한다고 해보자.
```java
BigInteger moby = ...;
moby = moby.flipBit(0);
```
flipBit 메서드는 새로운 BigInteger 인스턴스를 생성한다. 원본과 단지 한 비트만 다른 백만 비트짜리 인스턴스를 말이다. 이 문제를 대처하는 방법은 두 가지다. 첫 번째는 흔히 쓰일 다단계 연산(multistep operation)들을 예측하여 기본 기능으로 제공하는 방법이다. 이러한 다단계 연산을 기본으로 제공한다면 더 이상 각 단계마다 객체를 생성하지 않아도 된다. 예컨대 BigInteger는 모듈러 지수 같은 다단계 연산 속도를 높여주는 가변 동반 클래스(companion class)를 package-private으로 두고 있다. 앞서 이야기한 이유들로, 이 가변 동반 클래스를 사용하기란 BigInteger를 쓰는 것보다 훨씬 어렵다. 그래도 BigInteger가 어려운 부분을 모두 처리해주고 있다.

클라이언트들이 원하는 복잡한 연산들을 정확히 예측할 수 있다면 package-private의 가변 동반 클래스만으로 충분하다. 자바 플랫폼 라이브러리에서 이에 해당하는 대표적인 예가 바로 String이다. String의 가변 동반 클래스는 StringBuilder(와 과거의 StringBuffer)다.

## 불변 클래스를 만드는 또다른 방법
클래스가 불변임을 보장하려면 자신을 상속하지 못하게 해야한다. 가장 쉬운 방법은 클래스를 final 클래스로 선언하는 것이지만 더 유연한 방법이 있다. **모든 생성자를 private 혹은 package-private으로 만들고 public 정적 팩터리를 제공하는 방법**이다.
```java
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
  ...
```
패키지의 바깥의 클라이언트에서 바라본 이 불변 객체는 사실상 final이다. public이나 protected 생성자가 없으니 다른 패키지에서는 이 클래스를 확장하는게 불가능하기 때문이다.

BigInteger와 BigDecimal을 설계할 당시엔 불변 객체가 사실상 final이어야 한다는 생각이 널리 퍼지지 않았다. 그래서 이 두 클래스의 메서드들은 모두 재정의할 수 있게 설계되었고, 하위 호환성이 아직까지도 발목을 잡아 이 문제를 고치지 못했다. 그러니 신뢰할 수 없는 클라이언트로부터 BigInteger나 BigDecimal의 인스턴스를 인수로 받는다면 주의해야 한다. 이 값들이 불변이어야 클래스의 보안을 지킬 수 있다면 인수로 받은 객체가 진짜 BigInteger나 BigDecimal인지 반드시 확인해야 한다. 신뢰할 수 없는 하위 클래스의 인스턴스라고 확인되면 가변이라고 확정하고 방어적 복사해 사용해야 한다.
```java
public static BigInteger safeInstance(BigInteger val) {
    reutrn val.getClass() == BigInteger.class ?
        val : new BigInteger(val.toByteArray());
}
```
불변 클래스의 생성 규칙에 따르면 모든 필드가 final이고 어떤 메서드도 그 객체를 수정할 수 없어야 한다고 적혀있다. 이 규칙은 살짝 과한감이 있어서 다음과 같이 살짝 완화할 수 있다. **어떤 메서스도 객체의 상태 중 외부에 비치는 값을 변경할 수 없다.**

어떤 불변 클래스는 계산 비용이 큰 값을 나중에 (처음에 쓰일 때) 계산하여 final이 아닌 필드에 캐시 해놓기도 한다. 똑같은 값을 다시 요청하면 캐시해둔 값을 반환하여 계산 비용을 절감하는 것이다. 객체가 불변이기에 부릴수 있는 묘수이다

예컨대 PhoneNumber의 hashCode 메서드는 처음 불렸을 때 해시 값을 계산해 캐시한다. 지연 초기화의 예이기도 한 이 기법을 String도 사용한다.