/***
 * 
 * Das Tool dient der Berechnung der "Similarity of Networks"
 * und anderer Algorithmen.
 * 
 * 
 * 
 * 
 * doCalc( ... ) ist dazu die wesentliche Methode, um den Vergleich
 *               zweier Netzwerke, in Darstellung einer Messreihe
 *               zu berechnen.
 * 
 * 
 * 
 * 
 * 
 * 
 * Lade die Linkstärke-Tabelle von n Netzwerken - deren Links von 
 * PageID-Paaren und einer Linkstärke (String,Double) beschrieben 
 * werden - aus einer Tabelle und berechne die Kreuzkorrelation der 
 * jeweiligen Adjacency-Matrix, um die Netzwerke miteinander zu vergleichen.
 * 
 * 
 * TODO:
 * 
 * a) Kendall tau_coeffcient of two lists
 * b) Significanztest:
 *        Verschieben der Reihen und entfernen aller Link-Paare mit weniger 
 *        als 4 beteiligten Knoten (Formel sonst wie bei Kreuzkorrleation).
 * 
 * c) Normierung auf STDEV=1 vor Rechnung der CC
 * 
 * 
 */
package research.topics.networks.comparison;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import experiments.crosscorrelation.KreuzKorrelation;
import com.cloudera.wikiexplorer.ng.util.TimeLog;


/**
 *
 * @author kamir
 */
public class NetworkPairComparison {

    public static void main(String[] args) {
        debug = false;
        showRows = false;
        
        stdlib.StdRandom.initRandomGen(1);
        // Vector<Messreihe> vmr = loadRows();

        Vector<Messreihe> vmr = NetworkPairComparison.getTestData_A();
        Messreihe a = vmr.elementAt(0);
        Messreihe b = vmr.elementAt(1);

        Vector<Messreihe> vmr2 = NetworkPairComparison.getTestData_B();
        Messreihe a1 = vmr2.elementAt(0);
        Messreihe b1 = vmr2.elementAt(1);

        Vector<Messreihe> vmr3 = NetworkPairComparison.getTestData_C();
        Messreihe a2 = vmr3.elementAt(0);
        Messreihe b2 = vmr3.elementAt(1);
        
        Vector<Messreihe> vmr4 = NetworkPairComparison.getTestData_D();
        Messreihe d2 = vmr4.elementAt(0);
        Messreihe d3 = vmr4.elementAt(1);
//        
        Vector<Messreihe> vmr5 = NetworkPairComparison.getTestData_TM1();
        Messreihe a5 = vmr5.elementAt(0);
        Messreihe b5 = vmr5.elementAt(1);
        
        TimeLog tl = new TimeLog(true);
        
        doCalc(a, b , tl, null);
        doCalc(a1, b1, tl, null);
        doCalc(a2, b2, tl, null);
        doCalc(d2, d3, tl, null);
        doCalc(a5, b5, tl, null);
        
        tl.print(System.out);
        
        // System.exit(0);
        
    }
    
    
    
    static boolean showRows = false;
    static boolean debug = false;

