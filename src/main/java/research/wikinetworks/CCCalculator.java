package research.wikinetworks;

import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;
import com.cloudera.wikiexplorer.ng.util.nodegroups.PeakedRandomNodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.AllLinked32bit;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.RandomNodesGroup;
import com.cloudera.wikiexplorer.ng.util.FinancialDataNodeGroup;
import com.cloudera.wikiexplorer.ng.gui.NodeIDSelection;
//import charts.MultiBarChart;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import experiments.crosscorrelation.KreuzKorrelation;
import org.apache.hadoopts.chart.statistic.HistogramChart;
import extraction.TimeSeriesFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import org.jfree.ui.RefineryUtilities;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import com.cloudera.wikiexplorer.ng.util.reports.CCAnalyseReport;

/**
 *
 * @author kamir
 */
public class CCCalculator {
    
    static boolean doAnyway = true; // überschriebe die Verfügbarkeitsprüfung der TS

    public static String langID = "52";
    public static int nrOfNodes = 1000;

    public static boolean doCC_on_EDITS = false;
    public static boolean doCC_on_ACCESS = true;


    /**
     *  We have strong correlations in 
     *
     *      ***Acces TS***
     *  , maybe because of the strong peeks at the
     *  beginning of the series, so we cut the fist values.
     */
    public static int START_CUTTOF_LENGTH = 10;
    public static boolean doCutOffAtSTART = false;


    boolean showOriginalEdits = true;
    boolean showOriginalAccess = true;

    boolean showKKEdits = false;
    boolean showKKAccess = false;

    boolean showAVKKEdits = false;
    boolean showAVKKAccess = false;
    
    boolean showHistogramme = true;

    boolean logCrossCorrelations = true;

//    public String[] langIDS = {  "52", "60", "62", "72", "197", "-1" };
    String[] langIDS = { "60" }; //, "52", "62", "72", "197" } ; // b, "60", "62" };

    String[] _anzV = { "20", "100", "200", "500", "1000", "5000" };

    File f = null;
    public NodeGroup ng = null;

    int nrOfEditSeries = 0;
    int nrOfAccessSeries = 0;

    com.cloudera.wikiexplorer.ng.util.TimeLog tl = new com.cloudera.wikiexplorer.ng.util.TimeLog();

    static CCCalculator cc = null;
    public static CCCalculator getCCCalculator() {
        cc = new CCCalculator();
        return cc;
    };


    private CCCalculator() {
        System.out.println(">>> START of CCCalculator ... v1.5.1 <<< ");
        tl.setStamp("Start CCCalculator v1.5.1");
    }

    /**
     * alle Sprachen aber nur eine Anzahl von Knoten berechnen ...
     *
     * default = 100;
     *
     * @throws Exception
     */
    public void doFullRun() throws Exception {

        int nr = nrOfNodes;
        for( String l : langIDS ) {

            // HIER UMSCHALTEN, ob ACCESS ODER EDITS ...
            String fn = l+"_"+nr+ EditActivityFilter.extension2 ;
            File f = new File( fn );
            
            System.out.println( f.getAbsolutePath() + "; canRead=" + f.canRead() );
            
            doSingleRun( fn );
        }
    };


    public void doManualRun() throws Exception {
        /**
         * Node Group Selection
         */
        this.isPreInited = false;

        selectAllLinked32bit_WITH_CONTEXT_Partial();

 //        selectNodeGroup();
        
        
        
        doSingleRun( ng.fn );
    };

    /**
     * A single run uses a NodeGroup File labeld by
     * LangID_NR_"most active ..."
     *
     * But hier only the name comes up.
     *
     * @param _fn
     * @throws Exception
     */
    boolean isPreInited = false;
    public boolean doSingleRun( String _fn ) throws Exception {
        boolean back = true;

        if ( NodeGroup.doSplitRows ) {
            for ( int i = 0; i < NodeGroup.maxSplitIndex ; i++ ) {

                NodeGroup.splitIndex = i;

                if ( i==0 && !isPreInited ){
                    NodeGroup.useBuffer = false;
                    ng = new NodeGroup( _fn );
                    ng.load();
                    ng._initWhatToUse();

                }
                else {
                    NodeGroup.useBuffer = true;
                }


                back = back && doCCCalculator();
            }
        }
        else {
            ng = new NodeGroup( _fn );
            ng.load( 10 );
            ng._initWhatToUse();

            back = doCCCalculator();
        }
        return back;
    }

