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
import java.text.DecimalFormat;

import m3.wikipedia.corpus.extractor.NetDensityCalc;

import metadata.ExperimentDescriptor;

import org.apache.hadoopts.app.thesis.LongTermCorrelationSeriesGenerator;
import org.apache.hadoopts.app.bucketanalyser.TSOperationControlerPanel;

import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;
import m3.research.SigmaBandTool;

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
public class CorrelationPropertiesExperiment001a {

    static StringBuffer log = null;

    static Vector<TimeSeriesObject> testsA = null;
    static Vector<TimeSeriesObject> testsB = null;
    static Vector<TimeSeriesObject> testsC = null;
    static Vector<TimeSeriesObject> testsD = null;
    static Vector<TimeSeriesObject> testsE = null;

    static Vector<TimeSeriesObject> check = null;

    public static String BASEFOLDER = null;

    public static void main(String[] args) throws Exception {

        // never forget !!!
        stdlib.StdRandom.initRandomGen(1);

        int a1 = 50;
        int a2 = 8;

        int a4 = 3;

        boolean a5 = false;  // NOLTR
 
        double[] a3 = {0.0, 0.2, 0.5, 0.8, 1.0, 1.2, 1.5, 1.8};
        
        SigmaBandTool.exportFolder = "/TSBASE/FIG_3_4";

        testsA = new Vector<TimeSeriesObject>();
        testsB = new Vector<TimeSeriesObject>();

        int z = a1;
        int exp = a2;
        int N = (int) Math.pow(2, exp);

        // some sample frequencies ...
        //double beta = a3[3];
        /**
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "FF1_Mode_" + a4 + "_z_" + z + "_l_" + exp + "_beta_" + a3[0] + "_" + a3[3] + "_FINAL_WITH_MOMENTS";

        if (BASEFOLDER == null) {
            TSOperationControlerPanel.baseFolder = "/TSBASE/FIG_3_4";
        } else {
            TSOperationControlerPanel.baseFolder = BASEFOLDER;
        }

        File f = new File(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        Vector<TimeSeriesObject> vr = new Vector<TimeSeriesObject>();

        BufferedWriter bw = new BufferedWriter(new FileWriter(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv"));

        BufferedWriter bw2 = new BufferedWriter(new FileWriter("/Users/kamir/Documents/THESIS/dissertationFINAL/main/FINAL/LATEX/semanpix/FinalFigure1/imagedata/v5/" + TSOperationControlerPanel.label_of_EXPERIMENT + "_STATS" + ".tsv"));

        Vector<TimeSeriesObject> means = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> vars = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> skews = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> kurtosis = new Vector<TimeSeriesObject>();

        SigmaBandTool t1 = new SigmaBandTool();
        SigmaBandTool t2 = new SigmaBandTool();
        SigmaBandTool t3 = new SigmaBandTool();
        SigmaBandTool t4 = new SigmaBandTool();

        for (int RUN = 0; RUN < 50; RUN++) {

            TimeSeriesObject m = new TimeSeriesObject();
            TimeSeriesObject v = new TimeSeriesObject();
            TimeSeriesObject s = new TimeSeriesObject();
            TimeSeriesObject k = new TimeSeriesObject();

            m.setLabel("means");
            v.setLabel("vars");
            s.setLabel("skews");
            k.setLabel("kurtosis");

            for (double beta : a3) {

                testsA = new Vector<TimeSeriesObject>();
                testsB = new Vector<TimeSeriesObject>();

                for (int i = 0; i < z; i++) {

                    TimeSeriesObject mra = null;

                    if (a5) {
                        mra = TimeSeriesObject.getGaussianDistribution(N);
                    } else {
                        mra = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
                    }

                    mra.setLabel(mra.getLabel() + "_a_" + i);
                    testsA.add(mra);
                }

                for (int i = 0; i < z; i++) {

                    TimeSeriesObject mrb = null;

                    if (a5) {
                        mrb = TimeSeriesObject.getGaussianDistribution(N);
                    } else {
                        mrb = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
                    }

                    mrb.setLabel(mrb.getLabel() + "_b_" + i);
                    testsB.add(mrb);
                }

                Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();
                vmr.add(testsA.elementAt(0));
                vmr.add(testsB.elementAt(0));

                if (!a5) {

                    TimeSeriesObject mrX = TimeSeriesObject.getGaussianDistribution(N);

                    vmr.add(LongTermCorrelationSeriesGenerator.getRandomRow(mrX, 0.0, false, false));
                    vmr.add(mrX);

                    vmr.add(TimeSeriesObject.getUniformDistribution(N, 0.0, 1.0));
                    vmr.add(TimeSeriesObject.getExpDistribution(N, 1.0));

                }

                String label;
                if (a5) {
                    label = " gaussian";
                } else {
                    label = " beta=" + beta;
                }

//                MultiChart.open(vmr, true, "raw " + label);

//        /**
//         * FINISH this view ...
//         */
//        
//        JFrame f =new JFrame("Debug");
//        f.getContentPane().add(new TimeSeriesObjectPanel(testsA.elementAt(0)) );
//        f.setVisible( true );
                /**
                 * TODO:
                 *
                 * Refactor this component ...
                 */
                NetDensityCalc ndc = new NetDensityCalc();

                double ts = -1000;
                boolean showLegend = true;
                int runID = 0;

                ResultManager.mode = a4;

                HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsA, testsB, false, ts, null, ndc, beta + " testsAB_RAW", bw, runID, false);

//                HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsB, true, ts, null, ndc, beta + "testsAB_SHUFFLE", bw, runID, false);

