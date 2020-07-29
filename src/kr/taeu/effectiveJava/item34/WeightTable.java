package kr.taeu.effectiveJava.item34;

public class WeightTable {
  public static void main(String[] args) {
    double earthWeight = Planet.EARTH.surfaceGravity();
    double mass = earthWeight / Planet.EARTH.surfaceGravity();
    for (Planet p : Planet.values()) {
      System.out.printf("%s에서의 무게는 %f이다.\n", p, p.surfaceWeight(mass));
    }
  }
}