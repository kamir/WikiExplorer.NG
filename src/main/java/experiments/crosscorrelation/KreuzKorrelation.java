package experiments.crosscorrelation;

import org.apache.hadoopts.chart.simple.MultiChart;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.series.MessreiheFFT;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JFrame;
import experiments.linkstrength.CCFunction;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import org.apache.hadoopts.app.thesis.FFTPhaseRandomizer;
import org.apache.hadoopts.app.thesis.TSGenerator;
import org.apache.commons.math3.transform.TransformType;

/**
 * The result of cross-correlation-calculation is correlation function, which
 * is again an object of type Messreihe.
 *
 * Für Werte k_min ... k_max wird für jedes k der Korrelationskoeffizient
 * errechnet und als COEF( k ) abgespeichert.
 *
 * @author kamir
 */
public class KreuzKorrelation extends Messreihe {

    public static int defaultSR = 1;
    public static int debugCounter = 0;
    public static boolean debug = true;
    public static boolean GLdebug = false;
    public static int von;
    public static int bis;
    public static boolean chop;
    private static boolean notTested = true;
    
    // if this is false, we have also to change the range in the plots.
    public static boolean _DO_CALC_ADJUSTED = true;
    
    public static boolean DO_CALC_ADJUSTED_FFT_PR = false;
    
    public static boolean DO_CALC_ADJUSTED_SimpleShuffle = true;
    
    /**
     * MIN 3 für statistik !!!
     */
    private static int DO_CALC_ADJUSTED_z = 3; // nr of shuffelings

    public static String getCalcTypeLabel() {
        String t = DO_CALC_ADJUSTED_z + "-";
        t = t.concat(getAsInt(_DO_CALC_ADJUSTED));
        t = t.concat(getAsInt(DO_CALC_ADJUSTED_FFT_PR));
        t = t.concat(getAsInt(DO_CALC_ADJUSTED_SimpleShuffle));
        return t;
    }

    private static String getAsInt(boolean d) {
        if (d) {
            return "1";
        } else {
            return "0";
        }
    }
    public Messreihe mr_x = null;
    public Messreihe mr_y = null;
    // offset der Reihen aus der Wikipedia Analyse
    static int k_min = 0;
    static int k_max = 0;
    // Mittelwert der Reihen
    double avX = 0.0;
    double avY = 0.0;
    // Mittelwert der Reihen
    double stdAbwX = 0.0;
    double stdAbwY = 0.0;
    double stdAbwX_times_stdAbwY = 0;
    double t_max = 0.0;
    double tt_max = 0.0;
    
    public static int _defaultK = 7;

    public static void setK(int k) {
        setK(k, -1 * k);
    }

    public static void setK(int min, int max) {
        k_min = min;
        k_max = max;
    }
    
    public double _adjustedSIGMA = 1.0;
    public double adjustedCCSMEAN = 1.0;
    public double adjustedCC = 1.0;

    private void calcKR__adjusted(int z) throws Exception {
        Messreihe mr_xl = mr_x.copy();
        Messreihe mr_yl = mr_y.copy();

        double[] ls = new double[z];

        for (int i = 0; i < z; i++) {

            if (DO_CALC_ADJUSTED_SimpleShuffle) {
                
                mr_xl.shuffleYValues(z);
                mr_yl.shuffleYValues(z);
            
            } 
            else if (DO_CALC_ADJUSTED_FFT_PR) {
                /**
                 * Destroys the MultiFractal PROPERTIES
                 */
                mr_xl = FFTPhaseRandomizer.getPhaseRandomizedRow(mr_x, false, false, 0,FFTPhaseRandomizer.MODE_multiply_phase_with_random_value );
                mr_yl = FFTPhaseRandomizer.getPhaseRandomizedRow(mr_y, false, false, 0,FFTPhaseRandomizer.MODE_multiply_phase_with_random_value);

            }

            int[] sx = mr_xl.getSize();
            int[] sy = mr_yl.getSize();

            int s1X = sx[0];
            int s1Y = sx[1];
            int dMR1 = s1X - s1Y;
            int s2X = sy[0];
            int s2Y = sy[1];
            int dMR2 = s2X - s2Y;

            if (s1X == 0) {
                throw new Exception("MR1 hat 0 x-Werte ==> " + "{" 
                        + mr_xl.getLabel() + "; l=" + mr_xl.yValues.size() 
                        + " : " + mr_yl.getLabel() + "}");
            }
            if (s1Y == 0) {
                throw new Exception("MR1 hat 0 y-Werte");
            }
            if (s2X == 0) {
                throw new Exception("MR2 hat 0 x-Werte");
            }
            if (s2Y == 0) {
                throw new Exception("MR2 hat 0 y-Werte");
            }

            if (dMR1 != 0) {
                throw new Exception("MR1 ist nicht ausgewogen (" + dMR1 + ").");
            }
            if (dMR2 != 0) {
                throw new Exception("MR2 ist nicht ausgewogen (" + dMR2 + ").");
            }
            if (dMR1 != dMR2) {
                throw new Exception("Messreihen sind nicht gleich lang. (" 
                        + s1X + "," + s2X + "," + s1Y + "," + s2Y + ").");
            }

            ls[i] = calcKRCoeffizient(mr_xl, mr_yl, 0);
        }

        adjustedCCSMEAN = stdlib.StdStats.mean(ls);
        _adjustedSIGMA = stdlib.StdStats.stddev(ls);

    }

