/*
 * Create Charts for the distribution of the max(CC(tau)) values
 * for our link strength distributions ...
 *
 * Works on the CC-Calculation results files!
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;

import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import com.cloudera.wikiexplorer.ng.util.io.ExcelProject;


/**
 *
 * @author kamir
 */
public class DistributionCheckChartsDetailed {

//    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau");
    static ExcelProject pro = new ExcelProject("test_excel_influence_of_tau_2");

    static String inputset = "";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        
        boolean storSignificanz = false;
        

        String fnBASE = "P:/DATA/ETH/R11/"; // SP500_statistics.dat";
        inputset = "DAX";
        storSignificanz = true;
        
        AnalysisFileFilter.shuffle = true;
        doWorkNow(fnBASE, inputset, storSignificanz);
        
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

        pro.storeNow("Report2_" + inputset);
 
        
//        inputset = "SP500";
//        storSignificanz = true;
//        doWorkNow(fnBASE, inputset, storSignificanz);
//        pro.storeNow("Report2_" + inputset);

//        javax.swing.JOptionPane.showMessageDialog(null, "END");
//        System.exit(0);
    }
    
    static int tmax = 190;
    
    static double[][] sumX = new double[11][5];
    static double[][] sumXX = new double[11][5];
    static double[][][] sumX2 = new double[tmax][11][5];
    static double[][][] sumXX2 = new double[tmax][11][5];
    
    static double[][][] sumX3 = new double[tmax][11][5];
    static double[][][] sumXX3 = new double[tmax][11][5];
    
    static double[][] avCC = new double[11][5];
    static double[][] counter = new double[11][5];
    
    static double[][][] counter2 = new double[tmax][11][5];
    static double[][] av = new double[11][5];
    
    static TimeSeriesObject[] reihenMWArray = new TimeSeriesObject[5];
    static TimeSeriesObject[] reihenSIGMAArray = new TimeSeriesObject[5];
    static TimeSeriesObject[] reihenSIGMAArray2 = new TimeSeriesObject[5];
    static TimeSeriesObject[] reihenSIGMAArray3 = new TimeSeriesObject[5];

    public static void doWorkNow(String fnBase, String inputset, boolean storSignificanz) throws FileNotFoundException, IOException {

        int[] keysR = {20, 40, 60, 80, 100};

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 11; i++) {
                sumXX[i][j] = 0.0;
                sumX[i][j] = 0.0;
                
                for( int t = 0; t < tmax/10; t++ ) { 
                    sumXX2[t][i][j] = 0.0;  
                    sumX2[t][i][j] = 0.0;
                    counter2[t][i][j] = 0.0;
                }    
                
                avCC[i][j] = 0.0;
                counter[i][j] = 0.0;
                av[i][j] = 0.0;

            }
            reihenMWArray[j] = new TimeSeriesObject("l=" + keysR[j]);
            reihenSIGMAArray[j] = new TimeSeriesObject("l1=" + keysR[j]);
            reihenSIGMAArray2[j] = new TimeSeriesObject("l2=" + keysR[j]);
            reihenSIGMAArray3[j] = new TimeSeriesObject("l3=" + keysR[j]);
        }

        int[] tau = {5};
        
        int[] nrMW_L = { 0,0,0,0,0 };

        Hashtable<String, Hashtable<String, TimeSeriesObject>> charts = new Hashtable<String, Hashtable<String, TimeSeriesObject>>();
        String[] keysC = {"tv" , "lrp"  , "abs_lrp"};       

        for (String key1 : keysC) {

            Hashtable<String, TimeSeriesObject> reihen = new Hashtable<String, TimeSeriesObject>();

            for (int key : keysR) {
                TimeSeriesObject reihe = new TimeSeriesObject();
                reihe.setLabel("" + key);
                reihen.put("" + key, reihe);
                System.out.println("key: " + key + " ... " + key1 );
            }

            charts.put(key1, reihen);
        }

        File[] files = null;
        // lesen der Daten
        int z = 0;
        for (int key : keysR) {
            
//            for (String keyC : keysC) {
            String c = keysC[0];  
            
            
            // TODO :: WARUM lese ich hier DREI MAL die selbe Datei? 
            
            AnalysisFileFilter ff = new AnalysisFileFilter(inputset, c, key, tau[0]);
            
            files = ff.getFiles(new File(fnBase));
            for (File f : files) {

                System.out.println( " *** ### " + f.getAbsolutePath() + "  (" + key +")" + " c=" + c );

                BufferedReader br = new BufferedReader(new FileReader(f));
                
                String fn = f.getName().replace( "abs_lrp" , "abslrp" ); 
                int shift = getShiftOfWindow( fn );
                int win_length = getLengthOfWindow( fn ) - 1;
                
                
                nrMW_L[ win_length ] = nrMW_L[ win_length ] + 1;
                

                while (br.ready()) {
                    String line = br.readLine();
                    Record2 rec = processLine(line, key + "", c + "");

                    // 
                    Hashtable<String, TimeSeriesObject> temp = charts.get(rec.keyChart);

                    TimeSeriesObject mr = temp.get(rec.keyReihe);

                    if (mr != null) {

                        // System.out.println(">" + rec.keyReihe + " " + rec.tau );
                        avCC[ (int) rec.tau + 5][z] = avCC[ (int) rec.tau + 5][z] + rec.plot_cc;
                        counter[ (int) rec.tau + 5][z] = counter[ (int) rec.tau + 5][z] + 1;

                        sumX[ (int) rec.tau + 5][z] = sumX[ (int) rec.tau + 5][z] + rec.plot_cc;
                        sumXX[ (int) rec.tau + 5][z] = sumXX[ (int) rec.tau + 5][z] + rec.plot_cc * rec.plot_cc;

                        counter2[shift][ (int) rec.tau + 5][z] = counter2[shift][ (int) rec.tau + 5][z] + 1;

                        sumX2[shift][ (int) rec.tau + 5][z] = sumX2[shift][ (int) rec.tau + 5][z] + rec.plot_cc;
                        sumXX2[shift][ (int) rec.tau + 5][z] = sumXX2[shift][ (int) rec.tau + 5][z] + rec.plot_cc * rec.plot_cc;

                        mr.addValuePair(rec.tau + z * 0.1, rec.plot_cc);

                    }
                }
            }
            z++;
        }
