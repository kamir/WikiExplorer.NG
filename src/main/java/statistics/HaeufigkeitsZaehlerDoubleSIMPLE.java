package statistics;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector; 
import org.apache.commons.math3.stat.Frequency;

/**
 *
 * @author kamir
 */
public class HaeufigkeitsZaehlerDoubleSIMPLE {

    public static double DEFAULT_min = -100.0;
    public static double DEFAULT_max = 100.0;
    public static int DEFAULT_intervalle = 200;

    public double min = -1.0;
    public double max = 1.0;
    public int intervalle = 50;

    public Frequency f = new Frequency();

    public Vector<Double> dists = new Vector<Double>();

    public String label = "HISTOGRAM";

    public Messreihe mr = null;
    public Messreihe mr2 = null;
    public String folder = ".";

    Messreihe mrCUMUL = new Messreihe();
    Messreihe mrCONF = new Messreihe();

    public double SUMME = 0.0;

    public static boolean debug = false;

    /**
     * Einfügen eines Wertes in die Haeufigkeitszählung ...
     *
     * @param dates
     */
    public void addData(Double value) {
        dists.add(value);
    }

    ;

    /**
     * Einfügen einer Reihe von Zeitpunkten und
     * Berechnung aller Abstände aller Zeitpunkte dieser Reihe.
     *
     * @param dates
     */
    public void addData(Vector<Double> dates) {

        int anzahl = dates.size();

        double[][] l = new double[anzahl][anzahl];

        for (int i = 0; i < anzahl; i++) {
            for (int j = 0; j < anzahl; j++) {

                double timeA = dates.elementAt(i);
                double timeB = dates.elementAt(j);

                double dist = Math.abs(timeA - timeB);

                l[i][j] = dist;

                dists.add(dist);

                if (dist > max) {
                    max = dist;
                }
                if (dist < min) {
                    min = dist;
                }

                // System.out.println( dist );
            }
        }

    }

    public void calcWS() {

        mr = new Messreihe(label);
        int st = intervalle;

        int anz = dists.size();
        mr2 = new Messreihe(label + "/" + anz);

        Vector<Long> steps = new Vector<Long>();
        for (int i = 0; i < dists.size(); ++i) {
            f.addValue(Double.valueOf(dists.elementAt(i)));
        }

//        long a = min - 1;
//        long b = max + 1;
//
//        f.addValue(Double.valueOf( a ));
//        f.addValue(Double.valueOf( a ));
//        f.addValue(Double.valueOf( a ));
//        f.addValue(Double.valueOf( b ));
//        f.addValue(Double.valueOf( b ));
//        f.addValue(Double.valueOf( b ));
//        f.addValue(Double.valueOf( b ));
        
        min--;

        double delta = (max - min + 1) / st;

        if (debug) {
            System.out.println("DELTA: " + delta);
        }

        long vor = 0;

        mr.setLabel(label);

        for (int i = 0; i < st; i++) {

            double x = i * delta + min + 1;
            long w = f.getCumFreq(Double.valueOf(x));

            long y = w - vor;
            vor = w;

            if (debug) {
                System.out.println(x + "\t" + y + "\t" + vor);
            }

            mrCUMUL.addValuePair(x, vor);

            if (y != 0) {
                mr.addValuePair(x, y);
                double vv = y / (double) anz;
                mr2.addValuePair(x, vv);

            }

        }

        SUMME = vor;

        long f30 = f.getCumFreq(Double.valueOf(min));

        long f20 = f.getCumFreq(Double.valueOf(max));

        long f00 = f.getSumFreq();

        System.out.println("\n\n< min: " + (f30));
        System.out.println(">= min and < max: " + (f20 - f30));
        System.out.println(">= max  " + (f00 - f20));

        System.out.println("total " + (f00));

        System.out.println(this.getHistogram().toString());

    }

    public void store() throws IOException {
        FileWriter fw = new FileWriter(folder + label);
        fw.write(mr.toString());
        fw.flush();
        fw.close();
    }

    public Messreihe getConfidence() {
        mrCONF = new Messreihe("CONFIDENC Check");

        for (int i = 0; i < mrCUMUL.yValues.size(); i++) {
            Double perc = (Double) mrCUMUL.yValues.elementAt(i) / SUMME;
            if (perc.isInfinite()) {
                perc = 1.0;
            }

            System.out.println((Double) mr.xValues.elementAt(i) + "\t" + perc);
            this.mrCONF.addValuePair((Double) mr.xValues.elementAt(i), perc);
        }
        return mrCONF;
    }

    public Messreihe getHistogram(String l) {
        mr.setLabel("H_" + l);
        return mr;
    }

    public Messreihe getHistogram() {
        return mr;
    }

    public Messreihe getHistogramNORM(String l) {
        mr2.setLabel("H_" + l + "_NORM_" + "[" + dists.size() + "]");
        System.out.println(mr2.getLabel() + "[" + dists.size() + "]");
        return mr2;
    }

    public Messreihe getHistogramNORM() {
        return mr2;
    }
    
    public Messreihe getHistogram(double v) {
        return mr.scaleY_2(v);
    }
    

    /**
     * Return only values above a threshold TS
     * 
     * @param ts
     * @return 
     */
    public int getValueAboveTS(double ts) {
        int i = 0;
        for (double d : dists) {
            if (d > ts) {
                i = i + 1;
            }
        }
        return i;
    }

    /**
     * 
     * Return all "Non NaN elements of the dists Vector.
     * 
     * @return 
     */
    public double[] getDataArray() {

        int i = 0;

        Vector<Double> clear = new Vector<Double>();

        int allSize = this.dists.size();
        
        for (Double d : this.dists) {
            if (!d.isNaN()) {
                clear.add(d);
            }
        }
        int clearSize = clear.size();
 
        double[] data = new double[this.dists.size()];
        for (double d : clear) {
            data[i] = d;
            i++;
        }
        System.out.println( "all=" + allSize + " : clear=" +  clearSize );
        return data;
    }

}
