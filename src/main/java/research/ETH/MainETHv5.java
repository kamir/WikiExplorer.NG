/*
 * This Project defines the core of the analysis of 
 * the correlation between the 2009 Wikipedia Dataset and
 * the Stock market data.
 *
 * YEAR MONTH DAY OPEN HIGH LOW CLOSE VOLUME ADJ.CLOSE
 * 
 * Please note that not necessarily all companies were traded during the 
 * entire time period. Could you please focus on correlations between volume 
 * (change) time series and corresponding access (change) time series as well 
 * as between adjusted close (change) time series and corresponding access 
 * (change) time series? I would be very interesting to see distributions of 
 * Pearson correlation coefficients for different time 
 * lags, e.g. -3, ..., 0, ..., +3 days.
 *
 * 
 * // filter Acces Ts based on trading ts ...
 * 
 * Could you please restrict access volume to these days, i.e. neglecting 
 * weekends and bank holidays etc?
 * 
 */
package research.ETH;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import extraction.TimeSeriesFactory;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoopts.statistics.DistributionTester;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import experiments.crosscorrelation.KreuzKorrelation;
import experiments.linkstrength.CCFunction;
import com.cloudera.wikiexplorer.ng.util.FinancialDataNodeGroup;
import com.cloudera.wikiexplorer.ng.util.LogFileUser;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 *
 * @author kamir
 */
public class MainETHv5 implements LogFileUser {
    
    public static DistributionTester dtA = null;
    public static DistributionTester dtB = null;

