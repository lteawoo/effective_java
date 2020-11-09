# 가변인수는 신중히 사용하라
가변인수(varargs) 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다. 가변 인수 메서드를 호출하면, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고 인수들을 이 배열에 저장하여 가변인수 메서드에 건네준다.
```java
// 간단한 가변인수 활용 예
static int sum(int... args) {
    int sum = a;
    for (int arg : args) {
        sum += arg;
    }
    return sum;
}
```
인수가 1개 이상이어야 할 때도 있다. 예컨대 최솟값을 찾는 메서드인데 인수를 0개만 받을 수 도 있도록 설계하는 건 좋지 않다. 인수 개수는 런타임에 (자동 생성된) 배열의 길이로 알 수 있다.
```java
// 인수가 1개 이상이어야 하는 가변인수 메서드 - 잘못 구현한 예!
static int min(int... args) {
    if (args.length == 0) {
        throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
    }

    int min = args[0];
    for (int i = 1; i < args.length; i++) {
        if (args[i] < min) {
            min = args[i];
        }

        return min;
    }
}
```
위 코드는 문제가 있다. 인수를 0개만 넣어 호출하면 런타임에 실패한다.(컴파일에는 통과) 코드도 지저분하다. args 유효성 검사를 명시적으로 해야 하고, min의 초깃값을 Integer.MAX_VALUE로 설정하지 않고는 for-each 문도 사용할 수 없다.

다행히 더 나은 방법은 있다. 매개변수를 2개 받오록하면 된다.
```java
// 인수가 1개 이상이어야 할때 가변인수를 제대로 사용하는 방법
static int min(int firstArg, int... remainingArgs) {
    int min = firstArg;

    for (int arg : remainingArgs) {
        if (arg < min) {
            min = arg;
        }

        return min;
    }
}
```
위처럼 가변인수는 인수의 개수가 정해지지 않았을 때 유용하다, printf는 가변인수와 한 묶음으로 자바에 도입되었고, 이때 핵심 리플렉션도 재정비되었다.

성능에 민감하다면 가변인수가 걸림돌이 될 수 있다. **가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화**한다. 다행히 이 비용을 감당할 수는 없지만, 가변인수의 유연성이 필요할 때 선택할 수 있는 멋진 패턴이 있다.

예를 들어 해당 메서드 호출의 95%가 인수를 3개 이하로 사용한다고 해보자, 그렇다면 다음처럼 인수가 0개인 것부터 4개인 것까지, 총 5개를 다중 정의해보자. 마지막 다중정의 메서드가 인수 4개 이상인 5%의 호출을 담당하는 것이다.

```java
public void foo() { }
public void foo(int a1) { }
public void foo(int a1, int a2) { }
public void foo(int a1, int a2, int a3) { }
public void foo(int a1, int a2, int a3, int... rest) { }
```
따라서 메서드 호출 중 단 5%만이 배열을 생성한다. 대다수의 성능 최적화와 마찬가지로 이 기법도 보통 때는 별 이득이 없지만, 꼭 필요한 특수 상황에서 오아시스가 될 것이다.

EnumSet의 정적 팩터리도 이 기법을 사용해 열거 타입 집합 생성 비용을 최소화한다. EnumSet은 비트 필드를 대체하면서 성능까지 유지해야 하므로 아주 적절하게 활용한 예다.