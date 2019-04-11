package sgbd;

import sgbd.bloco.BlocoDado;
import utils.FileUtils;
import utils.PrintUtils;
import utils.SwapUtils;

import java.io.File;
import java.util.ArrayList;

import static constants.ConstantesSGBD.TAMANHO_MEMORIA;
import static enums.TipoArquivo.LOG_BUFFER;
import static utils.ConversorUtils.getIntFrom3Bytes;

public class GerenciadorBuffer {

    private int miss;
    private int hit;
    private GerenciadorArquivos ga;
    private BlocoDado[] memoria;
    private RowID[] LRU;
    private File log;

    public GerenciadorBuffer() {
        PrintUtils.printLoadingInformation("Iniciando o Log do Gerenciador de Buffer...\n");
        log = new File(LOG_BUFFER.path + ".txt");
    }

    public GerenciadorBuffer(GerenciadorArquivos ga){
        this.ga = ga;
        this.memoria = new BlocoDado[TAMANHO_MEMORIA];
        this.LRU = new RowID[TAMANHO_MEMORIA];
    }

    public BlocoDado getBloco(RowID id){
        int posicaoBlocoMemoria = getPosicaoBlocoEmMemoria(id);

        if (posicaoBlocoMemoria > -1) {

            hit++;
            atualizarPosicoesLRU(posicaoBlocoMemoria);

            return memoria[posicaoBlocoMemoria];

        } else {

            miss++;
            BlocoDado bloco = getBlocoEmDisco(id.getIdFileAsInt(), id.getIdBlocoAsInt());

            if (isMemoriaFull()) {
                removerLRU();
                adicionarNaMemoria(bloco);
                adicionarNaLRU(id);

            } else {
                adicionarNaMemoria(bloco);
                adicionarNaLRU(id);
            }

            return bloco;

        }
    }

    // Apenas para testar o Buffer
    public void init(ArrayList<String> rowIDs, GerenciadorArquivos ga) {
        ArrayList<String> logBuffer = new ArrayList<>();
        this.ga = ga;
        this.memoria = new BlocoDado[TAMANHO_MEMORIA];
        this.LRU = new RowID[TAMANHO_MEMORIA];
        int countLoading = 0;

        // Simulando os Pages Requests
        for (String id : rowIDs) {
            countLoading++;
            double percent = (double) countLoading / rowIDs.size();

            if (percent == 0.1 || percent == 0.25 || percent == 0.50 || percent == 0.75 || percent == 1.0)
                PrintUtils.printLoadingInformation(percent * 100 + "% das Requisições foram finalizadas...");

            RowID rowID = new RowID(id);
            logBuffer.add(">> Iniciando novo Page Request - PageID: " + rowID.getIdFileAsInt() + "-" + rowID.getIdBlocoAsInt());
            int posicaoBlocoMemoria = getPosicaoBlocoEmMemoria(rowID);

            if (posicaoBlocoMemoria > -1) {

                hit++;
                logBuffer.add("HIT - Pagina em memoria, iniciando swap...");
                atualizarPosicoesLRU(posicaoBlocoMemoria);

            } else {

                miss++;
                logBuffer.add("MISS - Iniciando recuperacao do Bloco em disco...");
                BlocoDado bloco = getBlocoEmDisco(rowID.getIdFileAsInt(), rowID.getIdBlocoAsInt());

                if (isMemoriaFull()) {
                    logBuffer.add("Memoria cheia, iniciando retirada do LRU...");
                    removerLRU();
                    adicionarNaMemoria(bloco);
                    adicionarNaLRU(rowID);
                    logBuffer.add("Nova pagina adicionada em memoria e LRU devolvido ao disco...");

                } else {
                    logBuffer.add("Memoria possui espaco disponivel, iniciando alocacao...");
                    adicionarNaMemoria(bloco);
                    adicionarNaLRU(rowID);
                    logBuffer.add("Nova pagina adicionada em memoria...");
                }

            }

            logBuffer.add("");

        }

        logBuffer.add(0,
                "Tamanho da memoria: " + TAMANHO_MEMORIA + "\n" +
                        "Quantidade de Pages Requests: " + rowIDs.size() + "\n" +
                        "Quantidade de HIT: " + hit + "\n" +
                        "Quantidade de MISS: " + miss + "\n" +
                        "Taxa de hit: " + getTaxaHit() +
                        "\n------------\n");

        FileUtils.escreverEmArquivo(log, logBuffer);

    }

    /**
     * Coloca PageID de indice posicaoAtualMemoria no começo do array da LRU
     */
    private void atualizarPosicoesLRU(int posicaoAtualMemoria) {
        SwapUtils.swap(LRU, 0, posicaoAtualMemoria);
    }

    /**
     * Remove elemento menos utilizado. Quando o elemento menos utilizado sai, o Bloco com o PageID dele sai da memória também
     */
    private void removerLRU() {
        RowID id = LRU[LRU.length - 1];
        LRU[LRU.length - 1] = null;
        removerBlocoEmMemoria(id);
    }

    private void removerBlocoEmMemoria(RowID id) {


        for (int i = 0; i < memoria.length; i++) {
            BlocoDado bloco = memoria[i];

            if (bloco.getIdArquivo() == id.getIdFile() && getIntFrom3Bytes(bloco.getIdBloco()) == id.getIdBlocoAsInt()) {
                ga.devolverBlocoAoDisco(id.getIdFileAsInt(), id.getIdBlocoAsInt(), memoria[i]);
                memoria[i] = null;
                break;
            }

        }

    }

    /**
     * Adiciona novo elemento na esquerda do array LRU e shifitando para direita os outros elementos
     */
    private void adicionarNaLRU(RowID novoPageID) {
        RowID[] novoArray = new RowID[LRU.length];
        int countArray = 0;

        novoArray[0] = novoPageID;

        for (int i = 1; i < novoArray.length; i++) {

            novoArray[i] = LRU[countArray];
            countArray++;

        }

        LRU = novoArray;

    }

    /**
     * Adiciona elemento na memória
     */
    private void adicionarNaMemoria(BlocoDado novoBloco) {

        for (int i = 0; i < memoria.length; i++) {

            if (memoria[i] == null) {
                memoria[i] = novoBloco;
                return;
            }

        }

    }

    private int getPosicaoBlocoEmMemoria(RowID id) {
        int result = -1;

        for (int i = 0; i < memoria.length; i++) {
            BlocoDado bloco = memoria[i];

            if (bloco != null) {

                if (bloco.getIdArquivo() == id.getIdFile() && getIntFrom3Bytes(bloco.getIdBloco()) == id.getIdBlocoAsInt()) {
                    result = i;
                    return result;
                }

            }

        }


        return result;
    }

    private boolean isMemoriaFull() {

        for (BlocoDado bloco : memoria) {
            if (bloco == null)
                return false;
        }

        return true;

    }

    private BlocoDado getBlocoEmDisco(int idFile, int idBloco) {
        return ga.buscarBloco(idFile, idBloco);
    }

    private double getTaxaHit() {
        return (double) hit / (hit + miss);
    }

}
