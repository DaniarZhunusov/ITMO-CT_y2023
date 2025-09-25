import java.util.*;
import java.io.*; 

public class Topsort {
    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in);
    	int n = scanner.nextInt();
    	int m = scanner.nextInt();

    	List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            edges.add(new int[]{u, v});
        }

        int[] permutation = new int[n];
        for (int i = 0; i < n; i++) {
            permutation[i] = scanner.nextInt();
        }

        int[] index = new int[n + 1];
        for (int i = 0; i < n; i++) {
            index[permutation[i]] = i;
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            if (index[u] > index[v]) {
                System.out.println("NO");
                return;
            }
        }  
        System.out.println("YES");
    }
}