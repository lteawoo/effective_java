# toString은 항상 재정의하라
Object의 기본 toString 메서드가 우리가 작성한 클래스에 적합한 문자열을 반환하는 경우는 거의 없다. 이 메서드는 PhoneNumber@adbbd처럼 단순히 클래스_이름@16진수로_표시한_해시코드를 반환할 뿐이다. 

toString의 규약은 다음과 같이 쉽다.
* '간결하면서 사람이 읽기 쉬운 형태의 유익한 정보'를 반환해야 한다.
* 모든 하위 클래스에서 이 메서드를 재정의하라

PhoneNumber용 toString을 제대로 재정의했다면 다음 코드만으로 문제를 진단하기에 충분한 메시지를 남길 수 있다.
```java
System.out.println(phoneNumber + "에 연결할 수 없습니다.");
```
**실전에서 toString은 그 객체가 가진 주요 정보 모두를 반환하는게 좋다.** 객체가 거대하다면 주요 정보를 담고 해당 내용은 객체를 완전히 설명하는 문자열이어야 한다.
# 반환값의 포맷을 문서화할지 결정해야 한다. #
전화번호나 행렬 같은 값 클래스라면 문서화하기를 권한다. 포맷을 명시하면 그 객체는 표준적이고, 명확하고, 사람이 읽을 수 있게 된다. 명시하기로 결정했다면 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공하면 좋다.(BigInteger, BigDecimal과 대부분의 기본 타입 클래스가 여기에 해당)

단점으로는 포맷을 한번 명시하면 평생 그 포맷에 얽매인다. 많은 프로그래머들이 그 포맷에 맞춰 파싱하고, 새로운 객체를 만들고, 영속 데이터로 저장하는 코드를 작성할 것이다. 향후 릴리스에서 포맷을 바꾼다면.. 끔찍할 것이다. 포맷을 명시하지 않으면 향후 릴리스에서 정보를 더 넣거나 포맷을 개선할 수 있는 유연성을 얻게 된다.

**명시하든 안하든 여러분의 의도는 명확히 밝혀야 한다.**

