/*
 * The tool loads time series from the Web, puts it into HBase, collects the 
 * time series per group in memory and creates time series buckets per study, 
 * group and time-range as sequence file, which is ready for further processing. 
 */
package m3.wikipedia.corpus.extractor.edits;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.hadoopts.core.TSBucket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.crunchts.store.TSB;
import thesis.apps.tools.SigmaBandTool;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.getMrFromCache;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.pages;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.von;
import m3.wikipedia.explorer.data.WikiNode;
import ws.cache3.HBaseCacheService;
import ws.cache3.TSCacheV3;
 

/**
 *
 * @author kamir
 */
public class EditTimeSeriesCollectorTool extends WikiHistoryExtractionBASE {

    /**
     * To read data from TSCache, we need a proxy.
     */
    static TSCacheV3 proxy = null;
    
    static long startTime = 0;
   
    /**
     * This switch controls if we ignore missing rows, or if a lazy loading
     * procedure should be applied. This works only in online mode because
     * Wikipedia-API calls are send to retrieve live data.
     * 
     * Especially in "Ah-Hoc" analysis procedures, this is useful.
     */
    static boolean doLOAD = true;
    
    private static void initCollectAndCacheMode(boolean deb) {
        
        // Debugging on / off
        m3.tscache.TSCache.debug = deb;
        
        // Use the HBase cache
        m3.tscache.TSCache.WebHBase = true;
        
        // do not use the in-VM-RAM cache
        m3.tscache.TSCache.RAM = false;
        
        // do not load local dump-files
        loadFromLocalCACHE = false;
        loadSPLITS = false;

        // do not create local dump-files
        storeToLocalCACHE = false;
        storeToLocal_SPLIT_PER_CN = false;
        
        // load rows from the web
        doLOAD = true;
        
    }
    
//    private static void initLoadDumpAndCreateProfile(boolean deb) {
//        
//        // Debugging on / off
//        tscache.TSCache.debug = deb;
//        
//        // Use the HBase cache
//        tscache.TSCache.WebHBase = true;
//        
//        // load data first from local dump-files
//        loadFromLocalCACHE = true;
//        loadSPLITS = true;
//
//        // Also use the in-VM-RAM cache
//        tscache.TSCache.RAM = true;
//
//        // do not create local dump-files, as they already exist
//        storeToLocalCACHE = false;
//        storeToLocal_SPLIT_PER_CN = false;
//        
//        // do not load rows from the web
//        doLOAD = false;
//    }
    
//    private static void initCollectCacheAndDump(boolean deb) {
//        
//        // Debugging on / off
//        tscache.TSCache.debug = deb;
//        
//        // Use the HBase cache
//        tscache.TSCache.WebHBase = true;
//        
//        // do not use the in-VM-RAM cache
//        tscache.TSCache.RAM = true;
//        
//        // do not load local dump-files
//        loadFromLocalCACHE = true;
//        loadSPLITS = true;
//
//        // do not create local dump-files
//        storeToLocalCACHE = false;
//        storeToLocal_SPLIT_PER_CN = false;
//        
//        // load rows from the web
//        doLOAD = false;
//    }
    
