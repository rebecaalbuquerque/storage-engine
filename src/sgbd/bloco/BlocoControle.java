package sgbd.bloco;

import utils.PrintUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static constants.ConstantesRegex.APENAS_NUMERO;
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

    private final int QUANTIDADE_BYTES_BUCKET_HEADER = 10;

    public BlocoControle(int idArquivo, String headers, boolean isBucket) {

        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setTamanhoBloco(intToArrayByte(TAMANHO_BLOCO, 3));
        setStatusArquivo(intToArrayByte(STATUS_0.valor, 1)[0]);

        byte[] dados;

        if (isBucket) {
            dadosHeader = new byte[TAMANHO_BLOCO - 11];

            setTamanhoHeader(intToArrayByte(TAMANHO_BLOCO - 11, 2));
            atualizarProximoBloco(intToArrayByte(0, 4));

        } else {
            String[] arrayFormatado = formatarArrayHeaders(headers);
            dados = stringsToBytes(arrayFormatado);

            dadosHeader = new byte[dados.length];

            setTamanhoHeader(intToArrayByte(dados.length, 2));
            atualizarProximoBloco(intToArrayByte(0, 4));

            setDadosHeader(dados);
        }

    }

    public BlocoControle(byte[] controle) {
        setIdArquivo(controle[0]);
        setTamanhoBloco(new byte[]{controle[1], controle[2], controle[3]});
        setStatusArquivo(controle[4]);
        atualizarProximoBloco(new byte[]{controle[5], controle[6], controle[7], controle[8]});
        setTamanhoHeader(new byte[]{controle[9], controle[10]});

        dadosHeader = new byte[(controle.length - 11)];

        System.arraycopy(controle, 11, dadosHeader, 0, dadosHeader.length);
    }

    /* Utilitários */
    public int getTamanhoTotal() {
        return dadosHeader.length + 11;
    }

    public byte[] getInformacoesCompletas() {
        byte[] result = new byte[2 + tamanhoBloco.length + proximoBloco.length + tamanhoHeader.length + dadosHeader.length];

        ByteBuffer target = ByteBuffer.wrap(result);

        target.put(new byte[]{getIdArquivo()});
        target.put(getTamanhoBloco());
        target.put(getStatusArquivo());
        target.put(getProximoBloco());
        target.put(getTamanhoHeader());
        target.put(getDadosHeader());

        return result;
    }

    ArrayList<String[]> getInformacoesColunas() {
        String[] arrayHeaders = bytesToString(getDadosHeader()).split("\\|");
        ArrayList<String[]> result = new ArrayList<>();

        for (String header : arrayHeaders) {
            result.add(getInformacaoColuna(header));
        }

        return result;
    }

    public ArrayList<String> getColunas() {
        String[] arrayColunas = bytesToString(getDadosHeader()).split("\\|");
        ArrayList<String> result = new ArrayList<>();

        for (String arrayColuna : arrayColunas) {
            String s = arrayColuna.replaceAll(APENAS_NUMERO, "");
            String coluna = s.substring(0, s.length() - 1);
            String tipo = s.substring(s.length() - 1);

            result.add(tipo + " - " + coluna);
        }

        return result;
    }

    /* Buckets */
    public ArrayList<byte[]> getListaBuckets(int tamanhoBuckets) {
        ArrayList<byte[]> result = new ArrayList<>();

        for (int i = 0; i < tamanhoBuckets*QUANTIDADE_BYTES_BUCKET_HEADER; i += QUANTIDADE_BYTES_BUCKET_HEADER) {
            byte[] bucket = Arrays.copyOfRange(dadosHeader, i, i + QUANTIDADE_BYTES_BUCKET_HEADER);
            result.add(bucket);
        }

        return result;
    }

    public boolean hasBucket(int idBucket) {

        byte[] bucket = new byte[QUANTIDADE_BYTES_BUCKET_HEADER];
        System.arraycopy(dadosHeader, idBucket*QUANTIDADE_BYTES_BUCKET_HEADER, bucket, 0, bucket.length);

        int primeiroBlocoBucket = getIntFromBytes(new byte[]{bucket[2], bucket[3], bucket[4], bucket[5]});
        int ultimoBlocoBucket = getIntFromBytes(new byte[]{bucket[6], bucket[7], bucket[8], bucket[9]});

        return primeiroBlocoBucket != 0 && ultimoBlocoBucket != 0;

    }

    /**
     * Retorna o index do começo do ultimo ultimo bloco de um bucket específico
     * */
    public int getUltimoBlocoDoBucket(int idBucket) {
        int indexBucket = idBucket * QUANTIDADE_BYTES_BUCKET_HEADER;
        byte[] bucket = new byte[QUANTIDADE_BYTES_BUCKET_HEADER];

        System.arraycopy(dadosHeader, indexBucket, bucket, 0, bucket.length);

        return getIntFromBytes(new byte[]{ bucket[6], bucket[7], bucket[8], bucket[9] });

    }

    public void updateBucket(int idBucket, Integer primeiroBloco, Integer ultimoBloco){
        int indexBucket = (idBucket * QUANTIDADE_BYTES_BUCKET_HEADER);
        byte[] primeiro;
        byte[] ultimo;
        byte[] bucket = new byte[QUANTIDADE_BYTES_BUCKET_HEADER];

        // 0, 1 = id do bucket
        // 2, 3, 4, 5 = primeiroBlocoBucket
        // 6, 7, 8, 9 = ultimoBlocoBucket

        System.arraycopy(dadosHeader, indexBucket, bucket, 0, bucket.length);

        if (primeiroBloco == null) {
            // Se o primeiroBloco for null é porque então a unica coisa que será modificada no bucket, é o ultimoBloco

            ultimo = intToArrayByte(ultimoBloco, 4);
            bucket[6] = ultimo[0];
            bucket[7] = ultimo[1];
            bucket[8] = ultimo[2];
            bucket[9] = ultimo[3];

        } else if(ultimoBloco == null) {
            // Se o ultimoBloco for null então é porque será modificado o primeiroBloco e o ultimoBloco

            primeiro = intToArrayByte(primeiroBloco, 4);
            bucket[2] = primeiro[0];
            bucket[3] = primeiro[1];
            bucket[4] = primeiro[2];
            bucket[5] = primeiro[3];

            bucket[6] = primeiro[0];
            bucket[7] = primeiro[1];
            bucket[8] = primeiro[2];
            bucket[9] = primeiro[3];
        }

        // Devolve o bucket modificado para dadosHeader
        System.arraycopy(bucket, 0, dadosHeader, indexBucket, bucket.length);
    }

    public void initBuckets(int quantidadeBuckets) {
        ArrayList<byte[]> emptyBuckets = new ArrayList<>();

        for (int i = 0; i < quantidadeBuckets; i++) {
            byte[] idBucket = intToArrayByte(i, 2);

            byte[] bucket = new byte[] {
                    idBucket[0], idBucket[1],   // id bucket
                    0, 0, 0, 0,                 // index primeiro bloco do bucket
                    0, 0, 0, 0                  // index ultimo bloco do bucket
            };

            emptyBuckets.add(bucket);
        }

        byte[] novoHeader = concatenarArrays(emptyBuckets);

        System.arraycopy(novoHeader, 0, dadosHeader, 0, novoHeader.length);

    }

    /* Getters e Setters */
    private byte[] getTamanhoBloco() {
        return tamanhoBloco;
    }

    private byte getStatusArquivo() {
        return statusArquivo;
    }

    public byte[] getProximoBloco() {
        return proximoBloco;
    }

    private byte[] getTamanhoHeader() {
        return tamanhoHeader;
    }

    private byte[] getDadosHeader() {
        return dadosHeader;
    }

    public byte[] getDadosHeaderFormatados() {
        ArrayList<byte[]> headers = new ArrayList<>();
        ArrayList<Byte> header = new ArrayList<>();
        int countQtdElementosColuna = 0;

        for (int i = dadosHeader.length - 2; i >= 0; i--) {

            if ((dadosHeader[i] == stringToBytes("I")[0] || dadosHeader[i] == stringToBytes("A")[0]) && countQtdElementosColuna != -1) {
                header.add(header.size() - header.size(), stringToBytes("]")[0]);
                header.add(header.size() - header.size(), stringToBytes(")")[0]);

                for (int j = i + countQtdElementosColuna; j >= i + 1; j--)
                    header.add(header.size() - header.size(), dadosHeader[j]);


                header.add(header.size() - header.size(), stringToBytes("(")[0]);
                header.add(header.size() - header.size(), dadosHeader[i]);
                header.add(header.size() - header.size(), stringToBytes("[")[0]);

                countQtdElementosColuna = -1;


            } else if (dadosHeader[i] == SEPARADOR_COLUNA_EM_BYTES[0] || i == 0) {
                header.add(SEPARADOR_COLUNA_EM_BYTES[0]);

                if (!header.isEmpty()) {
                    if (i == 0) {
                        header.add(header.size() - header.size(), dadosHeader[i]);
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

                if (countQtdElementosColuna != -1)
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

    private void setDadosHeader(byte[] dadosHeader) {
        this.dadosHeader = dadosHeader;
    }

    private void setTamanhoBloco(byte[] tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
    }

    private void setStatusArquivo(byte statusArquivo) {
        this.statusArquivo = statusArquivo;
    }

    private void atualizarProximoBloco(byte[] proximoBloco) {
        this.proximoBloco = proximoBloco;
    }

    private void setTamanhoHeader(byte[] tamanhoHeader) {
        this.tamanhoHeader = tamanhoHeader;
    }

    @Override
    public String toString() {
        PrintUtils.printLoadingInformation("\nIniciando processo de leitura do Bloco de Controle...\n");
        return bytesToString(getDadosHeader());

    }
}
