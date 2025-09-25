#include <iostream>
#include <vector>

using namespace std;

int partition(vector<int>& mas, int left, int right) {
    int mid = mas[(left + right) / 2];
    int i = left;
    int j = right;

    while (i <= j) {
        while (mas[i] < mid) {
            i++;
        }
        while (mas[j] > mid) {
            j--;
        }
        if (i >= j) {
            break;
        }
        swap(mas[i++], mas[j--]);
    }
    return j;
}

void quicksort(vector<int>& mas, int left, int right) {
    if (left < right) {
        int q = partition(mas, left, right);
        quicksort(mas, left, q);
        quicksort(mas, q + 1, right);
    }
}

int main() {
    int n;
    cin >> n;

    vector<int> mas(n);
    for (int i = 0; i < n; i++) {
        cin >> mas[i];
    }

    quicksort(mas, 0, n - 1);

    for (int i = 0; i < n; i++) {
        cout << mas[i] << " ";
    }
    cout << endl;

    return 0;
}
