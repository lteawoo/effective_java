package kr.taeu.effectiveJava.item7;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * 다 쓴 객체의 참조를 해제하여 메모리 누수를 막는다.
 */
public class Stack2 {
  private Object[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  public Stack2() {
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
