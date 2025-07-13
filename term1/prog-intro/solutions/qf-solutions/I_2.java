import java.util.Scanner;

public class IdealPyramid {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        scanner.nextLine();

        int[] x = new int[n];
        int[] y = new int[n];
        int[] h = new int[n];

        for (int i = 0; i < n; i++) {
            String[] number = scanner.nextLine().split("\\s");
            x[i] = Integer.parseInt(number[0]);
            y[i] = Integer.parseInt(number[1]);
            h[i] = Integer.parseInt(number[2]);
        }
        int xl = Integer.MAX_VALUE;
        int xr = Integer.MIN_VALUE;
        int yl = Integer.MAX_VALUE;
        int yr = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            xl = Math.min(xl, x[i] - h[i]);
            xr = Math.max(xr, x[i] + h[i]);
            yl = Math.min(yl, y[i] - h[i]);
            yr = Math.max(yr, y[i] + h[i]);
        }
        int height = (int) Math.ceil(Math.max(xr - xl, yr - yl) / 2.0);
        int centerx = (xl + xr) / 2;
        int centery = (yl + yr) / 2;
        System.out.println(centerx + " " + centery + " " + height);
        scanner.close();
    }
}

