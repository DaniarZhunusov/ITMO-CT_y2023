import java.util.Scanner;

public class BinarySearch {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int k = scan.nextInt();
        int[] mas1 = new int[n];
        int[] mas2 = new int[k];
        for (int i = 0; i < n; i++) {
            mas1[i] = scan.nextInt();
        }
        for (int i = 0; i < k; i++) {
            mas2[i] = scan.nextInt();
        }
        for (int number : mas2) {
            int result = binarysearch(mas1, number);
            if (result != -1) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    public static int binarysearch(int[] mas1, int number) {
        int left = -1;
        int right = mas1.length;
        while (left < right - 1) {
            int mid = (left + right) / 2;
            if (mas1[mid] < number) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (right < mas1.length && mas1[right] == number) {
            return right;
        } else {
            return -1;
        }
    }
}
