package kr.taeu.effectiveJava.item37;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Phase3 {
    SOLID, LIQUID, GAS, PLASMA;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

        private final Phase3 from;
        private final Phase3 to;

        Transition(Phase3 from, Phase3 to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화
        private static final Map<Phase3, Map<Phase3, Transition>> m = Stream.of(values())
                .collect(Collectors.groupingBy(t -> t.from,
                        () -> new EnumMap<>(Phase3.class),
                        Collectors.toMap(t -> t.to,
                                t -> t,
                                (x, y) -> y,
                                () -> new EnumMap<>(Phase3.class))));

        public static Transition from(Phase3 from, Phase3 to) {
            return m.get(from).get(to);
        }
    }
}
