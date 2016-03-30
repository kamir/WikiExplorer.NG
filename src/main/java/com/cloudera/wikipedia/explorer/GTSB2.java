/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.data.series.Messreihe;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger; 
import org.apache.hadoopts.statphys.detrending.MultiDFATool;

/**
 *
 * @author kamir
 */
public class GTSB2 {
    
    public static void reset() { 
        rows = new Hashtable<String,Messreihe>(); 
        System.out.println(">>> RESET GTSB2 now ... ");
    }
    
    public static Hashtable<String,Messreihe> rows = new Hashtable<String,Messreihe>(); 

    public void putAll(Vector<Messreihe> v) {
       for( Messreihe r : v ) { 
            System.out.println( ">>> HOLD : " + r.getLabel() + " " );
            rows.put( r.getLabel(), r);
       };
    }
    
    public void listRows(){ 
        for( String s : rows.keySet() ) { 
            System.out.println( "> " + s );
        }
    };
    
    public Vector<Messreihe> getRows() { 
        Vector<Messreihe> row = new Vector<Messreihe>();
        for( String s : rows.keySet() ) { 
            System.out.println( " >>> " + s );
            row.add( rows.get(s) );
        }
        return row;
    }

    public void analyseRows() {
        this.listRows();
        Vector<Messreihe> rows = getRows();
        System.out.println( ">>> GTSB2.analyseRows() ... z=" + rows.size() );
        try {
            // MFDFATester.process(rows, "t1");
            System.out.println( "*** SELECT A ANALYSIS-Tool ... "  );
        } 
        catch (Exception ex) {
            Logger.getLogger(GTSB2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
