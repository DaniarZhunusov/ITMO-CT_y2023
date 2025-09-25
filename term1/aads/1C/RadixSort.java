import java.util.*;

public class RadixSort {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int m = scan.nextInt();
        int k = scan.nextInt(); 
        String[] str = new String[n];
        for (int i = 0; i < n; i++) {
            str[i] = scan.next();
        }
        radixSort(str, m, k);
        for (String res : str) {
            System.out.println(res);
        }
    }

    public static void radixSort(String[] str, int m, int k) {
        for (int i = m - 1; i >= m - k; i--) {
            countingSort(str, m, i);
        }
    }

    public static void countingSort(String[] str, int m, int len) {
	    int[] count = new int[26];  
	    String[] result = new String[str.length];
	    int[] oIndex = new int[str.length]; 

	    for (int i = 0; i < str.length; i++) {
	        count[str[i].charAt(len) - 'a']++;
	        oIndex[i] = i; 
	    }

	    for (int i = 1; i < 26; i++) {
	        count[i] += count[i - 1];
	    }

	    for (int i = str.length - 1; i >= 0; i--) {
	        int charIndex = str[oIndex[i]].charAt(len) - 'a';
	        result[count[charIndex] - 1] = str[oIndex[i]];
	        count[charIndex]--;
	    }

	    System.arraycopy(result, 0, str, 0, str.length);
	}
}