    static int totalCounter = 0;
    static long started = -1;
    
    
    static public void countDownloads() {
        
        if ( started == -1 ) {
            started = System.currentTimeMillis();
            return;
        }
        
        totalCounter++;
        
        if ( totalCounter == 100 ) { 
            long now = System.currentTimeMillis();
            double duration = (double)(now - started) / 1000.0;
           
            
//javax.swing.JOptionPane.showMessageDialog( new JFrame(), "100 rows : " + duration + " s.");

            
            System.out.println( "##### 100 rows : " + duration + " s.");
            
            totalCounter = 0;
            
            started = now;
        }
    
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        
        /**
         * Set an operational mode ..
         */
        initCollectAndCacheMode( true );
        
        // initLoadDumpAndCreateProfile( false );
        
        // initCollectCacheAndDump( true );
        
        showChart = false;
        
        startTime = System.currentTimeMillis();

        // --- END Setup ---
        
        String path1 = ".";
        String path2 = ".";

        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();
        
        //File ff = wd.selectFile( "/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/WikiPaper_und_BA_2012");
        
//        File ff = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/WikiPaper_und_BA_2012/WikiPaper_und_BA_2012.xml");
//        File ff2 = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/Characterisierung_Der_Methode/ALOCAL12Concepts4test.xml");
//        File ff3 = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/Characterisierung_Der_Methode/AGLOBAL12Concepts4test.xml");
//        File ff4 = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/Characterisierung_Der_Methode/Characterisierung_Der_Methode_FINAL.xml");
//        File f5 = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/FINAL_THESIS/finance_wiki_analysis_thesis_final.xml");

        File f6 = new File("/Users/kamir/DEMO/ETOSHA/dissertation_DEMO.xml");
//        File f6 = new File("./dissertation_DEMO.xml");
        
//        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
//        jfc.setCurrentDirectory( f6.getParentFile() );
//        
//        int returnVal = jfc.showOpenDialog( new JFrame() );
//        f6 = jfc.getSelectedFile();
        
//        File f6 = new File("/Users/kamir/DEMO/ETOSHA/tutorial_one.xml");
        
        System.out.println( f6.getAbsolutePath() +" => exists:" + f6.exists() );
        
        wd = wd.load(f6);

        // NAME extrahieren und Liste LADEN
        File f = wd.getRealPathToProjectFile(f6);
        
        System.out.println( f.getAbsolutePath() );
        
        HBaseCacheService service = new HBaseCacheService();
        proxy = service.getTSCacheV3Port();

        von = new GregorianCalendar();
        von.clear();
        von.set(2010, 0, 1, 0, 0);

        java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
        bis.clear();
        bis.set(2010, 0, 1, 0, 0);
        bis.add(GregorianCalendar.DAY_OF_YEAR, 364);
 
        WikiHistoryExtraction2.von = von;
        WikiHistoryExtraction2.bis = bis;

        WikiHistoryExtractionBASE.von = von;
        WikiHistoryExtractionBASE.bis = bis;
      
        System.out.println( "* Time Range:" );
        System.out.println( "* ============" );
        System.out.println( "*  von: " + von.getTime().toString() );
        System.out.println( "*  bis: " + bis.getTime().toString() );

        processStudie(wd, von, bis, f);
        
        logStats();

        System.out.println("> Done. (Close the charts to finish the program!)" );

        if ( !showChart ) System.exit(0);
        
        System.out.println("> Done." );
    }

