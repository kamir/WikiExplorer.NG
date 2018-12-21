/*
 *  Ein Scatterplot wird logarithmisch dargestellt und die Werte in 
 *  einem Bereich über der X-Achse zu mitteln, damit nicht die Menge
 *  der Punkte das Ergebnis verfälscht.
 *
 */

package statistics;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Vector;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;

/**
 *
 * @author kamir
 */
public class ScatterPlotTrendCheck {

    double step = 1;
    int maxX = 10; // 10^10
    boolean debug = false;

    TimeSeriesObject rawdata = null;
    Vector<TimeSeriesObject> results = null;
    Vector<TimeSeriesObject> results2 = null;

    double[] upperBorders = null;
    double[] collectedValues = null;
    double[] counterOfValues = null;
    TimeSeriesObject[] valuesPerBin = null;

    String code = null;
    ScatterPlotTrendCheck(String string) {
        code = string;
        results = new Vector<TimeSeriesObject>();
        results2 = new Vector<TimeSeriesObject>();
    }

    ScatterPlotTrendCheck() {
         code = "";
         results = new Vector<TimeSeriesObject>();
         results2 = new Vector<TimeSeriesObject>();
    }

    public void init( TimeSeriesObject mr, String code ) {
        this.code = code;
        init( mr );
    }

    public void init(TimeSeriesObject mr ) {
        

        rawdata = mr;
        
        // wieviele bins?
        System.out.println( mr.getStatisticData("> "));
        
        upperBorders = new double[maxX];
        collectedValues = new double[maxX];
        counterOfValues = new double[maxX];
        valuesPerBin = new TimeSeriesObject[maxX];

        int nrBins = 0;
        int x = 0;
        while( x < maxX ) {
            upperBorders[nrBins] = x;
            valuesPerBin[nrBins] = new TimeSeriesObject();
            x = x + 1;
            if ( debug ) System.out.println( x );
            nrBins++;
        }

        // einsortieren
        for ( int i = 0; i < rawdata.yValues.size() ; i++ ) {

            double vx = (Double)rawdata.xValues.elementAt(i);
            double vy = (Double)rawdata.yValues.elementAt(i);
            if ( vx > 0.0 && vy > 0) {
                vx = Math.log10( vx );
                int binId = getBinId( vx );

                
                collectedValues[binId]=collectedValues[binId]+vy;
                counterOfValues[binId]=counterOfValues[binId]+1;

                valuesPerBin[binId].addValue(vy);
            }
            else {

            }
        }

        // MW bilden und Resultat zeigen ...
        TimeSeriesObject av = new TimeSeriesObject( code + "_average");
        TimeSeriesObject stdev = new TimeSeriesObject( code + "_stdev" );
        
        for ( int i = 1; i < upperBorders.length ; i++ ) {
            double vx = upperBorders[i];
            double vy = collectedValues[i] / counterOfValues[i];

            double stdevv = 0.0;
            try {
                stdevv = valuesPerBin[i].getStddev();
                av.addValuePair(vx, vy);
                stdev.addValuePair( vx , stdevv );

            }
            catch( Exception ex ) {
                System.out.println(">>> " + valuesPerBin[i].xValues.size() + " " + i + " " + upperBorders[i] );
            }
        }
        av.setLabel( av.getLabel() + " 1");
        stdev.setLabel( stdev.getLabel() + " 2");
        results.add(av);
        results.add(stdev);

        System.out.println( av );
        
        if (debug)System.out.println( av );
    };

    Vector<TimeSeriesObject> distributions = null;
    public void testPerBin() {

        distributions = new Vector<TimeSeriesObject>();

        TimeSeriesObject mw = new TimeSeriesObject("MW");
        TimeSeriesObject stdev = new TimeSeriesObject("STDEV");

        for( int i = 1; i < maxX ; i++ ) {
            double bL = Math.pow(10,i-1);
            double bH = Math.pow(10,i);

            BinResult br = calcPerBin( rawdata, bL , bH );
            double d[] = br.values;

            HaeufigkeitsZaehlerDouble zaehler = new HaeufigkeitsZaehlerDouble();
            zaehler.min = 0;
            zaehler.max = 1000;
            for ( double s : br.data ) {
                zaehler.addData( s );
            }
            zaehler.calcWS();
            TimeSeriesObject mr = zaehler.getHistogram();
            mr.setLabel( bH + " " );
            distributions.add( mr );

            System.out.println( bL + " " + bH + "\t" + d[0] +"\t" + d[1] );
            mw.addValuePair( Math.log10(bH) , d[0]  );
            stdev.addValuePair( Math.log10(bH) , d[1] );
        }

        stdev.setLabel( stdev.getLabel() + " 20");
        mw.setLabel( mw.getLabel() + " 10");

        results2.add(mw);
        results2.add(stdev);

        System.out.println( mw );
        
    };

