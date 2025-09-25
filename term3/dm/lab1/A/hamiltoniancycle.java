import java.io.*;
import java.util.*;

public class hamiltoniancycle {

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
                int value = line.charAt(j) - '0';
                matrix[i][j] = value;
                matrix[j][i] = value;
            }
        }

        List<Integer> hamiltonianCycle = findHamiltonianCycle(n, matrix);

        for (int vertex : hamiltonianCycle) {
            System.out.print((vertex + 1) + " ");
        }
        scanner.close();
    }

    public static List<Integer> findHamiltonianCycle(int n, int[][] matrix) {
        Deque<Integer> queue = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            queue.addLast(i);
        }

        for (int k = 0; k < n * (n - 1); k++) {
            int v1 = queue.peekFirst();
            int v2 = queue.toArray(new Integer[0])[1];

            if (matrix[v1][v2] == 0) {
                int i = 2;

                for (; i < n; i++) {
                    int next = queue.toArray(new Integer[0])[i];
                    int prev = queue.toArray(new Integer[0])[i - 1];

                    if (matrix[v1][next] == 1 && matrix[v2][prev] == 1) {
                        break;
                    }
                }

                reverseSublist(queue, 1, i);
            }

            queue.addLast(queue.pollFirst());
        }

        return new ArrayList<>(queue);
    }

    private static void reverseSublist(Deque<Integer> deque, int start, int end) {
        Integer[] arr = deque.toArray(new Integer[0]);
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
        
        deque.clear();
        Collections.addAll(deque, arr);
    }
}
