package sgbd.bloco;

import utils.PrintUtils;

import java.util.ArrayList;

import static constants.ConstantesSGBD.SEPARADOR_COLUNA_EM_BYTES;
import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static enums.TipoBloco.TIPO_1;
import static utils.BlocoUtils.getIndexesTuplas;
import static utils.BlocoUtils.getTuplaFormatada;
import static utils.ConversorUtils.*;

@SuppressWarnings("Duplicates")
public class BlocoDado extends Bloco {

    private static int contador = -1;

    /* Informações de um bloco de dados */
    private byte[] idBloco = new byte[3];
    private byte tipo;
    private byte[] tamanhoTuplaDirectory = new byte[2];
    private byte[] ultimoEnderecoTupla = new byte[2];
    private byte[] dados; // tupla directory + tuplas

    private int tamanhoTuplasDisponivel;

    public BlocoDado(int idArquivo){
        contador += 1;

        /* Header do bloco */
        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setIdBloco(intToArrayByte(contador, 3));
        setTipo(intToArrayByte(TIPO_1.valor, 1)[0]);

        /* Dados */
        this.dados = new byte[TAMANHO_BLOCO - 9];
        setTamanhoTuplasDisponivel(this.dados.length);
        setUltimoEnderecoTupla(intToArrayByte(this.dados.length + 9, 2));

    }

    public BlocoDado(byte[] bloco){
        setIdArquivo(bloco[0]);
        setIdBloco(new byte[]{ bloco[1], bloco[2], bloco[3] });
        setTipo(bloco[4]);
        setTamanhoTuplaDirectory(new byte[]{ bloco[5], bloco[6]});
        setUltimoEnderecoTupla(new byte[]{ bloco[7], bloco[8] });

        this.dados = new byte[bloco.length - 9];

        System.arraycopy(bloco, 9, this.dados, 0, this.dados.length);

    }

    /* Utilitários */
    public ArrayList<String> getRowIDs(){
        ArrayList<String> result = new ArrayList<>();
        int containerID = (int) getIdArquivo();
        int blocoID = getIntFrom3Bytes(getIdBloco());

        for (int indexTupla : getIndexesTuplas(getTuplaDirectory())) {
            result.add(containerID + "-" + blocoID + "-" + indexTupla);
        }

        return result;
    }

    public void adicionarNovaTupla(String novaTupla){
        inserirTupla(getTuplaFormatada(novaTupla));
    }

