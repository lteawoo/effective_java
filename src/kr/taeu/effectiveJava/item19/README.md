# 상속을 고려해 설계하고 문서화하라, 그러지 않았다면 상속을 금지하라 #
메서드를 재정의하면 어떤일 일어나는지를 정확히 정리하여 문서로 남겨야 한다. **상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지(자기사용) 문서로 남겨야 한다.** 클래스의 API로 공개된 메서드에서 클래스 자신의 또 다른 메서드들을 호출 할 수도 있다.그런데 마침 호출되는 메서드가 재정의 가능 메서드라면 그 사실을 호출하는 메서드의 API 설명에 적시해야 한다.(어떤 순서로 호출하는지, 각각의 호출 결과가 이어지는 처리에 어떤 영향을 주는지도 담아야 한다.)

재정의 가능이란 public과 protected 메서드 중 final이 아닌 모든 메서드를 뜻한다.

API문서의 메서드 설명 끝에서 종종 "Implementation Requirements"로 시작하는 절을 볼 수 있는데, 그 메서드의 내부 동작 방식을 설명하는 곳이다. @implSpec 태그를 붙여주면 자바독 도구가 생성해준다. 아래는 java.util.AbstractCollection에서 발췌한 예다.
```java
public boolean remove(Object o)
....
Implementation Requirements: 이 메서드는 컬력션을 순회하며 주어진 원소를 찾도록 구현되었다. 주어진 원소를 찾으면 반복자의 remove 메서드를 사용해 컬렉션에서 제거한다. 이 컬렉션이 주어진 객체를 갖고 있으나, 이 컬렉션의 iterator 메서드가 반환한 반복자가 remove 메서드를 구현하지 않았다면 UnsupportedOperationException을 던지니 주의하자.
```
이 설명에 따르면 iterator 메서드를 재정의하면 remove 메서드의 동작에 영향을 줌을 확실히 알 수 있다. 아이템 18에서는 HashSet을 상속하여 add를 재정의한 것이 addAll에까지 영향을 준다는 사실을 알 수 없었는데, 아주 대조적이다.

@implSpec 태그를 활성화하려면 명령줄 매개변수로 **-tag "implSpec:a:Implementation Requirements:"를 지정해주면 된다.(이는 자바 개발팀에서 내부적으로 사용하는 규약이고, 표준 태그는 아니다.)

효율적인 하위 클래스를 큰 어려움 없이 만들 수 있게 하려면 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 protected 메서드 형태로 공개해야 할 수도 있다. 드물게는 protected 필드로 공개해야 할수도 있다.

그렇다면 상속용 클래스를 설계 할 때 어떤 메서드를 protected로 노출해야 할지는 어떻게 결정할까? 실제 하위 클래스를 만들어 시험해 보는 것이 최선이다. 그 수는 가능한 한 적어야 한다. 한편으로는 너무 적게 노출해서 상속으로 얻는 이점마저 없애지 않도록 주의해야 한다. **상속용 클래스를 시험하는 방법은 직접 하위 클래스를 만들어보는 것이 유일한다.**

꼭 필요한 protected 멤버를 놓쳤다면 하위 클래스를 작성할 때 그 빈자리가 확연히 드러난다. 거꾸로, 하위 클래스를 여러 개 만들 때까지 전혀 쓰이지 않는 protected 멤버는 사실 private이었어야 할 가능성이 크다.(이러한 검증에는 하위 클래스 3개 정도가 적당하다, 그리고 이 중 하나 이상은 제3자가 작성해봐야 한다.)

상속용 클래스의 생성자는 직접적으로든 간접적이로든 재정의 가능 메서드를 호출해서는 안 된다. 이 규칙을 어기면 프로그램이 오작동할 것이다. 상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로 하위 클래스에서 재정의한 메서드가 하위 클래스의 생성자보다 먼저 호출된다. 이때 그 재정의한 메서드가 하위 클래스의 생성자에서 초기화하는 값에 의존한다면 의도대로 동작하지 않을 것이다.
```java
public class Super1 {
  // 잘못된 예 - 생성자가 재정의 가능 메서드를 호출했다.
  public Super1() {
    overrideMe();
  }
  
  public void overrideMe() {
    
  }
}

public class Sub1 extends Super1{
  // 초기화되지 않은 final 필드, 생성자에서 초기화한다.
  private final Instant instant;
  
  Sub1() {
    instant = Instant.now();
  }
  
  // 재정의 가능 메서드. 상위 클래스의 생성자가 호출한다.
  @Override
  public void overrideMe() {
    System.out.println(instant);
  }
  
  public static void main(String[] args) {
    Sub1 sub = new Sub1();
    sub.overrideMe();
  }
}

결과
null
2020-06-22T11:53:22.654Z
```
instant를 두 번 출력하지 않는다, 상위 클래스의 생성자는 하위 클래스의 생성자전에 overrideMe를 호출하기 때문이다. final 필드의 상태가 이 프로그램에서는 두 가지임에 주목하자. overrideMe에서 instant 객체의 메서드를 호출하려 한다면 상위 클래스의 생성자가 overrideMe를 호출할 때 NullPointerException을 던지게 된다. **private, final, static 메서드는 재정의가 불가능하니 생성자에서 안심하고 호출해도 된다.**

