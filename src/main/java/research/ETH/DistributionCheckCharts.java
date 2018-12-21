/*
 * Create Charts for the statistical significanze test 
 * for our link strength distributions ...
 *
 * Works on the CC-Calculation "statistics" file!
 * ---> not on the link strength files.
 * 
 * ---> the value of tau is selected during calculation time of
 *      linkstrengths.
 */

package research.ETH;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import org.apache.hadoopts.data.export.MeasurementTable;
import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import com.cloudera.wikiexplorer.ng.util.io.ExcelProject;

/**
 *
 * @author kamir
 */
public class DistributionCheckCharts {

    static final String project = "test_excel_3";
    static ExcelProject pro = new ExcelProject( project );
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
               
       boolean storSignificanz;
       int selectedRun = 1;
       int selectedSet = 1;
       
       String fn =  "";
       String inputset = "";
       
       String[] runs = { "R2","R3" };
       String run = runs[selectedRun];
       
       String[] sets = { "DAX", "SP500" };
       String set = sets[selectedSet];

       fn =  "P:/DATA/ETH/"+run+"/" + set + "_statistics.dat";
       inputset = set;
       
       System.out.println( ">>> run=" + run + " Inputset: " + inputset );

       storSignificanz = true;
       doWorkNow( fn, inputset,storSignificanz, run );
       storSignificanz = false;
       doWorkNow( fn, inputset,storSignificanz, run );

       pro.storeNow("Report_"+run+"_"+inputset );
        
    }   
    
    public static void doWorkNow( String fn, String inputset, boolean storSignificanz, String run ) throws FileNotFoundException, IOException {  
       
       BufferedReader br = new BufferedReader( new FileReader( fn) );

       int[] tau = { 5 };

       Hashtable<String,Hashtable<String,TimeSeriesObject>> charts = new Hashtable<String,Hashtable<String,TimeSeriesObject>>();
       String[] keysC = { "tv", "lrp", "abs_lrp" };       
       
       for( String key1 : keysC ) {

           Hashtable<String,TimeSeriesObject> reihen = new Hashtable<String,TimeSeriesObject>();
           int[] keysR = {20, 40, 60, 80, 100};

           for( int key : keysR ) {
               TimeSeriesObject reihe = new TimeSeriesObject();
               reihe.setLabel( ""+key );
               reihen.put( ""+key, reihe );
               System.out.println( ""+key );
           }

           charts.put(key1, reihen);
       }

       // lesen der Daten

       while ( br.ready() ) {
            String line = br.readLine();
            Record rec = _processLine( line );

            Hashtable<String,TimeSeriesObject> temp = charts.get( rec.keyChart );

            TimeSeriesObject mr = temp.get( rec.keyReihe );

            if( mr != null ) {
                System.out.println( rec.keyReihe );
                
                // Significanz speichern
                if( storSignificanz )
                    mr.addValuePair( rec.t0 , rec.p );
                else
                    mr.addValuePair( rec.t0 , rec.dMW );
                
            }
       }

       for( String chartLabel : charts.keySet() ) {

           Hashtable<String,TimeSeriesObject> tempReihen = charts.get( chartLabel );

           TimeSeriesObject[] reihenArray = new TimeSeriesObject[ tempReihen.size() ];

           int i = 0;
           for( String key : tempReihen.keySet() ) {
                reihenArray[ i ] = tempReihen.get(key);
                i++;
           }

           String title = chartLabel;
           String xLabel = "offset [days]";
           String yLabel = "p T-Test";

           MyXYPlot.xRangDEFAULT_MIN = -2;
           MyXYPlot.xRangDEFAULT_MAX = 122;
           MyXYPlot.yRangDEFAULT_MIN = -0.01;
           MyXYPlot.yRangDEFAULT_MAX = 0.13;

           MyXYPlot.open(reihenArray, title, xLabel, yLabel , true, new File( "." ), "out.png" );
           
           String label = "_dMW";
           if ( storSignificanz ) label = "p_Tests";
           
           File f = new File( "./ptest/" + run + "_" + inputset + "_"+ chartLabel + "_" + label + ".dat" );

           MeasurementTable mwt = new MeasurementTable();
           mwt.setLabel( f.getAbsolutePath() );
           mwt.setMessReihen(reihenArray);

           String header = MeasurementTable.getCommentLine(   "dataset   : " + chartLabel  );
           header = header + MeasurementTable.getCommentLine( "inputfile : " + fn   );
           header = header + MeasurementTable.getCommentLine( "inputset  : " + inputset   );
           header = header + MeasurementTable.getCommentLine( "probability of p-Test [class:DistributionCompare]"  );

           if ( storSignificanz )
               header = header + MeasurementTable.getCommentLine( mwt.toSignificanzString() );

           mwt.setHeader( header );
           mwt.writeToFile();
                      
           reihenArray = pro.sortRosByLabel_INTEGER(reihenArray);
           String[] labels = {"20","40","60","80","100" };
           pro.addTimeSeriesObjectnToBook( inputset + "_"+ chartLabel + "_" + label , reihenArray, labels);
           
       }
       
    }

    /**
     * Wir verarbeiten hier das Statistic-File des RUNs
     * 
     * ==> Distribution Comparison wird bereits beim Berechnen der 
     *     Linkdaten durchgeführt.
     * 
     * @param line
     * @return 
     */
    private static Record _processLine(String line) {
        Record rec = new Record();
        StringTokenizer st = new StringTokenizer( line , " " );
        System.out.println( st.countTokens() + "\t" + line );

        String o = st.nextToken();
        String n = st.nextToken();
        String k = st.nextToken();
        rec.keyChart = st.nextToken();
        String anz1 = st.nextToken();
        String anz2 = st.nextToken();
        String sp = st.nextToken();       // 7 String für p
        String st8 = st.nextToken();      //  
        String st9 = st.nextToken();      // 9 av1
        String st10 = st.nextToken();      // 10  
        String st11 = st.nextToken();      // 11  
        String st12 = st.nextToken();      // 12  
        String st13 = st.nextToken();      // 13 av2 
        
        double dMW = Double.parseDouble(st13) - Double.parseDouble(st9);
        
        rec.p = (double)Float.parseFloat(sp);
        rec.t0 = Double.parseDouble( o );
        rec.keyReihe = n;
        rec.dMW = dMW;
        return rec;
    }

}

class Record {
    String keyReihe;  // welche Reihe? 20, 40, 60, ...  
    String keyChart;  // Welches Diagramm? (tv, abs_lrp, lrp)
    double p;         // p-Value of Distribution comparison
    double t0;        // beginn des betrachteten Segments
    double dMW;       // Unterschied im Mittelwert der Verteilung
}