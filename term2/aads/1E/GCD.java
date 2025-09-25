import java.util.*;
import java.io.*;

public class GCD {
    static class SegmentTree {
        private int[] tree;
        private int n;

        public SegmentTree(int[] array) {
            n = array.length;
            tree = new int[4 * n];
            build(array, 0, 0, n - 1);
        }

        private void build(int[] array, int node, int start, int end) {
            if (start == end) {
                tree[node] = array[start];
            } else {
                int mid = start + (end - start) / 2;
                int left = 2 * node + 1;
                int right = 2 * node + 2;
                build(array, left, start, mid);
                build(array, right, mid + 1, end);
                tree[node] = gcd(tree[left], tree[right]);
            }
        }

        private int gcd(int a, int b) {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }

        public void update(int ind, int value) {
            update(0, 0, n - 1, ind, value);
        }

        private void update(int node, int start, int end, int ind, int value) {
            if (start == end) {
                tree[node] = value;
            } else {
                int mid = start + (end - start) / 2;
                int left = 2 * node + 1;
                int right = 2 * node + 2;
                if (ind <= mid) {
                    update(left, start, mid, ind, value);
                } else {
                    update(right, mid + 1, end, ind, value);
                }
                tree[node] = gcd(tree[left], tree[right]);
            }
        }

        public int query(int l, int r) {
            return query(0, 0, n - 1, l, r);
        }

        private int query(int node, int start, int end, int l, int r) {
            if (start > r || end < l) {
                return 0;
            }
            if (start >= l && end <= r) {
                return tree[node];
            }
            int mid = start + (end - start) / 2;
            int leftGCD = query(2 * node + 1, start, mid, l, r);
            int rightGCD = query(2 * node + 2, mid + 1, end, l, r);
            return gcd(leftGCD, rightGCD);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new BufferedOutputStream(System.out));

        int n = Integer.parseInt(in.readLine());
        int[] array = Arrays.stream(in.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        SegmentTree segTree = new SegmentTree(array);

        int m = Integer.parseInt(in.readLine());
        List<Integer> results = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            String[] parts = in.readLine().split(" ");
            char symbol = parts[0].charAt(0);

            if (symbol == 's') {
                int l = Integer.parseInt(parts[1]) - 1;
                int r = Integer.parseInt(parts[2]) - 1;
                results.add(segTree.query(l, r));
            } else if (symbol == 'u') {
                int ind = Integer.parseInt(parts[1]) - 1;
                int value = Integer.parseInt(parts[2]);
                segTree.update(ind, value);
            }
        }

        for (int result : results) {
            out.print(result + " ");
        }
        out.flush();
    }
}