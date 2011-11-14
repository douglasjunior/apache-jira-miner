/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import java.io.File;
import pojo.Projeto;
import util.Conn;

/**
 *
 * @author Douglas
 */
public class AllProjectsCommitsMiner {

    public static void main(String[] args) {
        for (int i = 1; i <= 50; i++) {
            Conn.conectarDao();
            Projeto projeto = (Projeto) Conn.daoProjeto.buscaIDint(Projeto.class, i);
            if (projeto != null && !projetoJaMinerado(projeto)) {
                HttpIssueMiner httpIssues = new HttpIssueMiner(projeto);
                try {
                    httpIssues.atualizarCommits();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                httpIssues = null;
            }
            projeto = null;
            Conn.fecharConexao();
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
