/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        File f = new File(caminho);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(msg);
            pw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
