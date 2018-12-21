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
public class CorrelationPropertiesExperiment002 {

    static StringBuffer log = null;

    static Vector<TimeSeriesObject> testsA = null;
    static Vector<TimeSeriesObject> testsB = null; 
    static Vector<TimeSeriesObject> testsC = null;
    static Vector<TimeSeriesObject> testsD = null;

    static Vector<TimeSeriesObject> check = null;

    public static void main(String[] args) throws Exception {
 
        testsA = new Vector<TimeSeriesObject>();
        testsB = new Vector<TimeSeriesObject>();
        testsC = new Vector<TimeSeriesObject>();
        testsD = new Vector<TimeSeriesObject>();

 

        /**
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "EXP2";
        
        TSOperationControlerPanel.baseFolder = "/TSBASE/Exp001/WikiExplorer.NG_DATA";

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

        BufferedWriter bw = new BufferedWriter(new FileWriter(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv"));

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
        
        testsA = loader1.getBucketData();
        testsB = loader2.getBucketData();
        testsC = loader3.getBucketData();
        testsD = loader3.getBucketData();

        MultiChart.open(testsA, true, "raw " + market1);
        MultiChart.open(testsB, true, "raw " + market2);
        MultiChart.open(testsC, true, "raw " + market3);
        MultiChart.open(testsD, true, "raw " + market4);
        
        
                
//        HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsA, testsB, false, ts, null, ndc, market1 + "_RAW", bw, runID, false);
 
//        HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsB, true, ts, null, ndc, market1 + "_SHUFFLE", bw, runID, false);

//        HaeufigkeitsZaehlerDoubleSIMPLE r3 = CCProzessor.getPartial(testsB, testsC, true, ts, null, ndc, market2 + "_RAW", bw, runID, false);
        
//        HaeufigkeitsZaehlerDoubleSIMPLE r4 = CCProzessor.getPartial(testsB, testsC, true, ts, null, ndc, market2 + "_SHUFFLE", bw, runID, false);

//        HaeufigkeitsZaehlerDoubleSIMPLE r5 = CCProzessor.getPartial(testsC, testsA, true, ts, null, ndc, market3 + "_RAW", bw, runID, false);
        
//        HaeufigkeitsZaehlerDoubleSIMPLE r6 = CCProzessor.getPartial(testsC, testsA, true, ts, null, ndc, market3 + "_SHUFFLE", bw, runID, false);
        
//        Vector<TimeSeriesObject> vr = new Vector<TimeSeriesObject>();
//        vr.add(r1.getHistogramNORM());
//        vr.add(r2.getHistogramNORM());
//        vr.add(r3.getHistogramNORM());
//        vr.add(r4.getHistogramNORM());
//        vr.add(r5.getHistogramNORM());
//        vr.add(r6.getHistogramNORM());

        /**
         * TODO:
         *
         * Tune the MultiChartPanel with right Metadata ...
         *
         */
//        MultiChart.xRangDEFAULT_MIN = -1;
//        MultiChart.xRangDEFAULT_MAX = 1;
//        MultiChart.open(vr, true, TSOperationControlerPanel.label_of_EXPERIMENT);

        System.out.println(">>> Link-Creation-Mode:  " + ResultManager.mode);

        bw.flush();
        bw.close();

    }

}
