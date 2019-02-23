package utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {


    public static File criarArquivo(int idTabela){
        String path = DiretorioUtils.getDiretorioSaida() + "/tabela" + idTabela + ".txt";

        File file = new File(path);

        try {

            if(file.createNewFile()){
                System.out.println(path + " Arquivo criado.");
            } else {
                System.out.println("Arquivo " + path + " já existe");
            }

            return file;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
