import java.util.*;

public class MinCostMaxFlow {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();  
        int m = sc.nextInt(); 

        Flow flow = new Flow(n);
        for (int i = 0; i < m; i++) {
            int from = sc.nextInt() - 1;
            int to = sc.nextInt() - 1;
            int capacity = sc.nextInt();
            int cost = sc.nextInt();
            flow.addEdge(from, to, capacity, cost);
        }

        long[] result = flow.minCostMaxFlow(0, n - 1);
        System.out.println(result[1]);  
    }

    static class Edge {
        int to, rev, cap, cost;
        int flow;

        Edge(int to, int rev, int cap, int cost) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.cost = cost;
            this.flow = 0;
        }

        int res() {
            return cap - flow;
        }
    }

    static class Flow {
        int n;
        List<Edge>[] graph;
        long[] potential;

        @SuppressWarnings("unchecked")
        Flow(int n) {
            this.n = n;
            graph = new List[n];
            for (int i = 0; i < n; i++) {
                graph[i] = new ArrayList<>();
            }
            potential = new long[n];
        }

        void addEdge(int from, int to, int cap, int cost) {
            Edge forward = new Edge(to, graph[to].size(), cap, cost);
            Edge backward = new Edge(from, graph[from].size(), 0, -cost);
            graph[from].add(forward);
            graph[to].add(backward);
        }

        long[] minCostMaxFlow(int s, int t) {
            long flow = 0;
            long cost = 0;
            long[] dist = new long[n];
            Edge[] prevEdge = new Edge[n];
            int[] prevNode = new int[n];

            Arrays.fill(potential, Long.MAX_VALUE);
            potential[s] = 0;
            boolean[] inQueue = new boolean[n];
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(s);
            inQueue[s] = true;

            while (!queue.isEmpty()) {
                int u = queue.poll();
                inQueue[u] = false;
                for (Edge e : graph[u]) {
                    if (e.res() > 0) {
                        int v = e.to;
                        if (potential[v] > potential[u] + e.cost) {
                            potential[v] = potential[u] + e.cost;
                            if (!inQueue[v]) {
                                inQueue[v] = true;
                                queue.add(v);
                            }
                        }
                    }
                }
            }

            while (true) {
                Arrays.fill(dist, Long.MAX_VALUE);
                dist[s] = 0;
                PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
                pq.add(new long[]{0, s});
                Arrays.fill(prevEdge, null);

                while (!pq.isEmpty()) {
                    long[] cur = pq.poll();
                    long d = cur[0];
                    int u = (int) cur[1];
                    if (d > dist[u]) {
                        continue;
                    }

                    for (Edge e : graph[u]) {
                        if (e.res() > 0) {
                            int v = e.to;
                            long ndist = dist[u] + e.cost + potential[u] - potential[v];
                            if (dist[v] > ndist) {
                                dist[v] = ndist;
                                prevEdge[v] = e;
                                prevNode[v] = u;
                                pq.add(new long[]{ndist, v});
                            }
                        }
                    }
                }

                if (prevEdge[t] == null) {
                    break; 
                }

                for (int i = 0; i < n; i++) {
                    if (dist[i] < Long.MAX_VALUE)
                        potential[i] += dist[i];
                }

                int push = Integer.MAX_VALUE;
                for (int v = t; v != s; v = prevNode[v]) {
                    Edge e = prevEdge[v];
                    push = Math.min(push, e.res());
                }

                for (int v = t; v != s; v = prevNode[v]) {
                    Edge e = prevEdge[v];
                    e.flow += push;
                    graph[e.to].get(e.rev).flow -= push;
                    cost += (long) push * e.cost;
                }

                flow += push;
            }

            return new long[]{flow, cost};
        }
    }
}
