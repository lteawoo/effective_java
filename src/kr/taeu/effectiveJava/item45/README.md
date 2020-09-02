# 스트림은 주의해서 사용하라
스트림API는 다량의 데이터 처리 작업(순차적이든 병렬적이든)을 돕고자 자바 8에 추가되었다. 이 API가 제공하는 추상 개념 중 핵심은 두 가지다.

그 첫번째인 스트림(stream)은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻한다.

두번째 스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.

스트림의 원소들은 어디로부터든 올 수 있다. 대표적으로는 컬렉션, 배열, 파일, 정규표현식 패턴 매처(matcher), 난수 생성기, 혹은 다른 스트림이 있다. 스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값으로는 int, long, double 이렇게 세 가지를 지원한다.

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝나며, 그 사이에 하나 이상의 중간 연산(intermediate operaation)이 있을 수 있다. 각 중간 연산은 스트림을 어떠한 방식으로 변환(transform)한다.

예컨대 각 원소에 함수를 적용하거나 특정 조건을 만족 못하는 원소를 걸러낼 수 있다. 중간 연산들은 모두 한 스트림을 다른 스트림으로 변환하는데, 변환 된 스트림의 원소 타입은 변환 전 스트림의 원소 타입과 같을 수도 있고 다를 수도 있다.

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다. 원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나, 모든 원소를 출력하는 식이다.

스트림 파이프라인은 지연평가(lazy evaluation)된다. 평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다. 이러한 지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠다. 종단 연산이 없는 스트림 파이프 라인은 아무 일도 하지 않는 명령어인 no-op과 같으니, 종단 연산을 빼먹는 일이 없도록 하자

## 스트림 API는 메서드 연쇄를 지원하는 플루언트(fluent API)다.
파이프 라인 하나를 구성하는 모든 호출을 연결하여 단 하나의 표현식으로 완성할 수 있다. 파이프라인 여러 개를 연결 해 표현식 하나로 만들 수 있다.

기본적으로 스트림 파이프라인은 순차적으로 수행된다. 파이프 라인을 병렬로 실행하려면 파이프라인을 구성하는 스트림 중 하나에서 parallel 메서드를 호출해 주기만 하면 되나, 효과를 볼 수 있는 상황은 많지 않다.

스트림 API는 다재다능하여 사실 상 어떠한 계산이라도 해낼 수 있다. 하지만 제대로 사용하지 못하면 읽기 어렵고 유지보수도 어려워진다.
```java
// 사전 하나를 훑고 원소 수가 많은 아나그램 그룹들을 출력해낸다.
public class Anagrams1 {
    public static void main(String[] args) throws IOException {
        File dictionary = new File(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner s = new Scanner(dictionary)) {
            while (s.hasNext()) {
                String word = s.next();
                groups.computeIfAbsent(alphabetize(word),
                        (unused) -> new TreeSet<>()).add(word);
            }
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + ": " + group);
            }
        }
    }

    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```
computeIfAbsent 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑 된 값을 반환한고 없으면 함수 객체를 키에 적용하여 값을 계산해낸 다음 그 키와 값을 매핑해놓고, 반환한다.

다음은 앞의 코드와 같은 일을 하지만 스트림을 과하게 활용한다.
```java
// 스트림을 과하게 사용했다. - 따라 하지 말것!
public class Anagrams2 {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(
                    Collectors.groupingBy(word -> word.chars().sorted()
                                            .collect(StringBuilder::new,
                                                    (stringBuilder, value) -> stringBuilder.append((char) value),
                                                    StringBuilder::append).toString()))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .map(group -> group.size() + ": " + group)
                    .forEach(System.out::println);
        }
    }
}
```
이처럼 스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워 진다.

다음은 절충하여 사용한 짧고 명확한 코드다
```java
// 스트림을 적절히 활용하면 깔끔해진다.
public class Anagrams3 {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(Collectors.groupingBy(word -> alphabetize(word)))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        }
    }

    private static String alphabetize(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```
try-with-resources 블록에서 사전 파일을 열고, 파일의 모든 라인으로 구성된 스트림을 얻는다. 스트림 변수의 이름을 words로 지어 스트림 안의 각 원소가 단어임을 명확히 했다.

이 스트림의 파이프라인에는 중간 연산은 없으며, 종단 연산에서는 모든 단어를 수집해 맵으로 모은다. 이 맵은 단어들을 아나그램끼리 묶어놓은 것으로, 앞서 두 프로그램이 생성한 맵과 실질적으로 같다.

그다음으로 이 맵의 values()가 반환한 값으로부터 새로운 Stream\<List\<String>> 스트림을 연다. 이 스트림의 원소는 물론 아나그램 리스트다. 그 리스트들 중 원소가 minGroupSize보다 적은 것은 필터링돼 무시된다. 마지막으로 종단 연산인 forEach는 살아남은 리스트를 출력한다.

