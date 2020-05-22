package kr.taeu.effectiveJava.item6;

/*
 * 비싼 객체의 반복적인 생성 및 사용
 */
public class RomanNumeral1 {
	static boolean isRomanNumeral(String s) {
		return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
				+ "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
	}
}
