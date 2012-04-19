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

        // FOR,HDFS,MAPREDUCE,TRINIDAD,OFBIZ,XMLBEANS,FLUME,QPID


        Projeto projeto = Connection.consultaProjetoPorKey("QPID");
        int proximaPagina = 663;

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
