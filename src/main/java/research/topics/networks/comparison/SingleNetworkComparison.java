/***
 * 
 * @Network-Comparison
 * 
 * 
 * Lade die Linkstaerke-Tabelle von n Netzwerken - deren Links von 
 * PageID-Paaren und einer Linkstaerke (String,Double) beschrieben 
 * werden - aus einer Tabelle und berechne die Kreuzkorrelation der 
 * jeweiligen Adjacency-Matrix, um die Netzwerke miteinander zu vergleichen.
 * 
 * 
 * TODO:
 * 
 * a) Kendall tau_coeffcient of two lists
 * b) Significanztest:
 *        Verschieben der Reihen und entfernen aller Link-Paare mit weniger 
 *        als 4 beteiligten Knoten (Formel sonst wie bei Kreuzkorrleation).
 * 
 * c) Normierung auf STDEV=1 vor Rechnung der CC
 * 
 **/
package research.topics.networks.comparison;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.io.MessreihenLoader;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import experiments.crosscorrelation.KreuzKorrelation;
import stdlib.StdRandom;

/**
 *
 * @author kamir
 */
public class SingleNetworkComparison {
  
    public static void main( String[] args ) { 
        File f = null;
        
        boolean calcCC = true;
        boolean calcTAU = true;
        boolean useNormalizedRows = true;
        
        boolean showRows = true;
        
        String fn = "ng_015.dat_LINKSTREGTH.tab";

//        if ( args != null ) {
//            f = new File( args[0] );
//            if ( f.exists() ) { 
//                fn = args[0];
//            }
//        }
        
        f = new File( fn );
        System.out.println( ">>> use network link-table : " + 
                            f.getAbsolutePath() + " -> " + f.exists());
        
        TimeSeriesObject mrA = null;
        TimeSeriesObject mrB = null;
        TimeSeriesObject mrC = null;

        double ccAB = 0.0;
        double ccBC = 0.0;
        double ccCA = 0.0;
        
        double stAB = 0.0;
        double stBC = 0.0;
        double stCA = 0.0;

        double kendall_tau_AB = 0.0;
        double kendall_tau_BC = 0.0;
        double kendall_tau_CA = 0.0;
        

        
        mrA = MessreihenLoader.getLoader().loadMessreihe_zr_SC(  f, 2, "\t" );
        mrB = MessreihenLoader.getLoader().loadMessreihe_zr_SC(  f, 3, "\t" );
        mrC = MessreihenLoader.getLoader().loadMessreihe_zr_SC(  f, 4, "\t" );
        
        mrA.setLabel( "A" );
        mrB.setLabel( "B" );
        mrC.setLabel( "C" );
        
        Vector<TimeSeriesObject> mrv = new Vector<TimeSeriesObject>();
        mrv.add( mrC );
        mrv.add( mrA );
        mrv.add( mrB );
        
        Vector<TimeSeriesObject> mrv2 = new Vector<TimeSeriesObject>();
        mrv2.add( mrC.normalizeToStdevIsOne() );
        mrv2.add( mrA.normalizeToStdevIsOne() );
        mrv2.add( mrB.normalizeToStdevIsOne() ); 
        
        System.out.println( mrA.getStatisticData( "[mrA : ]") );
        System.out.println( mrB.getStatisticData( "[mrB : ]") );
        System.out.println( mrC.getStatisticData( "[mrC : ]") );
        
        if ( useNormalizedRows ) { 
            mrA = mrA.normalizeToStdevIsOne();
            mrB = mrB.normalizeToStdevIsOne();
            mrC = mrC.normalizeToStdevIsOne();
            
            System.out.println( mrA.getStatisticData( "[mrA : ]") );
            System.out.println( mrB.getStatisticData( "[mrB : ]") );
            System.out.println( mrC.getStatisticData( "[mrC : ]") );
        }
        
        
        if ( showRows ) {
            MultiChart.open(mrv);
            MultiChart.open(mrv2);
            
            
        }
                
//        StdRandom.initRandomGen(1);
//        TimeSeriesObject mrA = TimeSeriesObject.getGaussianDistribution(50);
//        TimeSeriesObject mrB = TimeSeriesObject.getGaussianDistribution(50);
//        TimeSeriesObject mrC = TimeSeriesObject.getGaussianDistribution(50);
        
        if ( calcCC ) {
            KreuzKorrelation._defaultK = 3;       

            KreuzKorrelation kkAB = KreuzKorrelation.calcKR(mrA, mrB, true);
            KreuzKorrelation kkBC = KreuzKorrelation.calcKR(mrB, mrC, true);
            KreuzKorrelation kkCA = KreuzKorrelation.calcKR(mrC, mrA, true);

            ccAB = kkAB.getYValueForX2(0.0, 1e-6);
            ccBC = kkBC.getMaxY();
            ccCA = kkCA.getMaxY();
                    
            stAB = calcSignifikanzTest( mrA, mrB );
            stBC = calcSignifikanzTest( mrB, mrC );
            stCA = calcSignifikanzTest( mrC, mrA );
        }    

      
        if ( calcTAU ) {
            kendall_tau_AB = calcKendallTau( mrA, mrB );
            kendall_tau_BC = calcKendallTau( mrB, mrC );
            kendall_tau_CA = calcKendallTau( mrC, mrA );
        }

        System.out.println( 
          "stAB=" + stAB + " \tccAB=" + ccAB + " \ttau_AB=" + kendall_tau_AB);
        System.out.println( 
          "stBC=" + stBC + " \tccBC=" + ccBC + " \ttau_BC=" + kendall_tau_BC);
        System.out.println( 
          "stCA=" + stCA + " \tccCA=" + ccCA + " \ttau_CA=" + kendall_tau_CA);
        
    }

