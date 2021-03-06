package kr.taeu.effectiveJava.item42;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// 상수별 클래스 몸체의 데이터를 사용한 열거 타입
public enum Operation1 {
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
  
  Operation1(String symbol) {
    this.symbol = symbol;
  }
  
  private static final Map<String, Operation1> stringToEnum = Stream.of(values())
      .collect(toMap(Object::toString, e -> e));
  
  // 지정한 문자열에 해당하는 Operation을 반환한다.
  public static Optional<Operation1> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
  }
  
  @Override
  public String toString() {
    return symbol;
  }
  
  public abstract double apply(double x, double y);
}
