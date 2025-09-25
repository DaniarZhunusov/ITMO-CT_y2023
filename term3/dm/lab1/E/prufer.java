import java.io.*;
import java.util.*;

public class prufer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        List<List<Integer>> lists = new ArrayList<>();
        
        for (int i = 0; i <= n; i++) {
            lists.add(new ArrayList<>());
        }

        for (int i = 0; i < n - 1; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            lists.get(u).add(v);
            lists.get(v).add(u);
        }
        
        int[] prufer = pruferCode(n, lists);
        for (int node : prufer) {
            System.out.print(node + " ");
        }
        scanner.close();
    }

    public static int[] pruferCode(int n, List<List<Integer>> lists) {
        int[] degree = new int[n + 1];
        int[] prufer = new int[n - 2];
        PriorityQueue<Integer> leaves = new PriorityQueue<>();

        for (int i = 1; i <= n; i++) {
            degree[i] = lists.get(i).size();
            if (degree[i] == 1) {
                leaves.add(i);
            }
        }

        for (int i = 0; i < n - 2; i++) {
            int leaf = leaves.poll(); 
            int neighbor = lists.get(leaf).get(0); 
            
            prufer[i] = neighbor;
            degree[neighbor]--;
            degree[leaf]--;

            lists.get(neighbor).remove((Integer) leaf);

            if (degree[neighbor] == 1) {
                leaves.add(neighbor); 
            }
        }

        return prufer;
    }
}
