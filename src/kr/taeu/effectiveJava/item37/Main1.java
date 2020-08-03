package kr.taeu.effectiveJava.item37;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * 정원에 심은 식물들을 배열 하나로 관리하고 이들을 생애주기(한해살이, 여러해살이, 두해살이) 별로 묶어보자
 */
// ordinal()을 배열 인덱스로 사용 - 절대 따라하지말자
public class Main1 {
    public static void main(String[] args) {
        Plant1[] garden = new Plant1[4];
        garden[0] = new Plant1("식물1", Plant1.LifeCycle.ANNUAL);
        garden[1] = new Plant1("식물2", Plant1.LifeCycle.BIENNIAL);
        garden[2] = new Plant1("식물3", Plant1.LifeCycle.PERENNIAL);
        garden[3] = new Plant1("식물4", Plant1.LifeCycle.BIENNIAL);
        
        Set<Plant1>[] plantsByLifeCycle = 
                (Set<Plant1>[]) new Set[Plant1.LifeCycle.values().length];  // 비검사 형변환
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }
        
        for (Plant1 p : garden) {
            plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
        }
        
        Arrays.asList(plantsByLifeCycle).stream()
            .forEach((cycle) -> System.out.println(cycle));
    }
}
