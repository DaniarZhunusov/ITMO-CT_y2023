import java.util.*;

public class HamiltonianCycles {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();

        int[][] matrix = new int[n][n];
        for (int i = 1; i < n; i++) {
            String line = scanner.nextLine().trim();

            while (line.isEmpty()) {
                line = scanner.nextLine().trim();
            }

            for (int j = 0; j < i; j++) {
                matrix[i][j] = line.charAt(j) - '0';
                matrix[j][i] = matrix[i][j];
            }
        }

        List<Integer> hamiltonianCycle = findHamiltonianCycle(n, matrix);

        for (int vertex : hamiltonianCycle) {
            System.out.print((vertex + 1) + " ");
        }
        scanner.close();
    }

    public static List<Integer> findHamiltonianCycle(int n, int[][] matrix) {
        List<Integer> cycle = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            cycle.add(i);
        }

        for (int k = 0; k < n * (n - 1); k++) {
            int v1 = cycle.get(0);
            int v2 = cycle.get(1);

            if (matrix[v1][v2] == 0) {
                int i = 2;

                while (i < n - 1 && (matrix[v1][cycle.get(i)] == 0 || matrix[v2][cycle.get(i + 1)] == 0)) {
                    i++;
                }

                if (i >= n - 1) {
                    i = 2;
                    while (i < n && matrix[v1][cycle.get(i)] == 0) {
                        i++;
                    }
                }

                reverseSublist(cycle, 1, i);
            }

            cycle.add(cycle.remove(0));
        }

        return cycle;
    }

    private static void reverseSublist(List<Integer> list, int start, int end) {
        while (start < end) {
            int temp = list.get(start);
            list.set(start, list.get(end));
            list.set(end, temp);
            start++;
            end--;
        }
    }
}