    public boolean doCCCalculator() throws Exception {

        Hashtable<String, String> data = new Hashtable<String, String>();
        StringBuffer parameter = new StringBuffer("<p><ul>");
        parameter.append("<li> <b>Cut-off at begin :</b> " + doCutOffAtSTART + "</li>" );
        parameter.append("<li> <b>block peaking days :</b> " + TimeSeriesFactory.doBlock + "</li>" );
        parameter.append("<li> <b>do shuffling :</b> " + NodeGroup.doShuffle + "</li>");
        parameter.append("<li> <b>CC mode :</b> " + CheckInfluenceOfSingelPeaks.mode + "</li>" );
        parameter.append("<li> <b>only periode T2 :</b> " + NodeGroup.supressFirstPart + "</li>");
        parameter.append("<li> <b>only periode T1 :</b> " + NodeGroup.supressLastPart + "</li>" );
        parameter.append("<li> <b>Split-ID :</b> " + NodeGroup.splitIndex + "</li>" );
        parameter.append("<li> <b>Split-length :</b> " + NodeGroup.splitLength + "</li>" );

        if ( !NodeGroup.useBuffer ) {
            ng.checkForDoubleIds();
            langID = ng.getLangID();
            nrOfNodes = ng.ids.length;
        }

        String headerLine = "#[pair] \tk|y=max \tstdDev \tmaxY \tsignLeve\n";

        tl.setStamp("[#ofNodes = "+ nrOfNodes + "] in group: " + ng.fn);

        System.out.println( ">>> Use: " + ng.pfad + "/cc_edits/" + ng.fn + ng.getStateExtension() + ".cc.log" );
        
        BufferedWriter bwEDITS = new BufferedWriter( new FileWriter( ng.pfad + "/cc_edits/" + ng.fn + ng.getStateExtension() + ".cc.log" ));
        BufferedWriter bwACCESS = new BufferedWriter( new FileWriter( ng.pfad + "/cc_access/" + ng.fn + ng.getStateExtension() + ".cc.log" ));

        bwEDITS.write( "# " + ng.fn + "\n# edits crosscorrelations ");
        bwEDITS.write( headerLine );

        bwACCESS.write( "# " + ng.fn + "\n# access crosscorrelations ");
        bwACCESS.write( headerLine );

        /**
         * Check for availability of data rows
         */
        boolean editsAvailable = false;
        if (doCC_on_EDITS) {
            editsAvailable = ng.checkEditTimeSeries();
            System.out.println( "EDITS" );            
        }
        
        boolean accessAvailable = false;
        if (doCC_on_ACCESS) {
            accessAvailable = ng.checkAccessTimeSeries();
            System.out.println( ">>> work with ACCESS-TS ... accessAvailable=" + accessAvailable );
        }
        
        // Was tun wenn einige fehlen ????
        
        if ( doAnyway ) { 
            System.err.println( ">>> Arbeite trotzdem weiter ... accessAvailable=" + accessAvailable );
            accessAvailable = true;
        }


//        /*** NUR ZUM TESTEN des REPORTS ***/
//        ng.editReihen = ng.accessReihen;
//        editsAvailable = true;


        /**
         *
         *   Die ersten 30 Tage entfernen, weil da seltsame Artefakte da waren ...
         *
         */
        if( !NodeGroup.doShuffle ) {
            if ( doCutOffAtSTART ) {
                ng.cutOffAtStart( START_CUTTOF_LENGTH );
            }
        }

        /**
         * If not all data available ... goBack with FALSE !!!
         */
        // if ( !(editsAvailable && accessAvailable )) return false;


        Vector<TimeSeriesObject> rE = ng.editReihen;
        nrOfEditSeries = rE.size();

        
        
        Vector<TimeSeriesObject> rA = ng.accessReihen;
        if ( NodeGroup.useStockData ) {
            
            Vector<TimeSeriesObject> rASch = new Vector<TimeSeriesObject>();
            
            FinancialDataNodeGroup ngF = (FinancialDataNodeGroup)ng;
            rA = ng.stockDataReihen;
            
            if ( NodeGroup.doShuffle ) { 
                for( TimeSeriesObject m : rA ) { 
                    m.shuffleYValues();
                    rASch.add( m );
                }
                rA = rASch;
            }          
            
        }
        nrOfAccessSeries = rA.size();

        String ccResult = "";
        String ccLine = "";
        
        if ( editsAvailable && showOriginalEdits ) { 
             boolean legend = nrOfEditSeries < 5;
             String c = "";
             String folder = ng.pfad + "img/";
             
             // OLD-Version
             // String fn = langID + "_" + nrOfNodes + "_raw_edits" + NodeGroup.getStateExtension();
             String fn = ng.name + "_raw_edits" + NodeGroup.getStateExtension();
             data.put("raw_edits", fn+".png" );

             // MultiBarChart.open(rE, langID + " " + ng.editReihen.size() + " edit TS --> #edits(t)", "t", "#edits(t)", legend);
             // MultiBarChart.store(rE, langID + " " + ng.editReihen.size() + " edit TS --> #edits(t)", "t", "#edits(t)", legend , folder, fn , c);
             System.out.println( "### Created Edit-TS original Chart : [" + fn + "]" ); 
        }

        // Überschreibe die Kontrolle der Accessreihen, damit wird nicht abgebrichen
        // weil eine Reihe fehlt.
        //     accessAvailable = true;
        System.out.println("[Warning] >>> Ergebnis der PRÜFUNG der ACCESS Reihen : accessAvailable=" + accessAvailable );

        if ( accessAvailable && showOriginalAccess ) {
             boolean legend = rA.size() < 5;
             String c = "";
             String folder = ng.pfad + "img/";
             
             // String fn = langID + "_" + nrOfNodes +"_raw_access" + NodeGroup.getStateExtension();
             String fn = ng.name +"_raw_access" + NodeGroup.getStateExtension();


             data.put("raw_access", fn+".png" );

             // MultiBarChart.open(rA, langID + " " + ng.accessReihen.size() + " access TS --> #access(t)", "t", "#access(t)", legend);
             //MultiBarChart.store(rA, langID + " " + rA.size() + " access TS --> #access(t)", "t", "#access(t)", legend , folder, fn , c);
             System.out.println( "### Created Access-TS original Chart: [" + fn + "]" ); 
        };

        // System.out.println( ">>> puffer2 => " + ng.puffer2.size() );
        System.out.println( "\n>>> work on #nr_e=" + nrOfEditSeries + " and #nr_a=" + nrOfAccessSeries + " #Pairs: " + ( nrOfAccessSeries * nrOfAccessSeries ) );

        // wir belegen uns die Felder für die links-Properties ...
        double[][][] netWorkDataE = new double[nrOfEditSeries][nrOfEditSeries][3];
        double[][][] netWorkDataA = new double[nrOfAccessSeries][nrOfAccessSeries][3];


        // ------------------------------------------------------------------------------------------------------------------





        // run crosscorrelation on EDITS
        if ( doCC_on_EDITS ) {

            Vector<TimeSeriesObject> kksE = new Vector<TimeSeriesObject>();

            KreuzKorrelation._defaultK = 14;
            KreuzKorrelation.debug = false;

            TimeSeriesObject histMaxY = new TimeSeriesObject();
            histMaxY.setLabel( " edits Hist (tau)");

            TimeSeriesObject histSigLevel = new TimeSeriesObject();
            histSigLevel.setLabel( " edits Hist (strength)");

            Vector<String> keysOfPairs = new Vector<String>();
            // ggf wieder in die Schleife ...
            

            int c = 0;
            int w = 0;
            int max = ng.editReihen.size();
            for ( int i=0; i < max ; i++ ) {
                for ( int j=0; j < max ; j++ ) {


           
//                    String oldKey = j+"_"+i;
//                    if (  !(i==j) &&   // keine "Selbst-Referenz"
//                          !(keysOfPairs.contains(oldKey)) )  // keine Dopplungen
//                    {
//
//                        String realKey = i+"_"+j;
//
//                        keysOfPairs.add(realKey);
//                        
//
                        c++;
                        NodePair np = new NodePair();
                        np.id_A = i;
                        np.id_B = j;
                        
                        boolean v = false;
                        if ( ng.doWorkWithPair(np) ) {
                            w = w + 1;
                            try {
                                v = np._calcCrossCorrelation( ng, ng.editReihen, histMaxY, histSigLevel, NodePair.wrongPairsE );
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
                            if ( showKKEdits ) kksE.add( np.kk);
                            if ( logCrossCorrelations ) {
                                System.out.println(w +
                                        " {*} MatrixID:=" + c + " " + "\t" + ccLine );
                            }
                            bwEDITS.write( ccLine + "\n" );
                        }
//                    }
                }
            }

            System.out.println( ">>> wrong pairs :" + NodePair.wrongPairsE.size() );
            bwEDITS.write( "# wrong pairs :" + NodePair.wrongPairsE.size() + "\n" ) ;
            bwEDITS.flush();
            bwEDITS.close();

            if ( showKKEdits ) {
                String pfad = NodeGroup.pfad + "img/";
                String fn = ng.fn + "_E_CC";

                data.put("dat_E_CC", fn+".png");


                // 100 * 100 Reihen sind zu viele ...
                Vector<TimeSeriesObject> nr = new Vector<TimeSeriesObject>();
                int i = 0;
                for( TimeSeriesObject mr : kksE ) {
                    i++;
                    nr.add(mr);
                    if ( i > 1000 ) break;
                };
                // Show CC-Funktions ...
                // MultiChart.open(nr, langID + " Edits CC[k=+/-"+ KreuzKorrelation.defaultK +"] R(k)", "k", "R(k)", false);
                MultiChart.store(nr, langID + " Edits CC[k=+/-"+ KreuzKorrelation._defaultK +"] R(k)", "k", "R(k)", false, pfad, fn, "" );
                System.out.println( ">> Created Edit-TS CC-Functions Chart" ); 
            }

            if( showAVKKEdits ) {
                // Show Average CC Funktion
                String pfad = NodeGroup.pfad + "img/";
                String fn = ng.fn + "_E_AVG(CC)";

                data.put("dat_E_AVG_CC", fn+".png");

                TimeSeriesObject mr2 = TimeSeriesObject.calcAveragOfRows(kksE);
                Vector<TimeSeriesObject> mrv = new Vector<TimeSeriesObject>();
                mrv.add(mr2);
                // MultiChart.open(mrv, langID + " Edits <CC[k=+/-"+ KreuzKorrelation.defaultK +"]> <R(k)>", "k", "<R(k)>", false);
                MultiChart.store(mrv, langID + " Edits <CC[k=+/-"+ KreuzKorrelation._defaultK +"]> <R(k)>", "k", "<R(k)>", false, pfad, fn, "");
                System.out.println( ">> Created Edit-TS <CC-Functions> Chart" ); 
            }

            if ( showHistogramme ) {
                //      show histogram
                String pfad1 = createHistogramm( ng, histMaxY , 2 * KreuzKorrelation._defaultK + 1, -1 * KreuzKorrelation._defaultK-1, KreuzKorrelation._defaultK+1 );
                
                int hiXmin = 0;
                int hiXmax = 10;
                if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_CC_TAU_0  ||
                     CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED
                    ) {
                    hiXmin = -1;
                    hiXmax = 1;
                }

                if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_NORMALIZED ) {
                    String pfad2 = createHistogramm( ng, histSigLevel, 100, hiXmin, hiXmax );

                    data.put( "Edits_Hist_maxY", pfad1);
                    data.put( "Edits_Hist_sigLevel", pfad2);
                }
            }
        }
        // ---------------------------- ENDE CC Edits --------------------------

