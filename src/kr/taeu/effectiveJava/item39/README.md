# 명명 패턴보다 애너테이션을 사용하라
전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다. 예컨대 테스트 프레임워크인 JUnit은 버전 3까지 테스트 메서드 이름을 test로 시작하게끔 했다. 효과적인 방법이지만 단점도 크다.
## 명명 패턴의 단점
1. 오타가 나면 안 된다. 실수로 이름을 tset...로 지으면 JUnit3는 이 메서드를 무시하고 지나간다. 개발자는 통과됐다고 착각할 수 있다.
2. 올바른 프로그램 요소에만 사용되리라 보증할 방법이 없다. 클래스 이름을 TestSafety...로 지어 JUnit에 던져줬다고 해보자 개발자는 이 클래스에 정의된 메서드들을 수행해주길 기대하겠지만 JUnit은 클래스 이름에는 관심이 없다.

## 대안 - 애너테이션
Test라는 이름의 애너테이션을 정의한다고 해보자. 자동을 수행되는 간단한 테스트용 애너테이션으로, 예외가 발생하면 해당 테스트를 실패로 처리한다.
```java
/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```
@Test 애너테이션 타입 선언 자차에도 다른 애너테이션이 달려 있다. 바로 @Retention과 @Target이다. 이처럼 애너테이션 선언에 다는 애너테이션을 메타애너테이션(meta-annotation)이라 한다.

@Retention(RetentionPolicy.RUNTIME) 메타애너테이션은 @Test가 런타임에도 유지되어야 한다는 표시이다. 만약 이 메타애너테이션을 생략하면 테스트 도구는 @Test를 인식할 수 없다.

@Target(ElementType.METHOD) 메타애너테이션은 @Test가 반드시 메서드 선언에서만 사용돼야 한다는 표시이다. 따라서 클래스 선언, 필드 선언 등 다른 프로그램 요소에는 사용될 수 없다.

이 @Test 애너테이션과 같이 아무런 매개변수 없이 단순히 대상에 마킹(Marking)한다 는 뜻에서 마커 애너테이션이라 한다. 이 애너테이션을 사용하면 프로그래머가 Test 이름에 오타를 내거나 메서드 선언 외의 프로그램 요소에 달면 컴파일 오류를 내준다.
```java
public class Sample1 {
    @Test
    public static void m1() { } // 성공해야 한다.
    public static void m2() { }
    @Test
    public static void m3() {   // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m4() { }
    @Test
    public void m5() { }        // 잘못 사용한 예: 정적 메서드가 아니다.
    @Test
    public static void m7() {   // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m8() { }
}
```
총 4개의 테스트메서드 중 m1만 성공, m3,m7은 실패, m5는 사용이 잘못되었다. 그리고 나머지는 테스트 도구가 무시 할 것이다.

@Test 애너테이션이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다. 그저 이 애너테이션에 관심 있는 프로그램에게 추가 정보를 제공할 뿐이다. 더 넓게 이야기하면, 대상 코드의 의미는 그대로 둔 채 그 애너테이션에 관심 있는 도구에게 특별한 처리를 할 기회를 준다.
```java
public class RunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패: " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d\n", passed, tests - passed);
    }
}
```
이 테스트 러너는 명령줄로부터 완전 정규화된 클래스 이름을 받아, 그 클래스에서 @Test 애너테이션이 달린 메서드를 차례로 호출한다. 테스트 메서드가 예외를 던지면 리플렉션 메커니즘이 InvocationTargetException으로 감싸서 다시 던진다. 그래서 이 프로그램은 InvocationTargetException을 잡아 원래 예외에 담긴 실패 정보를 추출해(getCause) 출력한다.

InvocationTargetException 외의 예외가 발생한다면 @Test 애너테이션을 잘못 사용했다는 뜻이다.
```
public static void kr.taeu.effectiveJava.item39.Sample1.m3() 실패: java.lang.RuntimeException: 실패
잘못 사용한 @Test: public void kr.taeu.effectiveJava.item39.Sample1.m5()
public static void kr.taeu.effectiveJava.item39.Sample1.m7() 실패: java.lang.RuntimeException: 실패
성공: 1, 실패: 3
```
이제 특정 예외를 던져야만 성공하는 테스트를 지원하도록 해보자
```java
/**
 * 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```
이 애너테이션의 매개변수 타입은 Class<? extends Throwable>이다. 여기서의 와일드카드 타입은 많은 의미를 담고 있다. "Throwable을 확장한 클래스의 Class 객체"라는 뜻이며, 따라서 모든 예외(와 오류) 타입을 다 수용한다. 이는 한정적 타입 토큰의 또 하나의 활용 사례다. 그리고 다음은 이 애너테이션을 실제 활용하는 못브이다. class 리터럴은 애너테이션 매개변수의 값으로 사용됐다.