    /**
     * Für eine bestimmte Anzahl von tau bzw. k wird der Korrelationskoeffizient
     * bestimmt und in der Wertetabelle, die durch das Object (Instanz der
     * Klasse "KreuzKorrelation") abgelegt.
     */
    private void calcKR() throws Exception {

        int[] sx = mr_x.getSize();
        int[] sy = mr_y.getSize();

        if (debug) {
            System.out.print(sx[0] + "  " + sx[1] + " " + sy[0] + " " + sy[1]);
            System.out.println("\t" + k_min + "  " + k_max);
        }

        int s1X = sx[0];
        int s1Y = sx[1];
        int dMR1 = s1X - s1Y;
        int s2X = sy[0];
        int s2Y = sy[1];
        int dMR2 = s2X - s2Y;

        if (s1X == 0) {
            throw new Exception("MR1 hat 0 x-Werte ==> " 
                    + "{" + mr_x.getLabel() + "; l=" + mr_x.yValues.size() 
                    + " : " + mr_y.getLabel() + "}");
        }
        if (s1Y == 0) {
            throw new Exception("MR1 hat 0 y-Werte");
        }
        if (s2X == 0) {
            throw new Exception("MR2 hat 0 x-Werte");
        }
        if (s2Y == 0) {
            throw new Exception("MR2 hat 0 y-Werte");
        }


        if (dMR1 != 0) {
            throw new Exception("MR1 ist nicht ausgewogen (" + dMR1 + ").");
        }
        if (dMR2 != 0) {
            throw new Exception("MR2 ist nicht ausgewogen (" + dMR2 + ").");
        }
        if (dMR1 != dMR2) {
            throw new Exception("Messreihen sind nicht gleich lang. (" 
                    + s1X + "," + s2X + "," + s1Y + "," + s2Y + ").");
        }


        for (int i = k_min; i < (k_max + 1); i++) {
            // System.out.println( "k=" + i );
            double KR = calcKRCoeffizient(mr_x, mr_y, i);

            if (i == 0) {
                adjustedCC = KR;
            }

            if (KR > t_max) {
                t_max = KR;
                tt_max = i;
            }
            addValuePair(i, KR);
        }
//         System.out.println( "k_min = " + k_min );
//         System.out.println( getResultLine() );

    }
    /**
     * Hier wird der bestimmte einzelne Korrelationskoeffzient R(k) für ein k
     * bestimmt. Die Menge aller R(k) bildet die Kreuzkorrelations- funktion.
     *
     * @param mr_x
     * @param mr_y
     * @param k
     * @return
     */
    public static boolean shiftAndFillUpWithAvaerage = false;
    public static boolean shiftAndFillUpWithZero = true;
    public static boolean globalShuffle = false;
    public static int globalShuffleLoops = 150;

