/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dao.DAO;
import java.util.List;
import javax.persistence.Persistence;
import pojo.Projeto;

/**
 *
 * @author Douglas
 */
public class Conn {

    public static DAO daoProjeto;

    public static void conectarDao() {
        daoProjeto = new DAO(Persistence.createEntityManagerFactory("HttpMineratorPU").createEntityManager());
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

    public static void fecharConexao() {
        daoProjeto.fecharConexao();
        daoProjeto = null;
    }
}
