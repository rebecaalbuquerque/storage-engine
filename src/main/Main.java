package main;

import enums.TipoArquivo;
import sgbd.GerenciadorArquivos;
import sgbd.GerenciadorBuffer;

import java.util.ArrayList;
import java.util.Scanner;

import static constants.ConstantesRegex.APENAS_LETRAS;
import static constants.ConstantesRegex.CARACTER_ESPECIAL;
import static utils.DiretorioUtils.getDiretorioEntrada;
import static utils.DiretorioUtils.getListaArquivos;


public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static GerenciadorArquivos ga;
    private static GerenciadorBuffer gb;

    public static void main(String[] args) {

        ga = new GerenciadorArquivos();
        gb = new GerenciadorBuffer();

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

                case 3:
                    iniciarSimulacaoBuffer();
                    break;
            }

        } while (opcao != 4);


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

        ga.lerTabela(lerTabelaOpcao);
    }

    private static void iniciarSimulacaoBuffer(){
        ArrayList<String> arquivos = getListaArquivos(TipoArquivo.ROW_IDS);
        int index = 1;
        int rowIDsOpcao;
        String idTabela;

        do {
            System.out.println("# MENU DE ESCOLHA DE LISTA DE ROWIDS #");

            for (String a : arquivos) {
                System.out.println("[" + index + "] " + a);
                index++;
            }

            System.out.print("\nEscolha uma opção: ");
            rowIDsOpcao = scanner.nextInt();
            System.out.println();
            idTabela = arquivos.get(rowIDsOpcao-1).replaceAll(APENAS_LETRAS + "|" + CARACTER_ESPECIAL, "");

        } while (rowIDsOpcao < 0 || rowIDsOpcao > arquivos.size() - 1);


        //gb.init();

    }

}
