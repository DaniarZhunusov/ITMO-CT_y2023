import java.util.*;
import java.io.*;

public class Cycle {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] x = new int[n][n]; 
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x[i][j] = scanner.nextInt();
            }
        }

        int[] parent = new int[n];
        boolean[] path = new boolean[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(parent, -1);

        List<Integer> cycle = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                cycle = dfs(x, i, visited, parent, path);
                if (!cycle.isEmpty()) {
                    break;
                }
            }
        }

        if (cycle.isEmpty()) {
            System.out.println("NO");
        } else {
            System.out.println("YES");
            System.out.println(cycle.size() - 1); 
            for (int i = 0; i < cycle.size() - 1; i++) { 
                System.out.print(cycle.get(i) + " ");
            }
            System.out.println();
        }
    }

    private static List<Integer> dfs(int[][] graph, int v, boolean[] visited, int[] parent, boolean[] path) {
        visited[v] = true;
        path[v] = true;

        for (int neighbor = 0; neighbor < graph.length; neighbor++) {
            if (graph[v][neighbor] == 1) { 
                if (!visited[neighbor]) {  
                    parent[neighbor] = v;  
                    List<Integer> cycle = dfs(graph, neighbor, visited, parent, path);
                    if (!cycle.isEmpty()) {
                        return cycle;
                    }
                } else if (path[neighbor] && parent[v] != neighbor) { 
                    List<Integer> cycle = new ArrayList<>();
                    int current = v;
                    while (current != neighbor) {
                        cycle.add(current + 1);
                        current = parent[current];
                    }
                    cycle.add(neighbor + 1);
                    cycle.add(v + 1); 
                    Collections.reverse(cycle);
                    return cycle;
                }
            }
        }

        path[v] = false; 
        return Collections.emptyList();
    }
}