    public static void processStudie(WikiStudieMetaData wd, Calendar von, Calendar bis, File f) throws FileNotFoundException, ClassNotFoundException, IOException, FailedLoginException, Exception {
        /**
         * Populate the collections ...
         */
//        Vector<Messreihe> mrCN = new Vector<Messreihe>();
//        Vector<Messreihe> mrAL = new Vector<Messreihe>();
        Vector<Messreihe> mrBL = new Vector<Messreihe>();
//        Vector<Messreihe> mrIWL = new Vector<Messreihe>();
           
        long t0 = System.currentTimeMillis();
 
        if (von == null) {
            System.exit(-1);
        }
        if (bis == null) {
            System.exit(-1);
        }

        if (f.exists()) {

            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());

            if (loadFromLocalCACHE) {
                if (loadSPLITS) {
                    _loadLocalCacheDumpSPLITS(wd.path2, wd);
                } else {
                    _loadLocalCacheDump(wd.path2, wd);
                }
            }
            resetCounters();

            System.out.println(">>> Info: \n" + wd.description);

//            final Vector<WikiNode> wnCN = wd.getWn();
//            
//            System.out.println(">>> Nr of CN-nodes : " + wnCN.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                
//                run(wnCN, von, bis, true);
//                mrCN = populateCollections( "CN" );
//                
//                dumpTSCollectionAndSimpleProfile( mrCN, 1, "CN" , wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrCN, 2, "CN" , wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrCN, 3, "CN" , wd.getName(), false ) ;
//                dumpTSCollectionAndSimpleProfile( mrCN, 4, "CN" , wd.getName(), false );
//            
////                mrCN = null;   
//            }


//            final Vector<WikiNode> wnIWL = wd.getIWL();
//            System.out.println(">>> Nr of IWL-nodes : " + wnIWL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } else {
//
//                run(wnIWL, von, bis, true);
//                
//                mrIWL = populateCollections( "IWL" );
//                
//                dumpTSCollectionAndSimpleProfile( mrIWL, 1, "IWL", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrIWL, 2, "IWL", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrIWL, 3, "IWL", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrIWL, 4, "IWL", wd.getName(), false );
//                     
////                mrIWL = null;
//            }


//            final Vector<WikiNode> wnAL = wd.getAL();
//            System.out.println(">>> Nr of AL-nodes : " + wnAL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnAL, von, bis, true);
//                
//                mrAL = populateCollections( "A.L" );
//                
//                dumpTSCollectionAndSimpleProfile( mrAL, 1, "A.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrAL, 2, "A.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrAL, 3, "A.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrAL, 4, "A.L", wd.getName(), false );
//            
//                 
//            }

            final Vector<WikiNode> wnBL = wd.getBL();
            
            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                
            run(wnBL, von, bis, true);
                
//                mrBL = populateCollections( "B.L" );
                
                // dumpTSCollectionAndSimpleProfile( mrBL, 1, "B.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrBL, 2, "B.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrBL, 3, "B.L", wd.getName(), false );
//                dumpTSCollectionAndSimpleProfile( mrBL, 4, "B.L", wd.getName(), false );
            }

        } 
        else {
            System.out.println(">>> " + f.getAbsolutePath() + " not available.");
        }

        if (showChart) {
//            MultiBarChart.open( mrCN, "CN Event-Zeitreihen", "t", "nr", true); 
//            MultiBarChart.open( mrIWL, "IWL Event-Zeitreihen", "t", "nr", false); 
//            MultiBarChart.open( mrAL, "AL Event-Zeitreihen", "t", "nr", false); 
//            MultiBarChart.open( mrBL, "BL Event-Zeitreihen", "t", "nr", false); 
        }

        /**
         *
         */
        if (storeToLocalCACHE) {
            
            System.out.println(">>> STORE data in local cache.");

            if (storeToLocal_SPLIT_PER_CN) {
                System.out.println(">>> STORE data one split per CN.");

                int max = wd.getWn().size();
                // für jede CN eine eigene Hashtable anlegen
                for (int i = 0; i < max; i++) {
                    int CN = i + 1;
                    _storeLocalCacheDump_SPLIT_PER_CN(wd.path2, wd.selectedName, CN, wd);
                }
            } else {
                storeLocalCacheDump(wd.path2, wd.selectedName);
            }
        }

        wd.logExtraction(von, bis);

        System.out.println(">>> " + wd.name);
        System.out.println(">>> INFO : \n" + wd.getDescription());

        System.out.println(">>> von : " + von.getTime());
        System.out.println(">>> bis : " + bis.getTime());

        System.out.println(">>> Done.");

        long t1 = System.currentTimeMillis();

        System.out.println(">>> " + new Date(t1 - t0));

    }

    /**
     *
     * Aufruf als Tool ....
     *
     * @param nodes
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception
     */
    public static void run(Vector<WikiNode> n, Calendar v, Calendar b, boolean showCh) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        WikiNode[] nodes = new WikiNode[n.size()];
        int i = 0;
        for (WikiNode nnn : n) {
            nodes[i] = nnn;
            i++;
            System.out.println("(" + i + "): \t " + nnn);
        }
        run(nodes, v, b, showCh);
    }

    public static void run(WikiNode[] cnNodes, Calendar v, Calendar b, boolean showCh) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        ArrayList<Integer> list = new ArrayList<Integer>();

        // work with all
        for (int i = 0; i < cnNodes.length; i++) {
            list.add(i);
        }

        Collections.shuffle(list);

        pages = new String[cnNodes.length];
        wikis = new String[cnNodes.length];

        von = v;
        bis = b;
        int i = 0;
        for (WikiNode wn : cnNodes) {
            int id = list.get(i);
            pages[id] = wn.page;
            wikis[id] = wn.wiki;
            i++;
        }


        work(null);
    }


    public static void work(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        System.out.println(">>> WORK on " + pages.length + " pages.");
        System.out.println(">>> MISSING pages are loaded from Wikipedia: " + doLOAD );
        System.out.println(">>> USE TSCache: " + doLOAD );
        
        int i = 0;

        for (String pn : pages) {
            String w = wikis[i];
            testPageHistory(w, pn);
            i++;
            if (i % 1000 == 0) {
                logStats();
            }
        }
    }
    
    static void resetCounters() {
       MR_AVAILABLE = 0;
       MR_MISSING = 0;
       total = 0;
    }
    
    public static int MR_AVAILABLE = 0;
    public static int MR_MISSING = 0;
    public static int total = 0;

    
    /**
     * 
     * Retrieve a singel time series for a single WikiNode.
     * 
     * @param wikipedia
     * @param pn
     * @return
     * @throws IOException
     * @throws Exception 
     */
    public static Messreihe testPageHistory(String wikipedia, String pn) throws IOException, Exception {

        WikiNode wn = new WikiNode(wikipedia, pn);
        
        Messreihe m = getMrFromCache(wn);
        
        if (m != null) {
            MR_AVAILABLE++;
        } 
        else {
            MR_MISSING++;
            if (doLOAD) {
                System.out.println(">>> Load from web now ... ");
                m = loadPageHistory(wn);
                countDownloads();
                            }
            else {
                
                long now = System.currentTimeMillis();
                double duration = (double)(now - startTime) / 1000.0;
                
                System.out.println( "\n\n" + duration + " s *** total=" + total + " ***> MR_MISSING=" + MR_MISSING + " >> MR_AVAILABLE=" + MR_AVAILABLE );

                System.out.println("\n\n>>> LAZY LAOD FROM WEB IS TURNED OFF!");
            }
        }

        total++;
        return m;
    }

    public static void logStats() {
        Date d = new Date(System.currentTimeMillis());

        System.out.println("*** TIME               : " + d);

        System.out.println("   #available         : " + MR_AVAILABLE);
        System.out.println("   #missing           : " + MR_MISSING);

        System.out.println("   #ratio: available :: " + (double) (MR_AVAILABLE / total));
        System.out.println("   #ratio: missing   :: " + (double) (MR_MISSING / total));
    }

    /**
     * A collection is a group of time series for which an analysis is
     * done. The local neighorhood consists of 4 groups.
     * A study can have many many central nodes. So we have
     * 
     *     nr_of_collections = nr_of_CN * 4
     * 
     * @param gr
     * @return 
     */
    private static Vector<Messreihe> populateCollections(String gr) {

        long t1 = System.currentTimeMillis();
        
        Vector<Messreihe> vmr = new Vector<Messreihe>();
        
        int ERROR = 0;
        int i = 0;
        for (String pn : pages) {
            String w = wikis[i];
            try {
                
                Messreihe mr = testPageHistory(w, pn);
                
                if ( mr != null ) {
                    
                    mr.setGroupKey( mr.getGroupKey() + gr );
                    
                    vmr.add( mr );
                    // System.out.println( "validate Identifier: " + mr.getIdentifier() );
                }    
                else {
                    ERROR++;
                }
            }
            catch(Exception ex ) {
            
            }
            i++;
        }
        
        long t2 = System.currentTimeMillis();

        javax.swing.JOptionPane.showMessageDialog(new JFrame(), "Load time: ("+gr+")  " + (t2-t1) + " : " + "ROWS: " + vmr.size() + " ERRORS: " + ERROR );
        
        System.out.println("ROWS: " + vmr.size() + " ERRORS: " + ERROR );
        return vmr;
    
    }

    /**
     * Here we export the SequenceFile for one well defined TS-Group into
     * a TSBucket.
     * 
     *    Key:     Text
     *    Value:   VectorWritable
     * 
     * @param mrv
     * @param i
     * @param typ
     * @param studie 
     */
    private static void dumpTSSeriesGroupToBucketSequenceFile(Vector<Messreihe> mrv, int i, String typ, String studie){

        String groupLabel = studie + "_" + i + "_" + typ + ".editrate" ;

        int counter = 0;
        double sum = 0;
        int j = 0;
        
        
        File f = TSB.getAspectFolder( "editrates" , studie, groupLabel, ".tsb.seq"  );

        TSBucket tsb = TSBucket.createEmptyBucket();
 
        tsb._open( f ); 
        
        for (Messreihe mr : mrv) {
            
            /**
             * TODO: sometimes we use identifier and sometimes label !!!
             * 
             * Modify the time series label.
             * 
             * Add the GROUP KEY to the mr.label
             */

            mr.setLabel( groupLabel + "_" + mr.getLabel() );
            tsb.putMessreihe( mr );
           
            
            if (mr != null) { 

                counter++;
               sum = sum + mr.summeY();
            }
            // loop counter
            j++;
        }
        
        tsb.close();
        
        System.out.println( ">>> TSBucket generated: " +  f.getAbsolutePath() );
        System.out.println( ">>> The Bucket containt Wikipedia Edits Counts : (av=" +  (sum / (double)counter) + ") (sum="+ counter +")");
    };
    
    /**
     * The group statistic is calculated for a TSBucket. Time Series tables are
     * stored as TSV files and chart-sketches are plotted.
     * 
     * Then, the TSBucket is stored as SequenceFile.
     * 
     * @param mrv
     * @param i
     * @param typ
     * @param studie
     * @param detrendet 
     */
    private static void dumpTSCollectionAndSimpleProfile(Vector<Messreihe> mrv, int i, String typ, String studie, boolean detrendet) {
 
        String groupLabel = studie + "_" + i + "_" + typ + ".editrate";
        String k = ""+i;
        Vector<Messreihe> _mrv = new Vector<Messreihe>();
        
        for( Messreihe mr : mrv) {
            
            String gk = mr.getGroupKey();

            if ( gk.startsWith( k ) ) {
                _mrv.add(mr);
            }
        }
        
        if( _mrv.size() > 0 ) {
            
            SigmaBandTool sbt = new SigmaBandTool();

            sbt.exportFolder = TSB.getAspectFolder("descstat", studie ).getAbsolutePath();
            
            sbt.addCollect( _mrv, false );

            sbt.aggregate();
            sbt.plotAndStore( groupLabel );

            if( detrendet )
                sbt._plotAndStoreTrends( groupLabel );

            dumpTSSeriesGroupToBucketSequenceFile( _mrv, i, typ, studie );
            
        }
        else {
            System.out.println( ">>> Collection " + groupLabel + " : " + i + " empty ... " );
        }
    }
}
