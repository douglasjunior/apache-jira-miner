/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Douglas
 */
public class Util {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public static Date stringToDate(String yyyyMMdd) throws ParseException{
        return format.parse(yyyyMMdd);
    }
    
    public static InputStream abrirStream(URL url) {
        try {
            return url.openStream();
        } catch (Exception ex) {
            ex.printStackTrace();
            return abrirStream(url);
        }
    }
}
