/*
 *  Lese die einzelnen Linkstärken-Liste ein,
 *      - ermittle die Linkstärke als Funktion der Verschiebung und zeige
 *        Scatterplot
 * 
 */

package research;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author kamir
 */
public class CheckInfluenceOfTau {

    public static void main( String[] args ) throws FileNotFoundException, IOException {

       BufferedReader br = new BufferedReader( new FileReader( "P:/DATA/ETH/DAX_statistics.dat" ) );

       int[] tau = { 5 };

       Hashtable<String,Hashtable<String,TimeSeriesObject>> charts = new Hashtable<String,Hashtable<String,TimeSeriesObject>>();
       String[] keysC = { "tv", "lrp", "abs_lrp" };

       for( String key1 : keysC ) {

           Hashtable<String,TimeSeriesObject> reihen = new Hashtable<String,TimeSeriesObject>();
           int[] keysR = { 30, 60, 85, 100 };

           for( int key : keysR ) {
               TimeSeriesObject reihe = new TimeSeriesObject();
               reihe.setLabel( "l="+key );
               reihen.put( ""+key , reihe );
               System.out.println( ""+key );
           }
           
           charts.put(key1, reihen);
       }

       // lesen der Daten

       while ( br.ready() ) {
            String line = br.readLine();
            Record rec = processLine( line );


            Hashtable<String,TimeSeriesObject> temp = charts.get( rec.keyChart );

            TimeSeriesObject mr = temp.get( rec.keyReihe );
            mr.addValuePair( rec.t0 , rec.p );
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
           MyXYPlot.yRangDEFAULT_MIN = 0;
           MyXYPlot.yRangDEFAULT_MAX = 0.13;

           MyXYPlot.open(reihenArray, title, xLabel, yLabel , true );
       }

    }

    private static Record processLine(String line) {
        Record rec = new Record();
        StringTokenizer st = new StringTokenizer( line , " " );
        // System.out.println( st.countTokens() );

        String o = st.nextToken();
        String n = st.nextToken();
        String k = st.nextToken();
        rec.keyChart = st.nextToken();
        String anz1 = st.nextToken();
        String anz2 = st.nextToken();
        String sp = st.nextToken();

        rec.p = (double)Float.parseFloat(sp);
        rec.t0 = Double.parseDouble( o );
        rec.keyReihe = n;
        return rec;
    }


}

class Record {
    
    String keyReihe;
    String keyChart;
    double p;
    double t0;

    

}
