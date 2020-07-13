package kr.taeu.effectiveJava.item29;

import java.util.Arrays;
import java.util.EmptyStackException;

/*
 * 제네릭으로 만드는 방법2 - elements 타입을 Object[]로 선언
 */
public class Stack4<E> {
  private Object[] elements;
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
  public Stack4() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY]; // Type safety: unchecked cast 타입 안전하지 않음.
  }
  
  public void push(E e) {
    ensureCapacity();
    elements[size++] = e;
  }
  
  /*
   * E는 실체화 불가 타입으로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 방법이 없다.
   */
  public E pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    // E result = elements[--size]; // type mismatch
    // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
    @SuppressWarnings("unchecked") E result = (E) elements[--size]; // type safety: unchecked cast
    elements[size] = null;  // 다 쓴 참조 해제
    return result;
  }
  
  private void ensureCapacity() {
    if (elements.length == size) {
      elements = Arrays.copyOf(elements, 2 * size + 1);
    }
  }
}
