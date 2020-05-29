package kr.taeu.effectiveJava.item10;

import java.awt.Color;
import java.util.Objects;

/*
 * equals 규약을 지키면서 값 추가하기
 * 우회방법 : 상속 대신 컴포지션을 사용하자
 * Point를 상속하는 대신 Point를 ColorPoint의 private 필드로 두고, ColorPoint와
 * 같은 위치의 일반 Point를 반환하는 뷰 메서드(객체의 재사용)를 public으로 추가하는 식.
 */
public class ColorPoint4 {
	private final Point1 point;
	private final Color color;
	
	public ColorPoint4(int x, int y, Color color) {
		point = new Point1(x, y);
		this.color = Objects.requireNonNull(color);
	}
	
	/**
	 * 이 ColorPoint4의 Point1 뷰를 반환
	 */
	public Point1 asPoint() {
		return point;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ColorPoint4)) {
			return false;
		}
		ColorPoint4 cp = (ColorPoint4) o;
		return cp.point.equals(point) && cp.color.equals(color);
	}
}
