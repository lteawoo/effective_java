package kr.taeu.effectiveJava.item50;

import java.util.Date;

// 수정한 접근자 - 필드의 방어적 복사본을 반환한다.
public class Period3 {
    private final Date start;
    private final Date end;

    public Period3(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());

        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(this.start + "가 " + this.end + "보다 늦다.");
        }
    }

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }
}
