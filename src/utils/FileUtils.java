package utils;

import enums.TipoArquivo;

import java.io.File;
import java.io.IOException;

import static utils.DiretorioUtils.*;

public class FileUtils {


    public static File criarArquivo(int idTabela, TipoArquivo tipoArquivo){

        String path;

        // TODO: precisa do switch? não pode ser "path = tipoArquivo.path + idTabela + ".txt";" direto?
        switch (tipoArquivo){
            case SAIDA_TABELAS:
                path = tipoArquivo.path + idTabela + ".txt";
                break;

            case ROW_IDS:
                path = tipoArquivo.path + idTabela + ".txt";
                break;

            case LOG_BUFFER:
                path = tipoArquivo.path + idTabela + ".txt";
                break;

            default:
                path = "";
        }

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
