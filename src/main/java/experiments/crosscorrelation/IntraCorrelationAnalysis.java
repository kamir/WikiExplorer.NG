package experiments.crosscorrelation;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.export.OriginProject;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;

import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.plugin.NodeColorTransformer;

import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;

import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder; 
import org.apache.hadoopts.app.thesis.TSGenerator;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.ImportController;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;

import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

/**
 *
 * @author kamir
 */
public class IntraCorrelationAnalysis {

    static int SCALE = 1; // 24
    static int nrA = 20;  // intra group
//    static int nrB = 10;  // external system
    static int z = 12 * 4 * 7 * SCALE;
    static int tau = 4 * 7 * SCALE;
    static int dtau = 1 * SCALE;       // this values are changed within the code later on !!!!
    static double f = 1.0 / 86400.0;
    static double sr = 1.0 / 3600.0;

    /**
     * Group A for a simple test with no coupling ...
     *
     * amp : amplitude of the base wave ampN : amplitude of the noise (added)
     *
     * @return
     */
    public static Vector<TimeSeriesObject> getSampleRowGroupA(double amp, double ampN, String label, String ext) {

        Vector<TimeSeriesObject> groupA = new Vector<TimeSeriesObject>();

        // a simple sine-wave no noise
        TimeSeriesObject r1 = TSGenerator.getSinusWave(f, z * 3600, sr, amp);

        // add some noise 
        for (int i = 0; i < nrA; i++) {

            TimeSeriesObject m1 = TimeSeriesObject.getGaussianDistribution(z, 0, ampN);

            TimeSeriesObject m2 = m1.add(r1);

            System.out.println((i + 1) + ".)\t" + m2.getLabel());

            groupA.add(m2);
        }
        if (sampleVisible) {
            plotAvAndSigma(groupA, ext + label);
        } else {
        };
        return groupA;
    }
    static boolean sampleVisible = false;

    private static void createCoupledGroupsA(Vector<TimeSeriesObject> groupA, Vector<TimeSeriesObject> groupB) {

        for (int cc = 0; cc < nrA; cc++) {

            int min = 100;
            int max = 200;
            double m = 2.75 / (max - min);

            TimeSeriesObject randA = TimeSeriesObject.getGaussianDistribution(z, 0, 0.00000000001);
            TimeSeriesObject randB = TimeSeriesObject.getGaussianDistribution(z, 0, 0.00000000001);

            TimeSeriesObject a = new TimeSeriesObject();
            TimeSeriesObject b = new TimeSeriesObject();


            double c = 0.5;

            for (int i = 0; i < z; i++) {
                double v = 0.0;
                if (i > min && i < max) {
                    v = m * i;
                }

                a.addValuePair(i, v);
                b.addValuePair(i, c * v);
            };


            TimeSeriesObject A = randA.add(a);
            TimeSeriesObject B = randB.add(b);

            groupA.add(A);
            groupB.add(B);

        }
        Vector<TimeSeriesObject> c = groupA;
        c.addAll(groupB);

        if (sampleVisible) {
            MultiChart.open(c, false);
            plotAvAndSigma(groupB, "run3.B");
            plotAvAndSigma(groupA, "run3.A");
        } else {
        };

    }

