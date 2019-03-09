package main;

import sgbd.GerenciadorArquivos;

import java.util.ArrayList;
import java.util.Scanner;

import static utils.DiretorioUtils.getDiretorioEntrada;
import static utils.DiretorioUtils.getListaArquivos;

@SuppressWarnings("Duplicates")
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static GerenciadorArquivos ga;

    public static void main(String[] args) {

        ga = new GerenciadorArquivos();

        int opcao;

        do {
            System.out.println("### MENU SGBD ###");
            System.out.println("[1]\t Criar nova tabela");
            System.out.println("[2]\t Ler tabela");
            System.out.println("[3]\t Simular requisições de RowIDs");
            System.out.println("[4]\t Sair");
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
            }

        } while (opcao != 4);


    }

    private static void iniciarMenuCriarTabela() {
        ArrayList<String> arquivos = getListaArquivos(true);
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
        ArrayList<String> arquivos = getListaArquivos(false);
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

        ga.lerTabela(lerTabelaOpcao);
    }

    private static void iniciarSimulacaoRowIDs(){
        ArrayList<String> arquivos = getListaArquivos(false);
        int index = 1;
        int rowIDsOpcao;

        do {
            System.out.println("# MENU DE ESCOLHA DE LISTA DE ROWIDS #");

            for (String a : arquivos) {
                System.out.println("[" + index + "] " + a);
                index++;
            }

            System.out.print("\nEscolha uma opção: ");
            rowIDsOpcao = scanner.nextInt();
            System.out.println();


        } while (rowIDsOpcao < 0 || rowIDsOpcao > arquivos.size() - 1);
    }

}
