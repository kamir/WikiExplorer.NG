/*
 * Create Charts for the distribution of the <CC(tau)> values
 * for our link strength distributions ...
 *
 * Works just on the set of CC-Calculation results files!
 * 
 * 
 * TODO 
 * 
 * 
 * Here we just use the average value of each tau ...
 * and we do show the distribution of the shapiro test results 
 * DEPENDENT ON THE SELECTED window-length:
 * 
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.MessIntervall;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import com.cloudera.wikiexplorer.ng.util.io.ExcelProject;


/**
 *
 * @author kamir
 */
public class DistributionCheckChartsDetailedV4 {

//    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau");
    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau_v2");

    static String inputset = "";
    
    static double alpha = 0.00;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
//        alpha = 2.00;        
//        mainLoop( "R11" , "DAX" );      
//        
        alpha = 1.00;        
        
        mainLoop( "R11" , "DAX" );        
//
//        alpha = 0.05;        
//        mainLoop( "R11" , "SP500" );
        
        tauSELECT = 0;
        alpha = 1.00;        
        mainLoop( "R11" , "SP500" );        

//        tauSELECT = 1;
//        alpha = 1.00;        
//        mainLoop( "R11" , "SP500" );        
//
//        tauSELECT = -2;
//        alpha = 1.00;        
//        mainLoop( "R11" , "SP500" );        

 
    }
    
    public static void mainLoop( String run, String inputset ) throws FileNotFoundException, IOException { 
        
        String fnBASE = "P:/DATA/ETH/" +run + "/";

        boolean storSignificanz = true;
        
        chartsV = new Hashtable<String,Vector<Messreihe>>();
        Vector v1 = new Vector<Messreihe>();
        Vector v2 = new Vector<Messreihe>();
        Vector v3 = new Vector<Messreihe>();
        chartsV.put( "tv",  v1 );
        chartsV.put( "lrp",  v2 );
        chartsV.put( "abs_lrp",  v3 );
        
        // Diagramme für SHUFFLE data or raw-data
//        AnalysisFileFilter.shuffle = true;
//        doWorkNow(fnBASE, inputset, storSignificanz);
        
        AnalysisFileFilter.shuffle = false;
        doWorkNow(fnBASE, inputset, storSignificanz);
        
        Vector<Messreihe> mrv1 = chartsV.get( "tv" );
        Vector<Messreihe> mrv2 = chartsV.get( "lrp" );
        Vector<Messreihe> mrv3 = chartsV.get( "abs_lrp" );
        
        int[] dt = {0,0,0,0,0,1,1,1,1,1};
        
        MultiChart._setTypes( dt );
        
        MultiChart.xRangDEFAULT_MIN = 0;
        MultiChart.xRangDEFAULT_MAX = 1;
        MultiChart.yRangDEFAULT_MIN = 0;
        MultiChart.yRangDEFAULT_MAX = 0.2;
        
                
        MultiChart.open(mrv1, inputset + " " + " tv : link-strength : (alpha="+alpha+") " + " tau=" + tauSELECT, "link strength" , "p1", true );
        MultiChart.open(mrv2, inputset + " " + " lrp : link-strength : (alpha="+alpha+")" + " tau=" + tauSELECT, "link strength" , "p1", true );
        MultiChart.open(mrv3, inputset + " " + " abs_lrp : link-strength : (alpha="+alpha+") " + " tau=" + tauSELECT, "link strength" , "p1", true );
          
        pro.storeNow("Report_v4_" + run + "_" + inputset);
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

    
    static Hashtable<String,HaeufigkeitsZaehlerDouble> hz = new Hashtable<String,HaeufigkeitsZaehlerDouble>();
    static Hashtable<String,Vector<Messreihe>> chartsV = null;
        
    public static void doWorkNow(String fnBase, String inputset, boolean storSignificanz) throws FileNotFoundException, IOException {

        int[] keysR = {20, 40, 60, 80, 100};
        String[] keysC = {"tv" , "lrp"  , "abs_lrp"}; 
        
        Hashtable<String,Integer> keyMapR = new Hashtable<String,Integer>();
        keyMapR.put( keysC[0] , 0);
        keyMapR.put( keysC[1] , 1);
        keyMapR.put( keysC[2] , 2);
        
        
        Hashtable<String, Hashtable<String, Messreihe>> charts = new Hashtable<String, Hashtable<String, Messreihe>>();
        
   
        for( int c = 0; c < 3; c++ ) {
            for (int j = 0; j < 5; j++) {
                
                for ( int i=0; i < 11; i++) {
                
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
                String key = keysR[j] + ":" + keysC[c];
                
                reihenMWArray[c][j] = new Messreihe("l=" + key );
                reihenSIGMAArray[c][j] = new Messreihe("l1=" + key );
                reihenSIGMAArray2[c][j] = new Messreihe("l2=" + key );
                reihenSIGMAArray3[c][j] = new Messreihe("l3=" + key );
                
                HaeufigkeitsZaehlerDouble hz1 = new HaeufigkeitsZaehlerDouble();
                hz.put( key , hz1 );
            } 

        }

        int[] tau = {5};
        
        int[] nrMW_L = { 0,0,0,0,0 };

        Vector<String> keysAll = new Vector<String>();
        
 
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
                    
                    // _storeShapiroTestValues( rec );
                    
                    _storeLinkStrengthValues(rec);
                    
                    Hashtable<String, Messreihe> temp = charts.get(rec.keyChart);

                    int r = keyMapR.get(c);                    
                    
                    Messreihe mr = temp.get(rec.keyReihe);

                    if (mr != null) {
                        
                        try {
                            if ( rec.p1 > (1.0-alpha) && rec.p2 > (1.0-alpha) ) {
                                //System.out.println("> (" + rec.keyReihe + ") [" + rec.keyChart + "] " + rec.tau + "{" + line + "}" );
                                for( int t = 0; t < 11; t++ ) { 


                                        avCC[t][z][r] = avCC[t][z][r] + rec.cc[t];
                                        counter[t][z][r] = counter[t][z][r] + 1;


                                    // mr.addValuePair(rec.tau + z * 0.1, rec.plot_cc);
                                }   
                            } 
                        } 
                        catch(Exception ex) {
                            System.err.println( ex.toString() );
                            System.out.println("> " + z + " : " + rec.keyReihe + " " + rec.tau + "{" + line + "}" );
                        }

                    }
                    else {
                        System.err.print("nokey=" +  rec.keyReihe);
                    }
                } // eine Datei komplett gelesen
                
                
                
                
            } // alle Dateien zu dem Key ... gelesen
            
            // Nun kann die Verteilung verarbeitet werden ...
                
            
            
            
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
                    reihenSIGMAArray[c][j].addValuePair(i - 5, getSigma(counter[i][j][c], sumX[i][j][c], sumXX[i][j][c]));
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
                    
            String title = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +") alpha=" + alpha + " tau=" + tauSELECT;
            String xLabel = "tau [days]";
            String yLabel = "<CC(tau)>";

            
            
            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = -0.2;
            MyXYPlot.yRangDEFAULT_MAX = 0.2;
            // MyXYPlot.open(reihenMWArray[c], title, xLabel, yLabel, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_02");
            
            
            
            MultiChart.xRangDEFAULT_MIN = -5;
            MultiChart.xRangDEFAULT_MAX = 5;
            MultiChart.yRangDEFAULT_MIN = -1;
            MultiChart.yRangDEFAULT_MAX = 1;
            MultiChart.open(reihenMWArray[c], title, xLabel, yLabel, true );
            
            
            
            String title2 = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel2 = "tau [days]";
            String yLabel2 = "<stdev( CC(tau))>";

            MyXYPlot.xRangDEFAULT_MIN = -5;
            MyXYPlot.xRangDEFAULT_MAX = 5;
            MyXYPlot.yRangDEFAULT_MIN = -1;
            MyXYPlot.yRangDEFAULT_MAX = 1;
            // MyXYPlot.open(reihenSIGMAArray[c], title2, xLabel2, yLabel2, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_03");

            String title3 = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel3 = "tau [days]";
            String yLabel3 = "<stdev( CC(tau,t))>";

            MyXYPlot.xRangDEFAULT_MIN = -5;
            MyXYPlot.xRangDEFAULT_MAX = 5;
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
         
       
         prozessHZ( "tv" , AnalysisFileFilter.shuffle );
         prozessHZ( "lrp" , AnalysisFileFilter.shuffle );
         prozessHZ( "abs_lrp" , AnalysisFileFilter.shuffle );
             
//        }
        System.out.println("> Nr of files: " + files.length);
    }
    
    public static void prozessHZ( String type, boolean shuffled ) { 
             
             Vector<Messreihe> mr = chartsV.get( type );
             Messreihe r = null;
             
             HaeufigkeitsZaehlerDouble hz1 = hz.get("20:" + type);
             HaeufigkeitsZaehlerDouble hz2 = hz.get("40:" + type);
             HaeufigkeitsZaehlerDouble hz3 = hz.get("60:" + type);
             HaeufigkeitsZaehlerDouble hz4 = hz.get("80:" + type);
             HaeufigkeitsZaehlerDouble hz5 = hz.get("100:" + type);
             
             hz1.calcWS();
             hz2.calcWS(); 
             hz3.calcWS(); 
             hz4.calcWS();
             hz5.calcWS();
              
             r = hz1.getHistogramNORM();
             r.setLabel( "20:" + type + " " + shuffled );
             mr.add(r);
             r = hz2.getHistogramNORM();
             r.setLabel( "40:" + type + " " + shuffled );
             mr.add(r);
             r = hz3.getHistogramNORM();
             r.setLabel( "60:" + type + " " + shuffled );
             mr.add(r);
             r = hz4.getHistogramNORM();
             r.setLabel( "80:" + type + " " + shuffled );
             mr.add(r);
             r = hz5.getHistogramNORM();
             r.setLabel( "100:" + type + " " + shuffled );
             mr.add(r);
             
    } 
    
    public static int tauSELECT = 0;

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
                
        rec.cc = vcc;

        int tau = getTau(vcc);

        rec.tau = tau;
        
        rec.max_cc = stdlib.StdStats.max(vcc);
        rec.max_cc2 = Double.parseDouble( maxCC2 );
                
        rec.LS = rec.calcLS();
        
        // Was soll dargestellt werden ?
        
