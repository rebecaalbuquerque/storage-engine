package utils;

import sgbd.RowID;

public class SwapUtils {

    /**
     * Método para trocar posição no array
     * */
    public static void swap(RowID[] a, int i, int j) {
        RowID t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

}
