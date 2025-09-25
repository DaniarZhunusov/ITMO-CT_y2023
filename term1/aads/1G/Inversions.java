import java.util.*;

public class Inversions {
    static int[] bit;
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int[] mas = new int[n];
        int[] sorted = new int[n];
        for (int i = 0; i < n; i++) {
            mas[i] = scan.nextInt();
            sorted[i] = mas[i];
        }
        Arrays.sort(sorted);
        bit = new int[n + 1];
        long inversion = 0;
        for (int i = n - 1; i >= 0; i--) {
            int index = Arrays.binarySearch(sorted, mas[i]) + 1;
            inversion += count(index - 1);
            update(index);
        }
        System.out.println(inversion);
    }

    public static void update(int index) {
        while (index < bit.length) {
            bit[index]++;
            index += index & -index;
        }
    }

    public static int count(int index) {
        int count = 0;
        while (index > 0) {
            count += bit[index];
            index -= index & -index;
        }
        return count;
    }
}
