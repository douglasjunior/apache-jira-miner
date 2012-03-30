/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.miner.HttpIssueMiner;
import java.io.File;
import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Connection;

/**
 *
 * @author Douglas
 */
public class AllProjectsCommitsMiner {

    public static void main(String[] args) {
        for (int i = 368; i >= 1; i--) {
            Connection.conectarDao();
            Projeto projeto = (Projeto) Connection.dao.buscaIDint(Projeto.class, i);
            if (projeto != null && !projetoJaMinerado(projeto)) {
                HttpIssueMiner httpIssues = new HttpIssueMiner(projeto);
                try {
                    httpIssues.atualizarCommitsDasIssues();
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

    private static boolean projetoJaMinerado(Projeto projeto) {
        File file = new File("src");
        File[] files = file.listFiles();
        for (File fl : files) {
            if (fl.getName().contains(".commit")
                    && fl.getName().replaceAll(".commit", "").equals(projeto.getxKey())) {
                return true;
            }
        }
        return false;
    }
}
