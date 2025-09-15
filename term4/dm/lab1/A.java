import java.io.*;
import java.util.*;

public class A {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int[] p = new int[n + 1];
        int[] q = new int[m + 1];
        final int MOD = 998244353;

        for (int i = 0; i < n + 1; i++) {
            p[i] = scanner.nextInt();
        }

        for (int i = 0; i < m + 1; i++) {
            q[i] = scanner.nextInt();
        }

        int[] sum = add(p, q, MOD);
        printAns(sum);

        int[] result = mul(p, q, MOD);
        printAns(result);

        int[] division = divide(p, q, MOD, 1000);
        printDivide(division);
    }

    private static int[] add(int[] p, int[] q, int mod) {
        int maxL = Math.max(p.length, q.length);
        int[] res = new int[maxL];
        for (int i = 0; i < maxL; i++) {
            int pi = i < p.length ? p[i] : 0;
            int qi = i < q.length ? q[i] : 0;
            res[i] = (pi + qi) % mod;
        }
        return res;
    }

    private static int[] mul(int[] p, int[] q, int mod) {
        int[] res = new int[p.length + q.length - 1];
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < q.length; j++) {
                res[i + j] = (res[i + j] + (int)((long)p[i] * q[j] % mod)) % mod;
            }
        }
        return res;
    }

    private static int[] divide(int[] p, int[] q, int mod, int k) {
        int[] ans = new int[k];
        int inv = modInv(q[0], mod); 

        for (int i = 0; i < k; i++) {
            ans[i] = i < p.length ? p[i] : 0;
            long sum = 0;
            for (int j = 1; j < q.length && j <= i; j++) {
                sum = (sum + (long)q[j] * ans[i - j]) % mod;
            }
            ans[i] = (int)((ans[i] - sum + mod) % mod);
            ans[i] = (int)((long)ans[i] * inv % mod); 
        }
        return ans;
    }

    private static int modInv(int a, int mod) {
        a = a % mod;
        if (a == 0) {
            return 0; 
        }
        for (int x = 1; x < mod; x++) {
            if ((a * x) % mod == 1) {
                return x;
            }
        }
        return -1; 
    }

    private static void printAns(int[] arr) {
        int degree = arr.length - 1;
        while (degree >= 0 && arr[degree] == 0) {
            degree--;
        }
        if (degree < 0) {
            degree = 0;
        }
        System.out.println(degree);
        for (int i = 0; i <= degree; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    private static void printDivide(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append(i < arr.length ? arr[i] : 0).append(" ");
        }
        System.out.println(sb.toString().trim());
    }
}