/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Collection;

/**
 *
 * @author sdedic
 */
public class MethodNotTooComplex {
    private Collection col;
    
    public int m() {
        int a = 0;
        OUTER: for (int i = 0; i < 10; i++) {
            int j = 0;
            try {
                do {
                    if (j % 2 == 0) {
                        a = a + Math.random() > 0.5 ? 1 : 2;
                    }
                    if (j == i / 2) {
                        break;
                    } else if (j == i / 3) {
                        continue OUTER;
                    }
                    j++;
                } while (j < i);
            } catch (NullPointerException ex) {
                a -= this.m();
            }
            for (Object o : col) {
                int x = 0;
            }
        }
        return a;
    }
}