    /**
     * Die beiden Messreihen müssen beide die gleichen geordneten Listen
     * von Keys haben. 
     * 
     * @param mrA
     * @param mrB 
     */
    public static double[] doCalc(Messreihe _mrA, Messreihe _mrB, TimeLog tl, NetworkComparator comp) {

        // umwandeln der Link-Liste in ein vollständige
        // Adjaceny Matrix
        Messreihe[] reihen = joinLabeledRows( _mrA, _mrB );
        
        Messreihe mrA = reihen[0];
        Messreihe mrB = reihen[1];
        
        if ( debug ) {
            System.out.println(mrA.getSize()[0]);
            System.out.println(mrB.getSize()[0]);
        };
                
        // dann arbeiten wir auf allen Paaren der Adjacency-Matrix Einträge
        boolean calcCC = true;
        boolean calcTAU = true;
        boolean useNormalizedRows = false;


        double ccAB = 0.0;
        double stAB = 0.0;
        double[] kendall_tau_AB = new double[4];

        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

//        Vector<Messreihe> mrv2 = new Vector<Messreihe>();
//        mrv2.add(mrA.normalizeToStdevIsOne());
//        mrv2.add(mrB.normalizeToStdevIsOne());

        if( debug ) {
            System.out.println(mrA.getStatisticData("[mrA : ]"));
            System.out.println(mrB.getStatisticData("[mrB : ]"));
        }
//        if (useNormalizedRows) {
//            mrA = mrA.normalizeToStdevIsOne();
//            mrB = mrB.normalizeToStdevIsOne();
//
//            if( debug ) {
//                System.out.println(mrA.getStatisticData("[mrA : ]"));
//                System.out.println(mrB.getStatisticData("[mrB : ]"));
//            }
//        }

        if (showRows) {
            MultiChart.open(mrv);
//            MultiChart.open(mrv2);
        }

//        StdRandom.initRandomGen(1);
//        Messreihe mrA = Messreihe.getGaussianDistribution(50);
//        Messreihe mrB = Messreihe.getGaussianDistribution(50);
//        Messreihe mrC = Messreihe.getGaussianDistribution(50);

        if ( comp != null ) calcCC = comp.doCalc( "cc" );
        if (calcCC) {
            KreuzKorrelation._defaultK = 0;

            KreuzKorrelation kkAB = KreuzKorrelation.calcKR(mrA, mrB, true);
            
            ccAB = (Double)kkAB.yValues.elementAt(0);
            stAB = calcSignifikanzTest(mrA, mrB);
        }
        tl.setStamp("\t cc=" + ccAB + " stAB=" +stAB );


        if ( comp != null ) calcTAU = comp.doCalc( "kendall_tau" );
        if (calcTAU) {
            kendall_tau_AB = calcKendallTau2(mrA, mrB);
        }
        tl.setStamp("\t kendall_tau=" + kendall_tau_AB[0] + "\t" + kendall_tau_AB[1] + "\t" + kendall_tau_AB[2] );

        DecimalFormat df = new DecimalFormat( "' '0.00000E00;'-'0.00000E00" );
        DecimalFormat df2 = new DecimalFormat( "' '0;'-'0" );
        DecimalFormat df3 = new DecimalFormat( "' '0.0000;'-'0.0000" );
        
        System.out.println(
           "\tccAB=" + df.format( ccAB ) + 
           "\tstAB=" + df.format( stAB ) + 
           "\ttau_AB=" + df3.format(kendall_tau_AB[0]) +
           "\tnc=" + df2.format(kendall_tau_AB[1]) +
           "\tnd=" + df2.format(kendall_tau_AB[2]) +
           "\t summe(nd,nc)=" + df2.format( kendall_tau_AB[1] + kendall_tau_AB[2] ) );        
        double[] v2 = new double[3];

        v2[0] = ccAB;
        v2[1] = stAB;
        v2[2] = kendall_tau_AB[0];
        
        
        return v2;

    }

