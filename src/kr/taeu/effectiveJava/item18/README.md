### 상속보다는 컴포지션을 사용하라 ###
상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다. 메서드 호출과 달리 상속은 캡슐화를 깨뜨린다. 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다. 이러한 이유로 상위 클래스 설계자가 확장을 충분히 고려하고 문서화도 제대로 해두지 않으면 하위클래스는 상위 클래스의 변화에 발맞춰 수정돼야만 한다.

```java
/*
 * 상속을 잘못 사용함.
 */
public class InstrumentedHashSet1<E> extends HashSet<E> {
  // 추가된 원소의 수
  private int addCount = 0;
  
  public InstrumentedHashSet1() {
    
  }
  
  public InstrumentedHashSet1(int initCap, float loadFactor) {
     super(initCap, loadFactor);
  }
  
  @Override
  public boolean add(E e) {
    addCount++;
    return super.add(e);
  }
  
  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
  }
  
  public int getAddCount() {
    return addCount;
  }
  
  public static void main(String[] args) {
    InstrumentedHashSet1<String> s = new InstrumentedHashSet1<>();
    s.addAll(Arrays.asList("틱", "탁탁", "펑"));
    
    System.out.println(s.getAddCount());
  }
}
```
InstrumentedHashSet1의 addAll은 addCount에 3을 더 한 후 HashSet의 addAll 구현을 호출한다. HashSet의 addAll은 각 원소를 add 메서드를 호출해 추가하는데, 이때 불리는 add는 InstrumentedHashSet1에서 재정의한 메서드다. 그래서 결과가 6이 나오는것이다.

이 경우 하위클래스에서 addAll 메서드를 재정의 하지 않으면 당장은 제대로 동작한다. 하지만 이런 해결법은 HashSet의 addAll이 add 메서드를 이용해 구현했음을 가정한 해법이라는 한계를 지닌다. 이처럼 자신의 다른 부분을 사용하는 자기사용(self-use) 여부는 해당 클래스의 내부 구현 방식에 해당하며, 자바 플랫폼 전반적인 정책인지, 그래서 다음 릴리스에서도 유지될지는 알 수 없다. 따라서 이런 가정에 기댄 InstrumentedHashSet1도 깨지기 쉽다.

이렇나 문제를 모두 피해가는 묘안이 있다. 기존 클래스를 확장하는 대신, 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하자. 기존 클래스가 새로운 클래스의 구성요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션(composition: 구성)이라 한다. 새 클래스의 인스턴스 메서드들은 (private 필드로 참조하는) 기존 클래스의 대응하는 메서드를 호출해 그 결과를 반환한다. 이 방식을 전달(forwarding)이라 하며, 새 클래스의 메서드들을 전달 메서드(forwarding method)라 부른다. 그 결과 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나며, 심지어 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향받지 않는다.

```java
/*
 * 래퍼 클래스 - 상속 대신 컴포지션을 사용했다.
 * 전달 메서드만으로 이뤄진 재사용 가능한 전달 클래스(forwardingSet)으로 구성.
 */
public class InstrumentedSet1<E> extends ForwardingSet1<E> {
  // 추가된 원소의 수
  private int addCount = 0;
  
  public InstrumentedSet1(Set<E> s) {
    super(s);
  }
  
  @Override
  public boolean add(E e) {
    addCount++;
    return super.add(e);
  }
  
  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
  }
  
  public int getAddCount() {
    return addCount;
  }
  
  public static void main(String[] args) {
    InstrumentedSet1<String> s = new InstrumentedSet1<>(new HashSet<String>());
    s.addAll(Arrays.asList("틱", "탁탁", "펑"));
    
    System.out.println(s.getAddCount());
  }
}

/*
 * 재사용할 수 있는 전달 클래스
 */
public class ForwardingSet1<E> implements Set<E> {
  // 컴포지션 구성
  private final Set<E> s;
  
  public ForwardingSet1(Set<E> s) {
    this.s = s;
  }
  
  @Override
  public int size() {
    return s.size();
  }

  @Override
  public boolean isEmpty() {
    return s.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return s.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return s.iterator();
  }

  @Override
  public Object[] toArray() {
    return s.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return s.toArray(a);
  }

  @Override
  public boolean add(E e) {
    return s.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return s.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return s.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return s.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return s.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return s.removeAll(c);
  }

  @Override
  public void clear() {
    s.clear();
  }
  
}
```
InstrumentedSet은 HashSet의 모든 기능을 정의한 Set 인터페이스를 활용해 설계되어 견고하고 아주 유연하다. 임의의 Set에 계측 기능을 덧씌워 새로운 Set으로 만드는 것이 이 클래스의 핵심이다. 상속 방식은 구체 클래스 각각을 따로 확장해야 하며, 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 정의해줘야 한다. 하지만 컴포지션 방식은 한 번만 구현해두면 어떠한 Set 구현체라도 계측할 수 있으며, 기존 생성자들과도 함께 사용할 수 있다.

다른 Set 인스턴스를 감싸고 있다는 뜻에서 InstrumentedSet 클래스를 래퍼 클래스라 하며, 다른 Set에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴이라고 한다. 컴포지션과 전달의 조합은 넓은 의미로 위임(delegation)이라고 부른다. 단 엄밀히 따지면 래퍼 객체가 내부 객체에 자기 자신의 참조를 넘기는 경우만 위임에 해당한다.

래퍼 클래스는 단점이 거의 없다. 단 한가지 래퍼 클래스가 콜백 프레임워크와는 어울리지 않는다는 점만 주의하면 된다. 콜백 프레임워크에서는 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출 때 사용하도록 한다. 내부 객체는 자신을 감싸고 있는 래퍼의 존재를모르니 대신 자신(this)의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 된다. 이를 SELF 문제라고 한다. 

상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 쓰여야 한다. 다르게 말하면, 클래스 B가 클래스 A와 is-a 관계일 때만 클래스 A를 상속해야 한다. 클래스 A를 상속하는 클래스 B를 작성하려 한다면 "B가 정말 A인가?"라고 자문해보자. "그렇다"라고 확신할 수 없다면 B는 A를 상속해서는 안 된다.

