import java.util.*;
import java.io.*;

public class Nfunction {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = reader.readLine(); 
        
        int[] result = nFunc(s);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            sb.append(result[i]).append(" ");
        }
        System.out.print(sb.toString().trim());
    }

    public static int[] nFunc(String s) {
        int n = s.length();
        int[] f = new int[n];
        
        int l = 0; 
        int r = 0;
        f[0] = n;
        
        for (int i = 1; i < n; i++) {
            f[i] = Math.max(0, Math.min(r - i, f[i - l]));
            
            while (i + f[i] < n && s.charAt(f[i]) == s.charAt(i + f[i])) {
                f[i]++;
            }
            
            if (i + f[i] > r) {
                l = i;
                r = i + f[i];
            }
        }
        
        return f;
    }
}
