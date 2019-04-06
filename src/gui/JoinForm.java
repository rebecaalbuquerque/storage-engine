package gui;

import sgbd.HHJ;

import javax.swing.*;
import java.util.ArrayList;

import static constants.ConstantesRegex.APENAS_LETRAS;
import static constants.ConstantesRegex.CARACTER_ESPECIAL;

public class JoinForm extends JFrame {

    private JPanel rootPanel;

    private JButton btnJoin;

    private JPanel arquivosPanel;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel tabelaPanel;
    private JPanel panelSeparator1;
    private JPanel panelBtnJoin;
    private JPanel panelSeparator2;

    private JTable tabela;

    private JComboBox<String> cbTabela1;
    private JComboBox<String> cbTabela2;

    private JList<String> listaIndices1;
    private JList<String> listaIndices2;

    private JTextField txtTamanhoMemoriaHHJ;

    // ---------------------------- //
    HHJ join = new HHJ();

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

        setupBtnJoin();

        setupComboBoxClick(cbTabela1, listaIndices1);
        setupComboBoxClick(cbTabela2, listaIndices2);

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

    private void setupBtnJoin(){
        btnJoin.addActionListener(
                e -> System.out.println("Join")
        );
    }

    private void setupComboBoxClick(JComboBox<String> jComboBox, JList<String> jList){
        jComboBox.addActionListener(e ->
                initValuesForComboBoxTabela(String.valueOf(jComboBox.getSelectedItem()), jList)
        );
    }

    private void initValuesForComboBoxTabela(String itemSelecionado, JList<String> jList){
        int id = Integer.parseInt(itemSelecionado.replaceAll(APENAS_LETRAS + "|" + CARACTER_ESPECIAL, ""));

        setupJList(join.buscarColunasTabelaById(id), jList);
    }

    private void setupJList(ArrayList<String> lista, JList<String> jList){
        DefaultListModel<String> listaModel = new DefaultListModel<>();

        for (String s : lista) {
            listaModel.addElement(s);
        }

        jList.setModel(listaModel);
    }

}
