# ordinal 인덱싱 대신 EnumMap을 사용하라
식물을 간단히 나타낸 다음 클래스를 보자
```java
public class Plant1 {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    final String name;
    final LifeCycle lifeCycle;

    Plant1 (String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString () {
        return name;
    }
}
```
이제 이 식물들을 배열 하나로 관리하고, 이들을 생애주기(한해살이, 두해살이, 여러해살이)별로 묶어보자.
```java
// ordinal()을 배열 인덱스로 사용 - 절대 따라하지말자
public class Main1 {
    public static void main(String[] args) {
        Plant1[] garden = new Plant1[3];
        garden[0] = new Plant1("식물1", Plant1.LifeCycle.ANNUAL);
        garden[1] = new Plant1("식물2", Plant1.LifeCycle.BIENNIAL);
        garden[1] = new Plant1("식물2", Plant1.LifeCycle.PERENNIAL);
        garden[2] = new Plant1("식물3", Plant1.LifeCycle.BIENNIAL);

        Set<Plant1>[] planetsByLifeCycle =
                (Set<Plant1>[]) new Set[Plant1.LifeCycle.values().length];  // 비검사 형변환
        for (int i = 0; i < planetsByLifeCycle.length; i++) {
            planetsByLifeCycle[i] = new HashSet<>();
        }

        for (Plant1 p : garden) {
            planetsByLifeCycle[p.lifeCycle.ordinal()].add(p);
        }

        Arrays.asList(planetsByLifeCycle).stream()
            .forEach((cycle) -> System.out.println(cycle));
    }
}
```
동작은 하지만 문제가 한가득이다. 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 하고 깔끔히 컴파일되지 않을 것이다. 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다. 가장 심각한 문제는 정확한 정숫값을 사용한다는 것을 개발자가 보증해야한다.

여기서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다. 그러니 Map을 사용할 수 도 있을 것이다. EnumMap을 사용하면 열거 타입을 키로 사용하도록 설계되었으니 빠르게 작동한다.
```java
// EnumMap을 사용해 데이터와 열거타입을 매핑한다.
public class Main2 {
    public static void main(String[] args) {
        Plant1[] garden = new Plant1[4];
        garden[0] = new Plant1("식물1", Plant1.LifeCycle.ANNUAL);
        garden[1] = new Plant1("식물2", Plant1.LifeCycle.BIENNIAL);
        garden[2] = new Plant1("식물3", Plant1.LifeCycle.PERENNIAL);
        garden[3] = new Plant1("식물4", Plant1.LifeCycle.BIENNIAL);

        Map<Plant1.LifeCycle, Set<Plant1>> plantsByLifeCycle =
                new EnumMap<>(Plant1.LifeCycle.class);
        for (Plant1.LifeCycle lc : Plant1.LifeCycle.values()) {
            plantsByLifeCycle.put(lc, new HashSet<>());
        }

        for (Plant1 p : garden) {
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        }

        System.out.println(plantsByLifeCycle);
    }
}
```
더 짧고 명료하고 안전하고 성능도 원래 버전과 비등하다.

스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다. 다음은 앞 예의 동작을 거의 모방한 가장 단순한 형태의 스트림 기반 코드다.
```java
// Stream기반코드
public class Main3 {
    public static void main(String[] args) {
        Plant1[] garden = new Plant1[4];
        garden[0] = new Plant1("식물1", Plant1.LifeCycle.ANNUAL);
        garden[1] = new Plant1("식물2", Plant1.LifeCycle.BIENNIAL);
        garden[2] = new Plant1("식물3", Plant1.LifeCycle.PERENNIAL);
        garden[3] = new Plant1("식물4", Plant1.LifeCycle.BIENNIAL);

        Stream<Plant1> stream = Arrays.stream(garden);
        Function<Plant1 ,Plant1.LifeCycle> classifier = p -> p.lifeCycle; // Plant1를 Plant1.LifeCylce로 매핑 매개값을 리턴값으로 매핑하는 역할
        // Collector<T, A, R> T를 A에 담아서 R로 반환
        Collector<Plant1, ?, Map<Plant1.LifeCycle, Set<Plant1>>> collector = Collectors.groupingBy(classifier,
                () -> new EnumMap<>(LifeCycle.class),
                Collectors.toSet());
        System.out.println(stream.collect(collector));

        System.out.println(Arrays.stream(garden)
                .collect(Collectors.groupingBy(p -> p.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class),
                        Collectors.toSet())));
    }
}
```
Collectors.groupingBy 메서드는 mapFatory 매개변수에 원하는 맵 구현체를 명시해 호출 할 수 있다.

두 열거 타입 값들을 매핑하느라 ordinal을 (두번이나) 쓴 배열들의 배열을 본 적이 있을 것이다. 다음은 이방식을 적용해 두 가지 상태(Phase)를 전이(Transition)와 매핑하도록 구현한 것이다.
```java
public enum Phase1 {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT, SOLID, LIQUID, GAS;

        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
        private static final Transition[][] TRANSITIONS = {
            { null, MELT, SUBLIME },
            { FREEZE, null, BOIL },
            { DEPOSIT, CONDENSE, null }
        };

        // 한 상태에서 다른 상태로의 전이를 반환한다.
        public static Transition from (Phase1 from, Phase1 to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```
앞의 예제와 마찬가지로 컴파일러는 ordinal과 배열 인덱스의 관계를 알 도리가 없다. 즉, Phase나 Phase.Transition 열거 타입을 수정하면서 상전이 표 TRANSITIONS를 함께 수정 하지 않거나 실수로 잘못 수정하면 런타임 오류가 날 것이다.

EnumMap을 사용해보자, 이전 상태와 이후 상태가 필요하니, 맵 2개를 중첩하면 쉽게 해결 할 수 있다.
```java
public enum Phase2 {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase2 from;
        private final Phase2 to;

        Transition(Phase2 from, Phase2 to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화
        private static final Map<Phase2, Map<Phase2, Transition>> m = Stream.of(values())
                .collect(Collectors.groupingBy(t -> t.from,
                        () -> new EnumMap<>(Phase2.class),
                        Collectors.toMap(t -> t.to,
                                t -> t,
                                (x, y) -> y,
                                () -> new EnumMap<>(Phase2.class))));

        public static Transition from(Phase2 from, Phase2 to) {
            return m.get(from).get(to);
        }
    }
}
```
이 맵의 타임인 Map<Phase2, Map<Phase2, Transition>>은 "이전 상태에서 '이후 상태에서 전이로의 맵'에 대응시키는 맵"이라는 뜻이다.

이제 여기에 PLASMA를 추가해보자. 이 상태와 연결된 전이는 2개다. 첫 번째는 기체에서 플라스마로 변하는 이온화(IONIZE)이고, 둘째는 플라스마에서 기체로 변하는 탈이온화(DEIONIZE)다.
```java
public enum Phase3 {
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
...
```
