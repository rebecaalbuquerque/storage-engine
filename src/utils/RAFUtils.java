package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static utils.ConversorUtils.getShortFromBytes;

@SuppressWarnings("Duplicates")
public class RAFUtils {

    public static long escreverArquivo(File file, byte[] array) {
        long result = 0;

        try {

            RandomAccessFile raf = new RandomAccessFile(file,"rw");
            result = raf.length();
            raf.seek(raf.length());
            raf.write(array);
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void escreverArquivo(File file, byte[] array, int offset) {
        try {

            RandomAccessFile raf = new RandomAccessFile(file,"rw");
            raf.seek(offset);
            raf.write(array);
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] lerDadosArquivo(File file, int start, int length) {
        byte[] result = null;

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(start);

            result = new byte[length];
            raf.read(result);

            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] lerBlocoControle(File file) {
        byte[] tamanhoDescritor = new byte[2];
        byte[] result = null;

        try {
            RandomAccessFile raf1 = new RandomAccessFile(file, "r");
            RandomAccessFile raf2 = new RandomAccessFile(file, "r");

            raf1.seek(9);
            raf1.read(tamanhoDescritor);
            raf1.close();

            // Descobrindo tamanho do descritor (nome das tabelas e etc)
            int tamanhoBlocoControle = getShortFromBytes(tamanhoDescritor);
            result = new byte[tamanhoBlocoControle + 11];

            raf2.seek(0);
            raf2.read(result);

            raf2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void limparArquivo(File file) {
        try {

            RandomAccessFile raf = new RandomAccessFile(file,"rw");
            raf.setLength(0);
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
