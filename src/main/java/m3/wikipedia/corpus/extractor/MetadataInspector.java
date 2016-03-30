/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.wikipedia.corpus.extractor;

import java.io.File;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class MetadataInspector {
    
    public static void main(String[] args) throws Exception {
        
        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();

        String studie = "dissertation_DEMO";
        File f6 = new File("/Users/kamir/DEMO/ETOSHA/" + studie + ".xml");
        
        System.out.println( f6.getAbsolutePath() +" => exists:" + f6.exists() );
        
        wd = wd.load(f6);
        wd.initNetFromListFile( f6.getParent(), studie );

        for( int i = 1; i < 5; i++){
                      
            double c = wd.getCN(i).size();
            System.out.println( i + " CN: " + c );
                        
            double iwl = wd.getIWL(i).size();
            System.out.println( i + " IWL: " + iwl );
            
            double a = wd.getAL(i).size();
            System.out.println(i + " AL: " + a );
            
            double b = wd.getBL(i).size();
            System.out.println( i + " BL: " + b );
            
            System.out.println(  );
            
        }
    
    }
    
}
