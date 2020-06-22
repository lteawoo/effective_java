package kr.taeu.effectiveJava.item19;

import java.time.Instant;

public class Sub1 extends Super1{
  // 초기화되지 않은 final 필드, 생성자에서 초기화한다.
  private final Instant instant;
  
  Sub1() {
    instant = Instant.now();
  }
  
  // 재정의 가능 메서드. 상위 클래스의 생성자가 호출한다.
  @Override
  public void overrideMe() {
    System.out.println(instant);
  }
  
  public static void main(String[] args) {
    Sub1 sub = new Sub1();
    sub.overrideMe();
  }
}
