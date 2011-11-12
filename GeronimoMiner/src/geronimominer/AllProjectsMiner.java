/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import dao.DAO;
import java.io.File;
import java.util.List;
import javax.persistence.Persistence;
import pojo.Projeto;

/**
 *
 * @author Douglas
 */
public class AllProjectsMiner {

    public static DAO daoProjeto;

    public static void conectarDao() {
        daoProjeto = new DAO(Persistence.createEntityManagerFactory("HttpMineratorPU").createEntityManager());
    }

    public static void main(String[] args) {



        /*
         * este método irá coletar todos os projetos da página: "https://issues.apache.org/jira/secure/BrowseProjects.jspa#all"
         * não tem problema rodar este método várias vezes pois não cadastrará projetos repetidos
        
         * 
         
        
        conectarDao();
        HttpProjetosMiner httpProjetos = new HttpProjetosMiner();
        try {
            httpProjetos.minerarProjetos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fecharConexao();



        /*
         * este método percorrerá todos os projetos cadastrados e irá minerar suas Issues e comentários
         */
        for (int i = 1; i <= 400; i++) {
            conectarDao();
            Projeto projeto = (Projeto) daoProjeto.buscaIDint(Projeto.class, i);
            if (projeto != null && !projetoJaIniciado(projeto)) {
                HttpIssueMiner httpIssues = new HttpIssueMiner(projeto, 1);
                try {
                    httpIssues.minerarIssues();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                httpIssues = null;
            }
            projeto = null;
            fecharConexao();
            System.gc();
        }


    }

    public static boolean projetoJaIniciado(Projeto projeto) {
        File file = new File("src");
        File[] files = file.listFiles();
        for (File fl : files) {
            if (fl.getName().replaceAll(".txt", "").equals(projeto.getNome())
                    || fl.getName().replaceAll(".txt", "").equals(projeto.getxKey())) {
                return true;
            }
        }
        return false;
    }

    public static Projeto consultaPorKey(String key) {
        List<Projeto> pjts = daoProjeto.selecionaComParametros("SELECT p FROM Projeto p WHERE p.xKey = :key",
                new String[]{"key"},
                new Object[]{key});
        if (pjts.size() == 1) {
            return pjts.get(0);
        }
        return null;
    }

    private static void fecharConexao() {
        daoProjeto.fecharConexao();
        daoProjeto = null;
    }
}
