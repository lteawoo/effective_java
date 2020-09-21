# 적시에 방어적 복사본을 만들어라
자바는 안전한 언어다. 네이티브 메서드를 사용하지 않으니 메모리 충돌 오류에서 안전한다. 하지만 아무리 자바라 해도 다른 클래스로부터의 침범을 아무런 노력 없이 다 막을 수 있는 건 아니다. 그러니 **클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍해야 한다.**

어떤 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다. 하지만 주의를 기울이지 않으면 허락하는 경우가 생긴다.
```java
// 기간을 표현하는 클래스 - 불변식을 지키지 못했다.
public class Period1 {
    private final Date start;
    private final Date end;

    public Period1(Date start, Date end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }

        this.start = start;
        this.end = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }
}
```
이 클래스는 불변처럼 보이지만 Date가 가변이라는 사실을 이용하면 어렵지 않게 그 불변식을 깨뜨릴 수 있다.
```java
// Period 인스턴스의 내부를 공격하자
Date start = new Date();
Date end = new Date();
Period1 p1 = new Period1(start, end);
end.setYear(79); // p의 내부를 수정했따.
```
다행히 자바 8 이후로는 쉽게 해결 할 수 있다. date 대신 불변인 Instant를 사용하면 된다(혹은 LocalDateTime 이나 ZonedDateTime) **Date는 낡은 API이니 새로운 코드를 작성할 때는 더이상 사용하면 안된다.**

외부 공격으로부터 Period 인스턴스를 보호하려면 **생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)**해야 한다. 그런다음 Period 인스턴스 안에서는 원본이 아닌 복사본을 사용한다.
```java
// 수정한 생성자 - 매개변수의 방어적 복사본을 만든다.
public class Period2 {
    private final Date start;
    private final Date end;

    public Period2(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다.");
        }
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }
}
```
**매개변수의 유효성을 검사 하기전, 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사한 점에 주목하자.** 순서가 부자연스러워 보이겠지만 이렇게 해야한다. 멀티 스레딩 환경이라면 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험이 있기 때문이다. 컴퓨터 보안커뮤니티에서는 이를 검사시점/사용시점 공격 혹은 영어 표기를 줄여 TOCTOU(time-of-clock / time-of-use) 공격이라 한다.

방어적 복사에 Date의 clone 메서드를 사용하지 않은 점에도 주목하자. Date는 final이 아니므로 clone이 Date가 정의한 게 아닐 수 있다. 즉, clone이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다. 예를들어 이 하위 클래스는 start와 end 필드의 참조를 private 정적 리스트에 담아뒀다가 공격자에게 이 리스트에 접근하는 길을 열어줄 수도 있다. 이런 공격을 막아내기 위해 서는 **매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안된다.**

생성자를 수정하면 앞서의 공격은 막아 낼수 있지만, Period 인스턴스는 아직도 변경 가능하다. 접그자 메서드가 내부의 가변정보를 드러내기 때문이다.
```java
// Period 인스턴스를 향한 두번째 공격
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
p.end().setYear(78); // p의 내부를 변경했다!
```
두 번째 공격을 막아내려면 단순히 접근자가 가변 필드의 방어적 복사본을 반환하면 된다.
```java
// 수정한 접근자 - 필드의 방어적 복사본을 반환한다.
public class Period3 {
    private final Date start;
    private final Date end;

    public Period3(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다.");
        }
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }
}
```
Period 자신 말고는 가변 필드에 접근할 방법이 없으니 확실하다. 모든 필드가 객체 안에 완벽하게 캡슐화되었다.

생성자와 달리 접근자 메서드에서는 방어적 복사에 clone을 사용해도 된다. Period가 가지고 있는 Date 객체는 java.util.Date임이 확실하기 때문이다.(신뢰할 수 없는 하위 클래스가 아니다.) 그렇더라도 아이템 13에서 설명한 이유때문에 인스턴스를 복사하는 데는 일반적으로 생성자나 정적 팩터리를 쓰는게 좋다.

매개변수를 방어적으로 복사하는 목적이 불변 객체를 만들기 위해서만은 아니다. 메서드든 생성자든 클라이언트가 제공한 객체의 참조를 내부의 자료구조에 보관해야 할 때면 항시 그 객체가 변경될 수 있는지를 생각해야 한다. 변경될 수 있는 객체라면 그 객체가 클래스에 넘겨진 뒤 임의로 변경되어도 그 클래스가 문제없이 동작할지를 따져보자.(ex: 내부의 Set이나 Map 인스턴스의 키로 클라이언트가 건네준 객체를 사용한다면? 추후 그 객체가 변경될 때 객체를 담고 있는 Set, Map은 불변식이 깨진다.)

내부 객체를 클라이언트에 건네줄때도 클래스가 불변이든 가변이든, 가변인 내부 객체를 반환할 때는 방어적 복사본을 고려해봐야한다. 길이가 1이상인 배열은 무조건 가변임을 명심하자. 그러니 내부에서 사용하는 배열을 클라이언트에 반환할 때는 항상 방어적 복사를 수행해야 한다.

방어적 복사는 성능 저하가 따르고, 항상 쓸수 있는 것도 아니다. 같은 패키지에 속하는 등의 이유로 호출자가 컴포넌트 내부를 수정하지 않으리라 확신하면 방어적 복사를 생략할 수 있다. 이러한 상황에서라도 호출자에게 해당 매개변수나 반환값을 수정하지 말아야 함을 명시하는게 좋다.

다른 패키지에서 사용한다고 해서 넘겨받은 가변 매개변수를 항상 방어적으로 복사해 저장해야 하는 것은 아니다. 때로는 매개변수로 넘기는 행위가 그 객체의 통제권을 명백히 이전함을 뜻하기도 한다. 이처럼 통제권을 이전하는 메서드를 호출하는 클라이언트는 해당 객체를 더이상 직접 수정하는 일이 없다고 약속해야한다. 클라이언트가 건네주는 가변 객체의 통제권을 넘겨받는다고 기대하는 메서드나 생성자에서도 그 사실을 확실히 문서화하자

위의 메서드나 생성자를 가진 클래스들은 악의적인 클라이언트의 공격에 취약하다. 따라서 방어적 복사를 생략해도 되는 상황은 해당 클래스와 그 클라이언트가 상호 신뢰할 수 있을 때, 혹은 불변식이 깨지더라도 해당 클라이언트에 국한될 때로 한정해야 한다.