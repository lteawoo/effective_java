# 톱레벨 클래스는 한 파일에 하나만 담으라
소스 파일 하나에 톱레벨 클래스를 여러 개 선언하더라도 자바 컴파일러는 불평하지 않는다. 하지만 아무런 득이 없을 뿐더러 심각한 위험을 감수해야 하는 행위다. 이렇게 하면 한 클래스를 여러 가지로 정의할 수 있으며, 그중 어느 것을 사용할지는 어느 소스 파일을 먼저 컴파일하냐에 따라 달라지기 때문이다. 

구체적인 예를 보자, 다음 소스 파일은 Main 클래스 하나를 담고 있고, Main 클래스는 다른 톱레벨 클래스 2개(Utensil과 Dessert)를 참조한다.
```java
public class Main {
  public static void main(String[] args) {
    System.out.println(Utensil.NAME + Dessert.NAME);
  }
}
```

Utensil와 Dessert 클래스가 Utensil.java라는 한 파일에 정의되어 있다고 해보자.
```java
class Utensil {
  static final String NAME = "pan";
}

class Dessert {
  static final String NAME = "cake";
}
```
Main을 실행하면 pancake를 출력한다.
이제 우연히 똑같은 두 클래스를 담은 Dessert.java라는 파일을 만들었다고 해보자.
```java
class Utensil {
  static final String NAME = "pot";
}

class Dessert {
  static final String NAME = "pie";
}
```
javac Main.java Dessert.java 명령어로 컴파일하면 오류가 나고 Untensil과 Dessert가 중복정의 되었다고 알려줄 것이다. javac Main.java나 javac Main.java Untensil.java 명령으로 컴파일하면 pancake를 출력할 것이다. 그러나 javac Dessert.java Main.java 명령으로 컴파일하면 potpie를 출력한다. 이처럼 컴파일러에 어느 소스 파일을 먼저 건네느냐에 따라 동작이 달라지므로 반드시 바로 잡아야 할 문제다.

해결책은 톱레벨 클래스를 서로 다른 파일로 분리하면 그만이다. 굳이 여러 톱 레벨 클래스를 한 파일로 담고 싶다면 정적 멤버 클래스를 사용하는 방법을 고민해볼 수 있다. 읽기 좋고, private으로 선언하면 접근 범위도 최소로 관리할 수 있기 때문이다. 다음 코드는 앞의 예를 정적 멤버 클래스로 바꿔본 예다.
```java
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

    private static class Utensil {
        static final String Name = "pan";
    }

    private static class Dessert {
        static final String Name = "cake";
    }
}
```