/*
 * We compare some distributions ...
 */
package com.cloudera.wikipedia.explorer;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Vector;
import org.apache.xalan.lib.Redirect;
import org.openide.util.Exceptions;

/**
 *
 * @author kamir
 */
class KolmogorowSmirnowTester2 {

    public static void main(String[] args) {

        stdlib.StdRandom.initRandomGen(1);
        Messreihe mr1 = Messreihe.getGaussianDistribution2(1000, 2.0, 1.0);
        Messreihe mr2 = Messreihe.getGaussianDistribution2(2000, -2.0, 1.0);

        KolmogorowSmirnowTester2.test(mr1.yValues, mr2.yValues);
    }

    static void test2(String LABEL, String header, String run) throws IOException {

        File f = new File("KS.TEST.RESULT." + LABEL);

        FileWriter fw = new FileWriter(f);
        fw.write(header + "\n");

        String[] labels = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};

        for (int i = 0; i < d1ns.size(); i++) {

            StringBuffer sb = new StringBuffer();

            Vector<Double> ns = (Vector<Double>) d1ns.elementAt(i);
            Vector<Double> s = (Vector<Double>) d1s.elementAt(i);

            sb.append("(*) i=" + i + "\t" + ns.size() + "\t" + s.size() + "\t\t");

            String result = getTestResult(ns, s, labels[i] + "." + i, run);
            sb.append(result + "\n");

            double[] borders = {-100, 2.5, 100};
            for (int ii = 1; ii < borders.length; ii++) {
                double min = borders[ii - 1];
                double max = borders[ii];

                sb.append("\t\t\t\t" + getTestResult(ns, s, labels[i] + "." + i + "." + ii, run, min, max) + "\n");

            }

            fw.write(sb.toString());

            System.out.println("> \t" + labels[i] + "\t" + sb.toString() + "\t");
        }

        fw.flush();
        fw.close();
        System.out.println(">>> " + f.getAbsolutePath());

    }
    static Vector<Vector<Double>> d1ns = null;
    static Vector<Vector<Double>> d1s = null;

    static void setShuffledData(Vector<Vector<Double>> d1sA) {
        d1s = d1sA;
    }

    static void setNonShuffledData(Vector<Vector<Double>> d1nsA) {
        d1ns = d1nsA;
    }

    private static void test(Vector<Double> m1, Vector<Double> m2) {
        Vector<Vector<Double>> a = new Vector<Vector<Double>>();
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);
        a.add(m1);


        Vector<Vector<Double>> b = new Vector<Vector<Double>>();
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);
        b.add(m2);

        setNonShuffledData(a);
        setShuffledData(b);

        try {
            test2("DEMO", "local test", "RUN");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static String getTestResult(Vector<Double> a, Vector<Double> b, String LABEL, String run) {
        try {
            KSTester t = new KSTester();

            return t._test2Side(a, b, LABEL, run);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "?";


    }

    private static Object getTestResult(Vector<Double> a, Vector<Double> b, String LABEL, String run, double min, double max) {
        try {
            KSTester t = new KSTester(min, max);
            return t._test2Side(a, b, LABEL, run);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "?";

    }
}
