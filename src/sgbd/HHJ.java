package sgbd;

import sgbd.bloco.BlocoDado;
import utils.BlocoUtils;

import java.util.ArrayList;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);

    public HHJ() {}

    public void init(){
        // gerar buckets (usando a função hash)
        //
    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela){
         return ga.carregarBlocoControle(idTabela).getColunas();
    }

    public void gerarBuckets(int idTabela, int proximoBlocoLivre, int indexAtributoJuncao){
        ArrayList<BlocoDado> buckets = new ArrayList<>();

        for (int i = 0; i < proximoBlocoLivre; i++) {
            String blocID = idTabela + "-" + i;
            BlocoDado bloco = gb.getBloco(new RowID(blocID));
            ArrayList<byte[]> tuplas = bloco.getListaTuplas();

            for (byte[] tupla : tuplas) {
                BlocoUtils.getDadosByIndexColuna(tupla, indexAtributoJuncao);
                System.out.println(tupla);
            }
        }
    }

    private int hash(int a) {
        a ^= (a << 13);
        a ^= (a >>> 17);
        a ^= (a << 5);
        return a;
    }

}
