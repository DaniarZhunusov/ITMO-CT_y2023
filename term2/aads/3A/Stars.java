import java.util.Scanner;

public class Stars {
    private static int n;
    private static int[][][] stars;
    private static int[][][] prefixSum;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        n = sc.nextInt();
        stars = new int[n][n][n];
        prefixSum = new int[n][n][n];

        while (true) {
            int m = sc.nextInt();
            if (m == 1) {
                int x = sc.nextInt();
                int y = sc.nextInt();
                int z = sc.nextInt();
                int k = sc.nextInt();
                updateStars(x, y, z, k);
            } else if (m == 2) {
                int x1 = sc.nextInt();
                int y1 = sc.nextInt();
                int z1 = sc.nextInt();
                int x2 = sc.nextInt();
                int y2 = sc.nextInt();
                int z2 = sc.nextInt();
                System.out.println(queryStars(x1, y1, z1, x2, y2, z2));
            } else if (m == 3) {
                break;
            }
        }
        sc.close();
    }

    private static void updateStars(int x, int y, int z, int k) {
        stars[x][y][z] += k;
        for (int i = x; i < n; i = (i | (i + 1))) {
            for (int j = y; j < n; j = (j | (j + 1))) {
                for (int l = z; l < n; l = (l | (l + 1))) {
                    prefixSum[i][j][l] += k;
                }
            }
        }
    }

    private static int queryStars(int x1, int y1, int z1, int x2, int y2, int z2) {
        return getPrefixSum(x2, y2, z2) 
             - getPrefixSum(x1 - 1, y2, z2) 
             - getPrefixSum(x2, y1 - 1, z2) 
             - getPrefixSum(x2, y2, z1 - 1) 
             + getPrefixSum(x1 - 1, y1 - 1, z2) 
             + getPrefixSum(x1 - 1, y2, z1 - 1) 
             + getPrefixSum(x2, y1 - 1, z1 - 1) 
             - getPrefixSum(x1 - 1, y1 - 1, z1 - 1);
    }

    private static int getPrefixSum(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            return 0;
        }
        int sum = 0;
        for (int i = x; i >= 0; i = (i & (i + 1)) - 1) {
            for (int j = y; j >= 0; j = (j & (j + 1)) - 1) {
                for (int l = z; l >= 0; l = (l & (l + 1)) - 1) {
                    sum += prefixSum[i][j][l];
                }
            }
        }
        return sum;
    }
}
