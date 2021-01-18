# 정확한 답이 필요하다면 float와 double은 피하라
float와 double 타입은 과학과 공학 계산용으로 설계되었다. 이진 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 '근사치'로 계산하도록 세심하게 설계되었다.

따라서 정확한 결과가 필요할 때는 사용하면 안되며, 특히 금융 관련 계산과는 맞지 않는다. 0.1 혹은 10의 음의 거듭 제곱수를 표현할 수 없기 때문이다.

예를 들어 **주머니에 1.03 달러가 있는데 그중 42센트를 썼다고 해보자**, 남은 돈을 얼마인가? 다음은 어설프게 작성한 코드다
```java
System.out.println(1.03 - 0.42);
```
안타깝게도 이 코드는 0.6100000000000001을 출력한다. 이는 특수한 사례도 아니다.

**이번에는 1달러가 있는데, 10센트짜리 사탕 9개를 샀다고 해보자**. 얼마가 남았는가?
```java
System.out.println(1.00 - 9 * 0.10);
```
이 코드는 0.9999999999999998을 출력한다.

결괏값을 출력하기 전에 반올림하면 해결되리라 생각할지 모르지만, 반올림을 해도 틀린 답이 나올 수 있다.

예를 들어 주머니에는 1달러가 있고, 선반에 10센트, 20센트, 30센트, ... 1달러짜리의 맛있는 사탕이 있다고 해보자,
10센트짜리부터 하나씩, 살 때 몇개나 살 수 있고 잔돈은 얼마인가?
```java
public static void main(String[] args) {
    double funds = 1.00;
    int itemsBought = 0;
    for (double price = 0.10; funds >= price; price += 0.10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
}
```
결과는 사탕 3개를 구입한 후 잔돈은 0.399999999999999달러가 남았음을 알게 된다. 올바로 해결하려면 **금융 계산에는 BigDecimal, int 혹은 long을 사용해야 한다.**

다음은 double 타입을 BigDecimal로 교체만 했다.
```java
public static void main(String[] args) {
    final BigDecimal TEN_CENTS = new BigDecimal(".10");
    
    int itemsBought = 0;
    BigDecimal funds = new BigDecimal("1.00");
    for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = price.add(TEN_CENTS)) {
        funds = funds.subtract(price);
        itemsBought++;
    }
    
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러):" + funds);
}
```
하지만 BigDecimal에는 단점이 두 가지 있다. 기본 타입보다 쓰기가 훨씬 불편하고, 느리다. 단발성 계산이라면 문제는 무시할 수 있지만, 쓰기 불편하다는 점은 아쉽다.

BigDecimal 대안으로 int와 long 타입을 쓸 수 도 있다. 하지만 크기가 제한되고, 소수점을 직접 관리해야 한다.