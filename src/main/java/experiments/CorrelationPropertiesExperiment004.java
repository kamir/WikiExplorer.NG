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
import java.text.DecimalFormat;

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
public class CorrelationPropertiesExperiment004 {

    static StringBuffer log = null;

    static Vector<TimeSeriesObject> testsA = null;
    static Vector<TimeSeriesObject> testsB = null;
    static Vector<TimeSeriesObject> testsC = null;
    static Vector<TimeSeriesObject> testsD = null;

    static Vector<TimeSeriesObject> testsALL = null;

    static Vector<TimeSeriesObject> check1 = null;
    static Vector<TimeSeriesObject> check2 = null;

    static int tsPrepMode = 0;

    public static void main(String[] args) throws Exception {

        int mode = 7;

        testsA = new Vector<TimeSeriesObject>();
        testsB = new Vector<TimeSeriesObject>();
        testsC = new Vector<TimeSeriesObject>();
        testsD = new Vector<TimeSeriesObject>();

        testsALL = new Vector<TimeSeriesObject>();

        check1 = new Vector<TimeSeriesObject>();
        check2 = new Vector<TimeSeriesObject>();

        /**
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "EXP4";

        TSOperationControlerPanel.baseFolder = "/TSBASE/Exp004/WikiExplorer.NG_DATA";

        File f = new File(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        String resultFileName = TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv";

        double ts = -1000;
        boolean showLegend = true;

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

        if (mode == 1) {
            ResultManager.mode = 0;                            // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 1; // 2-1 TS-PREP-MODE
        }

        if (mode == 2) {
            ResultManager.mode = 0; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 2; // 2-1 TS-PREP-MODE
        }
        if (mode == 3) {

            ResultManager.mode = 2; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 1; // 2-1 TS-PREP-MODE
        }
        if (mode == 4) {

            ResultManager.mode = 2; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 2; // 2-1 TS-PREP-MODE
        }

        if (mode == 5) {

            ResultManager.mode = 0; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 0; // 2-1 TS-PREP-MODE
        }
        
        if (mode == 6) {

            ResultManager.mode = 2; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 0; // 2-1 TS-PREP-MODE
        }

        if (mode == 7) {

            ResultManager.mode = 0; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 3; // 2-1 TS-PREP-MODE
        }
        
        if (mode == 8) {

            ResultManager.mode = 2; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 3; // 2-1 TS-PREP-MODE
        
        }

        if (mode == 9) {

            ResultManager.mode = 1; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 0;  
        }
        
        if (mode == 10) {

            ResultManager.mode = 1; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 1;  
        }

        if (mode == 11) {

            ResultManager.mode = 1; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 2;  
        }
        
        if (mode == 12) {

            ResultManager.mode = 1; // LINK TYPE 0-2
            CorrelationPropertiesExperiment004.tsPrepMode = 3;  
            
        }
        
        
        
        
        boolean doShuffle = true;

//        int max = l.length;
        testsA = normalizeAll(loader1.getBucketData());
        testsB = normalizeAll(loader2.getBucketData());
        testsC = normalizeAll(loader3.getBucketData());
        testsD = normalizeAll(loader4.getBucketData());

//        MultiChart.open(testsA, true, "raw " + market1);
//        MultiChart.open(testsB, true, "raw " + market2);
//        MultiChart.open(testsC, true, "raw " + market3);
//        MultiChart.open(testsD, true, "raw " + market4);
        labelAllSeries(testsA, market1);
        labelAllSeries(testsB, market2);
        labelAllSeries(testsC, market3);
        labelAllSeries(testsD, market4);

        testsALL.addAll(testsA);
        testsALL.addAll(testsB);
        testsALL.addAll(testsC);
        testsALL.addAll(testsD);

        int[] u = {25, 2060, 400, 600, 920, 1500, 1750, 2020, 2220, 2420, 2620, 286, 820, 1065, 1300};
        int[] o = {275, 2265, 630, 830, 1050, 1740, 2030, 2220, 2420, 2620, 2900, 420, 930, 1300, 1540};
        String[] l = {"⇗", "↓", "⇗", "⇗", "⇗", "⇗", "⇗", "⇗", "⇗", "⇗", "↓", "↓", "↓", "↓", "↓"};
        int[] c = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1};

        /**
         * Lets do the quarterly loops ...
         */
        Vector<TimeSeriesObject> vr1 = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> vr2 = new Vector<TimeSeriesObject>();

//        int max = 1;
        int max = u.length;

        NetDensityCalc ndc = null;

        MultiChart.setDefaultRange = false;
        MultiChart.df1 = new DecimalFormat("0.00");
        MultiChart.df2 = new DecimalFormat("0.00");

        for (int i = 0; i < max; i++) {

            int runID = i;

            String fn11 = "/TSBASE/networks/" + l[i] + "gephi_" + tsPrepMode + "_" + ResultManager.mode + "_" + runID;

            File f11 = new File(fn11);
            f11.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(fn11 + "/" + l[i] + "gephi_" + tsPrepMode + "_" + ResultManager.mode + "_" + runID + "_list.tsv"));

            ndc = new NetDensityCalc(fn11);

            Vector<TimeSeriesObject> testsALLs = cut(testsALL, u[i], o[i]);

            TimeSeriesObject c1 = TimeSeriesObject.averageForAll(testsALLs);
            c1.setLabel("AV " + l[i] + " R" + i);

            if (c[i] == 0) {
                check1.add(c1);
            } else {
                check2.add(c1);
            }

//            // MultiChart.open(testsALLs, false, TSOperationControlerPanel.label_of_EXPERIMENT + " " + u[i] + " - " + o[i] + " " + tsPrepMode + "_" + ResultManager.mode + "_" + runID);
//            HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsALLs, testsALLs, false, ts, null, ndc, "ALL" + "_RAW", bw, runID, false);
//
//            TimeSeriesObject mr1 = r1.getHistogramNORM();
//            mr1.setLabel(l[i] + "R" + i);
//
//            if (c[i] == 0) {
//                vr1.add(mr1);
//            } else {
//                vr2.add(mr1);
//            }

//            if (doShuffle) {
//
//                HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsALLs, testsALLs, true, ts, null, ndc, "ALL" + "_SHUFFLE", bw, runID, false);
//                TimeSeriesObject mr2 = r2.getHistogramNORM();
//
//                mr2.setLabel(l[i] + "S" + i);
//
//                if (c[i] == 0) {
//                    vr1.add(mr2);
//                } else {
//                    vr2.add(mr2);
//                }
//            }

            bw.flush();
            bw.close();

        }