                vr.add(r1.getHistogramNORM());
//            vr.add(r1.getHistogramLOG());

//                vr.add(r2.getHistogramNORM());
//            vr.add(r2.getHistogramLOG());

                double[] mom = r1.getMoments();
//                double[] mom2 = r2.getMoments();

                m.addValuePair(beta, mom[0]);
                v.addValuePair(beta, mom[1]);
                s.addValuePair(beta, mom[2]);
                k.addValuePair(beta, mom[3]);

                DecimalFormat df = new DecimalFormat("0.000000");

                String STATSvalues = df.format(mom[0]) + "\t" + df.format(mom[1]) + "\t" + df.format(mom[2]) + "\t" + df.format(mom[3]) + "\n";

                bw2.write(a4 + "\t" + z + "\t" + beta + "\t" + STATSvalues);

            }
            
            System.out.println( "RUN     :  "  + RUN );

//            means.add(m);
//            vars.add(v);
//            skews.add(s);
//            kurtosis.add(k);

            t1.addCollect(m,false);
            t2.addCollect(v,false);
            t3.addCollect(s,false);
            t4.addCollect(k,false);

        }     
        
        MultiChart.df1 = new DecimalFormat("0.00");
        MultiChart.df2 = new DecimalFormat("0.00");
        MultiChart.setDefaultRange = false;
        MultiChart.autoscale = true;

//        t1._plotAndStoreTrends("means");
//        t2._plotAndStoreTrends("var");
//        t3._plotAndStoreTrends("skewness");
//        t4._plotAndStoreTrends("kurtosis");
        
        
                   
     
        MultiChart.open(t1.getRows(), true, a4 + "_means");
        MultiChart.open(t2.getRows(), true, a4 + "_variance");
        MultiChart.open(t3.getRows(), true, a4 + "_skewness");
        MultiChart.open(t4.getRows(), true, a4 + "_kurtosis");
        
//        

//        /**
//         * TODO:
//         *
//         * Tune the MultiChartPanel with right Metadata ...
//         */
//        MultiChart.xRangDEFAULT_MIN = 0;
//        MultiChart.xRangDEFAULT_MAX = 12;
//
//        if (a4 == 3) {
//            MultiChart.xRangDEFAULT_MIN = -5;
//            MultiChart.xRangDEFAULT_MAX = 5;
//        }
//
//
//        MultiChart.open(vr, true, TSOperationControlerPanel.label_of_EXPERIMENT);
//
        System.out.println(">>> Link-Creation-Mode       :  " + ResultManager.mode);
        System.out.println(">>> Link-List was written to :  " + TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");
//
//        MultiChart.persistPixNode();

        bw.flush();
        bw.close();

        bw2.flush();
        bw2.close();

    }

}
