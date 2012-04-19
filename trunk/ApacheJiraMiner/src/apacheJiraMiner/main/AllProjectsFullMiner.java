/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.miner.HttpIssueMiner;
import apacheJiraMiner.miner.HttpProjetosMiner;
import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Connection;

/**
 *
 * @author Douglas
 */
public class AllProjectsFullMiner {

    public static void main(String[] args) {


        /*
         * este método irá coletar todos os projetos da página: "https://issues.apache.org/jira/secure/BrowseProjects.jspa#all"
         * não tem problema rodar este método várias vezes pois não cadastrará projetos repetidos
         */
        minerarSomenteProjetos();


        /*
         * este método percorrerá todos os projetos cadastrados acima e irá minerar suas Issues e comentários
         */
        //    minerarIssuesDosProjetos(0, 999);

    }

    private static void minerarSomenteProjetos() {
        Connection.conectarDao();
        HttpProjetosMiner httpProjetos = new HttpProjetosMiner();
        try {
            httpProjetos.minerarProjetos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Connection.fecharConexao();
    }

    private static boolean mineracaoDasIssuesDoProjetoJaIniciada(Projeto projeto) {
        try {
            Connection.dao.refreshObjeto(projeto);
        } catch (Exception ex) {
        }
        if (projeto.getIssues().isEmpty()) {
            return false;
        }
        return true;
    }

    private static void minerarIssuesDosProjetos(int idProjetoInicial, int idProjetoFinal) {
        for (int i = idProjetoInicial; i <= idProjetoFinal; i++) {
            Connection.conectarDao();
            Projeto projeto = (Projeto) Connection.dao.buscaIDint(Projeto.class, i);
            if (projeto != null && !mineracaoDasIssuesDoProjetoJaIniciada(projeto)) {
                HttpIssueMiner httpIssues = new HttpIssueMiner(projeto, 1, true, true);
                try {
                    httpIssues.minerarIssues();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                httpIssues = null;
            }
            projeto = null;
            Connection.fecharConexao();
            System.gc();
        }
    }
}
