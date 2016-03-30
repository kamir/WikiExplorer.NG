/*
 * Vergleiche zwei Messreihen via CC ...
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import experiments.crosscorrelation.KreuzKorrelation;

/**
 *
 * @author kamir
 */
public class CCCompare {

    static int[] dt = { 7, 14, 28, 28*3 }; 
  
    static String[] label = {"illuminati","erfurt"};
    static String title = ".";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        stdlib.StdRandom.initRandomGen(1);
        KreuzKorrelation.GLdebug = true;
        KreuzKorrelation.debug = true;
    
        
        for( String lab : label ) {
            Messreihe[] rows = loadRows( lab );
        
            Vector<Messreihe> ccRows = calcCC( dt, rows );
                
            MultiChart.open(rows);
  
            MultiChart.openAndStore(ccRows, "cross correlation (" + title + ")", "t", "cc", true, "." , lab + ".2.cccompare.dat",  "");
        }
    }

    private static Messreihe[] loadRows(String label) throws FileNotFoundException, IOException {
        File f = new File( label + ".dat" );
        title = f.getName();
        
        Messreihe mr1 = new Messreihe();
        Messreihe mr2 = new Messreihe();
        
        BufferedReader br = new BufferedReader( new FileReader( f ) );
        while( br.ready() ) { 
            String line = br.readLine();
            if ( !line.startsWith("#") ) { 
                
                line = line.replace(",", ".");
                line = line.replace("--", "0");
                StringTokenizer st = new StringTokenizer( line, "\t" );
                double v1 = Double.parseDouble( st.nextToken() );
                double v2 = Double.parseDouble( st.nextToken() );
            
                mr1.addValue(v1);
                mr2.addValue(v2);
            }
        }
        
        System.out.println( ">>> load data : " +  f.getAbsolutePath() );
        
        Messreihe[] r = new Messreihe[2];
        r[0] = mr1;
        r[1] = mr2;
        return r;       
    }

    private static Vector<Messreihe> calcCC(int[] dt, Messreihe[] rows) {
        
        Vector<Messreihe> v = new Vector<Messreihe>();
        for( int l : dt ) {  
            
            Messreihe mr = KreuzKorrelation.calcKR_SLIDINGWINDOW( rows[0].copy(), rows[1].copy(), l );
            
            v.add( mr );
        }    
        return v;
    }
}
