'use strict';
 
function main() {
    const readline = require('readline');
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });
 
    let n;
    let mas = [];
 
    rl.question('', (num) => {
        n = parseInt(num);
        rl.question('', (elements) => {
            mas = elements.split(' ').map(Number);
            const inversion = countInversions(n, mas);
            console.log('', inversion);
            rl.close();
        });
    });
}
 
function countInversions(n, arr) {
    return mergeSortAndCount(arr, 0, n - 1);
}
 
function mergeSortAndCount(arr, left, right) {
    if (left >= right) {
        return 0;
    }
 
    let mid = Math.floor((left + right) / 2);
    let inversionCount = mergeSortAndCount(arr, left, mid) + mergeSortAndCount(arr, mid + 1, right) + mergeAndCount(arr, left, mid, right);
    return inversionCount;
}
 
function mergeAndCount(arr, left, mid, right) {
    let leftArr = arr.slice(left, mid + 1);
    let rightArr = arr.slice(mid + 1, right + 1);
 
    let i = 0, j = 0, k = left, invCount = 0;
 
    while (i < leftArr.length && j < rightArr.length) {
        if (leftArr[i] <= rightArr[j]) {
            arr[k++] = leftArr[i++];
        } else {
            arr[k++] = rightArr[j++];
            invCount += (mid - (left + i) + 1);
        }
    }
 
    while (i < leftArr.length) {
        arr[k++] = leftArr[i++];
    }
 
    while (j < rightArr.length) {
        arr[k++] = rightArr[j++];
    }
 
    return invCount;
}
 
main();