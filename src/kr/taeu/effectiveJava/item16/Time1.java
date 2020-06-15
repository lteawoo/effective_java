package kr.taeu.effectiveJava.item16;

/*
 * 불변 필드를 노출한 public 클래스
 * 여전히 단점은 유효하다(부수작을 수행 못한다, api를 변경하지 않고는 표현 방식을 변경할 수 없다 등)
 */
public class Time1 {
  private static final int HOURS_PER_DAY = 24;
  private static final int MINUTES_PER_HOURS = 60;
  
  public final int hour;
  public final int minute;
  
  public Time1(int hour, int minute) {
    if (hour < 0 || hour >= HOURS_PER_DAY) {
      throw new IllegalArgumentException("시간: " + hour);
    }
    if (minute < 0 || minute >= MINUTES_PER_HOURS) {
      throw new IllegalArgumentException("분: " + minute);
    }
    
    this.hour = hour;
    this.minute = minute;
  }
}