    /**
     * Group A for a simple test with no coupling ...
     *
     * amp : amplitude of the base wave ampN : amplitude of the noise (added)
     *
     * @return
     */
//    public static Vector<TimeSeriesObject> getSampleRowGroupB( double amp , double scaleF) {
//        
//        Vector<TimeSeriesObject> groupB = new Vector<TimeSeriesObject>();
//
//        TimeSeriesObject r2 = TSGenerator.getSinusWave(scaleF * f, z * 3600, sr, amp);
//        
//        for (int i = 0; i < nrB; i++) {
//            groupB.add(r2.add(TimeSeriesObject.getGaussianDistribution(z,0, 2)));
//        }
//
//        return groupB;
//    }
    public static Vector<TimeSeriesObject> getNoiseRowGroup() {

        Vector<TimeSeriesObject> groupB = new Vector<TimeSeriesObject>();

        for (int i = 0; i < nrA; i++) {
            groupB.add(TimeSeriesObject.getGaussianDistribution(z, 0.0, 1.0));
        }

        return groupB;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

//        int runs = 1;
//        debug = true;
//        verbose = true;

        int runs = 1;
        debug = false;
        verbose = true;

        /**
         * our samples are small, so we do not need sampling ...
         *
         */
        sampling = false;

        /**
         * TODO: should log the seed value of the random generator ...
         */
        stdlib.StdRandom.initRandomGen(1);

        OriginProject op = new OriginProject();
        op.initBaseFolder(null);

        initCharting();

        IntraCorrelationAnalysis.op = op;

        Vector<TimeSeriesObject> icfC = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> icfB = new Vector<TimeSeriesObject>();


///*DEMO 1*/    
        for (int i = 0; i < runs; i++) {
            if (i == 0) {
                sampleVisible = true;
            } else {
                sampleVisible = false;
            }

            Vector<TimeSeriesObject> groupA = IntraCorrelationAnalysis.getNoiseRowGroup();
            Vector<TimeSeriesObject> groupB = IntraCorrelationAnalysis.getNoiseRowGroup();



            if (sampleVisible) {
//                op.storeChart(groupA, false, "run1.raw A", "run1.rawA");
//                op.storeChart(groupB, false, "run1.raw B", "run1.rawB");
            }

            TimeSeriesObject icf1 = process(groupA, groupB, "C", "mode1");

            icfC.add(icf1);

        }
        plotAvAndSigma(icfC, "run1");



///*DEMO 2*/        
//
//        Vector<TimeSeriesObject> icfC1 = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> icfC2 = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> icfC3 = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> icfC4 = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> icfC5 = new Vector<TimeSeriesObject>();
//        
//        for( int i = 0; i < runs; i++ ) {
//            if( i == 0 ) sampleVisible = true;
//            else {
//                sampleVisible = false;
//            }
//            
//            Vector<TimeSeriesObject> groupA = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.1, "A" , "run2");
//            
//            Vector<TimeSeriesObject> groupB1 = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.1, "B1","run2");
//            Vector<TimeSeriesObject> groupB2 = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.2, "B2","run2");
//            Vector<TimeSeriesObject> groupB3 = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.3, "B3","run2");
//            Vector<TimeSeriesObject> groupB4 = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.4, "B4","run2");
//            Vector<TimeSeriesObject> groupB5 = MetacorrelationAnalysis.getSampleRowGroupA(1.0, 0.5, "B5","run2");
//            
//            TimeSeriesObject icf1 = process( groupA, groupB1, "C1", "mode1" );
//            TimeSeriesObject icf2 = process( groupA, groupB2, "C2", "mode1" );
//            TimeSeriesObject icf3 = process( groupA, groupB3, "C3", "mode1" );
//            TimeSeriesObject icf4 = process( groupA, groupB4, "C4", "mode1" );
//            TimeSeriesObject icf5 = process( groupA, groupB5, "C5", "mode1" );
//       
//            icfC1.add( icf1 );
//            icfC2.add( icf2 );
//            icfC3.add( icf3 );
//            icfC4.add( icf4 );
//            icfC5.add( icf5 );
//        }
//        
//        plotAvAndSigma( icfC1, "run2.0.1" );
//        plotAvAndSigma( icfC2, "run2.0.2" );
//        plotAvAndSigma( icfC3, "run2.0.3" );
//        plotAvAndSigma( icfC4, "run2.0.4" );
//        plotAvAndSigma( icfC5, "run2.0.5" );
//        

        /*DEMO 3*/
//        Vector<TimeSeriesObject> icfD3 = new Vector<TimeSeriesObject>();
//        for( int i = 0; i < runs; i++ ) {
//            if( i == 0 ) sampleVisible = true;
//            else {
//                sampleVisible = false;
//            }
//            
//            Vector<TimeSeriesObject> groupA = new Vector<TimeSeriesObject>();
//            Vector<TimeSeriesObject> groupB = new Vector<TimeSeriesObject>();
//            
//            createCoupledGroupsA( groupA, groupB );
//            
//            if( sampleVisible ) {
//                op.storeChart(groupA, false, "run3.raw A", "run3.rawA");
//                op.storeChart(groupB, false, "run3.raw B", "run3.rawB");
//            }
//
//            TimeSeriesObject mr = process( groupA, groupB, "C", "mode1" );
//            
//            icfD3.add( mr );
//        
//        }
//        plotAvAndSigma( icfD3, "run3" );
//        
    }
    public static OriginProject op = null;
    static public boolean sampling = true;

