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

import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;

@ExperimentDescriptor (
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
public class CorrelationPropertiesExperiment001 {
    
    static StringBuffer log = null;
    
    static Vector<Messreihe> testsA = null;
    static Vector<Messreihe> testsB = null;
    static Vector<Messreihe> testsC = null;
    static Vector<Messreihe> testsD = null;
    static Vector<Messreihe> testsE = null;
    
    static Vector<Messreihe> check = null;

    public static void main( String[] args ) throws Exception { 

        // never forget !!!
        stdlib.StdRandom.initRandomGen(1);

        int a1 = 10;
        int a2 = 14;
        double a3 = -1.0;
        int a4 = 3;
        boolean a5 = false;  // NOLTR
        
//        int a1 = Integer.parseInt( args[0] );
//        int a2 = Integer.parseInt( args[1] );
//        double a3 = Double.parseDouble( args[2] );
//        int a4 = Integer.parseInt( args[3] );
//        boolean a5 = Boolean.parseBoolean( args[4] );
        
        testsA = new Vector<Messreihe>();
        testsB = new Vector<Messreihe>();  
        
        int z = a1;
        int exp = a2;
        int N = (int)Math.pow(2, exp);
    
        // some sample frequencies ...
        double beta = a3; 

        /** 
         * Setup the project with Metadata to keep parameters and runtime logs
         */
        TSOperationControlerPanel.label_of_EXPERIMENT = "Mode_" + a4 + "_z_" + z + "_l_" + exp + "_beta_" + beta;

        TSOperationControlerPanel.baseFolder = "/TSBASE/Exp2/WikiExplorer.NG_DATA";
        
        File f = new File( TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv" );
        if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
            
        for( int i = 0; i < z; i++ ) {
            
            Messreihe mra=null;
            
            if ( a5 ) mra = Messreihe.getGaussianDistribution(N);
            else mra = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);

            mra.setLabel( mra.getLabel() + "_a_" + i ); 
            testsA.add(mra);
        }

        for( int i = 0; i < z; i++ ) {

            Messreihe mrb=null;
            
            if ( a5 ) mrb = Messreihe.getGaussianDistribution(N);
            else mrb = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
            
            mrb.setLabel( mrb.getLabel() + "_b_" + i ); 
            testsB.add(mrb);
        }
        
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        vmr.add( testsA.elementAt(0));
        vmr.add( testsB.elementAt(0));
        
        if ( !a5 ) {
            
            Messreihe mrX = Messreihe.getGaussianDistribution(N);
            
            vmr.add( LongTermCorrelationSeriesGenerator.getRandomRow(mrX, 0.0, false, false) );
            vmr.add( mrX);
            
            vmr.add( Messreihe.getUniformDistribution(N, 0.0, 1.0) );
            vmr.add( Messreihe.getExpDistribution(N, 1.0) );
            
        }
        
        String label;
        if ( a5 ) label = " gaussian";
        else label = " beta=" + beta;
        
        MultiChart.open( vmr, true, "raw " + label);
        
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
         *      Refactor this component ...
         */
        NetDensityCalc ndc = new NetDensityCalc();
        
        BufferedWriter bw = new BufferedWriter( new FileWriter( TSOperationControlerPanel.baseFolder + "/" + TSOperationControlerPanel.label_of_EXPERIMENT + ".tsv" ) );
  
        double ts = -1000;
        boolean showLegend = true;
        int runID = 0;
        
        ResultManager.mode = a4;
        
//        HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsA, testsB, false, ts, null, ndc, beta + " testsAB_RAW", bw, runID, false);
// 
//        HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsB, true, ts, null, ndc, beta + "testsAB_SHUFFLE", bw, runID, false);
//        
//        Vector<Messreihe> vr = new Vector<Messreihe>();
//        vr.add( r1.getHistogramNORM() );
//        vr.add( r2.getHistogramNORM() );
        
        /**
         * TODO:
         * 
         * Tune the MultiChartPanel with right Metadata ...
         *
         */
//        MultiChart.xRangDEFAULT_MIN = -1;
//        MultiChart.xRangDEFAULT_MAX = 1;
//        MultiChart.open( vr, true, TSOperationControlerPanel.label_of_EXPERIMENT );
        
        System.out.println(">>> Link-Creation-Mode:  " + ResultManager.mode );
        
        bw.flush();
        bw.close();
        
        
         
    };
    
    
    
     
 

 
    
}
