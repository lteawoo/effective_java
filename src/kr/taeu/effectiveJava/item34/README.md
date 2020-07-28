# int 상수 대신 열거 타입을 사용하라
열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다. 열거 타입을 지원하기 전에는 정수 상수를 한 묶음 선언해서 사용하곤 했다.
```java
// 정수 열거 패턴 - 취약하다.
public static final int APPLE_FUJI          = 0;
public static final int APPLE_PIPPIN        = 1;
public static final int APPLE_GRANNY_SMITH  = 2;

public static final int ORANGE_NAVEL        = 0;
public static final int ORANGE_TEMPLE       = 1;
public static final int ORANGE_BLOOD        = 2;
```
## 정수 열거 패턴 기법에는 단점이 많다.
* 타입 안전을 보장할 방법이 없으며 표현력도 좋지 않다. 오렌지를 건네야 할 메서드에 사과를 보내고 동등 연산자(==)로 비교하더라도 컴파일러는 아무런 경고를 출력하지 않는다.
* 이름 충돌 방지를 접두어를 써서 해결한다. 자바는 별도 이름공간을 지원하지 않기 때문이다.(정수 열거 패턴을 위한)
* 정수 상수는 문자열로 출력하기 까다롭다. 그 값을 출력하거나 디버거로 살펴보면 단지 숫자로만 보여서 썩 도움이 되지 않는다.
* 순회하는 방법도 마땅치 않다.

java의 대안은 열거 타입(enum type)이다.
## Enum Type
```java
// 가장 단순한 열거 타입
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```
자바 열거 타입은 완전한 형태의 클래스(단순한 정숫값일 뿐인)라서 다른 언어의 열거 타입보다 강력하다.

열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다. 열거 타입은 밖에서 접근할수 있는 생성자를 제공하지 않으므로 사실상 final이다. 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다. 싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있고, 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다.

열거 타입은 컴파일타임 타입 안정성을 제공한다. 위 코드의 Apple의 열거타입을 매개변수로 받는 메서드를 선언했다면, 건네받는 참조는 (null이 아니라면) Apple의 세 가지 값 중 하나임이 확실하다. 다른 타입의 값을 넘기려 하면 컴파일 오류가 난다. 타입이 다른 열거 타입 변수에 할당하려 하거나 다른 열거 타입의 값끼리 == 연산자로 비교하려는 꼴이기 때문이다.

열거 타입에는 각자의 이름공간이 있어서 이름이 같은 상수도 평화롭게 공존한다. 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다. 공개되는 것이 오직 이름뿐이라, 정수 열거 패턴과 달리 상수 값이 클라이언트로 컴파일되어 각인되지 않기 때문이다.

열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.
## 열거 타입에 메서드나 필드를 추가하는건 뭐를 의미하고 어떨 때 필요한 기능일까?
열거 타입에는 어떤 메서드도 추가할 수 있다, 가장 단순하게는 그저 상수 모임일 뿐이지만, 고차원의 추상 개념 하나를 완벽히 표현해낼 수도 있다.

태양계의 여덟 행성은 거대한 열거 타입을 설명하기에 좋은 예다. 각 행성에는 질량과 반지름이 있고, 이 두 속성을 이용해 표면중력을 계산할 수 있다. 따라서 어떤 객체의 질량이 주어지면 그 객체가 행성 표면에 있을 때의 무게도 계산할 수 있다.
```java
public enum Planet {
  // 데이터와 메서드를 갖는 열거 타입
  MERCURY(3.302e+23, 2.439e6),
  VENUS (4.869e+24, 6.052e6),
  EARTH (5.975e+24, 6.378e6),
  MARS (6.419e+23, 3.393e6),
  JUPITER (1.899e+27, 7.149e7),
  SATERN (5.685e+26, 6.027e7),
  URANUS (8.683e+25, 2.556e7),
  NEPTUEN (1.024e+26, 2.477e7);

  private final double mass;            // 질량 (단위: 킬로그램)
  private final double radius;          // 반지름 (단위: 미터)
  private final double surfaceGravity;  // 표면중력 (단위: m / s^2)

  // 중력상수(단위: m^3 / kg s^2)
  private static final double G = 6.67300E-11;

  // 생성자
  Planet(double mass, double radius) {
    this.mass = mass;
    this.radius = radius;
    surfaceGravity = G * mass / (radius * radius);
  }

  public double mass() {
    return mass;
  }

  public double radius() {
    return radius;
  }

  public double surfaceGravity() {
    return surfaceGravity;
  }

  public double surfaceWeight(double mass) {
    return mass * surfaceGravity; // F = ma
  }
}
```
거대한 열거 타입을 만드는 방법은 **열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.** 열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다. 필드를 public 으로 선언해도 되지만, private으로 두고 별도의 public 접근자 메서드를 두는 게 낫다.