    private static double calcSignifikanzTest(Messreihe mrA, Messreihe mrE) {

        double x = 0.0;
        double mwA = mrA.getAvarage2();
        double mwE = mrE.getAvarage2();

        double c = 0.0;
        double cor = 0.0;
        double corAnz = 0.0;
        int ci = 0;

        double[][] dA = mrA.getData();
        double[][] dE = mrE.getData();

        int[][] idsA = convertIds(mrA.xLabels);
        int[][] idsE = convertIds(mrE.xLabels);

        int ka1 = 0;
        int ka2 = 0;

        int ke1 = 0;
        int ke2 = 0;

        int total = mrA.yValues.size() * mrE.yValues.size();
        if ( debug ) System.out.println("ANZ=" + total);
        if ( debug ) System.out.println("ANZ(A)=" + mrA.yValues.size());
        for (int i = 0; i < mrA.yValues.size(); i++) {
            for (int j = 0; j < mrE.yValues.size(); j++) {
                ci++;
                double Ai = dA[1][i];
                double Ej = dE[1][j];

                ka1 = idsA[0][i];
                ka2 = idsA[1][i];

                ke1 = idsE[0][j];
                ke2 = idsE[1][j];

                if (ka1 != ke1 && ka2 != ke2 && ka1 != ke2 && ka2 != ke1) {
                    // System.out.println( lA + ":"+ Ai + "; \t" 
                    // + mrE.xLabels.elementAt(j) + ":" + Ej  );
                    cor = cor + ((Ai - mwA) * (Ej - mwE));
                    corAnz = corAnz + 1;
                } else {
                    // System.err.println( lA + ":"+ Ai + "; \t"  
                    // + mrE.xLabels.elementAt(j) + ":" + Ej  );
                }
                if (ci % 100000000 == 1) {
                    if ( debug ) System.out.print("[cc] " +cor + "\t" + 
                            corAnz + " \t " + (ci / (1.0 * total)) + " " +
                            new Date(System.currentTimeMillis()));
                    if ( debug ) System.out.println(  "\t" + ka1 + "\t" + 
                            ka2 + "\t" + ke1 + "\t" + ke2);
                }
            }
        }
        x = cor / corAnz;
        return x;
    }

    //
    private static double[] calcKendallTau2(Messreihe mrA, Messreihe mrE) {
        
        double[] dat = new double[4];
        
        Hashtable htA = mrA.hashedValues;
        
        double tau = 0.0;
        int nd = 0;
        int nc = 0;
        
        int ii = 0;
        int jj = 0;
        int nn = 0;
        
        for ( Object I : htA.keySet() ) { 
            ii++;
            for ( Object J : htA.keySet() ) { 
                jj++;
                
                String sI = (String)I;
                String sJ = (String)J;
                
                double Ai = (Double)mrA.hashedValues.get(I);
                double Aj = (Double)mrA.hashedValues.get(J);
                double Ei = (Double)mrE.hashedValues.get(I);
                double Ej = (Double)mrE.hashedValues.get(J);
                
                // boolean doNothing = sI.equals(sJ);

                // if ( !doNothing ) {
                    if ( (Ai > Aj && Ei > Ej) || ( (Ai < Aj && Ei < Ej) ) ) {
                        nc++;
                        if ( debug ) System.out.println( "nc " + ii + "(" + sI + ")" + ":" + jj + "(" + sJ + ")" +"\t"+ Ai + " " + Aj + " " + Ei + " " + Ej);
                
                    }
                    else if ( (Ai > Aj && Ei < Ej) || ( (Ai < Aj && Ei > Ej) ) ) {
                        nd++;
                        if ( debug ) System.out.println( "nd " + ii + "(" + sI + ")" + ":" + jj + "(" + sJ + ")" +"\t"+ Ai + " " + Aj + " " + Ei + " " + Ej);

                    }
                    else { nn++; };
//                }   
//                else {
//                        System.out.println( "##" + ii + "(" + sI + ")" + ":" + jj + "(" + sJ + ")" +"\t"+ Ai + " " + Aj + " " + Ei + " " + Ej);
//
//                }

                
            }   
            jj = 0;
        }

        
        tau = (double) (nc - nd) / (double) (nc + nd);
        dat[0] = tau;
        dat[1] = nc;
        dat[2] = nd;
        dat[3] = nn;
        
        return dat;
    }
    
