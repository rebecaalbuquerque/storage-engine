package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static enums.TipoArquivo.ENTRADA_ARQUIVOS;
import static utils.FileUtils.getDadosArquivo;

@SuppressWarnings("ALL")
public class Teste {

    public static void main(String[] args) {
        HashMap<Integer, LinkedList<String>> hash1 = new HashMap<>();
        HashMap<Integer, LinkedList<String>> hash2 = new HashMap<>();
        ArrayList<String> result = new ArrayList<>();

        int indexColunaTabela1 = 5;
        int indexColunaTabela2 = 6;
        int N = 366;

        ArrayList<String> linhas1 = getDadosArquivo(new File(ENTRADA_ARQUIVOS.path + "\\forn-tpch.txt"));
        ArrayList<String> linhas2 = getDadosArquivo(new File(ENTRADA_ARQUIVOS.path + "\\cli-tpch.txt"));

        for (int i = 1; i < linhas1.size(); i++) {

            String linha = linhas1.get(i);
            String[] colunas = linha.split("\\|");

            int idBucket = hashString(colunas[indexColunaTabela1], N);

            if (!hash1.containsKey(idBucket)) {

                LinkedList<String> list = new LinkedList<>();
                list.addFirst(linha);
                hash1.put(idBucket, list);

            } else {
                // pega a lista daquele idBucket
                LinkedList<String> list = hash1.get(idBucket);
                list.addLast(linha);

            }

        }

        for (int i = 1; i < linhas2.size(); i++) {

            String linha = linhas2.get(i);
            String[] colunas = linha.split("\\|");

            if(Integer.parseInt(colunas[0]) == 123918){
                System.out.println();
            }

            int idBucket = hashString(colunas[indexColunaTabela2], N);

            if (!hash2.containsKey(idBucket)) {

                LinkedList<String> list = new LinkedList<>();
                list.addFirst(linha);
                hash2.put(idBucket, list);

            } else {
                // pega a lista daquele idBucket
                LinkedList<String> list = hash2.get(idBucket);
                list.addLast(linha);

            }

        }

        System.out.println();

        int bucket1 = 0;
        int bucket2 = 0;

        for (LinkedList<String> lista1 : hash1.values()) {
            System.out.println(">>>>> Bucket " + bucket1);

            for (LinkedList<String> lista2 : hash2.values()) {
                System.out.println(">> Bucket " + bucket1);

                for (String tupla1 : lista1) {

                    String probe1 = tupla1.split("\\|")[indexColunaTabela1];

                    for (String tupla2 : lista2) {
                        String probe2 = tupla2.split("\\|")[indexColunaTabela2];

                        if(probe2.equals(probe1))
                            result.add(probe1);

                    }

                }

                bucket2++;

            }


            bucket1++;
        }

        System.out.println();

    }

    private static int hashInt(int k, int n) {
        return k % n;
    }

    private static int hashString(String s, int n) {
        int hash = 0;

        for (int i = 0; i < s.length(); i++)
            hash = (31 * hash + s.charAt(i)) % n;

        return hash;
    }

}
