import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class FindMax {

    static int[][] sparseTable;
    static int[][] indexTable;
    static int[] log;

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(reader.readLine());
        int[] arr = new int[n];

        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(tokenizer.nextToken());
        }

        int k = Integer.parseInt(reader.readLine());

        buildSparseTable(arr, n);

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < k; i++) {
            tokenizer = new StringTokenizer(reader.readLine());
            int l = Integer.parseInt(tokenizer.nextToken()) - 1;
            int r = Integer.parseInt(tokenizer.nextToken()) - 1;
            output.append(query(l, r) + 1).append('\n');
        }

        System.out.print(output);
    }

    static void buildSparseTable(int[] arr, int n) {
        int maxLog = (int) Math.floor(Math.log(n) / Math.log(2)) + 1;
        sparseTable = new int[n][maxLog];
        indexTable = new int[n][maxLog];
        log = new int[n + 1];

        for (int i = 0; i < n; i++) {
            sparseTable[i][0] = arr[i];
            indexTable[i][0] = i;
        }

        for (int j = 1; Math.pow(2, j) <= n; j++) {
            int step = (int) Math.pow(2, j - 1);
            for (int i = 0; i + step < n; i++) {
                int rightIndex = i + step;
                if (sparseTable[i][j - 1] >= sparseTable[rightIndex][j - 1]) {
                    sparseTable[i][j] = sparseTable[i][j - 1];
                    indexTable[i][j] = indexTable[i][j - 1];
                } else {
                    sparseTable[i][j] = sparseTable[rightIndex][j - 1];
                    indexTable[i][j] = indexTable[rightIndex][j - 1];
                }
            }
        }

        for (int i = 2; i <= n; i++) {
            log[i] = log[i / 2] + 1;
        }
    }

    static int query(int l, int r) {
        int length = r - l + 1;
        int j = log[length];
        int power = (int) Math.pow(2, j);
        if (sparseTable[l][j] >= sparseTable[r - power + 1][j]) {
            return indexTable[l][j];
        } else {
            return indexTable[r - power + 1][j];
        }
    }
}
