package kr.taeu.effectiveJava.item10;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * instanceof 검사를 getClass 검사로 바꾸면 규약도 지키고 값도 추가하면서 구체 클래스를 상속할 수 있을까
 */
public class Point2 {
	private final int x;
	private final int y;
	
	public Point2(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/*
	 * 같은 구현 클래스의 객체와 비교할 때만 true를 반환한다.
	 * 하지만 Point의 하위 클래스는 정의상 여전히 Point이므로 어디서든지 Point로써 활용될 수 있어야 한다.
	 * (리스코프 치환원칙)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o == null || o.getClass() != getClass())) {
			return false;
		}
		Point2 p = (Point2)o;
		return p.x == x && p.y == y;
	}
	
	/*
	 * 단위 원 안에 있는지 판별하는 메서드
	 */
	private static final Set<Point2> unitCircle = new HashSet<>(Arrays.asList(
			new Point2(1, 0), new Point2(0, 1),
			new Point2(-1, 0), new Point2(0, -1)));
	
	public static boolean onUnitCircle(Point2 p) {
		return unitCircle.contains(p);
	}
}
