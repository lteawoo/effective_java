package kr.taeu.effectiveJava.item13;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Stack;

/*
 * 가변 상태를 참조하는 클래스용 clone메서드.
 * elements 배열의 clone을 재귀적으로 호출 해 준다.
 */
public class Stack1 implements Cloneable{
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
    return elements[--size];
  }
  
  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
  
  @Override
  public Stack1 clone() {
    try {
      Stack1 result = (Stack1) super.clone();
      result.elements = elements.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
