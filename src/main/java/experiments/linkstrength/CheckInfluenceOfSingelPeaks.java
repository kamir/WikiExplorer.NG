/**
 * 
 * Modellierung der Verteilung von Peaks und Kontrolle mit der 
 * berechneten Kreuz-Korrelations-Funktion
 *
 **/

package experiments.linkstrength;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.awt.Container;
import java.util.Vector;
import org.jfree.ui.RefineryUtilities;
import experiments.crosscorrelation.KreuzKorrelation;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.RandomNodesGroup;

/**
 *
 * @author kamir
 */
public class CheckInfluenceOfSingelPeaks {

    public static boolean _debug = false;

    public static int mode = 0;

    public static final int mode_NORMALIZED = 0; // A
    public static final int mode_CC_TAU_0 = 1;   // B
    public static final int mode_ADVANCED = 2;   // C
    public static final int mode_CLEAN_ONE_PEAK = 3;   // C

    public static double calcStrength( KreuzKorrelation mr ) {
        
        switch( mode ) {
        
            case mode_NORMALIZED: { // 0
                return CCFunction.calcStrength_VERSION_A( mr );
            }
            
            case mode_CC_TAU_0: {// 1
                return CCFunction.calcStrength_VERSION_B( mr );
            }
            
            case mode_ADVANCED: { //2
                return CCFunction.calcStrength_VERSION__ADJUSTED( mr, false );
            }
//            case mode_CLEAN_ONE_PEAK: { //3
//                return CCFunction.calcStrength_VERSION_D( mr );
//            }

        }
        return 0.0;
    };
    


    
//    private static double calcStrength_VERSION_C( Messreihe mr ) {
//        double stdDev = mr.getStddev();
//        double maxY = mr.getMaxY();
//        mr.calcAverage();
//        double avY = mr.getAvarage();
//        double v = ( maxY - avY ) / stdDev;
//        if ( _debug ) {
//            System.out.println( mr.toString() );
//            System.out.println( mr.getLabel() + "\n\tstDev=" + stdDev + 
//                      "\tmaxY=" + maxY + "\t<y>=" + avY + "\tstr=" + v );
//        }
//        return v;
//    };



//    /**
//     * prüft ob "strength" > als Schwelle ist.
//     * @param mr
//     * @param threshold
//     * @return
//     */
//    public static boolean doCheck( Messreihe mr , double threshold ) {
//        boolean b = false;
//        double v = calcStrength_VERSION_A(mr);
//        if ( v > threshold ) b = true;
//        return b;
//    };

