import java.io.*;
import java.util.*;

public class Exponentiation {
    public static void main(String[] args) throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer token = new StringTokenizer(read.readLine());
        long a = Long.parseLong(token.nextToken());
        long n = Long.parseLong(token.nextToken());
        long m = Long.parseLong(token.nextToken());
        System.out.println(exponentiation(a, n, m));
    }

    public static long exponentiation(long a, long n, long m) {
        long res = 1;
        a = a % m;
        
        while (n > 0) {
            if ((n % 2) == 1) {
                res = (res * a) % m;
            }
            a = (a * a) % m;
            n = n / 2;
        }
        
        return res;
    }
}
