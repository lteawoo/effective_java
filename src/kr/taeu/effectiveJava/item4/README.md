인스턴스화를 막으려면 private 생성자를 사용하자
정적 멤버만을 담은 유틸리티 클래스는 인스턴스를 만들어 쓰라고 설계한게 아니다.
추상 클래스를 만드는것으로는 인스턴스화를 막을 수 없다. 하위 클래스를 만들면 그만이다. 오히려 상속해서 쓰라는 뜻으로
오해할 수 있다.
생성자를 private로 선언하면 상속을 막을 수 있다. 명시적이든 묵시적이든 모든 생성자는 상위 클래스의 생성자를 호출하니까