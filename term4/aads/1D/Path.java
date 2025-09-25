import java.io.*;
import java.util.*;

public class Path {
    private static int n, m;
    private static ArrayList<Integer>[] graph;
    private static int[] matching;
    private static boolean[] used;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        m = scanner.nextInt();

        graph = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            graph[u].add(v);
        }

        System.out.println(path());
    }

    public static int path() {
        matching = new int[n + 1];
        Arrays.fill(matching, -1);
        int result = 0;

        for (int u = 1; u <= n; u++) {
            used = new boolean[n + 1];
            if (dfs(u)) {
                result++;
            }
        }

        return n - result;
    }

    private static boolean dfs(int u) {
        if (used[u]) {
            return false;
        }
        used[u] = true;

        for (int v : graph[u]) {
            if (matching[v] == -1 || dfs(matching[v])) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }
}