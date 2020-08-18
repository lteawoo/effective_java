package kr.taeu.effectiveJava.item39;

import java.util.ArrayList;
import java.util.List;

public class Sample3 {
    @ExceptionTest2({IndexOutOfBoundsException.class, NullPointerException.class})
    public static void doublyBad() {    // 성공해야 한다.
        List<String> list = new ArrayList<>();

        // 자바명세에 따르면 다음 메서드는 IndexOutOfBoundsException
        // NullPointerException을 던질 수 있다.
        list.addAll(5, null);
    }
}
