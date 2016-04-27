/*
 * This Generator creates Time-Series for an experiment to analyze the influence 
 * LTM on correlation strength.
 *   
 *    5 different betas are used.
 * 
 *    1 chart to check individual series is created for valiation
 */
package experiments;

import com.cloudera.wikipedia.explorer.ResultManager;
import experiments.crosscorrelation.CCProzessor;

import org.apache.hadoopts.data.series.Messreihe;

import java.util.Vector;

import org.apache.hadoopts.chart.simple.MultiChart;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import m3.wikipedia.corpus.extractor.NetDensityCalc;

import metadata.ExperimentDescriptor;

import org.apache.hadoopts.app.thesis.LongTermCorrelationSeriesGenerator;
import org.apache.hadoopts.app.bucketanalyser.TSOperationControlerPanel;
import org.apache.hadoopts.hadoopts.buckets.BucketLoader;
import org.apache.hadoopts.hadoopts.core.TSBucket;

import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;

/**
 *
 * @author kamir
 */
@ExperimentDescriptor(
        author = "Mirko Kämpf",
        date = "23/02/2016",
        currentRevision = 2,
        lastModified = "30/03/2016",
        lastModifiedBy = "Mirko Kämpf",
        // Note array notation
        contributors = {"n.a."},
        topics = {"correlation properties", "link strength calculation"},
        tags = {"calibration"}
)
public class CorrelationPropertiesExperiment004 {

    static StringBuffer log = null;

    static Vector<Messreihe> testsA = null;
    static Vector<Messreihe> testsB = null; 
    static Vector<Messreihe> testsC = null;
    static Vector<Messreihe> testsD = null;

    static Vector<Messreihe> testsALL = null;

    static Vector<Messreihe> check1 = null;
    static Vector<Messreihe> check2 = null;

    public static void main(String[] args) throws Exception {
 
        testsA = new Vector<Messreihe>();
        testsB = new Vector<Messreihe>();
        testsC = new Vector<Messreihe>();
        testsD = new Vector<Messreihe>();

        testsALL = new Vector<Messreihe>();

        check1 = new Vector<Messreihe>();
        check2 = new Vector<Messreihe>();

 

        /**
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "EXP4";
        
        TSOperationControlerPanel.baseFolder = "/TSBASE/Exp004/WikiExplorer.NG_DATA";

        File f = new File(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
 
        /**
         * TODO:
         *
         * Refactor this component ...
         */
        NetDensityCalc ndc = new NetDensityCalc();

        String resultFileName = TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv";
        
        BufferedWriter bw = new BufferedWriter(new FileWriter( resultFileName ));

        double ts = -1000;
        boolean showLegend = true;
        int runID = 0;

        ResultManager.mode = 1;

        String market1 = "DAX2";
        String market2 = "SDAX";
        String market3 = "TECDAX";
        String market4 = "MDAX";
        
        String folder = "/TSBASE/EXP1/";

        String fn1 = "Components_" + market1 + "_Close__2003_2004_2005_2006_2007_2008_2009_2010_2011_2012_2013_2014.tsb.vec.seq";
        String fn2 = "Components_" + market2 + "_Close__2003_2004_2005_2006_2007_2008_2009_2010_2011_2012_2013_2014.tsb.vec.seq";
        String fn3 = "Components_" + market3 + "_Close__2003_2004_2005_2006_2007_2008_2009_2010_2011_2012_2013_2014.tsb.vec.seq";
        String fn4 = "Components_" + market4 + "_Close__2003_2004_2005_2006_2007_2008_2009_2010_2011_2012_2013_2014.tsb.vec.seq";

        TSBucket.useHDFS = false;
        BucketLoader loader1 = new BucketLoader();
        BucketLoader loader2 = new BucketLoader();
        BucketLoader loader3 = new BucketLoader();
        BucketLoader loader4 = new BucketLoader();

        loader1.loadBucketData(folder + fn1);
        loader2.loadBucketData(folder + fn2);
        loader3.loadBucketData(folder + fn3);
        loader4.loadBucketData(folder + fn4);
        
        testsA = normalizeAll( loader1.getBucketData());
        testsB = normalizeAll( loader2.getBucketData());
        testsC = normalizeAll( loader3.getBucketData());
        testsD = normalizeAll( loader4.getBucketData() );

//        MultiChart.open(testsA, true, "raw " + market1);
//        MultiChart.open(testsB, true, "raw " + market2);
//        MultiChart.open(testsC, true, "raw " + market3);
//        MultiChart.open(testsD, true, "raw " + market4);
        
        labelAllSeries( testsA, market1 );
        labelAllSeries( testsB, market2 );
        labelAllSeries( testsC, market3 );
        labelAllSeries( testsD, market4 );
        
        testsALL.addAll(testsA);
        testsALL.addAll(testsB);
        testsALL.addAll(testsC);
        testsALL.addAll(testsD);
        
        
        int[] u = { 25,  400, 600,  920, 1500, 1750, 2020, 2220, 2420, 2620, 286, 820, 1065, 1300, 2060};
        int[] o = { 275, 630, 830, 1050, 1740, 2030, 2220, 2420, 2620, 2900, 420, 930, 1300, 1540, 2265};
        String[] l = { "⇗", "⇗","⇗","⇗","⇗","⇗","⇗","⇗","⇗","⇗", "↓","↓","↓","↓","↓"};
        int[] c = { 0,0,0,0,0,0,0,0,0,0, 1,1,1,1,1};
        
        /**
         * Lets do the quarterly loops ...
         */
        Vector<Messreihe> vr1 = new Vector<Messreihe>();
        Vector<Messreihe> vr2 = new Vector<Messreihe>();
        
        ResultManager.mode = 2;
        
//            case 0 :  return np.getLinkA();
//            case 1 :  return np.getLinkB();
//            case 2 :  return np.getLinkC();
//            case 3 :  return np.getLinkD();

        for( int i=0; i < l.length; i++ ) {

            Vector<Messreihe> testsALLs = cut( testsALL, u[i], o[i] );
            
            Messreihe c1 = Messreihe.averageForAll(testsALLs);
            c1.setLabel( "AV " + l[i]+ " R" + i );
            
            if ( c[i] == 0 ) check1.add(c1);
            else check2.add( c1 );
            
//            MultiChart.open(testsALLs, false, TSOperationControlerPanel.label_of_EXPERIMENT + " " + (i*180) );
////
            HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsALLs, testsALLs, false, ts, null, ndc, "ALL" + "_RAW", bw, runID, false);
////
//////            HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsALLs, testsALLs, true, ts, null, ndc, "ALL" + "_SHUFFLE", bw, runID, false);
////
            Messreihe mr1 = r1.getHistogramNORM();
            mr1.setLabel( l[i]+ "R" + i);
            
////            Messreihe mr2 = r2.getHistogramNORM();
////            mr2.setLabel( l[i]+ "S" + i);
            
            if ( c[i] == 0 ) vr1.add(mr1);
            else vr2.add( mr1 );
            
//            vr.add(mr2);
        }
        /**
         * TODO:
         *
         * Tune the MultiChartPanel with right Metadata ...
         *
         */
        MultiChart.xRangDEFAULT_MIN = -1;
        MultiChart.xRangDEFAULT_MAX = 1;
        MultiChart.open(vr1, true, "UP ⇗ " + TSOperationControlerPanel.label_of_EXPERIMENT);
        MultiChart.open(vr2, true, "DOWNN ↓ " + TSOperationControlerPanel.label_of_EXPERIMENT);

