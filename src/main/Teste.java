package main;

import sgbd.GerenciadorArquivos;
import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.Arrays;

import static utils.BlocoUtils.inserirNovaTupla;

public class Teste {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        int tamanhoBloco = 30;
        int tamanhTuplaDirectory = 0;
        byte[] tuplas = new byte[tamanhoBloco - 8];
        byte[] novaTuplas = new byte[tamanhoBloco - 8];
        int tamanhoTuplasDisponivel = tuplas.length;

        byte[] tupla1 = new byte[]{7, 7, 7, 7, 7, 7, 7};
        byte[] tupla2 = new byte[]{5, 5, 5, 5, 5};

        System.out.println("Tamanho disponível: " + tamanhoTuplasDisponivel + ", Tupla directory size: " + tamanhTuplaDirectory);

        System.out.println("INSERINDO NOVA TUPLA");

        tamanhoTuplasDisponivel -= tupla1.length;
        tamanhTuplaDirectory += 2;
        novaTuplas = inserirNovaTupla(tamanhoTuplasDisponivel, tamanhTuplaDirectory, tupla1, tuplas);

        System.out.println(Arrays.toString(novaTuplas));
        System.out.println("Tamanho disponível: " + tamanhoTuplasDisponivel + ", Tupla directory size: " + tamanhTuplaDirectory);


        System.out.println("\nINSERINDO NOVA TUPLA");

        tamanhoTuplasDisponivel -= tupla2.length;
        tamanhTuplaDirectory += 2;
        novaTuplas = inserirNovaTupla(tamanhoTuplasDisponivel, tamanhTuplaDirectory, tupla2, tuplas);

        System.out.println(Arrays.toString(novaTuplas));
        System.out.println("Tamanho disponível: " + tamanhoTuplasDisponivel + ", Tupla directory size: " + tamanhTuplaDirectory);


        GerenciadorArquivos ga = new GerenciadorArquivos();
        ga.criarTabela();

    }

   private static void testarBlocoDados(){
        String[] array = new String[]{ "6545", "abcdef" };
        BlocoDado bloco = new BlocoDado(0, array);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));
   }

    private static void testarBlocoControle(){
        String[] array = new String[]{ "COLA[I(6)]", "COLB[A(25)]" };
        BlocoControle bloco = new BlocoControle(0, array);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));
    }



}
