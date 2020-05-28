package kr.taeu.effectiveJava.item10;

import java.util.Objects;

/*
 * 한 방향으로만 작동하는(대칭성을 어긴) equals 오버라이딩
 * CaseInsensitiveString의 equals는 String을 알고있지만
 * String의 equals는 CaseInsensitiveString을 모른다.
 */
public class CaseInsensitiveString {
  private final String s;
  
  public CaseInsensitiveString(String s) {
    this.s = Objects.requireNonNull(s);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof CaseInsensitiveString) {
      return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
    }
    if (o instanceof String) { // 한 방향으로만 작동
      return s.equalsIgnoreCase((String) o);
    }
    return false;
  }
}
 