    public static Vector<TimeSeriesObject> _calcIntraCorrelation(Vector<TimeSeriesObject> groupA, Vector<TimeSeriesObject> groupB, int tau, int dtau, boolean debug, boolean shuffle) {

        boolean buildGRAPH = false;
        
        dtau = 1;

        Vector<TimeSeriesObject> mrs = new Vector<TimeSeriesObject>();

        if (shuffle) {
            groupA = shuffle(groupA);
            groupB = shuffle(groupB);
        }
        
        TimeSeriesObject[] grBArray = convert( groupB );

        // instead of all rows in group B we use an average value 
        // (maybe a detrendet value)
        TimeSeriesObject m = TimeSeriesObject.averageForAll(grBArray);


//        if (sampling) {
//
//            String s = javax.swing.JOptionPane.showInputDialog("sample size: (" + groupA.size() + ")");
//
//            int n = Integer.parseInt(s);
//            groupA = getSubSetFrom(groupA, n);
//            
//            System.out.println(">>> USE SAMPLING with n=" + n + " to save some time.");
//
//        }

        KreuzKorrelation._defaultK = 0;
        KreuzKorrelation.GLdebug = debug;
        KreuzKorrelation.debug = debug;

        String shuffleString = " ";
        String sEXT = "";
        if (shuffle) {
            sEXT = "(shuffles_" + maxShuffles + ")";
            shuffleString = "*";
        }

        TimeSeriesObject Ctau = new TimeSeriesObject("Ctau" + sEXT);
        TimeSeriesObject PCtau = new TimeSeriesObject("PCtau" + sEXT);
        TimeSeriesObject ICFtau = new TimeSeriesObject("ICF" + sEXT);

        TimeSeriesObject E1 = new TimeSeriesObject("E1" + sEXT);
        TimeSeriesObject E2 = new TimeSeriesObject("E2" + sEXT);
        TimeSeriesObject E3 = new TimeSeriesObject("E3" + sEXT);

        Vector<TimeSeriesObject> temp1 = new Vector<TimeSeriesObject>();
        temp1.addAll(groupA);

        Vector<TimeSeriesObject> temp2 = new Vector<TimeSeriesObject>();
        temp2.addAll(groupB);

        Vector<TimeSeriesObject> temp3 = new Vector<TimeSeriesObject>();
        temp3.add(m);




        if (debug) {
            MultiChart.open(temp1, false, "raw data gA " + shuffleString);
        }
        if (debug) {
            MultiChart.open(temp2, false, "raw data gB" + shuffleString);
        }
        if (debug) {
            MultiChart.open(temp3, true, "raw data m" + shuffleString);
        }

        int maxT = m.yValues.size();
        int counter = -1;
        int c = 0;

        double DEBUGv = 0.0;

        double vC = 0.0;
        double vPC = 0.0;
        double vICF = 0.0;

        double vE1 = 0.0;
        double vE2 = 0.0;
        double vE3 = 0.0;


        for (int i = 0; i < (maxT - tau); i = i + dtau) {

            vE1 = 0.0;
            vE2 = 0.0;
            vE3 = 0.0;

            Vector<Double> pCij = new Vector();
            Vector<Double> pPCijm = new Vector();

            counter++;
            // int tV = i + (tau / 2);  // middle of intervall 
            int tV = i + (tau);  // end  of intervall 

            System.out.println("[" + new Date(System.currentTimeMillis()) + "]\tStep: i=" + i + " (tV=" + tV + ") " + vC);
            int max = groupA.size();

            initTemporalNetwork(tV);

            // do all the looping stuff ...
            for (int a = 0; a < max; a++) {
                for (int b = 0; b < max; b++) {

                    if (c > 0) {
                        KreuzKorrelation.GLdebug = false;
                        KreuzKorrelation.debug = false;
                    }
                    c++;

                    TimeSeriesObject[] pair = getPair(groupA, a, b);

                    if (pair != null) {

                        TimeSeriesObject[] choppedPair = chopPair(pair, i, tau);

                        if (choppedPair != null) {

                            TimeSeriesObject mChopped = m.cutOut(i, i + tau);

                            KreuzKorrelation c_ij = KreuzKorrelation.calcKR(choppedPair[0], choppedPair[1], true);
                            KreuzKorrelation c_im = KreuzKorrelation.calcKR(choppedPair[0], mChopped, true);
                            KreuzKorrelation c_jm = KreuzKorrelation.calcKR(choppedPair[1], mChopped, true);

                            // System.out.println( c_ij.toString() );

                            double ccValue_ij = (Double) c_ij.yValues.elementAt(0);
                            double ccValue_im = (Double) c_im.yValues.elementAt(0);
                            double ccValue_jm = (Double) c_jm.yValues.elementAt(0);

                            _addToTemporalNetwork(ccValue_ij, choppedPair[0].getLabel(), choppedPair[1].getLabel(), buildGRAPH);

                            Double C = ccValue_ij;


                            double N = ccValue_ij - (ccValue_im * ccValue_jm);
                            double Z1 = 1 - (ccValue_im * ccValue_im);
                            double Z2 = 1 - (ccValue_jm * ccValue_jm);
                            double Z = Z1 * Z2;

                            Double PC = (N) / (Math.sqrt(Z));

                            //if ( debug ) System.out.println( > C="+ C + "\tPC=" + PC );

                            if (C.isNaN() || C.isInfinite()) {
                                vE1++;
                            } else {
                                pCij.add(C);
                            }

                            if (PC.isNaN() || PC.isInfinite()) {
                                vE2++;
                            } else {
                                pPCijm.add(PC);
                            }

                            // System.out.println( " crunch (" + a + "," + b +") " + ccValue );

                        }
                    } else {
                        // System.out.println( " skip (" + a + "," + b +")");
                    }



                }

            }

            // do all the aggregation ...
            vC = getAverage(pCij);
            vPC = getAverage(pPCijm);
            vICF = vC / vPC;



            Ctau.addValuePair(tV, vC);
            PCtau.addValuePair(tV, vPC);
            ICFtau.addValuePair(tV, vICF);
            
            E1.addValuePair(tV, vE1);
            E2.addValuePair(tV, vE2);

        }

        mrs.add(Ctau);
        mrs.add(PCtau);
        mrs.add(ICFtau);

        mrs.add(E1);
        mrs.add(E2);

//        try {
////            op.closeAllWriter();
//        } catch (IOException ex) {
//            Logger.getLogger(MetacorrelationAnalysis.class.getName()).log(Level.SEVERE, null, ex);
//        }


        return mrs;
    }

