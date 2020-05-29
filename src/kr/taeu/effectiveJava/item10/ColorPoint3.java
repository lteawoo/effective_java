package kr.taeu.effectiveJava.item10;

import java.awt.Color;

/*
 * Point와 비교할땐 색상을 무시하면?
 */
public class ColorPoint3 extends Point1{
	private final Color color;

	public ColorPoint3(int x, int y, Color color) {
		super(x,y);
		this.color = color;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point1)) {
			return false;
		}
		
		// o가 일반 Point면 색상을 무시하고 비교한다.
		if (!(o instanceof ColorPoint3)) {
			return o.equals(this);
		}
		
		return super.equals(o) && ((ColorPoint3) o).color == color;
	}
	
	/*
	 * 추이성을 위배 p와는 좌표만 비교하여 true지만
	 * ColorPoint 끼리는 색상까지 비교하여 추이성을 위배한다.
	 */
	public static void main(String[] args) {
		Point1 p = new Point1(1,2);
		ColorPoint3 cp1 = new ColorPoint3(1, 2, Color.RED);
		ColorPoint3 cp2 = new ColorPoint3(1, 2, Color.BLUE);
		
		System.out.println(cp1.equals(p)); // true
		System.out.println(p.equals(cp2)); // true
		System.out.println(cp1.equals(cp2)); // false
	}
}
