package utils;

public class ConversorUtils {

    /**
     * Converte um numero em um array de bytes. A partir da quantidade de bytes se sabe quantas vezes
     * será necessário fazer o "Shifting" no numero.
     *
     * @param numero numero a ser convertido em array de bytes
     * @param quantidadeBytes tamanho do array de bytes
     * */
    public static byte[] intToArrayByte(int numero, int quantidadeBytes){

        byte[] result = new byte[quantidadeBytes];
        int quantidadeShifting = quantidadeBytes - 1;

        for (int i = 0; i < quantidadeBytes; i++) {

            int bits = 8 * quantidadeShifting;

            if(bits > 0){
                result[i] = (byte) ((numero >> bits) & 0xff);
            } else {
                result[i] = (byte) (numero & 0xff);
            }

            quantidadeShifting--;

        }

        return result;

    }

}
