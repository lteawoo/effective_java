# @Override 애너테이션을 일관되게 사용하라
자바가 기본으로 제공하는 애너테이션 중 보통의 프로그래머에게 가장 중요한 것은 @Override일 것이다. @Override는 메서드 선언에만 달 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의했음을 듯한다. 이 애너테이션을 일관되게 사용하면 여러 가지 악명 높은 버그들을 예방해준다.
```java
// 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
public class Bigram1 {
    private final char first;
    private final char second;

    public Bigram1(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 재정의가 아닌 오버로딩(다중정의)하였다. equals를 오버라이딩하려면 Object를 매개변수로 받아야한다.
    public boolean equals(Bigram1 b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
    // 그 결과가 260이다.
    public static void main(String[] args) {
        Set<Bigram1> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram1(ch, ch));
            }
            System.out.println(s.size());
        }
    }
}

```
equals를 오버라이딩이 아닌 오버로딩하여 같은 소문자를 소유한 바이그램 10개가 서로 다른 객체로 인식되고 결국 260을 출력하게 된다.

@Override 애너테이션을 달고 다시 컴파일하면 다음의 컴파일 오류가 발생한다.
```java
Method does not override method from its superclass
```
```java
// 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
public class Bigram2 {
    private final char first;
    private final char second;

    public Bigram2(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 올바르게 애너테이션을 표시하고, 오버라이딩으로 수정하였다.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bigram2)) {
            return false;
        }
        Bigram2 b = (Bigram2) o;
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
    // 그 결과가 26이다.
    public static void main(String[] args) {
        Set<Bigram2> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram2(ch, ch));
            }
            System.out.println(s.size());
        }
    }
}
```
올바르게 수정하여 올바른 결과를 출력시켰다.

상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자. 예외는 한 가지 뿐이다. 구체 클래스에서 상위 클래스의 추상메서드를 재정의 할때는 굳이 @Override를 달지 않아도 된다. 물론 재정의 메서드 모두에 @Override를 일괄로 붙여두는 게 좋아 보인다면 그래도 상관없다.

인터페이스 메서드를 구현한 메서드에도 @Override를 다는 습관을 들이면 시그니처가 올바른지 재차 확신할 수 있다. 구현하려는 인터페이스에 디폴트 메서드가 없음을 안다면 이를 구현한 메서드에서는 @Override를 생략해 코드를 조금 더 깔끔히 유지해도 좋다.