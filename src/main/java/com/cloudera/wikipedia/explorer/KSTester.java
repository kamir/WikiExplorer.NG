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
class KSTester {

    double min = -100;
    double max = 100;

    KSTester(double min, double max) {
        this.min = min;
        this.max = max;
    }

    KSTester() {
    }
    
    public String _test2Side( Vector<Double> a, Vector<Double> b, String LABEL, String RUN ) throws IOException {

       File scr = new File("/home/kamir/bin/WikiExplorer/bitOceanAnalysisCore/scripte/KS_SCRIPT." + RUN + "." + LABEL +".sh"); 
       
       Runtime.getRuntime().exec("chmod 777 " + scr.getAbsolutePath() );
       
       FileWriter fwScript = new FileWriter( scr ); 
       fwScript.write("#!/bin/sh\n");
        
           Vector<Double> ns = b;
           Vector<Double> s = a;
           
           if ( b.size() > a.size() ) { 
               ns = b;
               s = a;
           }
           else { 
               ns = a;
               s = b;
           }
       
           
           File f = File.createTempFile("ks_test_TMP.", null, new File("/home/kamir/bin/WikiExplorer/bitOceanAnalysisCore/tmp") );
           
           FileWriter fw = new FileWriter(f);

           // verschieden lange LISTEN !!!
           for( int z = 0; z<ns.size(); z++ ) {
               
               double value1 = ns.elementAt(z);
               if ( value1 < min ) value1 = min;               
               if ( value1 > max ) value1 = max;
               
               
               String val2 = "";
               if ( z >= s.size() ) { 
               
               }
               else { 
                    double value2 = s.elementAt(z);
                    if ( value2 < min ) value2 = min;
                    if ( value2 > max ) value2 = max;
                    val2 = "" + value2;
               } 
                              

               
               
               fw.write( value1  + "\t" + val2 +"\n" );
           }    
           
           fw.flush();
           fw.close();
           
           String para1 = "-v min=" + min + " -v max=" + max + " -v typ="+LABEL;

//           String para1 = " -v typ="+LABEL;
           String cmd = "/usr/bin/awk -f /home/kamir/bin/WikiExplorer/bitOceanAnalysisCore/kstest.awk " + para1 + " " + f.getAbsolutePath(); // + " >> awk." + LABEL + ".dat.csv\n";
           fwScript.write( cmd );
       
       fwScript.flush();
       fwScript.close();
       
       StringBuffer sb = new StringBuffer();
       
        ProcessBuilder pb =
                new ProcessBuilder( "/home/kamir/bin/WikiExplorer/bitOceanAnalysisCore/scripte/" + scr.getName() ) ;
        
//        sb.append( scr.canExecute() +"\t" );
//        sb.append( scr.getAbsolutePath() +"\t" );
        
        try {
            final Process process = pb.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                sb.append(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sb.toString();

    }
    
 
    
}
