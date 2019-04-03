package main;

import gui.JoinForm;

import javax.swing.*;
import java.util.ArrayList;

public class MainGUI {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ArrayList<String> lista = new ArrayList<>();

            lista.add("tabela1.txt");
            lista.add("tabela2.txt");
            lista.add("tabela3.txt");

            JoinForm joinForm = new JoinForm(lista);
            joinForm.setVisible(true);

        });

    }

}
