package kr.taeu.effectiveJava.item36;

/*
 * 비트 필드 열거 상수 - 구닥다리 기법!
 */
public class Text1 {
  public static final int STYLE_BOLD            = 1 << 0;  // 1
  public static final int STYLE_ITALIC          = 1 << 0;  // 1
  public static final int STYLE_UNDERLINE       = 1 << 0;  // 1
  public static final int STYLE_STRIKETHROUGH   = 1 << 0;  // 1
  
  // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
  public void applyStyles(int styles) { }
}
