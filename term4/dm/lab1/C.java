import java.io.*;
import java.util.*;

public class C {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int k = scanner.nextInt();
        int[] a = new int[k];
        int[] c = new int[k];
        for (int i = 0; i < k; i++) {
            a[i] = scanner.nextInt();
        }

        for (int i = 0; i < k; i++) {
            c[i] = scanner.nextInt();
        }

        int[] p = buildP(a, c, k);
        int[] q = buildQ(c);

        int dp = getDeg(p);
        
        printAns(dp, p);
        printAns(k, q);
    }

    private static int[] buildP(int[] a, int[] c, int k) {
        int[] p = new int[k];
        for (int n = 0; n < k; n++) {
            p[n] = a[n];
            for (int i = 1; i <= n; i++) {
                p[n] -= c[i - 1] * a[n - i];
            }
        }
        return p;
    }

    private static int[] buildQ(int[] c) {
        int[] q = new int[c.length + 1];
        q[0] = 1;
        for (int i = 1; i < q.length; i++) { 
            q[i] = -c[i - 1];
        }
        return q;
    }

    private static int getDeg(int[] p) {
        int d = p.length - 1;
        while (d >= 0 && p[d] == 0) {
            d--;
        }
        return d >= 0 ? d : 0;
    }
    
    private static void printAns(int deg, int[] poly) {
        System.out.println(deg);
        for (int i = 0; i <= deg; i++) {
            System.out.print(poly[i] + " ");
        }
        System.out.println();
    }
}