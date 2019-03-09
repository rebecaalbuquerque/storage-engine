package main;

import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;

import java.util.Arrays;

import static utils.BlocoUtils.temEspacoParaNovaTupla;

public class Teste {

    private static final String dado = "5|RE|";
    private static final String header = "COD_AUTHOR[I(5)]|NAME_AUTHOR[A(100)]|";
    private static final byte[] dadoBytes = new byte[]{0, 0, 0, 1, 1, 0, 2, 0, 20, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 0, 4, 0, 0, 1, -7, 0, 18, 82, 69, 66, 69, 67, 65, 32, 65, 76, 66, 85, 81, 85, 69, 82, 81, 85, 69};

    public static void main(String[] args) {
        //testarBlocoDados(dado);
        //testarBlocoControle();
    }

    private static void testarBlocoDados(String linha) {
        BlocoDado bloco = new BlocoDado(0);

        if (temEspacoParaNovaTupla(bloco.getTamanhoTuplasDisponivel(), linha)) {
            bloco.adicionarNovaTupla(linha);
            bloco.adicionarNovaTupla("6|BR|");
            System.out.println("Resultado: " + Arrays.toString(bloco.getInformacoesCompletas()));
        }


    }

    private static void testarBlocoControle() {
        BlocoControle bloco = new BlocoControle(0, header);
        System.out.println(bloco.getInformacoesCompletas().length);
        System.out.println(Arrays.toString(bloco.getInformacoesCompletas()));
    }

}
