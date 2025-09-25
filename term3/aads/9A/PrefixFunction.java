import java.io.*;

public class PrefixFunction {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s = reader.readLine(); 

        int[] prefix = prefixFunc(s);

        StringBuilder sb = new StringBuilder();
        for (int value : prefix) {
            sb.append(value).append(" ");
        }
        System.out.println(sb.toString().trim());  
    }

    public static int[] prefixFunc(String s) {
        int n = s.length();
        int[] f = new int[n];
        for (int i = 1; i < n; i++) {
            int m = f[i - 1];
            while (m > 0 && s.charAt(m) != s.charAt(i)) {
                m = f[m - 1];
            }
            if (s.charAt(m) == s.charAt(i)) {
                m++;
            }
            f[i] = m;
        }
        return f;
    }
}
