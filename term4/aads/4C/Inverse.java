import java.io.*;
import java.util.*;

public class Inverse {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int p = scanner.nextInt();

        if (p == 1) {
            return;
        }

        int[] inv = new int[p];
        inv[1] = 1; 

        for (int i = 2; i < p; i++) {
            inv[i] = (int)((long)(p - p / i) * inv[p % i] % p);
        }

        StringBuilder res = new StringBuilder();
        long sum = 0;
        int count = 0;

        for (int i = 1; i < p; i++) {
            sum = (sum + inv[i]) % p;
            count++;

            if (count == 100) {
                res.append(sum).append(" ");
                sum = 0;
                count = 0;
            }
        }

        if (count > 0) {
            res.append(sum).append(" ");
        }

        System.out.println(res.toString().trim());
    }
}