package kr.taeu.effectiveJava.item19;

public class Super1 {
  // 잘못된 예 - 생성자가 재정의 가능 메서드를 호출했다.
  public Super1() {
    overrideMe();
  }
  
  public void overrideMe() {
    
  }
}
