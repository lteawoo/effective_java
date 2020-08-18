package kr.taeu.effectiveJava.item39;

import java.util.ArrayList;
import java.util.List;

// 반복 가능 애너테이션을 두 번 단 코드
public class Sample4 {
    @ExceptionTest3(IndexOutOfBoundsException.class)
    @ExceptionTest3(NullPointerException.class)
    public static void doublyBad() {    // 성공해야 한다.
        List<String> list = new ArrayList<>();

        // 자바명세에 따르면 다음 메서드는 IndexOutOfBoundsException
        // NullPointerException을 던질 수 있다.
        list.addAll(5, null);
    }
}