    /**
     * Für eine Zeitreihe mit Werten aus einer Gauss-Verteilung
     * wird der Einfluss einzelner Peaks auf die Link-Stärke untersucht.
     *
     * @param args
     */
    public static void main( String[] args ) {
        
        stdlib.StdRandom.initRandomGen(1);
        
        CheckInfluenceOfSingelPeaks._debug = true;
//        
//        calcTestForSimulatedFluctuationFunction();
//        
//        calcTestForSimulatedTS(  );

    };

//    private static void calcTestForSimulatedFluctuationFunction() {
//        /**
//         * mit bestimmten Reihen testen
//         */
//        
//
//        // Wie groß ist die LINK-Stärke bei solch einem STÖR-Peak in 
//        // der CC-Function?
//        
//         Vector<Messreihe> reihen_strength = new Vector<Messreihe>();
//        for( int j = 0; j < 5; j++ ) {
//            
//            Vector<Messreihe> reihen = new Vector<Messreihe>();
//        
//            double mw = j * 5.5;
//            Messreihe mr1 = Messreihe.getGaussianDistribution(600, mw, 1);
//            Messreihe mr2 = null;
//       
//            
//            Messreihe test = new Messreihe();
//            test.setLabel("F_CC(tau)_mw="+mw);
//            
//            Messreihe test2 = new Messreihe();
//            test.setLabel("mw="+mw);
//            
//            
//
//            for ( int i = 0; i < 10; i=i+1 ) {
//                
//                Messreihe p = mr1.copy();
//                
//                double stdevA = p.getStddev();
//                double mwA = p.getAvarage2();
//                
//
//                // von der verrauschten F_CC Reihe ausgehend einen Peak 
//                // für eine starke Correlation einsetzen ...
//                p.yValues.setElementAt(i*10.0, i*50 );
//                
//                double stdevB = p.getStddev();
//                double mwB = p.getAvarage2();
//                
//                // wir wollen ja mehrere Peak Stärken ansehen ...
//                reihen.add( p );
//
//                double str = CCFunction.calcStrength_VERSION_C(p, stdevA, stdevB, mwA, mwB);
//                // double str = CheckInfluenceOfSingelPeaks.calcStrength_VERSION_A(p);
//
//                for( int ii = 0; ii < 50; ii++ ) { 
//                    test.addValuePair( ii , 0);
//                    test2.addValuePair( ii , 0);
//                } 
//                
//                test.addValuePair( i * 50, str);
//                // test2.addValuePair( i * 50, mw);
//                
//            }
//            reihen.add( test );
//            // reihen.add( test2 ); 
//            reihen_strength.add( test );
//            
//            MultiChart.open(reihen , "Test of the influence of strong correlation peaks in the " +
//                                 "CC-function", "tau", "CC(tau,peak_height)", true);
//        }
//        
//        //MultiChart.open(reihen_strength , "Influence of systematic peaks in the " +
//        //                         "CC-function", "peak_heigth", "strength", true);
//        
//       
//    }
//
//    private static void calcTestForSimulatedTS() {
//        Messreihe mr1;
//        Messreihe mr2;
//        /**
//         * Mit richtigen CC Reihen teste ...
//         */
//        double f = 0.0;
//        Messreihe f_str = new Messreihe();
//        Messreihe f_strH = new Messreihe();
//        
//        double ts = 0.25 ;
//        
//        f_str.setLabel("<strength>( mittlerer Peakhöhe )");
//        f_strH.setLabel("<strength>( mittlerer Peakhöhe ) && strength > " + ts);
//        Vector<Messreihe> fv = new Vector<Messreihe>();
//        fv.add(f_str);
//        fv.add(f_strH);
//        for ( int j = 1; j <= 10; j++) {
//            f = 0.5 * j;
//
//            double mw = 25;
//            double tau = 14;
//            double devH = 5;
//            double devW = 0.2;
//
//            mr1 = Messreihe.getGaussianDistribution(300, mw, 2);
//            mr2 = Messreihe.getGaussianDistribution(300, mw, 2);
//
//            Vector<Messreihe> rr = new Vector<Messreihe>();
//            Vector<Messreihe> kkrr = new Vector<Messreihe>();
//            KreuzKorrelation._defaultK = (int)tau;
//            
//            Messreihe test2 = new Messreihe();
//            test2.setLabel("<strength> zur mittleren Peakhöhe f=" + f );
//            Messreihe test3 = new Messreihe();
//            test3.setLabel("<strength> zur mittleren Peakhöhe f=" + f +" ( strength > " + ts + ")" );
//
//            
//            Messreihe test2A = new Messreihe();
//            test2A.setLabel("<adjusted> f=" + f );
//   
//            double avSTR = 0.0;
//            double avSTR_H = 0.0;
//            int c = 0;
//
//            int z = 100;
//            for ( int i = 0; i < z ; i++ ) {
//
//                // Variation der Lage der Peaks
//                // Wenn breiter verteilt, als tau, dann geht die Significanz
//                // verloren
//                double r1 = stdlib.StdRandom.gaussian( 0, devW * tau );
//                double r2 = stdlib.StdRandom.gaussian( 0, devW * tau );
//
//                // HÖHE des PEAKS
//                double y1 = stdlib.StdRandom.gaussian( mw*f , devH );
//                double y2 = stdlib.StdRandom.gaussian( mw*f , devH );
//
//                Messreihe m1 = mr1.copy();
//                Messreihe m2 = mr2.copy();
//
//                addPeak(m1, (int) (100 + r1), y1, 300);
//                addPeak(m2, (int) (100 + r2), y2, 300);
//
//                boolean b = true;
//                if ( i % 500 == 0) b = true;
//                else b = false;
//
//                m1.normalize();
//                m2.normalize();
//
//                KreuzKorrelation kr = null;
//                kr = KreuzKorrelation.calcKR(m1,m2,false,false);
//
//                kkrr.add( kr );
//                double str = 0.0;
//                
//                boolean debug = true;
//                double str2 = CCFunction.calcStrength_VERSION__ADJUSTED(kr, debug);
//                
//                str = CCFunction.calcStrength_VERSION_A(kr);
//                test2A.addValuePair( i, str2);
//                avSTR = avSTR + str;
//
//                // zähle die, wo die CC_max > als ts ist ...
////                if ( kr.getMaxY() > ts ) {
//                // zähle die, wo die CC_max > als ts ist ...
//                if ( str > ts ) {    
//                    test3.addValuePair(i,str);
//                    avSTR_H = avSTR_H + str;
//                    c++;
//                }
//                else test2.addValuePair( i, str);
//
//                rr.add( m1 );
//                rr.add( m2 );
//            }
//            f_str.addValuePair(f, avSTR/(z*1.0) );
//            f_strH.addValuePair(f, avSTR_H/(c*1.0));
//
//
//            MultiChart.open(kkrr,"CC -> " +"f="+f+", mw="+mw,"","",false);
//
//            MultiChart.open(rr,"test TS -> "+"f="+f+", mw="+mw,"","",false);
//            //createHistogramm(test2, test3, 120, 0, 6);
//            //createHistogramm(test2A, test3, 120, -50, 50);
//        }
//        MultiChart.open( fv , "f(str)", "f", "str", true );
//    }

