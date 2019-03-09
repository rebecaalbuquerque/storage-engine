package sgbd;

import sgbd.bloco.BlocoDado;

import java.util.ArrayList;

public class GerenciadorBuffer {

    private int miss;
    private int hit;
    private BlocoDado[] memoria;

    public GerenciadorBuffer() {
    }

    public void init(ArrayList<String> rowIDs){

    }

    private void buscarBloco(){
        // se estiver na memoria

        // se nao estiver na memoria (busca no disco)

        // fazer a troca

    }


    /* GETTERS E SETTERS */
    public int getMiss() { return miss; }

    public void setMiss(int miss) { this.miss = miss; }

    public int getHit() { return hit; }

    public void setHit(int hit) { this.hit = hit; }

    public BlocoDado[] getMemoria() { return memoria; }

}
