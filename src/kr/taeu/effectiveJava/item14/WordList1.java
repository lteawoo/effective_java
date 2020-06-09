package kr.taeu.effectiveJava.item14;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/*
 * 명령줄 인수들을 중복을 제거하고 알파벳순으로 출력하는 프로그램
 */
public class WordList1 {
  public static void main(String[] args) {
    Set<String> s = new TreeSet<>();
    Collections.addAll(s, args);
    System.out.println(s);
  }
}
