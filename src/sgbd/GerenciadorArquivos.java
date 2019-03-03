package sgbd;

import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;
import utils.FileUtils;

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
import static utils.FileUtils.buscarArquivo;
import static utils.RAFUtils.*;

public class GerenciadorArquivos {

    private int containerID;

    public GerenciadorArquivos(){
        containerID = getQuantidadeArquivosSaidaTabelas() + 1;
    }

    public void criarTabela(String arquivoEntrada){
        int offset;
        String linha;
        File saida = FileUtils.criarArquivo(containerID);
        ArrayList<BlocoDado> dados = new ArrayList<>();

        FileReader reader;
        BufferedReader buffer;

        // Lendo arquivo teste.txt que contem a tabela a ser lida
        try {
            reader = new FileReader(arquivoEntrada);
            buffer = new BufferedReader(reader);

            // Criando Bloco de Controle
            BlocoControle controle = new BlocoControle(containerID, buffer.readLine());

            // Criando primeiro Bloco de Dados
            BlocoDado blocoDadoAtual = new BlocoDado(getContainerID());
            dados.add(blocoDadoAtual);
            System.out.println("Criando bloco " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));

            while ((linha = buffer.readLine()) != null ){

                if(temEspacoParaNovaTupla(blocoDadoAtual.getTamanhoTuplasDisponivel(), linha)){
                    System.out.println("Adicionando nova tupla no bloco " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);

                } else {
                    blocoDadoAtual = new BlocoDado(getContainerID());
                    System.out.println("Criando bloco " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    System.out.println("Adicionando nova tupla no bloco " + getIntFrom3Bytes(blocoDadoAtual.getIdBloco()));
                    blocoDadoAtual.adicionarNovaTupla(linha);
                    dados.add(blocoDadoAtual);
                    controle.setProximoBloco( getIntFrom3Bytes(blocoDadoAtual.getIdBloco()) + 1 );
                }

            }

            // Escrevendo Bloco de Controle
            offset = controle.getInformacoesCompletas().length;
            escreverArquivo(saida, controle.getInformacoesCompletas(), 0);

            // Escrevendo Bloco de Dados
            for (BlocoDado d : dados) {
                //System.out.println("containerID.blocoID = " + d.getContainerBlocoID());
                System.out.println("BlocoID: " + getIntFrom3Bytes(d.getIdBloco()));

                byte[] blocoCompleto = d.getInformacoesCompletas();
                escreverArquivo(saida, blocoCompleto, offset);
                offset += blocoCompleto.length;
            }

            System.out.println("~ Fim ~");


            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void lerTabela(int tabelaID){

        if(tabelaID < 1 || tabelaID > getQuantidadeArquivosSaidaTabelas()){
            System.out.println("A tabela" + tabelaID + ".txt não existe.");
        }

        File file = buscarArquivo(tabelaID);

        // carregar bloco de controle e de dados do file
        BlocoControle controle = carregarBlocoControle(file);
        ArrayList<BlocoDado> dados = carregarBlocosDados(file, controle.getInformacoesCompletas().length, getIntFromBytes(controle.getProximoBloco()) - 1);

        System.out.println("Quantidade blocos: " + dados.size());
        System.out.println(controle.toString());

        for (BlocoDado d: dados) {
            System.out.println(d.toString());
        }
    }

    private BlocoControle carregarBlocoControle(File file){
        System.out.println("Carregando Bloco de Controle da tabela " + file.getName());
        byte[] dadosControle = lerBlocoControle(file);

        return new BlocoControle(dadosControle);
    }

    private ArrayList<BlocoDado> carregarBlocosDados(File file, int start, int ultimoBlocoID){
        // ler desde o primeiro bloco ate o proximo bloco livre - 1
        boolean buscarBloco = true;
        ArrayList<BlocoDado> result = new ArrayList<>();

        int offset = start;

        while (buscarBloco){

            byte[] bytes = lerDadosArquivo(file, offset, TAMANHO_BLOCO);
            offset+= bytes.length;
            BlocoDado bloco = new BlocoDado(bytes);
            result.add(bloco);

            if(getIntFrom3Bytes(bloco.getIdBloco()) == ultimoBlocoID){
                buscarBloco = false;
            }

        }

        return result;
    }

    private int getContainerID(){
        int result = containerID;
        atualizarContainerID();
        return result;
    }

    private void atualizarContainerID(){
        this.containerID++;
    }

    static {
        System.out.println("Iniciando Gerenciador de Arquivos...\n");
    }

}
