package utils;

import static constants.ConstantesRegex.CARACTER_ESPECIAL;
import static utils.ConversorUtils.intToArrayByte;

public class BlocoUtils {

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

    public static byte[] inserirNovaTupla(int tamanhoTuplasDisponivel, int tamanhTuplaDirectory, byte[] novaTupla, byte[] tuplas){
        int indexTuplas = 0;
        int indexTuplaDirectory = 0;

        byte[] tuplaDirectory = intToArrayByte(tamanhoTuplasDisponivel, 2);

        // Inserindo na parte do Tupla Directory o index de inicio da nova tupla
        for (int i = tamanhTuplaDirectory - 2; i < tamanhTuplaDirectory; i++) {
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
