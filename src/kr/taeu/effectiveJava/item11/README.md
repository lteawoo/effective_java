# equals를 재정의하려거든 hashCode도 재정의 하라
equals를 재정의한 클래스 모두에서 hashCode도 재정의해야 한다. 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.
* equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 일관되게 항상 같은 값을 반환해야 한다.
* equals가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
* 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단 다른 객체에 대해서는 다른 값을 반환해야 해시테이블 성능이 좋아진다.

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(707, 867, 5309), "제니");
m.get(new PhoneNumber(707, 867, 5309));
```
m.get(...)실행 시 제니가 나와야 할 것 같지만 null이 나온다. PhoneNumber 클래스는 hashCode를 재정의하지 않았기 때문에 논리적 동치인 두 객체가 서로 다른 해시코드를 반환하여 두번째 규약을 지키지 못한다. 그 결과 get메서드는 엉뚱한 해시 버킷에가서 객체를 찾으려 한 것이다. 두 인스턴스를 같은 버킷에 담았더라도, HashMap은 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화 되어 있기 때문에 null을 반환한다.
```java
@Override
public int hashCode() {
    return 42;
}
```
위와 같이 모든 객체에서 똑같은 해시코드를 반환하니 적법하다. 하지만 모든 객체가 똑같은 값만 내어주므로 해시테이블의 버킷 하나에 담겨 평균 수행시간이 O(1)인 해시테이블이 O(n)으로 느려져서, 객체가 많아지면 도저히 쓸 수가 없다. 이상적인 해시 함수는 주어진 (서로 다른) 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 한다. 완벽하게 하긴 어렵지만 비슷하게 만들기는 그다지 어렵지 않다.

1. int 변수 result를 선언한 후 값 c로 초기화한다.(c는 해당 객체의 첫번째 핵심 필드를 단계 2.1 방식으로 계산한 해시코드다, 핵심필드란 equals 비교에 사용되는 필드)
2. 해당 객체의 나머지 핵심 필드 f 각각에 대해 다음 작업을 수행한다.
    1. 해당 필드의 해시코드 c를 계산한다.
        1. 기본 타입 필드라면, Type.hashCode(f)(여기서 Type은 기본 타입의 박싱 클래스)를 수행한다.
        2. 참조 타입 필드면서 이 클래스의 equals 메서드가 이 필드의 equals를 재귀적으로 호출해 비교한다면, 이 필드의 hashCode를 재귀적으로 호출한다.
        3. 필드가 배열이라면, 핵심 원소 각각을 별도 필드처럼 다룬다. 원소 각각 위의 규칙을 적용해 해시코드를 계산한 다음 2.2 방식으로 갱신한다. 원소가 하나도 없다면 상수(0을 추천), 모든 원소가 핵심 원소라면 Arrays.hashCode를 사용한다.
    2. 단계 2.1에서 계산한 해시코드 c로 result를 갱신한다. -> result = 31 * result + c;
3. result를 반환한다.

31인 이유는 홀수이면서 소수(prime)이기 때문이다. 2를 곱하는 것은 시프트 연산과 같은 결과를 내기 때문이고, 소수는 전통적으로 그리 해왔다. 결과적으로 31을 이용하면, 이 곱셉을 시프트 연산과 뺄셈으로 대체해 최적화할 수 있다.(31 * i는 (i << 5) - i)와 같다.)
```java
@Override
public int hashCode() {
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}
```
위 메서드는 핵심 필드만을 이용하여 간단한 계산을 통해 해시코드를 반환한다, 비결정적인 요소는 전혀 없으니 동치인 객체들은 같은 해시코드를 가질 것이 확실하다. 부족함이 없는 메서드이다.

클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기보다는 캐싱하는 방식을 고려해야 한다. 이 타입의 객체가 주로 해시의 키로 사용될 것 같다면 인스턴스가 만들어질 때 해시코드를 계산해둬야 한다. 해시의 키로 사용되지 않는 경우라면 hashCode가 처음 불릴 때 계산하는지연 초기화 전략은 어떤가 스레드 안정성을 고려해야한다.
```java
private int hashCode; // 자동으로 0으로 초기화

@Override
public int hashCode() {
    int result = hashCode;
    if (result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}