    public static Vector<TimeSeriesObject> _calcDependcyNetworks(Vector<TimeSeriesObject> groupA, int tau, int dtau, boolean debug, boolean shuffle) {

        boolean buildGRAPH = false;
        
        dtau = 28;
        sampling = false;
        logNetworkFiles = true;

        Vector<TimeSeriesObject> mrs = new Vector<TimeSeriesObject>();

        if (shuffle) {
            groupA = shuffle(groupA);
        }

//        if (sampling) {
//
//            String s = javax.swing.JOptionPane.showInputDialog("sample size: (" + groupA.size() + ")");
//
//            int n = Integer.parseInt(s);
//            groupA = getSubSetFrom(groupA, n);
//            System.out.println(">>> USE SAMPLING with n=" + n + " to save some time.");
//
//        }

        KreuzKorrelation._defaultK = 0;
        KreuzKorrelation.GLdebug = debug;
        KreuzKorrelation.debug = debug;

        String shuffleString = " ";
        String sEXT = "";
        if (shuffle) {
            sEXT = "(s" + maxShuffles + ")";
            shuffleString = "*";
        }

        TimeSeriesObject nrEdgesTau = new TimeSeriesObject("#edges" + sEXT);
        TimeSeriesObject diameterTau = new TimeSeriesObject("diameter" + sEXT);

        TimeSeriesObject E1 = new TimeSeriesObject("E1" + sEXT);
        TimeSeriesObject E2 = new TimeSeriesObject("E2" + sEXT);
        TimeSeriesObject E3 = new TimeSeriesObject("E3" + sEXT);

        Vector<TimeSeriesObject> temp1 = new Vector<TimeSeriesObject>();
        temp1.addAll(groupA);

        if (debug) {
            MultiChart.open(temp1, false, "raw data gD " + shuffleString);
        }

        int maxT = groupA.elementAt(0).yValues.size();

        int counter = -1;


        double DEBUGv = 0.0;

        double vC = 0.0;
        double vPC = 0.0;
        double vICF = 0.0;

        double vE1 = 0.0;
        double vE2 = 0.0;
        double vE3 = 0.0;

        KreuzKorrelation.GLdebug = false;
        KreuzKorrelation.debug = false;

        int breakT = (maxT - tau);
        

        // For all times ...
        for (int i = 0; i < breakT; i = i + dtau) {

            vE1 = 0.0;
            vE2 = 0.0;
            vE3 = 0.0;

            counter++;

            // int tV = i + (tau / 2);  // middle of intervall 
            int tV = i + (tau);  // end  of intervall 

            System.out.println("[" + new Date(System.currentTimeMillis()) + "]\tStep: i=" + i + " (tV=" + tV + ") " + vC);
            int max = groupA.size();

            // new net for new time step
                                
            ////////// CHNAGE
            // initTemporalNetwork(tV);
            
            double[][] dIJ = new double[max][max];
            for (int a = 0; a < max; a++) {
                    for (int b = 0; b < max; b++) {
                       dIJ[a][b]  = 0.0;
                    }
            }        

            // do all the looping stuff ...
            for (int c = 0; c < max; c++) {
                System.out.println( "k:=>" + c);
                for (int a = 0; a < max; a++) {
                    for (int b = 0; b < max; b++) {

                        TimeSeriesObject[] pair = getPair(groupA, a, b);
                        TimeSeriesObject k = groupA.elementAt(c);

                        if (pair != null) {

                            TimeSeriesObject[] choppedPair = chopPair(pair, i, tau);

                            if (choppedPair != null) {

                                TimeSeriesObject mChopped = k.cutOut(i, i + tau);

                                KreuzKorrelation c_ij = KreuzKorrelation.calcKR(choppedPair[0], choppedPair[1], true);
                                KreuzKorrelation c_im = KreuzKorrelation.calcKR(choppedPair[0], mChopped, true);
                                KreuzKorrelation c_jm = KreuzKorrelation.calcKR(choppedPair[1], mChopped, true);
//
//                                // System.out.println( c_ij.toString() );
//
                                double ccValue_ij = (Double) c_ij.yValues.elementAt(0);
                                double ccValue_im = (Double) c_im.yValues.elementAt(0);
                                double ccValue_jm = (Double) c_jm.yValues.elementAt(0);

//                                
                                double aN = ccValue_ij - (ccValue_im * ccValue_jm);
                                double aZ1 = 1 - (ccValue_im * ccValue_im);
                                double aZ2 = 1 - (ccValue_jm * ccValue_jm);
                                double aZ = aZ1 * aZ2;

                                Double PC = (aN) / (Math.sqrt(aZ));
                            
                                Double CC = ccValue_ij;

                                if ( b!=c && valid(CC) && valid(PC) ) {
                                    dIJ[a][b] = dIJ[a][b] + CC - PC;
                                }    
                                
                                if (CC.isNaN() || CC.isInfinite()) {
                                    vE1++;
                                } else {
                                }

                                if (PC.isNaN() || PC.isInfinite()) {
                                    vE2++;
                                } else {
                                }

                            }
                        } 
                        else {
                            
                            // System.out.println( " skip (" + a + "," + b +")");
                        }
                    }
                }
            }
             
            for (int a = 0; a < max; a++) {
                    for (int b = 0; b < max; b++) {
                        
                       double s = dIJ[a][b] / (double)(max-1.0);
                       
                       ////////// CHNAGE
                       // _addToTemporalNetwork( s , ""+a, ""+b, buildGRAPH );  
                       
                    }
            } 
            
            double[] data = processTemporalNetwork();

            // do all the aggregation ...

            double diameterV = data[0];
            double nrEdgesV = data[1];
            

            E1.addValuePair(tV, vE1);
            E2.addValuePair(tV, vE2);

            /**
             * **
             *
             * Measure the network ...
             *
             */
            nrEdgesTau.addValuePair(tV, diameterV);
            diameterTau.addValuePair(tV, nrEdgesV);
            
        }

        /**
         * 
         * Here we can hook in to analyse the 
         * 
         */
        
        mrs.add(nrEdgesTau);
        mrs.add(diameterTau);

        mrs.add(E1);
        mrs.add(E2);



        return mrs;
    }