    private double calcKRCoeffizient(Messreihe mr_x, Messreihe mr_y, int k) 
            throws Exception {

        double doFillUpWith = 0.0;

        // StdAbw von beiden bestimmen
        stdAbwX = mr_x.getStddev();
        stdAbwY = mr_y.getStddev();

        double averR2 = mr_y.getAvarage();

        // Mittelwert von beiden abziehen
        Messreihe r1 = mr_x.subtractAverage();
        Messreihe r2 = mr_y.subtractAverage();

        int i = 0;
        if (globalShuffle) {
            while (i < globalShuffleLoops) {
                r2.shuffleYValues();
                r1.shuffleYValues();
                i++;
            }
        }

        if (shiftAndFillUpWithZero) {
            doFillUpWith = 0.0;
        } else if (shiftAndFillUpWithAvaerage) {
            doFillUpWith = averR2;
        }

        // eine Reihe verschieben und mit dem Mittelwert auffüllen
        if (k != 0) {
            r2 = r2.shift(k, doFillUpWith);
        }

//        if( k == 14 || k ==-14 ) {
//            Vector<Messreihe> reihen = new Vector<Messreihe>();
//            reihen.add(r1);
//            reihen.add(r2);
//            reihen.add( mr_x );
//            reihen.add( mr_y );
//
//            MultiChart.open(reihen, "TEST k=" + k, "s", "F(s)", true, "");
//        }

        stdAbwX_times_stdAbwY = stdAbwX * stdAbwY;

        // Produkt der StdAbw ermitteln


        double[][] dataX = r1.getData();
        double[][] dataY = r2.getData();

        int max = 0;
        int max1 = r1.yValues.size();
        int max2 = r2.yValues.size();

        if (max1 == max2) {
            max = max1;
        }
        if (max1 > max2) {
            max = max2;
        }
        if (max1 < max2) {
            max = max1;
        }


        double summe = 0.0;
        int nn = 0;

        // Schleife über alle Elemente
        for (int t = 0; t < max; t++) {

            double xt = dataX[1][t];
            double yt = dataY[1][t];

            double koeffizient = xt * yt / (stdAbwX_times_stdAbwY);
            summe = summe + koeffizient;
            nn = nn + 1;
        }
        double n = (double) (max - k);

        return summe / ((double) nn - 1);
    }

