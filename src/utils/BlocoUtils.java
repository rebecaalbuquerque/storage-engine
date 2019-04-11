package utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static constants.ConstantesRegex.*;
import static sgbd.bloco.BlocoDado.QTD_HEADERS;
import static utils.ConversorUtils.*;

public class BlocoUtils {

    /**
     * Método para colocar o separador de colunas "|" em um byte
     * */
    public static String[] formatarArrayHeaders(String headers){
        String arrayHeaders = headers.replaceAll(SEPARADOR_COLUNA, " | ");
        String[] result = arrayHeaders.split(" ");

        for (int i = 0; i < result.length; i++) {

            if(result[i].length() > 1){
                result[i] = result[i].replaceAll(CARACTER_ESPECIAL, "");
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

        byte[] tamanho = intToArrayByte(concatenarArrays(listaColunas).length, 4);

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
        return (getTuplaFormatada(novaTupla).length + 2) < tamanhoDisponivel;
    }

    /**
     * Retorna o index de início de todas as tuplas que um bloco possui
     * @param tupleDirectory estrutura de dados que possui informação dos indexes
     * */
    public static int[] getIndexesTuplas(byte[] tupleDirectory){
        ArrayList<Integer> indexes = new ArrayList<>();
        byte[] directory = new byte[2];
        int count = 0;

        for (byte b : tupleDirectory) {
            directory[count] = b;
            count++;

            if (count == 2) {
                indexes.add(getShortFromBytes(directory) - QTD_HEADERS);
                directory = new byte[2];
                count = 0;
            }

        }

        return indexes.stream().mapToInt(i -> i).toArray();

    }

    /**
     * Retorna o tipo e a quantidade máximo de uma coluna especifica
     * */
    public static String[] getInformacaoColuna(String coluna){
        String nomeColuna = coluna.replaceAll(APENAS_NUMERO, "");
        String quantidadeColuna = coluna.replaceAll(APENAS_LETRAS + "|(_)", "");

        return new String[]{nomeColuna.substring(nomeColuna.length() - 1),  quantidadeColuna};
    }

    /**
     * Retorna os dados de uma coluna a partir do index da mesma
     * */
    public static byte[] getDadosByIndexColuna(byte[] tupla, int index){
        int countColunas = 0;
        int indexProximaColuna = 4;
        int countTamanhoColunaAtual = 0;
        int countResult = 0;
        boolean foundColumn = false;
        byte[] tamanhoColunaAtual = new byte[2];
        byte[] result = new byte[0];

        for (int i = 4; i < tupla.length; i++) {

            // TODO: esse if tá errado
            if(indexProximaColuna == i){
                tamanhoColunaAtual[countTamanhoColunaAtual] = tupla[i];
                countTamanhoColunaAtual++;
            }

            if(countTamanhoColunaAtual == 2){

                indexProximaColuna = getShortFromBytes(tamanhoColunaAtual) + i + 1;

                if(index == countColunas){
                    result = new byte[getShortFromBytes(tamanhoColunaAtual)];
                    foundColumn = true;
                }

                countTamanhoColunaAtual = 0;
                countColunas++;

            }

            if(foundColumn){

                result[countResult] = tupla[i];

                if(indexProximaColuna == i){
                    break;
                }

            }

        }

        return result;
    }

}
