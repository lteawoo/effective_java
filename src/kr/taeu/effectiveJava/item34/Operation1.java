package kr.taeu.effectiveJava.item34;
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
