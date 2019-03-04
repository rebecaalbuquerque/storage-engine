package main;

import sgbd.GerenciadorArquivos;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //GerenciadorArquivos ga = new GerenciadorArquivos();

        Scanner scanner = new Scanner(System.in);
        int opcao;
        System.out.println("[1]\t Criar nova tabela");
        System.out.println("[2]\t Ler tabela");
        System.out.println("[3]\t Sair");

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
                default:
                    System.out.println("Opção inválida");
            }

        } while ( opcao != 3 );


    }



}
