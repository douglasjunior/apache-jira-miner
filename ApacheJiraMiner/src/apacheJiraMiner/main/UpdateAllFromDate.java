/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.miner.HttpIssueMiner;
import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Connection;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Douglas
 */
public class UpdateAllFromDate {

    public static void main(String[] args) throws ParseException {
//        int idProjetoInicial = 1;
//        int idProjetoFinal = 400;
        
        int issueInicial = 929;
        String key = "ZOOKEEPER";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date data = dateFormat.parse("21/11/2011");

//        Connection.conectarDao();
//        List<Projeto> projetos = Connection.dao.selecionaTodos("Projeto", "id");
//        Connection.fecharConexao();

//        for (int i = idProjetoInicial; i <= idProjetoFinal && i < projetos.size(); i++) {
            Connection.conectarDao();
//            int idProjeto = projetos.get(i).getId();
            Projeto projeto = Connection.consultaProjetoPorKey(key);
        //    if (projeto != null && !projetoJaAtualizado(projeto)) {
            if (projeto != null) {
                HttpIssueMiner httpIssues = new HttpIssueMiner(projeto, data, true, true);
                try {
                    httpIssues.atualizarDadosDasIssuesDoProjetoAPartirDeUmaData(issueInicial);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                httpIssues = null;
            }else{
                System.out.println("Projeto não encontrado.");
            }
            projeto = null;
            Connection.fecharConexao();
            System.gc();
//        }

    }

    private static boolean projetoJaAtualizado(Projeto projeto) {
        File file = new File("log/" + projeto.getxKey().toUpperCase() + ".txt");
        return file.exists();
    }
}
