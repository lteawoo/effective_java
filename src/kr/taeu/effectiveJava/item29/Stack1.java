package kr.taeu.effectiveJava.item29;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * item7의 stack, 제네릭 타입이어야 적절함
 */
public class Stack1 {
  private Object[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  public Stack1() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
  }
  
  public void push(Object o) {
    ensureCapacity();
    elements[size++] = o;
  }
  
  public Object pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    Object result = elements[--size];
    elements[size] = null;  // 다 쓴 참조 해제
    return result;
  }
  
  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