        // run crosscorrelation on ACCESS

        if ( doCC_on_ACCESS ) {
            Vector<TimeSeriesObject> kksA = new Vector<TimeSeriesObject>();

            KreuzKorrelation._defaultK = 14;
            KreuzKorrelation.debug = false;

            TimeSeriesObject histMaxYA = new TimeSeriesObject();
            histMaxYA.setLabel( " Access-rate Histogam (tau)");

            TimeSeriesObject histSigLevelA = new TimeSeriesObject();
            histSigLevelA.setLabel( " Access-rate Histogram (strength)");

            Vector<String> keysOfPairs = new Vector<String>();


            int logCounter = 0;
            int c =0;
            int w =0;
            int max = rA.size();
            for ( int i=0; i < max ; i++ ) {
                for ( int j=0; j < max ; j++ ) {
                    if (!(i==j)){

//
//                    String oldKey = j+"_"+i;
//                    if (  !(i==j) &&   // keine "Selbst-Referenz"
//                          !(keysOfPairs.contains(oldKey)) )  // keine Dopplungen
//                        {
//
//                            String realKey = i+"_"+j;
//                           
//                            keysOfPairs.add(realKey);
                            c++;
                            NodePair np = new NodePair();
                            np.id_A = i;
                            np.id_B = j;
                            np.pageIDA = i;
                            np.pageIDB = j;
                            
                            boolean v = false;
                            if ( ng.doWorkWithPair(np) ) {
                                // System.out.println( np );
                                w=w+1;
                                try {
                                    v = np._calcCrossCorrelation( ng, rA, histMaxYA, histSigLevelA, NodePair.wrongPairsA );
                                }
                                catch( Exception ex ) { 
                                    // ex.printStackTrace();
                                    // System.exit(-12);
                                    System.err.println( "!$%   " + ex.getMessage() );
                                    v = false;
                                }
                            }

                            if ( v ) {
                                ccResult = np.getResultOfCC();
                                ccLine = "[" + i + "," + j + "] \t " +  "[" + np.pageIDA + "," + np.pageIDB + "] \t "+ ccResult;

                                ccLine = np.appendAdvancdeResults( ccLine );

                                if ( showKKAccess )kksA.add( np.kk);
                                if ( logCrossCorrelations ) {
                                    if ( logCounter == 10000 ) {
                                        System.out.println( w +
                                            " {*} MatrixID:=" + c + " " + "\t" +
                                            ccResult);
                                        logCounter = 0;
                                    };
                                }
                                logCounter++;
                                bwACCESS.write( ccLine + "\n" );
                            }
//                        }
                    }
                }
            }
            System.out.println( ">>> wrong pairs :" + NodePair.wrongPairsA.size() );
            bwACCESS.write( "# wrong pairs :" + NodePair.wrongPairsA.size() + "\n" ) ;
            bwACCESS.flush();
            bwACCESS.close();

            if ( showKKAccess ) {
                String pfad = NodeGroup.pfad + "img/";
                String fn = ng.fn + "_A_CC";

                data.put("dat_A_CC", fn+".png");

                // 100 * 100 Reihen sind zu viele ...
                Vector<TimeSeriesObject> nr = new Vector<TimeSeriesObject>();
                int i = 0;
                for( TimeSeriesObject mr : kksA ) {
                    i++;
                    nr.add(mr);
                    if ( i > 1000 ) break;
                };
                // Show CC-Funktions ...
                // MultiChart.open(nr, langID + " Access CC[k=+/-"+ KreuzKorrelation.defaultK +"] R(k)", "k", "R(k)", false);
                MultiChart.store(nr, langID + " Access CC[k=+/-"+ KreuzKorrelation._defaultK +"] R(k)", "k", "R(k)", false, pfad, fn, "" );
                System.out.println( ">> Created Access-TS CC-Functions Chart" ); 
            }

            if( showAVKKAccess && CheckInfluenceOfSingelPeaks.mode != CheckInfluenceOfSingelPeaks.mode_ADVANCED ) {
                // Show Average CC Funktion
                String pfad = NodeGroup.pfad + "img/";
                String fn = ng.fn + "_A_AVG(CC)";

                data.put("dat_A_AVG_CC", fn+".png");

                TimeSeriesObject mr2 = TimeSeriesObject.calcAveragOfRows(kksA);
                Vector<TimeSeriesObject> mrv = new Vector<TimeSeriesObject>();
                mrv.add(mr2);
                // MultiChart.open(mrv, langID + " Access <CC[k=+/-"+ KreuzKorrelation.defaultK +"]> <R(k)>", "k", "<R(k)>", false);
                MultiChart.store(mrv, langID + " Access <CC[k=+/-"+ KreuzKorrelation._defaultK +"]> <R(k)>", "k", "<R(k)>", false, pfad, fn, "");
                System.out.println( ">> Created Access-TS <CC-Functions> Chart" ); 
            }

            


            if ( showHistogramme ) {
                //      show histogram

                if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_NORMALIZED ) {
                    String pfad1 = createHistogramm( ng, histMaxYA , 2 * KreuzKorrelation._defaultK + 1, -1 * KreuzKorrelation._defaultK-1, KreuzKorrelation._defaultK+1 );
                    data.put( "Access_Hist_maxY", pfad1);
                    System.out.println( ">> Created Histogramme " ); 
                }

                int hiXmin = 0;
                int hiXmax = 10;
                if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_CC_TAU_0 ||
                     CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED
                    ) {
                    hiXmin = -2;
                    hiXmax = 2;
                }

