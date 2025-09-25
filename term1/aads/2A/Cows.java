import java.util.*;

public class Cows {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int k = scan.nextInt();
        int[] mas = new int[n];
        for (int i = 0; i < n; i++) {
            mas[i] = scan.nextInt();
        }
        System.out.println(stalls(mas, n, k));
    }

    public static int stalls(int[] mas, int n, int k) {
        Arrays.sort(mas);
        int left = -1;
        int right = (int) Math.pow(10, 9);
        int res = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int last = mas[0];
            int cows = 1;

            for (int i : mas) {
                if (i - last >= mid) {
                    cows++;
                    last = i;
                }
            }

            if (cows >= k) {
                res = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return res;
    }
}
