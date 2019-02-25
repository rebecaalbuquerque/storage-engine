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

    public GerenciadorArquivos(){}

    public void criarTabela(){
        int containerId = getQuantidadeArquivosSaida() + 1;
        int offset = 0;
        BlocoControle controle;

        ArrayList<BlocoDado> dados = new ArrayList<>();

        String path = getDiretorioEntrada() + "\\teste2.txt";
        File saida = FileUtils.criarArquivo(containerId);

        FileReader reader;
        BufferedReader buffer;

        // Lendo arquivo teste.txt que contem a tabela a ser lida
        try {
            reader = new FileReader(path);
            buffer = new BufferedReader(reader);

            // Bloco de Controle
            String linha = buffer.readLine();
            controle = new BlocoControle(containerId, linha);
            offset = controle.getInformacoesCompletas().length;

            escreverArquivo(saida, controle.getDadosHeader(), 0);

            // Blocos de Dados
            BlocoDado dado = new BlocoDado(containerId);

            while ((linha = buffer.readLine()) != null ){
                if(!linha.isEmpty()){

                    System.out.println(linha);
                    if(temEspacoParaNovaTupla(dado.getTamanhoTuplasDisponivel(), linha)){
                        dado.adicionarNovaTupla(linha);
                    }


                }

            }

            /* SAIDA */
            escreverArquivo(saida, dado.getTuplas(), offset);


            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static {
        System.out.println("Iniciando Gerenciador de Arquivos...\n");
    }

}
