/*
 * This Project defines the core of the analysis of 
 * the 2009 Wikipedia Dataset.
 * 
 * We define Links between nodes by Cross-Correlations,
 * if they are significant.
 *
 */
package research.wikinetworks;

import extraction.TimeSeriesFactory;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import experiments.crosscorrelation.DailyCrossCorrelationMapper;
import experiments.crosscorrelation.KreuzKorrelation;
import com.cloudera.wikiexplorer.ng.util.FinancialDataNodeGroup;
import com.cloudera.wikiexplorer.ng.util.LogFile;
import com.cloudera.wikiexplorer.ng.util.LogFileUser;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class MainCC implements LogFileUser {

    private static void runFourCalculations(String fileName, CCCalculator ncomp) throws Exception {

        if (modeNotShuffle) {
            NodeGroup.doShuffle = false;
            if (mode0) {
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                ncomp.doCCCalculator();
            }

            if (mode1) {
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                ncomp.doCCCalculator();
            }
        }

        if (modeShuffle) {
            NodeGroup.doShuffle = true;
            if (mode0) {
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                ncomp.doCCCalculator();
            }
            if (mode1) {
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                ncomp.doCCCalculator();
            }
        }

    }

    private static void calcFinancialDataSet(FinancialDataNodeGroup ngF, CCCalculator ncomp, String fileName) throws Exception {
        for (int loop = minLoops; loop < maxLoops + 1; loop++) {

            ngF.name = fileName + "_loop=" + loop;

            ngF.calBegin.set(2003, 11, 31);
            ngF.calEnd.set(2003, 11, 31);

            ngF.calBegin.add(Calendar.DAY_OF_MONTH, interval * loop);
            ngF.calEnd.add(Calendar.DAY_OF_MONTH, interval * (loop + 1));

            ngF.initShortRows();

            runFourCalculations(fileName, ncomp);

        }

    }
    public String fn = null;

    public MainCC() {
        fn = this.getClass() + "_" + new Date(System.currentTimeMillis()) + ".log";
    }
    
    public static boolean mode0 = true;
    public static boolean mode1 = true;
    public static boolean modeShuffle = true;
    public static boolean modeNotShuffle = true;
    public static int mode = 1000;
    public static int minLoops = 0;
    public static int maxLoops = 35;
    public static int interval = 90;

    public static FinancialDataNodeGroup prepareStockDataSeries(
            String folder, String fileName, CCCalculator ncomp) throws IOException {

        FinancialDataNodeGroup ngF = new FinancialDataNodeGroup(folder, fileName);

        ngF.checkStockTimeSeries();
        ngF.showRawData();

        // ngF.checkAccessTimeSeries();
        // ngF.checkEditTimeSeries();

        ngF._initWhatToUse();

//              ngF.prepareRows( ngF.getAaccessReihen(), ngF.getStockDataReihen() );
//              MultiChart.open(ng.getAaccessReihen(),  "Wikipedia Access Data - FILTERED" , "t [days]", "# nr of page clicks per day",false);
//              MultiChart.open(ng.getStockDataReihen(),  "Stock Data - Volume" , "t [days]", "traded volume",false);
//                  

        ncomp.ng = (NodeGroup) ngF;
        return ngF;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        String refFolder = "G:\\DEV\\MLU\\Wiki\\TimeSeriesToolbox\\";
        NodeGroup.pfad = "G:\\PHYSICS\\PHASE2\\data\\out\\node_groups\\";

        TimeSeriesFactory.folderIn = "G:/PHYSICS/PHASE2/data/out/access_ts_h/";
     //   TimeSeriesFactory.folderIn2 = "G:/PHYSICS/32bit/32bit_VphMean/";

        int i = 2;

        if (args != null) {
            if (args.length == 5) {
                refFolder = args[0];
                NodeGroup.pfad = args[1];
                TimeSeriesFactory.folderIn = args[2];
//                TimeSeriesFactory.folderIn2 = args[3];
                i = Integer.parseInt(args[4]);

            } else {
                System.out.println(" zu wenige Argumente - nutze die Defaultwerte: " + args.length);
            }
        }

        System.out.println(" $0 [refFolder                   ] " + refFolder);
        System.out.println(" $1 [NodeGroup.pfad              ] " + NodeGroup.pfad);
        System.out.println(" $2 [TimeSeriesFactory.folderIn  ] " + TimeSeriesFactory.folderIn);
        // System.out.println(" $3 [TimeSeriesFactory.folderIn2 ] " + TimeSeriesFactory.folderIn2);

        System.out.println(" $4 [i                           ] " + i);

        MainCC mc = new MainCC();
        LogFile lf = new LogFile(mc, mc.fn);

        CCCalculator ncomp = CCCalculator.getCCCalculator();

        CCCalculator.doCC_on_EDITS = false;
        CCCalculator.doCC_on_ACCESS = true;

        NodeGroup.doShuffle = false;

        TimeSeriesFactory.doBlock = true; // default is true
        int[] tageZumBlocken = {0, 31, 32, 33, 34, 35, 88, 89, 90, 91, 92, 93, 150, 151, 152, 153, 154, 240, 241, 242, 243, 244,};
        TimeSeriesFactory.tageZumBlocken = tageZumBlocken;

        NodeGroup.offsetAtEnd = 150;
        NodeGroup.offsetAtStart = 150;

        /**
         * Mit NodeGroup File und allen nicht doppelten und nicht selbst
         * referenzierten Paaren.
         * --------------------------------------------------------- 0 = FullRun
         * 1 = manuelle Analyse einzelner Reihen 2 = manuelle Auswahl, nur ein
         * RUN 3 = manuelle Auswahl, aber Vergleich der ersten und zweiten
         * Hälfte. 4 = FullRun, aber Vergleich der ersten und zweiten Hälfte.
         *
         *
         * 40 = BEGINN an den Access-Nodes
         *
         *
         *
         * Mit NodePairList und nur den darin enthaltenen Paaren
         * --------------------------------------------------------- 12 =
         * manuelle Auswahl, nur ein RUN 13 = AllLinked32bit, nur ein RUN 14 =
         * Demo mit BLOCKEN und ohne nacheinenader, zwei RUNs
         *
         * 25 = MIT ZUFALLSZAHLEN REIHEN ....
         *
         * 30 = wie 0, aber mit mode = mode_CC_TAU_0 31 = wie 30, aber mit 10
         * monatl. Abschnitten
         *
         *
         *
         * Neue Auswahl von Node-Groups ============================ 200 -
         * rechne mit 600, 620, 640
         *
         *
         */
        stdlib.StdRandom.initRandomGen(0);

        switch (mode) {

            case 2001: {
                String fileName = "2001_sp500_filtered_lrp";
                
                NodeGroup.useStockData = true;
                FinancialDataNodeGroup.doUseLogReturn = true;
                FinancialDataNodeGroup.selectedColumn = 8;  // volume: 7
            
                FinancialDataNodeGroup ngF = prepareStockDataSeries("sp500", fileName, ncomp);
                calcFinancialDataSet( ngF, ncomp, fileName );
                break;
            }


            case 2000: {
                String fileName = "2000_DAX_filtered_lrp";

                NodeGroup.useStockData = true;
                FinancialDataNodeGroup.doUseLogReturn = true;
                FinancialDataNodeGroup.selectedColumn = 8;  // volume: 7

                FinancialDataNodeGroup ngF = prepareStockDataSeries("dax", fileName, ncomp);
                calcFinancialDataSet( ngF, ncomp, fileName );
                break;
            }

            case 1001: {
                String fileName = "1001_sp500_filtered_vol";

                NodeGroup.useStockData = true;
                FinancialDataNodeGroup.doUseLogReturn = false;
                FinancialDataNodeGroup.selectedColumn = 7;  // volume: 7

                FinancialDataNodeGroup ngF = prepareStockDataSeries("sp500", fileName, ncomp);
                calcFinancialDataSet( ngF, ncomp, fileName );
                break;
            }


            case 1000: {

                String fileName = "1000_DAX_filtered_vol";

                NodeGroup.useStockData = true;
                FinancialDataNodeGroup.doUseLogReturn = false;
                FinancialDataNodeGroup.selectedColumn = 7;  // volume: 7

                FinancialDataNodeGroup ngF = prepareStockDataSeries("dax", fileName, ncomp);
                calcFinancialDataSet( ngF, ncomp, fileName );
                break;
            }


            // Financial DATA-SET
            case 200: {

                String fileName = null;
                boolean doSingleRun = false;

//                for ( int i = 1; i < 2; i++ ) {

                int fID = i;

                NodeGroup.doShuffle = false;

                fileName = ncomp.selectFromNewDatasets(fID, refFolder);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                lf.logData(">>> Starte : " + fileName + " mode: " + CheckInfluenceOfSingelPeaks.mode);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                fileName = ncomp.selectFromNewDatasets(fID, refFolder);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                lf.logData(">>> Starte : " + fileName);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                NodeGroup.doShuffle = true;

                fileName = ncomp.selectFromNewDatasets(fID, refFolder);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                lf.logData(">>> Starte : " + fileName + " mode: " + CheckInfluenceOfSingelPeaks.mode);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                fileName = ncomp.selectFromNewDatasets(fID, refFolder);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                lf.logData(">>> Starte : " + fileName);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                //              }

                break;
            }

            // Random DATA-SET
            case 300: {
                stdlib.StdRandom.initRandomGen(1);
                String fileName = null;
                boolean doSingleRun = false;

                NodeGroup.doShuffle = false;

                int z = 250;

                fileName = ncomp.selectRandomGeneratedNG_withPeaks("RST_A_" + z, z);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                lf.logData(">>> Starte : " + fileName + " mode: " + CheckInfluenceOfSingelPeaks.mode);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                fileName = ncomp.selectRandomGeneratedNG_withPeaks("RST_A_" + z, z);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                lf.logData(">>> Starte : " + fileName);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                NodeGroup.doShuffle = true;

                fileName = ncomp.selectRandomGeneratedNG_withPeaks("RST_A_" + z, z);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                lf.logData(">>> Starte : " + fileName + " mode: " + CheckInfluenceOfSingelPeaks.mode);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                fileName = ncomp.selectRandomGeneratedNG_withPeaks("RST_A_" + z, z);
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                lf.logData(">>> Starte : " + fileName);
                doSingleRun = ncomp.doCCCalculator();
                lf.logData("    Status : " + fileName + " ==> " + doSingleRun);

                break;
            }

            case 0: {
                //ncomp.doFullRun();  

                NodeGroup.doSplitRows = false;
                File fn2 = new File("60_1000_most_active_by_access.dat");
                ncomp.doSingleRun(fn2.getName());
//                
//                File fn2 = new File("60_1000_most_active_by_access.dat");
//                ncomp.doManualRun();
//                

                break;
            }
            case 30: {

                /**
                 * DI 1.11.2011
                 */
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//                CheckInfluenceOfSingelPeaks._debug = false;
                KreuzKorrelation.debug = false;

//                NodeGroup.doShuffle = false;
//                ncomp.doFullRun();

                NodeGroup.doShuffle = true;
                ncomp.doFullRun();

                break;
            }

            case 31: {

                /**
                 * DI 1.11.2011 MI 2.11.
                 */
//                CheckInfluenceOfSingelPeaks._debug = false;
                KreuzKorrelation.debug = false;
                NodeGroup.debug = false;
                DailyCrossCorrelationMapper.debug = false;

                TimeSeriesFactory.doFilter = true;


                NodeGroup.doSplitRows = true;
                NodeGroup.maxSplitIndex = 3;
                NodeGroup.splitLength = 30;



//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//                NodeGroup.doShuffle = false;
//                ncomp.doFullRun();
//
                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                NodeGroup.doShuffle = false;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
                NodeGroup.doShuffle = false;
                ncomp.doFullRun();

//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//                NodeGroup.doShuffle = true;
//                ncomp.doFullRun();
//
//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//                NodeGroup.doShuffle = true;
//                ncomp.doFullRun();
//
//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
//                NodeGroup.doShuffle = true;
//                ncomp.doFullRun();

                break;
            }

            case 32: {

                /**
                 * MO 7.11.2011
                 */
//                CheckInfluenceOfSingelPeaks._debug = false;
                KreuzKorrelation.debug = false;
                NodeGroup.debug = false;
                DailyCrossCorrelationMapper.debug = false;

                TimeSeriesFactory.doFilter = true;

                NodeGroup.doSplitRows = true;
                NodeGroup.maxSplitIndex = 9;
                NodeGroup.splitLength = 30;



//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//                NodeGroup.doShuffle = false;
//                ncomp.doManualRun();
//
//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//                NodeGroup.doShuffle = false;
//                ncomp.doManualRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
                NodeGroup.doShuffle = false;
                ncomp.doManualRun();

//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
//                NodeGroup.doShuffle = true;
//                ncomp.doManualRun();
//
//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
//                NodeGroup.doShuffle = true;
//                ncomp.doManualRun();
//
//                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
//                NodeGroup.doShuffle = true;
//                ncomp.doManualRun();

                break;
            }

            case 33: {

                /**
                 * MI 8.11.2011
                 */
//                CheckInfluenceOfSingelPeaks._debug = false;
                KreuzKorrelation.debug = false;
                NodeGroup.debug = false;


                DailyCrossCorrelationMapper.debug = false;

                TimeSeriesFactory.doFilter = false;

                NodeGroup.doSplitRows = true;
                NodeGroup.maxSplitIndex = 9;
                NodeGroup.splitLength = 30;

                PageLanguageChecker.skipCheck = true;

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                NodeGroup.doShuffle = false;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                NodeGroup.doShuffle = false;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
                NodeGroup.doShuffle = false;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
                NodeGroup.doShuffle = true;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;
                NodeGroup.doShuffle = true;
                ncomp.doFullRun();

                CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_ADVANCED;
                NodeGroup.doShuffle = true;
                ncomp.doFullRun();

                break;
            }


            case 1: {
                ncomp.selectNodeGroup();
                ncomp.selectNodesOfTheGroup();
                break;
            }

            case 2: {
                TimeSeriesFactory.doFilter = true;
                ncomp.doManualRun();
                break;
            }

            case 3: {
                /**
                 * MANUELL
                 *
                 * löscht zuerst die erste oder zweite Hälfte der Reihen und
                 * berechent dann zwei Sätze, um den Vergleich zu zeigen
                 */
                NodeGroup.supressFirstPart = true;
                NodeGroup.supressLastPart = false;

                ncomp.doManualRun();

                javax.swing.JOptionPane.showConfirmDialog(null, "Datenreihe : " + ncomp.ng.fn + " fertig! ");

                NodeGroup.supressFirstPart = false;
                NodeGroup.supressLastPart = true;
                ncomp.doManualRun();
                break;
            }

            case 4: {
                /**
                 * FULL
                 *
                 * löscht zuerst die erste oder zweite Hälfte der Reihen und
                 * berechent dann zwei Sätze, um den Vergleich zu zeigen
                 */
                NodeGroup.supressFirstPart = false;
                NodeGroup.supressLastPart = true;
                ncomp.doFullRun();

                javax.swing.JOptionPane.showConfirmDialog(null, "Datenreihen mit erster Hälfte wurden beendet ... ");

                NodeGroup.supressFirstPart = true;
                NodeGroup.supressLastPart = false;
                ncomp.doFullRun();
                break;
            }

            case 12: {
                ncomp.selectNodePairListe();
                boolean doSingleRun = ncomp.doCCCalculator();
                break;
            }
            case 13: {
                ncomp.selectAllLinked32bit();
                boolean doSingleRun = ncomp.doCCCalculator();
                break;
            }

            case 14: {
                stdlib.StdRandom.initRandomGen(0);

                TimeSeriesFactory.doBlock = false;
                TimeSeriesFactory.doFilter = false;
                NodeGroup.doShuffle = false;
                ncomp.selectAllLinked32bit_WITH_CONTEXT_Full();
                boolean doSingleRun1 = ncomp.doCCCalculator();

                TimeSeriesFactory.doBlock = false;
                TimeSeriesFactory.doFilter = false;
                NodeGroup.doShuffle = true;
                ncomp.selectAllLinked32bit_WITH_CONTEXT_Full();
                boolean doSingleRun2 = ncomp.doCCCalculator();

                break;
            }

            case 15: {
                stdlib.StdRandom.initRandomGen(0);

                TimeSeriesFactory.doBlock = false;
                TimeSeriesFactory.doFilter = true;
                NodeGroup.doShuffle = false;
                ncomp.selectAllLinked32bit_WITH_CONTEXT_Full();
                boolean doSingleRun1 = ncomp.doCCCalculator();

                TimeSeriesFactory.doBlock = false;
                TimeSeriesFactory.doFilter = true;
                NodeGroup.doShuffle = true;
                ncomp.selectAllLinked32bit_WITH_CONTEXT_Full();
                boolean doSingleRun2 = ncomp.doCCCalculator();

                break;
            }


            case 25: {

                stdlib.StdRandom.initRandomGen(0);

                NodeGroup.doShuffle = false;
                TimeSeriesFactory.doBlock = false;

                ncomp.selectNodeGroupRandomly();
                boolean doSingleRun1 = ncomp.doCCCalculator();

                stdlib.StdRandom.initRandomGen(0);

                NodeGroup.doShuffle = true;
                TimeSeriesFactory.doBlock = false;

                ncomp.selectNodeGroupRandomly();
                boolean doSingleRun2 = ncomp.doCCCalculator();

                break;
            }



        }
        ncomp.tl.setStamp("READY!");
        ncomp.showTimeLog();

        if (isAlone) {
            System.exit(0);

        }
    }
    public static boolean isAlone = true;

    public String getLogFileHeader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
