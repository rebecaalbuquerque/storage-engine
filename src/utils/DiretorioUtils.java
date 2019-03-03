package utils;

import java.io.File;
import java.util.Objects;

import static constants.ConstantesDiretorio.DIRETORIO_ENTRADA;
import static constants.ConstantesDiretorio.DIRETORIO_SAIDA_TABELAS;

public class DiretorioUtils {

    /**
     * Retorna a string do diretório que está localizando os arquivos de entrada
     * */
    public static String getDiretorioEntrada(){
        return System.getProperty("user.dir") + DIRETORIO_ENTRADA;
    }


    /**
     * Retorna a string do diretório que está localizando os arquivos de saida
     * */
    public static String getDiretorioSaidaTabelas(){
        return System.getProperty("user.dir") + DIRETORIO_SAIDA_TABELAS;
    }

    /**
     * Verificando quantos arquivos existem no diretório de entrada
     * */
    public static int getQuantidadeArquivosEntrada(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioEntrada()).list()).length;
    }

    /**
     * Verificando quantos arquivos existem no diretório de saida
     * */
    public static int getQuantidadeArquivosSaidaTabelas(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioSaidaTabelas()).list()).length;
    }

}
