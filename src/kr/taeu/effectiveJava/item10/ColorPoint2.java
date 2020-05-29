package kr.taeu.effectiveJava.item10;

import java.awt.Color;

/*
 * 또 다른 ColorPoint와 비교하는 equals 위치와 색상이 같을 때만 true
 * 이 메서드는 일반 Point를 ColorPoint에 비교한 결과와 그 둘을 바꿔 비교한 결과가 다를 수 있다.
 */
public class ColorPoint2 extends Point1{
	private final Color color;

	public ColorPoint2(int x, int y, Color color) {
		super(x,y);
		this.color = color;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ColorPoint2)) {
			return false;
		}
		return super.equals(o) && ((ColorPoint2) o).color == color;
	}
	
	/*
	 * 대칭성을 위배
	 */
	public static void main(String[] args) {
		Point1 p = new Point1(1,2);
		ColorPoint2 cp = new ColorPoint2(1, 2, Color.RED);
		
		System.out.println(p.equals(cp)); // true
		System.out.println(cp.equals(p)); // false
	}
}
