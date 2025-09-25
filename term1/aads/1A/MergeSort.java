import java.util.*;

public class MergeSort {
	public static void main(String[] args) {
	    Scanner scan = new Scanner(System.in);
	    int n = scan.nextInt();
	    int[] mas = new int[n];
	    for (int i = 0; i < n; i++) {
	        mas[i] = scan.nextInt();
	    }
	    sort(mas, 0, n - 1);
	    for (int numbers : mas) {
	        System.out.print(numbers + " ");
	    }
	}

	public static void sort(int[] mas, int left, int right) {
	    if (left < right) {
	        int mid = (left + right) / 2;
	        sort(mas, left, mid);
	        sort(mas, mid + 1, right);
	        mergesort(mas, left, mid, right);
	    }
	}

	public static void mergesort(int[] mas, int left, int mid, int right) {
	    int n1 = mid - left + 1;
	    int n2 = right - mid;

	    int[] Left = new int[n1];
	    int[] Right = new int[n2];

	    for (int i = 0; i < n1; i++) {
	        Left[i] = mas[left + i];
	    }
	    for (int j = 0; j < n2; j++) {
	        Right[j] = mas[mid + j + 1];
	    }

	    int i1 = 0;
	    int j2 = 0;
	    int ind = left;
	    while (i1 < n1 && j2 < n2) {
	        if (Left[i1] <= Right[j2]) {
	            mas[ind++] = Left[i1++];
	        } else {
	            mas[ind++] = Right[j2++];
	        }
	    }

	    while (i1 < n1) {
	        mas[ind++] = Left[i1++];
	    }

	    while (j2 < n2) {
	        mas[ind++] = Right[j2++];
	    }
	}
}
