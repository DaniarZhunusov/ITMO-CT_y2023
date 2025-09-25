import java.io.*;
import java.util.*;

public class Diophantine {
    public static void main(String[] args) throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer token = new StringTokenizer(read.readLine());
        long a = Long.parseLong(token.nextToken());
        long b = Long.parseLong(token.nextToken());
        long c = Long.parseLong(token.nextToken());

        System.out.println(Equals(a, b, c));
    }

    public static String Equals(long a, long b, long c) {
        long d = gcd(a, b);
        if (c % d != 0) {
            return "Impossible";
        }

        a /= d;
        b /= d;
        c /= d;

        long[] coef = eGcd(a, b);
        long x0 = coef[0];
        long y0 = coef[1];

        x0 *= c;
        y0 *= c;

        long k = (long) Math.ceil((double) -x0 / b);
        long x = x0 + b * k;
        long y = y0 - a * k;

        long xt = x0 + b * (k - 1);
        if (xt >= 0) {
            x = xt;
            y = y0 - a * (k - 1);
        }

        if (x < 0) {
            return "Impossible";
        }

        return x + " " + y;
    }

    public static long[] eGcd(long a, long b) {
        if (b == 0) {
            return new long[]{1, 0};
        }
        long[] coef = eGcd(b, a % b);
        long x = coef[1];
        long y = coef[0] - (a / b) * coef[1];
        return new long[]{x, y};
    }

    public static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}