/*
 * Create Charts for the distribution of the <CC(tau)> values
 * for our link strength distributions ...
 *
 * Works just on the set of CC-Calculation results files!
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.io.ExcelProject;


/**
 *
 * @author kamir
 */
public class DistributionCheckChartsDetailedV2 {

//    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau");
    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau_v2");

    static String inputset = "";
    
    static double alpha = 1.00;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        
        boolean storSignificanz = false;
        

        String fnBASE = "P:/DATA/ETH/R20/"; // SP500_statistics.dat";
        inputset = "DAX";
        storSignificanz = true;
        
//        AnalysisFileFilter.shuffle = true;
//        doWorkNow(fnBASE, inputset, storSignificanz);
        
        AnalysisFileFilter.shuffle = false;
        doWorkNow(fnBASE, inputset, storSignificanz);
        
        
//       
//       storSignificanz = false;
//       doWorkNow( fnBASE, inputset,storSignificanz );
//        
//       fn =  "P:/DATA/ETH/R2/DAX_statistics.dat";
//       inputset = "DAX";
//
//       storSignificanz = true;
//       doWorkNow( fn, inputset,storSignificanz );
//       storSignificanz = false;
//       doWorkNow( fn, inputset,storSignificanz );

        pro.storeNow("Report3_" + inputset);
 
        
        inputset = "SP500";
        storSignificanz = true;
        AnalysisFileFilter.shuffle = false;
        doWorkNow(fnBASE, inputset, storSignificanz);