//        }


        // Variant 1
        for (int j = 0; j < 5; j++) {  // fuer jede Fensterlänge ...
            for (int i = 0; i < 11; i++) {  // fuer jedes tau zw.  -5 ... 0 ... 5

                if (counter[i][j] != 0.0) {
                    av[i][j] = avCC[i][j] / counter[i][j];
                }
                reihenMWArray[j].addValuePair(i - 5, av[i][j]);

                // Variante 1
                reihenSIGMAArray[j].addValuePair(i - 5, getSigma(counter[i][j], sumX[i][j], sumXX[i][j]));

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
                    double a = getSigma(counter2[t][i][j], sumX2[t][i][j], sumXX2[t][i][j]);
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
                    if ( zzz - nrMW_L[j] != 0 ) System.err.println( "ERROR" );
                    reihenSIGMAArray2[j].addValuePair(i - 5, sum / (1.0* nrMW_L[j]) );
                }    


//                // Variante 3
//                // MITTELWERT von SIGMA fuer jede Aktien(gruppe) als Funktion von TAU
//                reihenSIGMAArray3[j].addValuePair(i - 5, getSigma3(counter[i][j], sumX[i][j], sumXX[i][j]));

            }
        }
        
        
        
        
        for( int i : nrMW_L ) System.out.println( i );
 
        for (String chartLabel : charts.keySet()) {

            String FULLchartLabel = inputset + "_" + chartLabel + "_" + AnalysisFileFilter.shuffle;
            
            
        
            String title = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel = "tau [days]";
            String yLabel = "<max(CC(tau))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = 0;
            MyXYPlot.yRangDEFAULT_MAX = 1;

            MyXYPlot.open(reihenMWArray, title, xLabel, yLabel, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_02");

            String title2 = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel2 = "tau [days]";
            String yLabel2 = "<stdev( CC(tau))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = 0.05;
            MyXYPlot.yRangDEFAULT_MAX = 0.25;

            MyXYPlot.open(reihenSIGMAArray, title2, xLabel2, yLabel2, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_03");

            String title3 = inputset + " " + chartLabel + " (" + AnalysisFileFilter.shuffle +")";
            String xLabel3 = "tau [days]";
            String yLabel3 = "<stdev( CC(tau,t))>";

            MyXYPlot.xRangDEFAULT_MIN = -6;
            MyXYPlot.xRangDEFAULT_MAX = 7;
            MyXYPlot.yRangDEFAULT_MIN = 0.05;
            MyXYPlot.yRangDEFAULT_MAX = 0.25;

            MyXYPlot.open(reihenSIGMAArray2, title3, xLabel3, yLabel3, true, new File( fnBase + "/report_2/") , FULLchartLabel + "_04");

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
//            Hashtable<String, TimeSeriesObject> tempReihen = charts.get(chartLabel);
//
//            TimeSeriesObject[] reihenArray = new TimeSeriesObject[tempReihen.size()];
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
//           mwt.setTimeSeriesObjectn(reihenArray);
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
//           pro.addTimeSeriesObjectnToBook( inputset + "_"+ chartLabel + "_" + label , reihenArray, labels);
//           

//        }
        System.out.println("> Nr of files: " + files.length);


    }

    private static Record2 processLine(String line, String recRowKey, String recChartKey) {

        Record2 rec = new Record2();

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

        int tau = getTau(vcc);

        rec.tau = tau;
        
        rec.max_cc = stdlib.StdStats.max(vcc);
        
//        rec.max_cc = stdlib.StdStats.mean(vcc);
        rec.plot_cc = rec.max_cc;
         
        rec.check = Double.parseDouble(maxCC);

        rec.keyChart = recChartKey;
        rec.keyReihe = recRowKey;

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
class Record2 {

    String keyReihe;
    String keyChart;
    double tau;
    double plot_cc;
    double check;
    
    double max_cc;
        
    double av_cc;
    double median_cc;
    

}