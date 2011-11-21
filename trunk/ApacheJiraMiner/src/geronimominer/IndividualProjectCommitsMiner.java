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
public class IndividualProjectCommitsMiner {

    public static void main(String[] args) {
        Conn.conectarDao();

        Projeto projeto = Conn.consultaPorKey("DIRSTUDIO");
        int proximaPagina = 495;

        if (projeto != null) {
            HttpIssueMiner httpIssues = new HttpIssueMiner(projeto);
            try {
                httpIssues.atualizarCommits(proximaPagina);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
