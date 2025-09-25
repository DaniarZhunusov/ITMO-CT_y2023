import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MaxSegment {
    private int[] arr;
    private int n;
    private Node[] segmentTree;

    private class Node {
        int maxVal;
        int count;

        Node(int maxVal, int count) {
            this.maxVal = maxVal;
            this.count = count;
        }
    }

    public MaxSegment(int[] arr) {
        this.arr = arr;
        this.n = arr.length;
        this.segmentTree = new Node[4 * n];
        buildSegmentTree(0, 0, n - 1);
    }

    private void buildSegmentTree(int treeIndex, int l, int r) {
        if (l == r) {
            segmentTree[treeIndex] = new Node(arr[l], 1);
        } else {
            int mid = l + (r - l) / 2;
            int leftChild = 2 * treeIndex + 1;
            int rightChild = 2 * treeIndex + 2;

            buildSegmentTree(leftChild, l, mid);
            buildSegmentTree(rightChild, mid + 1, r);

            segmentTree[treeIndex] = merge(segmentTree[leftChild], segmentTree[rightChild]);
        }
    }

    private Node merge(Node left, Node right) {
        if (left.maxVal > right.maxVal) {
            return new Node(left.maxVal, left.count);
        } else if (left.maxVal < right.maxVal) {
            return new Node(right.maxVal, right.count);
        } else {
            return new Node(left.maxVal, left.count + right.count);
        }
    }

    public int[] query(int left, int right) {
        Node result = querySegmentTree(0, 0, n - 1, left - 1, right - 1);
        return new int[]{result.maxVal, result.count};
    }

    private Node querySegmentTree(int treeIndex, int l, int r, int queryL, int queryR) {
        if (queryL > r || queryR < l) {
            return new Node(Integer.MIN_VALUE, 0);
        }

        if (queryL <= l && queryR >= r) {
            return segmentTree[treeIndex];
        }

        int mid = l + (r - l) / 2;
        Node leftResult = querySegmentTree(2 * treeIndex + 1, l, mid, queryL, queryR);
        Node rightResult = querySegmentTree(2 * treeIndex + 2, mid + 1, r, queryL, queryR);

        return merge(leftResult, rightResult);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int[] arr = new int[n];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        int k = Integer.parseInt(br.readLine());
        int[][] queries = new int[k][2];
        for (int i = 0; i < k; i++) {
            st = new StringTokenizer(br.readLine());
            queries[i][0] = Integer.parseInt(st.nextToken());
            queries[i][1] = Integer.parseInt(st.nextToken());
        }
        MaxSegment maxSegment = new MaxSegment(arr);
        for (int[] query : queries) {
            int[] result = maxSegment.query(query[0], query[1]);
            System.out.println(result[0] + " " + result[1]);
        }
    }
}