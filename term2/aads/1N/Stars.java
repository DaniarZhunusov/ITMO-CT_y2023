import java.io.*;
import java.util.*;

public class Stars {

    static class FenwickTree {
        private int[] tree;

        public FenwickTree(int size) {
            tree = new int[size + 1];
        }

        public void add(int i, int d) {
            while (i < tree.length) {
                tree[i] += d;
                i += i & -i;
            }
        }

        public int sum(int i) {
            int sum = 0;
            while (i > 0) {
                sum += tree[i];
                i -= i & -i;
            }
            return sum;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(reader.readLine());
        
        int[][] stars = new int[n][2];
        for (int i = 0; i < n; i++) {
            String[] parts = reader.readLine().split(" ");
            stars[i][0] = Integer.parseInt(parts[0]);
            stars[i][1] = Integer.parseInt(parts[1]);
        }

        int[] X_coords = new int[n];
        for (int i = 0; i < n; i++) {
            X_coords[i] = stars[i][0];
        }
        int[] compressed_X = Coordinates(X_coords);

        FenwickTree fenwickTree = new FenwickTree(n);
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            int compressed_x = compressed_X[i];
            int level = fenwickTree.sum(compressed_x);
            result[level]++;
            fenwickTree.add(compressed_x, 1);
        }

        for (int count : result) {
            System.out.println(count);
        }
    }

    private static int[] Coordinates(int[] coords) {
        int n = coords.length;
        int[] sortedCoords = coords.clone();
        Arrays.sort(sortedCoords);

        Map<Integer, Integer> coordMap = new HashMap<>();
        int compressedValue = 1;
        for (int coord : sortedCoords) {
            if (!coordMap.containsKey(coord)) {
                coordMap.put(coord, compressedValue++);
            }
        }

        int[] compressed = new int[n];
        for (int i = 0; i < n; i++) {
            compressed[i] = coordMap.get(coords[i]);
        }
        return compressed;
    }
}