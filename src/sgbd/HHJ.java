package sgbd;

import custom.Pair;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.*;
import java.util.stream.Collectors;

import static utils.BlocoUtils.getDadosByIndexColuna;
import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.ConversorUtils.*;

public class HHJ {

    private GerenciadorArquivos ga = new GerenciadorArquivos();
    private GerenciadorBuffer gb = new GerenciadorBuffer(ga);
    private HashMap<Integer, LinkedList<BlocoDado>> memoria = new HashMap<>();
    private ArrayList<String> resultJoin;

    public HHJ() {
    }

    public ArrayList<String> getJoinResult(
            Pair<Integer, Integer> tabelas,
            Pair<Integer, Integer> indexAtributoJuncao,
            Pair<String, String> tiposColunas
    ) {
        resultJoin = new ArrayList<>();

        int idTabelaDisco;
        int tamanhoBucketsDisco;
        BlocoControle controle1 = ga.carregarBlocoControle(tabelas.first, false);
        BlocoControle controle2 = ga.carregarBlocoControle(tabelas.second, false);

        if (getIntFromBytes(controle1.getProximoBloco()) < getIntFromBytes(controle2.getProximoBloco())) {
            setupHeadersControle(controle1, controle2, indexAtributoJuncao.first, indexAtributoJuncao.second);
            tamanhoBucketsDisco = getIntFromBytes(controle1.getProximoBloco());
            idTabelaDisco = tabelas.second;
            ga.criarListaBuckets(tabelas.second, tamanhoBucketsDisco);
            gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, getIntFromBytes(controle1.getProximoBloco()), true);
            gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, getIntFromBytes(controle1.getProximoBloco()), false);
            probe(idTabelaDisco, tamanhoBucketsDisco, indexAtributoJuncao.first, indexAtributoJuncao.second, controle1, controle2);

        } else {
            setupHeadersControle(controle2, controle1, indexAtributoJuncao.second, indexAtributoJuncao.first);
            tamanhoBucketsDisco = getIntFromBytes(controle2.getProximoBloco());
            idTabelaDisco = tabelas.first;
            ga.criarListaBuckets(tabelas.first, tamanhoBucketsDisco);
            gerarBuckets(controle2, indexAtributoJuncao.second, tiposColunas.second, getIntFromBytes(controle2.getProximoBloco()), true);
            gerarBuckets(controle1, indexAtributoJuncao.first, tiposColunas.first, getIntFromBytes(controle2.getProximoBloco()), false);
            probe(idTabelaDisco, tamanhoBucketsDisco, indexAtributoJuncao.second, indexAtributoJuncao.first, controle2, controle1);

        }

        return resultJoin;

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
                        dado.setIdBucket(intToArrayByte(idBucket, 2));
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
                            aux.setIdBucket(intToArrayByte(idBucket, 2));
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
                            ga.adicionarBlocoNoBucket(idTabela, ultimo, indexUltimoBlocoDoBucket);

                        } else {
                            // "ultimo" agora será o "penultimo"
                            BlocoDado dado = new BlocoDado(idTabela);
                            dado.setIdBucket(intToArrayByte(idBucket, 2));
                            dado.adicionarNovaTupla(tuplaCompleta);

                            int indexNovoUltimoBlocoDoBucket = (int) ga.adicionarBlocoNoBucket(idTabela, dado);

                            ultimo.setProximoBlocoBucket( intToArrayByte(indexNovoUltimoBlocoDoBucket, 4) );
                            ga.adicionarBlocoNoBucket(idTabela, ultimo, indexUltimoBlocoDoBucket); // devolve o penultimo pro disco
                            controlebucket.updateBucket(idBucket, null, indexNovoUltimoBlocoDoBucket); // adiciona o novo ultimo no disco
                        }

                    } else {
                        BlocoDado dado = new BlocoDado(idTabela);
                        dado.setIdBucket(intToArrayByte(idBucket, 2));
                        dado.adicionarNovaTupla(tuplaCompleta);
                        long index = ga.adicionarBlocoNoBucket(idTabela, dado);

                        controlebucket.updateBucket(idBucket, (int) index, null);
                    }

                }

            }

        }

        if(!isInMemory)
            ga.atualizarBlocoControleBucket(idTabela, controlebucket);

    }

    private void probe(int idTabelaDisco, int tamanhoBuckets, int indexAtributoJuncao1, int indexAtributoJuncao2, BlocoControle controleMemoria, BlocoControle controleDisco){
        BlocoControle controlebucket = ga.carregarBlocoControle(idTabelaDisco, true);

        for (byte[] bucket : controlebucket.getListaBuckets(tamanhoBuckets)) {
            int idBucket = getShortFromBytes(new byte[]{ bucket[0], bucket[1] });
            int primeiroBloco = getIntFromBytes(new byte[]{ bucket[2], bucket[3], bucket[4], bucket[5] });
            int ultimoBloco = getIntFromBytes(new byte[]{ bucket[6], bucket[7], bucket[8], bucket[9] });

            LinkedList<BlocoDado> blocosMemoria = memoria.get(idBucket);

            if( blocosMemoria == null || blocosMemoria.get(0) == null || getIntFrom3Bytes(blocosMemoria.get(0).getIdBloco()) == 6547)
                System.out.println();

            for (BlocoDado bMemoria : blocosMemoria) {

                BlocoDado bDisco = ga.getBlocoFromBucket(idTabelaDisco, primeiroBloco);
                int proximoBloco = 0;

                while (proximoBloco != ultimoBloco) {

                    for (byte[] tuplaMemoria : bMemoria.getListaTuplas()) {
                        byte[] dadosColunaMemoria = getDadosByIndexColuna(tuplaMemoria, indexAtributoJuncao1);

                        for (byte[] tuplaDisco : bDisco.getListaTuplas()) {

                            byte[] dadosColunaDisco = getDadosByIndexColuna(tuplaDisco, indexAtributoJuncao2);

                            if (Arrays.equals(dadosColunaMemoria, dadosColunaDisco)) {
                                String s = "";
                                String[] tupla1 = bMemoria.tuplaToString(tuplaMemoria, controleMemoria).split("\\|");
                                String[] tupla2 = bDisco.tuplaToString(tuplaDisco, controleDisco).split("\\|");

                                for (int i = 0; i < tupla1.length; i++) {

                                    if(i != indexAtributoJuncao1){
                                        s += tupla1[i] + "|";
                                    }

                                }

                                for (int i = 0; i < tupla2.length; i++) {

                                    if(i != indexAtributoJuncao2){
                                        s += tupla2[i] + "|";
                                    }

                                }

                                resultJoin.add(s);
                            }

                        }

                    }

                    proximoBloco = bDisco.getProximoBlocoBucketAsInt();
                    bDisco = ga.getBlocoFromBucket(idTabelaDisco, proximoBloco);

                }
            }

            resultJoin = (ArrayList<String>) resultJoin.stream().distinct().collect(Collectors.toList());
        }

    }

    private void setupHeadersControle(BlocoControle memoria, BlocoControle disco, int indexJuncaoMemoria, int indexJuncaoDisco){
        ArrayList<String> colunas1 = memoria.getColunas();
        colunas1.remove(indexJuncaoMemoria);

        ArrayList<String> colunas2 = disco.getColunas();
        colunas2.remove(indexJuncaoDisco);

        String result = "";

        for (String s : colunas1) {
            result += s + "|";
        }

        for (String s : colunas2) {
            result += s + "|";
        }

        resultJoin.add(result);
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