                String pfad2 = createHistogramm2( ng, histSigLevelA, 200, hiXmin,hiXmax );
                //String pfad2 = createHistogramm2( ng, histSigLevelA, 75, 0, 8 );
                data.put( "Access_Hist_sigLevel", pfad2);
                System.out.println( ">> Created Histogramme2 " ); 
                
            }
        }
        // ---------------------------- ENDE CC Access --------------------------

        data.put("label", ng.fn );

        String shuffelLabel = "notShuffled";
        if ( NodeGroup.doShuffle ) shuffelLabel = "shuffled";
        
        // create Report
        File fout = new File( NodeGroup.pfad + "img/index_" + ng.fn + "_CC_mode_" + CheckInfluenceOfSingelPeaks.mode + "." + shuffelLabel + ".html" );
        parameter.append("</ul></p>");
        data.put("parameter", parameter.toString());
        
        CCAnalyseReport report = new CCAnalyseReport();
        report.createReport(data, fout);
        System.out.println( ">> Created Report " ); 

        report.showInBrwoser();
        System.out.println( ">> Opend Report ... " ); 

        
        // report.packAsZIP();
        
        
        // show network, and properties



        return true;
    };


    static File singleFile = null;

    /**
     * afterwards, the propertie ng is defined or null
     */
    public void selectNodeGroup() {
        
        if ( singleFile == null ) {
         
            singleFile = NodeGroup.selectNodegroupFile();
            ng = new NodeGroup(singleFile);
            ng.setFull();
            ng._initWhatToUse();

        }
        //ng.checkForDoubleIds();
    }

    static TimeSeriesObject[] mrn = null;
    static TimeSeriesObject[] mrn2 = null;

    /**
     * Histogramm für TAU ...
     *
     * @param ng
     * @param mr
     * @param bins
     * @param min
     * @param max
     * @return
     */
    public static String createHistogramm( NodeGroup ng,TimeSeriesObject mr, int bins, int min, int max ) {
        if ( mrn == null ) mrn = new TimeSeriesObject[ NodeGroup.maxSplitIndex ];

        String pfad = ng.pfad + "img/";
        // String fp = ng.getLangID() + "_" + ng.ids.length + mr.getLabel().replaceAll(" ", "_" );
         
        String fp = ng.name + mr.getLabel().replaceAll(" ", "_" );
        String file = fp + NodeGroup.getStateExtension();

        //if ( NodeGroup.doSplitRows ) {
        if ( true ) {
        
            HaeufigkeitsZaehlerDouble z = new HaeufigkeitsZaehlerDouble();
            for ( Double v : (Vector<Double>)mr.yValues ) {
                z.addData( v );
            }
            z.min = min;
            z.max = max;
            z.intervalle = bins;
            z.calcWS();
            TimeSeriesObject mrr = z.getHistogram();
            mrr.setLabel( NodeGroup.splitIndex + "  " + fp );
            mrn[ NodeGroup.splitIndex ] = mrr;
            
            // if ( NodeGroup.splitIndex == NodeGroup.maxSplitIndex-1 ) {
                // MultiChart.open(mrn, file , "" , "tau| CC=Max(CC)" , true);
                MultiChart.store(mrn, file, "#", "CC(tau=0)", true, pfad, file, "");
            // }
        }
        else {

            HistogramChart demo = new HistogramChart( mr.getLabel()  );
            demo.addSerieWithBinning( mr, bins, min, max );
            demo.setContentPane( demo.createChartPanel() );
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            // demo.setVisible(true);
            demo.store( pfad, file );
        }

        
        return file + ".png";
    };

    /**
     * Histogramm für Strength ...
     *
     * @param ng
     * @param mr
     * @param bins
     * @param min
     * @param max
     * @return
     */
    public static String createHistogramm2( NodeGroup ng,TimeSeriesObject mr, int bins, int min, int max ) {
        if ( mrn2 == null ) mrn2 = new TimeSeriesObject[ NodeGroup.maxSplitIndex ];

        String pfad = ng.pfad + "img/";
        // String fp = ng.getLangID() + "_" + ng.ids.length + mr.getLabel().replaceAll(" ", "_" );
        String fp = ng.name + mr.getLabel().replaceAll(" ", "_" );
        
        String file = fp + NodeGroup.getStateExtension();

        //if ( NodeGroup.doSplitRows ) {
        if ( true ) {

            HaeufigkeitsZaehlerDouble z = new HaeufigkeitsZaehlerDouble();
            for ( Double v : (Vector<Double>)mr.yValues ) {
                z.addData( v );
            }
            z.min = min;
            z.max = max;
            z.intervalle = bins;
            z.calcWS();
            TimeSeriesObject mrr = z.getHistogram();
            mrr.setLabel( NodeGroup.splitIndex + "  " + fp );
            mrn2[ NodeGroup.splitIndex ] = mrr;

            String yLabel = "strength";
            if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_CC_TAU_0 )
                yLabel = "CC(tau=0)";
            if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED )
                yLabel = "strength_2";
            

            //if ( NodeGroup.splitIndex == NodeGroup.maxSplitIndex-1 ) {
                // MultiChart.open(mrn2, file , "" , yLabel , true);
                MultiChart.store(mrn2, file, "#", yLabel , true, pfad, file, "");
                System.out.println( "### Create Histogram : " + file );
            
                //}
        }
        else {

            HistogramChart demo = new HistogramChart( mr.getLabel()  );
            demo.addSerieWithBinning( mr, bins, min, max );
            demo.setContentPane( demo.createChartPanel() );
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            // demo.setVisible(true);
            demo.store( pfad, file );
        }


        return file + ".png";
    };


    void showTimeLog() {
        System.out.println( tl.toString() );
    }

    // aus der gesamteln Liste nur einzelne Übernehemn und einzelne Paare berechnen ...
    
    public void selectNodesOfTheGroup() {
        NodeIDSelection.ng = ng;
        NodeIDSelection.open();
    }

    void selectNodePairListe() throws IOException {
        File f = NodePairListViewer.selectNodePairListFile("NodePair-Datei auswählen ...");
        NodePairList liste2 = new NodePairList();
        liste2.read( f );
        
        NodeGroup partialGroup2 = new NodeGroup( liste2 );
        partialGroup2.setPartial();

//        partialGroup2.checkAccessTimeSeries();
//        partialGroup2.checkEditTimeSeries();
//
//        System.out.println( partialGroup2.ids.length + " nodes in file: " + partialGroup2.fn );
//        System.out.println( ">>> nr of edits pairs: " + partialGroup2.getNrOfPairsE() + "\n" );

        ng = partialGroup2;
    }

    void selectAllLinked32bit() throws IOException {
        AllLinked32bit g3 = new AllLinked32bit();
        g3._initWhatToUse();
        ng = g3;
    }
    
    
    String selectFromNewDatasets( int i , String referenceNG ) throws IOException {
    
        String refNG_file = "60_1000_most_active_by_access.dat";
        String refNG_fileA = "list_DAX.dat.ids.dat";
        String refNG_fileB = "list_sup500.dat.ids.dat";
        String refNG_fileC = "page_names_financial2.dat.ids.dat";
        String refNG_fileD = "test_ng.dat";
    
        String[] files = new String[5];
        files[0] = refNG_file;
        files[1] = refNG_fileA;
        files[2] = refNG_fileB;
        files[3] = refNG_fileC;
        files[4] = refNG_fileD;
        
        NodeGroup.pfad = referenceNG;
        
        NodeGroup ref = new NodeGroup( files[i] );
        ref.name = files[i];
        ref.load();
        ref._langID = "-1";
        ng = ref;
        
        return referenceNG + "\\" + files[i];
    }

    void selectAllLinked32bit_WITH_CONTEXT_Full() throws IOException {
        NodePairList liste = new NodePairList();
        liste.read( new File( NodeGroup.pfad + "filtered/32bit_VphMean.dat.ngLINKED_both.pairs.dat") );
        //NodeGroup partialGroup = new NodeGroup( liste.getPairs() );
        NodeGroup g3 = new NodeGroup( liste.getPairs() );
        g3.fn = liste.getNodeGroupsFn();
        g3.setFull();
        g3._initWhatToUse();
        ng = g3;
        this.isPreInited = true;
    }

    void selectAllLinked32bit_WITH_CONTEXT_Partial() throws IOException {
        NodePairList liste = new NodePairList();
        liste.read( new File( NodeGroup.pfad + "filtered/32bit_VphMean.dat.ngLINKED_both.pairs.dat") );
        //NodeGroup partialGroup = new NodeGroup( liste.getPairs() );
        NodeGroup g3 = new NodeGroup( liste.getPairs() );
        g3.fn = liste.getNodeGroupsFn();
        g3.setPartial();
        g3._initWhatToUse();
        ng = g3;
        this.isPreInited = true;
    }

    void selectNodeGroupRandomly() {
        try {
            ng = new RandomNodesGroup( 300 );
            ng._initWhatToUse();
//            ng.checkAccessTimeSeries();
//            ng.checkEditTimeSeries();

        }
        catch (IOException ex) {
            Logger.getLogger(CCCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    Hashtable<String,NodeGroup> groups = new Hashtable<String,NodeGroup>();
    String selectRandomGeneratedNG_withPeaks(String label, int i) {
        
        NodeGroup _ng = (NodeGroup)groups.get(label);

        if (ng == null ) {
                
            try {
                
                _ng = new PeakedRandomNodeGroup( i );
                _ng._initWhatToUse();
                _ng.fn = label+ ".ng.dat";
                _ng.name = label;
                groups.put(label, _ng);
                
//            ng.checkAccessTimeSeries();
//            ng.checkEditTimeSeries();
            }
            catch (IOException ex) {
                Logger.getLogger(CCCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ng = _ng;
        if ( NodeGroup.doShuffle ) { 
             ng.doShuffleAllNow();
        }
        return "random_" + i;
    }

    
}
