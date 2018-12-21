/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class SingleDistributionChecker {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String path = "P:\\DATA\\ETH\\R2\\";

        String inputset = "DAX";
        String[] keysC = { "tv", "lrp", "abs_lrp" };

        int w = 20;
        int t0 = 0;

        String chartLabel = keysC[0];

        int[] tau = {5};

        String fn = path + inputset + "_" + chartLabel + "_" + tau[0] + "_" + t0 + "_" + w + "_WIKI_CC_shuffle=false.dat" ;
        //          DAX_tv_5_0_20_WIKI_CC_shuffle=false.dat

        System.out.println( fn );

        BufferedReader br = new BufferedReader(new FileReader(fn));

        while( br.ready() ) {
            String line = br.readLine();
            Result.processLine(line);
        }

        Vector<TimeSeriesObject> rows = new Vector<TimeSeriesObject>();
        for( String s : Result.results.keySet() ) {
            Result r = Result.results.get(s);
            TimeSeriesObject mr = r.getAverageTimeSeriesObject();

            rows.add( mr );
        }

        MultiChart.open2(rows, true);
    }
}

class Result {

    public Result( String comp ) {
        label = comp;
        for( int i = 0; i < 11; i++ ) {
            linesX[i] = 0;
            linesXX[i] = 0;
        }
    }

    static Hashtable<String, Result> results = new Hashtable<String,Result>();

    public static void processLine( String line ) {
        line = line.replaceAll(",",".");
        StringTokenizer st = new StringTokenizer(line);
        //System.out.println( line + " => " + st.countTokens() );
        String company = st.nextToken();
        String page = st.nextToken();
        String l2 = st.nextToken();
        String l3 = st.nextToken();
        Result r = results.get(company);
        if ( r == null ) {
            r = new Result( company );
            results.put(company, r);
        }
        for( int i = 0; i < 11; i++ ) {
            double x = Double.parseDouble( st.nextToken() );
            r.linesX[i] = r.linesX[i] + x;
            r.linesXX[i] = r.linesXX[i] + x*x;
            //System.out.println( x + "\t" + i + "\n");
        }
        r.counter++;

        r = results.get("all");
        if ( r == null ) {
            r = new Result( "all" );
            results.put("all", r);
        }
        st = new StringTokenizer(line);
        company = st.nextToken();
        page = st.nextToken();
        l2 = st.nextToken();
        l3 = st.nextToken();
        for( int i = 0; i < 11; i++ ) {
            double x = Double.parseDouble( st.nextToken() );
            r.linesX[i] = r.linesX[i] + x;
            r.linesXX[i] = r.linesXX[i] + x*x;
            r.counter++;
        }
    }

    String label = "?";

    double[] linesX = new double[11];
    double[] linesXX = new double[11];
    double counter = 0;

    public TimeSeriesObject getRow( String label ) {
        Result r = results.get(label);
        return r.getAverageTimeSeriesObject();
    }

    public TimeSeriesObject getAverageTimeSeriesObject() {
        TimeSeriesObject mr = new TimeSeriesObject();
        mr.setLabel( label + "anz:=" + counter );
        for( int i=0; i < 11; i++ ) {
            double mw = linesX[i] / counter;
            mr.addValuePair( -5.0 + i , mw);
        }
        return mr;
    };


}
