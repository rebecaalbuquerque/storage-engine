package utils;

import sgbd.PageID;

public class SwapUtils {

    /**
     * Método para trocar posição no array
     * */
    public static void swap(PageID[] a, int i, int j) {
        PageID t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

}
