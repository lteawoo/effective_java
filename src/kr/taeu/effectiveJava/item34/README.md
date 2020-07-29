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

Planet 열거 타입은 단순하지만 강력하다, 어떤 객체의 지구에서의 무게를 입력받아 여덟 행성에서의 무게를 출력하는 일을 다음처럼 짧은 코드로 작성할 수 있다.
```java
public class WeightTable {
  public static void main(String[] args) {
    double earthWeight = Planet.EARTH.surfaceGravity();
    double mass = earthWeight / Planet.EARTH.surfaceGravity();
    for (Planet p : Planet.values()) {
      System.out.printf("%s에서의 무게는 %f이다.\n", p, p.surfaceWeight(mass));
    }
  }
}
```
열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다. 값들은 선언된 순서로 저장된다. 각 열거 타입 값의 toString 메서드는 상수 이름을 문자열로 반환하므로 println과 printf로 출력하기에 좋다.
```java
// toString 기본결과
MERCURY에서의 무게는 3.704031이다.
VENUS에서의 무게는 8.870806이다.
EARTH에서의 무게는 9.801443이다.
MARS에서의 무게는 3.720667이다.
JUPITER에서의 무게는 24.794508이다.
SATERN에서의 무게는 10.443576이다.
URANUS에서의 무게는 8.868889이다.
NEPTUEN에서의 무게는 11.137022이다.
```
열거 타입에서 상수를 하나 제거하면 제거한 상수를 참조하지 않는 클라이언트에는 아무 영향이 없다. WeightTable 프로그램에서는 한줄이 줄어들 테고, 참조하고있는 프로그램이라면 상수를 참조하는 라인에서 컴파일 오류가 발생할 것이다.

열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private이나 package-private 메서드로 구현한다.

널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만들자. 예를 들어 소수 자릿수의 반올림 모드를 뜻하는 열거 타입인 java.math.RoundingMode는 BigDecimal이 사용한다. 그런데 반올림 모드는 BigDecimal과 관련 없는 영역에서도 유용한 개념이라 설계자는 RoundingMode를 톱레벨로 올렸다.

상수마다 동작이 달라져야 하는 상황이면? 예를들어 사칙 연산 계산기의 연산 종류를 열거 타입으로 선언하고, 실제 연산까지 열거 타입 상수가 직접 수행했으면 한다고 해보자.
```java
/*
 * switch로 값에 따라 분기하는 열거 타입 - 불만족
 */
public enum Operation1 {
  PLUS, MINUS, TIMES, DIVIDE;

  // 상수가 뜻하는 연산을 수행
  public double apply(double x, double y) {
    switch(this) {
    case PLUS: return x + y;
    case MINUS: return x - y;
    case TIMES: return x * y;
    case DIVIDE: return x / y;
    }
    throw new AssertionError("알 수 없는 연산: " + this);
  }
}
```
새로운 상수를 추가하면 case 문도 추가해야 하며, 혹시라도 깜빡하면 새로 추가한 연산에 대해 "알 수 없는 연산"이라는 런타임 오류를 내며 종료된다.

열거 타입은 상수 별로 다르게 동작하는 코드를 구현하는 더 나은 수단을 제공한다. 열거 타입에 apply라는 추상 메서드를 선언하고 각 상수별 클래스 몸체(constant-specific class body), 즉 상수에서 자신에 맞게 재정의하는 방법이다. 이를 상수별 메서드 구현(constant-specific method implementation)이라 한다.
```java
public enum Operation2 {
  PLUS { public double apply(double x, double y) { return x + y; }},
  MINUS { public double apply(double x, double y) { return x - y; }},
  TIMES { public double apply(double x, double y) { return x * y; }},
  DIVIDE { public double apply(double x, double y) { return x / y; }};

  public abstract double apply(double x, double y);
}
```
apply가 추상 메서드이므로 재정의하지 않았다면 컴파일 오류로 알려준다.

상수별 메서드 구현을 상수별 데이터와 결합할 수도 있다. 다음은 Operation의 toString을 재정의해 해당 연산을 뜻하는 기호를 반환하도록 한다.
```java
public enum Operation3 {
  PLUS("+") {
    public double apply(double x, double y) {
      return x + y;
    }
  },
  MINUS("-") {
    public double apply(double x, double y) {
      return x - y;
    }
  },
  TIMES("*") {
    public double apply(double x, double y) {
      return x * y;
    }
  },
  DIVIDE("/") {
    public double apply(double x, double y) {
      return x / y;
    }
  };

  private final String symbol;

  Operation3(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return symbol;
  }

  public abstract double apply(double x, double y);
}

// 위를 사용하는 소스 정말 편리하다.
public class Operation3_main {
  public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    for (Operation3 op : Operation3.values()) {
      System.out.printf("%f %s %f = %f\n", x, op, y, op.apply(x, y));
    }
  }
}
```
열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드가 자동 생성된다.

