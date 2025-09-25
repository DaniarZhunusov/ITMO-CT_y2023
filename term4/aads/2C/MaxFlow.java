import java.io.*;
import java.util.*;

public class MaxFlow {
	public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());
        Dinic dinic = new Dinic(n);
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken()) - 1;
            int v = Integer.parseInt(st.nextToken()) - 1;
            long c = Long.parseLong(st.nextToken());
            dinic.addEdge(u, v, c);
        }
        System.out.println(dinic.maxFlow(0, n - 1));
    }

    static class Edge {
        int to, rev;
        long flow, capacity;

        Edge(int to, int rev, long capacity) {
            this.to = to;
            this.rev = rev;
            this.capacity = capacity;
            this.flow = 0;
        }
    }

    static class Dinic {
        int n;
        List<Edge>[] graph;
        int[] level;
        int[] ptr;
        Queue<Integer> queue;

        public Dinic(int n) {
            this.n = n;
            graph = new ArrayList[n];
            for (int i = 0; i < n; i++) {
            	graph[i] = new ArrayList<>();
            }
            level = new int[n];
            ptr = new int[n];
            queue = new ArrayDeque<>();
        }

        public void addEdge(int from, int to, long capacity) {
            graph[from].add(new Edge(to, graph[to].size(), capacity));
            graph[to].add(new Edge(from, graph[from].size() - 1, 0)); 
        }

        public boolean bfs(int s, int t) {
            Arrays.fill(level, -1);
            level[s] = 0;
            queue.clear();
            queue.add(s);
            while (!queue.isEmpty()) {
                int v = queue.poll();
                for (Edge e : graph[v]) {
                    if (e.capacity - e.flow > 0 && level[e.to] == -1) {
                        level[e.to] = level[v] + 1;
                        queue.add(e.to);
                    }
                }
            }
            return level[t] != -1;
        }

        public long dfs(int v, int t, long push) {
            if (push == 0) {
            	return 0;
            }

            if (v == t) {
            	return push;
            }

            for (; ptr[v] < graph[v].size(); ptr[v]++) {
                Edge e = graph[v].get(ptr[v]);
                if (level[v] + 1 != level[e.to] || e.capacity - e.flow <= 0) {
                	continue;
                }

                long tr = dfs(e.to, t, Math.min(push, e.capacity - e.flow));
                if (tr == 0) {
                	continue;
                }

                e.flow += tr;
                graph[e.to].get(e.rev).flow -= tr;
                return tr;
            }
            return 0;
        }

        public long maxFlow(int s, int t) {
            long flow = 0;
            while (bfs(s, t)) {
                Arrays.fill(ptr, 0);
                long push;
                while ((push = dfs(s, t, Long.MAX_VALUE)) > 0) {
                    flow += push;
                }
            }
            return flow;
        }
    }
}
