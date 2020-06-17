package kr.taeu.effectiveJava.item18;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
