package kr.taeu.effectiveJava.item5;

import java.util.ArrayList;
import java.util.List;

/*
 * 싱글턴을 잘못 사용한 예
 */
public class SpellChecker2 {
  private final Lexicon dictionary = new NaverDictionary();
  
  private SpellChecker2() {}
  public static SpellChecker2 INSTANCE = new SpellChecker2();
  
  public boolean isValid(String word) {
    dictionary.find(word);
    // ...
    return true;
  }
  public List<String> suggestions(String typo) {
    dictionary.find(typo);
    // ...
    return new ArrayList<String>();
  }
}
