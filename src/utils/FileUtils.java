package utils;

import java.io.File;
import java.io.IOException;

import static utils.RAFUtils.limparArquivo;

public class FileUtils {


    public static File criarArquivo(int idTabela){
        String path = DiretorioUtils.getDiretorioSaidaTabelas() + "/tabela" + idTabela + ".txt";

        File file = new File(path);

        try {

            if(file.createNewFile()){
                System.out.println("Criando novo container: " + file.getName());
            } else {
                limparArquivo(file);
            }

            return file;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File buscarArquivo(int idTabela){
        String path = DiretorioUtils.getDiretorioSaidaTabelas() + "/tabela" + idTabela + ".txt";

        return new File(path);
    }

}
