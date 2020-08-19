package kr.taeu.effectiveJava.item40;

import java.util.HashSet;
import java.util.Set;

// 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스
public class Bigram2 {
    private final char first;
    private final char second;

    public Bigram2(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 올바르게 애너테이션을 표시하고, 오버라이딩으로 수정하였다.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bigram2)) {
            return false;
        }
        Bigram2 b = (Bigram2) o;
        return b.first == first && b.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
    // 그 결과가 26이다.
    public static void main(String[] args) {
        Set<Bigram2> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram2(ch, ch));
            }
            System.out.println(s.size());
        }
    }
}
