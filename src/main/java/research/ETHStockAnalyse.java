/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research;

import research.wikinetworks.MainCC;

/**
 *
 * @author kamir
 */
public class ETHStockAnalyse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        MainCC.interval = 90;
        MainCC.minLoops = 0;
        MainCC.maxLoops = 35;
        MainCC.isAlone = false;    
        
        MainCC.mode0 = true;
        MainCC.mode1 = true;
        
        // hier noch abs-logreturn als mode 3 ergänzen
        
        // Hintergrundfarbe der Bilder ändern 
                 
        MainCC.modeNotShuffle = true;
        MainCC.modeShuffle = true;        
        
        MainCC.mode = 1001;
        MainCC.main( args );
        
//        MainCC.mode = 1001;
//        MainCC.main( args );

        MainCC.mode = 2001;
        MainCC.main( args );

//        MainCC.mode = 2001;
//        MainCC.main( args );        
        
    }
}
