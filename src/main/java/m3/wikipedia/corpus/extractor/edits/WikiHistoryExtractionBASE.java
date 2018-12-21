/***
 * 
 *  Import der Edit-TS aus WIKIPEDIA und Ablage
 *  im HBase-Cache sowie laden des lokalen Cache zum Testen.
 *
 ***/
package m3.wikipedia.corpus.extractor.edits;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import com.thoughtworks.xstream.XStream;
import org.apache.hadoopts.data.series.MRT;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import org.apache.hadoopts.hadoopts.core.TSBucket;
import m3.io.WikiNodeCacheEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane; 
import m3.tscache.TSCache;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;
import org.etosha.core.sc.connector.external.Wiki.Revision;

/**
 *
 * @author kamir
 */
public class WikiHistoryExtractionBASE {
    
    static Calendar von = null;
    static Calendar bis = null;
    
    static boolean getFirstRevision = false;
    
    static String[] wikis = {"de", "en"};
    static String[] pages = {"Deutschland", "United_States"};
    
    static Vector<TimeSeriesObject> _rows1 = new Vector<TimeSeriesObject>();
    
    static boolean showChart = false;
    
    static boolean loadSPLITS = true;

    static boolean loadFromLocalCACHE = true;
    static boolean storeToLocalCACHE = true;
    static boolean storeToLocal_SPLIT_PER_CN = true;
    
        /**
     * TestRun ...
     *
     * main( ... )
     *
     * run( Vector<WikiNode> ... ) run( WikiNode[] ... )
     *
     * work( ... )
     *
     * => TimeSeriesObjectn liegen vor ... und können gepuffert werden ...
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {


        
        String path1 = "."; 
        String path2 = "."; 
 
        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();
        File ff = wd.selectFile( path1 );
  
        von = new GregorianCalendar();
        von.clear();
        von.set(2010, 0, 1, 0, 0);

        bis = new GregorianCalendar();
        bis.clear();
        bis.set(2010, 0, 1, 0, 0);
        bis.add(GregorianCalendar.DAY_OF_YEAR, 364);

        String label = von.getTime().getTime() + "_" + bis.toString().toString();
        String folder = "/Volumes/MyExternalDrive/TSB.Store";
        File fF = new File( folder );
        if ( !fF.exists() ) fF.mkdir();
        
        System.out.println( "> use label: " + label );
        
        boolean loadFromLocalCACHE = true;
        boolean storeToLocalCACHE = true;


        File f = wd.getRealPathToProjectFile( ff );

        if (f.exists() ) {

            
            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());
            wd = wd.load(f); 
            
            if( loadFromLocalCACHE ) {
                if ( loadSPLITS ) { 
                    _loadLocalCacheDumpSPLITS(path2, wd);
                }
                else {
                    _loadLocalCacheDump(path2, wd);
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
                run(wnCN, von, bis, true, folder, label + "_CN" );
            }

            final Vector<WikiNode> wnIWL = wd.getIWL();
            System.out.println(">>> Nr of IWL-nodes : " + wnIWL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } 
            else {
                run(wnIWL, von, bis, true, folder, label + "_IWL" );
            }


            final Vector<WikiNode> wnAL = wd.getAL();
            System.out.println(">>> Nr of AL-nodes : " + wnAL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
                System.exit(-1);
            } 
            else {
                run(wnAL, von, bis, true, folder, label + "_A.L" );
            }

            final Vector<WikiNode> wnBL = wd.getBL();
            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());

            if (!von.before(bis)) {
                System.err.println("< ENDE muss nach ANFANG liegen !!! />");
                System.exit(-1);
            } 
            else {
                run(wnBL, von, bis, true, folder, label + "_B.L" );
            }

        } 
        else {
            System.out.println(">>> " + f.getAbsolutePath() + " not available.");
        }

        if ( showChart ) {
            // Here we can go into the study object and take all the elements from cache for plotting ...
            
            
            //MultiBarChart.open(rows1, "1 Event-Zeitreihen", "t", "nr", false);
        }
        
        /**
         *  
         */ 
        if( storeToLocalCACHE ) {
            storeLocalCacheDump(path2, wd.selectedName);
        }    
        
        wd.logExtraction( von, bis );

        System.out.println( ">>> " + wd.name );
        System.out.println( ">>> INFO : \n" + wd.getDescription() );

        System.out.println( ">>> von : " + von.getTime() );
        System.out.println( ">>> bis : " + bis.getTime() );
     
