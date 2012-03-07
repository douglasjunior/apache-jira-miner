/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.miner.HttpIssueMiner;
import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Conn;

/**
 *
 * @author Douglas
 */
public class IndividualProjectCommitsMiner {

    public static void main(String[] args) {
        Conn.conectarDao();

        Projeto projeto = Conn.consultaPorKey("DIRSTUDIO");
        int proximaPagina = 495;

        if (projeto != null) {
            HttpIssueMiner httpIssues = new HttpIssueMiner(projeto);
            try {
                httpIssues.atualizarCommitsDasIssues(proximaPagina);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
