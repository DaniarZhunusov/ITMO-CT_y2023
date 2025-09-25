import java.io.*;
import java.util.*;

public class pruferreverse {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int n = scanner.nextInt();
		int[] prufer = new int[n - 2];

		for (int i = 0; i < n - 2; i++) {
			prufer[i] = scanner.nextInt();
		}

		List<int[]> edges = buildTree(n, prufer);
        for (int[] edge : edges) {
            System.out.println(edge[0] + " " + edge[1]);
        }
        
        scanner.close();
	}

	public static List<int[]> buildTree(int n, int[] prufer) {
		int[] degree = new int[n + 1];
		List<int[]> edges = new ArrayList<>();

		for (int i = 0; i <= n; i++) {
			degree[i] = 1;
		}

		for (int node : prufer) {
            degree[node]++;
        }

        PriorityQueue<Integer> leaves = new PriorityQueue<>();
        for (int i = 1; i <= n; i++) {
            if (degree[i] == 1) {
                leaves.add(i);
            }
        }

        for (int node : prufer) {
            int leaf = leaves.poll();
            edges.add(new int[]{leaf, node});
            
            degree[leaf]--;
            degree[node]--;
            
            if (degree[node] == 1) {
                leaves.add(node);
            }
        }

        int u = leaves.poll();
        int v = leaves.poll();
        edges.add(new int[]{u, v});
        
        return edges;
    }
}
