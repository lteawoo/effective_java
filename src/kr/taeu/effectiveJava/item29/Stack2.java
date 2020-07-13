package kr.taeu.effectiveJava.item29;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * 제네릭 스택으로 가는 첫단계 - 컴파일 되지 않는다.
 */
public class Stack2<E> {
  private E[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  public Stack2() {
    elements = new E[DEFAULT_INITIAL_CAPACITY]; // E는 실체화 불가 타입으로 배열 생성 불가
  }
  
  public void push(E e) {
    ensureCapacity();
    elements[size++] = e;
  }
  
  public E pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    E result = elements[--size];
    elements[size] = null;  // 다 쓴 참조 해제
    return result;
  }
  
  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
