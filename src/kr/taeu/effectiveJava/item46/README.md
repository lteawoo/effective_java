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