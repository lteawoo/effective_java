# 반환 타입으로는 스트림보다 컬렉션이 낫다.
원소 시퀀스, 즉 일련의 원소를 반환하는 메서드는 수 없이 많다. 자바 7까지는 이런 메서드의 반환 타입으로 Collection, Set, List 같은 컬렉션 인터페이스, 혹은 Iterable이나 배열을 썼다. 그런데 자바 8이 스트림이라는 개념을 들고 오면서 이 선택이 아주 복잡한 일이 되어버렸다.

원소 시퀀스를 반환할 때는 당연히 스트림을 사용해야 한다는 이야기를 들어봤을지 모르겠지만, 아이템 45에서 이야기했듯이 스트림은 반복(iteration)을 지원하지 않는다. 따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다. API를 스트림만 반환하도록 짜놓으면 반환된 스트림을 for-each로 반복하길 원하는 사용자는 당연히 불만을 토로할 것이다.

사실 Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐 아니라, Iterable 인터페이스가 정의한 방식대로 동작한다. 그럼에도 for-each로 스트림을 반복할 수 없는 까닭은 바로 Stream이 Iterable을 확장(extend)하지 않아서다.

안타깝게도 이 문제를 해결해줄 멋진 우회로는 없다. 얼핏 보면 Stream의 iterator 메서드에 메서드 참조를 건네면 해결될 것 같다. 코드가 좀 지저분하고 직관성이 떨어지지만 못 쓸 정도는 아니다.
```java
// 자바 타입 추론의 한계로 컴파일되지 않는다.
for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) {
  // 프로세서를 처리한다.
}
```
아쉽게도 이 코드는 컴파일 오류를 낸다.
```
error: method reference not expected here
for (ProcessHandle ph : (이곳)ProcessHandle.allProcesses()::iterator) {}
```
이 오류를 바로잡으려면 메서드 참조를 매개변수화된 Iterable로 적절히 형변환해줘야 한다.
```java
// 스트림을 반복하기 위한 '끔찍한' 우회 방법
for (ProcessHandle ph : (Iterable<ProcessHandle>ProcessHandle.allProcesses()::iterator) {
  // 프로세서를 처리한다.
}
```
작동은 하지만 난잡하다. 어댑터 메서드를 사용하면 상황이 나아진다.
```java
// Stream<E>를 Iterable<E>로 중개해주는 어댑터
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
  return stream::iterator;
}

for (ProcessHandle p : iterableOf(ProcessHandle.allProcesses()) {
  // 프로세스를 처리한다.
}
```
반대로 Iterable만 반환하면 스트림 파이프라인에서 처리하려는 개발자가 화낼것이다. 반대의 경우도 만들어보자
```java
// Iterable<E>를 Stream<E>로 중개해주는 어댑터
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
  return StreamSupport.stream(iterable.spliterator(), false);
}
```
객체 시퀀스를 반환하는 메서드를 작성하는데, 이 메서드가 오직 스트림 파이프라인에서만 쓰일 걸 안다면 마음 놓고 스트림을 반환하게 해주자. 반대로 Iterable도 마찬가지다 하지만 공개 API를 작성하는 경우는 둘다 고려해야한다.

Collection 인터페이스는 Iterable의 하위 타입이고, stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다. 따라서 **원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.**

반환할 시퀀스가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션을 구현하는 방안을 검토해보자. 예컨대 주어진 집합의 멱집합(한 집합의 모든 부분집합을 원소로 하는 집합)을 반환하는 상황이다.{a, b c}의 멱집합은 {{}, {a}, {b}, {c}, {a, b}, {a, c}, {b, c}, {a, b, c}}이다. 원소 개수가 n개면 멱집합의 원소 개수는 2^n개가 된다. 그러니 멱집합을 표준 컬렉션 구현체에 저장하려는 생각은 위험하다. 하지만 AbstractList를 이용하면 훌륭한 전용 컬렉션을 손쉽게 구현할 수 있다.

비결은 멱집합을 구성하는 각 원소의 인덱스를 비트 벡터로 사용하는 것이다. 인덱스의 n번째 비트 값은 멱집합의 해당 원소가 원래 집합의 n번째 원소를 포함하는지 여부를 알려준다. 따라서 0부터 2^n-1까지의 이진수와 원소 n개인 집합의 멱집합과 자연스럽게 매핑된다.
```java
// 입력 집합의 멱집합을 전용 컬렉션에 담아 반환한다.
public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
        List<E> src = new ArrayList<>(s);
        if (src.size() > 30) {
            throw new IllegalArgumentException("집합에 원소가 너무 많습니다.(최대 30개).: " + s);
        }

        return new AbstractList<Set<E>>() {
            @Override
            public int size() {
                // 멱집합의 크기는 2를 원래 집합의 원소 수만큼 거듭제곱 한 것과 같다.
                return 1 << src.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof Set && src.containsAll((Set)o);
            }

            @Override
            public Set<E> get(int index) {
                Set<E> result = new HashSet<>();
                for (int i = 0; index != 0; i++, index >>= 1) {
                    if ((index & 1) == 1) {
                        result.add(src.get(i));
                    }
                }

                return result;
            }
        };
    }
}
```
AbstractCollection을 활용해서 Collection 구현체를 구현할 때는 Iterable용 메서드 외에 2개만 더구현하면 된다. Contains와 size다. 이 메서드들은 손쉽게 효율적으로 구현할 수 있다.

때로는 단순히 구현하기 쉬운 쪽을 선택하기도 한다. 예컨대 입력 리스트의 부분리스트를 모두 반환하는 메서드를 작성한다고 해보자. 필요한 부분리스트를 만들어 표준 컬렉션에 담는 코드는 단 3줄이면 충분하다. 하지만 이 컬렉션은 입력 리스트 크기의 거듭제곱만큼 메모리를 차지한다.

입력리스트의 모든 부분리스트를 스트림으로 구현하기는 어렵지 않다 .약간의 통찰만 있으면 된다. 첫 번째 원소를 포함하는 부분리스트를 그 리스트의 프리픽스(prefix)라 해보자. 예를 들어 (a, b, c)의 프리픽스는 (a), (ab), (abc)가 된다.같은 식으로 마지막 원소를 포함하는 부분리스트를 그 리스트의 서픽스(suffix)라 하자, 따라서 (a,b,c)의 서픽스는 (a,b,c), (b,c), (c)가 된다.

어떤 리스트의 부분리스트는 그리스트의 프리픽스의 서픽스(혹은 서픽스의 프리픽스)에 빈 리스트 하나만 추가하면 된다. 이과정은 직관적으로 구현할 수 있다.
```java
// 입력 리스트의 모든 부분리스트를 스트림으로 반환한다.
public class SubLists {
  public static <E> Stream<List<E>> of(List<E> list) {
    return Stream.concat(Stream.of(Collections.emptyList()),
      prefixes(list).flatMap(SubLists::suffixes));
  }


}
```