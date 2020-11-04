# 다중정의는 신중히 사용하라
다음은 컬렉션을 집합, 리스트, 그 외로 구분하고자 만든 프로그램이다.
```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> s) {
        return "리스트";
    }

    public static String classify(Collection<?> s) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
            new HashSet<String>(),
            new ArrayList<BigInteger>(),
            new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections) {
            System.out.println(classify(c));
        }
    }
}
```
'집합', '리스트', '그 외'를 출력할 것 같지만 실제로 수행해보면 '그 외'만 세번 연달아 출력한다. 이유는 다중정의된 세 classify 중 어느 메서드를 호출할지가 컴파일타임에 정해지기 때문이다. 컴파일타임에는 for 문 안의 c는 항상 Collection<?> 타입이다. 런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다. 따라서 컴파일타임의 매개변수 타입을 기준으로 항상 세 번째 메서드만 호출하는 것이다.

이 처럼 직관과 어긋나는 이유는 **재정의한 메서드는 동적으로 선택되고, 다중정의한 메서드는 정적으로 선택되기 때문이다.** 메서드를 재정의했다면 해당 객체의 런타임 타입이 어떤 메서드를 호출할지의 기준이 된다. 모두 알다시피, 메서드 재정의란 상위 클래스가 정의한 것과 똑같은 시그니처의 메서드를 하위 클래스에서 다시 정의한 것을 말한다. 메서드를 재정의 한 다음 '하위 클래스의 인스턴스'에서 그 메서드를 호출하면 재정의한 메서드가 호출 된다.
```java
/* 재정의된 메서드 호출 - 이 프로그램은 무엇을 출력할까? */
class Wine {
    String name() { return "포도주"; }
}

class SparklingWine extends Wine {
    @Override String name() { return "발포성 포도주"; }
}

class Champagne extends SparklilngWine {
    @Override String name() { return "샴페인"; }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
            new Wine(), new SparklingWine(), new Champagne());
        
        for (Wine wine : wineList) {
            System.out.println(wine.name());
        }
    }
}
```
모두 예상한 것과 같이 '포도주', '발포성 포도주', '샴페인'을 차례로 출력한다. for 문에서의 컴파일타임 타입이 모두 Wine인 것에 무관하게 항상 '가장 하위에서 정의한' 재정의 메서드가 실행 되는 것이다.

한편, 다중정의 메서드 사이에서는 객체의 런타임 타입은 중요하지 않다. 선택은 컴파일타임에, 오직 매개변수의 컴파일타임 타입에 의해 이뤄진다.

맨 위의 CollectionClassifier 에서의 목적은 매개변수의 런타임 타입에 기초해 적절한 다중정의 메서드로 자동 분배하는 것이다. 이 문제는 모든 classify를 하나로 합친 후 instanceof로 명시적으로 검사하면 해결된다.
```java
public static String classify(Collection<?> c) {
    return c instanceof Set ? "집합" :
           c instanceof List ? "리스트" : "그 외";
}
```
다중정의가 혼동을 일으키는 상황을 피해야한다 그러기 위해서는 웬만해서는 안전하고 보수적이게 매개변수 수가 같은 다중정의는 만들지 말자. 가변인수(varargs)를 사용하는 메서드라면 다중정의를 아예 하지 말아야 한다. 이 규칙만 잘 따르면 어던 다중정의 메서드가 호출될지 헷갈일 일은 없을 것이다. 다중정의하는 대신 메서드 이름을 다르게 지어주는 길도 있다.

이번에는 ObjectOutputStream 클래스를 살펴보자. 이 클래스의 write 메서드는 모든 기본 타입과 일부 참조 타입용 변형을 가지고 있다. 그런데 다중정의가 아닌, 모든 메서드에 다른 이름을 지어주는 길을 택했다. writeBoolean(boolean), writeInt(int), writeLong(long) 같은 식이다.

위의 방식이 다중정의 보다 더 나은 점은 read 메서드의 이름과 짝을 맞추기 좋다. readLong() 등등이 있다.

한편, 생성자는 이름을 다르게 지을 수 없으니 두 번째 생성자부터는 무조건 다중정의가 된다. 하지만 정적팩터리라는 대안을 활용할 수 있는 경우가 많다.

그래도 여러 생성자가 같은 수의 매개변수를 받아야 하는 경우를 완전히 피해갈 수는 없을 테니, 그럴 때를 대비해 안전 대책을 배워두면 도움이 될 것이다.

