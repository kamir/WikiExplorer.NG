/*
 * The tool loads time series from the Web, puts it into HBase, collects the 
 * time series per group in memory and creates time series buckets per study, 
 * group and time-range as sequence file, which is ready for further processing. 
 */
package m3.wikipedia.corpus.extractor.edits;
 
import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
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
import org.etosha.tsstore.HBasePrefixStreamLoader;
import org.crunchts.store.TSB;
import m3.research.SigmaBandTool;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.getMrFromCache;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.pages;
import static m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE.von;
import m3.wikipedia.explorer.data.WikiNode;

/**
 * 
 *     @author kamir
 * 
 */
public class EditTimeSeriesBucketStreamerTool extends WikiHistoryExtractionBASE {
    
   
    
    static long startTime = 0;
    
    private static void initCollectAndCacheMode(boolean deb) {
        
        /**
         * We load all from HBase, if not in, we use simply
         * the web-download.
         */
        // Debugging on / off
        m3.tscache.TSCache.debug = deb;
        
        // Use the HBase cache
        m3.tscache.TSCache.WebHBase = true;
        
        // do not load local dump-files
        loadFromLocalCACHE = false;
        loadSPLITS = false;

        // do not create local dump-files
        storeToLocalCACHE = true;
        storeToLocal_SPLIT_PER_CN = false;
        
 
        
    }
    
    static int totalCounter = 0;
    static long started = -1;
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
        /**
         * Set an operational mode ..
         */
        initCollectAndCacheMode( true );
        
        startTime = System.currentTimeMillis();

        // --- END Setup ---
        
        String path1 = ".";
        String path2 = ".";

        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();

        File f6 = new File("/Users/kamir/DEMO/ETOSHA/dissertation_DEMO.xml");
        
        System.out.println( f6.getAbsolutePath() +" => exists:" + f6.exists() );
        
        wd = wd.load(f6);

        // NAME extrahieren und Liste LADEN
        File f = wd.getRealPathToProjectFile(f6);
        System.out.println( f.getAbsolutePath() );
 
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

        System.out.println("> Done." );
    }

    public static void processStudie(WikiStudieMetaData wd, Calendar von, Calendar bis, File f) throws FileNotFoundException, ClassNotFoundException, IOException, FailedLoginException, Exception {
        
//        Vector<TimeSeriesObject> mrCN = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> mrAL = new Vector<TimeSeriesObject>();
        Vector<TimeSeriesObject> mrBL = new Vector<TimeSeriesObject>();
//        Vector<TimeSeriesObject> mrIWL = new Vector<TimeSeriesObject>();
           
        long t0 = System.currentTimeMillis();
 
        if (von == null) {
            System.exit(-1);
        }
        if (bis == null) {
            System.exit(-1);
        }

        if (f.exists()) {

            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());
            
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

            int nr = 2;
            
            final Vector<WikiNode> wnBL = wd.getBL( nr );
            
            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());
            
            // here we know all the names of the B.L nodes ...

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } else {
                
            mrBL = forkBucketFromTable( "B.L", nr, wnBL, von, bis );
                
//                dumpTSCollectionAndSimpleProfile( mrBL, 1, "B.L", wd.getName(), false );
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
//        if (storeToLocalCACHE) {
//            
//            System.out.println(">>> STORE data in local cache.");
//
//            if (storeToLocal_SPLIT_PER_CN) {
//                System.out.println(">>> STORE data one split per CN.");
//
//                int max = wd.getWn().size();
//                // für jede CN eine eigene Hashtable anlegen
//                for (int i = 0; i < max; i++) {
//                    int CN = i + 1;
//                    _storeLocalCacheDump_SPLIT_PER_CN(wd.path2, wd.selectedName, CN, wd);
//                }
//            } else {
//                storeLocalCacheDump(wd.path2, wd.selectedName);
//            }
//        }

        wd.logExtraction(von, bis);

        System.out.println(">>> " + wd.name);
        System.out.println(">>> INFO : \n" + wd.getDescription());

        System.out.println(">>> von : " + von.getTime());
        System.out.println(">>> bis : " + bis.getTime());

        System.out.println(">>> Done.");

        long t1 = System.currentTimeMillis();

        System.out.println(">>> " + new Date(t1 - t0));

    }

    
    static StringBuffer sbFails = new StringBuffer();
 

    private static Vector<TimeSeriesObject> forkBucketFromTable(
            String grouplabel,
            int CNid,
            Vector<WikiNode> nodes, 
            Calendar von, 
            Calendar bis) throws IOException {
        
        Vector<TimeSeriesObject> v = new Vector<TimeSeriesObject>();
        
        // Expected KEYs are created here...
        Vector<String> nodeS = new Vector<String>();
        for( WikiNode wn: nodes ) {
            String name = wn.getKey_TIME_DEPENDENT(von, bis);
            System.out.println( "*** " + name );
            nodeS.add(name);
        }
 
        String prefix = von.getTime().getTime() + "___" + bis.getTime().getTime();
        System.out.println( "PREFIX:" + prefix );
        
        File f = TSB.getAspectFolder( "editrates" , "dissertation_DEMO", CNid + "." + grouplabel + ".xt-FULL" , ".tsb.seq"  );
        
        TSBucket tsb = TSBucket.createEmptyBucket();
        tsb._open( f ); 

        HBasePrefixStreamLoader.forkStream( tsb, CNid, grouplabel, nodeS, prefix );
        
        tsb.close();
        System.out.println("*** Bucket file : " + f.getAbsolutePath() );
        return v;    
    }

    
       
   
}
