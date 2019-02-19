package utils;

import java.io.File;
import java.util.Objects;

import static constants.ConstantesDiretorio.DIRETORIO_ENTRADA;
import static constants.ConstantesDiretorio.DIRETORIO_SAIDA;

public class DiretorioUtils {

    public static String getDiretorioEntrada(){
        return System.getProperty("user.dir") + DIRETORIO_ENTRADA;
    }

    public static String getDiretorioSaida(){
        return System.getProperty("user.dir") + DIRETORIO_SAIDA;
    }

    public static int getQuantidadeArquivosEntrada(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioEntrada()).list()).length;
    }

    public static int getQuantidadeArquivosSaida(){
        return Objects.requireNonNull(new File(DiretorioUtils.getDiretorioSaida()).list()).length;
    }

}
