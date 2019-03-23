package sgbd;

import sgbd.bloco.BlocoDado;
import utils.SwapUtils;

import java.util.ArrayList;

import static constants.ConstantesSGBD.TAMANHO_MEMORIA;

public class GerenciadorBuffer {

    private int miss;
    private int hit;
    private GerenciadorArquivos ga;
    private BlocoDado[] memoria;
    private PageID[] LRU;

    public GerenciadorBuffer() { }

    public void init(ArrayList<String> rowIDs, GerenciadorArquivos ga){
        this.ga = ga;
        this.memoria = new BlocoDado[TAMANHO_MEMORIA];
        this.LRU = new PageID[TAMANHO_MEMORIA];

        // Simulando os Pages Requests
        for (String rowID : rowIDs) {
            PageID pageID = new PageID(rowID);
            int posicaoBlocoMemoria = getPosicaoBlocoEmMemoria(pageID);

            if(posicaoBlocoMemoria > -1){

                hit++;
                atualizarPosicoesLRU(posicaoBlocoMemoria);

            } else {

                miss++;

                if(isMemoriaFull()){

                } else {

                }

            }

        }

    }

    /**
     * Coloca PageID de indice posicaoAtualMemoria no começo do array da LRU
     * */
    private void atualizarPosicoesLRU(int posicaoAtualMemoria){
        SwapUtils.swap(LRU, 0, posicaoAtualMemoria);
    }

    /**
     * Remove elemento menos utilizado. Quando o elemento menos utilizado sai, o Bloco com o PageID dele sai da memória também
     * */
    private void removerLRU(){
        PageID id = LRU[LRU.length - 1];
        LRU[LRU.length -1] = null;
        removerBlocoEmMemoria(id);
    }

    private void removerBlocoEmMemoria(PageID id){


        for (int i = 0; i < memoria.length; i++) {
            BlocoDado dado = memoria[i];

            if(dado.getIdArquivo() == id.getIdFile() && dado.getIdBloco() == id.getIdBloco())
                memoria[i] = null;

        }

    }

    /**
     * Adiciona elemento na LRU shifitando para direita
     * */
    private void adicionarNaLRU(){

    }

    /**
     * Adiciona elemento na memória
     * */
    private void adicionarNaMemoria(){

    }

    private int getPosicaoBlocoEmMemoria(PageID id){

        for (int i = 0; i < memoria.length; i++) {
            BlocoDado bloco = memoria[i];

            if(bloco.getIdArquivo() == id.getIdFile() && bloco.getIdBloco() == id.getIdBloco())
                return i;
        }


        return -1;
    }

    private boolean isMemoriaFull(){

        for (BlocoDado bloco : memoria) {
            if(bloco == null)
                return false;
        }

        return true;

    }

    private BlocoDado getBlocoEmDisco(int idFile, int idBloco){
        return ga.buscarBloco(idFile, idBloco);
    }

}
