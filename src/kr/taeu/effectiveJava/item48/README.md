# 스트림 병렬화는 주의해서 적용하라
동시성 프로그래밍을 할 때는 안전성과 응답 가능 상태를 유지하기 위해 애써야 하는데, 병렬 스트림 파이프라인 프로그래밍에서도 다를 바 없다. 아이템 45에서 다루었던 메르센 소수를 생성하는 프로그램을 다시 살펴보자.
```java
public static void main(String[] args) {
  primes().map(p -> TWO.pow(p.intValueExcat()).subtract(ONE))
    .filter(mersenne -> mersenne.isProbablePrime(50))
    .limit(20)
    .forEach(System.out::println);
}

static Stream<BigInteger> primes() {
    return Stream.iterate(TWO, BigInteger::nextProbablePrime)
}
```
이 프로그램은 내컴퓨터에서 12.5초만에 완료된다. 속도를 높이고 싶어 스트림 파이프라인의 parallel()을 호출하겠다는 순진한 생각을 했다고 치자, 이렇게 하면 성능은 어떻게 변할까? 몇 퍼센트나 빨라질까? 안타깝게도 이 프로그램은 아무것도 출력하지 못하면서 cpu는 90%나 잡아먹는 상태가 무한히 계속된다.(응답불가)

무슨 일인가?, 스트림라이브러리가 이 파이프라인을 병렬화하는 방법을 찾아내지 못햇기 때문이다. 환경이 아무리 좋더라도 **데이터 소스가 Stream.iterate거나 중간 연산으로 limit을 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.**

위의 코드는 두 문제를 동시에 갖고있고, 파이프라인 병렬화는 limit을 다룰 때 cpu 코어가 남는다면 원소를 몇개 더 처리한 후 제한된 개수 이후의 결과르 버려도 아무런 해가 없다고 가정한다. 그런데 이 코드의 경우 새롭게 메르센 소수를 찾을 때마다 그 전 소수를 찾을 때보다 두배 정도 더 오래 걸린다.

원소 하나를 계산하는 비용이 대략 그이전까지의 원소 전부를 계산한 비용 합친 것만큼 든다는 뜻이다. 스트림 파이프라인을 마구잡이로 병렬화하면 안된다.

대체로 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위 일때 병렬화의 효과가 가장 좋았다. 이 자료구조들은 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어서 일으 다수의 스레드에 분배하기에 좋다는 특징이 있다. 나누는 작업은 Spliterator가 담당하며, Spliterator 객체는 Stream이나 Iterable의 spliterator 메서드로 얻을 수있다.

이 자료구들의 또 다른 중요한 점은 원소들을 순차적으로 실행할때의 참조 지역성이 뛰어나다는 것이다. 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다. 참조들이 가리키는 실체 객체들이 떨어져있으면 참조 지역성이 낮아진다. 이러면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분 시간을 멍하니 보낸다.

참조 지역성이 가장 뛰어난 자료구조는 기본 배열이다. 기본 타입 배열에서는 참조가아닌 데이터 자체가 메모리에 연속해서 저장되기 때문이다.

스트림 파이프라인의 종단연산의 동작 방식 역시 병렬 수행 효율에 영향을 준다. 종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 연산이라면 파이프라인 병렬 수행의 효과는 제한 적일 수 밖에 없다.

종단 연산 중 병렬화에 가장 적합한 것은 축소(reduction)이다. 축소는 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업으로 Stream의 reduce 메서드 중 하나, 혹은 min, max count, sum같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행한다.

직접 구현한 Stream Iterable, Collection이 병렬화의 이점을 제대로 누리게 하려면 spliterator 메서드를 반드시 재정의하고, 결과 스트림의 병렬화 성능을 강도 높게 테스트하라

결과가 잘못되거나 오동작하는 것을 안전 실패라한다. 안전실패는 병렬화한 파이프라인이 사용하는 mappers, filters, 혹은 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을때 벌어질 수있다.

Stream 명세는 이때 사용되는 함수 객체에 관한 엄중한 규약을 정의해놨다. 예컨대 stream의 reduce 연산에 건네지는 누적기(accumulator)와 결합기(combiner) 함수는 반드시 결합법칙을 만족하고 간섭받지 않고(데이터변경X), 상태를 갖지 않아야한다.

## 스트림 병렬화는 오직 성능 최적화 수단임을 기억해라
다른 최적화와 마찬가지로 사용할 가치가 있는지 성능테스트를 한후  결정해야 한다. 스트림 파이프라인의 병렬화가 효과를 제대로 발휘하는 예제를 보자
```java
// 소수 계산 스트림 파이프라인 - 병렬화에 적합
static long pi(long n) {
  return LongStream.rangeClosed(2, n)
    .mapToObj(BigInteger::valueOf)
    .filter(i -> i.isProbablePrime(50))
    .count();
}
```
n보다 작거나 같은 소수의 개수를 계산하는 함수다. 31초가 걸리는데, 여기에 parallel을 호출하면
```java
// 소수 계산 스트림 파이프라인 - 병렬화
static long pi(long n) {
  return LongStream.rangeClosed(2, n)
    .parallel()
    .mapToObj(BigInteger::valueOf)
    .filter(i -> i.isProbablePrime(50))
    .count();
}
```
9.2초로 단축됐다. 즉 쿼듴코어를 장착한 내 컴퓨터에서 이 연산은 병렬화 덕분에 3.37배나 빨라졌다. 하지만 n이 크다면 파이(n)을 이 방식으로 계산하는건 좋지 않다. 레머공식이라는 효울적인 알고리즘이 있기 때문이다.

무작위 수들로 이뤄진 스트림을 병렬화하려거든 ThreadLocalRandom(혹은 구식인 random)보다는 SplittableRandom 인스턴스를 이용하자. 정확히 이럴 때 쓰라고 설계된것이라 병렬화하면 성능이 선형으로 증가한다.

한편 ThreadLocalRandom은 단일 스레드에서 쓰고자 만들어졌다. SplittableRandom보단 느리다. 최악은 Random은 모든 연산을 동기화하기 때문에 최악이다.