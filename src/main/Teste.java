package main;

import model.BlocoControle;
import model.BlocoDado;

import java.util.Arrays;

public class Teste {

    public static void main(String[] args) {
        System.out.println("Testando bloco de controle");
        testarBlocoControle();

        System.out.println("Testando bloco de dados");
        testarBlocoDados();
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
