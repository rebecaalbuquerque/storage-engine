package sgbd;

import sgbd.bloco.BlocoDado;

public class GerenciadorBuffer {

    private int miss;
    private int hit;
    private BlocoDado[] memoria;

    public GerenciadorBuffer() {
    }

    /* UTILITARIOS */
    public void getBloco(){

    }


    /* GETTERS E SETTERS */
    public int getMiss() { return miss; }

    public void setMiss(int miss) { this.miss = miss; }

    public int getHit() { return hit; }

    public void setHit(int hit) { this.hit = hit; }

    public BlocoDado[] getMemoria() { return memoria; }

}