    public static Vector<TimeSeriesObject> calcIntraCorrelation2(Vector<TimeSeriesObject> groupA, Vector<TimeSeriesObject> groupB, int tau, int dtau, boolean debug, boolean shuffle) {

        System.err.println(">> grA: " + groupA.size());
        System.err.println(">> grB: " + groupB.size());

        dtau = 1;

        Vector<TimeSeriesObject> mrs = new Vector<TimeSeriesObject>();

        if (shuffle) {
            groupA = shuffle(groupA);
            groupB = shuffle(groupB);
        }

        KreuzKorrelation._defaultK = 0;
        KreuzKorrelation.GLdebug = debug;
        KreuzKorrelation.debug = debug;

        String shuffleString = " ";
        String sEXT = "";
        if (shuffle) {
            sEXT = "(s" + maxShuffles + ")";
            shuffleString = "*";
        }

        TimeSeriesObject localCC = new TimeSeriesObject("totalCC" + sEXT);
        TimeSeriesObject localCCsigma = new TimeSeriesObject("totalCC_sigma" + sEXT);

        TimeSeriesObject E1 = new TimeSeriesObject("E1" + sEXT);
        TimeSeriesObject E2 = new TimeSeriesObject("E2" + sEXT);
        TimeSeriesObject E3 = new TimeSeriesObject("E3" + sEXT);

        Vector<TimeSeriesObject> temp1 = new Vector<TimeSeriesObject>();
        temp1.addAll(groupA);

        Vector<TimeSeriesObject> temp2 = new Vector<TimeSeriesObject>();
        temp2.addAll(groupB);

        if (debug) {
            MultiChart.open(temp1, false, "raw data gA " + shuffleString);
        }
        if (debug) {
            MultiChart.open(temp2, false, "raw data gB " + shuffleString);
        }


        int maxT = groupA.elementAt(0).yValues.size();
        int counter = -1;
        int c = 0;

        double DEBUGv = 0.0;

        double vC = 0.0;
        double vPC = 0.0;
        double vICF = 0.0;

        double vE1 = 0.0;
        double vE2 = 0.0;
        double vE3 = 0.0;


        Vector<TimeSeriesObject> localCCSet = new Vector<TimeSeriesObject>();
        for (int i = 0; i < groupA.size(); i++) {
            TimeSeriesObject m = new TimeSeriesObject();
            localCCSet.add(m);
        }

        int maxA = groupA.size();
        int maxB = groupB.size();

        TimeSeriesObject mrB = TimeSeriesObject.averageForAll( convert( groupB ) );

        // FOR EACH TIMESTEP
        for (int i = 0; i < (maxT - tau); i = i + dtau) {

            vE1 = 0.0;
            vE2 = 0.0;
            vE3 = 0.0;

            counter++;

            int tV = i + (tau / 2);  // middle of intervall 
//            int tV = i + (tau);  // end  of intervall 

            System.out.println("[" + new Date(System.currentTimeMillis()) + "]\tStep: i=" + i + " (tV=" + tV + ") " + vC);


            // do all the looping stuff ...

            // loop over all in GroupA (CN, or IWL)
            for (int a = 0; a < maxA; a++) {

                TimeSeriesObject mrA = groupA.elementAt(a);

                if (c > 0) {
                    KreuzKorrelation.GLdebug = false;
                    KreuzKorrelation.debug = false;
                }
                c++;

                TimeSeriesObject mChoppedA = mrA.cutOut(i, i + tau);
                TimeSeriesObject mChoppedB = mrB.cutOut(i, i + tau);

                KreuzKorrelation cc = KreuzKorrelation.calcKR(mChoppedA, mChoppedB, true);

                // System.out.println( c_ij.toString() );

                Double C = (Double) cc.yValues.elementAt(0);


                if (C.isNaN() || C.isInfinite()) {
                    vE1++;
                    localCCSet.elementAt(a).addValuePair(tV, 0.0);
                } else {
                    System.out.println(a + " " + tV + " " + C);
                    localCCSet.elementAt(a).addValuePair(tV, C);
                }



            }

            E1.addValuePair(tV, vE1);
            E2.addValuePair(tV, vE2);


        }

        localCC = TimeSeriesObject.averageForAll( convert(localCCSet));
        localCCsigma = TimeSeriesObject.sigmaForAll(localCCSet);

        mrs.add(localCC);
        mrs.add(localCCsigma);

//        mrs.add(E1);
//        mrs.add(E2);

        return mrs;
    }

