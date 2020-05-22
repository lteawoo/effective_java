package kr.taeu.effectiveJava.item6;

import java.util.regex.Pattern;

/*
 * 생성 비용이 비싼 객체는 캐싱하여 사용하자(값비싼 객체의 재사용)
 */
public class RomanNumeral2 {
	private static final Pattern ROMAN = Pattern.compile(
			"^(?=.)M*(C[MD]|D?C{0,3})"
			+ "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
	
	static boolean isRomanNumeral(String s) {
		return ROMAN.matcher(s).matches();
	}
}
