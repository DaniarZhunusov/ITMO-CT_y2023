import java.util.*;
import java.io.*;

public class Tree {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());

        int n = Integer.parseInt(tokenizer.nextToken());
        int m = Integer.parseInt(tokenizer.nextToken());

        List<List<Integer>> graph = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int i = 0; i < m; i++) {
            tokenizer = new StringTokenizer(reader.readLine());
            int u = Integer.parseInt(tokenizer.nextToken());
            int v = Integer.parseInt(tokenizer.nextToken());
            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        Set<Integer> visited = new HashSet<>();
        List<int[]> treeEdges = new ArrayList<>();
        bfs(graph, 1, visited, treeEdges);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        for (int[] edge : treeEdges) {
            writer.write(edge[0] + " " + edge[1]);
            writer.newLine();
        }
        writer.flush();
    }

    private static void bfs(List<List<Integer>> graph, int start, Set<Integer> visited, List<int[]> treeEdges) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && treeEdges.size() < graph.size() - 2) { 
            int current = queue.poll();

            for (int neighbor : graph.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    treeEdges.add(new int[]{current, neighbor});
                    queue.add(neighbor);

                    if (treeEdges.size() == graph.size() - 2) {
                        return;
                    }
                }
            }
        }
    }
}
