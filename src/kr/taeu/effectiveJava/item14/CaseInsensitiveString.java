package kr.taeu.effectiveJava.item14;

import java.util.Objects;

/*
 * Comparable을 구현할 때 일반적으로 따르는 패턴
 * CaseInsensitiveString의 참조는 CaseInsensitiveString 참조와만 비교할 수 있다는 뜻
 */
public class CaseInsensitiveString implements Comparable<CaseInsensitiveString> {
  private final String s;
  
  public CaseInsensitiveString(String s) {
    this.s = Objects.requireNonNull(s);
  }
  
  /*
   * 자바가 제공하는 비교자를 사용한다.(Comparator를 이용)
   */
  @Override
  public int compareTo(CaseInsensitiveString cis) {
    return String.CASE_INSENSITIVE_ORDER.compare(s,  cis.s);
  }
}
 