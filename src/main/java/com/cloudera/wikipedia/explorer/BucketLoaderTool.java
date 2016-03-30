/**
 *  Some static helper for working with a Bucket ...
 **/
package com.cloudera.wikipedia.explorer;

import com.cloudera.wikiexplorer.ng.app.SimpleSFE;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kamir
 */
public class BucketLoaderTool implements Runnable {
    
    SimpleSFE gui = null;
            
    public static BucketLoaderTool getBucketLoaderTool( SimpleSFE gui ) { 
        BucketLoaderTool tool = new BucketLoaderTool();
        tool.gui = gui;
        
        return tool;
    };
    public static boolean GUI_AUTOMODE = false;

    void init() throws IOException {
        // Prozedur für einzelnes File aus dem Cluster ...
        gui.getFileName();
        gui.initFileSystem();
        gui.getMetaDataOnly(true);
//        gui.loadRowsWithLimit();
        gui.initListe2();
    }

    @Override
    public void run() {
        try {
            init();
        } 
        catch (IOException ex) {
            Logger.getLogger(BucketLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