        System.out.println( ">>> Done. " );
    }
    
    protected static Vector<Long> convertRevsToLong(Vector<Revision> r) {
        Vector<Long> v = new Vector<Long>();
        for (Revision rev : r) {
            v.add(rev.getTimestamp().getTimeInMillis());
            // System.out.println( rev.getTimestamp().getTimeInMillis() );
        }
        return v;
    }

    public static Calendar getBis() {
        return bis;
    }

    static boolean debug = true;
    protected static TimeSeriesObject getMrFromCache(WikiNode wikiNode) {
        TimeSeriesObject mr = null;
        try {
            if (debug)
                System.out.println(">>> get(" + wikiNode.getKey() + ")");
    
            mr = TSCache.getTSCache().getMrFromCache(wikiNode);
        } 
        catch (IOException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mr;
    }

    public static Calendar getVon() {
        return von;
    }

    public static Hashtable<String, TimeSeriesObject> load(File f) throws FileNotFoundException {
        System.out.println(">>> " + f.getAbsolutePath() + " (" + f.canRead() + ")");
        if (f.canRead()) {
            FileInputStream os = new FileInputStream(f);
            XStream xstream = new XStream();
            try {
                Object o = xstream.fromXML(os);
                Hashtable<String, TimeSeriesObject> d = (Hashtable<String, TimeSeriesObject>) o;
                return d;
            } 
            catch(Exception ex) { 
                System.err.println(">>> " + f.getAbsolutePath() + " ... was not filled !!!");
                return new Hashtable<String, TimeSeriesObject>();
            }    
        } else {
            System.err.println(">>> " + f.getAbsolutePath() + " ... was created !!!");
            return new Hashtable<String, TimeSeriesObject>();
        }
    }

    protected static void _loadLocalCacheDump(String path2, WikiStudieMetaData wd) throws FileNotFoundException {

        String name = wd.selectedName;
        
        System.out.println(">>> WARM-UP local cache ... ");
        File f = new File(path2 + "/editts-cache." + name + ".dump");
        Hashtable<String, TimeSeriesObject> c = load(f);
        TSCache.setC(c);
        JOptionPane.showMessageDialog(null, c.size() + " Reihen von " + wd.getNrOfNodes_ALL(path2, name));
    }
    
    public static int CURRENT_Chunk = 0;
    
    protected static void _loadLocalCacheDumpSPLITS(String path2, WikiStudieMetaData wd) throws FileNotFoundException, ClassNotFoundException {
    
        long t0 = System.currentTimeMillis();
        
        String name = wd.selectedName;
        
        int max = wd.getCN().size();
        System.out.println(">>> WARM-UP local cache with SPLITS ... " + max);
        
        int loaded = 0;
        
        for( int i = 0; i < max; i++ ) {
            
            CURRENT_Chunk = i+1;

            File f = new File(path2 + "/editts-cache." + name + "." + (i+1) + ".dump");
            System.out.println(">>> SPLITS : " + f + " " + f.canRead() );
        
            try {
                if ( f.canRead() )
                    loaded = loaded + TSCache.getTSCache()._loadC( getStoreIN(f) );
            } 
            catch (IOException ex) {
                Logger.getLogger(WikiHistoryExtractionBASE.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        long t1 = System.currentTimeMillis();
        
        JOptionPane.showMessageDialog(null, loaded + " Reihen von " + wd.getNrOfNodes_ALL(path2, name) + " in: " + (t1-t0) + " ms.");
        
    }

    public static TimeSeriesObject loadPageHistory(WikiNode wn) throws IOException, Exception {
        return loadPageHistory(wn.wiki, wn.page);
    }

    public static boolean useTSCache = true;
    
    public static TimeSeriesObject loadPageHistory(String wikipedia, String pn) throws IOException, Exception {
        
        WikiNode wn = new WikiNode(wikipedia, pn);
        TimeSeriesObject m = null;
        
        if ( useTSCache ) {
            System.out.println("useTSCache=" + useTSCache);
            m = getMrFromCache(wn);
            
        }
            
        if (m != null) {
            //System.out.println( m.getStatisticData(">>> "));
            return m;
        }
        
        System.err.println(">>> GRAB DATA FROM WIKIPEDIA <<<");
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
        Vector<Revision> allR = new Vector<Revision>();
        Calendar ist = (Calendar) von.clone();
        Calendar late = (Calendar) von.clone();
        late.add(Calendar.DAY_OF_YEAR, 30);
        while (late.before(bis)) {
            Revision[] r = lookupRevisions(pn, wiki, ist, late);
            ist.add(Calendar.DAY_OF_YEAR, 30);
            late.add(Calendar.DAY_OF_YEAR, 30);
            for (Revision rev : r) {
                allR.add(rev);
            }
        }
        Revision[] r = lookupRevisions(pn, wiki, ist, bis);
        for (Revision rev : r) {
            allR.add(rev);
        }
        //        System.out.println(">>> EDITS: " + allR.size());
        Vector<Long> v = convertRevsToLong(allR);
        String label = wikipedia + "___" + pn;
        String descr = "Edit-ES:" + wikipedia + "___" + pn;
//        System.out.println("*** EDITS: " + label + " ===> " + v.size());
        // TODO : OPRIMIEREN auf BEDARF ...
        // CONVERTIERE die Reihen ... (NICHT ALLES BENÖTIGT !!!)
        TimeSeriesObject mr = MRT.convertDates2Messreihe(v, label, descr);
        mr.setIdentifier(wn.getKey());
        // wird in den cache gelegt ..
        
        TimeSeriesObject exp1 = null;
        try {
            exp1 = MRT.expand(mr, von, bis, 60 * 60, false);
            // System.out.println(late.getTime());
            if ( useTSCache )
                putIntoCache(wn, exp1);
        }
        catch(Exception ex) { 
        
        }
        return exp1;
    }

    protected static Revision[] lookupRevisions(String pn, Wiki wiki, Calendar von, Calendar bis) throws IOException {
        Revision[] revs = null;
        revs = wiki.getPageHistory3(pn, von, bis);
        //
        //        System.out.println(">>> "  +revs.length  );
        //
        //        int j = 0;
        //        if (revs != null) {
        //            Calendar calFIRST = null;
        //            int z = 0;
        //            for (Revision r : revs) {
        //                z++;
        //                System.out.println("\t" + z + ")" + r.getTimestamp().getTime());
        //                Calendar cal = r.getTimestamp();
        //                if (calFIRST == null) {
        //                    calFIRST = cal;
        //                } else {
        //                    if (cal.before(calFIRST)) {
        //                        calFIRST = cal;
        //                    }
        //                }
        //            }
        //            j++;
        //        }
        if (getFirstRevision) {
            Revision r = wiki.getFirstRevision(pn);
            System.out.println(">>> first revision : " + r.getTimestamp().getTime());
        }
        return revs;
    }

//    protected static TimeSeriesObject preprocess(TimeSeriesObject m) {
//        if (showChart) {
//            TimeSeriesObject day1 = m.setBinningX_sum(24);
//            rows1.add(day1);
//            rowsBINARY1.add(m);
//        }
//        return m;
//    }

    protected static void putIntoCache(WikiNode wn, TimeSeriesObject exp1) {
        try {
            System.out.println(">>> put( " + wn.getKey() + " );");
            TSCache.getTSCache().putIntoCache(exp1);
        } catch (IOException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public static void run(Vector<WikiNode> n, Calendar v, Calendar b, boolean showCh, String folder, String label ) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        WikiNode[] nodes = new WikiNode[n.size()];
        int i = 0;
        System.out.println(">>>>>> Wiki-API collection subsystem works on the follwoing CN nodes:");
        for (WikiNode nnn : n) {
            nodes[i] = nnn;
            i++;
            System.out.println("(" + i + "): \t "+nnn);
        }
        _run(nodes, v, b, showCh, folder, label );
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
    public static void _runWithIWLExpansionOfLists(Vector<WikiNode> n, Calendar v, Calendar b, boolean show, String folder, String label ) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
        Vector<WikiNode> odes = new Vector<WikiNode>();
        
        int i = 0;
        System.out.println(">>>>>> Wiki-API collection subsystem works on the follwoing CN nodes:");
        for (WikiNode nnn : n) {
            odes.add( nnn );

            
            i++;
            System.out.println("(" + i + "): \t "+nnn);

            Wiki wiki = new Wiki(nnn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            HashMap<String, String> map = wiki.getInterWikiLinks(nnn.page);
            
            for( String key : map.keySet() ) { 
                WikiNode wnn = new WikiNode( key, map.get(key) );
                odes.add(wnn);
            }
            
            System.out.println("> # of interwiki links: " + map.size());
        }
        
        
        WikiNode[] nodes = new WikiNode[ odes.size() ];
        
        int j = 0;
        for( WikiNode n2 : odes ) { 
            nodes[j] = n2;
            j++;
        }
        
        _run(nodes, v, b, show, folder, label );
    }
    
    
    

    public static void _run(WikiNode[] cnNodes, Calendar v, Calendar b, boolean showCh, String folder, String label ) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
       
        ArrayList<Integer> list = new ArrayList<Integer>();
        
        // work with all
        for (int i = 0; i < cnNodes.length ; i++) {
            list.add(i);
        }
        
        // Collections.shuffle( list );
        
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
        
        
        _work(null, folder, label );
    }

    public static void setBis(Calendar _bis) {
        bis = _bis;
    }

    public static void setVon(Calendar _von) {
        von = _von;
    }

    public static void store(File f, Hashtable<String, TimeSeriesObject> data) throws FileNotFoundException, IOException {
        FileWriter os = new FileWriter(f);
        XStream xstream = new XStream();
        String s = xstream.toXML(data);
        os.write(s);
        os.flush();
        os.close();
    }
    
    public static ObjectOutputStream getStore(File f) throws FileNotFoundException, IOException {
        
        OutputStream os = new FileOutputStream( f );
        ObjectOutputStream oos = new ObjectOutputStream( os );
        return oos;
    }    
    
   public static ObjectInputStream getStoreIN(File f) throws FileNotFoundException, IOException {
        System.out.println(">>> load : " +f.canRead() );
        InputStream os = new FileInputStream( f );
        ObjectInputStream oos = new ObjectInputStream( os );
        return oos;
    }    
    
    public static void _store2(File f, Hashtable<String, TimeSeriesObject> data) throws FileNotFoundException, IOException {
        System.out.println(">>> store ... ");
        OutputStream os = new FileOutputStream( f );
        ObjectOutputStream oos = new ObjectOutputStream( os );
        
        XStream xstream = new XStream();

        WikiNodeCacheEntry entry = new WikiNodeCacheEntry();

        String s = null;
        
        for( String key : data.keySet() ) {
            
            entry.key = key;
            entry.mr = data.get(key);
            
            s = xstream.toXML( entry );
            
            oos.writeObject( s );
            s=null;
            
            
        }    
        oos.close();
    }

    protected static void storeLocalCacheDump(String path2, String name) {
        File f = new File(path2 + "/editts-cache." + name + ".dump");
        System.out.println( ">>> LOCAL DUMP (full): " + f.getAbsolutePath() );
        try {
            store(f, TSCache.getTSCache().getC());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Auch hier wird noch zu viel HEAP benötigt
     * 
     * =>>>  SINNVOLLE Serialisierung !!!
     * 
     * @param path2
     * @param name
     * @param CN
     * @param wd 
     */
    protected static void _storeLocalCacheDump_SPLIT_PER_CN(String path2, String name, int CN, WikiStudieMetaData wd) {
        if( name == null ) { 
            name = javax.swing.JOptionPane.showInputDialog( path2 + "/editts-cache." + name + "." + CN + ".dump", "${name}" );
        }
        File f = new File(path2 + "/editts-cache." + name + "." + CN + ".dump");
        System.out.println( ">>> LOCAL DUMP (split) : " + f.getAbsolutePath() );
        try {
            
            Hashtable<String, TimeSeriesObject> c = new Hashtable<String, TimeSeriesObject>();
            Vector<WikiNode> n = wd.extractByCN( CN );
            
            // Umweg über STRING und Object-Serialisierung
//            store2(f, TSCache.getTSCache().getC( n ));
            TSCache.getTSCache().storeC( n , getStore( f ) );
 
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikiHistoryExtraction2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static void _work(String[] args , String folder, String label) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        int i = 0;
        
        int counter = 0;
        double sum = 0;
        
        String fn = folder + "/" + label + ".tsb.seq";
        
        TSBucket tsb = TSBucket.createEmptyBucket();
        tsb._open( new File( fn ) ); 
        
        for (String pn : pages) {
            String w = wikis[i];
            TimeSeriesObject mr = loadPageHistory(w, pn);
            tsb.putMessreihe( mr );
            
            if (mr != null) { 
               counter++;
               sum = sum + mr.summeY();
            }
            
            // loop counter
            i++;
        }
        
        tsb.close();
        
        System.out.println( ">>> TSBucket generated: " +  fn );
        System.out.println( ">>> The Bucket containt Wikipedia Edits Counts : (av=" +  (sum / (double)counter) + ") (sum="+ counter +")");
    }
    
    
}
