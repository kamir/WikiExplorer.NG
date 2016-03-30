/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.wikipedia.analysis.charts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author kamir
 */
public class RepresentationPlotBubbleChart {

 

    public static FileWriter getWriter(String string) throws IOException {
       
        
        return new FileWriter( new File( "./charts/" + string ));
        
    }
    
}
