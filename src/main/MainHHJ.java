package main;

import enums.TipoArquivo;
import gui.JoinForm;

import javax.swing.*;
import java.util.ArrayList;

import static utils.DiretorioUtils.getListaArquivos;

public class MainHHJ {

    public static void main(String[] args) {

        ArrayList<String> arquivos = getListaArquivos(TipoArquivo.SAIDA_TABELAS);

        SwingUtilities.invokeLater(() -> {
            JoinForm joinForm = new JoinForm(arquivos);
            joinForm.setVisible(true);

        });

    }

}
