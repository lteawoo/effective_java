package kr.taeu.effectiveJava.item50;

import java.util.Date;

// 수정한 생성자 - 매개변수의 방어적 복사본을 만든다.
public class Period2 {
    private final Date start;
    private final Date end;

    public Period2(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다.");
        }
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }
}