    public static void main(String[] args) {

        debug = true;

        try {

            Messreihe mr1 = new Messreihe();
            Messreihe mr2 = new Messreihe();

            /**
             * a, phi, constante, trend
             */
            int dSR = 10000;
            KreuzKorrelation.defaultSR = dSR;

            mr1 = TSGenerator.getSinusWave(140, 2 * Math.PI, dSR, 1.0);
            mr2 = TSGenerator.getSinusWave(3000, 2 * Math.PI, dSR, 2.0);


            // je stärker der Trend, umso kleiner wird kr

            KreuzKorrelation._defaultK = 25;
            KreuzKorrelation.GLdebug = debug;

            KreuzKorrelation kr = new KreuzKorrelation();

            mr1.label = "a";
            mr2.label = "b";

            kr.label = "Kreuzkorrelation (a,b)";

            kr.mr_x = mr1;
            kr.mr_y = mr2;

//            // dort muss dann das Maximum liegen
//            int offset = 4; // für die testdatenbestimmung ....
//            for (int i = 0; i < 20; i++) {
//                if (i > 10) {
//                    if (i < 16) {
//                        mr1.addValuePair(i, i - 10);
//                    } else {
//                        mr1.addValuePair(i, 0);
//                    }
//                } else {
//                    mr1.addValuePair(i, 0);
//                }
//                if (i > 10 + offset) {
//                    if (i < 16 + offset) {
//                        mr2.addValuePair(i, -1.0 * ( i + (10 + offset) ) );
//                    } else {
//                        mr2.addValuePair(i, 0);
//                    }
//                } else {
//                    mr2.addValuePair(i, 0);
//                }
//            }


//                        // dort muss dann das Maximum liegen
//            int offset = 4; // für die testdatenbestimmung ....
//            for (int i = 0; i < 20; i++) {
//                if (i > 3) {
//                    if (i < 7) {
//                        mr1.addValuePair(i, 5);
//                    } else {
//                        mr1.addValuePair(i, 0);
//                    }
//                } else {
//                    mr1.addValuePair(i, 0);
//                }
//                if (i > 3 + offset) {
//                    if (i < 7 + offset) {
//                        mr2.addValuePair(i,  2 );
//                    } else {
//                        mr2.addValuePair(i, 0);
//                    }
//                } else {
//                    mr2.addValuePair(i, 0);
//                }
//            }




            kr.calcKR(mr1, mr2, true, false);

            Vector<Messreihe> v = new Vector<Messreihe>();
            v.add(mr1);
            v.add(mr2);
            // v.add( kr );

            Messreihe mw = Messreihe.calcAveragOfRows(v);

    KreuzKorrelation kkt1 = KreuzKorrelation.calcKR(mr1, mr1, false, false);
    KreuzKorrelation kkt2 = KreuzKorrelation.calcKR(mr2, mr2, false, false);

            if ( debug ) {
        
                System.out.println(kkt1.getYValueForX2(0.0));
                System.out.println(kkt2.getYValueForX2(0.0));
            }

        } catch (Exception ex) {
Logger.getLogger(KreuzKorrelation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Window is sliding over the full length .... with deley = 0;
     *
     * @param mr1
     * @param mr2
     * @param dt
     * @return
     */
    public static Messreihe calcKR_SLIDINGWINDOW(
            Messreihe _mr1, Messreihe _mr2, int window) {

        Messreihe result = new Messreihe();
        debug = false;
        KreuzKorrelation.setK(0);

        int von = 0; 
        int bis = von + window;
                
        while (bis < _mr1.xValues.size()) {


            Messreihe mr1 = _mr1.cutOut(von, bis);
            Messreihe mr2 = _mr2.cutOut(von, bis);

            KreuzKorrelation kr = new KreuzKorrelation();

            try {

                String a = mr1.getLabel();
                String b = mr2.getLabel();

                kr.label = "Kreuzkorrelation (" + a + "," + b + ")";

                kr.mr_x = mr1;
                kr.mr_y = mr2;

//            kr.k_min = -1 * mr1.xValues.size();
//            kr.k_max = mr1.xValues.size();

                kr.calcKR();
                
                double cc = (Double)kr.yValues.elementAt(0);
                
                double t = 1.0 * von + ( 1.0*window / 2.0 );
                result.addValuePair(t , cc );

                if (debug && debugCounter < 1) {
                    Vector<Messreihe> vtKR = new Vector<Messreihe>();
                    //vtKR.add(kr);
                    mr1.normalize();
                    mr2.normalize();

                    vtKR.add(mr1);
                    vtKR.add(mr2);
                    MultiChart mcKR = new MultiChart(null, true);
               mcKR.open(vtKR, "Korrelationsfunktion R(k)", "k", "R(k)", true);
                }
                debugCounter++;


            } catch (Exception ex) {
                Logger.getLogger(KreuzKorrelation.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            von++; 
            bis = von + window;

        }

        return result;
    }

    public static KreuzKorrelation calcKR(
            Messreihe mr1, Messreihe mr2, boolean debug) {

        KreuzKorrelation kr = new KreuzKorrelation();

        try {

            String a = mr1.getLabel();
            String b = mr2.getLabel();

            kr.label = "Kreuzkorrelation (" + a + "," + b + ")";

            kr.mr_x = mr1;
            kr.mr_y = mr2;

//            kr.k_min = -1 * mr1.xValues.size();
//            kr.k_max = mr1.xValues.size();

            kr.calcKR();

            if (debug && debugCounter < 1) {
                Vector<Messreihe> vtKR = new Vector<Messreihe>();
                //vtKR.add(kr);
                mr1.normalize();
                mr2.normalize();

                vtKR.add(mr1);
                vtKR.add(mr2);
                MultiChart mcKR = new MultiChart(null, true);
                mcKR.open(vtKR, "Korrelationsfunktion R(k)", "k", "R(k)", true);
            }
            debugCounter++;


        } 
        catch (Exception ex) {
        
        }

        return kr;
    }

    /**
     *
     * @param mr1
     * @param mr2
     *
     * @param debug - ture => Reihen und Kreizkorrelation als Diagramm anzeigen.
     *
     * @param noOfset - von -5 ... 5 wird verschoben, um den Effekt zu erahnen.
     *
     * @return
     */
    public static KreuzKorrelation calcKR(
            Messreihe mr1, Messreihe mr2, 
            boolean debug, boolean noOfset) {

        int dk = _defaultK; // damit man um NULL herum etwas sehen kann

        KreuzKorrelation kr = new KreuzKorrelation();

        if (chop) {
            mr1 = mr1.cutOut(von, bis);
            mr2 = mr2.cutOut(von, bis);
        }
        else {
        
        }

        int a1 = mr1.getSize()[0];
        int b1 = mr2.getSize()[0];

        mr1.calcAverage();
        mr2.calcAverage();

        
        try {
            String a = mr1.getLabel();
            String b = mr2.getLabel();

//            if (notTested) {
//                int k = dk;
//                int index = CCFunction.ID_TO_SELECT_CC_FROM;
//                 javax.swing.JOptionPane.showMessageDialog(
//                    new JFrame(), "k=[" + (-1.0 * k) + "..." + k + "], selected: " + index);
//
//                 System.out.println( 
//                        ">>>> k=[" + (-1.0 * k) + "..." + k 
//                        + "], selected: " + index);
//                notTested = false;
//            }
            
            
            kr.label = "CC(" + a + "," + b 
                    + ")[" + _defaultK + ":" 
                    + CCFunction.ID_TO_SELECT_CC_FROM + "]";

            kr.mr_x = mr1;
            kr.mr_y = mr2;

            kr.setK(-1 * dk, dk);

            // NORMALE CC berechnen
            kr.calcKR();

            // ADJUSTED KR BERECHNEN
            if (_DO_CALC_ADJUSTED) {
                kr.calcKR__adjusted(DO_CALC_ADJUSTED_z);
            }


//            // DATEN PRÜFEN
//            if (GLdebug && debugCounter < 1) {
//                Vector<Messreihe> vtKR = new Vector<Messreihe>();
//                Vector<Messreihe> testB = new Vector<Messreihe>();
//                vtKR.add(kr);
//
//
//
////                TSCompareForm.open( mr1 , mr2 );
////                
////                vtKR.add(mr1);
////                vtKR.add(mr2);
//             MultiChart mcKR = new MultiChart(null, true);
//             mcKR.openNormalized(vtKR, "Korrelationsfunktion R(k)", "k", "R(k)", true);
//             mcKR.open(vtKR, "Correlation function CC(k)", "k", "CC(k)", true);
//             debugCounter++;
//
//
//
//
////                //  check for dominant frequencies
////                Messreihe mrFFTA = new Messreihe();
////                Messreihe mrFFTB = new Messreihe();
////                MessreiheFFT mrFFT2 = MessreiheFFT.convertToMessreiheFFT(mr1);
////                MessreiheFFT mrFFT3 = MessreiheFFT.convertToMessreiheFFT(mr2);
////
////                
////                mrFFTA = MessreiheFFT.calcFFT(mrFFT2, defaultSR, TransformType.FORWARD);
////                testB.add(mrFFTA);
////
////                mrFFTB = MessreiheFFT.calcFFT(mrFFT3, defaultSR, TransformType.FORWARD);
////                testB.add(mrFFTB);
////
////                MultiChart.open(testB, "FFT tests",
////                        "f [Hz]", "c", true, "");
//
//
//            }
        } 
        catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return kr;
    };

    public static DecimalFormat df = new DecimalFormat("0.00000000");
    public static DecimalFormat df2 = new DecimalFormat("0.0");

    /**
     * Eine Textdarstellung der Kreuzkorrelationsrechnung
     *
     * @return
     */
    public String getResultLine() {
        StringBuffer sb = new StringBuffer();
        Enumeration<Double> enumeration = this.yValues.elements();
        while (enumeration.hasMoreElements()) {
            double d = enumeration.nextElement();
            sb.append(df.format(d));
            sb.append("\t");
        };
        return "t_max=" + df.format(t_max) + "\t"
                + "tt_max=" + df2.format(tt_max) + "\t|\t"
                + sb.toString();
    }

    ;
    
    /**
     * 
     * @return 
     */
    public String[] getResultLine2ARRAY() {

        String[] sb = new String[this.yValues.size()];

        int i = 0;
        Enumeration<Double> enumeration = this.yValues.elements();
        while (enumeration.hasMoreElements()) {
            double d = enumeration.nextElement();
            sb[i] = df.format(d);
            
            if( debug ) 
                System.out.print(sb[i] + "\t");

            i++;
        };
        if (debug) System.out.print("\n");
        return sb;
    }

    /**
     * Create a list of the function values ...
     * 
     * @return 
     */    
    public String getResultLineAsJSON() {
        StringBuffer sb = new StringBuffer();
        sb.append( "{ \"FCC\":\""  );
        
        Enumeration<Double> enumeration = this.yValues.elements();
        while (enumeration.hasMoreElements()) {
            double d = enumeration.nextElement();
            sb.append(df.format(d));
            sb.append(" ");
        };
        
        sb.append( "\"}");
        
        return sb.toString();
    }
    
    /**
     * Create a list of the function values ...
     * 
     * @return 
     */    
    public String getResultLine2() {
        StringBuffer sb = new StringBuffer();
        sb.append( "(" + this.yValues.size() + "values)\t");
        Enumeration<Double> enumeration = this.yValues.elements();
        while (enumeration.hasMoreElements()) {
            double d = enumeration.nextElement();
            sb.append(df.format(d));
            sb.append("\t");
        };
        return sb.toString();
    }

    



    /**
     * Eine Sinusfuntion mit
     *
     * Amplitude           a
     * Phase               phi
     * constantes_offset   constante
     * linearem_Trend      trend

     *
     *
     * @param a
     * @param phi
     * @param offset
     * @param constante
     * @param trend
     * @return
     */
    public static Messreihe getTestSinusFunktion(
            double a, double phi, double constante, double trend) {
        
        Messreihe mr = new Messreihe();
        for (int i = 0; i < 1000; i++) {
            mr.addValuePair(
                    
                    i / 10.0, 
                    
                    (trend * (double) i) 
                    + constante 
                    + a * Math.sin((double) i / 10.0 + phi));
        }
        
        return mr;
    }

}
