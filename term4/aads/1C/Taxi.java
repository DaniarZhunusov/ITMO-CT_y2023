import java.util.*;

public class Taxi {
	public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = Integer.parseInt(sc.nextLine());
        TaxiRide[] rides = new TaxiRide[m];
        for (int i = 0; i < m; i++) {
            rides[i] = new TaxiRide(sc.nextLine().split(" "));
        }

        graph = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            graph[i] = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                if (i != j && rides[i].Go(rides[j])) {
                    graph[i].add(j);
                }
            }
        }

        matchTo = new int[m];
        Arrays.fill(matchTo, -1);
        int matching = 0;
        for (int i = 0; i < m; i++) {
            visited = new boolean[m];
            if (dfs(i)) {
                matching++;
            }
        }

        System.out.println(m - matching);
    }

    static class TaxiRide {
        int startTime, endTime;
        int startX, startY, endX, endY;

        public TaxiRide(String[] data) {
            this.startTime = parseTime(data[0]);
            this.startX = Integer.parseInt(data[1]);
            this.startY = Integer.parseInt(data[2]);
            this.endX = Integer.parseInt(data[3]);
            this.endY = Integer.parseInt(data[4]);
            this.endTime = startTime + Math.abs(startX - endX) + Math.abs(startY - endY);
        }

        private int parseTime(String time) {
            String[] parts = time.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        }

        public boolean Go(TaxiRide next) {
            int travel = Math.abs(endX - next.startX) + Math.abs(endY - next.startY);
            return this.endTime + travel + 1 <= next.startTime;
        }
    }

    static List<Integer>[] graph;
    static int[] matchTo;
    static boolean[] visited;

    static boolean dfs(int u) {
        for (int v : graph[u]) {
            if (visited[v]) {
            	continue;
            }
            
            visited[v] = true;
            if (matchTo[v] == -1 || dfs(matchTo[v])) {
                matchTo[v] = u;
                return true;
            }
        }
        return false;
    }
}
