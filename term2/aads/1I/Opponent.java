import java.io.*;
import java.util.*;

public class Opponent {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        
        int n = Integer.parseInt(tokenizer.nextToken());
        int[] a = new int[n];
        
        tokenizer = new StringTokenizer(reader.readLine());
        for (int i = 0; i < n; i++) {
            a[i] = Integer.parseInt(tokenizer.nextToken());
        }
        
        System.out.println(calculate(n, a));
    }
    
    private static long calculate(int n, int[] a) {
        int[] sorted = a.clone();
        Arrays.sort(sorted);
        Map<Integer, Integer> compressed = new HashMap<>();
        int rank = 1;
        for (int value : sorted) {
            if (!compressed.containsKey(value)) {
                compressed.put(value, rank++);
            }
        }
        
        int maxRank = rank - 1;
        int[] leftTree = new int[maxRank + 1];
        int[] rightTree = new int[maxRank + 1];
        
        int[] left = new int[n];
        int[] right = new int[n];
        
        for (int i = 0; i < n; i++) {
            a[i] = compressed.get(a[i]);
            left[i] = query(leftTree, maxRank) - query(leftTree, a[i]);
            update(leftTree, a[i], 1, maxRank);
        }
        
        for (int i = n - 1; i >= 0; i--) {
            right[i] = query(rightTree, a[i] - 1);
            update(rightTree, a[i], 1, maxRank);
        }
        
        long weakness = 0;
        for (int i = 0; i < n; i++) {
            weakness += (long) left[i] * right[i];
        }
        
        return weakness;
    }
    
    private static void update(int[] tree, int index, int value, int maxVal) {
        while (index <= maxVal) {
            tree[index] += value;
            index += index & -index;
        }
    }
    
    private static int query(int[] tree, int index) {
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= index & -index;
        }
        return sum;
    }
}