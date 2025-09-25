import java.util.*;

public class Fibonachi {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] dp = new int[1000];
        int res = fibo(n, dp);
        System.out.println(res);
    }

    public static int fibo(int n, int[] dp) {   
    	if (dp[n] == 0) {
    		if (n == 1 || n == 2) {
    			dp[n] = 1;
    	} else {
    		dp[n] = fibo(n - 1, dp) + fibo(n - 2, dp);
    	}
    }
	return dp[n];
	}
}