    private static double calcSignifikanzTest(TimeSeriesObject mrA, TimeSeriesObject mrE) {
        
        double x = 0.0;
        double mwA = mrA.getAvarage2();
        double mwE = mrE.getAvarage2();
                
        double c = 0.0;
        double cor = 0.0;
        double corAnz = 0.0;
        int ci = 0;
        
        double[][] dA = mrA.getData();
        double[][] dE = mrE.getData();
        
        int[][] idsA = convertIds( mrA.xLabels );
        int[][] idsE = convertIds( mrE.xLabels );
        
        int ka1 = 0;
        int ka2 = 0;

        int ke1 = 0;
        int ke2 = 0;
                
        int total = mrA.yValues.size() * mrE.yValues.size();
        System.out.println( "ANZ=" + total );
        System.out.println( "ANZ(A)=" + mrA.yValues.size() );
        for( int i = 0; i < mrA.yValues.size() ; i++ ) {
            for( int j = 0; j < mrE.yValues.size() ; j++ ) {
                ci++;
                double Ai = dA[1][i];
                double Ej = dE[1][j];
                
                ka1 = idsA[0][i];
                ka2 = idsA[1][i];
 
                ke1 = idsE[0][j];
                ke2 = idsE[1][j];
                
                if ( ka1!=ke1 && ka2!=ke2 && ka1!=ke2 && ka2!=ke1 ) {
                    // System.out.println( lA + ":"+ Ai + "; \t" 
                    // + mrE.xLabels.elementAt(j) + ":" + Ej  );
                    cor = cor + ( (Ai-mwA) * (Ej-mwE) ); 
                    corAnz = corAnz+1;
                }
                else {
                    // System.err.println( lA + ":"+ Ai + "; \t"  
                    // + mrE.xLabels.elementAt(j) + ":" + Ej  );
                }
                if ( ci % 100000000 == 0 )  {
                    System.out.print( cor + "\t" + corAnz + " \t " + 
                            (ci / (1.0*total)) + " " + 
                            new Date( System.currentTimeMillis() ) );
                    System.out.println( "\t" + ka1 + "\t"+ ka2 + 
                                        "\t" + ke1 + "\t" + ke2  );
                }
            }            
        }
        x = cor / corAnz;
        return x;
    }

    private static double calcKendallTau(TimeSeriesObject mrA, TimeSeriesObject mrE){
        double tau = 0.0;
        int nd = 0;
        int nc = 0;
                      
        int ci = 0;
        
        double[][] dA = mrA.getData();
        double[][] dE = mrE.getData();
                
        int total = mrA.yValues.size() * mrE.yValues.size();
        System.out.println( "ANZ=" + total );
        System.out.println( "ANZ(A)=" + mrA.yValues.size() );
        for( int i = 0; i < mrA.yValues.size()-1 ; i++ ) {
            for( int j = 0; j < mrE.yValues.size()-1 ; j++ ) {
                ci++;
                
                // Linkstaerke zweier aufeinander folgender Links in der 
                // Linkliste beider Netzwerke
                double Ai = dA[1][i];
                double Aj = dA[1][i+1];
                
                double Ei = dE[1][j];
                double Ej = dE[1][j+1];
                
                if ( ( Ai > Aj && Ei > Ej ) || ( Ai < Aj && Ei < Ej) ){
                    nc = nc + 1;
                }
                else {
                    nd = nd + 1;
                }
                if ( ci % 100000000 == 0 )  {
                    System.out.println( nc + "\t" + nd + " \t " + 
                                      (ci / (1.0*total)) + " " + 
                                      new Date( System.currentTimeMillis()));
                }
            }            
        }
        tau = (double)( nc - nd ) / (double)( nc + nd );
        return tau;
    }

    private static int[][] convertIds(Vector<String[]> xLabels) {
        int[][] ids = new int[2][ xLabels.size() ];
        for ( int u = 0; u < xLabels.size(); u++ ) { 

            String[] l =  xLabels.elementAt(u);
            
            int ke1 = Integer.parseInt(l[0]);
            int ke2 = Integer.parseInt(l[1]);
            ids[0][u] = ke1;
            ids[1][u] = ke2;       
            //System.out.println( "u=" + u + "\t" + ids[0] + " : " + ids[1]);
        }        
        return ids;
    }
}
