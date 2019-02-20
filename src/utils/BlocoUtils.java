package utils;

import static constants.ConstantesRegex.CARACTER_ESPECIAL;
import static utils.ConversorUtils.intToArrayByte;

public class BlocoUtils {

    /**
     * Método para colocar o separador de colunas "|" em um byte
     * */
    public static String[] formatarArrayHeaders(String[] arrayHeaders){
        String[] result = new String[arrayHeaders.length + (arrayHeaders.length - 1)];

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

    /**
     * Esse método adiciona a nova tupla no final do array e adiciona as informações do tuple directory no inicio do array
     * @param tamanhoTuplasDisponivel serve como index auxiliar ao adicionar nova tupla no array de tuplas
     * @param tamanhoTuplaDirectory serve como index auxiliar ao adicionar as informações do tuple directory no array
     * @param novaTupla tupla a ser adicionada
     * @param tuplas array onde será colocada as informações do tuple directory e a nova tupla
     * */
    public static byte[] inserirNovaTupla(int tamanhoTuplasDisponivel, int tamanhoTuplaDirectory, byte[] novaTupla, byte[] tuplas){
        int indexTuplas = 0;
        int indexTuplaDirectory = 0;

        byte[] tuplaDirectory = intToArrayByte(tamanhoTuplasDisponivel, 2);

        // Inserindo na parte do Tupla Directory o index de inicio da nova tupla
        for (int i = tamanhoTuplaDirectory - 2; i < tamanhoTuplaDirectory; i++) {
            tuplas[i] = tuplaDirectory[indexTuplaDirectory];
            indexTuplaDirectory++;
        }

        // Inserindo os dados na nova tupla na parte das tuplas
        for (int i = tamanhoTuplasDisponivel; i < (tamanhoTuplasDisponivel + novaTupla.length); i++) {

            tuplas[i] = novaTupla[indexTuplas];
            indexTuplas++;

        }

        return tuplas;
    }

}
