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

import org.apache.hadoopts.data.series.TimeSeriesObject;

import java.util.Vector;

import org.apache.hadoopts.chart.simple.MultiChart;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import m3.wikipedia.corpus.extractor.NetDensityCalc;

import metadata.ExperimentDescriptor;

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
public class CorrelationPropertiesExperiment003 {

    static StringBuffer log = null;

    static Vector<TimeSeriesObject> testsA = null;
    static Vector<TimeSeriesObject> testsB = null; 
    static Vector<TimeSeriesObject> testsC = null;
    static Vector<TimeSeriesObject> testsD = null;

    static Vector<TimeSeriesObject> testsALL = null;

    static Vector<TimeSeriesObject> check = null;

    public static void main(String[] args) throws Exception {
 
        testsA = new Vector<TimeSeriesObject>();
        testsB = new Vector<TimeSeriesObject>();
        testsC = new Vector<TimeSeriesObject>();
        testsD = new Vector<TimeSeriesObject>();

        testsALL = new Vector<TimeSeriesObject>();

 

        /**
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "EXP3";
        
        TSOperationControlerPanel.baseFolder = "/TSBASE/Exp003/WikiExplorer.NG_DATA";

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

        MultiChart.open(testsA, true, "raw " + market1);
        MultiChart.open(testsB, true, "raw " + market2);
        MultiChart.open(testsC, true, "raw " + market3);
        MultiChart.open(testsD, true, "raw " + market4);
        
        labelAllSeries( testsA, market1 );
        labelAllSeries( testsB, market2 );
        labelAllSeries( testsC, market3 );
        labelAllSeries( testsD, market4 );
        
        testsALL.addAll(testsA);
        testsALL.addAll(testsB);
        testsALL.addAll(testsC);
        testsALL.addAll(testsD);
        
        
        /**
         * Lets do the quarterly loops ...
         */
        Vector<TimeSeriesObject> vr = new Vector<TimeSeriesObject>();

        for( int i=0; i < 20; i++ ) {

            Vector<TimeSeriesObject> testsALLs = cut( testsALL, i * 180, i * 180 + 180 );
            
            MultiChart.open(testsALLs, false, TSOperationControlerPanel.label_of_EXPERIMENT + " " + (i*180) );

            HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsALLs, testsALLs, false, ts, null, ndc, "ALL" + "_RAW", bw, runID, false);

            HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsALLs, testsALLs, true, ts, null, ndc, "ALL" + "_SHUFFLE", bw, runID, false);

            TimeSeriesObject mr1 = r1.getHistogramNORM();
            mr1.setLabel( "R" + i);
            
            TimeSeriesObject mr2 = r2.getHistogramNORM();
            mr2.setLabel( "S" + i);
            
            
            vr.add(mr1);
            vr.add(mr2);
        }
        /**
         * TODO:
         *
         * Tune the MultiChartPanel with right Metadata ...
         *
         */
        MultiChart.xRangDEFAULT_MIN = -1;
        MultiChart.xRangDEFAULT_MAX = 1;
        MultiChart.open(vr, true, TSOperationControlerPanel.label_of_EXPERIMENT);

        System.out.println(">>> Link-Creation-Mode:  " + ResultManager.mode);
        System.out.println(">>> ALL:  " + testsALL.size() );
        System.out.println(">>> FN :  " + resultFileName );
        
        
        

        bw.flush();
        bw.close();
    }

    private static void labelAllSeries(Vector<TimeSeriesObject> v, String l) {
        for( TimeSeriesObject mr : v)
            mr.setLabel( l + "___" + mr.getLabel() );
    }

    private static Vector<TimeSeriesObject> normalizeAll(Vector<TimeSeriesObject> v) {
        
        Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();
        
        for( TimeSeriesObject mr : v) {
           
            TimeSeriesObject mr2 = mr.normalizeToStdevIsOne();
           vmr.add(mr2);
           
        }
        
        return vmr;
        
    }

    private static Vector<TimeSeriesObject> cut(Vector<TimeSeriesObject> v, int von, int bis) {
        
        Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();
        
        for( TimeSeriesObject mr : v) {
           
            TimeSeriesObject mr2 = mr.cutOut(von, bis);
            
            System.out.println( mr2.getMinX() + "=>" + mr2.getMaxX() );
            vmr.add(mr2);
           
        }
        
        return vmr;    
    }
    

}
