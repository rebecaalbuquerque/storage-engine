package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static constants.ConstantesDiretorio.*;

public class DiretorioUtils {

    /**
     * Retorna a string do diretório que está localizado os arquivos de entrada
     * */
    public static String getDiretorioEntrada(){
        return System.getProperty("user.dir") + DIRETORIO_ENTRADA;
    }


    /**
     * Retorna a string do diretório que está localizado as tabelas geradas
     * */
    public static String getDiretorioSaidaTabelas(){
        return System.getProperty("user.dir") + DIRETORIO_SAIDA_TABELAS;
    }

    /**
     * Retorna a string do diretório que está localizado os row ids
     * */
    public static String getDiretorioSaidaRowIDs(){
        return System.getProperty("user.dir") + DIRETORIO_SAIDA_ROW_IDS;
    }

    /**
     * Verificando quantos arquivos existem no diretório de entrada
     * */
    public static int getQuantidadeArquivosEntrada(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioEntrada()).list()).length;
    }

    /**
     * Verificando quantos arquivos existem no diretório das tabelas
     * */
    public static int getQuantidadeArquivosSaidaTabelas(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioSaidaTabelas()).list()).length;
    }

    /**
     * Verificando quantos arquivos existem no diretório dos Row IDs
     * */
    public static int getQuantidadeArquivosRowIDs(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioSaidaRowIDs()).list()).length;
    }

    public static ArrayList<String> getListaArquivos(boolean isEntrada){
        ArrayList<String> result = new ArrayList<>();
        File[] arrayArquivos;

        if(isEntrada)
            arrayArquivos = new File(getDiretorioEntrada()).listFiles();
        else
            arrayArquivos = new File(getDiretorioSaidaTabelas()).listFiles();

        if(arrayArquivos != null){
            for (File a : arrayArquivos) {
                result.add(a.getName());
            }
        }

        return result;

    }

}
