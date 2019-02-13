package model;

abstract class Bloco {
    private byte idArquivo;

    Bloco(){}

    public Bloco(byte idArquivo, byte[] idBloco) {
        this.idArquivo = idArquivo;
    }

    public byte getIdArquivo() { return idArquivo; }

    public void setIdArquivo(byte idArquivo) { this.idArquivo = idArquivo; }

}