    private static TimeSeriesObject[] getPair(Vector<TimeSeriesObject> groupA, int a, int b) {
        TimeSeriesObject[] pair = null;
        if (a < b) {
            pair = new TimeSeriesObject[2];
            pair[0] = groupA.elementAt(a);
            pair[1] = groupA.elementAt(b);
        }
        return pair;
    }

    private static TimeSeriesObject[] chopPair(TimeSeriesObject[] pair, int i, int tau) {
        TimeSeriesObject r1 = pair[0].cutOut(i, i + tau);
        TimeSeriesObject r2 = pair[1].cutOut(i, i + tau);

//        if (r2.yValues.size() > 24 && r1.yValues.size() > 24) {
        pair[0] = r1;
        pair[1] = r2;
//        } else {
//            pair = null;
//        }
        return pair;
    }

    private static double getAverage(Vector<Double> pCij) {
        Double sum = 0.0;
        for (double v : pCij) {
            sum = sum + v;
            // System.out.println( sum );
        }
        double mw = sum / pCij.size();
        return mw;
    }
    static int maxShuffles = 10;

    private static Vector<TimeSeriesObject> shuffle(Vector<TimeSeriesObject> gr) {
        Vector<TimeSeriesObject> groupA = new Vector<TimeSeriesObject>();

        for (TimeSeriesObject r : gr) {

            TimeSeriesObject r2 = r.copy();

            r2.shuffleYValues(maxShuffles);

            groupA.add(r2);
        }

        return groupA;
    }
    static boolean logNetworkFiles = false;
    private static String linkListName = null;
    static String graphID = null;
    
