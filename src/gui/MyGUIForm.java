package gui;

import javax.swing.*;
import java.util.ArrayList;

public class MyGUIForm extends JFrame {

    private JPanel rootPanel;
    private JList<String> listaArquivos;
    private JButton btnAbrirArquivo;
    private JPanel arquivosPanel;
    private JPanel tabelaPanel;
    private JTable tabela;

    public MyGUIForm(){
        init(null);
    }

    public MyGUIForm(ArrayList<String> lista){
        init(lista);
    }

    private void init(ArrayList<String> lista){
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("SGBD");

        if(lista != null)
            setupLista(lista);
        else
            setupLista(new ArrayList<>());

        setupBtnAbrirArquivo();

        add(rootPanel);
    }

    private void setupLista(ArrayList<String> lista){
        DefaultListModel<String> listaModel = new DefaultListModel<>();

        for (String s : lista) {
            listaModel.addElement(s);
        }

        listaArquivos.setModel(listaModel);
    }

    private void setupBtnAbrirArquivo(){
        btnAbrirArquivo.addActionListener(
                e -> System.out.println("Arquivo selecionado: " + listaArquivos.getSelectedValue())
        );
    }

}