열거 타입의 toString 메서드를 재정의하려거든, toString이 반환하는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 함께 제공하는 걸 고려해보자.(단, 타입 이름을 적절히 바꿔야 하고 모든 상수의 문자열 표현이 고유해야 한다.)
```java
  private static final Map<String, Operation3> stringToEnum = Stream.of(values())
      .collect(toMap(Object::toString, e -> e));

  // 지정한 문자열에 해당하는 Operation을 반환한다.
  public static Optional<Operation3> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
  }
```
Operation 상수가 stringtoEnum 맵에 추가되는 시점은 열거 타입 상수 생성 후 정적 필드가 초기화될 때다. 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는 것은 상수 변수뿐이다. 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 아직 초기화되기 전이라, 자기 자신을 추가하지 못하게 하는 제약이 꼭 필요하다. 이 제약의 특수한 예로, 열거 타입 생성자에서 같은 열거 타입의 다른 상수에도 접근할 수 없다.

fromString이 Optional< Operation3>을 반환하는 점도 주의하자. 이는 주어진 문자열이 가리키는 연산이 존재하지 않음을 클라이언트에 알리고, 그 상황을 클라이언트에서 대처하도록 한 것이다.

## 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다는 단점이 있다.
급여명세서에서 쓸 요일을 표현하는 열거 타입을 예로 생각해보자. 이 열거 타입은 직원의 (시간당) 기본 임금과 그날 일한 시간(분 단위)이 주어지면 일당을 계산해주는 메서드를 갖고 있다. 주중에 오버타임이 발생하면 잔업수당이 주어지고, 주말에는 무조건 잔업수당이 주어진다.
```java
// 값에 따라 분기하여 코드를 공유하는 열거 타입, 좋은방법은 아니다.
public enum PayrollDay1 {
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

  private static final int MINS_PER_SHIFT = 8 * 60;

  int pay(int minutesWorked, int payRate) {
    int basePay = minutesWorked * payRate;

    int overtimePay;
    switch(this) {
      case SATURDAY: case SUNDAY: // 주말
        overtimePay = basePay / 2;
        break;
      default: // 주중
        overtimePay = minutesWorked <= MINS_PER_SHIFT ?
            0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
    }

    return basePay + overtimePay;
  }
}
```
간결하나, 관리 관점에서는 위험한 코드다. 휴가와 같은 새로운 값을 열거 타입에 추가하려면 그 값을 처리하는 case 문을 잊지 말고 쌍으로 넣어줘야 하는 것이다. 깜빡하면 휴가기간에 열심히 일해도 평일과 같은 임금을 받게 된다.

상수별 메서드 구현으로 급여를 정확히 계산하는 방법은 두 가지 이다.
1. 잔업수당을 계산하는 코드를 몯느 상수에 중복해서 넣는다.
2. 계산 코드를 평일용과 주말용으로 나눠 각각을 도우미 메서드로 작성한 다음 각 상수가 자신에게 필요한 메서드를 적절히 호출하면 된다.

두 방식 모두 코드가 장황해져 가독성이 떨어지고 오류 발생 가능성이 높아진다.

payrollDay에 평일 잔업수당 계산용 메서드인 overtimePay를 구현해놓고,주말 상수에서만 재정의해 쓰면 장황한 부분은 줄일수 있으나 새로운 상수를 추가했을 때 overtimePay 메서드를 재정의하지 않으면 switch 문과 같은 문제가 나타난다(평일용 코드를 그대로 물려받게 됨)

가장 깔끔한 방법은 새로운 상수를 추가할 때 잔업수당 '전략'을 선택하도록 하는 것이다. 잔업 수당 계산을 private 중첩 열거 타입(다음 코드의 PayType)으로 옮기고 PayrollDay 열거 타입의 생성자에서 이 중 적당한 것을 선택한다. 그러면 PayrollDay 열거 타입은 잔업수당 계산을 그 전략 열거 타입에 위임하여, switch 문이나 상수별 메서드 구현이 필요 없게 된다.
```java
// 전략 열거 타입 패턴
public enum PayrollDay2 {
  MONDAY(PayType.WEEKDAY),
  TUESDAY(PayType.WEEKDAY),
  WEDNESDAY(PayType.WEEKDAY),
  THURSDAY(PayType.WEEKDAY),
  FRIDAY(PayType.WEEKDAY),
  SATURDAY(PayType.WEEKEND),
  SUNDAY(PayType.WEEKEND);

  private final PayType payType;

  PayrollDay2(PayType payType) {
    this.payType = payType;
  }

  int pay(int minutesWorked, int payRate) {
    return payType.pay(minutesWorked, payRate);
  }

  // 전략 열거 타입
  enum PayType {
    WEEKDAY {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked <= MINS_PER_SHIFT ?
            0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;

      }
    },
    WEEKEND {
      int overtimePay(int minsWorked, int payRate) {
        return minsWorked * payRate / 2;
      }
    };
    abstract int overtimePay(int mins, int payRate);
    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minsWorked, int payRate) {
      int basePay = minsWorked * payRate;
      return basePay + overtimePay(minsWorked, payRate);
    }
  }
}
```
대부분의 열거 타입의 성능은 정수 상수와 별반 다르지 않다. 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감될 정도는 아ㅣㄴ다.

# 그래서 열거 타입은 언제 씀?
필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자. 태양계의 행성, 한 주의 요일, 체스 말처럼 본질적으로 열거 타입인 타입은 당연히 포함된다.

열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다. 열거 타입은 나중에 추가돼도 바이너리 수준에서 호환되도록 설계되었다.

# 결론
* 하나의 메서드가 상수별로 다르게 동작해야 한다면 상수별 메서드 구현을 사용하자
* 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자