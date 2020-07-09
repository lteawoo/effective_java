# 비검사 경고를 제거하라
```java
Set<Lark> exaltation = new HashSet();
```
위의 코드를 작성후 빌드를 하면 경고메세지로 unchecked conversion 메세지가 출력된다. 이러한 비검사 경고는 할 수 있는 한 모두 제거해야한다. 제거하면 그 코드는 타입 안정성이 보장된다. 즉 런타임에 ClassCastException이 발생할 일이 없다.

경고를 제거할 수 없지만 타입 안정하다고 확신한다면 @SuppressWarnings("unchecked") 어노테이션을 달아 경고를 숨기자.

@SuppressWarnings 어노테이션은 개별 지역변수 선언부터 클래스 전체까지 어떤 선언에도 달 수 있다. 하지만 @SuppressWarnings 어노테이션은 항상 가능한 한 좁은 범위에 적용하자. 보통은 변수 선언, 아주 짧은 메서드, 혹은 생성자가 될 것이다. 자칫 심각한 경고를 놓칠 수 있으니 절대로 클래스 전체에 적용해서는 안 된다.

한 줄이 넘는 메서드나 생성자에 달린 @SuppressWarnings 어노테이션을 발견하면 지역변수 선언 쪽으로 옮기자.
```java
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
        return (T[]) Arrays.copyOf(elements, size, a.getClass());
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size) {
        a[size] = null;
    }

    return a;
}
```
위의 코드를 컴파일하면 return (T[]) Arrays.copyOf(elements, size, a.getClass()); 구문에서 unchecked cast 경고가 발생한다. 어노테이션은 선언에만 달 수 있기 때문에 return 문에는 @SuppressWarnings를 다는게 불가능하다. 메서드 전체에 다는건 필요 이상으로 넓어지니 자제하고 반환값을 담을 지역변수를 하나 선언하고 그 변수에 어노테이션을 달아주자.

```java
/*
 * 지역변수를 추가해 @SuppressWarnings의 범위를 좁힌다.
 */
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
        // 생성한 배열과 매개변수로 받은 배열의 타입이 모두 T[]로 같으므로
        // 올바른 형변환이다.
        @SuppressWarnings("unchecked") T[] result = 
            (T[]) Arrays.copyOf(elements, size, a.getClass());
        return reuslt;
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size) {
        a[size] = null;
    }

    return a;
}
```
이 코드는 깔끔하게 컴파일되고 비검사 경고를 숨기는 범위도 최소화했다. 위의 어노테이션을 사용할 때는 항상 안전한 이유를 주석으로 남겨야한다.