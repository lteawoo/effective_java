# public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
인스턴스 필드들을 모아놓는 일 외에는 아무 목적도 없는 퇴보한 클래스를 작성하려 할 때가 있다.
```java
class Point {
    public double x;
    public double y;
}
```
이런 클래스는 데이터 필드에 직접 접근 할 수 있으니 캡슐화의 이점을 제공하지 못한다. API를 수정하지 않고는 내부 표현을 바꿀 수 없고, 불변식을 보장할 수 없으며, 외부에서 필드에 접근할 때 부수 작업을 수행할 수도 없다.
```java
/*
 * 접근자와 변경자(mutator) 메서드를 활용해 데이터를 캡슐화한다.
 */
public class Point2 {
  private double x;
  private double y;
  ㅋ
  public Point2(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public double getX() {
    return x;
  }
  
  public double getY() {
    return y;
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
}
```
철저한 객체지향 프로그래머는 위와 같이 필드들을 모두 private으로 바꾸고 public 접근자(getter)를 추가한다. public 클래스에서라면 이 방식이 확실히 맞다. 패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다.

하지만 package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출한다 해도 하등의 문제가 없다. 그 클래스가 표현하려는 추상 개념만 올바르게 표현해주면 된다. 이 방식은 클래스 선언 면에서나 이를 사용하는 클라이언트 코드 면에서나 접근자 방식보다 훨씬 깔끔하다.

public 클래스의 필드가 불변이라면 직접 노출할 때의 단점이 조금은 줄어들지만, 여전히 결코 좋은 생각은 아니다. 위에 설명한 단점은 여전히 유효하다.
```java
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

```