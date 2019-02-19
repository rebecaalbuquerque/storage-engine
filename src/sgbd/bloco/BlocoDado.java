package sgbd.bloco;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static enums.TipoBloco.TIPO_1;
import static constants.ConstantesRegex.APENAS_NUMERO;
import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.ConversorUtils.*;

// TODO: Colocar lógica para quando os dados a serem inseridos não forem maiories que o tamanho do sgbd.bloco,
//  sendo necessário colocar em outro sgbd.bloco

public class BlocoDado extends Bloco {

    private static int contador;

    private byte[] idBloco = new byte[3];
    private byte tipo;
    private byte[] tamanhoTupla = new byte[2];
    private byte[] ultimoEnderecoTupla = new byte[2]; // endereço do ultimo byte da diretorioTupla usado no sgbd.bloco
    private byte[] diretorioTupla;

    public BlocoDado(int idArquivo, String[] dados) {
        contador += 1;

        /* Header do sgbd.bloco */
        setIdArquivo(intToArrayByte(idArquivo, 1)[0]);
        setIdBloco(intToArrayByte(contador, 3));
        setTipo(intToArrayByte(TIPO_1.valor, 1)[0]);

        /* Dados */
        this.diretorioTupla = new byte[TAMANHO_BLOCO - 9];
        ArrayList<byte[]> listaDados = new ArrayList<>();

        for (String dado : dados) {

            if (Pattern.matches(APENAS_NUMERO, dado)) {

                listaDados.add(colunaIntParaBytes(dado));

            } else {

                listaDados.add(colunaStringParaBytes(dado));

            }

        }


        byte[] tuplaAux = concatenarArrays(listaDados);
        this.diretorioTupla = new byte[tuplaAux.length + 3];

        setDiretorioTupla(
                concatenarArrays(
                        new ArrayList<byte[]>(){{
                            add(intToArrayByte(tuplaAux.length, 3));
                            add(tuplaAux);
                        }}
                )
        );

        setTamanhoTupla(intToArrayByte(this.diretorioTupla.length, 2));
        setUltimoEnderecoTupla(intToArrayByte(this.diretorioTupla.length + 9 - 1, 2));
    }

    /* Utilitários */
    public byte[] getInformacoesCompletas() {
        return concatenarArrays(
                new ArrayList<byte[]>() {{
                    add( new byte[]{getIdArquivo()} );
                    add( getIdBloco() );
                    add( new byte[]{getTipo()} );
                    add( getTamanhoTupla() );
                    add( getUltimoEnderecoTupla() );
                    add( getDiretorioTupla() );
                }}
        );
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

    public byte[] getDiretorioTupla() {
        return diretorioTupla;
    }

    public void setDiretorioTupla(byte[] diretorioTupla) {
        this.diretorioTupla = diretorioTupla;
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

    public byte[] getTamanhoTupla() {
        return tamanhoTupla;
    }

    public void setTamanhoTupla(byte[] tamanhoTupla) {
        this.tamanhoTupla = tamanhoTupla;
    }

    public byte[] getUltimoEnderecoTupla() {
        return ultimoEnderecoTupla;
    }

    public void setUltimoEnderecoTupla(byte[] ultimoEnderecoTupla) {
        this.ultimoEnderecoTupla = ultimoEnderecoTupla;
    }
}
