/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package apacheJiraMiner.main.teste;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Douglas
 */
public class Teste {

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
//        Date date1 = new Date();
//        
//        for (int i = 0; i < 999999999; i++) {
//            
//        }
//        
//        Date date2 = new Date();
//       
//        System.out.println(date1);
//        System.out.println(date2);
//        System.out.println(date1.before(date2));




        File diretorio = new File("log/");
        for (File file : diretorio.listFiles()) {
            boolean terminou = false;
            if (!file.exists()) {
                System.err.println("Arquivo não existe: " + file.getAbsolutePath());
                continue;
            }
            if (file.isDirectory()) {
                System.err.println("Este é um diretório: " + file.getAbsolutePath());
                continue;
            }
            if (file.getAbsolutePath().endsWith(".txt")) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while (br.ready()) {
                    String linha = br.readLine();
                    if (linha.contains("Fim")) {
                        terminou = true;
                    }
                }
                br.close();
                if (!terminou) {
                    System.err.println("############" + file.getName() + "############" + file.getAbsolutePath());
                }
            }
        }
    }
}
