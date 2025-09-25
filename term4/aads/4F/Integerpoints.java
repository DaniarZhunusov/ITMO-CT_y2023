import java.io.*;
import java.util.*;

public class Integerpoints {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int x1 = scanner.nextInt();
        int y1 = scanner.nextInt();
        int x2 = scanner.nextInt();
        int y2 = scanner.nextInt();
        System.out.println(countIntegerPoints(x1, y1, x2, y2));
    }

    public static int countIntegerPoints(int x1, int y1, int x2, int y2) {
        if (Math.abs(x2 - x1) == 0 && Math.abs(y2 - y1) == 0) {
            return 1;
        }
        
        int gcdVal = gcd(Math.abs(x2 - x1), Math.abs(y2 - y1));
        return gcdVal + 1;
    }
    
    public static int gcd(int a, int b) {
    	if (a == 0) {
    		return b;
    	}
    	
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }
}