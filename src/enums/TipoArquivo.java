package enums;

public enum TipoArquivo {

    ENTRADA_TABELAS(0),
    SAIDA_TABELAS(1),
    ENTRADA_ROW_IDS(2),
    SAIDA_ROW_IDS(3);

    public int valor;

    TipoArquivo(int valor){ this.valor = valor; }

}
