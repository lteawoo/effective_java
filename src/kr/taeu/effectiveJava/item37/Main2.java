package kr.taeu.effectiveJava.item37;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * 정원에 심은 식물들을 배열 하나로 관리하고 이들을 생애주기(한해살이, 여러해살이, 두해살이) 별로 묶어보자
 */
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
