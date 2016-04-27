/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.crosscorrelation;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.series.MessreiheFFT;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Vector; 
import org.apache.hadoopts.statphys.detrending.DetrendingMethodFactory;
import org.apache.hadoopts.statphys.detrending.methods.IDetrendingMethod; 

import org.apache.hadoopts.app.thesis.FFTPhaseRandomizer;
import org.apache.hadoopts.app.thesis.TSGenerator;

/**
 *
 * @author kamir
 */
public class KKEntropieTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        stdlib.StdRandom.initRandomGen(1);
        
        Color[] dcSet = new Color[10];
        dcSet[0] = Color.blue;
        dcSet[1] = Color.red;
        dcSet[2] = Color.GREEN;
        dcSet[3] = Color.orange;
        dcSet[4] = Color.gray;
        dcSet[5] = Color.magenta;
        dcSet[6] = Color.PINK;
        dcSet[7] = Color.lightGray;
        dcSet[8] = Color.CYAN;
        dcSet[9] = Color.yellow;
        
        MultiChart._initColors(dcSet);

        // generation of the test data ...

        double PI = Math.PI;

        double[] f = {20, 100, 200, 500, 1000, 0.00001};
        double[] a = {5, 3, 8, 2, 1, 5};
        double[] phase = {0.5, -0.2, -0.20, 0.8, 1.0, 0.0};
        double[] noise = {0.81, 0.82, 0.81, 0.801, 0.825, 10.0};

        double time = 5;
        double samplingRate = 10000;

        Messreihe mr = new Messreihe();
        

        Vector<Messreihe> mrv = new Vector<Messreihe>();
        for (int i = 0; i < f.length; i++) {
            Messreihe m = TSGenerator.getSinusWave(f[i], time, samplingRate, a[i], phase[i], noise[i] * PI);
            mr = mr.add(m);
            mrv.add(m.copy());
        }
        
        mr.setLabel("mix");
        mrv.add(mr);

        MultiChart.open(mrv, "1" , "t" , "f(t)", true);
        
        // DFA ...
//        applyDFA(mrv);

        // simple randomisation
        Messreihe temp = mr.copy();
        int nsh = 100;
        temp.shuffleYValues( nsh );
        Messreihe m2 = temp;

        Messreihe m3 = FFTPhaseRandomizer.getPhaseRandomizedRow(mr.copy(), false, false, 0, FFTPhaseRandomizer.MODE_shuffle_phase);

        Vector<Messreihe> mrv2 = new Vector<Messreihe>();
        m2.setLabel("shuffled (" + nsh + ")" );
        mrv2.add( mr );
        mrv2.add( m2 );
        mrv2.add( m3 );
        
        
        MultiChart.open(mrv2, "2", "t", "rand( f(t) ) ", true);

        // DFA ...
//        applyDFA(mrv2);
        
        
        // teste die Autokorrelation
        Messreihe mrA = mr.copy();
        Messreihe mrB = mr.copy();
        
