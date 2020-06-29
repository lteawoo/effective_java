package kr.taeu.effectiveJava.item22;


/*
 * 상수 필드로만 가득 찬 인터페이스
 * 사용금지! 안티패턴
 */
public interface PhysicalConstrants1 {
  // 아보가드로 수 (1/몰)
  static final double AVOGADROS_NUMBER = 6.022_140_857e23;
  
  // 볼츠만 상수 (J/K)
  static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
  
  // 전자 질량 (kg)
  static final double ELECTRON_MASS = 9.109_383_56e-31;
}