    /**
     * Lese das Mapping der Stock Codes zu PageID aus der Textdatei.
     */
    private static Hashtable<String, Vector<Integer>> fill(Hashtable<String, Vector<Integer>> map, File mapperFile) throws FileNotFoundException, IOException {
        System.out.println( "MAPPER FILE : " + mapperFile.getAbsolutePath() + " " + mapperFile.canRead() );
        
        
        
        BufferedReader br = new BufferedReader(new FileReader(mapperFile));
        int lineCounter = 0;
        while (br.ready()) {
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line, "\t");
            String id = st.nextToken();
            String code = st.nextToken();
            System.err.println(id + " ... " + code);
            Vector<Integer> ids = map.get(code);
            if (ids == null) {
                ids = new Vector<Integer>();
                map.put(code, ids);
            }
            ids.add(Integer.parseInt(id));
            lineCounter++;
        }
        System.err.println(map.size() + " [" + lineCounter + "] ...");
        
//        System.exit(0);
        return map;
    }
    public static boolean doShuffle = false;
    public static boolean debug = false;

    /**
     * Stunden Daten werden hier gebinnt auf Tage, ohne Filterung..
     */
    private static TimeSeriesObject getPageForID(Integer id) {
        TimeSeriesObject mr = null;
        try {
            mr = TimeSeriesFactory.prepareAccessDataSTUNDE(id, 299 * 24);

            mr = mr.setBinningX_sum(24);
            mr.setLabel("" + id);
            if (doShuffle) {
                mr.shuffleYValues();
            }

        } catch (Exception ex) {
            if (debug) {
                Logger.getLogger(NodeGroup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mr;
    }

    private static FinancialDataNodeGroup prepareRadnomDataSeries(String folder, String mapper) throws IOException {
        FinancialDataNodeGroup ngF = new FinancialDataNodeGroup(folder, mapper);
        
        ngF.createRandomSample();
        
        ngF.checkStockTimeSeries();
//        ngF.checkAccessTimeSeries();        
//        ngF.checkEditTimeSeries();

        ngF._initWhatToUse();
        ngF.doRandomizeAllNow();
        ngF.doShuffleAllNow();

        return ngF;
    }
    public String fn = null;

    public MainETHv5() {
        fn = this.getClass() + "_" + new Date(System.currentTimeMillis()) + ".log";
    }
    public static int NR_OF_SHUFFLINGS = 10;
    
    public static String folder = "dax";
    public static String mapper = "DAX.data";
    public static String group = "DAX";
    public static File mapperFile = new File("/home/kamir/dayly_stock_data/list_DAX_stockcode_PageID_mapping.tab");
//    
//    public static String folder = "sp500";
//    public static String mapper = "SP500.data";
//    public static String group = "SP500";
//    public static File mapperFile = new File( "out/sup500_mapper.dat");
//    
    public static boolean showCharts = true;
    public static boolean quite = false;
    
    // tau = offset ...
    static int offset = 1;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {

        
        // 11 war der letzte mit echten Daten
        // 20 ist nun für Zufallszahlen
        String run = "R20";
        
        System.out.println( " gooo .... \n\n offset="+offset+"\n run="+run );
        
        TimeLog tl = new TimeLog();
        tl.setStamp("Run ... ");

        PrintStream out = System.out;
        PrintStream err = System.err;
        
        final String offsetPath = "/home/kamir/DATA/ETH/";

        if (quite) {
            System.setOut(new PrintStream(
                    new OutputStream() {

                        @Override
                        public void write(int b) {
                        }
                    ;
            }
                )
            );
 
            System.setErr(new PrintStream(
                    new OutputStream() {

                        @Override
                        public void write(int b) {
                        }
                    ;
        }
        )
            );
        }

        File f = new File( offsetPath + run );
        if ( !f.exists() ) f.mkdirs();
        
        bw = new BufferedWriter(new FileWriter( offsetPath + run + "/" + group + "_statistics.dat"));

//        int[] ov = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170, 180, 200, 210};
//        int[] ov = { 0,10,20,30,40,50,60,90, 100, 110,120, 130    150,    ,140,,160,170,180 };
        

        int[] ov = { 80 } ;        
//           70   
        
//       
        int[] kv = {5};

        int[] nv = {   20, 40, 60, 80, 100 };//,
//        int[] nv = {   };

        for (int k : kv) {
            for (int o : ov) {
                for (int n : nv) {
                    if ((o + n) < 200) {
                        doLoop(o, n, k, run);
                        tl.setStamp(o + " " + n + " " + k + " ... done! ");
                        out.println( o + " " + n + " " + k + " ... done! " );
                        
                        bw.flush();
                        
//                        HaeufigkeitsZaehlerDouble zA = new HaeufigkeitsZaehlerDouble();
//                        zA.addData( MainETH.dtA.getData() );
//                        zA.calcWS();
//
//                        HaeufigkeitsZaehlerDouble zB = new HaeufigkeitsZaehlerDouble();
//                        zB.addData( MainETH.dtB.getData() );
//                        zB.calcWS();
//
//                        TimeSeriesObject[] r = new TimeSeriesObject[2];
//                        r[0] = zA.getHistogram();
//                        r[1] = zB.getHistogram();
//
//                        MultiBarChart.open( r );
//                        
//                        dtA = null;
//                        dtB = null;
                    }
                }                
            }
        }

        bw.flush();
        bw.close();

        tl.print(out);
        
        System.setOut(out);
        System.setErr(err);
        

    
        // DistributionCheckCharts.main(null);
        
        System.out.println( "OK" );
        
//        System.exit(0);
        
        
    }
    static BufferedWriter bw = null;

    public static void doLoop(int o, int n, int k, String run) throws FileNotFoundException, IOException, Exception {

        stdlib.StdRandom.initRandomGen(1);

        distrA = new Vector<TimeSeriesObject>();
        distrB = new Vector<TimeSeriesObject>();

        NodeGroup.useStockData = true;
        
        FinancialDataNodeGroup.doUseLogReturn = false;
        FinancialDataNodeGroup.doUseAbsLogReturn = false;
        
        FinancialDataNodeGroup.selectedColumn = 7;  // volume: 7

        DistributionCompare compare0 = new DistributionCompare();
        doWork(false, o, n, k, "tv", run);
        compare0.zNormal = linksB.getDataArray().clone();
        
        doWork(true, o, n, k, "tv", run);
        compare0.zShuffles = linksB.getDataArray().clone();
        
        compare0.storeData(bw, o + " " + n + " " + k + " tv ");

        if (showCharts) {
            MultiChart.open(distrA, group + " [o=" + o + " n=" + n + " k=" + k + "] : trading volume", "link strength -> normalized ", "# of links", true);
            MultiChart.open(distrB, group + " [o=" + o + " n=" + n + " k=" + k + "] : trading volume", "link strength -> cc("+offset+")", "# of links", true);
        }

//        distrA = new Vector<TimeSeriesObject>();
//        distrB = new Vector<TimeSeriesObject>();
//
//        NodeGroup.useStockData = true;
//        FinancialDataNodeGroup.doUseLogReturn = true;
//        FinancialDataNodeGroup.doUseAbsLogReturn = false;
//        FinancialDataNodeGroup.selectedColumn = 8;  // volume: 7
//
//        DistributionCompare compare1 = new DistributionCompare();
//        doWork(false, o, n, k, "lrp", run);
//        compare1.zNormal = linksB.getDataArray().clone();
//        doWork(true, o, n, k, "lrp", run);
//        compare1.zShuffles = linksB.getDataArray().clone();
//        compare1.storeData(bw, o + " " + n + " " + k + " lrp ");
//
//        if (showCharts) {
//            MultiChart.open(distrA, group + " [o=" + o + " n=" + n + " k=" + k + "] : logreturn of prices", "link strength -> normalized ", "# of links", true);
//            MultiChart.open(distrB, group + " [o=" + o + " n=" + n + " k=" + k + "] : logreturn of prices", "link strength -> cc("+offset+") ", "# of links", true);
//        }
//
//        distrA = new Vector<TimeSeriesObject>();
//        distrB = new Vector<TimeSeriesObject>();
//
//        NodeGroup.useStockData = true;
//        FinancialDataNodeGroup.doUseLogReturn = false;
//        FinancialDataNodeGroup.doUseAbsLogReturn = true;
//        FinancialDataNodeGroup.selectedColumn = 8;  // volume: 7
//
//        DistributionCompare compare2 = new DistributionCompare();
//        doWork(false, o, n, k, "abs_lrp", run);
//        compare2.zNormal = linksB.getDataArray().clone();
//        doWork(true, o, n, k, "abs_lrp", run);
//        compare2.zShuffles = linksB.getDataArray().clone();
//        compare2.storeData(bw, o + " " + n + " " + k + " abs_lrp ");
//
//        // hier sind nun in distrA die geschufflte und die nicht geschufflete Reihe drin , für Mode 0 ),
//
//        if (showCharts) {
//            MultiChart.open(distrA, group + " [o=" + o + " n=" + n + " k=" + k + "] : abs( logreturn of prices ) ", "link strength -> normalized", "# of links", true);
//            MultiChart.open(distrB, group + " [o=" + o + " n=" + n + " k=" + k + "] : abs( logreturn of prices ) ", "link strength -> cc("+offset+")", "# of links", true);
//        }

    }
    public static Vector<TimeSeriesObject> distrA = new Vector<TimeSeriesObject>();
    public static Vector<TimeSeriesObject> distrB = new Vector<TimeSeriesObject>();
    static HaeufigkeitsZaehlerDouble linksA = null;
    static HaeufigkeitsZaehlerDouble linksB = null;

    public static void doWork(boolean shuffle, int offsetDays, int nrOfDays, int k, String value, String run) throws FileNotFoundException, IOException, Exception {

        linksA = new HaeufigkeitsZaehlerDouble();
        linksA.min = -5;
        linksA.max = 5;
        linksA.intervalle = 50;

        linksB = new HaeufigkeitsZaehlerDouble();
        linksB.min = -0.8;
        linksB.max = 0.8;
        linksB.intervalle = 50;

        doShuffle = shuffle;

        KreuzKorrelation.globalShuffle = doShuffle;

        if (!mapperFile.canRead()) {
//            System.exit(-1);
            System.err.println("Can not read mapper-file! (" + mapperFile.getAbsolutePath() + ")" );
        }

        Hashtable<String, Vector<Integer>> map = new Hashtable<String, Vector<Integer>>();
        /**
         * TODO:
         * lese den richtigen Mapper ...
         */
        //map = fill(map, mapperFile);


        // FinancialDataNodeGroup ngF = prepareStockDataSeries(folder, mapper);
        FinancialDataNodeGroup ngF = prepareRadnomDataSeries(folder, mapper);

        ngF.calBegin.clear();
        ngF.calEnd.clear();
        ngF.calBegin.set(2009, 0, 1);
        ngF.calEnd.set(2009, 0, 1);
        ngF.calBegin.add(Calendar.DAY_OF_YEAR, offsetDays);
        ngF.calEnd.add(Calendar.DAY_OF_YEAR, offsetDays);

        ngF.calEnd.add(Calendar.DAY_OF_MONTH, nrOfDays);

        List<Date> l = ngF.initShortRows();

        Vector<TimeSeriesObject> vfmr = ngF.getStockDataReihen();

        Vector<TimeSeriesObject> vwmr = new Vector<TimeSeriesObject>();
        ngF.checkStockTimeSeries();

        ngF.showRawData();        
       
        int j = 0;

        KreuzKorrelation._defaultK = k;
        KreuzKorrelation.setK(k);
        System.out.println( "k=" + k );
        // System.exit( 0 );

        CCFunction.ID_TO_SELECT_CC_FROM = k + offset;

        BufferedWriter bw = new BufferedWriter(new FileWriter("/home/kamir/DATA/ETH/"+run+"/" + group + "_" + value + "_" + k + "_" + offsetDays + "_" + nrOfDays + "_WIKI_CC_shuffle=" + doShuffle + ".dat"));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter("/home/kamir/DATA/ETH/"+run+"/" + group + "_WIKI_Missing_WIKIPAGES.dat"));

        // für alle Börsenkurse ...
        for (TimeSeriesObject finanzMR : vfmr) {

            Vector<TimeSeriesObject> mrKK = new Vector<TimeSeriesObject>();

            System.out.print(finanzMR.getLabel() + " \t");
            StringTokenizer st = new StringTokenizer(finanzMR.getLabel(), ".");
            String code = st.nextToken();
            finanzMR.setLabel(code);

            Vector<Integer> ids = map.get(code);
            if (ids != null) {
                System.out.print(code + " nr of pages=" + ids.size());

                for (Integer id : ids) {
                    Calendar cBegin = new GregorianCalendar();
                    cBegin.clear();
                    cBegin.set(2009, 0, 1);

                    TimeSeriesObject mrWIKI = getPageForID(id);

//                    mr2.mapToStartingDate(2009, 1, 1);
//                    mr2.filterAndMapBack( l );

                    if (mrWIKI != null) {

                        mrWIKI.mapDataToDateHash(cBegin);
                        mrWIKI.filterAndMapBack(l);

                        vwmr.add(mrWIKI);

                        int z = 1;
                        if (shuffle) {
                            z = NR_OF_SHUFFLINGS;
                        }
                        for (int iii = 0; iii < z; iii++) {

                            if (shuffle) {
                                finanzMR.shuffleYValues();
                                mrWIKI.shuffleYValues();
                            }

                            ExtendedNodePair np = new ExtendedNodePair(finanzMR, mrWIKI);

                            KreuzKorrelation kk = np.calcCrossCorrelation();

                            np.getLinkStrength();

                            linksA.addData(np.getLinkA());
                            linksB.addData(np.getLinkB());

                            System.out.println(np.toString());
                            String line = np.toString();
                            if (line != null) {
                                bw.write(line + "\n");
                            }

                            mrKK.add(kk);
                        }
                    } else {
                        System.out.println(id);
                        bw2.write(id + "\n");
                    }
                    j++;
                }
            }
            // MultiChart.open( mrKK , "cross correlation functions :: STOCK CODE : " + code , "k" , "CC(k)" , false );

        }

        //MultiChart.open( vfmr , "stock data " , "t" , "trading volume per day" , false);
        //MultiChart.open( vwmr , "wikiaccess data" , "t" , "#of clicks per day" , false );
        bw.flush();
        bw2.flush();
        bw.close();

        linksA.calcWS();
        linksB.calcWS();

        TimeSeriesObject lA = linksA.getHistogram();
        TimeSeriesObject lB = linksB.getHistogram();

        String label = "notShuffled";

        if (shuffle) {
            lA = linksA.getHistogram(1.0 / NR_OF_SHUFFLINGS);
            lB = linksB.getHistogram(1.0 / NR_OF_SHUFFLINGS);
            label = "shuffled";
        }



        lA.setLabel(label + "_linkA");
        lB.setLabel(label + "_linkB");

        distrA.add(lA);
        distrB.add(lB);

        System.out.println(">>> Ready .... ");
    }

    public static FinancialDataNodeGroup prepareStockDataSeries(
            String folder, String fileName) throws IOException {

        FinancialDataNodeGroup ngF = new FinancialDataNodeGroup(folder, fileName);
        ngF.checkStockTimeSeries();

        // ngF.checkAccessTimeSeries();
        // ngF.checkEditTimeSeries();

        ngF._initWhatToUse();

        return ngF;
    }

    @Override
    public String getLogFileHeader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
