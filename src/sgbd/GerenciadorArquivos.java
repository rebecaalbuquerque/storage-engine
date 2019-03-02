package sgbd;

import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;
import utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static utils.BlocoUtils.temEspacoParaNovaTupla;
import static utils.DiretorioUtils.getDiretorioEntrada;
import static utils.DiretorioUtils.getQuantidadeArquivosSaida;
import static utils.RAFUtils.escreverArquivo;

public class GerenciadorArquivos {

    private int containerID;

    public GerenciadorArquivos(){
        containerID = getQuantidadeArquivosSaida() + 1;
    }

    public void criarTabela(){
        int offset;
        String linha;
        String entradaPath = getDiretorioEntrada() + "\\teste2.txt";
        File saida = FileUtils.criarArquivo(containerID);
        ArrayList<BlocoDado> dados = new ArrayList<>();

        FileReader reader;
        BufferedReader buffer;

        // Lendo arquivo teste.txt que contem a tabela a ser lida
        try {
            reader = new FileReader(entradaPath);
            buffer = new BufferedReader(reader);

            // Escrevendo Bloco de Controle
            BlocoControle controle = new BlocoControle(containerID, buffer.readLine());
            offset = controle.getDadosHeader().length;
            escreverArquivo(saida, controle.getDadosHeader(), 0);

            BlocoDado blocoDadoAtual = new BlocoDado(getContainerID());

            while ((linha = buffer.readLine()) != null ){

                if(temEspacoParaNovaTupla(blocoDadoAtual.getTamanhoTuplasDisponivel(), linha)){
                    blocoDadoAtual.adicionarNovaTupla(linha);
                    // TODO: setProximoBloco
                    dados.add(blocoDadoAtual);
                } else {
                    blocoDadoAtual = new BlocoDado(getContainerID());
                    blocoDadoAtual.adicionarNovaTupla(linha);
                    dados.add(blocoDadoAtual);
                }

            }

            // Escrevendo Bloco de Dados
            for (BlocoDado d : dados) {
                escreverArquivo(saida, d.getTuplas(), offset);
                offset += d.getTuplas().length;
            }


            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