        MultiChart.xRangDEFAULT_MIN = -1;
        MultiChart.xRangDEFAULT_MAX = 1;
        MultiChart.open(vr1, true, "UP ⇗ " + TSOperationControlerPanel.label_of_EXPERIMENT + " " + tsPrepMode + "_" + ResultManager.mode);
        MultiChart.open(vr2, true, "DOWNN ↓ " + TSOperationControlerPanel.label_of_EXPERIMENT + " " + tsPrepMode + "_" + ResultManager.mode);

        MultiChart.open(check1, true, TSOperationControlerPanel.label_of_EXPERIMENT + "_check1 " + tsPrepMode + "_" + ResultManager.mode);
        MultiChart.open(check2, true, TSOperationControlerPanel.label_of_EXPERIMENT + "_check1 " + tsPrepMode + "_" + ResultManager.mode);

        System.out.println(">>> TS-Prep-Mode       :  " + tsPrepMode);
        System.out.println(">>> Link-Creation-Mode :  " + ResultManager.mode);
        System.out.println(">>> ALL :  " + testsALL.size());
        System.out.println(">>> FN  :  " + resultFileName);

    }

    private static void labelAllSeries(Vector<TimeSeriesObject> v, String l) {
        for (TimeSeriesObject mr : v) {
            mr.setLabel(l + "___" + mr.getLabel());
        }
    }

    /**
     * *
     *
     *
     * Now we tune the preprocessin ...
     *
     * @param v
     * @return
     */
    private static Vector<TimeSeriesObject> normalizeAll(Vector<TimeSeriesObject> v) {

        Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();

        for (TimeSeriesObject mr : v) {

            TimeSeriesObject mr2 = null;

            switch (CorrelationPropertiesExperiment004.tsPrepMode) {

                case 0:
                    mr2 = mr.normalizeToStdevIsOne();
                    break;

                case 1:
                    mr2 = getLogReturn(mr);
                    break;

                case 2:
                    mr2 = getDiff(mr);
                    break;

                case 3:
                    mr2 = mr;
                    break;

            }

            if (mr2 != null) {
                vmr.add(mr2);
            }

        }

        return vmr;

    }

    private static Vector<TimeSeriesObject> cut(Vector<TimeSeriesObject> v, int von, int bis) {

        Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();

        for (TimeSeriesObject mr : v) {

            TimeSeriesObject mr2 = mr.cutOut(von, bis);

            System.out.println(mr2.getMinX() + "=>" + mr2.getMaxX());
            vmr.add(mr2);

        }

        return vmr;
    }

    private static TimeSeriesObject getDiff(TimeSeriesObject m) {

        TimeSeriesObject mr = new TimeSeriesObject();

        mr.setLabel(m.getLabel() + "_Diff");

        int len = m.xValues.size();

        // calc diffs from data
        double delta = 0.0;

        double now = 0.0;

        double last = (Double) m.yValues.elementAt(0);

        for (int i = 0; i < len; i++) {

            now = (Double) m.yValues.elementAt(i);

            delta = now - last;

            last = now;

            mr.addValuePair(i, delta);

        }

        return mr;

    }

    private static TimeSeriesObject getLogReturn(TimeSeriesObject m) {

        TimeSeriesObject mr = new TimeSeriesObject();

        mr.setLabel(m.getLabel() + "_LogReturn");

        int len = m.xValues.size();

        // calc diffs from data
        double delta = 0.0;
        double relDelta = 0.0;
        double now = 0.0;

        double last = (Double) m.yValues.elementAt(0);

        for (int i = 0; i < len; i++) {

            now = (Double) m.yValues.elementAt(i);

            delta = now - last;

            relDelta = delta / last;

            last = now;

            mr.addValuePair(i, Math.log(relDelta + 1));

        }

        return mr;

    }

}
