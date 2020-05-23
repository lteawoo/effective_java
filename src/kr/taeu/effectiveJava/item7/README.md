# 다 쓴 객체 참조를 해제하라
```java
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
```
위의 stack 프로그램은 에러는 없지만 메모리 누수가 발생한다, 스택이 커졌다가 줄어들었을 때 스택에서 꺼내진 객체들을 GC가 회수하지 않는다. 프로그램이 더 이상 해당 객체들을 사용하지 않아도 말이다. 스택이 그 객체들의 다 쓴 참조(obsolete reference)를 여전히 가지고 있기 때문이다.
```java
  public Object pop() {
    if (size == 0) {
      throw new EmptyStackException();
    }
    Object result = elements[--size];
    elements[size] = null;  // 다 쓴 참조 해제
    return elements[--size];
  }
```
객체를 pop하고 해당 객체를 null 처리해줘 참조를 해제하면 해결된다, 자기 메모리를 직접 관리하는 클래스라면 프로그래머는 항시 메모리 누수에 주의해야 한다.

위의 stack 프로그램은 elements로 저장소 풀을 만들어 원소들을 관리한다. 원소들이 활성영역에 있는지 비활성 영역에 있는지 GC가 알길이 없다, 그러므로 비활성 영역이 되는순간 null처리하여 GC에 알려야한다.