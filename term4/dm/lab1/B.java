import java.util.*;

public class B {
    static final int MOD = 998244353;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int[] p = new int[Math.max(n + 1, m)];
        for (int i = 0; i <= n; i++) {
            p[i] = scanner.nextInt();
        }

        int[] sqrt = sqrt(p, m);
        int[] exp = exp(p, m);
        int[] ln = ln(p, m);

        printAns(sqrt);
        printAns(exp);
        printAns(ln);
    }

    private static int[] sqrt(int[] p, int m) {
        int[] res = new int[m];
        res[0] = 1;
        
        long[] coeff = new long[m];
        coeff[0] = 1;
        for (int k = 1; k < m; k++) {
            coeff[k] = coeff[k-1] * (1 - 2L*(k-1)) % MOD;
            coeff[k] = coeff[k] * modInverse(2*k) % MOD;
            if (coeff[k] < 0) coeff[k] += MOD;
        }

        int[] current = new int[m];
        current[0] = 1;

        for (int k = 1; k < m; k++) {
            current = multiply(current, p, m);
            for (int i = 0; i < m; i++) {
                res[i] = (int)((res[i] + current[i] * coeff[k]) % MOD);
                if (res[i] < 0) res[i] += MOD;
            }
        }
        return res;
    }

    private static int[] exp(int[] p, int m) {
        int[] res = new int[m];
        res[0] = 1;
        
        long[] invFact = new long[m];
        invFact[0] = 1;
        for (int k = 1; k < m; k++) {
            invFact[k] = invFact[k-1] * modInverse(k) % MOD;
        }

        int[] current = new int[m];
        current[0] = 1;

        for (int k = 1; k < m; k++) {
            current = multiply(current, p, m);
            for (int i = 0; i < m; i++) {
                res[i] = (int)((res[i] + current[i] * invFact[k]) % MOD);
                if (res[i] < 0) res[i] += MOD;
            }
        }
        return res;
    }

    private static int[] ln(int[] p, int m) {
        int[] res = new int[m];
        int[] current = new int[m];
        System.arraycopy(p, 0, current, 0, Math.min(p.length, m));

        for (int k = 1; k < m; k++) {
            long sign = (k % 2 == 1) ? 1 : (MOD - 1);
            long term = sign * modInverse(k) % MOD;
            for (int i = 0; i < m; i++) {
                res[i] = (int)((res[i] + current[i] * term) % MOD);
                if (res[i] < 0) res[i] += MOD;
            }
            if (k < m - 1) {
                current = multiply(current, p, m);
            }
        }
        return res;
    }

    private static int[] multiply(int[] a, int[] b, int m) {
        int[] res = new int[m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= i; j++) {
                if (j < a.length && (i - j) < b.length) {
                    res[i] = (int)((res[i] + 1L * a[j] * b[i - j]) % MOD);
                }
            }
        }
        return res;
    }

    private static void printAns(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i != arr.length - 1) System.out.print(" ");
        }
        System.out.println();
    }

    private static int modInverse(long a) {
        return pow((int)(a % MOD), MOD - 2);
    }

    private static int pow(int a, int b) {
        long res = 1;
        long x = a % MOD;
        while (b > 0) {
            if ((b & 1) == 1) {
                res = (res * x) % MOD;
            }
            x = (x * x) % MOD;
            b >>= 1;
        }
        return (int)res;
    }
}