    Vector<Double> data = null;

    public BinResult calcPerBin( TimeSeriesObject mr, double lB, double hB ) {
        double[] back = new double[2];
        data = new Vector<Double>();
        for( int i = 0 ; i < mr.yValues.size(); i++ ) {
            double x = (Double)mr.xValues.elementAt(i);
            double y = (Double)mr.yValues.elementAt(i);
            if ( x >= lB && x<hB ) {
                data.add(y);
            }
        }
        Double[] dataD = new Double[data.size()];
        data.copyInto(dataD);

        double[] ddd = new double[data.size()];
        int i = 0;
        for( double c : data ) {
            ddd[i] = c;
            i++;
        };

        if ( ddd.length > 0 ) {
        back[0] = stdlib.StdStats.mean(ddd);
        back[1] = stdlib.StdStats.stddev(ddd);
        }
        else {
            back[0] = 0;
            back[1] = 0;

        }

        BinResult r = new BinResult();
        r.values = back;
        r.data = data;

        System.out.println( data.size() + "\t" + lB );
        return r;
    };

    public void showChart() {
        MyXYPlot.xRangDEFAULT_MIN = 0 ;
        MyXYPlot.xRangDEFAULT_MAX = results.elementAt(0).getMaxX() ;
        MyXYPlot.yRangDEFAULT_MIN = 0 ;
        MyXYPlot.yRangDEFAULT_MAX = 1.5 * results.elementAt(0).getMaxY() ;
        MyXYPlot.open( results, "Edits vs. Access Activity", "average nr of ACCESS", "average nr of EDITS", true);
        MultiChart.open( results, "Edits vs. Access Activity", "average nr of ACCESS", "average nr of EDITS", true);
        MultiChart.open( results2, "Edits vs. Access Activity 2", "average nr of ACCESS", "average nr of EDITS", true);
        if ( distributions != null ) {
            MultiChart.open( distributions , "Histogram of edits per range", "nr od edits", "nr of nodes", true);
        }
    };

    public static void main( String[] args ) {
        stdlib.StdRandom.initRandomGen(1);
        TimeSeriesObject mrX = TimeSeriesObject.getGaussianDistribution( 100 , 1000 , 1 );
        TimeSeriesObject mrY = TimeSeriesObject.getGaussianDistribution( 100 , 10000 , 1 );
        TimeSeriesObject mrZ = mrX.combineAsXWithY(mrY);

        ScatterPlotTrendCheck check = new ScatterPlotTrendCheck( "test" );
        check.init(mrZ);
        check.testPerBin();
        check.showChart();

        Vector<TimeSeriesObject> v1 = new Vector<TimeSeriesObject>();
        v1.add( mrX );
        v1.add( mrY );
        //MultiChart.open(v1, true);

        Vector<TimeSeriesObject> v2 = new Vector<TimeSeriesObject>();
        v2.add( mrZ );
        // MyXYPlot.open(v2, "Scatterplot", "mrx", "mry", true);



    };

    private int getBinId(double x) {
        int id = 0;
        int i = 1;
        while ( i < upperBorders.length ) {
            
            boolean go = false;
            if (  x > upperBorders[i-1] ) {
                id = i;
                go = true;
            }
            if ( go ) {
                if ( debug ) System.out.print( x + "\t" + upperBorders[i] );
                if ( debug ) System.out.println( " " + go + " " + id );
            }
            i++;
        }
        return id;
    }

}

class BinResult {
    double[] values = null;
    Vector<Double> data = null;
}