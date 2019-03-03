package main;

import sgbd.GerenciadorArquivos;

import static utils.DiretorioUtils.getDiretorioEntrada;

public class TesteFiles {

    public static void main(String[] args) {

        GerenciadorArquivos ga = new GerenciadorArquivos();
        //ga.criarTabela(getDiretorioEntrada() + "\\teste.txt");
        ga.lerTabela(1);

    }

}
