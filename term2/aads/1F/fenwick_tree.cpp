#include <iostream>
#include <vector>

using namespace std;

class FenwickTree {
public:
    FenwickTree(int n) : n(n), tree(n + 1, 0) {}

    void update(int idx, int delta) {
        while (idx <= n) {
            tree[idx] += delta;
            idx += idx & -idx;
        }
    }

    int query(int idx) const {
        int sum = 0;
        while (idx > 0) {
            sum += tree[idx];
            idx -= idx & -idx;
        }
        return sum;
    }

private:
    int n;
    vector<int> tree;
};

int main() {
    int n;
    cin >> n;

    vector<int> a(n + 1);
    FenwickTree fenwickOdd(n), fenwickEven(n);

    for (int i = 1; i <= n; ++i) {
        cin >> a[i];
        if (i % 2 == 1) {
            fenwickOdd.update(i, a[i]);
        } else {
            fenwickEven.update(i, a[i]);
        }
    }

    int m;
    cin >> m;

    while (m--) {
        int type;
        cin >> type;
        if (type == 0) {
            int i, j;
            cin >> i >> j;
            if (i % 2 == 1) {
                fenwickOdd.update(i, j - a[i]);
            } else {
                fenwickEven.update(i, j - a[i]);
            }
            a[i] = j;
        } else {
            int l, r;
            cin >> l >> r;
            int sumOdd = fenwickOdd.query(r) - fenwickOdd.query(l - 1);
            int sumEven = fenwickEven.query(r) - fenwickEven.query(l - 1);
            if (l % 2 == 1) {
                cout << sumOdd - sumEven << endl;
            } else {
                cout << sumEven - sumOdd << endl;
            }
        }
    }

    return 0;
}
