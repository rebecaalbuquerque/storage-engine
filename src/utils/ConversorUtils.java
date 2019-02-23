package utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConversorUtils {

    /**
     * Converte um numero em um array de bytes. A partir da quantidade de bytes se sabe quantas vezes
     * será necessário fazer o "Shifting" no numero.
     *
     * @param numero numero a ser convertido em array de bytes
     * @param quantidadeBytes tamanho do array de bytes
     *
     * @return o array de bytes resultante
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

    /**
     * Transforma cada item do array de string em array de byte, adiciona os elementos desse array numa lista
     * de Byte e por ultimo transforma a lista de Byte em um array de byte
     *
     * @param array estrutura de dados a ser trasnformada em array de bytes
     *
     * @return o array de bytes resultante
     * */
    public static byte[] stringsToBytes(String[] array){
        List<Byte> listaBytes = new ArrayList<>();
        byte[] result;
        int tamanhoBytes = 0;

        for (String s : array) {
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            tamanhoBytes += bytes.length;

            for (byte b : bytes)
                listaBytes.add(b);
        }

        result = new byte[tamanhoBytes];

        for (int i = 0; i < listaBytes.size(); i++) {
            result[i] = listaBytes.get(i);
        }

        return result;
    }

    /**
     * Transforma uma string em um array de bytes.
     *
     * @param valor variável que irá resultar no array de bytes
     * @return array de bytes
     * */
    public static byte[] stringToBytes(String valor){
        return valor.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Dado uma lista em que os elementos são um array de bytes, esse método concatena todos esses elementos e coloca
     * em um array.
     * @param arrayBytes lista que contem os arrays
     * @return um array com todos os elementos concatenados
     * */
    public static byte[] concatenarArrays(ArrayList<byte[]> arrayBytes){
        int tamanho = 0;
        byte[] result;

        for (byte[] array : arrayBytes) {
            tamanho += array.length;
        }

        result = new byte[tamanho];
        ByteBuffer target = ByteBuffer.wrap(result);

        for (byte[] array : arrayBytes) {
            target.put(array);
        }

        return result;

    }

    public static int getIntFromBytes(byte[] array){
        return ByteBuffer.wrap(array).getInt();
    }

    public static int getShotFromBytes(byte[] array){
        return ByteBuffer.wrap(array).getShort();
    }


}