        inputset = "SP500";
//        storSignificanz = true;
//        AnalysisFileFilter.shuffle = true;
//        doWorkNow(fnBASE, inputset, storSignificanz);
// 
        //        pro.storeNow("Report2_" + inputset);

//        javax.swing.JOptionPane.showMessageDialog(null, "END");
//        System.exit(0);
    }
    
    static int tmax = 190;
    
    static double[][][] sumX = new double[11][5][3];
    static double[][][] sumXX = new double[11][5][3];
    static double[][][][] sumX2 = new double[tmax][11][5][3];
    static double[][][][] sumXX2 = new double[tmax][11][5][3];
    
    static double[][][][] sumX3 = new double[tmax][11][5][3];
    static double[][][][] sumXX3 = new double[tmax][11][5][3];
    
    static double[][][] avCC = new double[11][5][3];
    static double[][][] counter = new double[11][5][3];
    
    static double[][][][] counter2 = new double[tmax][11][5][3];
    static double[][][] av = new double[11][5][3];
    
    static Messreihe[][] reihenMWArray = new Messreihe[3][5];
    static Messreihe[][] reihenSIGMAArray = new Messreihe[3][5];
    static Messreihe[][] reihenSIGMAArray2 = new Messreihe[3][5];
    static Messreihe[][] reihenSIGMAArray3 = new Messreihe[3][5];

    public static void doWorkNow(String fnBase, String inputset, boolean storSignificanz) throws FileNotFoundException, IOException {

        int[] keysR = {20, 40, 60, 80, 100};
        String[] keysC = {"tv" , "lrp"  , "abs_lrp"}; 
        
        Hashtable<String,Integer> keyMapR = new Hashtable<String,Integer>();
        keyMapR.put( keysC[0] , 0);
        keyMapR.put( keysC[1] , 1);
        keyMapR.put( keysC[2] , 2);
        
        
        for( int c = 0; c < 3; c++ ) {
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 11; i++) {
                
                    sumXX[i][j][c] = 0.0;
                    sumX[i][j][c] = 0.0;

                    for( int t = 0; t < tmax/10; t++ ) { 
                        sumXX2[t][i][j][c] = 0.0;  
                        sumX2[t][i][j][c] = 0.0;
                        counter2[t][i][j][c] = 0.0;
                    }    

                    avCC[i][j][c] = 0.0;
                    counter[i][j][c] = 0.0;
                    av[i][j][c] = 0.0;

                }
                   
                reihenMWArray[c][j] = new Messreihe("l=" + keysR[j] + ":" + keysC[c]);
                reihenSIGMAArray[c][j] = new Messreihe("l1=" + keysR[j] + ":" + keysC[c]);
                reihenSIGMAArray2[c][j] = new Messreihe("l2=" + keysR[j] + ":" + keysC[c]);
                reihenSIGMAArray3[c][j] = new Messreihe("l3=" + keysR[j] + ":" + keysC[c]);
            } 
        }

        int[] tau = {5};
        
        int[] nrMW_L = { 0,0,0,0,0 };

        Vector<String> keysAll = new Vector<String>();
        
        Hashtable<String, Hashtable<String, Messreihe>> charts = new Hashtable<String, Hashtable<String, Messreihe>>();
              

        // prepare the set of charts ...
        for (String key1 : keysC) {

            Hashtable<String, Messreihe> reihen = new Hashtable<String, Messreihe>();

            for (int key : keysR) {
                String theKey = key + ":" + key1;
                
                keysAll.add( theKey );
                
                Messreihe reihe = new Messreihe();
                reihe.setLabel("" + theKey);
                reihen.put("" + theKey, reihe);
                System.out.println("key: " + theKey );
                
            }

            charts.put(key1, reihen);
        }
        
        
        // load data ...
        File[] files = null;
        // lesen der Daten
        int z = 0;
        for (String keyA : keysAll) {
            
            System.out.println( "#{" + keyA + "}" );
            
            String[] kp = keyA.split(":");
            int key = Integer.parseInt( kp[0] );
            String c = kp[1];
            
            
            // TODO :: WARUM lese ich hier DREI MAL die selbe Datei? 
            
            AnalysisFileFilter ff = new AnalysisFileFilter(inputset, c, key, tau[0]);
            
            files = ff.getFiles(new File(fnBase));
            for (File f : files) {

                System.out.println( " *** ### " + f.getAbsolutePath() + "  (key=" + key +"; " + " c=" + c + ")" );

                BufferedReader br = new BufferedReader(new FileReader(f));
                
                String fn = f.getName().replace( "abs_lrp" , "abslrp" ); 
                int shift = getShiftOfWindow( fn );
                int win_length = getLengthOfWindow( fn ) - 1;
                
                
                nrMW_L[ win_length ] = nrMW_L[ win_length ] + 1;
                z = (key / 20) - 1;
                System.out.println( "### " +  key + " : " + c + " : " + z + " ###" );
                while (br.ready()) {
                    String line = br.readLine();
                    Record3 rec = processLine(line, key + "", c + "");

                    
                    Hashtable<String, Messreihe> temp = charts.get(rec.keyChart);

                    int r = keyMapR.get(c);                    
                    
                    Messreihe mr = temp.get(rec.keyReihe);

                    if (mr != null && rec.p1 > (1.0-alpha) && rec.p2 > (1.0-alpha) ) {
                        
                        try {

                        //System.out.println("> (" + rec.keyReihe + ") [" + rec.keyChart + "] " + rec.tau + "{" + line + "}" );
                        
                        avCC[ (int) rec.tau + 5][z][r] = avCC[ (int) rec.tau + 5][z][r] + rec.plot_cc;
                        counter[ (int) rec.tau + 5][z][r] = counter[ (int) rec.tau + 5][z][r] + 1;

                        sumX[ (int) rec.tau + 5][z][r] = sumX[ (int) rec.tau + 5][z][r] + rec.plot_cc;
                        sumXX[ (int) rec.tau + 5][z][r] = sumXX[ (int) rec.tau + 5][z][r] + rec.plot_cc * rec.plot_cc;

                        counter2[shift][ (int) rec.tau + 5][z][r] = counter2[shift][ (int) rec.tau + 5][z][r] + 1;

                        sumX2[shift][ (int) rec.tau + 5][z][r] = sumX2[shift][ (int) rec.tau + 5][z][r] + rec.plot_cc;
                        sumXX2[shift][ (int) rec.tau + 5][z][r] = sumXX2[shift][ (int) rec.tau + 5][z][r] + rec.plot_cc * rec.plot_cc;

                        mr.addValuePair(rec.tau + z * 0.1, rec.plot_cc);
                        
                        } 
                        catch(Exception ex) {
                            System.err.println( ex.toString() );
                            System.out.println("> " + z + " : " + rec.keyReihe + " " + rec.tau + "{" + line + "}" );
                        }

                    }
                    else {
                        System.err.print("nokey=" +  rec.keyReihe);
                    }
                }
            }
            // z++;
        } 


        for( int c = 0; c < 3; c++ ) {
            // Variant 1
            for (int j = 0; j < 5; j++) {  // fuer jede Fensterlänge ...
                for (int i = 0; i < 11; i++) {  // fuer jedes tau zw.  -5 ... 0 ... 5

                    if (counter[i][j][c] != 0.0) {
                        av[i][j][c] = avCC[i][j][c] / counter[i][j][c];
                    }

                    reihenMWArray[c][j].addValuePair(i - 5, av[i][j][c]);

                    // Variante 1
                    reihenSIGMAArray[c][j].addValuePair(i - 5, counter[i][j][c]);
                }
            }

            // Variante 2
            for (int j = 0; j < 5; j++) {  // fuer jede Fensterlänge ...
            //int j = 0;  // nur 20 er 
                for (int i = 0; i < 11; i++) {  // fuer jedes tau zw.  -5 ... 0 ... 5

                    // Variante 2

                    // MITTELWERT von SIGMA fuer jedes ZEITFENSTER über alle Kurse als Funktion von TAU
                    double mwSIGMA = 0.0;
                    double sum = 0.0;
                    int t = 0;
                    int zzz = 0;
                    for( t=0; t<tmax/10; t++){
                        double a = getSigma(counter2[t][i][j][c], sumX2[t][i][j][c], sumXX2[t][i][j][c]);
                        if ( ! Double.isNaN( a ) ) { 
                            sum = sum + a;
                            zzz++;
                        }
                        else {
                            // System.err.println( t + " " + i + " "+ j );
                        }
                    }
                    if( nrMW_L[j] != 0.0 ) {    
                        System.out.println( "tau=" + ( i-5 ) + "\t:\t" + sum + "  nr=" + nrMW_L[j] + " check:= "+ zzz );
                        // if ( zzz - nrMW_L[j] != 0 ) System.err.println( "ERROR" );
                        reihenSIGMAArray2[c][j].addValuePair(i - 5, sum / (1.0* nrMW_L[j]) );
                    }    


    //                // Variante 3
    //                // MITTELWERT von SIGMA fuer jede Aktien(gruppe) als Funktion von TAU
    //                reihenSIGMAArray3[j].addValuePair(i - 5, getSigma3(counter[i][j], sumX[i][j], sumXX[i][j]));

                }
            }
        }
        
        
        
        for( int i : nrMW_L ) System.out.println( "> " + i );
 
        for (String chartLabel : charts.keySet()) {
            int c = keyMapR.get(chartLabel);
            System.out.println(" **** " + chartLabel + " c=" + c );
            
            String FULLchartLabel = inputset + "_" + chartLabel + "_" + AnalysisFileFilter.shuffle;
                    
            String title = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +") alpha=" + alpha;
            String xLabel = "tau [days]";
            String yLabel = "<max(CC(tau))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = 0;
            MyXYPlot.yRangDEFAULT_MAX = 1;

            
            
            MyXYPlot.open(reihenMWArray[c], title, xLabel, yLabel, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_02");

            MultiChart.xRangDEFAULT_MIN = -5;
            MultiChart.xRangDEFAULT_MAX = 5;
            MultiChart.yRangDEFAULT_MIN = 0;
            MultiChart.yRangDEFAULT_MAX = 1;
            MultiChart.open(reihenMWArray[c], title, xLabel, yLabel, true );
            
            String title2 = "SUMME " + inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel2 = "tau [days]";
            String yLabel2 = "<stdev( CC(tau))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = -1;
            MyXYPlot.yRangDEFAULT_MAX = 1000;

            MyXYPlot.open(reihenSIGMAArray[c], title2, xLabel2, yLabel2, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_03");

            String title3 = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel3 = "tau [days]";
            String yLabel3 = "<stdev( CC(tau,t))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = 0.05;
            MyXYPlot.yRangDEFAULT_MAX = 0.25;

            // MyXYPlot.open(reihenSIGMAArray2, title3, xLabel3, yLabel3, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_04");

//            String title4 = inputset + " " + chartLabel;
//            String xLabel4 = "tau [days]";
//            String yLabel4 = "V3 <sigma CC(tau)>";
//
//            MyXYPlot.xRangDEFAULT_MIN = -6;
//            MyXYPlot.xRangDEFAULT_MAX = 7;
//            MyXYPlot.yRangDEFAULT_MIN = 0.05;
//            MyXYPlot.yRangDEFAULT_MAX = 0.25;

//            MyXYPlot.open(reihenSIGMAArray3, title4, xLabel4, yLabel4, true);
        }

//
//        for (String chartLabel : charts.keySet()) {
//
//            String FULLchartLabel = inputset + "_" + chartLabel + "_" + AnalysisFileFilter.shuffle;
//            
//            Hashtable<String, Messreihe> tempReihen = charts.get(chartLabel);
//
//            Messreihe[] reihenArray = new Messreihe[tempReihen.size()];
//
//            int i = 0;
//            for (String key : tempReihen.keySet()) {
//                reihenArray[ i] = tempReihen.get(key);
//                i++;
//            }
//            
//            String title = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
//            String xLabel = "tau [days]";
//            String yLabel = "max( CC(tau) )";
//
//            MyXYPlot.xRangDEFAULT_MIN = -6;
//            MyXYPlot.xRangDEFAULT_MAX = 7;
//            MyXYPlot.yRangDEFAULT_MIN = -1;
//            MyXYPlot.yRangDEFAULT_MAX = 1;
//
//            MyXYPlot.open(reihenArray, title, xLabel, yLabel, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_01" );
            
            

//           String label = "_dMW";
//           if ( storSignificanz ) label = "p_Tests";
//                      
//           File f2 = new File( "./ptest/" + inputset + "_"+ chartLabel + "_" + label + ".dat" );
//
//           MesswertTabelle mwt = new MesswertTabelle();
//           mwt.setLabel( f.getAbsolutePath() );
//           mwt.setMessReihen(reihenArray);
//
//           String header = MesswertTabelle.getCommentLine(   "dataset   : " + chartLabel  );
//           header = header + MesswertTabelle.getCommentLine( "inputfile : " + fn2   );
//           header = header + MesswertTabelle.getCommentLine( "inputset  : " + inputset   );
//           header = header + MesswertTabelle.getCommentLine( "probability of p-Test [class:DistributionCompare]"  );
//
//           if ( storSignificanz )
//               header = header + MesswertTabelle.getCommentLine( mwt.toSignificanzString() );
//
//           mwt.setHeader( header );
//           mwt.writeToFile();
//                      
//           
//           reihenArray = pro.sortRosByLabel_INTEGER(reihenArray);
//           String[] labels = {"20","40","60","80","100" };
//           pro.addMessreihenToBook( inputset + "_"+ chartLabel + "_" + label , reihenArray, labels);
//           

//        }
        System.out.println("> Nr of files: " + files.length);


    }

    private static Record3 processLine(String line, String recRowKey, String recChartKey) {

        Record3 rec = new Record3();

        line = line.replaceAll(" ", "\t");
        line = line.replaceAll(",", ".");

        StringTokenizer st = new StringTokenizer(line, "\t");
        // System.out.println( st.countTokens() + "{" + line + "}");

        String[] cc = new String[11];
        double[] vcc = new double[11];

        String stockName = st.nextToken();
        String wikiPageID = st.nextToken();
        String maxCC2 = st.nextToken();
        String maxCC = st.nextToken();

        for (int i = 0; i < 11; i++) {
            cc[i] = st.nextToken();
            vcc[i] = Double.parseDouble(cc[i]);
            // System.err.println( cc[i] );
        }
        
        rec.p1 = Double.parseDouble( st.nextToken() );
        rec.w1 = Double.parseDouble( st.nextToken() );
        rec.p2 = Double.parseDouble( st.nextToken() );
        rec.w2 = Double.parseDouble( st.nextToken() );
        
        int tau = getTau(vcc);

        rec.tau = tau;
        
        rec.max_cc = stdlib.StdStats.max(vcc);
        
//        rec.max_cc = stdlib.StdStats.mean(vcc);
                rec.plot_cc = vcc[tau + 5];
         
        rec.check = Double.parseDouble(maxCC);

        rec.keyChart = recChartKey;
        rec.keyReihe = recRowKey + ":" + recChartKey;

        return rec;
    }

    private static int getTau(double[] vcc) {
        int tau = 0;
        double ccMax = stdlib.StdStats.max(vcc);
        for (int i = 0; i < vcc.length; i++) {
            if (vcc[i] == ccMax) {
                tau = i;
            }
        }
        return tau - 5;
    }

    private static double getSigma(double n, double sx, double sxx) {
        double f = 1.0 / (n - 1.0);

        double sigma = Math.sqrt(f * (sxx - sx * sx / n));
        return sigma;
    }

    private static double getSigma2(double d, double d0, double d1) {
        return getSigma(d, d0, d1);
    }

    private static double getSigma3(double d, double d0, double d1) {
        return getSigma(d, d0, d1);
    }

    private static int getShiftOfWindow(String f) {
        String[] fn = f.split("_");
        String shift = fn[3].substring(0);
        System.out.println("shift=" + shift);
        return Integer.parseInt(shift) / 10;
    }

    private static int getLengthOfWindow(String f) {
        String[] fn = f.split("_");
        String shift = fn[4].substring(0);
        System.out.println("shift=" + shift);
        return Integer.parseInt(shift) / 20;
    }
}