    private static double calcKendallTau(Messreihe mrA, Messreihe mrE) {
        double tau = 0.0;
        int nd = 0;
        int nc = 0;

        int ci = 0;

        double[][] dA = mrA.getData();
        double[][] dE = mrE.getData();

        double numer = 0.0;
        double n = mrA.yValues.size();
        double nr = (0.5 * (double)n * (double)(n-1));
        
        int total = mrA.yValues.size() * mrE.yValues.size();
        
//        System.out.println("ANZ=" + total);
//        System.out.println("ANZ(A)=" + mrA.yValues.size());
        for (int i = 1; i < mrA.yValues.size(); i++) {
            for (int j = 1; j < mrE.yValues.size(); j++) {
                ci++;
                
                // Linkstärke zweier aufeinander folgender Links in der 
                // Linkliste beider Netzwerke
                
                
                
                double Ai = dA[1][i];
                double Aj = dA[1][i - 1];

                double Ei = dE[1][j];
                double Ej = dE[1][j - 1];
                
                numer = numer + Math.signum(Ai - Aj) * Math.signum(Ei - Ej);


                if ((Ai > Aj && Ei > Ej) || (Ai < Aj && Ei < Ej)) {
                    nc = nc + 1;
                } else {
                    nd = nd + 1;
                }
                if (ci % 100000000 == 1) {
                    if ( debug ) System.out.println("[tau] " + nc + "\t" + 
                            nd + " \t " + (ci / (1.0 * total)) + " " + 
                            new Date(System.currentTimeMillis()));
                }
            }
        }
//      tau = (double) (nc - nd) / (double) (nc + nd);
//      System.out.print("[tau] nr=" + nr + "\tnumer=" + numer + "\tn=" + n); 
        tau = (double)numer / (double)nr; 
        return tau;
    }

    private static int[][] convertIds(Vector<String[]> xLabels) {
        int[][] ids = new int[2][xLabels.size()];
        for (int u = 0; u < xLabels.size(); u++) {

            String[] l = xLabels.elementAt(u);

            int ke1 = Integer.parseInt(l[0]);
            int ke2 = Integer.parseInt(l[1]);
            ids[0][u] = ke1;
            ids[1][u] = ke2;
            //System.out.println( "u=" + u + "\t" + ids[0] + " : " + ids[1]);
        }
        return ids;
    }
    
    public static Vector<Messreihe> getTestData_C() {
        int n = 50;
        System.out.println( "C) - zufällige Linkstärken für n=" + n + " Werte.");

        
        Messreihe mrA = new Messreihe("C1");
        Messreihe mrB = new Messreihe("C2");

        
        
        Messreihe mrAr = Messreihe.getGaussianDistribution(n, 0.0, 0.2);
        Messreihe mrBr = Messreihe.getGaussianDistribution(n, 0.0, 0.2);
        

        for( int i = 0; i < n; i++ ) {
         Link l1 = new Link( ""+i, ""+i+1, (Double)mrAr.yValues.elementAt(i));
         Link l2 = new Link( ""+i, ""+i+1, (Double)mrBr.yValues.elementAt(i));
         mrA.addValue(l1.strength, l1.labels);
         mrB.addValue(l2.strength, l2.labels);
        }    


        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

        return mrv;
    }
    
    public static Vector<Messreihe> getTestData_B() {
        System.out.println( "B) - identische Beträge aber invertiert.");

        Messreihe mrA = new Messreihe("B1");
        Messreihe mrB = new Messreihe("B2");

        Link l1 = new Link("1", "2", 0.0);
        Link l2 = new Link("2", "3", 0.2);
        Link l3 = new Link("3", "4", 0.8);
        Link l4 = new Link("4", "5", 0.2);
        Link l5 = new Link("5", "6", 0.0);

        mrA.addValue(l1.strength, l1.labels);
        mrA.addValue(l2.strength, l2.labels);
        mrA.addValue(l3.strength, l3.labels);
        mrA.addValue(l4.strength, l4.labels);
        mrA.addValue(l5.strength, l5.labels);

        Link l10 = new Link("1", "2", 0.0);
        Link l20 = new Link("2", "3", -0.2);
        Link l30 = new Link("3", "4", -0.8);
        Link l40 = new Link("4", "5", -0.2);
        Link l50 = new Link("5", "6", 0.0);

        mrB.addValue(l10.strength, l10.labels);
        mrB.addValue(l20.strength, l20.labels);
        mrB.addValue(l30.strength, l30.labels);
        mrB.addValue(l40.strength, l40.labels);
        mrB.addValue(l50.strength, l50.labels);


        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

        return mrv;
    }

