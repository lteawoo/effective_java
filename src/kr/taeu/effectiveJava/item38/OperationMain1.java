package kr.taeu.effectiveJava.item38;

import java.util.Arrays;
import java.util.Collection;

public class OperationMain1 {
    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static void test(Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.printf("%f %s %f = %f\n", x, op, y, op.apply(x, y));
        }
    }
}
