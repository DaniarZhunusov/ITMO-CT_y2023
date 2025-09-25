import java.util.*;

public class Dangerous {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int k = scan.nextInt();
        int[] mas = new int[k];
        for (int i = 0; i < k; i++) {
            mas[i] = scan.nextInt();
        }
        System.out.println(numbers(n, mas));
    }

    public static int numbers(int n, int[] mas) {
        boolean[] d = new boolean[n + 1];
        for (int i : mas) {
            d[i] = true;
        }
        int[] num = new int[n + 1];
        num[0] = 1;
        for (int i = 1; i <= n; i++) {
            if (!d[i]) {
                if (i - 1 >= 0) {
                    num[i] += num[i - 1]; 
                }
                if (i - 2 >= 0) {
                    num[i] += num[i - 2]; 
                }
            }
        }
        return num[n];
    }
}
