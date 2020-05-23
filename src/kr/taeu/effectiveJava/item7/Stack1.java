package kr.taeu.effectiveJava.item7;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * 에러는 없지만 메모리 누수가 일어나는 스택
 * pop 실행 시 size가 줄어들지만 꺼내진 객체는 계속 참조 하고있다. GC가 처리하지 않음.
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
    return elements[--size];
  }
  
  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