    public static Vector<Messreihe> getTestData_D() {
        System.out.println( "D) - verschobene identische Link-Listen");

        Messreihe mrA = new Messreihe("D1");
        Messreihe mrB = new Messreihe("D2");

        Link l1 = new Link("1", "2", 0.0);
        Link l2 = new Link("2", "3", 0.0);
        Link l3 = new Link("3", "4", 0.2);
        Link l4 = new Link("4", "5", 0.8);
        Link l5 = new Link("5", "6", 0.2);

        mrA.addValue(l1.strength, l1.labels);
        mrA.addValue(l2.strength, l2.labels);
        mrA.addValue(l3.strength, l3.labels);
        mrA.addValue(l4.strength, l4.labels);
        mrA.addValue(l5.strength, l5.labels);

        Link l10 = new Link("1", "2", 0.0);
        Link l20 = new Link("2", "3", 0.2);
        Link l30 = new Link("3", "4", 0.8);
        Link l40 = new Link("4", "5", 0.2);
        Link l50 = new Link("5", "6", 0.0);

        mrB.addValue(l10.strength, l10.labels);
        mrB.addValue(l20.strength, l20.labels);
        mrB.addValue(l30.strength, l30.labels);
        mrB.addValue(l40.strength, l40.labels);
        mrB.addValue(l50.strength, l50.labels);


        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

        return mrv;
    }
 
    public static Vector<Messreihe> getTestData_TM1() {
        System.out.println( "TM1) - TestMatrix");

        Messreihe mrA = new Messreihe("M1");
        Messreihe mrB = new Messreihe("M2");

        Link l1 = new Link("1", "2", 0.0);
        Link l2 = new Link("2", "3", 0.2);
        
        
        mrA.addValue(l1.strength, l1.labels);
        mrA.addValue(l2.strength, l2.labels);
        
        
        Link l10 = new Link("11", "12", 0.0);
        Link l20 = new Link("12", "13", 0.2);
        
        
        mrB.addValue(l10.strength, l10.labels);
        mrB.addValue(l20.strength, l20.labels);
        
        
        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

        return mrv;
    }

    public static Vector<Messreihe> getTestData_A() {
        System.out.println( "A) - identische Link-Listen");

        Messreihe mrA = new Messreihe("A1");
        Messreihe mrB = new Messreihe("A2");

        Link l1 = new Link("1", "2", 0.0);
        Link l2 = new Link("2", "3", 0.2);
        Link l3 = new Link("3", "4", 0.8);
        Link l4 = new Link("4", "5", 0.2);
        Link l5 = new Link("5", "6", 0.0);

        mrA.addValue(l1.strength, l1.labels);
        mrA.addValue(l2.strength, l2.labels);
        mrA.addValue(l3.strength, l3.labels);
        mrA.addValue(l4.strength, l4.labels);
        mrA.addValue(l5.strength, l5.labels);

        Link l10 = new Link("1", "2", 0.0);
        Link l20 = new Link("2", "3", 0.2);
        Link l30 = new Link("3", "4", 0.8);
        Link l40 = new Link("4", "5", 0.2);
        Link l50 = new Link("5", "6", 0.0);

        mrB.addValue(l10.strength, l10.labels);
        mrB.addValue(l20.strength, l20.labels);
        mrB.addValue(l30.strength, l30.labels);
        mrB.addValue(l40.strength, l40.labels);
        mrB.addValue(l50.strength, l50.labels);


        Vector<Messreihe> mrv = new Vector<Messreihe>();
        mrv.add(mrA);
        mrv.add(mrB);

        return mrv;
    }

    
    private static Messreihe[] joinLabeledRows(Messreihe _mrA, Messreihe _mrB) {
        Messreihe[] rows = new Messreihe[2];
        
        
        // sind die Keys schon gleich ???  ggf sind si noch nicht vollständig ...
//        boolean bothAreEqual = checkRows( _mrA, _mrB );
//        if ( bothAreEqual ) { 
//            rows[0] = _mrA;
//            rows[1] = _mrB;
//        }
//        else { 
//            rows = mergeRows( _mrA, _mrB );
//        }        
        rows = mergeRows( _mrA, _mrB );
        return rows;
        
    }

