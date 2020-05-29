package kr.taeu.effectiveJava.item10;

import java.awt.Color;

/*
 * equals를 재정의 안하면 Point의 구현이 상속되어 색상 정보는 무시한채 진행된다.
 * equals 규약을 어긴 것은 아니지만 중요한 정보를 놓치게 되니 안된다.
 */
public class ColorPoint1 extends Point1{
	private final Color color;

	public ColorPoint1(int x, int y, Color color) {
		super(x,y);
		this.color = color;
	}
	
}
