package kr.taeu.effectiveJava.item3;

/*
 * pubilc static final 필드 방식의 싱글턴
 */
public class Elvis1 {
  public static final Elvis1 INSTANCE = new Elvis1();
  private Elvis1() {}
  
  public void leaveTheBuilding() {
    System.out.println("hello singleton");
  }
}
