package utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static constants.ConstantesRegex.*;
import static utils.ConversorUtils.*;

public class BlocoUtils {

    /**
     * Método para colocar o separador de colunas "|" em um byte
     * */
    public static String[] formatarArrayHeaders(String headers){
        String[] arrayHeaders = headers.split(SEPARADOR_COLUNA);
        String[] result = new String[arrayHeaders.length * 2];

        for (int i = 0; i < result.length; i++) {

            if(i % 2 == 0){

                if(i > 0){
                    result[i] = arrayHeaders[i-1].replaceAll(CARACTER_ESPECIAL, "");
                } else {
                    result[i] = arrayHeaders[i].replaceAll(CARACTER_ESPECIAL, "");
                }

            } else {
                result[i] = "|";
            }

        }

        return result;
    }

    public static byte[] getTuplaFormatada(String linha){
        ArrayList<byte[]> listaColunas = new ArrayList<>();

        for (String dado : linha.split(SEPARADOR_COLUNA)) {

            if (Pattern.matches(APENAS_NUMERO, dado))
                listaColunas.add(getColunaIntFormatada(dado));
            else
                listaColunas.add(getColunaStringFormatada(dado));

        }

        byte[] tamanho = intToArrayByte(concatenarArrays(listaColunas).length, 3);

        listaColunas.add(0, tamanho);

        return concatenarArrays(listaColunas);
    }

    private static byte[] getColunaIntFormatada(String dado) {
        byte[] tamanhoColuna = intToArrayByte(4, 2);
        byte[] dadosColuna = intToArrayByte(Integer.parseInt(dado), 4);

        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add(tamanhoColuna);
                    add(dadosColuna);
                }}
        );
    }

    /**
     * Transforma os dados em array de byte e retorna outro array de bytes que é a concatenação do tamanho dos dados em array e os dados em array, ou seja,
     * array[ array[tamanhoDados] + array[dados] ]
     * */
    private static byte[] getColunaStringFormatada(String dado) {
        byte[] dadosColuna = stringToBytes(dado);
        byte[] tamanhoColuna = intToArrayByte(dadosColuna.length, 2);

        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add(tamanhoColuna);
                    add(dadosColuna);
                }}
        );

    }

    public static boolean temEspacoParaNovaTupla(int tamanhoDisponivel, String novaTupla){
        return getTuplaFormatada(novaTupla).length < tamanhoDisponivel;
    }

    public static int[] getIndexesTuplas(byte[] tupleDirectory){
        ArrayList<Integer> indexes = new ArrayList<>();
        byte[] directory = new byte[2];
        int count = 0;

        for (byte b : tupleDirectory) {
            directory[count] = b;
            count++;

            if (count == 2) {
                indexes.add(getShortFromBytes(directory) - 9);
                directory = new byte[2];
                count = 0;
            }

        }

        return indexes.stream().mapToInt(i -> i).toArray();

    }

}
