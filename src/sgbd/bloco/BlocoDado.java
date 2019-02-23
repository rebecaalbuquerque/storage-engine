package sgbd.bloco;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static constants.ConstantesRegex.SEPARADOR_COLUNA;
import static enums.TipoBloco.TIPO_1;
import static constants.ConstantesRegex.APENAS_NUMERO;
import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.ConversorUtils.*;

// TODO: Colocar lógica para quando os dados a serem inseridos não forem maiories que o tamanho do bloco,
//  sendo necessário colocar em outro bloco

public class BlocoDado extends Bloco {

    private static int contador;

    /* Informações de um bloco de dados */
    private byte[] idBloco = new byte[3];
    private byte tipo;
    private byte[] tamanhoTuplaDirectory = new byte[2];
    private byte[] ultimoEnderecoTupla = new byte[2]; // endereço do ultimo byte da diretorioTupla usado no bloco
    private byte[] dados; // tupla directory + tuplas

    /* Informações auxiliares sobre o Tuple directory */
    private int tamanhoTuplasDisponivel;

    public BlocoDado(int idArquivo, String tupla) {
        contador += 1;

        /* Header do bloco */
        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setIdBloco(intToArrayByte(contador, 3));
        setTipo(intToArrayByte(TIPO_1.valor, 1)[0]);

        /* Dados */
        this.dados = new byte[TAMANHO_BLOCO - 9];
        setTamanhoTuplasDisponivel(this.dados.length);
        setUltimoEnderecoTupla(intToArrayByte(this.dados.length + 9, 2));

        ArrayList<byte[]> listaColunas = new ArrayList<>();

        for (String dado : tupla.split(SEPARADOR_COLUNA)) {

            if (Pattern.matches(APENAS_NUMERO, dado))
                listaColunas.add(colunaIntParaBytes(dado));
            else
                listaColunas.add(colunaStringParaBytes(dado));

        }

        inserirNovaTupla(getTuplaFormatada(listaColunas));
    }

    /* Utilitários */
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

    private void inserirNovaTupla(byte[] novaTupla){
        int countIndexTuplas = 0;
        int countIndexTuplaDirectory = 0;

        // TODO
        int ultimoEndereco = getShotFromBytes(getUltimoEnderecoTupla());
        setUltimoEnderecoTupla(intToArrayByte(ultimoEndereco - novaTupla.length, 2));

        // Atualiza a quantidade de bytes disponível para novas tuplas serem adicionadas
        setTamanhoTuplasDisponivel( (tamanhoTuplasDisponivel - (novaTupla.length + 2) ) );

        // Inserindo no começo do Tuple Directory o index de início da tupla que está sendo adicionada
        int tamTuplaDirectory = getShotFromBytes(tamanhoTuplaDirectory);
        for (int i = tamTuplaDirectory; i <= tamTuplaDirectory + 1; i++) {
            this.dados[i] = getUltimoEnderecoTupla()[countIndexTuplaDirectory];
        }

        // Atualizando o tamanho do Tuple Directory
        setTamanhoTuplaDirectory(intToArrayByte(tamTuplaDirectory + 2, 2));

        // Inserindo no final do Tuple Directory os dados na nova tupla
        for (int i = ultimoEndereco - novaTupla.length; i < ultimoEndereco - 9; i++) {
            dados[i] = novaTupla[countIndexTuplas];
            countIndexTuplas++;
        }

    }

    private byte[] getTuplaFormatada(ArrayList<byte[]> listaColunas){
        byte[] tamanho = intToArrayByte(concatenarArrays(listaColunas).length, 3);

        listaColunas.add(0, tamanho);

        return concatenarArrays(listaColunas);
    }

    private byte[] colunaIntParaBytes(String dado) {
        byte[] tamanhoColuna = intToArrayByte(4, 2);
        byte[] dadosColuna = intToArrayByte(Integer.parseInt(dado), 4);

        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add(tamanhoColuna);
                    add(dadosColuna);
                }}
        );
    }

    private byte[] colunaStringParaBytes(String dado) {
        byte[] dadosColuna = stringToBytes(dado);
        byte[] tamanhoColuna = intToArrayByte(dadosColuna.length, 2);

        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add(tamanhoColuna);
                    add(dadosColuna);
                }}
        );

    }

    /* Getters e Setters */

    public int getTamanhoTuplasDisponivel() { return tamanhoTuplasDisponivel; }

    public void setTamanhoTuplasDisponivel(int tamanhoTuplasDisponivel) { this.tamanhoTuplasDisponivel = tamanhoTuplasDisponivel; }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] diretorioTupla) {
        this.dados = diretorioTupla;
    }

    public byte[] getIdBloco() {
        return idBloco;
    }

    public void setIdBloco(byte[] idBloco) {
        this.idBloco = idBloco;
    }

    public byte getTipo() {
        return tipo;
    }

    public void setTipo(byte tipo) {
        this.tipo = tipo;
    }

    public byte[] getTamanhoTuplaDirectory() {
        return tamanhoTuplaDirectory;
    }

    public void setTamanhoTuplaDirectory(byte[] tamanhoTuplaDirectory) {
        this.tamanhoTuplaDirectory = tamanhoTuplaDirectory;
    }

    public byte[] getUltimoEnderecoTupla() {
        return ultimoEnderecoTupla;
    }

    public void setUltimoEnderecoTupla(byte[] ultimoEnderecoTupla) {
        this.ultimoEnderecoTupla = ultimoEnderecoTupla;
    }
}
