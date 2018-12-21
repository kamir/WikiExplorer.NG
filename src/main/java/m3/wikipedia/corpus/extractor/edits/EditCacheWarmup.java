/*
 * Prüft nur, ob Daten in HBase gespeichert und abgerufen werden können. 
 */
package m3.wikipedia.corpus.extractor.edits;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.data.series.MRT;
import org.apache.hadoopts.data.series.TimeSeriesObject;
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
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;
import static m3.wikipedia.corpus.extractor.edits.EditTimeSeriesCollectorTool.testPageHistory;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2.processStudie;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.getMrFromCache;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.pages;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.run;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.von;
import m3.wikipedia.explorer.data.WikiNode;
import ws.cache3.HBaseCacheService;
import ws.cache3.TSCacheV3;

/**
 *
 * @author kamir
 */
public class EditCacheWarmup extends WikiHistoryExtractionBASE {

    
   
    static TSCacheV3 proxy = null;
            
    /**
     * @param args the command line arguments
     */
    public static void loadDataForStudieInRAM() throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        m3.tscache.TSCache.RAM = true;
        m3.tscache.TSCache.WebHBase = false;
        
        
        String path1 = ".";
        String path2 = ".";

        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();
        
        File fStudie = new File("/Users/kamir/DEMO/ETOSHA/dissertation_DEMO.xml");
        
        wd = wd.load(fStudie);

        // NAME extrahieren und Liste LADEN
        File f = wd.getRealPathToProjectFile(fStudie);
        
        System.out.println( f.getAbsolutePath() );
        
        HBaseCacheService service = new HBaseCacheService();
        proxy = service.getTSCacheV3Port();

        von = new java.util.GregorianCalendar();
        von.clear();
        von.set(2010, 0, 1, 0, 0);

        java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
        bis.clear();
        bis.set(2010, 0, 1, 0, 0);
//      processStudie(wd, von, bis, f);
        bis.add(GregorianCalendar.DAY_OF_YEAR, 364);
        
        WikiHistoryExtraction2.von = von;
        WikiHistoryExtraction2.bis = bis;
        
      
        System.out.println( "* Time Range:" );
        System.out.println( "* ============" );
        System.out.println( "*  von: " + von.getTime().toString() );
        System.out.println( "*  bis: " + bis.getTime().toString() );

        processStudie(wd, von, bis, f);

        logStats();

        System.out.println("Cache WARMUP is done.");
//        System.exit(0);
    }

    public static void processStudie(WikiStudieMetaData wd, Calendar von, Calendar bis, File f) throws FileNotFoundException, ClassNotFoundException, IOException, FailedLoginException, Exception {
        /**
         * Populate the collections ...
         * 
         */
        Vector<TimeSeriesObject> mrCN = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> mrAL = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> mrBL = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> mrIWL = new Vector<TimeSeriesObject>();
        
        
        long t0 = System.currentTimeMillis();

//        boolean operate_LOCALY = false;
//        boolean operate_DEV = false;

        if (von == null) {
            System.exit(-1);
        }
        if (bis == null) {
            System.exit(-1);
        }

        showChart = true;

        loadFromLocalCACHE = true;
        loadSPLITS = true;

        storeToLocalCACHE = false;
        storeToLocal_SPLIT_PER_CN = false;

        if (f.exists()) {

            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());

            if (loadFromLocalCACHE) {
                if (loadSPLITS) {
                    _loadLocalCacheDumpSPLITS(wd.path2, wd);
                } else {
                    _loadLocalCacheDump(wd.path2, wd);
                }
            }

            System.out.println(">>> Info: \n" + wd.description);

            final Vector<WikiNode> wnCN = wd.getWn();
            
            System.out.println(">>> Nr of CN-nodes : " + wnCN.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } 
            else {
                
                run(wnCN, von, bis, true);
                mrCN = populateCollections( wnCN );
//                logStats();
                
            }


            final Vector<WikiNode> wnIWL = wd.getIWL();
            System.out.println(">>> Nr of IWL-nodes : " + wnIWL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                run(wnIWL, von, bis, true);
                mrIWL = populateCollections( wnIWL );
//                logStats();
            }


//            final Vector<WikiNode> wnAL = wd.getAL();
//            System.out.println(">>> Nr of AL-nodes : " + wnAL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnAL, von, bis, true);
//                logStats();
//            }

//            final Vector<WikiNode> wnBL = wd.getBL();
//            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } else {
//                run(wnBL, von, bis, true);
//                logStats();
//            }

        } 
        else {
            System.out.println(">>> " + f.getAbsolutePath() + " not available.");
        }

        if (showChart) {
            MultiBarChart.open( mrCN, "CN Event-Zeitreihen", "t", "nr", true); 
            MultiBarChart.open( mrIWL, "IWL Event-Zeitreihen", "t", "nr", false); 
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

    /**
     * If a dynamic lazyloading is required, we set this flag to true.
     */
    public static boolean doLoadLazyFromWIKIPEDIA = false;

    public static void work(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        System.out.println(">>> WORK on " + pages.length + " pages.");
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
    
    public static int MR_AVAILABLE = 0;
    public static int MR_MISSING = 0;
    public static int total = 0;

    public static TimeSeriesObject testPageHistory(String wikipedia, String pn) throws IOException, Exception {

        WikiNode wn = new WikiNode(wikipedia, pn);
        
        TimeSeriesObject m = getMrFromCache(wn);
        if (m != null) {
            MR_AVAILABLE++;
        } else {
            MR_MISSING++;
            if (doLoadLazyFromWIKIPEDIA) {
                
                m = loadPageHistory(wn);
                
            }
        }

        total++;
        return m;
    }

    public static void logStats() {
        Date d = new Date(System.currentTimeMillis());

        System.out.println("***TIME               : " + d);

        System.out.println("   #available         : " + MR_AVAILABLE);
        System.out.println("   #missing           : " + MR_MISSING);

        System.out.println("   #ratio: available :: " + (double) (MR_AVAILABLE / total));
        System.out.println("   #ratio: missing   :: " + (double) (MR_MISSING / total));
    }

    public static Vector<TimeSeriesObject> populateCollections(Vector<WikiNode> wnCN) {

        Vector<TimeSeriesObject> vmr = new Vector<TimeSeriesObject>();
        
        int ERROR = 0;
        int i = 0;
        for (String pn : pages) {
            String w = wikis[i];
            try {
                
                TimeSeriesObject mr = testPageHistory(w, pn);
                if ( mr != null ) {
                    vmr.add( mr );
                    System.out.println( "validate Identifier: " + mr.getIdentifier() );
                }    
                else {
                    ERROR++;
                }
            }
            catch(Exception ex ) {
            
            }
            i++;
             
        }
        
        System.out.println(wnCN.size() + " ERRORS: " + ERROR );
        return vmr;
    
    }
}
