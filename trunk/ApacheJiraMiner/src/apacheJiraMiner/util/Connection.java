/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.util;

import apacheJiraMiner.pojo.Issue;
import apacheJiraMiner.pojo.Projeto;
import dao.DAO;
import java.util.List;
import javax.persistence.Persistence;

/**
 *
 * @author Douglas
 */
public class Connection {

    public static DAO dao;

    public static void conectarDao() {
        dao = new DAO(Persistence.createEntityManagerFactory("HttpMineratorPU").createEntityManager());
    }

    public static Projeto consultaProjetoPorKey(String key) {
        List<Projeto> pjts = dao.selecionaComParametros("SELECT p FROM Projeto p WHERE p.xKey = :key",
                new String[]{"key"},
                new Object[]{key});
        if (pjts.isEmpty()) {
            return null;
        }
        return pjts.get(0);
    }
    
    public static Issue consultaIssuePorNumeroEProjeto(int numeroIssue, Projeto projeto) {
        List<Issue> issues = dao.selecionaComParametros("SELECT i FROM Issue i WHERE i.numeroIssue = :numeroIssue AND i.projeto = :projeto",
                new String[]{"numeroIssue", "projeto"},
                new Object[]{numeroIssue, projeto});
        if (issues.isEmpty()) {
            return null;
        }
        return issues.get(0);
    }

    public static void fecharConexao() {
        dao.fecharConexao();
        dao = null;
    }
}
