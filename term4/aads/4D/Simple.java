import java.io.*;
import java.math.BigInteger;

public class Simple {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());

        for (int i = 0; i < n; i++) {
            BigInteger num = new BigInteger(br.readLine());
            if (simple(num)) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    public static boolean simple(BigInteger num) {
        if (num.compareTo(BigInteger.TWO) < 0) {
            return false;
        }
        if (num.equals(BigInteger.TWO) || num.equals(BigInteger.valueOf(3))) {
            return true;
        }
        if (num.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        int[] base = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};
        
        BigInteger d = num.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        for (int a : base) {
            if (BigInteger.valueOf(a).compareTo(num) >= 0) {
                continue;
            }
            BigInteger x = BigInteger.valueOf(a).modPow(d, num);
            if (x.equals(BigInteger.ONE) || x.equals(num.subtract(BigInteger.ONE))) {
                continue;
            }
            boolean composite = true;
            for (int i = 0; i < s - 1; i++) {
                x = x.modPow(BigInteger.TWO, num);
                if (x.equals(num.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }
            if (composite) {
                return false;
            }
        }
        return true;
    }
}