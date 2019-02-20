package sgbd;

import sgbd.bloco.BlocoControle;
import sgbd.bloco.BlocoDado;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static constants.ConstantesRegex.SEPARADOR_COLUNA;
import static utils.DiretorioUtils.*;
import static utils.RAFUtils.escreverArquivo;

public class GerenciadorArquivos {

    public GerenciadorArquivos(){}

    public void criarTabela(){
        int containerId = getQuantidadeArquivosSaida() + 1;
        BlocoControle controle = null;
        ArrayList<BlocoDado> dados = new ArrayList<>();

        String path = getDiretorioEntrada() + "\\teste.txt";
        String saida = getDiretorioSaida() + "\\saida_teste.txt";
        File arquivo_saida = new File(saida);

        FileReader reader = null;
        BufferedReader buffer = null;

        try {
            reader = new FileReader(path);
            buffer = new BufferedReader(reader);

            /* LENDO O ARQUIVO ENTRADA E TRANSFORMANDO EM BLOCOS (CONTROLE E DADOS) */
            String linha = buffer.readLine();
            controle = new BlocoControle(containerId, linha.split(SEPARADOR_COLUNA));
            controle.getInformacoesCompletas();

            while ((linha = buffer.readLine()) != null ){
                if(!linha.isEmpty()){
                    System.out.println(linha);
                    BlocoDado dado = new BlocoDado(containerId, linha.split(SEPARADOR_COLUNA));
                    dados.add(dado);
                }

            }

            /* SAIDA */
            escreverArquivo(arquivo_saida, controle.getDadosHeader(), 0);

            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static {
        System.out.println("Iniciando Gerenciador de Arquivos...\n");
    }

}
