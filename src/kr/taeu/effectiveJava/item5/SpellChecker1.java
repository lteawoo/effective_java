package kr.taeu.effectiveJava.item5;

import java.util.ArrayList;
import java.util.List;

/*
 * 정적 유틸리티를 잘못 사용한 예
 */
public class SpellChecker1 {
  private static final Lexicon dictionary = new NaverDictionary();
  
  private SpellChecker1() {} // 객체 생성 방지
  
  public static boolean isValid(String word) {
    dictionary.find(word);
    // ...
    return true;
  }
  public static List<String> suggestions(String typo) {
    dictionary.find(typo);
    // ...
    return new ArrayList<String>();
  }
}
