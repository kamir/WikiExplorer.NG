package com.cloudera.wikipedia.explorer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.export.OriginProject;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.awt.Color;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoopts.app.thesis.TSGenerator;
import experiments.crosscorrelation.KreuzKorrelation;
import static com.cloudera.wikiexplorer.ng.app.SimpleSFE.sdl;

/**
 *
 * @author kamir
 */
public class MetacorrelationAnalysisV2 {

    static int nrA = 6;  // intra group
    static int nrB = 2;  // external system
    
    static int z = 12 * 4 * 7;
    static int tau = 4 * 7;
    
    
    // WOCHEN dtau = 7, Tage: dtau = 1
    static int _dtau = 1;
    
    static double f = 1.0 / 86400.0;
    static double sr = 1.0 / 3600.0;
    static double amp1 = 1.0;
    static double amp2 = 1.0;

    public static Messreihe rescaleAndShiftTimeSeries(Date start, long scale, Messreihe mr, long offset2) {

        Double x1 = (Double) mr.xValues.elementAt(0);
        int max = mr.xValues.size() - 1;
        Double xn = (Double) mr.xValues.elementAt(max);


        Date d1 = new Date(x1.longValue());
        Date d2 = new Date(xn.longValue());

        long t0 = start.getTime();
        long offset = -1 * (int) (t0 / scale);
        offset = offset + offset2;

        System.out.println(scale + " # " + t0 + " : " + (offset) + " : " + start.toString());
        System.out.println(d1.toString() + " => " + d2.toString());


        Messreihe m2 = mr.divideXBy(scale);
//        Messreihe m1 = m2.addToX2( (int)offset );

        System.out.println(m2.toString());
        return m2;
    }

    public static Messreihe getWeekdaySampleTimeSeries(Date start, Date end) {

        long offset = start.getTime();

        Messreihe r1 = new Messreihe();

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(start);

        System.out.println(">>> " + start.toString());
        System.out.println(">>> " + end.toString());
        int days = 0;

        double scale = 1000.0 * 3600.0;

        double last = 0;

        while (gc.getTime().before(end)) {

            int day = gc.get(Calendar.DAY_OF_WEEK);

            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
//                last = 3;
//                r1.addValuePair( ( gc.getTime().getTime() - offset ) / scale , last );
            } else {
                last = Math.random();
                r1.addValuePair((gc.getTime().getTime() - offset) / scale, last);
            }
            days++;
            gc.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }

