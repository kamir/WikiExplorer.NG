/**
 * Neue Version für den Einsatz in der Relevance-Check Analyse ...
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.json.*;

import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.statphys.eventsynchronisation.ESCalc;

import org.apache.hadoopts.statphys.eventsynchronisation.ESCalc2;
import org.apache.hadoopts.statphys.eventsynchronisation.experiments.ESMain;
import java.util.HashSet;
import java.util.Vector;
import org.openide.util.Exceptions;
import org.apache.hadoopts.statistics.DistributionTester;
import research.wikinetworks.NodePair;
import experiments.crosscorrelation.KreuzKorrelation;
import experiments.linkstrength.CCFunction;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

public class ExtendedNodePairSFE extends NodePair {
    
    public static double NONEAVAILABLEVALUE = -100.0;
    public static String currentStudy = "XYZ";

    private static double[] validate(double[] sDep) {
        for( int i = 0; i < sDep.length ; i++ ) {
            if ( Double.isInfinite( sDep[i] ) ) sDep[i] = NONEAVAILABLEVALUE; 
            else if ( Double.isNaN( sDep[i] ) ) sDep[i] = NONEAVAILABLEVALUE; 
        }
        return sDep;
    }
    
    private static double validate(double sDep) {
        if ( Double.isInfinite( sDep ) ) sDep = NONEAVAILABLEVALUE; 
        else if ( Double.isNaN( sDep ) ) sDep = NONEAVAILABLEVALUE; 
        return sDep;
    }

    public Messreihe mrA = null;
    public Messreihe mrB = null;
    public int[] esAint = null;
    public int[] esBint = null;
    public Messreihe esA = null;
    public Messreihe esB = null;
    
    
    public Messreihe getEsA() {
        return esA;
    }

    public void setEsA(Messreihe esA) {
        this.esA = esA;
    }

    public Messreihe getEsB() {
        return esB;
    }

    public void setEsB(Messreihe esB) {
        this.esB = esB;
    }

    public ExtendedNodePairSFE(String as, String bs, String tp) {
        s = as;
        t = bs;
        type = tp;
    }
    
    public double getActivityB(){
        return mrB.summeY();
    } 
    
    public double getActivityA(){
        return mrA.summeY();
    } 


    public ExtendedNodePairSFE(Messreihe a, Messreihe b, HashSet keys) {
        
        mrA = a;
        mrB = b;

        String key1 = a.getLabel() + "_" + b.getLabel();
        String key2 = b.getLabel() + "_" + a.getLabel();

        s = mrA.getLabel();
        t = mrB.getLabel();
        
        if (keys != null) {

            if (keys.contains(key1) || keys.contains(key2)) {
                mrA = null;
                mrB = null;
            } 
            else {
                // System.out.println( ">>> " + keys.size() + " ==> " + a.getLabel() + "\t" + b.getLabel() + "\t" );
                keys.add(key1);
                keys.add(key2);

            }
            
        }
    }

    public void normalize(boolean n) {
        normalizeResult = n;
    }
    boolean normalizeResult = false;

    public double[] calcEventSynchronisation() {
        StringBuffer sb = new StringBuffer();
        double[] Qq = calcES(esA, esB, sb);
        //System.out.println(sb);
        return Qq;
    }

    /**
     * @param args the command line arguments
     */
    private double[] calcES(Messreihe a, Messreihe b, StringBuffer sb) {

        double[] esResult = new double[2];
        esResult[0] = -10;
        esResult[1] = -10;
                
        
        if (sb == null) {
            sb = new StringBuffer();
        }

        ESCalc2.debug = false;

        int[] r1;
        int[] r2;


        if (a == null) {
            r1 = esAint;
        } else {
            r1 = a.getYData_as_INT();
        }

        if (b == null) {
            r2 = esBint;
        } else {
            r2 = b.getYData_as_INT();
        }

        if (doShuffle) {
            for (int i = 0; i < 150; i++) {
                stdlib.StdRandom.shuffle(r2);
                stdlib.StdRandom.shuffle(r1);
            }
        }


//        ESCalc2.checkRows(r1, r2, "Events");

        int[] r1ET;
        int[] r2ET;

        try {
            r1ET = ESCalc2.getEventIndexSeries(r1);
            r2ET = ESCalc2.getEventIndexSeries(r2);

            esResult = ESCalc2.calcES(r1ET, r2ET);
            sb.append(">>> Q=" + esResult[0] + "\n>>> q=" + esResult[1] + "\n");//        System.out.println(sb.toString());

            double z1 = r1ET.length;
            double z2 = r2ET.length;
            double ratio = 1.0;

            if (z1 > z2 && z2 != 0.0) {
                ratio = z1 / z2;
            } else if (z2 > z1 && z1 != 0.0) {
                ratio = z2 / z1;
            }


            if (normalizeResult && esResult[0] != -2.0) {
                double f = 0.7 / Math.pow(5.0 + ratio, 0.6);
                double q1 = esResult[0] / f;
                esResult[0] = q1;
    //            System.out.println("norm: (RATIO=" + ratio + ";  f=" + f + "; q1=" + q1);
            };
        
        
        }
        catch(Exception ex) { 
            sb.append("PROBLEM: " + ex.getMessage() + "\n");
        }
        
        


        return esResult;
    }
    /**
     * Distribution of the values of the inputdata can be checked.
     *
     * @return KreuzKorrelation
     *
     * @throws Exception
     */
    static boolean chechDistributionForInputData = false;

    public double[] calcMutualINFORMATION() throws Exception {

        // local reference ti the KK object is stored in the ExtendedNodePairSFE
        stats = null;
        if (mrA != null && mrB != null) {

            if (chechDistributionForInputData) {
                if (MainETH.dtA == null) {
                    MainETH.dtA = DistributionTester.getDistributionTester();
                }
                if (MainETH.dtB == null) {
                    MainETH.dtB = DistributionTester.getDistributionTester();
                }

                double[] d1 = mrA.getYData();
                double[] d2 = mrB.getYData();

                shapA = MainETH.dtA.testDatasetS(d1);
                shapB = MainETH.dtB.testDatasetS(d2);
            } 
            else {
                // System.err.println(">>> NO DISTRIBUTION TEST FOR INPUT ROWS!!!");
            }


            stats = calcInformationMeasures( mrA, mrB );
            
        }
        return stats;
    }
    
    public static double[] calcInformationMeasures( Messreihe mr1, Messreihe mr2) {
        double[] sDep = null;
        try {
            double[] y1 = mr1.getYData();
            double[] y2 = mr2.getYData();

            int z = mr1.xValues.size();

            sDep = new double[9];

            double entr1 = EntropyToolJIDTDiscrete.calcEntropyDISCRETE(mr1, z);
            double entr2 = EntropyToolJIDTDiscrete.calcEntropyDISCRETE(mr2, z);
            
            sDep[0] = entr1;
            sDep[1] = entr2;
            
            double h1 = entr1;
            double h2 = entr2;
            
            double back[] = EntropyToolJIDTDiscrete.calcMutualInformationDISCRETE(mr1, mr2, z);

            double info = back[0];
            
            double q1 = info / h1;
            double q2 = info / h2;

            sDep[2] = info;
            sDep[3] = q1;
            sDep[4] = q2;
            
            sDep[5] = back[1];
            sDep[6] = back[2];
            sDep[7] = back[3];
            sDep[8] = back[4];
            
            sDep = validate( sDep );

//        System.out.println(  "> H(x) = " + h1 );
//        System.out.println(  "> H(y) = " + h2 );
            if ( debug ) 
                System.out.println(  "> I(x,y) = H(x) + H(y) - H(x,y) = " + info );
//        System.out.println(  "> H(x,y) = -( I(x,y) - H(x) - H(y) ) = " + (( info - h1 - h2 ) -1.0 ) );
        
            return sDep;
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return sDep;
    }
    
    public static boolean debug = false;
    
    public KreuzKorrelation calcCrossCorrelation() throws Exception {

        // local reference ti the KK object is stored in the ExtendedNodePairSFE
        
        kk = null;
        
        if (mrA != null && mrB != null) {

            if (chechDistributionForInputData) {
                
                if (MainETH.dtA == null) {
                    MainETH.dtA = DistributionTester.getDistributionTester();
                }
                
                if (MainETH.dtB == null) {
                    MainETH.dtB = DistributionTester.getDistributionTester();
                }

                double[] d1 = mrA.getYData();
                double[] d2 = mrB.getYData();

//                shapA = MainETH.dtA.testDatasetS(d1);
//                shapB = MainETH.dtB.testDatasetS(d2);
                
            } 
            else {
                //System.err.println(">>> NO DISTRIBUTION TEST FOR INPUT ROWS!!!");
            }

            kk = KreuzKorrelation.calcKR(mrA, mrB, false, false);
        }
        return kk;
    }
    
    public double[] stats = null;
    public String shapA = "0.0";
    public String shapB = "0.0";

    /**
     * @return mrA.getLabel() : mrB.getLabel() : shapA : shapB
     */
    public String toString() {
        String line = mrA.getLabel() + "\t" + mrB.getLabel() + "\t" + "\t" + shapA + "\t" + shapB;
        return line;
    }
    

    /**
     * 
     * Beside Cross-Correlation also the Mutual Information
     * can be calculated.
     * 
     * @return 
     */
    public String getMutualInforamtionAsJSON() throws JSONException {

        JSONObject o = new JSONObject();
        
        o.put( "mi_h1", this.stats[0] );
        o.put( "mi_h2", this.stats[1] );
        o.put( "mi_info", this.stats[2] );
        
//        if ( this.stats[3] == Double.NaN ) this.stats[3] = -10.0;
//        o.put( "q1", this.stats[3] );
//        
//        if ( this.stats[4] == Double.NaN ) this.stats[4] = -10.0;
//        o.put( "q2", this.stats[4] );
        
        o.put( "mi_mean", this.stats[5] );
        o.put( "mi_std", this.stats[6] );
        o.put( "mi_tscore", this.stats[7] );
        o.put( "mi_p", this.stats[8] );
        
        return o.toString();
    }
    
    /**
     * Link Descriptor as JSON for storage in a Hive-Table
     * 
     * @param type
     * @param id
     * @return 
     */
    public String _getNodePairNamesAsJSON(String type, int id) throws JSONException {

        String s = extractCore(mrA.getLabel());
        String t = extractCore(mrB.getLabel());
        String typ = type;
        String linkid = "" + id;
        
        String label = "(" + s + ";" + t + ")";
        
        JSONObject o = new JSONObject();
        
        o.put( "np_source", s );
        o.put( "np_target", t );
        o.put( "np_typ", typ );
        o.put( "np_linkid", linkid );
        
        return o.toString();
    }
    
    public String _toString5(String type, int id) {

        String s = extractCore(mrA.getLabel());
        String t = extractCore(mrB.getLabel());
        String typ = type;
        String linkid = "" + id;
        String label = "(" + s + ";" + t + ")";
        
        String weights = "weights";
        if ( kk != null )         
            weights = kk.getResultLine2();

        return (s + "\t" + t + "\t" + typ + "\t" + linkid + "\t" + label + "\t" );

    }
    
    public String _toString2(String type, int id) {

        String s = extractCore(mrA.getLabel());
        String t = extractCore(mrB.getLabel());
        String typ = type;
        String linkid = "" + id;
        String label = "(" + s + ";" + t + ")";
        
        String weights = "weights";
        if ( kk != null )         
            weights = kk.getResultLine2();

        return (s + "\t" + t + "\t" + typ + "\t" + linkid + "\t" + label + "\t" + getLinkA() + "\t" + getLinkB() + "\t" + getLinkC() + "\t" + getLinkD() );

    };
    
     public String getCCStrengthsAsJSON() throws JSONException {
       JSONObject o = new JSONObject();
        o.put( "cc_linkA", getLinkA() );
        o.put( "cc_linkB", getLinkB() );
        o.put( "cc_linkC", getLinkC() );
        o.put( "cc_linkD", getLinkD() );
        return o.toString();
    }
    
    public String _toString3(String type, int id) {

        String s = extractCore(mrA.getLabel());
        String t = extractCore(mrB.getLabel());
        
        String names = "{"+s+"}{"+t+"}";
        return ( getLinkA() + "\t" + getLinkB() + "\t" + getLinkC() + "\t" + getLinkD() + "\t" + names );

    }
  
    double linkA = 0.0;
    double linkB = 0.0;
    double linkC = 0.0;
    double linkD = 0.0;
        
    boolean doShuffle = false;
    public String s = null;
    public String t = null;
    public String type = null;
    
//    /**
//     * Produce two link strength values as JSON Strings.
//     * 
//     * @return 
//     */
//    public String getLinkStrength() {
//
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//        linkA = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        String s = "\"link_mode_NORMALIZED\":\"" + linkA + "\"";
//
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//        linkB = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        s = s + ", \"mode_CC_TAU_0\":\"" + linkB + "\"";
//
//        return s;
//    }

//    public double getLinkA() {
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//        linkA = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        return validate( linkA );
//    }

    public double getLinkA() {
        return CCFunction.calcStrength_VERSION_A( this.kk );
    }
    public double getLinkB() {
        return CCFunction.calcStrength_VERSION_B( this.kk );
    }
    public double getLinkC() {
        return CCFunction.calcStrength_VERSION_C(kk, false);
    }
    public double getLinkD() {
        return CCFunction.calcStrength_VERSION_D(kk);
    }
    

    
    
//    public double getLinkB() {
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//        linkB = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        return validate( linkB );
//    }
    
//    public double getLinkD() {
//        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CLEAN_ONE_PEAK;
//        linkD = CheckInfluenceOfSingelPeaks.calcStrength(kk);
//        return validate( linkD );
//    }


    

    public void show() {
        
        Vector<Messreihe> v = new Vector<Messreihe>();
        v.add(mrA);
        v.add(mrB);

        MultiChart.open(v, mrA.label, "t", "y(t)", true);
    }

    public static String extractCore(String label) {
        String s = label.replace("_log10", "");
        return label;
    }

    public void shuffle(boolean s) {
        doShuffle = s;
    }

    public String getStaticLinkLine() {
        return "***** SLLN:   " + s + "\t" + t + "\t" + type;
    }

    public String getId(String CONTEXT) {
        String key1 = mrA.getLabel();
        String key2 = mrB.getLabel();
        return CONTEXT + "_" + key1 + "_" + key2;
    }

    public String getFCCAsJSON() {
        return this.kk.getResultLineAsJSON();
    }
    
}
