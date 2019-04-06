package gui;

import javax.swing.*;
import java.util.ArrayList;

public class JoinForm extends JFrame {

    private JPanel rootPanel;

    private JButton btnJoin;

    private JPanel arquivosPanel;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel tabelaPanel;

    private JTable tabela;

    private JComboBox<String> cbTabela1;
    private JComboBox<String> cbTabela2;

    private JList<String> listaIndices1;
    private JList<String> listaIndices2;
    private JPanel panelSeparator1;
    private JPanel panelBtnJoin;
    private JTextField txtTamanhoMemoriaHHJ;
    private JPanel panelSeparator2;

    public JoinForm(){
        init(null);
    }

    public JoinForm(ArrayList<String> lista){
        init(lista);
    }

    private void init(ArrayList<String> lista){
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Hybrid Hash Join");

        if(lista != null)
            setupTabelas(lista);
        else
            setupTabelas(new ArrayList<>());

        setupBtnAbrirArquivo();

        add(rootPanel);
    }

    private void setupTabelas(ArrayList<String> listaTabelas){
        DefaultComboBoxModel<String> listaTabela1Model = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> listaTabela2Model = new DefaultComboBoxModel<>();

        for (String s : listaTabelas) {
            listaTabela1Model.addElement(s);
        }

        for (String s : listaTabelas) {
            listaTabela2Model.addElement(s);
        }

        cbTabela1.setModel(listaTabela1Model);
        cbTabela2.setModel(listaTabela2Model);
    }

    private void setupBtnAbrirArquivo(){
        btnJoin.addActionListener(
                e -> System.out.println("Join")
        );
    }
}