//        rec.max_cc = stdlib.StdStats.mean(vcc);
        
//        rec.plot_cc = vcc[tauSELECT + 5];
//        rec.plot_cc = rec.max_cc;
//        rec.plot_cc = rec.max_cc2;
//        rec.plot_cc = rec.LS;
        
        rec.plot_cc = rec.p1;
//        rec.plot_cc = rec.p2;
        
         
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

    private static void _storeShapiroTestValues_STOCK(Record3 rec) {
        // System.err.println( rec.keyReihe );
        HaeufigkeitsZaehlerDouble z = hz.get(rec.keyReihe);
        z.addData( rec.p1 );
    }
    private static void _storeShapiroTestValues_WIKI(Record3 rec) {
        // System.err.println( rec.keyReihe );
        HaeufigkeitsZaehlerDouble z = hz.get(rec.keyReihe);
        z.addData( rec.p2 );
    }
    
    private static void _storeLinkStrengthValues(Record3 rec) {
//        if ( filterC( rec ) ) {
            // System.err.println( rec.keyReihe );
            HaeufigkeitsZaehlerDouble z = hz.get(rec.keyReihe);
            z.addData( rec.plot_cc );
//        }    
    }
    
    static double maxCC2_TS = 3.0;
    static boolean filterA( Record3 rec ) { 
        return rec.max_cc2 > maxCC2_TS;
    }
    
    static boolean filterB( Record3 rec ) { 
        boolean v = rec.LS > 0.5 && rec.LS < 1.2; 
        return v;
    }
        
    static boolean filterC( Record3 rec ) { 
        boolean v = rec.LS < -0.5 && rec.LS > -1.2; 
        return v;
    }
}

