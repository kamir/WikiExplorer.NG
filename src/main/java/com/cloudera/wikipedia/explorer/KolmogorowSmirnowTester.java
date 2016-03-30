/*
 * We compare some distributions ...
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Vector;
import org.apache.xalan.lib.Redirect;
import org.openide.util.Exceptions;

/**
 *
 * @author kamir
 */
class KolmogorowSmirnowTester {

    public static void main(String[] args) {

        stdlib.StdRandom.initRandomGen(1);

        Messreihe mr1 = Messreihe.getGaussianDistribution2(10000, 0.0, 1.0);
        Messreihe mr2 = Messreihe.getGaussianDistribution2(10000, 0.0, 1.0);


        KolmogorowSmirnowTester.test(mr1.yValues, mr2.yValues);
    }

    static void test2( String LABEL ) throws IOException {

       File scr = new File("scripte/KS_SCRIPT."+ LABEL +".sh"); 
       
       Runtime.getRuntime().exec("chmod 777 " + scr.getAbsolutePath() );
       
       FileWriter fwScript = new FileWriter( scr ); 
       fwScript.write("#!/bin/sh\n");
        
       for( int i = 0; i < d1ns.size(); i++ ) { 
           Vector<Double> ns = (Vector<Double>)d1ns.elementAt(i);
           Vector<Double> s = (Vector<Double>)d1s.elementAt(i);
           
           System.out.println( "i=" + i + " \t " +  ns.size() + " \t" + s.size() );
           
           File f = File.createTempFile("ks_test_" + i + ".", null, new File(".") );
           
           FileWriter fw = new FileWriter(f);

           for( int z = 0; z<ns.size(); z++ ) {
               fw.write( ns.elementAt(z) + "\t" + s.elementAt(z) +"\n" );
           }    
           
           fw.flush();
           fw.close();
           
           String para1 = " -v typ="+i;
           String cmd = "/usr/bin/awk -f /home/kamir/bin/bitOceanAnalysisCore/kstest.awk " + para1 + " " + f.getAbsolutePath() + " >> awk." + LABEL + ".dat.csv\n";
           fwScript.write( cmd );

           double[] borders = { -100, 2.5, 100 };
           for( int ii = 1; ii < borders.length; ii++ ){
               double min = borders[ii-1];
               double max = borders[ii];
               
               String para = "-v min=" + min + " -v max=" + max + " -v typ="+i;
               cmd = "/usr/bin/awk -f /home/kamir/bin/bitOceanAnalysisCore/kstest.awk " + para + " " + f.getAbsolutePath() + " >> awk." + LABEL + ".dat.csv\n";
                       
               fwScript.write( cmd );
            }    
            
       }
       
       fwScript.flush();
       fwScript.close();
       
        ProcessBuilder pb =
                new ProcessBuilder( "/home/kamir/bin/bitOceanAnalysisCore/scripte/" + scr.getName() ) ;
        try {
            final Process process = pb.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }



    }
    
    static Vector<Vector<Double>> d1ns = null; 
    static Vector<Vector<Double>> d1s = null; 

    static void setShuffledData(Vector<Vector<Double>> d1sA ) {
        d1s = d1sA;
    }

    static void setNonShuffledData(Vector<Vector<Double>> d1nsA ) {
        d1ns = d1nsA;
    }

    private static void test(Vector<Double> m1, Vector<Double> m2) {
        Vector<Vector<Double>> a = new Vector<Vector<Double>>();
        a.add(m1);

        Vector<Vector<Double>> b = new Vector<Vector<Double>>();
        b.add(m2);

        setNonShuffledData(a);
        setShuffledData(b);
        try {
            test2( "DEMO");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
