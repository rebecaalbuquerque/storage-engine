package main;

import enums.TipoArquivo;
import sgbd.GerenciadorArquivos;
import sgbd.GerenciadorBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static utils.DiretorioUtils.getDiretorioEntrada;
import static utils.DiretorioUtils.getListaArquivos;
import static utils.FileUtils.criarArquivo;
import static utils.FileUtils.getDadosArquivo;


public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static GerenciadorArquivos ga;
    private static GerenciadorBuffer gb;

    public static void main(String[] args) {

        ga = new GerenciadorArquivos();

        int opcao;

        do {
            System.out.println("### MENU SGBD ###");
            System.out.println("[1]\t Criar nova tabela");
            System.out.println("[2]\t Ler tabela");
            System.out.println("[3]\t Gerar Pages IDs");
            System.out.println("[4]\t Simular requisições de PageIDs");
            System.out.println("[5]\t Buscar bloco aleatório a partir de um PageID (exemplo: 1-219)");
            System.out.println("[6]\t Sair");
            System.out.print("\nEscolha uma opção do menu principal: ");
            opcao = scanner.nextInt();
            System.out.println();

            switch (opcao) {
                case 1:
                    iniciarMenuCriarTabela();
                    break;

                case 2:
                    iniciarMenuLerTabela();
                    break;

                case 3:
                    iniciarGeracaoDePageIDs();
                    break;

                case 4:
                    iniciarSimulacaoBuffer();
                    break;

                case 5:
                    iniciarBuscaDeBloco();
                    break;
            }

        } while (opcao != 6);


    }

    private static void iniciarBuscaDeBloco() {
        System.out.println("Digite o PageID");
        String pageID = scanner.next();
        String[] pageIdArray = pageID.split("-");
        ga.printarBloco(Integer.parseInt(pageIdArray[0]), Integer.parseInt(pageIdArray[1]));

    }

    private static void iniciarGeracaoDePageIDs(){
        ga.gerarPageIDs();
    }

    private static void iniciarMenuCriarTabela() {
        ArrayList<String> arquivos = getListaArquivos(TipoArquivo.ENTRADA_ARQUIVOS);
        int index = 0;
        int criarTabelaOpcao;

        do {
            System.out.println("# MENU DE ESCOLHA DE ARQUIVOS #");

            for (String a : arquivos) {
                System.out.println("[" + index + "] " + a);
                index++;
            }

            System.out.print("\nEscolha um arquivo: ");
            criarTabelaOpcao = scanner.nextInt();
            System.out.println();


        } while (criarTabelaOpcao < 0 || criarTabelaOpcao > arquivos.size() - 1);

        ga.criarTabela(getDiretorioEntrada() + "\\" + arquivos.get(criarTabelaOpcao));

    }

    private static void iniciarMenuLerTabela() {
        ArrayList<String> arquivos = getListaArquivos(TipoArquivo.SAIDA_TABELAS);
        int index = 1;
        int lerTabelaOpcao;

        do {
            System.out.println("# MENU DE ESCOLHA DE TABELAS #");

            for (String a : arquivos) {
                System.out.println("[" + index + "] " + a);
                index++;
            }

            System.out.print("\nEscolha uma tabela: ");
            lerTabelaOpcao = scanner.nextInt();
            System.out.println();


        } while (lerTabelaOpcao < 0 || lerTabelaOpcao > arquivos.size());

        ga.lerTabela(lerTabelaOpcao, false);
    }

    private static void iniciarSimulacaoBuffer(){
        File file = criarArquivo(-1, TipoArquivo.ROW_IDS_SHUFFLED);

        gb = new GerenciadorBuffer();

        gb.init(getDadosArquivo(file), ga);

    }

}
