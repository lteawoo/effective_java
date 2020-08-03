package kr.taeu.effectiveJava.item37;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.taeu.effectiveJava.item37.Plant1.LifeCycle;

/*
 * 정원에 심은 식물들을 배열 하나로 관리하고 이들을 생애주기(한해살이, 여러해살이, 두해살이) 별로 묶어보자
 */
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
