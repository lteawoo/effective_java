package kr.taeu.effectiveJava.item10;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * 리스코프 치환 원칙 - 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다.
 * 따라서 그 타입의 모든 메서드가 하위 타입에서도 똑같이 잘 작동해야 한다.
 */
public class CounterPoint1 extends Point2 {
	private static final AtomicInteger counter = new AtomicInteger();
	
	public CounterPoint1(int x, int y) {
		super(x, y);
		counter.incrementAndGet();
	}
	
	public static int numberCreated() {
		return counter.get();
	}
	
	public static void main(String[] args) {
		CounterPoint1 cp1 = new CounterPoint1(0, 1);
		CounterPoint1 cp2 = new CounterPoint1(1, 2);
		
		System.out.println(cp1.numberCreated());
		
		/*
		 * Point2의 equals가 getClass로 작성되어있으니 x,y값과는 무관하게 false를 반환한다.
		 * 대부분의 컬렉션은 contains작업에서 equals 메서드를 사용한다. CounterPoint의 인스턴스는 어떤 Point와도 같을 수 없다.
		 */
		System.out.println(Point2.onUnitCircle(cp1));
	}
}
