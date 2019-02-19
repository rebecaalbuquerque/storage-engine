package utils;

import static constants.ConstantesRegex.CARACTER_ESPECIAL;

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

}
