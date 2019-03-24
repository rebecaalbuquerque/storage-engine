package utils;

import enums.TipoArquivo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static utils.DiretorioUtils.*;

public class FileUtils {

    public static void escreverEmArquivo(File file, ArrayList<String> dados){
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {

            if (file != null) {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);

                for (String dado : dados) {
                    bw.write(dado);
                    bw.newLine();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (bw != null) {
                    bw.close();
                    fw.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File criarArquivo(int idTabela, TipoArquivo tipoArquivo){

        String path = tipoArquivo.path + idTabela + ".txt";

        File file = new File(path);

        try {

            if(file.createNewFile()){

                switch (tipoArquivo){
                    case SAIDA_TABELAS:
                        PrintUtils.printLoadingInformation("Criando novo container: " + file.getName());
                        break;

                    case ROW_IDS:
                        PrintUtils.printLoadingInformation("Criando novo arquivo de Row ID: " + file.getName());
                        break;

                    case LOG_BUFFER:
                        PrintUtils.printLoadingInformation("Criando novo arquivo de Log Buffer: " + file.getName());
                }

            }

            return file;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File buscarTabela(int idTabela){
        String path = getDiretorioSaidaTabelas() + "/tabela" + idTabela + ".txt";
        return new File(path);
    }

}