    static ProjectController pc = null;
    static Workspace workspace = null;
    static GraphModel graphModel = null;
    
 

    
    private static void initTemporalNetwork(int tV) {

  
//        if (!logNetworkFiles) {
//            return;
//        }
        
        System.out.println( ">>> NEW TEMP NET : " + tV );
        
        if ( graphModel != null ) { 
            _storeGraph( graphModel );
            graphModel.clear();
        }
        else { 
            pc = Lookup.getDefault().lookup(ProjectController.class);
            // pc.closeCurrentProject();
            // pc.newProject();
            workspace = pc.getCurrentWorkspace();

            //Get a graph model - it exists because we have a workspace
            graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        }
        
        graphID = "gr_"+tV;
        
        
        try {
            String name = "temporalNet.4.linklist." + tV + ".csv";
            linkListName = name;
            op.createLogFile(name);
        } catch (IOException ex) {
            Logger.getLogger(IntraCorrelationAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void _addToTemporalNetwork(double depValue, String labela, String labelb, boolean buildGraph) {

        if (!logNetworkFiles) {
            return;
        }
        
        if ( depValue == 0.0 ) return;

        String line = labela + "\t" + labelb + "\t" + depValue + "\n";
        System.out.println( "##### " + line ); 
        try {
            op.logLine(linkListName, line);
        } 
        catch (IOException ex) {
            Logger.getLogger(IntraCorrelationAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if ( buildGraph ) {

            DirectedGraph directedGraph = graphModel.getDirectedGraph();

            Node n0 = directedGraph.getNode(labela);
            if ( n0 == null ) {
                n0 = graphModel.factory().newNode(labela);
                n0.getNodeData().setLabel(labela); 
            }

            Node n1 = directedGraph.getNode(labelb);
            if ( n1 == null ) {
                n1 = graphModel.factory().newNode(labelb);
                n1.getNodeData().setLabel(labelb); 
            }



            //Create three edges
            Edge e1 = graphModel.factory().newEdge(n0, n1, (float)depValue, true);

            //Append as a Directed Graph
            directedGraph.addNode(n0);
            directedGraph.addNode(n1);
            directedGraph.addEdge(e1);
        
        }
               
    }

    private static Vector<TimeSeriesObject> getSubSetFrom(Vector<TimeSeriesObject> groupA, int n) {

        Vector<TimeSeriesObject> m = new Vector<TimeSeriesObject>();
        List<Integer> l = new ArrayList<Integer>();

        for (int i = 0; i < groupA.size(); i++) {
            l.add(i);
        }

        for (int i = 0; i < 10; i++) {
            Collections.shuffle(l);
        }

        for (int i = 0; i < n; i++) {
            Integer in = l.get(i);
            TimeSeriesObject mm = (TimeSeriesObject) groupA.elementAt(in.intValue());
            m.add(mm);
        }


        return m;
    }

    private static void initCharting() {

        int[] _dt = new int[10];
        _dt[0] = 1;
        _dt[1] = 1;
        _dt[2] = 1;
        _dt[3] = 1;
        _dt[4] = 1;
        _dt[5] = 0;
        _dt[6] = 0;
        _dt[7] = 0;
        _dt[8] = 0;
        _dt[9] = 0;

        MultiChart._setTypes(_dt);

        Color[] col = new Color[10];
        col[0] = Color.BLACK;
        col[1] = Color.BLUE;
        col[2] = Color.GRAY;
        col[3] = Color.ORANGE;
        col[4] = Color.RED;
        col[5] = Color.BLACK;
        col[6] = Color.BLUE;
        col[7] = Color.GRAY;
        col[8] = Color.ORANGE;
        col[9] = Color.RED;

        MultiChart.yRangDEFAULT_MIN = -5;
        MultiChart.yRangDEFAULT_MAX = 5;
        MultiChart.xRangDEFAULT_MIN = 0;
        MultiChart.xRangDEFAULT_MAX = 365;
        MultiChart.setDefaultRange = true;

        MultiChart._initColors(col);
    }
    static boolean verbose = true;
    static boolean debug = true;

    private static TimeSeriesObject process(Vector<TimeSeriesObject> groupA, Vector<TimeSeriesObject> groupB, String typ, String label) {

        Vector<TimeSeriesObject> resultRows1 = _calcIntraCorrelation(groupA, groupB, tau, dtau, debug, false);
//        Vector<TimeSeriesObject> resultRows2 = calcIntraCorrelation(groupA, groupB, tau, dtau, debug, true);

//        resultRows1.addAll(resultRows2);

        String header = "#\n#\tIntraCorrelation - modeller " + typ + " \n#";
        op.setHeader(header);

        op.addMessreihen(resultRows1, "intracorrelation.sh0." + typ + "", true);
//        op.addTimeSeriesObjectn(resultRows2, "intracorrelation.sh1." + typ +".", true);

        System.out.println(">>> results are stored in: " + op.toString());

        String a = label + " (1)  [C,PC,ICF](tau) tau=" + tau + " typ=" + typ;

        if (verbose) {
            op.storeChart(resultRows1, true, typ, "intracorrelation." + typ + "_" + label + ".image1.png");
        }
        
        

        TimeSeriesObject icf = resultRows1.elementAt(2);
        System.out.println("USE: " + icf.getLabel());

        return icf;

    }

    private static void plotAvAndSigma(Vector<TimeSeriesObject> icfC, String title) {

        // Calc average
//        TimeSeriesObject avB = TimeSeriesObject.averageForAll(icfB);
        TimeSeriesObject avC = TimeSeriesObject.averageForAll(icfC);

//        avB.setLabel("<ICF B>");
        avC.setLabel("<ICF C>");

        // Calc sigma
//        TimeSeriesObject sigmaB = TimeSeriesObject.sigmaForAll(icfB);
        TimeSeriesObject sigmaC = TimeSeriesObject.sigmaForAll(icfC);

//        sigmaB.setLabel("sigma(B)");
        sigmaC.setLabel("sigma(C)");

        Vector<TimeSeriesObject> rows = new Vector<TimeSeriesObject>();
//        rows.add(avB);
        rows.add(avC);
//        rows.add(sigmaB);
        rows.add(sigmaC);

        op.storeChart(rows, true, title, title + ".ICF");
        // plot result
        MultiChart.open(rows, title, "t", "f(t)", true);

    }

    private static double[] processTemporalNetwork() {
        double d[] = new double[2];
        
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
        RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
        PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        
        
               //See if graph is well imported
        DirectedGraph graph = graphModel.getDirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

//        //Partition with 'source' column, which is in the data
        PartitionController partitionController = Lookup.getDefault().lookup(PartitionController.class);
//        Partition p = partitionController.buildPartition(attributeModel.getNodeTable().getColumn("source"), graph);
//        NodeColorTransformer nodeColorTransformer = new NodeColorTransformer();
//        nodeColorTransformer.randomizeColors(p);
//        partitionController.transform(p, nodeColorTransformer);

        
        String location = op.folderName + "/";
        File f1 = new File(location + "gephi_depTemp1." + graphID + ".pdf");
        File f2 = new File(location + "gephi_depTemp2." + graphID + ".pdf");
       
        
//        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
//        try {
//            ec.exportFile( f1 );
//        } catch (IOException ex) {
//            ex.printStackTrace();
//             
//        }

        //Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.execute(graphModel, attributeModel);

        //Partition with 'modularity_class', just created by Modularity algorithm
        AttributeColumn modColumn = attributeModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Partition p2 = partitionController.buildPartition(modColumn, graph);
        System.out.println(p2.getPartsCount() + " partitions found");
        
        NodeColorTransformer nodeColorTransformer2 = new NodeColorTransformer();
        nodeColorTransformer2.randomizeColors(p2);
        partitionController.transform(p2, nodeColorTransformer2);

        
        
        
        
        //Export
        try {
            ec.exportFile( f2 );
        } catch (IOException ex) {
            ex.printStackTrace();
            
        }
        
     
        
        
        d[0] = Math.random();
        d[1] = Math.random();
        
        
        return d;
    }

    private static void _storeGraph(GraphModel graphModel) {
        
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            try {
                
                String location = op.folderName + "/";
                File f = new File(location + "gephi_depTemp." + graphID + ".gexf");
                ec.exportFile(f);
                
                File f2 = new File(location + "gephi_depTemp." + graphID + ".graphml");
                ec.exportFile(f2);
                
                System.err.println("GEPHI Export: " +  f.getAbsolutePath());                
            } 
            catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
            
            
          
    }

    private static boolean valid(Double CC) {
       if (CC.isNaN() || CC.isInfinite()) {
           return false;
       } else {
           return true;
       }
    }

    private static TimeSeriesObject[] convert(Vector<TimeSeriesObject> g) {
        TimeSeriesObject[] a = new TimeSeriesObject[ g.size() ];
        int i = 0;
        for( TimeSeriesObject m : g ) {
            a[i] = m;
            i++;
        }
        return a;
    }
}
