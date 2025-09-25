import java.util.Scanner;

public class BinSearch {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int m = scan.nextInt();
        int[] mas1 = new int[n];
        int[] mas2 = new int[m];
        for (int i = 0; i < n; i++) {
            mas1[i] = scan.nextInt();
        }
        for (int i = 0; i < m; i++) {
            mas2[i] = scan.nextInt();
        }
        for (int number : mas2) {
            int first = left(mas1, number);
            int last = right(mas1, number);

            if (first == -1) {
                System.out.println("0");
            } else {
                System.out.println((first + 1) + " " + (last + 1));
            }
        }
    }

    public static int left(int[] mas1, int number) {
            int left = -1;
            int right = mas1.length;
            int res = -1;
            while (left < right - 1) {
                int mid = (left + right) / 2;
                if (mas1[mid] >= number) {
                    right = mid;
                } else {
                    left = mid;
                }
            }
            if (right < mas1.length && mas1[right] == number) {
                res = right;
            }
            return res;
        }

    public static int right(int[] mas1, int number) {
        int left = -1;
        int right = mas1.length;
        int res = -1;
        while (left < right - 1) {
            int mid = (left + right) / 2;
            if (mas1[mid] <= number) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (left >= 0 && mas1[left] == number) {
            res = left;
        }
        return res;
    }
}
