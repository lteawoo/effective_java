package kr.taeu.effectiveJava.item34;

public class Operation3_main {
  public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    for (Operation3 op : Operation3.values()) {
      System.out.printf("%f %s %f = %f\n", x, op, y, op.apply(x, y));
    }
  }
}
