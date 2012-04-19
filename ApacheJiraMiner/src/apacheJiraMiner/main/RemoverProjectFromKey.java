/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main;

import apacheJiraMiner.pojo.Projeto;
import apacheJiraMiner.util.Connection;

/**
 *
 * @author Douglas
 */
public class RemoverProjectFromKey {

    public static void main(String[] args) {
        // FOR,HDFS,MAPREDUCE,TRINIDAD,OFBIZ,XMLBEANS,FLUME

        String[] keys = new String[]{"FOR", "HDFS", "MAPREDUCE", "TRINIDAD", "OFBIZ", "XMLBEANS", "FLUME", "QPID"};

        for (String string : keys) {
            Connection.conectarDao();
            Projeto projeto = Connection.consultaProjetoPorKey(string);

            if (projeto != null) {
                try {
                    Connection.dao.remove(projeto);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            Connection.fecharConexao();
            System.gc();
        }
    }
}
