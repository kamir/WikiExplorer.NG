package experiments.crosscorrelation;

import research.sqlclient.phase2.Topic4;
import org.apache.hadoopts.chart.simple.MultiChart;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Es handelt sich hierbei um einen Datencontainer mit Funktionalität,
 * aus anderen Containeren des Typs TimeSeriesObject, eine Kreuzkorrelation
 * zu errechnen.
 *
 * @author kamir
 */

public class KreuzKorrelationStunde extends TimeSeriesObject {

    static public int tage = 299;
    static public int steps = tage*24;

    public TimeSeriesObject mr_x = null;
    public TimeSeriesObject mr_y = null;

    int k_min = -72;
    int k_max = 72;

    double avX = 0.0;
    double avY = 0.0;

    double t_max = -0.0;
    double tt_max = -0.0;

    public double get_k_MAX() {
        return tt_max;
    };

    /**
     *  Für eine bestimmte Anzahl von tau bzw. k wird der
     *  Korrelationskoeffizient bestimmt und in der Wertetabelle,
     *  die durch das Object (Instanz der Klasse "KreuzKorrelationStunde")
     *  abgelegt.
     */
    public void calcKR() throws Exception {

         int[] sx = mr_x.getSize();
         int[] sy = mr_y.getSize();

         int s1X = sx[0];
         int s1Y = sx[1];
         int dMR1 = s1X - s1Y;
         int s2X = sy[0];
         int s2Y = sy[1];
         int dMR2 = s2X - s2Y;

         if ( s1X == 0 ) throw new Exception( "MR1 hat 0 x-Werte" );
         if ( s1Y == 0 ) throw new Exception( "MR1 hat 0 y-Werte");
         if ( s2X == 0 ) throw new Exception( "MR2 hat 0 x-Werte");
         if ( s2Y == 0 ) throw new Exception( "MR2 hat 0 y-Werte");


         if ( dMR1 != 0 ) throw new Exception( "MR1 ist nicht ausgewogen ("+dMR1+").");
         if ( dMR2 != 0 ) throw new Exception( "MR2 ist nicht ausgewogen ("+dMR2+").");
         if ( dMR1 != dMR2 ) throw new Exception( "TimeSeriesObjectn sind nicht gleich lang. (" + s1X +"," +s2X +"," +s1Y +"," +s2Y +")." );

         //
//        mr_y.calcAverage();
//
//        avX = mr_x.getAvarage();
//        avY = mr_y.getAvarage();

        for( int i = k_min ; i < (k_max+1); i++ ) {

            double KR = calcKR( mr_x, mr_y, i );

            if ( KR > t_max ) {
                t_max = KR;
                tt_max = i;
            }
            addValuePair(i, KR);

        }
        System.out.println( getResultLine() );

    }


    /**
     *  Hier wird der bestimmte einzelne Korrelationskoeffzient R(k) für
     *  ein k bestimmt. Die Menge aller R(k) bildet die Kreuzkorrelations-
     *  funktion.
     *
     * @param mr_x
     * @param mr_y
     * @param k
     * @return
     */
    private double calcKR(TimeSeriesObject mr_x, TimeSeriesObject mr_y, int k) throws Exception {

        mr_y = mr_y.shift( k );
        
        mr_y.calcAverage();
        avY = mr_y.getAvarage();

        double[][] dataX = mr_x.getData();
        double[][] dataY = mr_y.getData();

        int max = mr_y.yValues.size();

        double summe = 0.0;

        for ( int t = 0; t < max; t++ ) {
            
            double xt = dataX[1][t];
            double yt = dataY[1][t];

            yt=yt-avY;

            summe=summe + xt*yt;
            //System.out.println( "\tk:" + k + "\tS:" + summe + "\tt:" + t + "\tx:" + xt + "\ty:" + yt  );

        };
        double n = (double)(max - k);
        return summe / (n);
    }

    public static void main( String[] args ) {

        try {
            TimeSeriesObject mr1 = new TimeSeriesObject();
            TimeSeriesObject mr2 = new TimeSeriesObject();
            KreuzKorrelationStunde kr = new KreuzKorrelationStunde();
            mr1.setLabel( "a" );
            mr2.setLabel( "b");
            kr.setLabel( "Kreuzkorrelation (a,b)");
            kr.mr_x = mr1;
            kr.mr_y = mr2;
            kr.k_min = -5;
            kr.k_max = 5;
            kr.steps = 40;

            // dort muss dann das Maximum liegen
            int offset = 2; // für die testdatenbestimmung ....
            for (int i = 0; i < steps; i++) {
                if (i > 10) {
                    if (i < 16) {
                        mr1.addValuePair(i, i - 10);
                    } else {
                        mr1.addValuePair(i, 0);
                    }
                } else {
                    mr1.addValuePair(i, 0);
                }
                if (i > 10 + offset) {
                    if (i < 16 + offset) {
                        mr2.addValuePair(i, i - (10 + offset));
                    } else {
                        mr2.addValuePair(i, 0);
                    }
                } else {
                    mr2.addValuePair(i, 0);
                }
            }
            ;
            kr.calcKR();
            Vector<TimeSeriesObject> vtKR = new Vector<TimeSeriesObject>();
            vtKR.add(kr);
            vtKR.add(mr1);
            vtKR.add(mr2);
            MultiChart mcKR = new MultiChart(null, true);
            mcKR.openNormalized(vtKR, "Korrelationsfunktion R(k)", "k", "R(k)", false);
//        mr1.calcAverage();
//        System.out.println( mr1.toString() );
//        System.out.println( mr1.getAvarage() );
//
//        mr2.calcAverage();
//        System.out.println( mr2.toString() );
//        System.out.println( mr2.getAvarage() );
            // System.out.println( kr.toString() );
        } catch (Exception ex) {
            Logger.getLogger(KreuzKorrelationStunde.class.getName()).log(Level.SEVERE, null, ex);
        }

//        mr1.calcAverage();
//        System.out.println( mr1.toString() );
//        System.out.println( mr1.getAvarage() );
//
//        mr2.calcAverage();
//        System.out.println( mr2.toString() );
//        System.out.println( mr2.getAvarage() );

        // System.out.println( kr.toString() );





    };

    public String getResultLine() {
        StringBuffer sb = new StringBuffer();
        Enumeration<Double> enumeration = this.yValues.elements();
        while( enumeration.hasMoreElements() ) {
            double d = enumeration.nextElement();
            sb.append( Topic4.df.format( d ));
            sb.append("\t");
        };
        return "t_max=" + Topic4.df.format( t_max ) + "\t" +
               "tt_max=" + Topic4.df2.format(tt_max) + "\t|\t" +
               sb.toString();
    };

    public void setScale( int scale ) {
        k_min = k_min / scale ;
        k_max = k_max / scale ;
        steps = steps / scale;
    };


}

