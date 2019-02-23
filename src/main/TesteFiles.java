package main;

import utils.FileUtils;
import utils.RAFUtils;

import java.io.File;

public class TesteFiles {

    public static void main(String[] args) {

        File f = FileUtils.criarArquivo(3);

        RAFUtils.escreverArquivo(f, new byte[]{67, 79, 68, 95, 65, 85, 84, 72, 79, 82, 73, 53, 124, 78, 65, 77, 69, 95, 65, 85, 84, 72, 79, 82, 65, 49, 48, 48}, 0);

    }

}
