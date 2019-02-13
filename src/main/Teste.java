package main;

import model.BlocoControle;

import java.util.Arrays;

public class Teste {

    public static void main(String[] args) {

        String[] array = new String[]{ "COLA[I(6)]", "COLB[A(25)]" };
        BlocoControle bloco = new BlocoControle(array, 0);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));

    }


}
