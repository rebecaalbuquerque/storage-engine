package sgbd;

import java.util.ArrayList;

public class HHJ {

    private GerenciadorBuffer gb = new GerenciadorBuffer();
    private GerenciadorArquivos ga = new GerenciadorArquivos();

    public HHJ() {}

    public void carregarTabela(){

    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela){
        return ga.carregarBlocoControle(idTabela).getColunas();
    }

}
