package kr.taeu.effectiveJava.item28;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Chooser를 제네릭으로 만들기 위한 첫 시도. 컴파일 안됨
public class Chooser2<T> {
  private final T[] choiceArray;
  
  public Chooser2(Collection<T> choices) {
    // choiceArray = choices.toArray(); // Type mismatch: cannot convert from Object[] to T[]
    choiceArray = (T[]) choices.toArray(); // Type safety: Unchecked cast from Object[] to T[]
  }
  
  public Object choose() {
    Random rnd = ThreadLocalRandom.current();
    return choiceArray[rnd.nextInt(choiceArray.length)];
  }
}
