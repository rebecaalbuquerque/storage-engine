package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ListUtils {

    public static ArrayList<String> shuffleWithRepetition(ArrayList<String> rows) {
        ArrayList<String> sorted = new ArrayList<>();
        int length = rows.size();
        Random rnd = new Random();

        // Sera criada n listas de tamanho 621
        ArrayList<ArrayList<String>> rowsDivididos = criarListasMenores(rows, 621);
        Collections.shuffle(rowsDivididos);

        ArrayList<String> novoRows = new ArrayList<>();

        for (ArrayList<String> lista : rowsDivididos) {
            Collections.shuffle(lista);
            novoRows.addAll(lista);
        }

        for (int i = 0; i < length; i++) {
            int min = i - 2;
            int max = i + 2;

            if (min < 0)
                min = 0;

            if (max > length - 1)
                max = length - 1;

            int random = rnd.nextInt((max - min) + 1) + min;

            sorted.add(novoRows.get(random));
        }

        return sorted;
    }

    private static <T> ArrayList<ArrayList<T>> criarListasMenores(ArrayList<T> list, int L) {
        ArrayList<ArrayList<T>> parts = new ArrayList<>();
        int N = list.size();

        for (int i = 0; i < N; i += L) {
            parts.add(
                    new ArrayList<>(list.subList(i, Math.min(N, i + L)))
            );
        }

        return parts;
    }
}
