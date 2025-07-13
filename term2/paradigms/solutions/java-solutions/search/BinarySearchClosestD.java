package search;

public class BinarySearchClosestD {
    // Hoare triple:
    // :NOTE: мат языком
    // :NOTE: a[0] ?
    //{P} -> Pred: forall array a is sorted by non-increasing, i < j -> a[i] >= a[j], x is a valid Integer
    //{Q} -> Post: Returns the value of the element in the array that is closest to x
    public static void main(String[] args) {
        // :NOTE: contract?
        int x = Integer.parseInt(args[0]);
        int[] a = new int[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            a[i - 1] = Integer.parseInt(args[i]);
        }
        if (sum(a) % 2 == 0) {
            //array a is sorted by non-increasing && sum(a) % 2 == 0
            int index = binarySearchClosestRecursive(a, x, 0, a.length - 1);
            System.out.println(a[index]);
        } else {
            //array a is sorted by non-increasing && sum(a) % 2 != 0
            int index = binarySearchClosestIterative(a, x);
            System.out.println(a[index]);
        }
    }


    // Hoare triple:
    // :NOTE: А где left и right
    // {P} -> Pre-condition: array is sorted, x is valid, and left <= right
    // {Q} -> Post-condition: Returns the value of the element in the array that is closest to x
    public static int binarySearchClosestIterative(int[] a, int x) {
        int left = 0;
        int right = a.length - 1;
        // l = 0 && r = a.length - 1
        while (left <= right) {
            // Inv && l <= r
            int mid = left + (right - left) / 2;
            // m = l + (r - l) / 2
            if (a[mid] == x) {
                //a[m] = x
                return mid;
            } else if (a[mid] >= x) {
                left = mid + 1;
                //Inv && a[m] >= x && l = m + 1
            } else {
                right = mid - 1;
                //Inv && a[m] < x && r = m - 1
            }
        }

        if (right < 0) {
            //r < 0
            return left;
        } else if (left >= a.length) {
            //l >= a
            return right;
        } else {
            if (x - a[right] >= a[left] - x) {
                //Inv && x - a[r] >= a[l] - x
                return right;
            } else {
                //Inv && x - a[r] < a[l] - x
                return left;
            }
        }
    }

    // Hoare triple:
    // :NOTE: Условие не соответствует месту вызова
    // {P} -> pre-condition: forall array is sorted, x is valid, and left <= right && l = 0 && r = a.length - 1
    // {Q} -> post-condition: Returns the value of the element in the array that is closest to x
    public static int binarySearchClosestRecursive(int[] a, int x, int left, int right) {
        // :NOTE: left > right ??
        if (left > right) {
            // Inv && l > r
            if (right < 0) {
                //Inv && r < 0
                return left;
            } else if (left >= a.length) {
                // l >= a
                return right;
            } else {
                if (x - a[right] >= a[left] - x) {
                    //Inv && x - a[r] < a[l] - x
                    return right;
                } else {
                    return left;
                }
            }
        }


        // :NOTE: potential overflow
        int mid = (left + right) / 2;
        //m = (l + r) / 2

        if (a[mid] == x) {
            //Inv && a[m] = x
            return mid;
        } else if (a[mid] >= x) {
            //Inv && a[m] >= x
            return binarySearchClosestRecursive(a, x, mid + 1, right);
        } else {
            //Inv && a[m] < x
            return binarySearchClosestRecursive(a, x, left, mid - 1);
        }
    }

    // Hoare triple:
    // {P} -> pre-condition: args.length >= 2
    // :NOTE: mat yazik
    // {Q} -> post-condition: Returns the sum of all elements in the array
    public static int sum(int[] a) {
        int s = 0;
        //s = 0
        for (int num : a) {
            s += num;
            //s = s + num
        }
        return s;
    }
}























