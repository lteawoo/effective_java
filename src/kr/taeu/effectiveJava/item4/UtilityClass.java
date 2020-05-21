package kr.taeu.effectiveJava.item4;

/*
 * 인스턴스를 만들수 없는 유틸리티 클래스
 */
public class UtilityClass {
	// 기본 생성자가 만들어지는 것을 막는다.(인스턴스화 방지용)
	private UtilityClass() {
		throw new AssertionError();
	}
}