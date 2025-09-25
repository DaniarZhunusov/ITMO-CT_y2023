import java.util.*;

public class LCM {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long n = scanner.nextLong();
        long res;
        
        if (n < 3) {
            res = n;
        } else {
            if (n % 2 != 0) {
                res = n * (n - 1) * (n - 2);
            } else {
                if (n % 3 == 0) {
                    res = (n - 1) * (n - 2) * (n - 3);
                } else {
                    res = n * (n - 1) * (n - 3);
                }
            }
        }
        
        System.out.println(res);
        scanner.close();
    }
}