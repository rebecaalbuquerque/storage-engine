package main;

import gui.MyGUIForm;

import javax.swing.*;
import java.util.ArrayList;

public class MainGUI {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ArrayList<String> lista = new ArrayList<>();

            lista.add("Arquivo1.txt");
            lista.add("Arquivo2.txt");
            lista.add("Arquivo3.txt");

            MyGUIForm myGUI = new MyGUIForm(lista);
            myGUI.setVisible(true);

        });

    }

}
