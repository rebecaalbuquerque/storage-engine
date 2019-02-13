package model;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enums.StatusContainer.STATUS_1;
import static utils.Constantes.TAMANHO_BLOCO;
import static utils.ConversorUtils.*;
import static utils.RegexExpressions.APENAS_NUMERO;

public class BlocoControle extends Bloco {

    private byte[] tamanhoBloco = new byte[3];
    private byte statusArquivo;
    private byte[] proximoBloco = new byte[4];
    private byte[] tamanhoHeader = new byte[2];
    private byte[] dadosHeader;

    public BlocoControle(String[] arrayHeaders, int idArquivo){
        int tamanhoTotalHeader = 0;
        Pattern p = Pattern.compile(APENAS_NUMERO);
        Matcher m;

        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setTamanhoBloco(intToArrayByte(TAMANHO_BLOCO, 3));
        setStatusArquivo(intToArrayByte(STATUS_1.valor, 1)[0]);

        for (String item : arrayHeaders) {
            m = p.matcher(item);

            if(m.find())
                tamanhoTotalHeader += Integer.parseInt(m.group());
        }

        setProximoBloco(intToArrayByte(10+tamanhoTotalHeader, 4));
        this.dadosHeader = new byte[tamanhoTotalHeader];
        setTamanhoHeader(intToArrayByte(tamanhoTotalHeader, 2));
        setDadosHeader(stringsToBytes(arrayHeaders));

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

    public void setProximoBloco(byte[] proximoBloco) { this.proximoBloco = proximoBloco; }

    public void setTamanhoHeader(byte[] tamanhoHeader) { this.tamanhoHeader = tamanhoHeader; }
}