    public static void addPeak( Messreihe mr , int x, double y, int l ) {
        if ( x > l-1 ) x = l - x;
        if ( x < 0 ) x = l + x;
        System.out.println( x + " " + y );
        mr.yValues.setElementAt( y, x);
    };

    public static Container createHistogramm( Messreihe mr, 
                                              Messreihe r2, 
                                              int bins, int min, int max ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( r2, bins, min, max );
        
        demo.addSerieWithBinning( mr, bins, min, max );


        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
//        demo.store( ng.pfad + "img/" , ng.getLangID() +"_" + 
//        ng.editReihen.size() + mr.getLabel().replaceAll(" ", "_" ) );
        return demo.getContentPane();
    };
    
//    public static NodeGroup getRandomPeakedNodeGroup( 
//            String label,
//            int ngOffset,
//            int nrOfNodes,
//            int length,
//            double mw, 
//            double mw_peakHoeh,
//            double tau,
//            double devH,
//            double devW ) { 
//    
//        NodeGroup ng = new NodeGroup(label);
//        for( int i = 0; i < nrOfNodes ; i++ ) { 
//            String sl = label + "_" + (ngOffset + i);
//            Messreihe r = createRandomSeries_A_INTERNAL( sl, length, mw, mw_peakHoeh, tau, devH, devW );
//            ng.accessReihen.add( r );
//            ng.getIdsAsVector().add(ngOffset + i);
//        }
//        
//        ng.postInit();
//        
//        return ng;
//    }
    
    public static Messreihe createRandomSeries_A_INTERNAL(String sl, int length, double mw, double mw_peakHoeh, double tau, double devH, double devW) {
        

        // double f = stdlib.StdRandom.gaussian( 1, 10 ) * 0.5;

        // Variation der Lage der Peaks
        // Wenn breiter verteilt, als tau, dann geht die Significanz
        // verloren
        double r1 = stdlib.StdRandom.gaussian( 0, devW * tau );

        // HÖHE des PEAKS
        double y1 = stdlib.StdRandom.gaussian( mw_peakHoeh , devH );

        Messreihe m1 = Messreihe.getGaussianDistribution(length, mw, 1);
        m1.setLabel(sl);

        int pos_peak = (int) stdlib.StdRandom.gaussian(0,length);

        if ( pos_peak < 0 ) pos_peak = pos_peak + length;
        else if ( pos_peak > length ) pos_peak = pos_peak - length;

        if ( pos_peak < 0 ) pos_peak = pos_peak + length;
        else if ( pos_peak > length ) pos_peak = pos_peak - length;

        if ( pos_peak < 0 ) pos_peak = pos_peak + length;
        else if ( pos_peak > length ) pos_peak = pos_peak - length;

        if ( pos_peak < 0 ) pos_peak = pos_peak + length;
        else if ( pos_peak > length ) pos_peak = pos_peak - length;

        int nr = (int) stdlib.StdRandom.gaussian( 1, 10 );

        addPeak(m1, (int) (pos_peak + r1), y1, length);

        return m1;
    }


    public static Messreihe createRandomSeries_A_Peaks( String label ) {

        double mw = 0;
        double mw_peakHoeh = 0 ;// 20;  // 10
        double tau = 14;
        double devH = 5;
        double devW = 0.2;
        int length = RandomNodesGroup.length;
        
        return createRandomSeries_A_INTERNAL( label, length, mw, mw_peakHoeh, tau, devH, devW );
        
    }

 

      
    
    /**
     * Tool function to manipulate a time series object.
     * 
     * @param athis
     * @return 
     */
    static public Messreihe removeMaximumValueFromRow( Messreihe athis ) {

        boolean removed = false;

        Messreihe mr = new Messreihe();
        mr.setLabel( athis.getLabel() );

        int max = athis.yValues.size();
        double maxY = athis.getMaxY();
        
        for( int i = 0; i < max ; i++ ) {
            double v = (Double)athis.yValues.elementAt(i);
            if ( !removed ) {
                if( v != maxY ) {
                    mr.addValuePair( (double)i , v);
                }
                else { 
                    removed = true;
                }
            }   
            else {
                mr.addValuePair( (double)i , (Double)athis.yValues.elementAt(i) );
            }
        }
        return mr;
    }


    


}
