import java.io.*;

public class MaxSum {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(reader.readLine());
        String[] elements = reader.readLine().split("\\s+");
        int[] mas = new int[n];
        for (int i = 0; i < n; i++) {
            mas[i] = Integer.parseInt(elements[i]);
        }
        int[] res = maxsum(mas);
        System.out.println(res[0] + " " + res[1] + " " + res[2]);
    }

    public static int[] maxsum(int[] mas) {
        if (mas.length == 0) {
            return new int[]{};
        }
        int start = 1;
        int end = 1;
        int maxSum = mas[0];  
        int sum = mas[0];
        int curstart = 1;
        for (int i = 1; i < mas.length; i++) {
            if (sum < 0) {
                sum = mas[i];
                curstart = i + 1;
            } else {
                sum += mas[i];
            }
            if (sum > maxSum) {
                maxSum = sum;
                start = curstart;
                end = i + 1;
            }
        }
        return new int[]{start, end, maxSum};
    }
}
