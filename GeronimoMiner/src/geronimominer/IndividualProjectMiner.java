/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import pojo.Projeto;

/**
 *
 * @author Douglas
 */
public class IndividualProjectMiner {

    public static void main(String[] args) {
        AllProjectsMiner.conectarDao();

        HttpProjetosMiner httpProjetos = new HttpProjetosMiner();
        try {
            httpProjetos.minerarProjetos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Projeto projeto = AllProjectsMiner.consultaPorKey("FOR");
        int proximaPagina = 168;
        
        if (projeto != null) {
            HttpIssueMiner httpIssues = new HttpIssueMiner(projeto, proximaPagina);
            try {
                httpIssues.minerarIssues();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
