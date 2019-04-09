package sgbd;

import enums.TipoArquivo;

import java.util.ArrayList;

import static utils.FileUtils.criarArquivo;
import static utils.FileUtils.getDadosArquivo;

public class HHJ {

    private GerenciadorBuffer gb = new GerenciadorBuffer();
    private GerenciadorArquivos ga = new GerenciadorArquivos();

    public HHJ() {}

    public void init(){
        gerarTabelas();
        // gerar buckets (usando a função hash)
        //
    }

    public void gerarTabelas(){
        ArrayList<String> rowIds = getDadosArquivo(criarArquivo(-1, TipoArquivo.ROW_IDS));

        for (String id : rowIds) {

        }

    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela){
        return ga.carregarBlocoControle(idTabela).getColunas();
    }

}
