package bfst17.KDTrees;

import sun.reflect.generics.tree.Tree;

import java.util.Random;

public class QuickSelect {

    private static int partition(TreeNode[] arr, int left, int right, int pivot) {
        TreeNode pivotVal = arr[pivot];
        swap(arr, pivot, right);
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (arr[i].compareTo(pivotVal) < 0) {
                swap(arr, i, storeIndex);
                storeIndex++;
            }
        }
        swap(arr, right, storeIndex);
        return storeIndex;
    }

    public static TreeNode select(TreeNode[] arr, int n, int lo, int hi) {
        int left = lo;
        int right = hi;
        Random rand = new Random();
        while (right >= left) {
            int pivotIndex = partition(arr, left, right, rand.nextInt(right - left + 1) + left);
            if (pivotIndex == n) {
                return arr[pivotIndex];
            } else if (pivotIndex < n) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return null;
    }

    private static void swap(TreeNode[] arr, int i1, int i2) {
        if (i1 != i2) {
            TreeNode temp = arr[i1];
            arr[i1] = arr[i2];
            arr[i2] = temp;
        }
    }

}