    /**
     * sind die Keys in beiden gleich ????
     * 
     * @param _mrA
     * @param _mrB
     * @return 
     */
    private static boolean checkRows(Messreihe _mrA, Messreihe _mrB) {
        boolean b = true;
        Hashtable t1 = _mrA.hashedValues;
        Hashtable t2 = _mrB.hashedValues;
        for( Object o : t1.keySet() ) { 
            String s = (String)o;
            if ( t2.containsKey(o) ) { 
                b = b && true;
            }
            else { 
                b = b && false;
                break;
            }
        }
        if ( b ) { 
            if( t1.size() == t2.size() ) {
                b = true;
            }
            else {
                b = false;
            }
        }        
        return b;
    }

    private static Messreihe[] mergeRows(Messreihe _mrA, Messreihe _mrB) {
        
        Messreihe[] rows = new Messreihe[2];
        
        Vector<String> allKeys = new Vector<String>();
        
        // alle Keys in mrA ermitteln ...
        Hashtable t1 = _mrA.hashedValues;
        Hashtable t2 = _mrB.hashedValues;
        
        System.out.println(" mrA: " + t1.size() + " links." );
        System.out.println(" mrB: " + t2.size() + " links." );
        
        for( Object o : t1.keySet() ) { 
            String s = (String)o;
            
            String[] ids = s.split("_");
            String id1 = ids[0];
            String id2 = ids[1];

            if ( !allKeys.contains( id1 ) ) allKeys.add( id1 );
            if ( !allKeys.contains( id2 ) ) allKeys.add( id2 );
        
        }
        // alle weiteren Keys in mrB ermitteln
        for( Object o : t2.keySet() ) { 
            String s = (String)o;
            
            String[] ids = s.split("_");
            String id1 = ids[0];
            String id2 = ids[1];

            if ( !allKeys.contains( id1 ) ) allKeys.add( id1 );
            if ( !allKeys.contains( id2 ) ) allKeys.add( id2 );
        }
        
        System.out.println(" nrOfKeys : " + allKeys.size() + " nr of keys.");

        
        Messreihe rA = new Messreihe();
        Messreihe rB = new Messreihe();
        
        
        // alle Paare ermitteln und mit null belegen bzw. den Wert aus mrA oder mrB einstellen
        Vector<String> allPairs = new Vector<String>();
        for( String id1 : allKeys ) { 
            for( String id2 : allKeys ) {
                String[] label = new String[2];
                label[0] = id1;
                label[1] = id2;
                
                String keyOfLink = id1+"_"+id2;
                double vA = 0.0;
                double vB = 0.0;
                if ( t1.containsKey( keyOfLink ) ) {
                    vA = (Double)t1.get( keyOfLink );
                }
                if ( t2.containsKey( keyOfLink ) ) {
                    vB = (Double)t2.get( keyOfLink );
                }
                rA.addValue(vA, label);
                rB.addValue(vB, label);
            }
        }
        rows[0] = rA;
        rows[1] = rB;
        
        System.out.println(" mrA : " + rA.hashedValues.size() + " links.");
        System.out.println(" mrB : " + rB.hashedValues.size() + " links.");

        
        return rows;
        
    }
}

class Link {
    Link(String labelA, String labelB, double s) {
        labels[0] = labelA;
        labels[1] = labelB;
        strength = s;
    }
    public double strength = 0.0;
    public String[] labels = new String[2];
}