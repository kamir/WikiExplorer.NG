package m3.tscache;

import org.apache.hadoopts.data.series.MRT;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import m3.io.WikiNodeCacheEntry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE;
import m3.wikipedia.explorer.data.WikiNode;

import ws.cache3.*;

public class TSCache {

    // lädt lokal oder aus HBase ...
    static public boolean RAM = false;
    // lädt aus dem Netz und legt im HBase ab ...
    static public boolean WebHBase = true;
//    public static void setModeFROMCache() { 
//        RAM = true;
//        WebHBase = false;
//    }
    static TSCache tsc = null;

    public static TSCache getTSCache() {
        if (tsc == null) {
            tsc = new TSCache();
            tscConnects();
        }
        return tsc;
    }
    
    static TSCacheV3 proxy = null;

    private static void tscConnects() {

        if (WebHBase) {
            System.out.println(">>> Connect to TSCache DB ... ");
            try {
                if (proxy == null) {
                    HBaseCacheService service = new HBaseCacheService();
                    proxy = service.getTSCacheV3Port();
                    System.out.println(">>> " + proxy.init());
                }
            } catch (Exception ex) {
                Logger.getLogger(TSCache.class.getName()).log(Level.SEVERE, null, ex);
            }

        }


        if (RAM) {
            System.out.println(">>> chache in RAM ... ");
            if (c == null) {
                c = new Hashtable<String, TimeSeriesObject>();
            }
        }

    }
    
    static public boolean debug = true;
    
    private static Hashtable<String, TimeSeriesObject> c = new Hashtable<String, TimeSeriesObject>();

    public TimeSeriesObject getMrFromCache(WikiNode wikiNode) throws IOException {

        String key = getKey(wikiNode, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());

//        System.out.println("\n[KEY] " + key );
//        System.out.println("      RAM   :" + RAM  );
//        System.out.println("      HBASE :" + WebHBase  );

        if (RAM) {
            TimeSeriesObject mr = c.get(key);
            if (mr != null) {
                System.out.println("[RAM]");
                return mr;
            }
        }

        if (proxy == null) {
            TSCache.tscConnects();
        }

        if (WebHBase) {

            if (debug ) System.out.println( ">> WS-Proxy: " + proxy + " KEY:"+key );

            String value = proxy.get(key);
            
            TimeSeriesObject mr = null;


            if (value != null) {
                if (!value.equals("NULL")) {
                    
                    mr = MRT.deserializeFromXMLString(value);
                    
                    if (debug ) System.out.println( "*   KEY:"+key + " => (VALUE != NULL):" + (value != null) );
                }
                else {
                    if (debug ) System.out.println( "**   KEY:"+key + " : _NULL_" );
                }
            }
            
            if (mr != null ) {
                c.put(key, mr);
                System.out.println( mr.summeY() );
            }
            else { 
                System.out.println( ">>> Recieved NULL from HBase for KEY:"+key ); 
            }

            return mr;
        } 
        else {
            return null;
        }
    }
 
    HBaseCacheService service = null;

    public void putIntoCache(TimeSeriesObject exp1) throws IOException {
        
        String key = getKey(exp1, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());

        if (RAM) {
            c.put(key, exp1);
        }

        if (WebHBase) {

            if ( service == null ) service = new HBaseCacheService();
            TSCacheV3 proxy = service.getTSCacheV3Port();

            String value = MRT.getAsString(exp1);

            proxy.put(key, value);
        };
    }

    /**
     * 
     * Provide a Key Extension to encode the time range ...
     * 
     * @param exp1
     * @param von
     * @param bis
     * @return 
     */
    public static String getKey() {
        Calendar von = WikiHistoryExtractionBASE.getVon();
        Calendar bis = WikiHistoryExtractionBASE.getBis();
        
        String v = cutMillis(von);
        String b = cutMillis(bis);

//         System.out.println("(MR-Key: von=" + v + ")");

        String key = v + "___" + b + "___" ;
//        System.out.println(key);

        return key;
    }
    
    /**
     * 
     * Provide a Key Extension to encode the time range ...
     * 
     * @param exp1
     * @param von
     * @param bis
     * @return 
     */
    public static String getKey(TimeSeriesObject exp1, Calendar von, Calendar bis) {
        if ( von == null ) von = WikiHistoryExtractionBASE.getVon();
        if ( bis == null ) bis = WikiHistoryExtractionBASE.getBis();

//        System.out.println("(MR-Key: von=" + von + ")");
//        System.out.println("(MR-Key: bis=" + bis + ")");

        String v = cutMillis(von);
        String b = cutMillis(bis);

        
        String key = v + "___" + b + "___" + exp1.getIdentifier();
        System.out.println(key);

        return key;
    }

    public static String getKey(WikiNode wn, Calendar von, Calendar bis) {
        
        if ( von == null ) von = WikiHistoryExtraction2.getVon();
        if ( bis == null ) bis = WikiHistoryExtraction2.getBis();
        
        
        String v = cutMillis(von);
        String b = cutMillis(bis);

        // System.out.println("(WN-Key: von=" + v + ")");

        String key = v + "___" + b + "___" + wn.getKey();
        // System.out.println( key );
        return key;
    }

    static String cutMillis(Calendar o) {
        String v = "" + o.getTimeInMillis();
//        v = v.substring( 0 , v.length() -4 ) + "0000";
        return "" + v;
    }

    public static Hashtable<String, TimeSeriesObject> getC() {
        return tsc.c;
    }

    public static void setC(Hashtable<String, TimeSeriesObject> c) {
        tsc.c = c;
        for (String s : tsc.c.keySet()) {
            System.out.println("k=> " + s);
        }
    }

//    public Hashtable<String, TimeSeriesObject> _getC(Vector<WikiNode> n) {
//        Hashtable<String, TimeSeriesObject> cSUB = new Hashtable<String, TimeSeriesObject>();
//
//        for (WikiNode node : n) {
//            String key = getKey(node, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());
//            TimeSeriesObject mr = c.get(key);
//            cSUB.put(key, mr);
//        }
//
//        return cSUB;
//    }
    public void storeC(Vector<WikiNode> n, ObjectOutputStream store) throws IOException {

        WikiNodeCacheEntry entry = new WikiNodeCacheEntry();

        for (WikiNode node : n) {

            entry.key = getKey(node, WikiHistoryExtraction2.getVon(), WikiHistoryExtraction2.getBis());
            entry.mr = c.get(entry.key);
            
            if( entry.mr != null ) {
              // if ( debug ) System.out.println( "MR:" + entry.mr.getLabel() + " Key: " + entry.key );

              entry._store(store);
   
            }
            else {
              if (debug ) System.out.println( "ERROR: >>> MR for Node: " + node.toString() + " is not available in group: " + entry.key );
            }
              

        }
        store.close();
    }

    public static Calendar von;
    public static Calendar bis;
    
    public int _loadC(ObjectInputStream store) throws IOException, ClassNotFoundException {
        
        int i = 0;
        
        WikiNodeCacheEntry entry = new WikiNodeCacheEntry();
        
        i = i + entry.load( store , c , von, bis );

        if ( debug ) System.out.println( ">>> gelesen : i=" +i );
        return i;
    }

    public void ping() {
         
    }
}

