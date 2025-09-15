import java.util.*;
import java.io.*;

public class F {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] first = br.readLine().split(" ");
        int k = Integer.parseInt(first[0]);
        int m = Integer.parseInt(first[1]);
        final int MOD = 1000000007;

        int[] c = new int[k];
        String[] second = br.readLine().split(" ");
        for (int i = 0; i < k; i++) {
            c[i] = Integer.parseInt(second[i]);
        }

        long[] dp = new long[m + 1];
        long[] summ = new long[m + 1];
        dp[0] = 1;
        summ[0] = 1; 

        for (int w = 1; w <= m; w++) {
            long total = 0;
            for (int ci : c) {
                if (ci <= w) {
                    total = (total + summ[w - ci]) % MOD;
                }
            }
            dp[w] = total;

            long sum = 0;
            for (int i = 0; i <= w; i++) {
                sum = (sum + dp[i] * dp[w - i]) % MOD;
            }
            summ[w] = sum;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= m; i++) {
            sb.append(dp[i]).append(" ");
        }
        System.out.println(sb.toString().trim());
    }
}