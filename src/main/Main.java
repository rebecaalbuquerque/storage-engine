package main;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {

    private static final String FILE_PATH = "/Users/Rebeca/Documents/BD2/forn-tpch.txt";

    public static void main(String[] args) {

        try {

            RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "r");
            String line;

            while ( (line = raf.readLine()) != null ) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
