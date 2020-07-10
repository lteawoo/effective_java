package kr.taeu.effectiveJava.item28;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// 리스트 기반 Chooser - 타입 안정성 확보
public class Chooser3<T> {
  private final List<T> choiceList;
  
  public Chooser3(Collection<T> choices) {
    choiceList = new ArrayList<>(choices);
  }
  
  public Object choose() {
    Random rnd = ThreadLocalRandom.current();
    return choiceList.get(rnd.nextInt(choiceList.size()));
  }
}
