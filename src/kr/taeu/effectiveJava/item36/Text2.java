package kr.taeu.effectiveJava.item36;

import java.util.Set;

/*
 * EnumSet - 비트 필드를 대체하는 현대적 기법
 */
public class Text2 {
  public enum Style {
      BOLD,
      ITALIC,
      UNDERLINE,
      STRIKETHROUGH
  }
  
  // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
  // text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
  public void applyStyles(Set<Style> styles) { }
}
