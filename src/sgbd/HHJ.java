package sgbd;

import custom.Pair;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static utils.BlocoUtils.getDadosByIndexColuna;
import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.ConversorUtils.*;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);
    private HashMap<Integer, LinkedList<BlocoDado>> teste = new HashMap<>();

    public HHJ() {}

    public void init(
            Pair<Integer, Integer> tabelas,
            Pair<Integer, Integer> indexAtributoJuncao,
            Pair<String, String> tiposColunas
    ){

        BlocoControle controle1 = ga.carregarBlocoControle(tabelas.first);
        BlocoControle controle2 = ga.carregarBlocoControle(tabelas.second);

        if(getIntFromBytes(controle1.getProximoBloco()) < getIntFromBytes(controle2.getProximoBloco())){
            ga.criarListaBuckets(tabelas.second);
            gerarBuckets(tabelas.first, getIntFromBytes(controle1.getProximoBloco()), indexAtributoJuncao.first, tiposColunas.first, true);
            gerarBuckets(tabelas.second, getIntFromBytes(controle2.getProximoBloco()), indexAtributoJuncao.second, tiposColunas.second, false);

        } else {
            ga.criarListaBuckets(tabelas.first);
            gerarBuckets(tabelas.first, getIntFromBytes(controle1.getProximoBloco()), indexAtributoJuncao.first, tiposColunas.first, false);
            gerarBuckets(tabelas.second, getIntFromBytes(controle2.getProximoBloco()), indexAtributoJuncao.second, tiposColunas.second, true);

        }

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
    private void gerarBuckets(int idTabela, int proximoBlocoLivre, int indexAtributoJuncao, String tipoColuna, boolean isInMemory){

        for (int i = 0; i < proximoBlocoLivre; i++) {

            BlocoDado blocoDisco = new BlocoDado(idTabela);
            BlocoDado bloco = gb.getBloco(new RowID(idTabela + "-" + i));
            ArrayList<byte[]> tuplas = bloco.getListaTuplas();

            for (byte[] tupla : tuplas) {
                byte[] tuplaCompleta = new byte[tupla.length + 4];
                byte[] tamanhoTupla = intToArrayByte(tupla.length, 4);

                System.arraycopy(tamanhoTupla, 0, tuplaCompleta, 0, tamanhoTupla.length);
                System.arraycopy(tupla, 0, tuplaCompleta, 4, tupla.length);

                byte[] dadosColuna = getDadosByIndexColuna(tupla, indexAtributoJuncao);
                int idBucket = -1;

                if(tipoColuna.equals("A")) {
                    idBucket = hashString(bytesToString(dadosColuna), proximoBlocoLivre - 1);
                } else {
                    idBucket = hashInt(getIntFromBytes(dadosColuna), proximoBlocoLivre - 1);
                }

                    if(isInMemory){
                        // Criando buckets em memória para a menor tabela

                        if(!teste.containsKey(idBucket)){

                            BlocoDado dado = new BlocoDado(idTabela);
                            dado.adicionarNovaTupla(tuplaCompleta);
                            LinkedList<BlocoDado> list = new LinkedList<>();
                            list.addFirst(dado);
                            teste.put(idBucket, list);

                        } else {
                            // pega a lista daquele idBucket
                            LinkedList<BlocoDado> list = teste.get(idBucket);

                            // pega ultimo elemento da lista
                            BlocoDado dado = list.getLast();

                            if(temEspacoParaNovaTupla(dado.getTamanhoTuplasDisponivel(), tuplaCompleta))
                                dado.adicionarNovaTupla(tuplaCompleta);
                            else{
                                BlocoDado aux = new BlocoDado(idTabela);
                                aux.adicionarNovaTupla(tuplaCompleta);
                                list.addLast(aux);
                            }


                        }

                    } else {
                        // Criando buckets em disco para a maior tabela

                        // TODO


                    }



            }

        }

        System.out.println();

    }

    private int hashInt(int k, int n) {
        return k % n;
    }

    private int hashString(String s, int n) {
        int hash = 0;

        for (int i = 0; i < s.length(); i++)
            hash = (31 * hash + s.charAt(i)) % n;

        return hash;
    }

}
