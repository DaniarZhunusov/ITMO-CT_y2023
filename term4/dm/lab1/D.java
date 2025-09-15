import java.util.*;

public class D {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int r = scanner.nextInt();
        int k = scanner.nextInt();
        int[] p = new int[k + 1];
        for (int i = 0; i <= k; i++) p[i] = scanner.nextInt();

        long[] num = new long[k + 1];
        long[] den = new long[k + 1];
        Arrays.fill(den, 1);

        for (int i = 0; i <= k; i++) {
            long[] bc = new long[k + 1];
            binom(k, i, bc);

            long pi = p[i];
            long rPow = 1;
            for (int j = 0; j < i; j++) rPow *= r;

            for (int j = 0; j <= k; j++) {
                if (bc[j] == 0) continue;
                long n = pi * bc[j];
                long d = rPow * fact(k);

                long cd = lcm(den[j], d);
                num[j] = num[j] * (cd / den[j]) + n * (cd / d);
                den[j] = cd;
            }
        }

        for (int j = 0; j <= k; j++) {
            long g = gcd(num[j], den[j]);
            num[j] /= g;
            den[j] /= g;
            if (den[j] < 0) {
                num[j] *= -1;
                den[j] *= -1;
            }
        }

        for (int j = 0; j <= k; j++) {
            System.out.print(num[j] + "/" + den[j]);
            if (j < k) {
            	System.out.print(" ");
            }
        }
        System.out.println();
    }

    private static void binom(int k, int i, long[] c) {
        c[0] = 1;
        for (int m = 1; m <= k; m++) {
            for (int j = k; j >= 1; j--) {
                c[j] = c[j - 1] + (-i + (k - m + 1)) * c[j];
            }
            c[0] *= (-i + (k - m + 1));
        }
    }

    private static long fact(int n) {
        long res = 1;
        for (int i = 2; i <= n; i++) {
        	res *= i;
        }
        return res;
    }

    private static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
}