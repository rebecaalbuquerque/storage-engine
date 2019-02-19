package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RAFUtils {

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
}
