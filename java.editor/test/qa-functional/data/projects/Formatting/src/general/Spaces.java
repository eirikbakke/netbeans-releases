
package general;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Spaces {
    String array[] = new String[]{"a", "b"};

    static {
        int arrayInt[] = new int[3];
    }

    @Annot(name = "name")
    public void method(int a, int b) {
        while (a > 0) {
            a++;
            b = a == 0 ? b : a;
        }
        do {
            a = (b + 2) * 3;
        } while (a > 0);
        if (b == 0) {
            method(a, b);
        } else {

        }
        try (BufferedReader br = new BufferedReader(null)) {
            throw new IOException("message");
        } catch (IOException exception) {
        } finally {
        }
        for (int i = 0; i < 10; i++) {
        }
        switch (b) {
            case 1:
                break;
        }
        synchronized (this) {
        }
        Comparator c = (Comparator) (Object o1, Object o2) -> 1;
        Arrays.sort(array, String::compareTo);
        String s = (String) "aaa";
    }

    @interface Annot {
        String name() default "";
    }
}
