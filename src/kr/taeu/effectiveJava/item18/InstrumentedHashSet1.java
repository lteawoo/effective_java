package kr.taeu.effectiveJava.item18;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
  
  /*
   * InstrumentedHashSet1의 addAll은 addCount에 3을 더 한 후 HashSet의 addAll 구현을 호출한다.
   * HashSet의 addAll은 각 원소를 add 메서드를 호출해 추가하는데, 이때 불리는 add는 InstrumentedHashSet1에서 재정의한 메서드다.
   */
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
