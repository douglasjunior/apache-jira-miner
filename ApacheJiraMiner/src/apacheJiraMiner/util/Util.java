/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Douglas
 */
public class Util {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String[] CARACTERES = {"{", "}", "(", ")", "\\[", "\\]", "<", ">",
        ":", ";", ".", ",", "!", "?", "\\", "/", "~", "`", "\"", "\'", "\\\\",
        "=", "+", "\\-", "*", "@", "#", "$", "%", "^", "&", "_", "\\|"};

    public static Date stringToDate(String yyyyMMdd) throws ParseException {
        return format.parse(yyyyMMdd);
    }

    public static BufferedReader abrirStream(URL url) {
        try {
            return new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return abrirStream(url);
        }
    }

    public static boolean writeToFile(String caminho, String msg) {
        File file = new File(caminho);
        try {
            if (!file.exists()) {
                new File("log/").mkdirs();
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(msg);
            pw.close();
            fw.close();
            pw = null;
            fw = null;
            file = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static String filterChar(String texto) {
        StringBuilder padrao = new StringBuilder();
        padrao.append("[^a-zA-Z0-9|");
        for (int i = 0; i < CARACTERES.length; i++) {
            padrao.append(CARACTERES[i]);
            if (i < CARACTERES.length - 1) {
                padrao.append("|");
            }
        }
        padrao.append("]");
        return (texto.replaceAll(padrao.toString(), " "));
    }
}
