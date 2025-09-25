import java.util.*;

public class Decomposition {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        System.out.println(decomposition(n));
    }

    public static String decomposition(int n) {
        StringBuilder result = new StringBuilder();
        int div = 2;

        while (div * div <= n) {
            int count = 0;
            while (n % div == 0) {
                count++;
                n /= div;
            }
            if (count > 0) {
                if (result.length() > 0) {
                    result.append("*");
                }
                result.append(div);
                if (count > 1) {
                    result.append("^").append(count);
                }
            }
            div++;
        }

        if (n > 1) {
        	if (result.length() > 0) {
        		result.append("*");
        	}
        	result.append(n);
    	}

        return result.toString();
    }
}
