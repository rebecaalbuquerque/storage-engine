package sgbd.bloco;

import java.util.ArrayList;

import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static enums.TipoBloco.TIPO_1;
import static utils.BlocoUtils.getTuplaFormatada;
import static utils.ConversorUtils.*;

public class BlocoDado extends Bloco {

    private static int contador;

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

    /* Utilitários */

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
        setUltimoEnderecoTupla(intToArrayByte(ultimoEndereco - novaTupla.length - 1, 2));

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
        // array de bytes sem a informação do tamanho da tupla
        int ultimoByteTupla = getShortFromBytes((getUltimoEnderecoTupla());



        return null;

    }

    /* Getters e Setters */

    public int getTamanhoTuplasDisponivel() { return tamanhoTuplasDisponivel; }

    private void setTamanhoTuplasDisponivel(int tamanhoTuplasDisponivel) { this.tamanhoTuplasDisponivel = tamanhoTuplasDisponivel; }

    private byte[] getDados() {
        return dados;
    }

    private void setDados(byte[] diretorioTupla) {
        this.dados = diretorioTupla;
    }

    private byte[] getIdBloco() {
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

    private byte[] getTamanhoTuplaDirectory() {
        return tamanhoTuplaDirectory;
    }

    private void setTamanhoTuplaDirectory(byte[] tamanhoTuplaDirectory) { this.tamanhoTuplaDirectory = tamanhoTuplaDirectory; }

    private byte[] getUltimoEnderecoTupla() {
        return ultimoEnderecoTupla;
    }

    private void setUltimoEnderecoTupla(byte[] ultimoEnderecoTupla) {
        this.ultimoEnderecoTupla = ultimoEnderecoTupla;
    }
}
