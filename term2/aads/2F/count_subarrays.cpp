#include <iostream>
#include <vector>
#include <algorithm>
#include <unordered_map>

using namespace std;

class FenwickTree {
public:
    FenwickTree(int size) : tree(size + 1, 0) {}

    void update(int index, int delta) {
        while (index < tree.size()) {
            tree[index] += delta;
            index += index & -index;
        }
    }

    int query(int index) {
        int sum = 0;
        while (index > 0) {
            sum += tree[index];
            index -= index & -index;
        }
        return sum;
    }

private:
    vector<int> tree;
};

int main() {
    int n;
    long long t;
    cin >> n >> t;

    vector<long long> a(n);
    for (int i = 0; i < n; i++) {
        cin >> a[i];
    }

    vector<long long> prefixSums(n + 1, 0);
    for (int i = 1; i <= n; i++) {
        prefixSums[i] = prefixSums[i - 1] + a[i - 1];
    }

    vector<long long> allSums = prefixSums;
    sort(allSums.begin(), allSums.end());
    allSums.erase(unique(allSums.begin(), allSums.end()), allSums.end());

    unordered_map<long long, int> compress;
    for (int i = 0; i < allSums.size(); i++) {
        compress[allSums[i]] = i + 1;
    }

    FenwickTree fenwickTree(allSums.size());

    long long count = 0;
    fenwickTree.update(compress[0], 1);

    for (int i = 1; i <= n; i++) {
        long long target = prefixSums[i] - t;
        int pos = upper_bound(allSums.begin(), allSums.end(), target) - allSums.begin();
        count += fenwickTree.query(allSums.size()) - fenwickTree.query(pos);

        fenwickTree.update(compress[prefixSums[i]], 1);
    }

    cout << count << endl;

    return 0;
}
