import java.io.*;
import java.util.*;

public class Equation {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        int t = scanner.nextInt();

        for (int i = 0; i < t; i++) {
        	long a = scanner.nextLong();
            long b = scanner.nextLong();
            long n = scanner.nextLong();
            long m = scanner.nextLong();

	        long[] solution = equation(a, n, b, m);
	            
	        if (solution == null) {
	            System.out.println("NO");
	        } else {
	            System.out.println("YES " + solution[0] + " " + solution[1]);
	        }
	    }
	}

	public static long[] eGcd(long a, long b) {
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        long[] coef = eGcd(b, a % b);
        long gcd = coef[0];
        long x = coef[2];
        long y = coef[1] - (a / b) * coef[2];
        return new long[]{gcd, x, y};
    }

    public static long[] equation(long a, long n, long b, long m) {
        n = Math.abs(n);
        m = Math.abs(m);
        
        long[] egcd = eGcd(n, m);
        long g = egcd[0];
        long u = egcd[1];
        
        if ((a - b) % g != 0) {
            return null;
        }
        
        long lcm = n / g * m;
        
        long x0 = (a + (b - a) / g * u % (m / g) * n) % lcm;
        if (x0 < 0) {
            x0 += lcm;
        }
        
        return new long[]{x0, lcm};
    }
}