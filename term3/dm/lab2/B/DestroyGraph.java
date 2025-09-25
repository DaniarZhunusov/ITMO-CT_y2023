import java.io.*;
import java.util.*;

public class DestroyGraph {
    static class Edge implements Comparable<Edge> {
        int u, v, cost, index;

        public Edge(int u, int v, int cost, int index) {
            this.u = u;
            this.v = v;
            this.cost = cost;
            this.index = index;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    static class DSU {
        int[] parent, rank;

        public DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    int temp = rootX;
                    rootX = rootY;
                    rootY = temp;
                }
                parent[rootY] = rootX;
                if (rank[rootX] == rank[rootY]) {
                    rank[rootX]++;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("destroy.in"));
        PrintWriter pw = new PrintWriter(new FileWriter("destroy.out"));

        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        long s = Long.parseLong(st.nextToken());

        Edge[] edges = new Edge[m];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken()) - 1;
            int v = Integer.parseInt(st.nextToken()) - 1;
            int cost = Integer.parseInt(st.nextToken());
            edges[i] = new Edge(u, v, cost, i + 1);
        }

        Arrays.sort(edges);

        DSU dsu = new DSU(n);
        Set<Integer> mstEdges = new HashSet<>();
        for (Edge edge : edges) {
            if (dsu.find(edge.u) != dsu.find(edge.v)) {
                dsu.union(edge.u, edge.v);
                mstEdges.add(edge.index);
            }
        }

        List<Edge> nonMstEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (!mstEdges.contains(edge.index)) {
                nonMstEdges.add(edge);
            }
        }

        nonMstEdges.sort((a, b) -> Integer.compare(b.cost, a.cost));

        long totalCost = 0;
        List<Integer> destroyedEdges = new ArrayList<>();
        for (Edge edge : nonMstEdges) {
            if (totalCost + edge.cost <= s) {
                totalCost += edge.cost;
                destroyedEdges.add(edge.index);
            } else {
                break;
            }
        }

        Collections.sort(destroyedEdges);
        pw.println(destroyedEdges.size());
        for (int i = 0; i < destroyedEdges.size(); i++) {
            if (i > 0) pw.print(" ");
            pw.print(destroyedEdges.get(i));
        }
        pw.println();

        br.close();
        pw.close();
    }
}
