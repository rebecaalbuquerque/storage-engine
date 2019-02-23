package sgbd.bloco;

import java.nio.ByteBuffer;

import static enums.StatusContainer.STATUS_0;
import static utils.BlocoUtils.formatarArrayHeaders;
import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.ConversorUtils.intToArrayByte;
import static utils.ConversorUtils.stringsToBytes;

public class BlocoControle extends Bloco {

    private byte[] tamanhoBloco = new byte[3];
    private byte statusArquivo;
    private byte[] proximoBloco = new byte[4];
    private byte[] tamanhoHeader = new byte[2];
    private byte[] dadosHeader;

    public BlocoControle(int idArquivo, String headers){

        String[] arrayFormatado = formatarArrayHeaders(headers);

        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setTamanhoBloco(intToArrayByte(TAMANHO_BLOCO, 3));
        setStatusArquivo(intToArrayByte(STATUS_0.valor, 1)[0]);

        byte[] dados = stringsToBytes(arrayFormatado);
        dadosHeader = new byte[dados.length];

        setTamanhoHeader(intToArrayByte(dados.length, 2));
        atualizarProximoBloco(intToArrayByte(11 + dados.length, 4));

        setDadosHeader(dados);

    }

    /* Utilitários */
    public byte[] getInformacoesCompletas(){
        byte[] result = new byte[2 + tamanhoBloco.length + proximoBloco.length + tamanhoHeader.length + dadosHeader.length];

        ByteBuffer target = ByteBuffer.wrap(result);

        target.put(new byte[]{ getIdArquivo() });
        target.put(getTamanhoBloco());
        target.put(getStatusArquivo());
        target.put(getProximoBloco());
        target.put(getTamanhoHeader());
        target.put(getDadosHeader());

        return result;
    }

    /* Getters e Setters */
    public byte[] getTamanhoBloco() { return tamanhoBloco; }

    public byte getStatusArquivo() { return statusArquivo; }

    public byte[] getProximoBloco() { return proximoBloco; }

    public byte[] getTamanhoHeader() { return tamanhoHeader; }

    public byte[] getDadosHeader() { return dadosHeader; }

    public void setDadosHeader(byte[] dadosHeader) { this.dadosHeader = dadosHeader; }

    public void setTamanhoBloco(byte[] tamanhoBloco) { this.tamanhoBloco = tamanhoBloco; }

    public void setStatusArquivo(byte statusArquivo) { this.statusArquivo = statusArquivo; }

    public void atualizarProximoBloco(byte[] proximoBloco) { this.proximoBloco = proximoBloco; }

    public void setTamanhoHeader(byte[] tamanhoHeader) { this.tamanhoHeader = tamanhoHeader; }
}
