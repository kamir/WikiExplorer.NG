/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.hadoopts.core.SingleRowTSO;
import java.util.Vector; 

/**
 *
 * @author kamir
 */
public class HadoopTSAdapter {
    
    public static Vector<SingleRowTSO> rowOperators = null;
    
    public static void init() { 
        rowOperators = new Vector<SingleRowTSO>();
        
        // rowOperators.add( new SingleTsRISTool() );
               
    }
    
}