## 스트림을 처음 쓸때의 유혹
처음 쓰기 시작하면 모든 반복문을 스트림으로 바꾸고 싶은 유혹이 일겠지만, 서두르지 않는게 좋다. 스트림으로 바꾸는게 가능할지라도 코드 가독성과 유지보수 측면에서는 손해를 볼 수 있기 때문이다. 중간 정도 복잡한 작업에도(앞서의 아나그램) 스트림과 반복문을 적절히 조합하는 게 최선이다. 그러니 기존 코드는 스트림을 사용하도록 리팩토링하되, 새 코드가 더 나아 보일때까지만 반영하자.

## 함수 객체로는 할 수 없는 일
* 코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다 하지만 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고, 지역변수를 수정하는 건 불가능하다.
* 람다로는 메서드에서 빠져나가거나(return) break, continue 문으로 블록 바깥의 반복문을 컨트롤 할수 없다.
## 스트림이 안성 맞춤인 경우
* 원소들의 시퀀스를 일관되게 변환한다.
* 원소들의 시퀀스를 필터링한다.
* 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.(더하기, 연결하기, 최솟값 구하기 등)
* 원소들의 시퀀스를 컬렉션에 모은다.
* 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

스트림으로 처리하기 어려운 일도 있다. 대표적인 예로, 데이터가 파이프라인의 여러 단계를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기는 어려운 경우다. 스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문이다.

예를 들어 처음 20개의 메르센 소수를 출력하는 프로그램을 작성해보자. 메르센 소수는 2^p-1 형태의 수다. 여기서 p가 소수이면 해당 메르센 수도 소수일 수 있는데, 이때의 수를 메르센 소수라고 한다.
```java
static Stream<BigInteger> primes() {
    return Stream.iterate(TWO, BigInteger::nextProbablePrime)
}
```
메서드 이름 primes는 스트림의 원소가 소수임을 말해준다. 스트림을 반환하는 메서드 이름은 이처럼 원소의 정체를 알려주는 복수 명사로 쓰기를 강력히 추천한다. 스트림 파이프라인의 가독성이 크게 좋아질 것이다. 이 메서드가 이용하는 Stream.iterate라는 정적 팩터리는 매개변수를 2개 받는다. 첫 번째 매개변수는 스트림의 첫 번째 원소이고, 두 번째 매개변수는 스트림에서 다음 원소를 생성해주는 함수다. 이제 처음 20개의 메르센 소수를 출력하는 프로그램을 만나보자.
```java
    primes().map(p -> TWO.pow(p.intValueExcat()).subtract(ONE))
        .filter(mersenne -> mersenne.isProbablePrime(50))
        .limit(20)
        .forEach(System.out::println);
```
소수들을 사용해 메르센 수를 계산하고, 결괏값이 소수인 경우만 남긴 다음(매직넘버 50은 소수성 검사가 true를 반환할 확률을 제어한다), 결과 스트림의 원소 수를 20개로 제한해놓고, 작업이 끝나면 결과를 출력한다.

이제 우리가 각 메르센 소수의 앞에 지수(p)를 출력하길 원한다고 해보자.이 값은 초기 스트림에만 나타나므로 결과를 출력하는 종단 연산에서는 접근할 수 없다. 하지만 다행히 첫 번째 중간 연산에서 수행한 매핑을 거꾸로 수행해 메르센 수의 지수를 쉽게 계산해낼 수 있다. 지수는 단순히 숫자를 이진수로 표현한 다음 몇 비트인지를 세면 나오므로, 종단 연산을 다음처럼 작성하면 원하는 결과를 얻을 수 있다.
```java
.forEach(mp -> System.out.println(mp.bitLength() + ": "+ mp));
```
스트림과 반복 중 어느 쪽을 써야 할지 바로 알기 어려운 작업도 많다. 카드 덱을 초기화하는 작업을 생각해보자. 카드는 숫자(rank)와 무늬(suit)를 묶은 불변 값 클래스이고 숫자와 무늬는 모두 열거 타입이라 하자. 이 작업은 두 집합의 원소들로 만들 수 있는 가능한 모든 조합을 계산하는 문제다. 수학자들은 이를 두 집합의 데카르트 곱이라 한다.
```java
// 데카르트 곱 계산을 반복 방식으로 구현
private static List<Card> newDeck() {
    List<Card> result = new ArrayList<>();
    for (Suit suit : Suit.values()) {
        for (Rank rank : Rank.values()) {
            result.add(new Card(suit, rank));
        }
    }
    return result;
}
```
다음은 스트림으로 구현한 코드다 중간연산으로 사용한 flatMap은 스트림의 원소 각각을 하나의 스트림으로 매핑한 다음 그 스트림들을 다시 하나의 스트림으로 합친다. 이를 평탄화라고도 한다.
```java
// 데카르트 곱 계산을 스트림 방식으로 구현
private static List<Card> newDeck() {
    return Stream.of(Suit.values())
        .flatMap(suit ->
            Stream.of(Rank.values())
                .map(rank -> new Card(suit, rank)))
        .collect(toList());
}
```
