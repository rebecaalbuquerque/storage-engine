package sgbd;

import custom.Pair;
import sgbd.bloco.BlocoDado;

import java.util.ArrayList;

import static utils.BlocoUtils.getDadosByIndexColuna;
import static utils.ConversorUtils.getIntFromBytes;
import static utils.ConversorUtils.intToArrayByte;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);
    private BlocoDado[] memoriaTabela1;
    private BlocoDado[] memoriaTabela2;

    public HHJ() {}

    public void init(
            int tamanhoMemoria,
            Pair<Integer, Integer> tabelas,
            Pair<Integer, Integer> indexAtributoJuncao,
            Pair<String, String> tiposColunas
    ){
        this.memoriaTabela1 = new BlocoDado[tamanhoMemoria/2];
        this.memoriaTabela2 = new BlocoDado[tamanhoMemoria/2];

        gerarBuckets(tabelas.first, 366, indexAtributoJuncao.first, tiposColunas.first);
        //gerarBuckets(tabelas.second, 6290, indexAtributoJuncao.second, tiposColunas.second);

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
    private void gerarBuckets(int idTabela, int proximoBlocoLivre, int indexAtributoJuncao, String tipoColuna){

        for (int i = 0; i < proximoBlocoLivre; i++) {

            String blocID = idTabela + "-" + i;
            BlocoDado bloco = gb.getBloco(new RowID(blocID));
            ArrayList<byte[]> tuplas = bloco.getListaTuplas();

            for (byte[] tupla : tuplas) {
                byte[] tuplaCompleta = new byte[tupla.length + 4];
                byte[] tamanhoTupla = intToArrayByte(tupla.length, 4);

                System.arraycopy(tamanhoTupla, 0, tuplaCompleta, 0, tamanhoTupla.length);
                System.arraycopy(tupla, 0, tuplaCompleta, 4, tupla.length);

                byte[] dadosColuna = getDadosByIndexColuna(tupla, indexAtributoJuncao);

                if(tipoColuna.equals("I")){
                    int idBucket = hashInt(getIntFromBytes(dadosColuna));


                }

            }
        }

        System.out.println();

    }

    private int hashInt(int k) {
        int n = 10; // TODO: n = quantidade buckets
        return k % n;
    }

}
