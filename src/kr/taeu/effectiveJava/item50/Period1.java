package kr.taeu.effectiveJava.item50;

import java.util.Date;

// 기간을 표현하는 클래스 - 불변식을 지키지 못했다.
public class Period1 {
    private final Date start;
    private final Date end;

    public Period1(Date start, Date end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }

        this.start = start;
        this.end = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }
}
