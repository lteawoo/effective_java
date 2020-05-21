package kr.taeu.effectiveJava.item5;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * 싱글턴을 잘못 사용한 예
 */
public class SpellChecker3 {
  private final Lexicon dictionary;
  
  public SpellChecker3(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }
  
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
