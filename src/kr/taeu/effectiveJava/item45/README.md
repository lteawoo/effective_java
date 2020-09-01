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