package kr.taeu.effectiveJava.item29;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * 제네릭으로 만드는 방법1 - 비검사 형변환
 */
public class Stack3<E> {
  private E[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  /*
   * 1. elements는 private 필드에 저장됨
   * 2. 클라이언트로 반환되거나 다른 메서드에 전달되는 일이 전혀 없다.
   * 3. push 메서드를 통해 배열에 저장되는 원소의 타입은 항상 E다.
   * 따라서 이 비검사 형변환은 확실히 안전하다.
   * 
   * 하지만 이 배열의 런타임 타입은 E[]가 아닌 Object[]다.
   */
  @SuppressWarnings("unchecked")
  public Stack3() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY]; // Type safety: unchecked cast 타입 안전하지 않음.
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