        System.out.println("> " + days + " days ... ");
        return r1;
    }

    public static Messreihe getWeekdaySampleTimeSeriesWithHolidays(Date start, Date end) {

        long offset = start.getTime();

        Messreihe r1 = new Messreihe();

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(start);

        System.out.println(">>> " + start.toString());
        System.out.println(">>> " + end.toString());
        int days = 0;

        double scale = 1000.0 * 3600.0;

        double last = 0;

        int randRemoval = 0;
        int max = 10;

        while (gc.getTime().before(end)) {

            double prob = 0.05;

            int day = gc.get(Calendar.DAY_OF_WEEK);

            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
//                last = 3;
//                r1.addValuePair( ( gc.getTime().getTime() - offset ) / scale , last );
            } else {

                double useValue = Math.random();

                if (useValue > prob || randRemoval >= max) {

                    last = Math.random();

                    r1.addValuePair((gc.getTime().getTime() - offset) / scale, last);
                } else {
//                    last = 5;
//                    r1.addValuePair( ( gc.getTime().getTime() - offset ) / scale , last );
                    randRemoval = randRemoval + 1;
                }
            }
            days++;
            gc.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }


        System.out.println("> " + days + " days ... ");
        return r1;
    }

    public static Messreihe getAllDayTimeSeries(Messreihe mr, Date start, Date end, double scale) {

        System.out.println(start.toString() + " ... " + end.toString());

//        long offset = start.getTime();

        Messreihe r1 = new Messreihe();
        r1.setLabel(mr.label + "_");

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(start);

        System.out.println(gc.getTime().toString() + " : " + gc.get(Calendar.DAY_OF_WEEK));

        System.out.print("<von> " + start.toString());
        System.out.println("  <bis> " + end.toString());

        System.out.println("  z= " + mr.xValues.size());

        int days = 0;



        double last = 0;

        int lastDay = 0;
        int deltaDay = 0;
        int day = 0;

        int lastDaySOLL = 0;
        int deltaDaySOLL = 0;
        int daySOLL = 0;

        double vLAST = 0.0;
        double valueA = 0.0;

        int index = 0;
        Date timeSOLL = start;
        GregorianCalendar calSOLL = new GregorianCalendar();
        calSOLL.setTime(timeSOLL);
        System.out.println(calSOLL.getTime().toString() + " : " + calSOLL.get(Calendar.DAY_OF_WEEK));

//        calSOLL.add(GregorianCalendar.DAY_OF_YEAR, 1);

        boolean goOn = true;
        double timeA = 0.0;


        int deltaIndex = 0;

        boolean modeA = true;

        boolean isFirstRun = true;
        
        while (gc.getTime().before(end) && goOn) {

            vLAST = valueA;

            try {

                timeA = ((Double) mr.xValues.elementAt(index) * scale) + start.getTime() / 3600 / 1000 / 24;
                valueA = (Double) mr.yValues.elementAt(index);

                Date timeMR = new Date((long) timeA);
                GregorianCalendar calMR = new GregorianCalendar();
                calMR.setTime(timeMR);
                
//                System.out.println( timeA + "  ***  (" + timeMR.toString() + ")" + calMR.getTime().toString() + " : " + calMR.get(Calendar.DAY_OF_WEEK));
                
                lastDay = day;
                
                day = calMR.get(Calendar.DAY_OF_WEEK);

                deltaDay = day - lastDay;

                if (modeA) {
                    // SOLL
                    lastDaySOLL = daySOLL;
                    daySOLL = calSOLL.get(Calendar.DAY_OF_WEEK);

                    deltaDaySOLL = daySOLL - lastDaySOLL;

                    if (deltaDaySOLL < 0) {
                        deltaDaySOLL = deltaDaySOLL + 7;
                    }
                    if (deltaDay < 0) {
                        deltaDay = deltaDay + 7;
                    }

                }
                
                int insert = deltaDay - deltaDaySOLL;


                if (insert != 0 && !isFirstRun ) {
                    /**
                     * in branch B we have to fill in INSERT gaps
                     */
//                    System.out.println("B) Go ...");

                    for (int i = 0; i < insert; i++) {

                        if (i == 0) {
                            calMR.add(GregorianCalendar.DAY_OF_YEAR, -deltaDay);
                        }


//                    gc.add(GregorianCalendar.DAY_OF_YEAR, 1);
                        calMR.add(GregorianCalendar.DAY_OF_YEAR, 1);
                        day = calMR.get(Calendar.DAY_OF_WEEK);

//                        System.out.println(" #" + modeA + i + ":\t" + daySOLL + "\t*" + deltaDaySOLL + "\t" + day + "\t" + deltaDay + "\tvalue=" + vLAST + "\tinsert=" + insert);

//                        vLAST = 5;
                        r1.addValuePair(index + deltaIndex, vLAST);

                        deltaIndex = deltaIndex + 1;

                    }
//                    calSOLL.add(GregorianCalendar.DAY_OF_YEAR, 1);


//                    System.out.println("B) Done.");
                    deltaDaySOLL = 1;
                    modeA = false;
                    days++;
                     

                } 
                else {

                    /**
                     * no gaps to fill
                     */
//                    System.out.println("A) Go ...");
//                    System.out.println(index  + " " + modeA + "\t" + daySOLL + "\t*" + deltaDaySOLL + "\t" + day + "\t" + deltaDay + "\tvalue=" + valueA + "\tinsert=" + insert);

                    r1.addValuePair(index + deltaIndex, valueA);

                    gc.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    calSOLL.add(GregorianCalendar.DAY_OF_YEAR, 1);

                    
//                    System.out.println("A) Done.");
                    modeA = true;
                    days++;
                    index++;
                    if( index >= mr.xValues.size() ) goOn = false;
                }
                isFirstRun = false;
            } 
            catch (Exception x) {

                x.printStackTrace();
                goOn = false;
            }
        }

        System.out.println("> " + days + " days ... " + mr.getLabel());
        return r1;
    }

    public static Vector<Messreihe> getSampleRowGroupA() {
        Vector<Messreihe> groupA = new Vector<Messreihe>();

        Messreihe r1 = TSGenerator.getSinusWave(f, z * 3600, sr, amp1);
        
        // groupA.add(r1);

        // 
        for (int i = 0; i < nrA; i++) {
            Messreihe m1 = Messreihe.getGaussianDistribution(z, 0, 1);
            Messreihe m2 = m1.add(r1);
            System.out.println( (i+1) + ".)\t" + m2.getLabel() );
            
            groupA.add( m2 );
        }

        return groupA;
    }

    public static Vector<Messreihe> getSampleRowGroupB() {
        Vector<Messreihe> groupB = new Vector<Messreihe>();

        Messreihe r2 = TSGenerator.getSinusWave((1.0 / 7.0) * f, z * 3600, sr, amp2);
        // groupA.add(r1);

        // 
        for (int i = 0; i < nrB; i++) {
            groupB.add(r2.add(Messreihe.getGaussianDistribution(z, 20, 3)));
        }

        return groupB;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException, Exception {
        
        stdlib.StdRandom.initRandomGen(1);
        
        Vector<Messreihe> groupA = getSampleRowGroupA();
        
        calcMetacorrelation( symbols[0] , groupA );
//        calcMetacorrelation( symbols[1] , groupA ); //-
//        calcMetacorrelation( symbols[2] , groupA ); //-        
//        calcMetacorrelation( symbols[3] , groupA ); // -
        calcMetacorrelation( symbols[4] , groupA );
        calcMetacorrelation( symbols[5] , groupA );
        
    }
    

    public static final String[] symbols = { "^N225", "FDAX.EX", "^FTLC", "000001.SS",  "^GSPC", "^BSESN"   };
    
    
    
    public static void calcMetacorrelation(String symbol, Vector<Messreihe> groupC) throws IOException, ParseException, Exception {
       String label = "global_indexes.csv"; // "dowjonesmap.dat";//"daxmap.dat";//

        String colu = "Close";

//        IndexDataLoader2 sdl = IndexDataLoader2.getOnlineLoader( colu, "2009-01-01", "2010-01-01", label);
        IndexDataLoader2 sdl = IndexDataLoader2.getLocalLoader("2009-01-01", "2010-01-01", label);
        


        /**
         * 
         *
http://de.wikipedia.org/wiki/Nikkei_225,%5EN225,Nikkei 225
http://de.wikipedia.org/wiki/SSE_Composite_Index,000001.SS,SSE Composite
http://de.wikipedia.org/wiki/DAX,FDAX.EX,DAX Performance
http://de.wikipedia.org/wiki/FTSE_350_Index,%5EFTLC,FTSE 350
http://de.wikipedia.org/wiki/S%26P_500,%5EGSPC,SuP500
http://de.wikipedia.org/wiki/BSE_Sensex,%5EBSESN,BSE SENSEX
         *
         *
         */

        if ( MetacorrelationAnalysisV2.op == null ) {
            OriginProject op = new OriginProject();
            op.initFolder(null);
            MetacorrelationAnalysisV2.op = op;
        }    

        String sDate = "2009-01-01";
        String eDate = "2010-01-01";



        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd");

        Date s = parserSDF.parse(sDate);
        Date e = parserSDF.parse(eDate);
        
        Vector<Messreihe> rows2 = new Vector<Messreihe>();
        Vector<Messreihe> rows = new Vector<Messreihe>();
        
        colu = "Close";
        sdl.initColumn( colu );
//        sdl.showCharts();
        sdl.showChartsNormalized();
        Messreihe rowC1 = IndexDataLoader2._normalizedMR.get( symbol + "_" + colu );
        Messreihe rowC1a = MetacorrelationAnalysisV2.getAllDayTimeSeries(rowC1, s, e, 1.0);
        rows2.add(rowC1a);
        
        colu = "Volume";
        sdl.initColumn( colu );
//        sdl.showCharts();
        sdl.showChartsNormalized();
        Messreihe rowC2 = IndexDataLoader2._normalizedMR.get( symbol + "_" + colu);
        Messreihe rowC2a = MetacorrelationAnalysisV2.getAllDayTimeSeries(rowC2, s, e, 1.0);
        rows2.add(rowC2a);
        
        colu = "Adj_Close";
        sdl.initColumn( colu );
//        sdl.showCharts();
        sdl.showChartsNormalized();
        Messreihe rowC3 = IndexDataLoader2._normalizedMR.get( symbol + "_" + colu);
        Messreihe rowC3a = MetacorrelationAnalysisV2.getAllDayTimeSeries(rowC3, s, e, 1.0);
        rows2.add(rowC3a);  
        rows.add( rowC3 );
        
        



        GregorianCalendar gcS = new GregorianCalendar();
        gcS.setTime(s);
        int dayId_of_first_day = gcS.get(Calendar.DAY_OF_WEEK);

        Messreihe rowA = MetacorrelationAnalysisV2.getWeekdaySampleTimeSeries(s, e);
        Messreihe rowB = MetacorrelationAnalysisV2.getWeekdaySampleTimeSeriesWithHolidays(s, e);

////        Messreihe rowC12 = rescaleAndShiftTimeSeries(s, 3600 * 1000 * 24, rowC, dayId_of_first_day);

//        rows2.add(rowC12);

//        
        rowA.setLabel("A");
        rowB.setLabel("B");

        //        
        Messreihe rowA1 = MetacorrelationAnalysisV2.getAllDayTimeSeries(rowA, s, e, 1000.0 * 3600.0);
        Messreihe rowB1 = MetacorrelationAnalysisV2.getAllDayTimeSeries(rowB, s, e, 1000.0 * 3600.0);

        rows.add(rowA);
        rows.add(rowB);
        rows.add(rowA1);
        rows.add(rowB1);

        /**
         * call the analysis function ...
         */
//        Vector<Messreihe> resultRows1 = calcMetaCorrelation(groupA, groupB, tau, dtau, true, false);
//        Vector<Messreihe> resultRows2 = calcMetaCorrelation(groupA, groupB, tau, dtau, true, true);
//        resultRows1.addAll(resultRows2);
        String header = "#\n#\tMetaCorrelationTester\n#";
        op.setHeader(header);

        op.addMessreihen(rows2, "rawdata", true);

        System.out.println(">>> rawdata was stored in: " + op.toString());

        int[] _dt = new int[12];
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
        _dt[10] = 0;        
        _dt[11] = 0;

        MultiChart._setTypes(_dt);

        Color[] col = new Color[12];
        col[0] = Color.BLACK;
        col[1] = Color.BLUE;
        col[2] = Color.GRAY;
        col[3] = Color.ORANGE;
        col[4] = Color.RED;
        col[5] = Color.GREEN;
        
        col[6] = Color.BLACK;
        col[7] = Color.BLUE;
        col[8] = Color.GRAY;
        col[9] = Color.ORANGE;
        col[10] = Color.RED;
        col[11] = Color.GREEN;

        MultiChart.yRangDEFAULT_MIN = -5;
        MultiChart.yRangDEFAULT_MAX = 5;
        MultiChart.xRangDEFAULT_MIN = 0;
        MultiChart.xRangDEFAULT_MAX = 400;
        MultiChart.setDefaultRange = true;

        MultiChart._initColors(col);
//
//        MultiChart.open(rows, true, "(1.1) raw data");
//        MultiChart.open(rows2, true, "(1.2) cleaned data");
//


        Vector<Messreihe> resultRows01 = calcMetaCorrelationEXTERNAL(groupC, rowC1a, rowC2a, tau, _dtau, true, symbol);
        
        String title = symbol + " (2) C, PC, ICF as a function of tau=" + tau;
        MultiChart.open(resultRows01, true, title );

        MesswertTabelle mwt4 = new MesswertTabelle();
        mwt4.setMessReihen(resultRows01);
        mwt4.setHeader( title );  
        mwt4.setLabel( "metacorrelation.2_image1" );  
        
       
        op.storeMesswertTabelle(mwt4);
        op.storeChart(resultRows01, true, title, "metacorrelation.2_image1");
        
        op.closeAllWriter();

    }

    public static Vector<Messreihe> calcMetaCorrelationEXTERNAL(Vector<Messreihe> groupA, Messreihe m1,  Messreihe m2, int tau, int dt, boolean debug, String symbol) {

        
        Vector<Messreihe> mrs = new Vector<Messreihe>();

        KreuzKorrelation._defaultK = 0;
        KreuzKorrelation.GLdebug = debug;
        KreuzKorrelation.debug = debug;

        Messreihe Ctau = new Messreihe("Ctau");

        Messreihe PCtau1 = new Messreihe("PCtau1");
        Messreihe PCtau2 = new Messreihe("PCtau2");
//        Messreihe PCtau3 = new Messreihe("PCtau3");

        Messreihe ICFtau1 = new Messreihe("ICF1");
        Messreihe ICFtau2 = new Messreihe("ICF2");
//        Messreihe ICFtau3 = new Messreihe("ICF3");

        Messreihe E1 = new Messreihe("E1");
        Messreihe E2 = new Messreihe("E2");
//        Messreihe E3 = new Messreihe("E3");

        Vector<Messreihe> temp1 = new Vector<Messreihe>();
        temp1.addAll(groupA);


        Vector<Messreihe> temp3 = new Vector<Messreihe>();
        temp3.add(m1);
        temp3.add(m2);

//        if (debug) {
//            MultiChart.open(temp1, false, symbol + " raw data groupC => (CN,IWL,AL,BL)");
//        }
//        if (debug) {
//            MultiChart.open(temp3, true, symbol + " raw data m_i");
//        }
//        
        MesswertTabelle mwt1 = new MesswertTabelle();
        mwt1.setMessReihen( temp1 );
        mwt1.setHeader(" raw data ");
        op.storeMesswertTabelle(mwt1);
        
        
        MesswertTabelle mwt3 = new MesswertTabelle();
        mwt3.setMessReihen( temp3 );
        mwt3.setHeader(" m_i ");
        op.storeMesswertTabelle(mwt3);
        
        op.storeChart(temp3, true, symbol + " raw data m_i", "metacorrelation2.m_" );
        op.storeChart(temp1, false, symbol + " raw data groupC => (CN,IWL,AL,BL)", "metacorrelation2.gC_" );

        int maxT = m1.yValues.size();
        int counter = -1;
        int c = 0;

        double DEBUGv = 0.0;

        double vC1 = 0.0;
        double vC2 = 0.0;
        double vC3 = 0.0;

        double vPC1 = 0.0;
        double vPC2 = 0.0;
        double vPC3 = 0.0;

        double vICF1 = 0.0;
        double vICF2 = 0.0;
        double vICF3 = 0.0;

        double vE1 = 0.0;
        double vE2 = 0.0;
        double vE3 = 0.0;
        
        Double C1 = 0.0;


        int x = 0;
        
        for (int i = 0; i < (maxT - tau); i = i + dt) {

            vE1 = 0.0;
            vE2 = 0.0;
            vE3 = 0.0;

            Vector<Double> pCij = new Vector();

            Vector<Double> pPCijm1 = new Vector();
            Vector<Double> pPCijm2 = new Vector();

            counter++;
            int tV = i + (tau / 2);  // middle of intervall 
//            System.out.println("[" + new Date(System.currentTimeMillis()) + "]\tStep: i=" + i + " (tV=" + tV + ") " + vC1);
            int max = groupA.size(); 

            // do all the looping stuff ...
            for (int a = 0; a < max; a++) {
                for (int b = 0; b < max; b++) {

                    if (c > 0) {
                        KreuzKorrelation.GLdebug = false;
                        KreuzKorrelation.debug = false;
                    }
                    c++;

                    Messreihe[] pair = getPair(groupA, a, b);
                    
               
 
                    x++;
                    
                    if (pair != null) {

                        Messreihe[] choppedPair = chopPair(pair, i, tau);
 
      
                        
                        if (choppedPair != null) {

                            Messreihe mChopped1 = m1.cutOut(i, i + tau);
                            Messreihe mChopped2 = m2.cutOut(i, i + tau);
                            
//                            System.out.println( "*****\n" +  choppedPair[0].toString() );
//                            System.out.println( "=====\n" + choppedPair[1].toString() );

                            KreuzKorrelation c_ij = KreuzKorrelation.calcKR(choppedPair[0], choppedPair[1], true);
                            
//                            System.out.println( "i=" + i + " : tau=" + tau );
//                            System.out.println( c_ij.toString() );
                                
                            KreuzKorrelation c_im1 = KreuzKorrelation.calcKR(choppedPair[0], mChopped1, true);
                            KreuzKorrelation c_jm1 = KreuzKorrelation.calcKR(choppedPair[1], mChopped1, true);

                            KreuzKorrelation c_im2 = KreuzKorrelation.calcKR(choppedPair[0], mChopped2, true);
                            KreuzKorrelation c_jm2 = KreuzKorrelation.calcKR(choppedPair[1], mChopped2, true);

                            double ccValue_ij = (Double) c_ij.yValues.elementAt(0);

                            double ccValue_im1 = (Double) c_im1.yValues.elementAt(0);
                            double ccValue_jm1 = (Double) c_jm1.yValues.elementAt(0);

                            double ccValue_im2 = (Double) c_im2.yValues.elementAt(0);
                            double ccValue_jm2 = (Double) c_jm2.yValues.elementAt(0);





                            C1 = ccValue_ij;
                            
                            

                            double aN = ccValue_ij - (ccValue_im1 * ccValue_jm1);
                            double aZ1 = 1 - (ccValue_im1 * ccValue_im1);
                            double aZ2 = 1 - (ccValue_jm1 * ccValue_jm1);
                            double aZ = aZ1 * aZ2;

                            Double PC1 = (aN) / (Math.sqrt(aZ));
//                            if ( debug ) System.out.println( "> C="+ C1 + "\n\tPC=" + PC1 );


                            if (C1.isNaN() || C1.isInfinite()) {
                                vE1++;
                            } 
                            else {
                                pCij.add(C1);
                            }

                            if (PC1.isNaN() || PC1.isInfinite()) {
                                vE2++;
                            } else {
                                pPCijm1.add(PC1);
                            }



                            double bN = ccValue_ij - (ccValue_im2 * ccValue_jm2);
                            double bZ1 = 1 - (ccValue_im2 * ccValue_im2);
                            double bZ2 = 1 - (ccValue_jm2 * ccValue_jm2);
                            double bZ = bZ1 * bZ2;

                            Double PC2 = (bN) / (Math.sqrt(bZ));

                            if (PC2.isNaN() || PC2.isInfinite()) {
                                vE2++;
                            } else {
                                pPCijm2.add(PC2);
                            }
                        }
                    } 
                    else {
                        // System.out.println( " skip (" + a + "," + b +")");
                    }



                }

            }

            // do all the aggregation ...
            
//             MultiChart.open( deb );
            vC1 = _getAverage(pCij);
            vPC1 = _getAverage(pPCijm1);
            vICF1 = vC1 / vPC1;
 
            vPC2 = _getAverage(pPCijm2);
            vICF2 = vC1 / vPC2;
 

            E1.addValuePair(tV, vE1);
            E2.addValuePair(tV, vE2);

            Ctau.addValuePair(tV, vC1 );

            PCtau1.addValuePair(tV, vPC1);
            PCtau2.addValuePair(tV, vPC2); 

            ICFtau1.addValuePair(tV, vICF1);
            ICFtau2.addValuePair(tV, vICF2); 

            System.out.println( "SIZE: " + pCij.size() + ":  " + C1 + " : " + tV + " # " + getAverage3(pCij) );

        }

        mrs.add(Ctau);
        mrs.add(PCtau1);
        mrs.add(PCtau2); 

        mrs.add(ICFtau1);
        mrs.add(ICFtau2); 

        mrs.add(E1);
        mrs.add(E2);

//        System.out.println( Ctau.toString() );
        
        return mrs;
    }
    
    public static OriginProject op = null;

    
    private static Messreihe[] getPair(Vector<Messreihe> groupA, int a, int b) {
//        System.out.println( a + " : " + b  );
        Messreihe[] pair = null;
        if (a < b) {
            pair = new Messreihe[2];
            pair[0] = groupA.elementAt(a);
            pair[1] = groupA.elementAt(b);
        }
        return pair;
    }

    private static Messreihe[] chopPair(Messreihe[] pair, int i, int tau) {
        
//        System.out.println(i + " ... " + tau );
        Messreihe r1 = pair[0].cutOut(i, i + tau);
        Messreihe r2 = pair[1].cutOut(i, i + tau);
        
        pair[0] = r1;
        pair[1] = r2;

//        if (r2.yValues.size() == tau && r1.yValues.size() == tau ) {
//            pair[0] = r1;
//            pair[1] = r2;
//        } else {
//            pair = null;
//        }
        
        
        return pair;
    }

    private static double getAverage3(Vector<Double> p) {
        Double sum = 0.0;
        for (double v : p) {
            sum = sum + v;
//             System.out.println( v + " # " + sum );
        }
        double mw = sum / (double)p.size();
        return mw;
    }
        
    private static double _getAverage(Vector<Double> j) {
        Double sum = 0.0;
        for (double v : j) {
            sum = sum + v;
            
        }
        double mw = sum / j.size();
//        System.out.println( "MW: " + sum );
        return mw;
    }
    static int maxShuffles = 10;

    private static Vector<Messreihe> shuffle(Vector<Messreihe> gr) {
        Vector<Messreihe> groupA = new Vector<Messreihe>();

        for (Messreihe r : gr) {

            Messreihe r2 = r.copy();

            r2.shuffleYValues(maxShuffles);

            groupA.add(r2);
        }

        return groupA;
    }
    static boolean logNetworkFiles = false;
    private static String linkListName = null;

    private static void initTemporalNetwork(int tV) {

        if (!logNetworkFiles) {
            return;
        }

        try {
            String name = "temporalNet.linklist." + tV + ".dat";
            linkListName = name;
            op.createLogFile(name);
        } catch (IOException ex) {
            Logger.getLogger(MetacorrelationAnalysisV2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void addToTemporalNetwork(double ccValue_ij, String labela, String labelb) {

        if (!logNetworkFiles) {
            return;
        }

        String line = labela + "\t" + labelb + "\t" + ccValue_ij + "\n";
        try {
            op.logLine(linkListName, line);
        } catch (IOException ex) {
            Logger.getLogger(MetacorrelationAnalysisV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Vector<Messreihe> setBinning(Vector<Messreihe> groupA, int i) {
        Vector<Messreihe> r = new Vector<Messreihe>();
        for( Messreihe m : groupA ) { 
            Messreihe mr = m.setBinningX_sum(i);
            r.add( mr );      
        
        }
        return r;
        
    }
}
