package sgbd;

import enums.TipoArquivo;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;
import utils.FileUtils;
import utils.PrintUtils;
import utils.RAFUtils;

import java.io.*;
import java.util.ArrayList;

import static constants.ConstantesSGBD.TAMANHO_BLOCO;
import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.ConversorUtils.getIntFrom3Bytes;
import static utils.ConversorUtils.getIntFromBytes;
import static utils.DiretorioUtils.getQuantidadeArquivosSaidaTabelas;
import static utils.FileUtils.buscarTabela;
import static utils.FileUtils.criarArquivo;
import static utils.RAFUtils.*;

public class GerenciadorArquivos {

    private int containerID;

    public GerenciadorArquivos() { }

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
                    PrintUtils.printAdditionalInformation("Adicionando nova tupla no Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);

                } else {
                    blocoDadoAtual = new BlocoDado(getContainerID());
                    PrintUtils.printLoadingInformation("Criando Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    PrintUtils.printAdditionalInformation("Adicionando nova tupla no Bloco de Dados " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);
                    dados.add(blocoDadoAtual);
                    controle.setProximoBloco(getIntFrom3Bytes(blocoDadoAtual.getIdBloco()) + 1);
                }

            }

            // Escrevendo Bloco de Controle
            offset = controle.getInformacoesCompletas().length;
            escreverArquivo(saida, controle.getInformacoesCompletas(), 0);

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

    public void lerTabela(int tabelaID) {
        ArrayList<String> rowIDs = new ArrayList<>();

        if (tabelaID < 1 || tabelaID > getQuantidadeArquivosSaidaTabelas()) {
            PrintUtils.printError("A tabela" + tabelaID + ".txt não existe.");
        }

        File file = buscarTabela(tabelaID);

        // carregar bloco de controle e de dados do file
        BlocoControle controle = carregarBlocoControle(file);
        ArrayList<BlocoDado> dados = carregarBlocosDados(file, controle.getInformacoesCompletas().length, getIntFromBytes(controle.getProximoBloco()) - 1);

        PrintUtils.printAdditionalInformation("Quantidade de Bloco de Dados = " + dados.size());
        PrintUtils.printResultData(controle.toString());

        for (BlocoDado d : dados) {
            PrintUtils.printResultData(d.toString(controle));
            rowIDs.addAll(d.getRowIDs());
        }

        //Collections.shuffle(rowIDs);

        /*System.out.println("Começando leitura dos RowIDs");
        for (String rowID : rowIDs) {
            System.out.println(rowID);
        }*/

        escreverRowIDs(tabelaID, rowIDs);
    }

    public BlocoDado buscarBloco(int idTabela, int idBloco){
        File file = buscarTabela(idTabela);

        BlocoControle controle = new BlocoControle(lerBlocoControle(file));

        return new BlocoDado(RAFUtils.lerDadosArquivo(file, controle.getTamanhoTotal() + (idBloco*TAMANHO_BLOCO), TAMANHO_BLOCO));
    }

    private void escreverRowIDs(int idTabela, ArrayList<String> rowIDs) {
        File file = criarArquivo(idTabela, TipoArquivo.ROW_IDS);

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {

            if (file != null) {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);

                for (String rowID : rowIDs) {
                    bw.write(rowID);
                    bw.newLine();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (bw != null) {
                    bw.close();
                    fw.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private BlocoControle carregarBlocoControle(File file) {
        PrintUtils.printLoadingInformation("Carregando Bloco de Controle da " + file.getName() + "...");
        byte[] dadosControle = lerBlocoControle(file);

        return new BlocoControle(dadosControle);
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
        PrintUtils.printLoadingInformation("Iniciando Gerenciador de Arquivos...\n");
    }

}
