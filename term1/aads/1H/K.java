import java.util.*;

public class K {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int k = scan.nextInt();
        long[] h = new long[n];
        for (int i = 0; i < n; i++) {
            h[i] = scan.nextLong(); 
        }
        Arrays.sort(h); 
        long res = h[k - 1]; 
        System.out.println(res);
    }
}
