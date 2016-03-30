/*
 * This Generator creates Time-Series for an experiment to analyze the influence 
 * LTM on correlation strength.
 *   
 *    5 different betas are used.
 * 
 *    1 chart to check individual series is created for valiation
 */
package experiments;

import com.cloudera.wikipedia.explorer.CCProzessor;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import org.apache.hadoopts.data.series.Messreihe;
import java.util.Vector;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.MessreiheFFT;
import org.apache.hadoopts.hadoopts.core.TSBucket;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import m3.wikipedia.corpus.extractor.NetDensityCalc;
import metadata.ExperimentDescriptor;
import org.apache.hadoopts.app.thesis.LongTermCorrelationSeriesGenerator;
import org.apache.commons.math3.transform.TransformType;
import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;

/**
 *
 * @author kamir
 */

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
        
        /** 
         * TODO:
         * 
         * Setup the project with Metadata to keep parameters and runtime logs
         */

        testsA = new Vector<Messreihe>();
        testsB = new Vector<Messreihe>();  
        
        int z = 40;
        int exp = 10;
        
        int N = (int)Math.pow(2, exp);
    
        // some sample frequencies ...
        double beta = 0.5; 
        
        for( int i = 0; i < z; i++ ) {
            Messreihe mra = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
            mra.setLabel( mra.getLabel() + "_a_" + i ); 
            testsA.add(mra);
        }

        for( int i = 0; i < z; i++ ) {
            Messreihe mrb = LongTermCorrelationSeriesGenerator.getRandomRow(N, beta, false, false);
            mrb.setLabel( mrb.getLabel() + "_b_" + i ); 
            testsB.add(mrb);
        }
        
        /**
         * TODO:
         * 
         * Refactor this component ...
         */
        NetDensityCalc ndc = new NetDensityCalc();
        
        BufferedWriter bw = new BufferedWriter( new FileWriter( "tmp_"+beta+".dat" ) );
        double ts = -1000;
        boolean showLegend = true;
        int runID = 0;
        
        HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsA, testsB, false, ts, null, ndc, beta + " testsAB_RAW", bw, runID, false);
        
        HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsB, true, ts, null, ndc, beta + "testsAB_SHUFFLE", bw, runID, false);
        
        Vector<Messreihe> vr = new Vector<Messreihe>();
        vr.add( r1.getHistogramNORM() );
        vr.add( r2.getHistogramNORM() );
        
        /**
         * TODO:
         * 
         * Tune the MultiChartPanel with right Metadata ...
         *
         */
        MultiChart.open( vr, true, "histogram " + beta);
        
        System.out.println(">>> Link-Creation-Mode: " + CheckInfluenceOfSingelPeaks.mode );
        
         
    };
    
    
    
     
 

 
    
}
