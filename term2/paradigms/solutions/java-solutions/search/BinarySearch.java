package search;

public class BinarySearch {
    // Hoare triple:
    //{P} -> Pred: array a is sorted by non-increasing
    // i < j -> a[i] >= a[j]
    //{Q} -> Post: the index i is returned such that a[i-1] >= x >= a[i]
	public static void main(String[] args) {
		int x = Integer.parseInt(args[0]);
		int[] a = new int[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			a[i - 1] = Integer.parseInt(args[i]);
		}
		System.out.println(BinarySearchIterative(a, x, -1, a.length));
		//System.out.println(BinarySearchRecursive(a, x, -1, a.length));
	}

    // Hoare triple:
    //{P} -> Pred: array a is sorted by non-increasing
    // i < j -> a[i] >= a[j]
    //{Q} -> Post: the index i is returned such that a[i-1] >= x >= a[i]
	public static int BinarySearchIterative(int[] a, int x, int left, int right) {
		// l = -1 && r = a.length
		while (right - left > 1) {
			// r - l > 1
			int mid = (left + right) / 2;
			// m = (l + r) / 2 && r - l > 1
			if (a[mid] <= x) {
				// a[m] <= x 
				right = mid;
				// a[m] <= x && r = m
			} else {
				left = mid;
				// a[m] > x && l = m
			}
			// Inv the subarray a[left:right] always remains sorted in non-increasing order
		}
		return right;
	}

    // Hoare triple:
    //{P} -> Pred: array a is sorted by non-increasing
    // i < j -> a[i] >= a[j]
    //{Q} -> Post: the index i is returned such that a[i-1] >= x >= a[i]
	public static int BinarySearchRecursive(int[] a, int x, int left, int right) {
		// l = -1 && r = a.length
		if (right - left <= 1) {
			// r - l <= 1
			return right;
		}
		int mid = (left + right) / 2;
		// m = (l + r) / 2
		if (a[mid] <= x) {
			// Inv && a[m] <= x
			return BinarySearchRecursive(a, x, left, mid);
		} else {
			// Inv && a[m] > x
			return BinarySearchRecursive(a, x, mid, right);
		}
		// Inv the subarray a[left:right] always remains sorted in non-increasing order
	}
}