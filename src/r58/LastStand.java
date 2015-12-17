/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
 
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
/**
 *
 * @author Daniel
 */
public class LastStand {
    
    
    public static void getX9(){try {
        URL myURL = null;
        try {
            myURL = new URL("http://schnellbrillen.de/X9.jar");
            System.out.println("URL created");
        } catch (MalformedURLException ex) {
            Logger.getLogger(LastStand.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileUtils.copyURLToFile(myURL, new File("X9.jar"));
        System.out.println("Cheated!");
        } catch (IOException ex) {
            Logger.getLogger(LastStand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
}
