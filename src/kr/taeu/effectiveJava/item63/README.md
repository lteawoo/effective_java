# 성능을 생각한다면 String 대신 StringBuilder
문자열 연결 연산자(+)는 편리하지만, 성능 위주의 환경에서는 성능 저하를 감내하기 어려울 것이다.

**문자열 연결 연산자로 문자열 n개를 잇는 시간은 n^2에 비례한다.** 문자열은 불변이라서 두 문자열을 연결할 경우
양쪽의 내용을 모두 복사해야 하므로 성능 저하는 피할 수 없다.
```java
public String statement() {
    // 문자열 연결을 잘못 사용한 예 - 느리다!
    String result = "";
    for (int i = 0; i < numItems(); i++) {
        result += lineForItem(i); // 문자열 연결
    }
    return result;
}
```
성능을 포기하고 싶지 않다면 StringBuilder를 사용하자
```java
public String statement2() {
    StringBuilder b = new StringBuilder(numItems() * LINE_WIDTH);
    for (int i = 0; i < numItems(); i++) {
        b.append(lineForItem(i));
    }
    return b.toString();
}
```
자바 6 이후 문자열 연결 성능을 다방면으로 개선했지만, 여전히 성능 차이는 크다.

품목을 100개로 하고 lineForItem이 길이 80인 문자열을 반환하게 하여 수행해보면 statement2가 훨씬 빠른걸 볼 수 있다.

참고로 statement2에서 전체 결과를 담기에 충분한 크기로 초기화 한점을 잊지 말자. 허나 기본값을 사용하더라도 훨씬 빠르다.