        MultiChart.open(check1, true, TSOperationControlerPanel.label_of_EXPERIMENT + "_check");
        MultiChart.open(check2, true, TSOperationControlerPanel.label_of_EXPERIMENT + "_check");

        System.out.println(">>> Link-Creation-Mode:  " + ResultManager.mode);
        System.out.println(">>> ALL:  " + testsALL.size() );
        System.out.println(">>> FN :  " + resultFileName );
        
        
        

        bw.flush();
        bw.close();
    }

    private static void labelAllSeries(Vector<Messreihe> v, String l) {
        for( Messreihe mr : v)
            mr.setLabel( l + "___" + mr.getLabel() );
    }

    
    /***
     * 
     * 
     * Now we tune the preprocessin ...
     * 
     * @param v
     * @return 
     */
    private static Vector<Messreihe> normalizeAll(Vector<Messreihe> v) {
        
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        
        for( Messreihe mr : v) {
           
            // (A)
             Messreihe mr2 = mr.normalizeToStdevIsOne();
           
            // (B)
            
//            Messreihe mr2 = getLogReturn( mr );
           
           vmr.add(mr2);
           
        }
        
        return vmr;
        
    }

    private static Vector<Messreihe> cut(Vector<Messreihe> v, int von, int bis) {
        
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        
        for( Messreihe mr : v) {
           
            Messreihe mr2 = mr.cutOut(von, bis);
            
            System.out.println( mr2.getMinX() + "=>" + mr2.getMaxX() );
            vmr.add(mr2);
           
        }
        
        return vmr;    
    }

    private static Messreihe getLogReturn(Messreihe m) {
 
        
        Messreihe mr = new Messreihe();
        
        mr.setLabel( m.getLabel() + "_RelDiff");

        int len = m.xValues.size();

        // calc diffs from data
        double delta = 0.0;
        double logDelta = 0.0;
        double now = 0.0;
        
        double last = (Double)m.yValues.elementAt(0);
        
        for (int i = 0; i < len; i++) {
            
            now = (Double)m.yValues.elementAt(i);
            
            delta = now - last;
            
            logDelta = delta / last;
            
            last = now;
             
            mr.addValuePair(i, Math.log( logDelta + 1 ) );
        
        }

        return mr;
     
    
    }
    

}
