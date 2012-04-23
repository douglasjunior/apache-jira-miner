/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.miner.HttpIssueMiner;
import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Connection;

/**
 *
 * @author Douglas
 */
public class IndividualProjectMiner {

    public static void main(String[] args) {
        Connection.conectarDao();

//        HttpProjetosMiner httpProjetos = new HttpProjetosMiner();
//        try {
//            httpProjetos.minerarProjetos();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }


        Projeto projeto = Connection.consultaProjetoPorKey("HADOOP");
        int proximaPagina = 7700;

        if (projeto != null) {
            HttpIssueMiner httpIssues = new HttpIssueMiner(projeto, proximaPagina, true, true);
            try {
                httpIssues.minerarIssues();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
