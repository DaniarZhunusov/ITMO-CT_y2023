import java.io.*;
import java.util.*;

public class Matching {
    private static int[][] graph; 
    private static int[] matching; 
    private static boolean[] used; 
    private static int n, m; 

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out));
        StringTokenizer st = new StringTokenizer(reader.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        graph = new int[n][m];
        for (int i = 0; i < n; i++) {
            String[] parts = reader.readLine().split(" ");
            for (int j = 0; j < parts.length - 1; j++) { 
                int v = Integer.parseInt(parts[j]) - 1; 
                graph[i][v] = 1;
            }
        }

        matching = new int[m];
        Arrays.fill(matching, -1);

        int size = 0;
        for (int v = 0; v < n; v++) {
            used = new boolean[n]; 
            if (dfs(v)) {
                size++;
            }
        }

        writer.println(size);
        for (int i = 0; i < m; i++) {
            if (matching[i] != -1) {
                writer.println((matching[i] + 1) + " " + (i + 1)); 
            }
        }

        reader.close();
        writer.close();
    }

    private static boolean dfs(int v) {
        if (used[v]) {
            return false;
        }
        used[v] = true;

        for (int i = 0; i < m; i++) {
            if (graph[v][i] == 1) { 
                if (matching[i] == -1 || dfs(matching[i])) {
                    matching[i] = v;
                    return true;
                }
            }
        }
        return false;
    }
}