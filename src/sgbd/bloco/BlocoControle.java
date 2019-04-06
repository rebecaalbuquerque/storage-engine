package sgbd.bloco;

import utils.PrintUtils;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static constants.ConstantesRegex.APENAS_NUMERO;
import static constants.ConstantesRegex.CARACTER_ESPECIAL;
import static constants.ConstantesSGBD.SEPARADOR_COLUNA_EM_BYTES;
import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static enums.StatusContainer.STATUS_0;
import static utils.BlocoUtils.formatarArrayHeaders;
import static utils.BlocoUtils.getInformacaoColuna;
import static utils.ConversorUtils.*;

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
        atualizarProximoBloco(intToArrayByte(0, 4));

        setDadosHeader(dados);

    }

    public BlocoControle(byte[] controle){
        setIdArquivo(controle[0]);
        setTamanhoBloco(new byte[]{ controle[1], controle[2], controle[3]  });
        setStatusArquivo(controle[4]);
        atualizarProximoBloco(new byte[]{ controle[5], controle[6], controle[7], controle[8]  });
        setTamanhoHeader(new byte[]{ controle[9], controle[10] });

        dadosHeader = new byte[(controle.length - 11)];

        System.arraycopy(controle, 11, dadosHeader, 0, dadosHeader.length);
    }

    /* Utilitários */
    public int getTamanhoTotal(){
        return dadosHeader.length + 11;
    }

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

    ArrayList<String[]> getInformacoesColunas(){
        String[] arrayHeaders = bytesToString(getDadosHeader()).split("\\|");
        ArrayList<String[]> result = new ArrayList<>();

        for (String header : arrayHeaders) {
            result.add(getInformacaoColuna(header));
        }

        return result;
    }

    /* Getters e Setters */
    private byte[] getTamanhoBloco() { return tamanhoBloco; }

    private byte getStatusArquivo() { return statusArquivo; }

    public byte[] getProximoBloco() { return proximoBloco; }

    private byte[] getTamanhoHeader() { return tamanhoHeader; }

    private byte[] getDadosHeader() { return dadosHeader; }

    public byte[] getDadosHeaderFormatados() {
        ArrayList<byte[]> headers = new ArrayList<>();
        ArrayList<Byte> header = new ArrayList<>();
        int countQtdElementosColuna = 0;

        for(int i = dadosHeader.length - 2; i >= 0; i--){

            if((dadosHeader[i] == stringToBytes("I")[0] || dadosHeader[i] == stringToBytes("A")[0]) && countQtdElementosColuna != -1){
                header.add(header.size()-header.size(), stringToBytes("]")[0]);
                header.add(header.size()-header.size(), stringToBytes(")")[0]);

                for(int j = i + countQtdElementosColuna; j >= i + 1; j--)
                    header.add(header.size()-header.size(), dadosHeader[j]);


                header.add(header.size()-header.size(), stringToBytes("(")[0]);
                header.add(header.size()-header.size(), dadosHeader[i]);
                header.add(header.size()-header.size(), stringToBytes("[")[0]);

                countQtdElementosColuna = -1;


            } else if(dadosHeader[i] == SEPARADOR_COLUNA_EM_BYTES[0] || i == 0) {
                header.add(SEPARADOR_COLUNA_EM_BYTES[0]);

                if(!header.isEmpty()) {
                    if(i == 0){
                        header.add(header.size()-header.size(), dadosHeader[i]);
                    }

                    byte[] arr = new byte[header.size()];

                    for (int j = 0; j < header.size(); j++) {
                        arr[j] = header.get(j);
                    }

                    headers.add(arr);

                    header.clear();

                    countQtdElementosColuna = 0;

                }

            } else {

                if(countQtdElementosColuna != -1)
                    countQtdElementosColuna++;
                else
                    header.add(header.size() - header.size(), dadosHeader[i]);


            }

        }

        return concatenarArrays(headers);
    }

    public void setProximoBloco(byte[] proximoBloco) {
        this.proximoBloco = proximoBloco;
    }

    public void setProximoBloco(int proximoBloco) {
        this.proximoBloco = intToArrayByte(proximoBloco, 4);
    }

    public ArrayList<String> getColunas(){
        String[] arrayColunas = bytesToString(getDadosHeader()).split("\\|");
        ArrayList<String> result = new ArrayList<>();

        for (String arrayColuna : arrayColunas) {
            String coluna = arrayColuna.replaceAll(APENAS_NUMERO, "");
            result.add(coluna.substring(0, coluna.length() - 1));
        }

        return result;
    }

    private void setDadosHeader(byte[] dadosHeader) { this.dadosHeader = dadosHeader; }

    private void setTamanhoBloco(byte[] tamanhoBloco) { this.tamanhoBloco = tamanhoBloco; }

    private void setStatusArquivo(byte statusArquivo) { this.statusArquivo = statusArquivo; }

    private void atualizarProximoBloco(byte[] proximoBloco) { this.proximoBloco = proximoBloco; }

    private void setTamanhoHeader(byte[] tamanhoHeader) { this.tamanhoHeader = tamanhoHeader; }

    @Override
    public String toString() {
        PrintUtils.printLoadingInformation( "\nIniciando processo de leitura do Bloco de Controle...\n");
        return bytesToString(getDadosHeader());

    }
}
