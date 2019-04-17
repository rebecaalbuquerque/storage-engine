package sgbd;

import enums.TipoArquivo;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;
import utils.FileUtils;
import utils.PrintUtils;
import utils.RAFUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.ConversorUtils.getIntFrom3Bytes;
import static utils.ConversorUtils.getIntFromBytes;
import static utils.DiretorioUtils.getQuantidadeArquivosSaidaTabelas;
import static utils.FileUtils.*;
import static utils.ShuffleUtils.shuffleWithRepetition;
import static utils.PrintUtils.*;
import static utils.RAFUtils.*;

public class GerenciadorArquivos {

    private int containerID;
    private ArrayList<String> rowIDs = new ArrayList<>();

    public GerenciadorArquivos() {
    }

    public void criarListaBuckets(int idTabela){
        criarArquivo(containerID, TipoArquivo.BUCKET);
    }

    public void adicionarBucket(int idTabela, BlocoDado bucket){
        File bucketFile = FileUtils.buscarBucket(idTabela);
        escreverArquivo(bucketFile, bucket.getInformacoesCompletas());

    }

    public void criarTabela(String arquivoEntrada) {
        containerID = getQuantidadeArquivosSaidaTabelas() + 1;

        int offset;
        String linha;
        File saida = FileUtils.criarArquivo(containerID, TipoArquivo.SAIDA_TABELAS);
        ArrayList<BlocoDado> dados = new ArrayList<>();

        FileReader reader;
        BufferedReader buffer;

        // Lendo arquivo que contem a tabela a ser lida
        try {
            reader = new FileReader(arquivoEntrada);
            buffer = new BufferedReader(reader);

            // Criando Bloco de Controle
            BlocoControle controle = new BlocoControle(containerID, buffer.readLine());

            // Criando primeiro Bloco de Dados
            BlocoDado blocoDadoAtual = new BlocoDado(getContainerID());
            dados.add(blocoDadoAtual);

            PrintUtils.printLoadingInformation("Criando Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));

            while ((linha = buffer.readLine()) != null) {

                if (temEspacoParaNovaTupla(blocoDadoAtual.getTamanhoTuplasDisponivel(), linha)) {
                    printAdditionalInformation("Adicionando nova tupla no Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);

                } else {
                    blocoDadoAtual = new BlocoDado(getContainerID());
                    PrintUtils.printLoadingInformation("Criando Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    printAdditionalInformation("Adicionando nova tupla no Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);
                    dados.add(blocoDadoAtual);
                    controle.setProximoBloco(getIntFrom3Bytes(blocoDadoAtual.getIdBloco()) + 1);
                }

            }

            blocoDadoAtual.resetarId();

            // Escrevendo Bloco de Controle
            offset = controle.getInformacoesCompletas().length;
            escreverArquivo(saida, controle.getInformacoesCompletas(), 0);

            printLoadingInformation("Aguarde...");

            // Escrevendo Bloco de Dados
            for (BlocoDado d : dados) {
                byte[] blocoCompleto = d.getInformacoesCompletas();
                escreverArquivo(saida, blocoCompleto, offset);
                offset += blocoCompleto.length;
            }

            buffer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void lerTabela(int tabelaID, boolean gerarPagesIds) {

        int qtdTotalTuplas = 0;

        if (tabelaID < 1 || tabelaID > getQuantidadeArquivosSaidaTabelas()) {
            printError("A tabela" + tabelaID + ".txt não existe.");
        }

        File file = buscarTabela(tabelaID);

        // carregar bloco de controle e de dados do file
        BlocoControle controle = carregarBlocoControle(file);
        ArrayList<BlocoDado> dados = carregarBlocosDados(file, controle.getInformacoesCompletas().length, getIntFromBytes(controle.getProximoBloco()) - 1);

        printAdditionalInformation("Quantidade de Bloco de Dados = " + dados.size());

        if (gerarPagesIds) {

            for (BlocoDado d : dados) {
                qtdTotalTuplas += d.getQuantidadeTuplas();
                rowIDs.addAll(d.getRowIDs());
            }

        } else {

            printResultData(controle.toString());

            for (BlocoDado d : dados) {
                qtdTotalTuplas += d.getQuantidadeTuplas();
                printResultData(d.toString(controle));
            }

        }

        printAdditionalInformation("Quantidade total de tuplas da Tabela " + tabelaID + " = " + qtdTotalTuplas);
        printAdditionalInformation("Quantidade total de Blocos da Tabela " + tabelaID + " = " + dados.size());


    }

    public void gerarPageIDs() {
        printLoadingInformation("Iniciando geração de PageIDs a partir de todas as tabelas existentes...\n");

        if (getQuantidadeArquivosSaidaTabelas() == 0) {
            printError("Não existe tabela na pasta de saída.");
        }

        for (int i = 1; i <= getQuantidadeArquivosSaidaTabelas(); i++) {
            lerTabela(i, true);
        }

        escreverRowIDs(rowIDs);

        rowIDs = new ArrayList<>();

        printLoadingInformation("PageIDs gerados com sucesso!");

    }

    public BlocoDado buscarBloco(int idTabela, int idBloco) {
        File file = buscarTabela(idTabela);

        BlocoControle controle = new BlocoControle(lerBlocoControle(file));

        return new BlocoDado(RAFUtils.lerDadosArquivo(file, controle.getTamanhoTotal() + (idBloco * TAMANHO_BLOCO), TAMANHO_BLOCO));
    }

    public void devolverBlocoAoDisco(int idTabela, int idBloco, BlocoDado bloco) {
        File file = buscarTabela(idTabela);
        BlocoControle controle = new BlocoControle(lerBlocoControle(file));
        escreverArquivo(file, bloco.getInformacoesCompletas(), controle.getTamanhoTotal() + (idBloco * TAMANHO_BLOCO));
    }

    public void printarBloco(int idTabela, int idBloco) {
        File file = buscarTabela(idTabela);

        BlocoControle controle = new BlocoControle(lerBlocoControle(file));
        printResultData(new BlocoDado(lerDadosArquivo(file, controle.getTamanhoTotal() + (idBloco * TAMANHO_BLOCO), TAMANHO_BLOCO)).toString(controle));
    }

    public BlocoControle carregarBlocoControle(int idTabela) {
        File file = buscarTabela(idTabela);
        return carregarBlocoControle(file);
    }

    private BlocoControle carregarBlocoControle(File file) {
        //PrintUtils.printLoadingInformation("Carregando Bloco de Controle da " + file.getName() + "...");
        byte[] dadosControle = lerBlocoControle(file);

        return new BlocoControle(dadosControle);
    }

    private void escreverRowIDs(ArrayList<String> rowIDs) {
        File file = criarArquivo(-1, TipoArquivo.ROW_IDS);
        File fileShuffled = criarArquivo(-1, TipoArquivo.ROW_IDS_SHUFFLED);

        escreverEmArquivo(file, rowIDs);

        escreverEmArquivo(fileShuffled, shuffleWithRepetition(rowIDs));
    }

    private ArrayList<BlocoDado> carregarBlocosDados(File file, int start, int ultimoBlocoID) {
        // ler desde o primeiro bloco ate o proximo bloco livre - 1
        if (ultimoBlocoID < 0)
            ultimoBlocoID = 0;

        boolean buscarBloco = true;
        ArrayList<BlocoDado> result = new ArrayList<>();

        int offset = start;

        while (buscarBloco) {

            byte[] bytes = lerDadosArquivo(file, offset, TAMANHO_BLOCO);
            offset += bytes.length;
            BlocoDado bloco = new BlocoDado(bytes);
            result.add(bloco);

            if (getIntFrom3Bytes(bloco.getIdBloco()) == ultimoBlocoID) {
                buscarBloco = false;
            }

        }

        return result;
    }

    private int getContainerID() {
        return containerID;
    }

    static {
        printLoadingInformation("Iniciando Gerenciador de Arquivos...\n");
    }

}
