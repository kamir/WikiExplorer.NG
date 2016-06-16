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
import java.text.DecimalFormat;

import m3.wikipedia.corpus.extractor.NetDensityCalc;

import metadata.ExperimentDescriptor;
import org.apache.commons.math.stat.descriptive.moment.*;

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
public class CorrelationPropertiesExperiment001b {

    static StringBuffer log = null;

    static Vector<Messreihe> testsA = null;
    static Vector<Messreihe> testsB = null;
    static Vector<Messreihe> testsC = null;
    static Vector<Messreihe> testsD = null;
    static Vector<Messreihe> testsE = null;

    static Vector<Messreihe> check = null;

    public static String BASEFOLDER = null;

    public static void main(String[] args) throws Exception {

        // never forget !!!
        stdlib.StdRandom.initRandomGen(1);

        int a1 = 50;
        int a2 = 8;

        int a4 = 1;

        boolean a5 = false;  // NOLTR
 
        double[] a3 = {0.0, 0.2, 0.5, 0.8, 1.0, 1.2, 1.5, 1.8};
        
        SigmaBandTool.exportFolder = "/Users/kamir/Documents/THESIS/dissertationFINAL/main/FINAL/LATEX/semanpix/FinalFigure1/imagedata/v4/";

        testsA = new Vector<Messreihe>();
        testsB = new Vector<Messreihe>();

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
            TSOperationControlerPanel.baseFolder = "/Users/kamir/Documents/THESIS/dissertationFINAL/main/FINAL/LATEX/semanpix/FinalFigure1/imagedata/v4/";
        } else {
            TSOperationControlerPanel.baseFolder = BASEFOLDER;
        }

        File f = new File(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        Vector<Messreihe> vr = new Vector<Messreihe>();

        BufferedWriter bw = new BufferedWriter(new FileWriter(TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv"));

        BufferedWriter bw2 = new BufferedWriter(new FileWriter("/Users/kamir/Documents/THESIS/dissertationFINAL/main/FINAL/LATEX/semanpix/FinalFigure1/imagedata/v4/" + TSOperationControlerPanel.label_of_EXPERIMENT + "_STATS" + ".tsv"));
 
        for (int RUN = 0; RUN < 1; RUN++) {
 

            for (double beta : a3) {

                testsA = new Vector<Messreihe>();
                testsB = new Vector<Messreihe>();

                for (int i = 0; i < z; i++) {

                    Messreihe mra = null;

                    if (a5) {
                        mra = Messreihe.getGaussianDistribution(N);
                    } else {
                        mra = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
                    }

                    mra.setLabel(mra.getLabel() + "_a_" + i);
                    testsA.add(mra);
                }

                for (int i = 0; i < z; i++) {

                    Messreihe mrb = null;

                    if (a5) {
                        mrb = Messreihe.getGaussianDistribution(N);
                    } else {
                        mrb = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
                    }

                    mrb.setLabel(mrb.getLabel() + "_b_" + i);
                    testsB.add(mrb);
                }

                Vector<Messreihe> vmr = new Vector<Messreihe>();
                vmr.add(testsA.elementAt(0));
                vmr.add(testsB.elementAt(0));

                if (!a5) {

                    Messreihe mrX = Messreihe.getGaussianDistribution(N);

                    vmr.add(LongTermCorrelationSeriesGenerator.getRandomRow(mrX, 0.0, false, false));
                    vmr.add(mrX);

                    vmr.add(Messreihe.getUniformDistribution(N, 0.0, 1.0));
                    vmr.add(Messreihe.getExpDistribution(N, 1.0));

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
//        f.getContentPane().add(new MessreihePanel(testsA.elementAt(0)) );
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

                HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsB, true, ts, null, ndc, beta + "testsAB_SHUFFLE", bw, runID, false);

//                vr.add(r1.getHistogramNORM());
            vr.add(r1.getHistogramLOG());

//                vr.add(r2.getHistogramNORM());
            vr.add(r2.getHistogramLOG());

            }
            
            System.out.println( "RUN     :  "  + RUN );
             

        }
         
        
        MultiChart.df1 = new DecimalFormat("0.00");
        MultiChart.df2 = new DecimalFormat("0.00");
        MultiChart.setDefaultRange = false;
        MultiChart.autoscale = true;

 
        
 
        /**
         * TODO:
         *
         * Tune the MultiChartPanel with right Metadata ...
         */
        MultiChart.xRangDEFAULT_MIN = 0;
        MultiChart.xRangDEFAULT_MAX = 12;

        if (a4 == 3) {
            MultiChart.xRangDEFAULT_MIN = -5;
            MultiChart.xRangDEFAULT_MAX = 5;
        }


        MultiChart.open(vr, true, TSOperationControlerPanel.label_of_EXPERIMENT);

        System.out.println(">>> Link-Creation-Mode       :  " + ResultManager.mode);
        System.out.println(">>> Link-List was written to :  " + TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv");

        MultiChart.persistPixNode();

        bw.flush();
        bw.close();

        bw2.flush();
        bw2.close();

    }

}
