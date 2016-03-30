/***
 *
 *  Calculates EventSynchronisation for random Event-TimeSeries
 * 
 ***/
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.FileWriter;
import org.apache.hadoopts.statphys.eventsynchronisation.ESCalc;
import org.apache.hadoopts.statphys.eventsynchronisation.ESCalc2;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import research.ETH.ExtendedNodePairSFE;
import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;
import m3.tscache.TSCache;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class ESProcessor2 {
    
    // Normalize ES-Synch Values ...
    static public boolean normalize = true; 
  
    
    static int errors = 0;
    
    
    public static HaeufigkeitsZaehlerDoubleSIMPLE[] getAll(Messreihe source, Vector<Messreihe> target, boolean shuffle, FileWriter fw, String linkGroup) {
        
        int lowDens = 0;
        int normDensity = 0;
        
        HaeufigkeitsZaehlerDoubleSIMPLE[] hz = new HaeufigkeitsZaehlerDoubleSIMPLE[2];
        
        HaeufigkeitsZaehlerDoubleSIMPLE hz1 = initHZ(shuffle);
        HaeufigkeitsZaehlerDoubleSIMPLE hz2 = initHZ(shuffle);
        
        hz[0] = hz1;
        hz[1] = hz2;  
        
        if ( source != null ) {
        
        for (int i = 0; i < target.size(); i++) {
            
            ExtendedNodePairSFE np = new ExtendedNodePairSFE(source, target.elementAt(i), null );
            
            if ( normalize ) np.normalize( true );
            
            boolean goOn = _loadEventDataFromWIKIAPI( np );
            
            if ( shuffle ) np.shuffle( true );
            
            try {
                
                if( goOn ) {
                    
                    double[] Qq=np.calcEventSynchronisation();
                    
                    if ( Qq[0] != -2 ) {
                    
                        
                        String line = linkGroup + "\t" + shuffle + "\t" + np.mrA.getLabel() + "\t" + np.mrB.getLabel() + "\t" + Qq[0] + "\t" + Qq[1] + "\n";
                        
                        fw.write(  line );
                        hz1.addData( Qq[0] );
                        hz2.addData( Qq[1] );
                        
                        normDensity++;
                    }    
                    else { 
                        lowDens++;
                    }
                }

            } catch (Exception ex) {
                errors++;
                //ex.printStackTrace();
                Logger.getLogger(CCProzessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        hz1.calcWS();
        hz2.calcWS();
        
        System.out.println("*** # of row with no id     : " + noIdentifier );
        System.out.println("*** # of ERRORS             : " + errors );
        System.out.println("*** # of low dens row-pairs      : " + lowDens );
        System.out.println("*** # of pairs with norm density : " + normDensity  );
      
        
        }        
        else { 
            System.out.println("*** SOURCE = NULL : " + (source == null) );
        }
        return hz;
    }

//    static HaeufigkeitsZaehlerDoubleSIMPLE getPartial(Vector<Messreihe> source, Vector<Messreihe> target, boolean shuffle, double ts, Vector<ExtendedNodePairSFE> cont) {
//
//        HaeufigkeitsZaehlerDoubleSIMPLE hz = initHZ(shuffle);
//
//        Enumeration enA = source.elements();
//               
//        int z1 = source.size();
//        int z2 = target.size();
//        
//   
//        int i = 0;
//        int j = 0;
//        
//        HashSet keys = new HashSet();
//        
//        while (enA.hasMoreElements()) {
//            
//            Messreihe a = (Messreihe) enA.nextElement();
//            Enumeration enB = target.elements();
//            
//            while (enB.hasMoreElements()) {
//                
//                i++;
//                
//                Messreihe b = (Messreihe) enB.nextElement();
//                
//                ExtendedNodePairSFE np = new ExtendedNodePairSFE(a, b, keys);
//                
//                try {
//                    KreuzKorrelation kk = np.calcCrossCorrelation();
//                    if( kk != null ) {
//                        String s = np.getLinkStrength();
//                        
//                        
//                        System.out.println(">>> NP : " + np.toString());
//                        System.out.println(">>> s  : " + s);
//                        System.out.println(">>> lB : " + np.getLinkB());
//
//                        if ( np.getLinkB() > ts ) {
//                            cont.add(np);
//                        }
//                        hz.addData(np.getLinkB());
//                    }
//                } 
//                catch (Exception ex) {
//                    ex.printStackTrace();
////                        Logger.getLogger(CCProzessor.class.getName()).log(Level.SEVERE, null, ex);
//
//                }
//            }
//        }
//        
//        //javax.swing.JOptionPane.showMessageDialog(null, "SOLL: " + (z1 * z2)  +"\nIST : " + i+"\nIST': " + j );
//        
//
//        hz.calcWS();
//        return hz;
//    }

    public static HaeufigkeitsZaehlerDoubleSIMPLE initHZ(boolean shuffle) {

        HaeufigkeitsZaehlerDoubleSIMPLE hz = new HaeufigkeitsZaehlerDoubleSIMPLE();
        hz.min = -1.0;
        hz.max = 1.0;
        hz.intervalle = 100;
        
//        hz.debug = true;

        // CONFIG ESCalc here ...
        return hz;
    }

    
    public static int noIdentifier = 0;
    
    /**
     * returns true, if the load procedure was fine.
     * 
     * @param np
     * @return 
     */
    private static boolean _loadEventDataFromWIKIAPI(ExtendedNodePairSFE np) {
        
        String wna = np.mrA.getIdentifier();
        String wnb = np.mrB.getIdentifier();
        
        if( wna == null || wnb == null ) {
//            noIdentifier++;
//            return false;
            
            int la = np.mrA.getLabel().length();
            int lb = np.mrB.getLabel().length();
            
            
            wna = np.mrA.getLabel().substring(0,la-7);
            wnb = np.mrB.getLabel().substring(0,lb-7);;
        }
        
//        System.out.println( ">>> load edits now ... (" + wna + "," + wnb + ")" );
        
        // javax.swing.JOptionPane.showMessageDialog(null, ">>> load edits now ... (" + a + "," + b + ")"  );
        
//        np.esAint = ESCalc.createEventTS(2000, 100); 
//        np.esBint = ESCalc.createEventTS(2000, 100); 
        
        try {
        
            TSCache.RAM = true;
            TSCache.WebHBase = true;

            TSCache.debug = true;
            
            Messreihe esA = WikiHistoryExtraction2.loadPageHistory( new WikiNode( wna ) );
            Messreihe esB = WikiHistoryExtraction2.loadPageHistory( new WikiNode( wnb ) );
        
            np.setEsA(esA);
            np.setEsB(esB);
            
            return true;
            
//            double[] dat = np.calcEventSynchronisation();
//            System.out.println("****** " +  dat[0] + "\t" + dat[1] );
        } 
        catch (IOException ex) {
            Logger.getLogger(ESProcessor2.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
        catch (Exception ex) {
            Logger.getLogger(ESProcessor2.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static HaeufigkeitsZaehlerDoubleSIMPLE[] getPartial(Vector<Messreihe> source, Vector<Messreihe> target, boolean shuffle, double ts, Vector<ExtendedNodePairSFE> suggestA, FileWriter fw, String linkGroup) {

        HaeufigkeitsZaehlerDoubleSIMPLE[] hz = new HaeufigkeitsZaehlerDoubleSIMPLE[2];
        
        HaeufigkeitsZaehlerDoubleSIMPLE hz1 = initHZ(shuffle);
        HaeufigkeitsZaehlerDoubleSIMPLE hz2 = initHZ(shuffle);
        
        hz[0] = hz1;
        hz[1] = hz2;  
        
        Enumeration enA = source.elements();
               
        int z1 = source.size();
        int z2 = target.size();
        
        int lowDens=0;
        int normDensity=0;
   
        int i = 0;
        int j = 0;
        
        HashSet keys = new HashSet();
        
        for (j = 0; j < source.size(); j++) {
            
            
            
            for (i = 0; i < target.size(); i++) {
                            
                ExtendedNodePairSFE np = new ExtendedNodePairSFE(source.elementAt(j), target.elementAt(i), null );
            
                boolean goOn = _loadEventDataFromWIKIAPI( np );
                
                if ( shuffle ) np.shuffle( true );
            
                if ( normalize ) np.normalize( true );
                
                try {

                    if( goOn ) {
                        double[] Qq=np.calcEventSynchronisation();
                        
                    if ( Qq[0] != -2 ) {
  
                        String line = linkGroup + "\t" + shuffle + "\t" + np.mrA.getLabel() + "\t" + np.mrB.getLabel() + "\t" + Qq[0] + "\t" + Qq[1] + "\n";
                        
                        fw.write(  line );
                        hz[0].addData( Qq[0] );
                        hz[1].addData( Qq[1] );
                        
                        normDensity++;
                    }    
                    else { 
                        lowDens++;
                    }

                    }

                } catch (Exception ex) {
                    errors++;
                    //ex.printStackTrace();
                    Logger.getLogger(CCProzessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
      
        System.out.println("# of pairs with low density  : " + lowDens  );
        System.out.println("# of pairs with norm density : " + normDensity  );
      
        hz[0].calcWS();
        hz[1].calcWS();
                
        return hz;
    }
    


}
