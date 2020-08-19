package kr.taeu.effectiveJava.item40;

import java.util.HashSet;
import java.util.Set;

// 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
public class Bigram1 {
    private final char first;
    private final char second;

    public Bigram1(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 재정의가 아닌 오버로딩(다중정의)하였다. equals를 오버라이딩하려면 Object를 매개변수로 받아야한다.
    public boolean equals(Bigram1 b) {
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
    // 그 결과가 260이다.
    public static void main(String[] args) {
        Set<Bigram1> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram1(ch, ch));
            }
            System.out.println(s.size());
        }
    }
}
