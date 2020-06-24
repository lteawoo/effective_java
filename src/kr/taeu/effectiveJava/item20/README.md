# 추상 클래스보다는 인터페이스를 우선하라
추상 클래스와 인터페이스의 가장 큰 차이는 추상 클래스가 정의한 타입을 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다는 점이다. 자바는 단일 상속만 지원하니, 추상 클래스 방식은 새로운 타입을 정의하는 데 커다란 제약을 안게 되는 셈이다. 인터페이스는 선언한 메서드를 모두 정의하고 그 일반 규약을 잘 지킨 클래스라면 다른 어떤 클래스를 상속했든 같은 타입으로 취급된다.

### 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해넣을 수 있다.
자바 플랫폼에서도 Comparable, Iterable, AutoCloseable 인터페이스가 새로 추가됐을 때 표준 라이브러리의 수많은 기존 클래스가 이 인터페이스들을 구현한 채 릴리스됐다. 반면 기존 클래스 위에 새로운 추상 클래스를 끼워넣기는 어려운게 일반적이다. 두 클래스가 같은 추상클래스를 확장하길 원한다면, 그 추상 클래스는 계증구조상 두 클래스의 공통 조상이어야 한다. 새로 추가된 추상 클래스의 모든 자손이 이를 상속하게 되는 것이다.

### 인터페이스는 믹스인(mixin) 정의에 안성맞춤이다.
믹스인이란 클래스가 구현할 수 있는 타입으로, 믹스인을 구현한 클래스에 원래의 '주된 타입'외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다. 예컨대 Comparable은 자신을 구현한 클래스의 인스턴스들끼리는 순서를 정할 수 있다고 선언하는 믹스인(Mixin) 인터페이스이다.

### 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.
타입을 계층적으로 정의하면 수많은 개념을 구조적으로 잘 표현할 수 있지만, 현실에는 계층을 엄격히 구분하기 어려운 개념도 있다. 예를 들어 가수(Singer) 인터페이스와 작곡가(Songwriter) 인터페이스가 있다고 해보자.
```java
public interface Singer {
    AudioClip song(Song s);
}

public interface Songwriter {
    Song compose(int chartPosition);
}
```
우리 주변엔 작곡도 하는 가수가 제법 있다. 이 코드처럼 타입을 인터페이스로 정의하면 가수 클래스가 Singer와 Songwriter 모두를 구현해도 전혀 문제되지 않는다. 심지어 Singer와 Songwriter 모두를 확장하고 새로운 메서드까지 추가 한 제 3의 인터페이스를 정의할 수 있다.
```java
public interface SingerSongwriter extends Singer, SongWriter {
    AudioClip strum();
    void actSensitive();
}
```
인터페이스의 메서드 중 구현 방법이 명백한 것이 있다면, 그 구현을 디폴트 메서드로 제공할 수 있다, 상속하려는 사람을 위한 설명을 @impleSpec 자바독 태그를 붙여 문서화해야 한다.

인터페이스와 추상 골격 구현(skeletal implementation) 클래스를 함께 제공하는 식으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다. 인터페이스로는 타입을 정의하고, 필요하면 디폴트 메서드 몇 개도 함께 제공한다. 그리고 골격 구현 클래스는 나머지 메서드들까지 구현한다. 바로 템플릿 메서드 패턴이다.

관례상 인터페이스 이름이 Interface라면 그 골격 구현 클래스의 이름은 AbstractInterface로 짓는다. 좋은 예로, 컬렉션 프레임워크의 AbstractCollection, AbstractSet, AbstractList 등 각각이 핵심 컬렉션 인터페이스의 골격 구현이다. 제대로 설계했다면 골격은 나름의 구현을 만들려는 프로그래머의 일을 상당히 덜어준다. 다음은 List 구현체를 반환하는 정적 팩터리 메서드로 AbstractList 골격구현으로 활용했다.
```java
static List<Integer> intArrayAsList(int[] a) {
    Objects.requireNonNull(a);

    // 다이아몬드 연산자를 이렇게 사용하는 건 자바 9부터 가능하다.
    // 더 낮은 버전을 사용한다면 <Integer>로 수정하자.
    return new AbstractList<>() { //AbstractList<Integer>...
        @Override
        public Integer get(int i) {
            return a[i];    // 오토박싱
        }

        @Override
        public Integer set(int i, Integer val) {
            int oldVal = a[i];
            a[i] = val;  // 오토언박싱
            return oldVal;  // 오토박싱
        }

        @Override
        public int size() {
            return a.length;
        }
    };
}
```
List 구현체가 여러분에게 제공하는 기능들을 생각하면, 이 코드는 골격 구현의 힘을 잘 보여주는 인상적인 예다. int 배열을 받아 Integer 인스턴스의 리스트 형태로 보여주는 어댑터라고 볼 수 있다.