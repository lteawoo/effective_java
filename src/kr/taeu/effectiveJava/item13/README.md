# clone 재정의는 주의해서 진행하라
Cloneable은 이름에도 나타나듯 복제해도 되는 클래스임을 명시하는 용도의 mixin interface이다. 그러나 clone 메서드는 Object에 선언되어 있고, 그마저도 protected이다. 그래서 Cloneable을 구현하는 것만으로 외부 객체에서 clone 메서드를 호출할 수 없다.

## Cloneable 인터페이스가 하는일
Cloneable 인터페이스는 메서드가 하나도 없다. 그렇다면 뭐를 할까? 놀랍게도 Object의 protected 메서드인 clone의 동작 방식을 결정한다. Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환한다. 그렇지 않으면 **CloneNotSupportedException**을 던진다.

## clone 메서드의 일반규약
* x.clone() != x 는 참이다.
* x.clone().getClass() == x.getClass() 는 참이다.
* x.clone().equals(x) 이 식도 일반적으로 참이지만 필수는 아니다.
* 관례상, 이 메서드가 반환하는 객체는 super.clone을 호출해 얻어야 한다. 이 클래스와 (Object를 제외한) 모든 상위 클래스가 이 관례를 따른다면 다음 식은 참이다. x.clone().getClass() == x.getClass()
* 관례상, 반환된 객체와 원본 객체는 독립적이어야 한다. super.clone()으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.

## 제대로된 clone 메서드를 가진 상위 클래스를 상속해 cloneable을 구현해보자
먼저 super.clone을 호출한다. 그렇게 얻은 객체는 원본의 완벽한 복제본일 것이다. 모든 필드가 기본 타입이거나 불변 객체를 참조한다면 이 객체는 완벽히 우리가 원하는 상태라 더 손볼 것이 없다. PhoneNumber 클래스가 여기에 해당한다. 그런데 쓸데없는 복사를 지양한다는 관점에서 보면 불변 클래스는 굳이 clone 메서드를 제공하지 않는게 좋다. 이 점을 고려해 구현 해보면 다음과 같다.
```java
  public PhoneNumber1 clone() {
	  try {
		  return (PhoneNumber1) super.clone();
	  } catch (CloneNotSupportedException e) {
		  throw new AssertionError(); // 일어날 수 없는 일이다.
	  }
  }
}
```
Object의 clone 메서드는 Object를 반환하지만 PhoneNumber의 clone 메서드는 PhoneNumber를 반환하게 했다. 자바가 공변 반환 타이핑(convariant return typing)을 지원하니 가능하고 권장하는 방식이다.(절대 실패하지 않는다.)

클라이언트가 형변환하지 않아도 되게끔 해주자, 이를 위해 앞 코드에서는 super.clone에서 얻은 객체를 반환하기 전에 PhoneNumber로 형변환하였다.(절대 실패하지 않는다)