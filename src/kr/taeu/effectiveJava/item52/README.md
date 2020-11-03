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