import java.util.*;
import java.io.*;

public class MaxSegment {
    static class SegmentTree {
        private int[] tree;
        private int[] delta;
        private int n;

        public SegmentTree(int[] array) {
            n = array.length;
            tree = new int[4 * n];
            delta = new int[4 * n];
            build(array, 0, 0, n - 1);
        }

        private void build(int[] array, int node, int start, int end) {
            if (start == end) {
                tree[node] = array[start];
            } else {
                int mid = start + (end - start) / 2;
                int leftChild = 2 * node + 1;
                int rightChild = 2 * node + 2;
                build(array, leftChild, start, mid);
                build(array, rightChild, mid + 1, end);
                tree[node] = Math.max(tree[leftChild], tree[rightChild]);
            }
        }

        private void propagate(int node, int start, int end) {
            if (delta[node] != 0) {
                tree[node] += delta[node];
                if (start != end) {
                    int leftChild = 2 * node + 1;
                    int rightChild = 2 * node + 2;
                    delta[leftChild] += delta[node];
                    delta[rightChild] += delta[node];
                }
                delta[node] = 0;
            }
        }

        public void updateRange(int l, int r, int value) {
            updateRange(0, 0, n - 1, l, r, value);
        }

        private void updateRange(int node, int start, int end, int l, int r, int value) {
            propagate(node, start, end);
            if (start > r || end < l) {
                return;
            }
            if (start >= l && end <= r) {
                delta[node] += value;
                propagate(node, start, end);
                return;
            }
            int mid = start + (end - start) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            updateRange(leftChild, start, mid, l, r, value);
            updateRange(rightChild, mid + 1, end, l, r, value);
            tree[node] = Math.max(tree[leftChild], tree[rightChild]);
        }

        public int queryRange(int l, int r) {
            return queryRange(0, 0, n - 1, l, r);
        }

        private int queryRange(int node, int start, int end, int l, int r) {
            propagate(node, start, end);
            if (start > r || end < l) {
                return Integer.MIN_VALUE;
            }
            if (start >= l && end <= r) {
                return tree[node];
            }
            int mid = start + (end - start) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            int leftMax = queryRange(leftChild, start, mid, l, r);
            int rightMax = queryRange(rightChild, mid + 1, end, l, r);
            return Math.max(leftMax, rightMax);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(new BufferedOutputStream(System.out));

        int n = Integer.parseInt(reader.readLine());
        int[] array = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

        SegmentTree segTree = new SegmentTree(array);

        int m = Integer.parseInt(reader.readLine());
        List<Integer> results = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            String[] parts = reader.readLine().split(" ");
            char queryType = parts[0].charAt(0);

            if (queryType == 'm') {
                int l = Integer.parseInt(parts[1]) - 1;
                int r = Integer.parseInt(parts[2]) - 1;
                results.add(segTree.queryRange(l, r));
            } else if (queryType == 'a') {
                int l = Integer.parseInt(parts[1]) - 1;
                int r = Integer.parseInt(parts[2]) - 1;
                int addValue = Integer.parseInt(parts[3]);
                segTree.updateRange(l, r, addValue);
            }
        }
        for (int result : results) {
            writer.print(result + " ");
        }
        writer.flush();
    }
}