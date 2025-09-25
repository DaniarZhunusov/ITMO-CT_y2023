import java.io.*;
import java.util.*;

public class Domino {
    private static char[][] field;
    private static int n, m, a, b;
    private static ArrayList<Integer>[] list;
    private static int[] pair;
    private static boolean[] used;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        m = scanner.nextInt();
        a = scanner.nextInt();
        b = scanner.nextInt();
        scanner.nextLine();

        field = new char[n][m];
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            for (int j = 0; j < m; j++) {
                field[i][j] = line.charAt(j);
            }
        }

        System.out.println(domino(n, m, a, b));
    }

    public static int domino(int n, int m, int a, int b) {
        int empty = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (field[i][j] == '*') {
                    empty++;
                }
            }
        }

        if (2 * b <= a) {
            return empty * b;
        }

        int size = n * m;
        list = new ArrayList[size];
        for (int i = 0; i < size; i++) {
            list[i] = new ArrayList<>();
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (field[i][j] == '*' && (i + j) % 2 == 0) {
                    int node = i * m + j;
                    if (i > 0 && field[i - 1][j] == '*') {
                        list[node].add((i - 1) * m + j);
                    }
                    if (i < n - 1 && field[i + 1][j] == '*') {
                        list[node].add((i + 1) * m + j);
                    }
                    if (j > 0 && field[i][j - 1] == '*') {
                        list[node].add(i * m + (j - 1));
                    }
                    if (j < m - 1 && field[i][j + 1] == '*') {
                        list[node].add(i * m + (j + 1));
                    }
                }
            }
        }

        pair = new int[size];
        Arrays.fill(pair, -1);
        int matching = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (field[i][j] == '*' && (i + j) % 2 == 0) {
                    used = new boolean[size];
                    if (dfs(i * m + j)) {
                        matching++;
                    }
                }
            }
        }

        int remaining = empty - 2 * matching;
        return matching * a + remaining * b;
    }

    private static boolean dfs(int v) {
        if (used[v]) {
        	return false;
        }

        used[v] = true;
        for (int u : list[v]) {
            if (pair[u] == -1 || dfs(pair[u])) {
                pair[u] = v;
                return true;
            }
        }
        return false;
    }
}