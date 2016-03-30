/*
 *  Mit den Zeitreihen auf Stundenbasis werden die täglichen Kreuzkorrelationen
 *  berechnet.


[17.10. Mail von Jan (google Konto)


To define a systematic procedure, we must make some assumptions on the
way artifacts could occur.  For example assume that there is no time
delay as suggested by the prelimiary results.  Then calculate cross-
correlation (without time delay) for each day and get 300 values of
the cross-correlation function.

Instead of taking the average of all
these (which corresponds to the over-all cross-correlation function
for the considered two nodes) take the   m e d i a n   (center value of the
sorted list of all 300 values).  This is unaffected by all (possibly
artificial) highly correlated and all (also possibly artificial) very
lowly correlated days.

Besides I am no longer sure that we should focus on the significance
levels.  They may be very sensitive to artifacts, since a high signi-
ficance can appear, even if the total cross-correlation of a pair of
nodes is in fact very weak.  At least, significance level and absolute
cross-correlation strenght must be combinded to define reliable links.
 *
 *
 */

package experiments.crosscorrelation;

import org.apache.hadoopts.data.series.Messreihe;
import java.util.Arrays;
import java.util.Vector;
import research.wikinetworks.NodePair;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class DailyCrossCorrelationMapper {

    static public boolean debug = true;

    /**
     * The result is a value for the Correlation between two Nodes
     * during a given periode of Time, defined in the NodeGroup
     *
     * We Calculate the KreuzKorrelation with k=0 for every day andt pick
     * out the median value of the ordered list.
     *
     * This is the result.
     */
    public static double calcLinkStrength( Messreihe hourlyData_dayA, Messreihe hourlyData_dayB) {

        double v = 0.0;
        double cc[] = new double[NodeGroup.splitLength];
        for( int i = 0; i < NodeGroup.splitLength; i++ ) {

            int begin = i * 24;
            int ende = begin + 24;
            log( "Tag: " + i + " [" + begin+", " + ende+"]");
            Messreihe mwDA = hourlyData_dayA.cutOut(begin, ende);
            Messreihe mwDB = hourlyData_dayB.cutOut(begin, ende);

            KreuzKorrelation kr = KreuzKorrelation.calcKR(mwDA, mwDB, false, false);
            cc[i] = CheckInfluenceOfSingelPeaks.calcStrength(kr);
        }

        v = getMedianOfArray( cc );

        return v;
    }

        /**
     * The result is a value for the Correlation between two Nodes
     * during a given periode of Time, defined in the NodeGroup
     *
     * We Calculate the KreuzKorrelation with k=0 for every day andt pick
     * out the median value of the ordered list.
     *
     * This is the result.
     */
    public static double[] calcLinkStrength2( Messreihe hourlyData_dayA, Messreihe hourlyData_dayB) {

        double[] v = new double[5];

        // cc werte für tau = 0;
        double cc[] = new double[NodeGroup.splitLength];
        for( int i = 0; i < NodeGroup.splitLength; i++ ) {

            int begin = i * 24;
            int ende = begin + 24;
            log( "Tag: " + i + " [" + begin+", " + ende+"]");
            Messreihe mwDA = hourlyData_dayA.cutOut(begin, ende);
            Messreihe mwDB = hourlyData_dayB.cutOut(begin, ende);

            // nur einen Wert bestimmen ...
            KreuzKorrelation kr = KreuzKorrelation.calcKR(mwDA, mwDB, false, false);
            cc[i] = CheckInfluenceOfSingelPeaks.calcStrength(kr);
        }

        v[0] = getMedianOfArray( cc );
        v[1] = stdlib.StdStats.stddev( cc );
        v[2] = stdlib.StdStats.mean( cc );
        v[3] = v[1]/v[2];
        v[3] = v[2]/v[1];


        return v;
    }

    static void log(String d) {
        if ( debug) {
            System.out.println(">>> " +d);
        }
    }

    private static double getMedianOfArray(double[] cc) {
        double[] sorted = sort(cc);
        return median( sorted );
    }

    // the array double[] m MUST BE SORTED
    public static double median(double[] m) {
    int middle = m.length/2;
    if (m.length%2 == 1) {
        return m[middle];
    } else {
        return (m[middle-1] + m[middle]) / 2.0;
    }
}

    /**
     * http://www.java-examples.com/java-sort-double-array-example
     *
     * @param cc
     * @return
     */
    private static double[] sort(double[] cc) {
        Arrays.sort(cc);
        return cc;
    }


    public static void main( String[] args ) {

        DailyCrossCorrelationMapper.debug = false;
        NodeGroup.debug = false;

        KreuzKorrelation.debug = false;
        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
        NodeGroup.doSplitRows = true;
        NodeGroup.splitLength = 30;
        NodeGroup.splitIndex = 2;
        
        NodeGroup ng = new NodeGroup();
        ng.load();
        ng._initWhatToUse();

        ng.checkAccessTimeSeries();

        int c = 0;
        int w = 0;

        for( int split = 0; split < 9; split++ ) {

            NodeGroup.splitIndex = split;

            Messreihe histMaxY = new Messreihe("hist Max");
            Messreihe histSigLevel = new Messreihe("hist Strength");
            Vector<NodePair> wrong = new Vector<NodePair>();

            int max = ng.getAaccessReihen().size();
            for ( int i=0; i < max ; i++ ) {
                for ( int j=0; j < max ; j++ ) {


                    c++;
                    NodePair np = new NodePair();
                    np.id_A = i;
                    np.id_B = j;

                    String ccResult = "";
                    String ccLine = "";

                    boolean v = false;
                    if ( ng.doWorkWithPair(np) ) {
                        w = w + 1;
                        try {

                            v = np._calcCrossCorrelation( ng, ng.getAaccessReihen(), histMaxY, histSigLevel , wrong);

                        }
                        catch( Exception ex ) {
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                    }

                    // nur wenn korrekt berechnet und wenn auch Paar genutzt werden soll, gibt es eine Ausgabe.
                    if ( v ) {
                        ccResult = np.getResultOfCC();
                        ccLine = "[" + i + "," + j + "] \t " + ccResult;

                        System.out.println(w +
                                    " {*} MatrixID:=" + c + " " + "\t" + ccLine );
                    }
                }
            }
        }
    }
}
