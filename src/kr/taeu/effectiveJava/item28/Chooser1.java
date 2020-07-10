package kr.taeu.effectiveJava.item28;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// 제네릭을 시급히 적용해야 한다.
public class Chooser1 {
  private final Object[] choiceArray;
  
  public Chooser1(Collection choices) {
    choiceArray = choices.toArray();
  }
  
  public Object choose() {
    Random rnd = ThreadLocalRandom.current();
    return choiceArray[rnd.nextInt(choiceArray.length)];
  }
}
