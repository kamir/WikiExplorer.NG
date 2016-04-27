/*
 * The final resultfile will be created here ...
 * 
 * Data for Histogramms are collected here ...
 * 
 */
package com.cloudera.wikipedia.explorer;

import java.io.BufferedWriter;
import java.io.IOException;
import org.openide.util.Exceptions;
import research.ETH.ExtendedNodePairSFE; 
import experiments.crosscorrelation.KreuzKorrelation;
import m3.wikipedia.corpus.extractor.NetDensityCalc;
import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;

/**
 *
 * @author kamir
 */
public class ResultManager {

    static boolean debug = false;
    
    /**
     *   2 = adjusted ...
     * 
     */
    public static int mode = 1;

    public static void process(
            ExtendedNodePairSFE np,
            KreuzKorrelation kk,
            BufferedWriter bw,
            NetDensityCalc _ndc,
            HaeufigkeitsZaehlerDoubleSIMPLE hz,
            String extension,
            String groupKEY,
            int runID) {

        counter++;

        double staerkeCOUNT1 = getStaerke(np, mode);   

        String result = np._toString2(groupKEY, counter);
        
        String result3 = np._toString3(groupKEY, counter);
        
//        String line = counter + "\t" + np.toString() + "\t:\t" + runID + "\t" + result + "\t" + extension + "\n";

        String lineSHORT = counter + "\t" + runID + "\t" + groupKEY + "\t" + result3 + "\n";
        
        try {
            
            bw.write(lineSHORT);
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            bw.flush();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (debug) {
            System.out.println(">>> NP : " + np.toString());
            System.out.println(">>> NP2: " + np._toString2(groupKEY, counter));
            System.out.println(">>> NP3: " + np._toString3(groupKEY, counter));
            System.out.println(">>> s  : " + staerkeCOUNT1);
            System.out.println(">>> la : " + np.getLinkA());
            System.out.println(">>> lB : " + np.getLinkB());
            System.out.println(">>> lC : " + np.getLinkC());
            System.out.println(">>> lD : " + np.getLinkD());
        }

        // Welche Linkstärke wird gesammelt ?
//        if ( staerkeCOUNT > 2.0 ) _ndc.collectLink(groupKEY, np);

        // Welche LinkStärke ist zu zeigen?
        hz.addData(staerkeCOUNT1);

    }
    
    static int counter = 0;

    public static void resetCounter() {
        counter = 0;        
    }

    private static double getStaerke(ExtendedNodePairSFE np, int mode) {
        switch ( mode ) {

            case 0 :  return np.getLinkA();
            case 1 :  return np.getLinkB();
            case 2 :  return np.getLinkC();
            case 3 :  return np.getLinkD();

        }
        return Double.NaN;
        
    }
    
}