//        KreuzKorrelation._defaultK = 100;
//        KreuzKorrelation.GLdebug = true;
//        KreuzKorrelation.calcKR(mrA, mrB, true, true);
//        
//        // teste die Kreuzkorrelation - SHUFFLED
//        Messreihe mrA2 = mr.copy();
//        Messreihe mrB2 = m2.copy();
//        KreuzKorrelation.calcKR(mrA2, mrB2, true, true);
//
//        // teste die Kreuzkorrelation - Phase RAND
//        Messreihe mrA3 = mr.copy();
//        Messreihe mrB3 = m3.copy();
//        KreuzKorrelation.calcKR(mrA3, mrB3, true, true);
        
        int z = 5;
        
        KreuzKorrelation._defaultK = 25;
        Messreihe mrX = null;
        Messreihe mrA4 = mr.copy();
        Vector<Messreihe> mrv4 = new Vector<Messreihe>();
        for( int i = 0; i < z; i++ ) { 
            // teste die Kreuzkorrelation - SHUFFLED
            Messreihe mrB4 = FFTPhaseRandomizer.getPhaseRandomizedRow(mr.copy(), false, false, 0,FFTPhaseRandomizer.MODE_multiply_phase_with_random_value);
            Messreihe mrKR = (Messreihe)KreuzKorrelation.calcKR(mrA4, mrB4, true, true);

            if ( mrX == null ) { 
                mrX = mrKR.copy();
            }
            else { 
                mrX = mrX.add(mrKR);
            }
        
            mrv4.add( mrKR );
        
        }
        mrv4.add( mrX );
        
        MultiChart.open(mrv4, "4", "t", "cc", true);
        
        
        KreuzKorrelation._defaultK = 25;
        Messreihe mrX2 = null;
        Messreihe mrA5 = Messreihe.getGaussianDistribution( (int)Math.pow(2.0, 14.0) );
        
        
        
        // teste die Kreuzkorrelation - SHUFFLED
        Vector<Messreihe> mrv5 = new Vector<Messreihe>();
        for( int i = 0; i < z; i++ ) { 
            
            Messreihe mrB5 = Messreihe.getGaussianDistribution( (int)Math.pow(2.0, 14.0) );
            
            Messreihe mrKR = (Messreihe)KreuzKorrelation.calcKR(mrA5, mrB5, true, true);

            if ( mrX2 == null ) { 
                mrX2 = mrKR.copy();
            }
            else { 
                mrX2 = mrX2.add(mrKR);
            }
        
            mrv5.add( mrKR );
        
        }
        mrX2.divide_Y_by( (double)z);
        mrX2.add_to_Y( 100 );
        
        mrv5.add( mrX2  );
        
        MultiChart.open(mrv5, "5", "t", "cc", true);
    
    
        // teste die Kreuzkorrelation - PHASE RANDOM
        Messreihe mrX3 = null;
        Messreihe mrA6 = Messreihe.getGaussianDistribution( (int)Math.pow(2.0, 14.0) );
        Vector<Messreihe> mrv6 = new Vector<Messreihe>();
        for( int i = 0; i < z; i++ ) { 
            
            Messreihe mrB6 = Messreihe.getGaussianDistribution( (int)Math.pow(2.0, 14.0) );
            mrB6 = FFTPhaseRandomizer.getPhaseRandomizedRow( mrB6 , false, false, 0, FFTPhaseRandomizer.MODE_multiply_phase_with_random_value);
            
            Messreihe mrKR = (Messreihe)KreuzKorrelation.calcKR(mrA6, mrB6, true, true);

            if ( mrX3 == null ) { 
                mrX3 = mrKR.copy();
            }
            else { 
                mrX3 = mrX3.add(mrKR);
            }
        
            mrv6.add( mrKR );
        
        }
        mrX3.divide_Y_by( (double)z);
        mrX3.add_to_Y( 100 );
        
        mrv6.add( mrX3  );
        
        MultiChart.open(mrv6, "6", "t", "cc", false);
    
    
    }
    
    
    static double fitMIN = 1.2;
    static double fitMAX = 3.5;

    static int order = 0;
    
    private static void applyDFA(Vector<Messreihe> mrv) throws Exception {

            
        int nrOfSValues = 250;
        
        Vector<Messreihe> v = new Vector<Messreihe>();
        
        for (Messreihe d4 : mrv) {
            
            int N = d4.yValues.size();
            double[] zr = new double[N];

            Vector<Messreihe> vr = new Vector<Messreihe>();
            
            vr.add(d4);

            zr = d4.getData()[1];

            IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
            order = dfa.getPara().getGradeOfPolynom();
            dfa.getPara().setzSValues(nrOfSValues);

            // Anzahl der Werte in der Zeitreihe
            dfa.setNrOfValues(N);

            // die Werte für die Fensterbreiten sind zu wählen ...
            //dfa.initIntervalS();
            dfa.initIntervalSlog();
           
            dfa.showS();
            


            // http://stackoverflow.com/questions/12049407/build-sample-data-for-apache-commons-fast-fourier-transform-algorithm

            dfa.setZR(d4.getData()[1]);

            dfa.calc();

            Messreihe mr4 = dfa.getResultsMRLogLog();
            mr4.setLabel(d4.getLabel());
            v.add(mr4);

            String status = dfa.getStatus();

//            SimpleRegression alphaSR = mr4.linFit(fitMIN, fitMAX);
//
//            double alpha = alphaSR.getSlope();

  
            System.out.println( status );
        }
        
        if ( true ) {
                DecimalFormat df = new DecimalFormat("0.000");
                MultiChart.open(v, "fluctuation function F(s) [order:" + order + "] ", "log(s)", "log(F(s))", true, "???", null);
                
//                System.out.println(" alpha = " + df.format(alpha));
//                System.out.println("       = " + ((2 * alpha) - 1.0));

        }
    }
}
