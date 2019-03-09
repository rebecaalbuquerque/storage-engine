package utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {


    public static File criarArquivo(int idTabela, boolean isRowID){

        String path;

        if(isRowID)
            path = DiretorioUtils.getDiretorioSaidaRowIDs() + "/rowIDs-tabela" + idTabela + ".txt";
        else
            path = DiretorioUtils.getDiretorioSaidaTabelas() + "/tabela" + idTabela + ".txt";

        File file = new File(path);

        try {

            if(file.createNewFile()){

                if(isRowID)
                    PrintUtils.printLoadingInformation("Criando novo arquivo de Row ID: " + file.getName());
                else
                    PrintUtils.printLoadingInformation("Criando novo container: " + file.getName());

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
