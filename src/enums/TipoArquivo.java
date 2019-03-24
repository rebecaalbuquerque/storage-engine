package enums;

import static utils.DiretorioUtils.*;

public enum TipoArquivo {

    ENTRADA_ARQUIVOS(0, getDiretorioEntrada()),
    SAIDA_TABELAS(1, getDiretorioSaidaTabelas() + "/tabela"),
    ROW_IDS(2, getDiretorioSaidaRowIDs() + "/rowIDs-tabela"),
    LOG_BUFFER(3, getDiretorioSaidaBuffer() + "/buffer");

    public int id;
    public String path;

    TipoArquivo(int id, String path){ this.id = id; this.path = path; }

}
