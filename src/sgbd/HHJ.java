package sgbd;

import custom.Pair;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.*;

import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.BlocoUtils.getDadosByIndexColuna;
import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.ConversorUtils.*;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);
    private HashMap<Integer, LinkedList<BlocoDado>> memoria = new HashMap<>();

    public HHJ() {
    }

    public void init(
            Pair<Integer, Integer> tabelas,
            Pair<Integer, Integer> indexAtributoJuncao,
            Pair<String, String> tiposColunas
    ) {

        BlocoControle controle1 = ga.carregarBlocoControle(tabelas.first, false);
        BlocoControle controle2 = ga.carregarBlocoControle(tabelas.second, false);

        if (getIntFromBytes(controle1.getProximoBloco()) < getIntFromBytes(controle2.getProximoBloco())) {
            ga.criarListaBuckets(tabelas.second);
            gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, true);
            gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, false);

        } else {
            ga.criarListaBuckets(tabelas.first);
            gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, true);
            gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, false);

        }

    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela) {
        return ga.carregarBlocoControle(idTabela, false).getColunas();
    }

    /**
     * Método que lê os blocos de uma relação e gera os bucketes, colocando tuplas correspondentes no mesmo bucket.
     * @param indexAtributoJuncao index da coluna que será aplicada a função hash para gerar os buckets
     * @param tipoColuna          podendo ser "I" (int) ou "A" (string), usada para saber qual função hash utilizar
     */
    private void gerarBuckets(BlocoControle controle, int indexAtributoJuncao, String tipoColuna, boolean isInMemory) {
        int idTabela = (int) controle.getIdArquivo();
        int proximoBlocoLivre = getIntFromBytes(controle.getProximoBloco());

        for (int i = 0; i < proximoBlocoLivre; i++) {

            BlocoDado bloco = gb.getBloco(new RowID(idTabela + "-" + i));

            for (byte[] tupla : bloco.getListaTuplas()) {
                byte[] tuplaCompleta = new byte[tupla.length + 4];
                byte[] tamanhoTupla = intToArrayByte(tupla.length, 4);

                System.arraycopy(tamanhoTupla, 0, tuplaCompleta, 0, tamanhoTupla.length);
                System.arraycopy(tupla, 0, tuplaCompleta, 4, tupla.length);

                byte[] dadosColuna = getDadosByIndexColuna(tupla, indexAtributoJuncao);
                int idBucket;

                if (tipoColuna.equals("A"))
                    idBucket = hashString(bytesToString(dadosColuna), proximoBlocoLivre);
                else
                    idBucket = hashInt(getIntFromBytes(dadosColuna), proximoBlocoLivre);


                if (isInMemory) {
                    // Criando buckets em memória para a menor tabela

                    if (!memoria.containsKey(idBucket)) {

                        BlocoDado dado = new BlocoDado(idTabela);
                        dado.adicionarNovaTupla(tuplaCompleta);
                        LinkedList<BlocoDado> list = new LinkedList<>();
                        list.addFirst(dado);
                        memoria.put(idBucket, list);

                    } else {
                        // pega a lista daquele idBucket
                        LinkedList<BlocoDado> list = memoria.get(idBucket);

                        // pega ultimo elemento da lista
                        BlocoDado dado = list.getLast();

                        if (temEspacoParaNovaTupla(dado.getTamanhoTuplasDisponivel(), tuplaCompleta))
                            dado.adicionarNovaTupla(tuplaCompleta);
                        else {
                            BlocoDado aux = new BlocoDado(idTabela);
                            aux.adicionarNovaTupla(tuplaCompleta);
                            list.addLast(aux);
                        }


                    }

                } else {
                    // Criando buckets em disco para a maior tabela
                    // TODO: rever logica
                    BlocoControle controlebucket = ga.carregarBlocoControle(idTabela, true);

                    if(controlebucket.hasBucket(idBucket)) {

                        long indexUltimoBlocoDoBucket = controlebucket.getUltimoBlocoDoBucket(idBucket);
                        BlocoDado ultimo = ga.getBlocoFromBucket(idTabela, indexUltimoBlocoDoBucket);

                        if(temEspacoParaNovaTupla(ultimo.getTamanhoTuplasDisponivel(), tuplaCompleta)){
                            ultimo.adicionarNovaTupla(tuplaCompleta);
                            ga.adicionarBucket(idTabela, ultimo, (int) indexUltimoBlocoDoBucket);

                        } else {
                            // "ultimo" agora será o "penultimo"
                            BlocoDado dado = new BlocoDado(idTabela);
                            dado.adicionarNovaTupla(tuplaCompleta);
                            ga.adicionarBucket(idTabela, ultimo, (int) indexUltimoBlocoDoBucket); // devolve o penultimo
                            ga.adicionarBucket(idTabela, dado);
                            controlebucket.atualizarUltimoBucket(idBucket, (int) indexUltimoBlocoDoBucket + TAMANHO_BLOCO); // adiciona o novo ultimo
                        }

                    } else {
                        BlocoDado dado = new BlocoDado(idTabela);
                        dado.adicionarNovaTupla(tuplaCompleta);
                        long index = ga.adicionarBucket(idTabela, dado);
                        controlebucket.adicionarBucket(idBucket, index, index);
                    }

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
