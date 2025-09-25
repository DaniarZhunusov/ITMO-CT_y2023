#include <iostream>
#include <vector>
#include <cmath>
using namespace std;

const int MAXN = 100000;
const int LOGMAXN = 17; 

vector<int> a;
vector<vector<int>> sparseTable;
vector<int> logTable;

void buildSparseTable(int n) {
    sparseTable.assign(n, vector<int>(LOGMAXN));

    for (int i = 0; i < n; ++i) {
        sparseTable[i][0] = a[i];
    }

    for (int j = 1; (1 << j) <= n; ++j) { 
        for (int i = 0; (i + (1 << j) - 1) < n; ++i) {
            sparseTable[i][j] = min(sparseTable[i][j - 1], sparseTable[i + (1 << (j - 1))][j - 1]);
        }
    }

    logTable.assign(n + 1, 0);
    for (int i = 2; i <= n; ++i) {
        logTable[i] = logTable[i / 2] + 1;
    }
}

int querySparseTable(int l, int r) {
    int j = logTable[r - l + 1];
    return min(sparseTable[l][j], sparseTable[r - (1 << j) + 1][j]);
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);

    int n, m;
    long long a1;
    cin >> n >> m >> a1;

    a.resize(n);
    a[0] = a1;
    for (int i = 1; i < n; ++i) {
        a[i] = (23LL * a[i - 1] + 21563) % 16714589;
    }

    buildSparseTable(n);

    int u1, v1;
    cin >> u1 >> v1;
    int u = u1, v = v1;
    int result = 0;

    for (int i = 1; i <= m; ++i) {
        int l = min(u, v) - 1;
        int r = max(u, v) - 1;
        result = querySparseTable(l, r);
        if (i == m) {
            cout << u << " " << v << " " << result << "\n";
        }
        u = ((17LL * u + 751 + result + 2 * i) % n) + 1;
        v = ((13LL * v + 593 + result + 5 * i) % n) + 1;
    }

    return 0;
}
