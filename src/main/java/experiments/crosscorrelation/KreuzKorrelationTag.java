package experiments.crosscorrelation;

import research.sqlclient.phase2.Topic4;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.Enumeration;


public class KreuzKorrelationTag extends TimeSeriesObject {

    static int steps = 300;

    public TimeSeriesObject mr_x = null;
    public TimeSeriesObject mr_y = null;

    int k_min = -14;
    int k_max = 14;

    double avX = 0.0;
    double avY = 0.0;

    double t_max = 0.0;
    double tt_max = 0.0;

    /**
     *  für eine bestimmte Anzahl von tau bzw. k wird der
     *  Korrelationskoeffizient bestimmt und in der Wertetabelle
     *  abgelegt.
     */
    public void calcKR() {

        mr_x.calcAverage();
        mr_y.calcAverage();

        avX = mr_x.getAvarage();
        avY = mr_y.getAvarage();



        for( int i = k_min ; i < (k_max+1); i++ ) {

            double k = calcKR( mr_x, mr_y, i );

            if ( k > t_max ) {
                t_max = k;
                tt_max = i;
            }
            this.addValuePair(i, k);

        };
        // System.out.println( getResultLine() );

    }


    /**
     *  Hier wird der bestimmte einzelne Korrelationskoeffzient für
     *  ein k bestimmt.
     *
     * @param mr_x
     * @param mr_y
     * @param k
     * @return
     */
    private double calcKR(TimeSeriesObject mr_x, TimeSeriesObject mr_y, int k) {

        double[][] dataX = mr_x.getData();
        double[][] dataY = mr_y.getData();

        int max = steps;

        double summe = 0.0;

        for ( int t = 1; t < max; t++ ) {
            
            double xt = dataX[t][1] - avX;
            double yt = 0.0;

            if ( (k+t) > (max-1) ) { yt = 0.0; }
            else if ( (k+t) < 0 ) { yt = 0.0; }
            else {
                yt = dataY[t+k][1];
            };

            yt=yt-avY;

            summe=summe + xt*yt;

        };
        return summe;
    }

    public static void main( String[] args ) {

        TimeSeriesObject mr1 = new TimeSeriesObject();
        TimeSeriesObject mr2 = new TimeSeriesObject();

        KreuzKorrelationTag kr = new KreuzKorrelationTag();

            mr1.setLabel( "a" );
            mr2.setLabel( "b");
            kr.setLabel( "Kreuzkorrelation (a,b)");
        kr.mr_x = mr1;
        kr.mr_y = mr2;
        
        // dort muss dann das Maximum liegen
        int offset = 4;  // für die testdatenbestimmung ....

        for ( int i = 0; i < steps; i++ ) {
            if ( i > 10 ) {
                if ( i < 16 ) {
                    mr1.addValuePair(i, i-10);
                }
                else {
                    mr1.addValuePair(i, 0);
                }
            }
            else {
                mr1.addValuePair(i, 0);
            }

            if ( i > 10 + offset ) {
                if ( i < 16 + offset ) {
                    mr2.addValuePair(i, i-(10+offset));
                }
                else {
                    mr2.addValuePair(i, 0);
                }
            }
            else {
                mr2.addValuePair(i, 0);
            }
        };


        kr.calcKR();

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
        
        return Topic4.df.format( t_max ) + "\t" + 
               Topic4.df2.format(tt_max) + "\t" +
               sb.toString();
    };
}

