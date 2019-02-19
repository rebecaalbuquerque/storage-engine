package main;

import sgbd.GerenciadorArquivos;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        GerenciadorArquivos ga = new GerenciadorArquivos();

        Scanner scanner = new Scanner(System.in);
        int opcao;
        System.out.println("[1]\t Criar arquivo/tabela");
        System.out.println("[2]\t Ler sgbd.bloco");
        System.out.println("[3]\t Gravar sgbd.bloco (existente)");
        System.out.println("[4]\t Criar novo sgbd.bloco");
        System.out.println("[5]\t Excluir sgbd.bloco");

        do{

            System.out.print("\nEscolha uma opção: ");
            opcao = scanner.nextInt();
            System.out.println();

            switch (opcao) {
                case 1:
                    System.out.println("opção 1");
                    break;
                case 2:
                    System.out.println("opção 2");
                    break;
                case 3:
                    System.out.println("opção 3");
                    break;
                case 4:
                    System.out.println("opção 4");
                    break;
                case 5:
                    System.out.println("opção 5");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        } while ( opcao < 1 || opcao > 5 );


    }

}
