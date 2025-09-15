import java.util.*;

public class E {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int r = scanner.nextInt();
        int d = scanner.nextInt();
        int[] p = new int[d + 1];

        for (int i = 0; i <= d; i++) {
            p[i] = scanner.nextInt();
        }
        
        long[] seq = Sequence(r, d, p);
        
        long[] Q = buildQ(r, d);
        long[] P = calcP(seq, Q);
        
        printAns(P);
        printAns(Q);
    }
    
    private static long[] Sequence(int r, int deg, int[] p) {
        int nTerms = 2 * deg + 10;
        long[] seq = new long[nTerms];
        
        for (int n = 0; n < nTerms; n++) {
            long val = calculate(n, p);
            seq[n] = val * pow(r, n);
        }
        
        return seq;
    }
    
    private static long calculate(int n, int[] p) {
        long res = 0;
        long term = 1;
        
        for (int coeff : p) {
            res += coeff * term;
            term *= n;
        }
        
        return res;
    }
    
    private static long[] buildQ(int r, int deg) {
        int qDeg = deg + 1;
        long[] Q = new long[qDeg + 1];
        Q[0] = 1;
        
        for (int i = 1; i <= qDeg; i++) {
            Q[i] = Q[i-1] * (-r) * (qDeg - i + 1) / i;
        }
        
        return Q;
    }
    
    private static long[] calcP(long[] seq, long[] Q) {
        int pDeg = Q.length - 1;
        long[] P = new long[pDeg];
        
        for (int i = 0; i < pDeg; i++) {
            for (int j = 0; j <= i; j++) {
                P[i] += seq[j] * Q[i - j];
            }
        }
        
        return P;
    }
    
    private static void printAns(long[] poly) {
        int deg = poly.length - 1;
        while (deg > 0 && poly[deg] == 0) {
            deg--;
        }
        
        System.out.println(deg);
        for (int i = 0; i <= deg; i++) {
            System.out.print(poly[i] + " ");
        }
        System.out.println();
    }
    
    private static long pow(int a, int b) {
        long res = 1;
        for (int i = 0; i < b; i++) {
            res *= a;
        }
        return res;
    }
}