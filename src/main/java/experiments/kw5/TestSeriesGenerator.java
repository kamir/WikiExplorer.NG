/*
 *
 */
package experiments.kw5;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class TestSeriesGenerator {
  
    public static void main( String[] args ) throws IOException { 
        stdlib.StdRandom.initRandomGen(1);
        
        String label = "random_A";
        
        int N = 50;
        int Z = 7195;
        
        Vector<TimeSeriesObject> reihen = new Vector<TimeSeriesObject>();
        for ( int n = 0 ; n < N; n++ ) { 
            TimeSeriesObject mr = TimeSeriesObject.getGaussianDistribution( Z , 0.0, 1.0 );
            reihen.add(mr);
        }
        writeTimeSeriesObject( reihen, label );
        
        
        
    }

    private static void writeTimeSeriesObject(Vector<TimeSeriesObject> reihen, String label ) throws IOException {
        
        String pfad = "P:/DATA/testreihen/" + label;
        
        File f = new File( pfad );
        if ( f.exists() ) { }
        else { f.mkdirs(); };
        
        StringBuffer sb = new StringBuffer();
        Enumeration<TimeSeriesObject> en = reihen.elements();
        int i = 1;
        DecimalFormat df = new DecimalFormat("0.000000");
        
        while( en.hasMoreElements() ) { 
            File file = new File( pfad + "/PageID_" + i + ".wmd.h.dat" );
            System.out.println( file.getAbsolutePath() );
            FileWriter fw = new FileWriter( pfad + "/PageID_" + i + ".wmd.h.dat" );
            
            TimeSeriesObject mr = en.nextElement();
            int zz = mr.getSize()[1];
            double[][] data = mr.getData();
            for( int j = 0; j < zz; j++ ) {  
                fw.write(  df.format(data[1][j]) + "\n" );
            }
            fw.close();
            sb.append(i+"\n");
            i++;
        }
        FileWriter fw = new FileWriter( pfad + "/ng.dat" );
        fw.write( sb.toString() );
        fw.close();
    }
}
