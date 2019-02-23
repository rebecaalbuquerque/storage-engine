package main;

import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.Arrays;

public class Teste {

    public static void main(String[] args) {
        testarBlocoDados();
    }

   private static void testarBlocoDados(){
        String dado = "505|REBECA ALBUQUERQUE|";
        BlocoDado bloco = new BlocoDado(0, dado);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));
        System.out.println("\t\t\t\t\t\t\t" + Arrays.toString(bloco.getDados()));
   }

    private static void testarBlocoControle(){
        String[] array = new String[]{ "COLA[I(6)]", "COLB[A(25)]" };
        BlocoControle bloco = new BlocoControle(0, array);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));
    }

}
