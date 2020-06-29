package kr.taeu.effectiveJava.item22;

import static kr.taeu.effectiveJava.item22.PhysicalConstrants2.*;

/*
 * 정적 임포트를 사용해 상수 이름만으로 사용하기
 */
public class Test {
  double atoms(double mols) {
    return AVOGADROS_NUMBER * mols;
  }
}
