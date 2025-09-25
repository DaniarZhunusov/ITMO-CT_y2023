import java.util.*;
import java.io.*;

public class Skobki {
    static class Tree {
        static class Node {
            int open;   // '('
            int close;  // ')'
            int pairs;  // '()'
        }

        private Node[] tree;
        private int n;

        public Tree(String s) {
            n = s.length();
            tree = new Node[4 * n];
            for (int i = 0; i < 4 * n; i++) {
                tree[i] = new Node();
            }
            build(s, 0, 0, n - 1);
        }

        private void build(String s, int idx, int start, int end) {
            if (start == end) {
                tree[idx].open = s.charAt(start) == '(' ? 1 : 0;
                tree[idx].close = s.charAt(start) == ')' ? 1 : 0;
            } else {
                int mid = start + (end - start) / 2;
                int left = 2 * idx + 1;
                int right = 2 * idx + 2;
                build(s, left, start, mid);
                build(s, right, mid + 1, end);
                merge(idx);
            }
        }

        private void merge(int idx) {
            int left = 2 * idx + 1;
            int right = 2 * idx + 2;
            int match = Math.min(tree[left].open, tree[right].close);
            tree[idx].pairs = tree[left].pairs + tree[right].pairs + match;
            tree[idx].open = tree[left].open + tree[right].open - match;
            tree[idx].close = tree[left].close + tree[right].close - match;
        }

        public Node query(int l, int r) {
            return query(0, 0, n - 1, l, r);
        }

        private Node query(int idx, int start, int end, int l, int r) {
            if (start > r || end < l) {
                return new Node();
            }
            if (start >= l && end <= r) {
                return tree[idx];
            }
            int mid = start + (end - start) / 2;
            Node leftNode = query(2 * idx + 1, start, mid, l, r);
            Node rightNode = query(2 * idx + 2, mid + 1, end, l, r);
            Node res = new Node();
            int match = Math.min(leftNode.open, rightNode.close);
            res.pairs = leftNode.pairs + rightNode.pairs + match;
            res.open = leftNode.open + rightNode.open - match;
            res.close = leftNode.close + rightNode.close - match;
            return res;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(new BufferedOutputStream(System.out));

        String s = in.readLine();
        int n = s.length();
        Tree segTree = new Tree(s);

        int m = Integer.parseInt(in.readLine());
        for (int i = 0; i < m; i++) {
            String[] parts = in.readLine().split(" ");
            int l = Integer.parseInt(parts[0]) - 1;
            int r = Integer.parseInt(parts[1]) - 1;
            Tree.Node res = segTree.query(l, r);
            out.println(2 * res.pairs);
        }
        out.flush();
    }
}