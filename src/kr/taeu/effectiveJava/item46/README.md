# 스트림에서는 부작용 없는 함수를 사용하라
스트림 패러다임의 핵심은 계산을 일련의 변환으로 재구성하는 부분이다. 이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다. 순수 함수란 오직 입력만이 결과에 영향을 주는 함수를 말한다.

다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않는다. 이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 부작용이 없어야 한다.

아래는 텍스트 파일에서 단어별 수를 세어 빈도표를 만드는 일을 한다.
```java
// 스트림 패러다임을 이해하지 못한 채 API만 사용했다.
Map<String, Long> freq = new HashMap<>();
try (Stream<String> words = new Scanner(file).tokens()) {
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
```
위는 스트림 코드를 가장한 반복적 코드다. 읽기 어렵고 유지보수에도 안좋다.
```java
// 스트림을 제대로 활용해 빈도표를 초기화한다.
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words
        .collect(groupingBy(String::toLowerCase, counting()));
}
```
첫 코드의 forEach는 종단 연산 중 장 기능이 적고 덜 스트림답다. 대놓고 반복적이라서 병렬화 할수도 없다. forEach연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산하는 데는 쓰지 말자.

collector(수집기)는 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.
```java
// 빈도표에서 가장 흔한 단어 10개를 뽑는 파이프라인
List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
```
## 가장 간단한 맵 수집기
toMap(keyMapper, valueMapper)로, 보다시피 스트림원소를 키에 매핑하는 함수와 값에 매핑하는 함수를 인수로 받는다.
```java
// toMap 수집기를 사용하여 문자열을 열거 타입 상수에 매핑한다.
private static final Map<String, Operation> stringToEnum = 
    Stream.of(values()).collect(
        toMap(Object::toString, e -> e));
```
이 간단한 형태의 toMap은 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다.

더 복잡한 형태의 toMap이나 groupingBy는 이런 충돌을 다루는 다양한 전략을 제공한다.예컨대 toMap에 키 매퍼와 값 매퍼는 물론 병합(merge) 함수까지 제공할 수 있다.

병합 함수의 형태는 BinaryOperator\<U>이며, 여기서 U는 해당 맵의 값 타입이다. 같은 키를 공유하는 값들은 이 병합 함수를 사용해 기존 값에 합쳐진다.
```java
// 각 키와 해당 키의 특정 원소를 연관 짓는 맵을 생성하는 수집기
Map<Artist, Album> topHits = albums.collect(
    toMap(Album::artist, a->a, maxBy(comparing(Alubm::sales))));
```
인수 3개를 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다. 여기서 비교자로는 BinaryOperator에서 정적 임포트한 maxBy라는 정적패터리메서드를 사용했다. maxBy는 comparator\<T>를 입력받아 BinaryOperator\<T>를 돌려준다.

이 경우 비교자 생성 메서드인 comparing이 maxBy에 넘겨줄 비교자를 반환하는데, 자신의 키 추출 함수로는 Album::sales를 받았다. 복잡해 보일 수 있지만 매끄럽게 읽히는 코드다. 말로 풀어보면 "앨범 스트림을 맵으로 바꾸는데, 이 맵은 각 음악가와 그 음악가의 베스트 앨범을 짝지은 것이다."는 이야기다.

