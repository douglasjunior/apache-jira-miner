/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main.teste;

import java.awt.FileDialog;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *
 * @author Douglas
 */
public class Teste {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Date date1 = new Date();
        
        for (int i = 0; i < 999999999; i++) {
            
        }
        
        Date date2 = new Date();
       
        System.out.println(date1);
        System.out.println(date2);
        System.out.println(date1.before(date2));
                
    }
}
