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
        int idTabelaDisco;
        BlocoControle controle1 = ga.carregarBlocoControle(tabelas.first, false);
        BlocoControle controle2 = ga.carregarBlocoControle(tabelas.second, false);

        if (getIntFromBytes(controle1.getProximoBloco()) < getIntFromBytes(controle2.getProximoBloco())) {
            //ga.criarListaBuckets(tabelas.second);
            idTabelaDisco = tabelas.second;
            gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, getIntFromBytes(controle1.getProximoBloco()), true);
            //gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, getIntFromBytes(controle1.getProximoBloco()), false);

        } else {
            //ga.criarListaBuckets(tabelas.first);
            idTabelaDisco = tabelas.first;
            gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, getIntFromBytes(controle2.getProximoBloco()), true);
            //gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, getIntFromBytes(controle2.getProximoBloco()), false);

        }

        probe(idTabelaDisco);

    }

    public ArrayList<String> buscarColunasTabelaById(int idTabela) {
        return ga.carregarBlocoControle(idTabela, false).getColunas();
    }

    /**
     * Método que lê os blocos de uma relação e gera os bucketes, colocando tuplas correspondentes no mesmo bucket.
     * @param indexAtributoJuncao index da coluna que será aplicada a função hash para gerar os buckets
     * @param tipoColuna          podendo ser "I" (int) ou "A" (string), usada para saber qual função hash utilizar
     */
    private void gerarBuckets(BlocoControle controle, int indexAtributoJuncao, String tipoColuna, int quantidadeBuckets, boolean isInMemory) {
        int idTabela = (int) controle.getIdArquivo();
        int proximoBlocoLivre = getIntFromBytes(controle.getProximoBloco());
        BlocoControle controlebucket = null;

        if(!isInMemory)
            controlebucket = ga.carregarBlocoControle(idTabela, true);

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
                    idBucket = hashString(bytesToString(dadosColuna), quantidadeBuckets);
                else
                    idBucket = hashInt(getIntFromBytes(dadosColuna), quantidadeBuckets);


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

                    if(controlebucket.hasBucket(idBucket)) {

                        int indexUltimoBlocoDoBucket = controlebucket.getUltimoBlocoDoBucket(idBucket);
                        BlocoDado ultimo = ga.getBlocoFromBucket(idTabela, indexUltimoBlocoDoBucket);

                        if(ultimo.temEspacoParaNovaTupla(tuplaCompleta)) {
                            ultimo.adicionarNovaTupla(tuplaCompleta);
                            ga.adicionarBucket(idTabela, ultimo, indexUltimoBlocoDoBucket);

                        } else {
                            // "ultimo" agora será o "penultimo"
                            BlocoDado dado = new BlocoDado(idTabela);
                            dado.adicionarNovaTupla(tuplaCompleta);
                            ga.adicionarBucket(idTabela, ultimo, indexUltimoBlocoDoBucket); // devolve o penultimo
                            ga.adicionarBucket(idTabela, dado);
                            controlebucket.atualizarUltimoBucket(idBucket, indexUltimoBlocoDoBucket + TAMANHO_BLOCO); // adiciona o novo ultimo
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

        if(!isInMemory)
            ga.atualizarBlocoControleBucket(idTabela, controlebucket);

    }

    private void probe(int idTabelaDisco){
        BlocoControle controlebucket = ga.carregarBlocoControle(idTabelaDisco, true);

        for (int[] buckets : controlebucket.getListaBuckets()) {
            LinkedList<BlocoDado> blocoMemor = memoria.get(buckets[0]);
            BlocoDado blocoDisco = ga.getBlocoFromBucket(idTabelaDisco, buckets[1]);
            System.out.println();
        }

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
