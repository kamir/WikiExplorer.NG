package experiments.crosscorrelation;

/**
 * 
 * The CCProzessor calculates possible pairs of correlations.
 * 
 * 1:n  getPartial ...
 * 
 * m:n  getAll ...
 * 
 * 
 **/


import com.cloudera.wikipedia.explorer.ResultManager;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedWriter;
import java.util.Enumeration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.wikipedia.corpus.extractor.NetDensityCalc;
  
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import research.ETH.ExtendedNodePairSFE;
import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;
 
/**
 *
 * @author kamir
 */
public class CCProzessor {
    
    public static int NORM = 0;

    public static HaeufigkeitsZaehlerDoubleSIMPLE getAll(
            Messreihe source, Vector<Messreihe> target,
            boolean shuffle, NetDensityCalc _ndc,
            String groupKEY, double ts,
            Vector<Messreihe> norms,
            BufferedWriter bw,
            int runID) {

        HaeufigkeitsZaehlerDoubleSIMPLE hz = initHZ(shuffle);

        for (int i = 0; i < target.size(); i++) {

            Messreihe a = source;
            Messreihe b = target.elementAt(i);

            if (norms != null) {
                b = b.divide_by(norms.elementAt(NORM));
            }

            ExtendedNodePairSFE np = new ExtendedNodePairSFE(a, b, null);

            try {
            
                KreuzKorrelation kk = np.calcCrossCorrelation();

                String extension = "{ \"norm\":\"" + NORM + "\" }";

                ResultManager.process(np, kk, bw, _ndc, hz, extension, groupKEY, runID);

            } 
            catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(CCProzessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        hz.calcWS();
        
        return hz;
    }

    /**
     *
     * @param source
     * @param target
     * @param shuffle
     * @param ts
     * @param cont für die VORHERSAGE ...
     * @param ndc
     * @param gK
     * @param bw
     * @param runID
     * @return
     */
    public static HaeufigkeitsZaehlerDoubleSIMPLE getPartial(
            Vector<Messreihe> source,
            Vector<Messreihe> target,
            boolean shuffle,
            double ts,
            Vector<ExtendedNodePairSFE> cont,
            NetDensityCalc ndc,
            String gK,
            BufferedWriter bw, int runID, boolean useRECOMMENDATION) {

        HaeufigkeitsZaehlerDoubleSIMPLE hz = initHZ(shuffle);
        hz.label = gK + " " + shuffle;

        Enumeration enA = source.elements();

        int z1 = source.size();
        int z2 = target.size();
        
        int i = 0;
        int j = 0;

        HashSet keys = new HashSet();

        while (enA.hasMoreElements()) {

            j++;
            
            Messreihe a = (Messreihe) enA.nextElement();
            
            Enumeration enB = target.elements();

            while (enB.hasMoreElements()) {

                i++;
                
//                System.out.println( "i: " + i + " => j: " + j  );

                Messreihe b = (Messreihe) enB.nextElement();

                ExtendedNodePairSFE np = new ExtendedNodePairSFE(a, b, keys);

                try {
                    
                    KreuzKorrelation kk = np.calcCrossCorrelation();
                    
                    if (kk != null) {
                    
//                        String s = np.getLinkStrength();

                        /**
                         * wir haben ein:
                         *
                         * ExtendedNodePairSFE np KreuzKorrelation kk
                         *
                         * bw => FINAL result file _ndc => filtered network
                         * generator hz => HISTOGRAM
                         */
                        String extension = "{}"; //{ \"norm\":\"" + NORM + "\" }";

                        // System.out.println( "***> " + s );
                        
                        ResultManager.process(np, kk, bw, ndc, hz, extension, gK, runID);

//                        if (useRECOMMENDATION) {
//                            cont.add(np);
//                        }

                    }
                } 
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //javax.swing.JOptionPane.showMessageDialog(null, "SOLL: " + (z1 * z2)  +"\nIST : " + i+"\nIST': " + j );

        hz.calcWS();
        return hz;
    }

    public static HaeufigkeitsZaehlerDoubleSIMPLE initHZ(boolean shuffle) {

        HaeufigkeitsZaehlerDoubleSIMPLE hz = new HaeufigkeitsZaehlerDoubleSIMPLE();
        hz.min = 0.0;
        hz.max = 10.0;
        hz.intervalle = 150;

        KreuzKorrelation.globalShuffle = shuffle;

        return hz;
    }

    public static KreuzKorrelation getPairComparison(Messreihe FD, Vector<Messreihe> grCN, boolean shuffle,
            Object ndc, String fn, double ts, Vector<Messreihe> norms) {


        KreuzKorrelation.GLdebug = false;

        KreuzKorrelation._defaultK = 7;
        KreuzKorrelation kk = null;

        ExtendedNodePairSFE np = new ExtendedNodePairSFE(FD, grCN.elementAt(0).divide_by(norms.elementAt(NORM)), null);

        try {

            kk = np.calcCrossCorrelation();

//            String s = np.getLinkStrength();

        } 
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(CCProzessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        KreuzKorrelation.GLdebug = false;

        return kk;
    }
}
