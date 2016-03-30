/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudera.wikiexplorer.ng.util;

//import edu.stanford.ejalbert.BrowserLauncher;
//import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
//import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class LaunchBrowser {

    public static void openReport(String filename) throws java.io.IOException {
        

        String url = "file://" + filename;
//        try {
            //        String osName = System.getProperty("os.name").toLowerCase();
            //        if (osName.startsWith("windows")) {
            //
            //            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            //        }

            System.out.println(">>> open: " + url + " in Browser ...");
//            BrowserLauncher bl = new BrowserLauncher();
//
//            bl.openURLinBrowser(url);

//        } catch (BrowserLaunchingInitializingException ex) {
//            Logger.getLogger(LaunchBrowser.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedOperatingSystemException ex) {
//            Logger.getLogger(LaunchBrowser.class.getName()).log(Level.SEVERE, null, ex);
//        }



    }


}
