package sgbd;

import sgbd.bloco.BlocoDado;
import utils.BlocoUtils;

import java.util.ArrayList;

import static utils.BlocoUtils.getDadosByIndexColuna;
import static utils.ConversorUtils.getIntFromBytes;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);
    private BlocoDado[] memoriaTabela1;
    private BlocoDado[] memoriaTabela2;

    public HHJ() {}

    public void init(){

    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela){
         return ga.carregarBlocoControle(idTabela).getColunas();
    }

    /**
     * Método que lê os blocos de uma relação e gera os bucketes, colocando tuplas correspondentes no mesmo bucket.
     * @param idTabela tabela da relação que será gerado os buckets.
     * @param proximoBlocoLivre simula o último byte da relação que está sendo usada
     * @param  indexAtributoJuncao index da coluna que será aplicada a função hash para gerar os buckets
     * @param tipoColuna podendo ser "I" (int) ou "A" (string), usada para saber qual função hash utilizar
     * */
    public void gerarBuckets(int idTabela, int proximoBlocoLivre, int indexAtributoJuncao, String tipoColuna){

        for (int i = 0; i < proximoBlocoLivre; i++) {
            String blocID = idTabela + "-" + i;
            BlocoDado bloco = gb.getBloco(new RowID(blocID));
            ArrayList<byte[]> tuplas = bloco.getListaTuplas();

            for (byte[] tupla : tuplas) {
                byte[] dadosColuna = getDadosByIndexColuna(tupla, indexAtributoJuncao);

                if(tipoColuna.equals("I")){
                    int idBucket = hashInt(getIntFromBytes(dadosColuna));
                    // se não estivar no Bloco de Controle, setar no header dele
                    // setar
                }

            }
        }
    }

    private int hashInt(int k) {
        int n = 10; // TODO: n = quantidade buckets
        return k % n;
    }

}