    public byte[] getInformacoesCompletas() {
        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add( new byte[]{getIdArquivo()} );
                    add( getIdBloco() );
                    add( new byte[]{getTipo()} );
                    add( getTamanhoTuplaDirectory() );
                    add( getUltimoEnderecoTupla() );
                    add( getDados() );
                }}
        );
    }

    private void inserirTupla(byte[] novaTupla){
        int countIndexTuplas = 0;
        int countIndexTuplaDirectory = 0;

        int ultimoEndereco = getShortFromBytes(getUltimoEnderecoTupla());
        setUltimoEnderecoTupla(intToArrayByte(ultimoEndereco - novaTupla.length , 2));

        // Atualiza a quantidade de bytes disponível para novas tuplas serem adicionadas
        setTamanhoTuplasDisponivel( (tamanhoTuplasDisponivel - (novaTupla.length + 2) ) );

        // Inserindo no começo do Tuple Directory o index de início da tupla que está sendo adicionada
        int tamTuplaDirectory = getShortFromBytes(tamanhoTuplaDirectory);
        for (int i = tamTuplaDirectory; i <= tamTuplaDirectory + 1; i++) {
            this.dados[i] = getUltimoEnderecoTupla()[countIndexTuplaDirectory];
            countIndexTuplaDirectory++;
        }

        // Atualizando o tamanho do Tuple Directory
        setTamanhoTuplaDirectory(intToArrayByte(tamTuplaDirectory + 2, 2));

        // Inserindo no final do Tuple Directory os dados na nova tupla
        for (int i = ultimoEndereco - novaTupla.length - 9; i < ultimoEndereco - 9; i++) {
            dados[i] = novaTupla[countIndexTuplas];
            countIndexTuplas++;
        }

    }

    public byte[] getTuplas(){
        int[] indexesTuplas = getIndexesTuplas(getTuplaDirectory());
        ArrayList<byte[]> tuplas = new ArrayList<>();
        ArrayList<byte[]> tuplasComSeparador = new ArrayList<>();

        for (int index: indexesTuplas) {
            byte[] tamanhoTupla = new byte[4];
            int countTamanhoTupla = 0;

            // Descobrindo tamanho da tupla
            for (int i = index; i < index + 4; i++) {
                tamanhoTupla[countTamanhoTupla] = this.dados[i];
                countTamanhoTupla++;
            }

            tuplas.add(
                    getTuplaReduzidaFormatada(index, getIntFromBytes(tamanhoTupla))
            );

        }

        for (byte[] tupla : tuplas) {
            tuplasComSeparador.add(tupla);
            tuplasComSeparador.add(SEPARADOR_COLUNA_EM_BYTES);
        }

        return concatenarArrays(tuplasComSeparador);
    }

    private ArrayList<byte[]> getListaTuplas(){
        int[] indexesTuplas = getIndexesTuplas(getTuplaDirectory());
        ArrayList<byte[]> tuplas = new ArrayList<>();

        for (int index: indexesTuplas) {
            byte[] tamanhoTupla = new byte[4];
            int countTamanhoTupla = 0;

            // Descobrindo tamanho da tupla
            for (int i = index; i < index + 4; i++) {
                tamanhoTupla[countTamanhoTupla] = this.dados[i];
                countTamanhoTupla++;
            }

            tuplas.add(
                    getTuplaCompleta(index, getIntFromBytes(tamanhoTupla))
            );

        }

        PrintUtils.printAdditionalInformation("Total de tuplas do Bloco de Dados " + getIntFrom3Bytes(getIdBloco()) + " = " + tuplas.size() + "\n");
        return tuplas;
    }

    private byte[] getTuplaCompleta(int inicio, int tamanhoTupla){
        byte[] result = new byte[tamanhoTupla];

        System.arraycopy(this.dados, inicio + 4, result, 0, result.length);

        return result;
    }

    private byte[] getTuplaReduzidaFormatada(int inicio, int tamanhoCompletoTupla){
        byte[] tupla = new byte[tamanhoCompletoTupla];
        byte[] result;

        // Começa a ler ignorando os 4 primeiros bytes, referentes ao tamanho da tupla
        int quantidadeColunas = 0;
        int countTamanhoTupla = 0;
        int countElementosAdd = 0;
        int countColuna = 0;
        boolean isAdd = false;
        byte[] tamanhoColunaBytes = new byte[2];

        for (int i = inicio + 4; i < tamanhoCompletoTupla + inicio + 4; i++){

            if(isAdd){
                tupla[countTamanhoTupla] = this.dados[i];
                countTamanhoTupla++;
                countElementosAdd++;

                if(countElementosAdd == getShortFromBytes(tamanhoColunaBytes)){
                    isAdd = false;
                    countElementosAdd = 0;
                    tamanhoColunaBytes = new byte[2];
                }
            } else {
                tamanhoColunaBytes[countColuna] = this.dados[i];
                countColuna++;

                if (countColuna == 2) {
                    quantidadeColunas++;
                    countColuna = 0;
                    isAdd = true;
                }
            }

        }

        result = new byte[tamanhoCompletoTupla - (quantidadeColunas * 2)];

        System.arraycopy(tupla, 0, result, 0, result.length);

        return result;
    }

    /* Getters e Setters */
    private byte[] getTuplaDirectory(){
        byte[] tuplaDirectory = new byte[ getShortFromBytes(getTamanhoTuplaDirectory()) ];

        if (tuplaDirectory.length >= 0)
            System.arraycopy(this.dados, 0, tuplaDirectory, 0, tuplaDirectory.length);

        return tuplaDirectory;

    }

    public int getTamanhoTuplasDisponivel() { return tamanhoTuplasDisponivel; }

    private void setTamanhoTuplasDisponivel(int tamanhoTuplasDisponivel) { this.tamanhoTuplasDisponivel = tamanhoTuplasDisponivel; }

    private byte[] getDados() {
        return dados;
    }

    private void setDados(byte[] diretorioTupla) {
        this.dados = diretorioTupla;
    }

    public byte[] getIdBloco() {
        return idBloco;
    }

    private void setIdBloco(byte[] idBloco) {
        this.idBloco = idBloco;
    }

    private byte getTipo() {
        return tipo;
    }

    private void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public byte[] getTamanhoTuplaDirectory() {
        return tamanhoTuplaDirectory;
    }

    private void setTamanhoTuplaDirectory(byte[] tamanhoTuplaDirectory) { this.tamanhoTuplaDirectory = tamanhoTuplaDirectory; }

    public byte[] getUltimoEnderecoTupla() {
        return ultimoEnderecoTupla;
    }

    private void setUltimoEnderecoTupla(byte[] ultimoEnderecoTupla) {
        this.ultimoEnderecoTupla = ultimoEnderecoTupla;
    }

    public String toString(BlocoControle controle) {
        PrintUtils.printLoadingInformation("Iniciando processo de leitura das tuplas do Bloco de Dados: " + getIntFrom3Bytes(getIdBloco()) + " ...");

        ArrayList<byte[]> tuplas = getListaTuplas();
        ArrayList<String[]> informacoesColunas = controle.getInformacoesColunas();
        ArrayList<String> tuplasString = new ArrayList<>();
        String result = "";

        for (byte[] tupla : tuplas) {
            int numeroColuna = 0;
            byte[] tamanhoColunaAtual = new byte[2];
            byte[] dadosColunaAtual = new byte[0];
            int countTamanhoColunaAtual = 0;
            int countDadoColunaAtual = 0;
            boolean isAddDados = false;
            String linha = "";

            for (int i = 0; i < tupla.length; i++) {

                if(isAddDados){
                    dadosColunaAtual[countDadoColunaAtual] = tupla[i];
                    countDadoColunaAtual++;

                    if(countDadoColunaAtual == getShortFromBytes(tamanhoColunaAtual)){
                        // Aqui o array de dados já está cheio, falta descobrir se é "I" ou "A"
                        isAddDados = false;
                        countTamanhoColunaAtual= 0;

                        String[] infoColuna = informacoesColunas.get(numeroColuna);
                        numeroColuna++;

                        if(infoColuna[0].equals("A")){
                            linha += bytesToString(dadosColunaAtual) + "|";
                        } else {
                            linha += getIntFromBytes(dadosColunaAtual) + "|";
                        }

                    }

                } else {
                    tamanhoColunaAtual[countTamanhoColunaAtual] = tupla[i];
                    countTamanhoColunaAtual++;
                }

                if(countTamanhoColunaAtual == 2){
                    dadosColunaAtual = new byte[getShortFromBytes(tamanhoColunaAtual)];
                    countDadoColunaAtual = 0;
                    countTamanhoColunaAtual = -1;
                    isAddDados = true;
                }

            }
            tuplasString.add(linha);
        }

        for (String s : tuplasString) {
            result += s + "\n";
        }

        return result;
    }
}
