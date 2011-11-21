/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import pojo.Projeto;
import util.Conn;

/**
 *
 * @author Douglas
 */
public class IndividualProjectMiner {

    public static void main(String[] args) {
        Conn.conectarDao();

//        HttpProjetosMiner httpProjetos = new HttpProjetosMiner();
//        try {
//            httpProjetos.minerarProjetos();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        // FOR,HDFS,MAPREDUCE,TRINIDAD,OFBIZ,XMLBEANS,FLUME,QPID


        Projeto projeto = Conn.consultaPorKey("QPID");
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
