/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geronimominer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojo.Comentario;
import pojo.Issue;
import pojo.Projeto;
import util.Conn;

/**
 *
 * @author Douglas
 */
public class AcertarDataHoraComentarios {

    public static void main(String[] args) {

        for (int i = 101; i <= 150; i++) {
            Conn.conectarDao();
            Projeto projeto = (Projeto) Conn.daoProjeto.buscaIDint(Projeto.class, i);
            if (projeto != null) {
                System.err.println("Processando Projeto: " + projeto.getId() + " " + projeto.getxKey());
               // System.out.println("Issues: "+projeto.getIssues().size());
                for (Issue issue : projeto.getIssues()) {
                    for (Comentario coment : issue.getComentarios()) {
                       // System.err.println("Projeto: " + projeto.getxKey() + " Issue: " + issue.getNumeroIssue() + " Coment:" + coment.getId());
                        String strHora = coment.getHoraComentario();
                        if (strHora != null && !strHora.isEmpty()) {
                            Date data = coment.getDataComentario();
                            String strData = new SimpleDateFormat("dd/MM/yyyy").format(data);
                            try {
                                coment.setDataComentario(new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(strData + " " + strHora));
                                coment.setHoraComentario(null);
                                Conn.daoProjeto.atualiza(coment);
                            } catch (Exception ex) {
                                System.out.println("Comentario: " + coment.getId());
                                ex.printStackTrace();
                            }
                            strData = null;
                            data = null;
                        }
                        strHora = null;
                        coment = null;
                    }
                    issue = null;
                }
            }
            projeto = null;
            Conn.fecharConexao();
            System.gc();
        }


    }
}
