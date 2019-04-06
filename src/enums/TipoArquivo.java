package enums;

import static utils.DiretorioUtils.*;

public enum TipoArquivo {

    ENTRADA_ARQUIVOS(0, getDiretorioEntrada()),
    SAIDA_TABELAS(1, getDiretorioSaidaTabelas() + "/tabela"),
    ROW_IDS(2, getDiretorioSaidaRowIDs() + "/rowIDs"),
    ROW_IDS_SHUFFLED(3, getDiretorioSaidaRowIDs() + "/rowIDs-shuffled"),
    LOG_BUFFER(4, getDiretorioSaidaBuffer() + "/buffer");

    public int id;
    public String path;

    TipoArquivo(int id, String path){ this.id = id; this.path = path; }

}
