package m3.wikipedia.corpus.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException; 
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;
import org.etosha.core.sc.connector.external.Wiki.Revision;

import org.openide.util.Exceptions; 
import research.ETH.ExtendedNodePairSFE;

/**
 * Wir laden hier von einer CN ausgehend alle Gruppen 
 * IWL, AL, BL und sammeln für diese Seiten alle edit events.
 * 
 * Jeder dieser Events kann dann in localen Queries verarbeitet werden.
 * 
 * @author kamir
 */
public class CollabNetworkExtractor implements Runnable  {
    
    private CollabNetworkExtractor() {   }
    
    static public String version = "v11";
    static public boolean verbose = false;
    static public boolean useBacklinks = true; 

    public CollabNetworkExtractor(WikiNode wn) {
        this.wn = wn;
    }
        
    static int year_MIN = 2001;
    static int year_MAX = 2014;
        
//    static String[] pages = { "Lehman_Brothers", "Amoklauf_von_Erfurt", "Illuminati_(Buch)"  }; //, "Illuminati_(Buch)" , "Sulingen", "Amoklauf_von_Erfurt"  }; // , "Stollberg"}; // {"Daimler_AG", "Sulingen", "Bad Harzburg"}; // , "Fritiof_Nilsson_Piraten"};
//    static String[] wikis = { "en", "de","de" };//

    static String[] pages = { 
        "Jon_Stewart", 
        "The_Daily_Show", 
        "Trevor_Noah", 
        "Stephen_Colbert", 
        "The_Colbert_Report", 
        "John_Oliver_(comedian)", 
        "Last_Week_Tonight_with_John_Oliver"   }; 
    
    static String[] wikis = { "en", "en", "en", "en", "en", "en", "en" };//

    private Hashtable<Integer, WikiNode> CN = new Hashtable<Integer, WikiNode>();
    private Hashtable<Integer, WikiNode> IWL = new Hashtable<Integer, WikiNode>();
    private Hashtable<Integer, WikiNode> AL = new Hashtable<Integer, WikiNode>();
    private Hashtable<Integer, WikiNode> BL = new Hashtable<Integer, WikiNode>();
    
    static java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
    static java.util.GregorianCalendar von = new java.util.GregorianCalendar();

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
        CollabNetworkExtractor nc = new CollabNetworkExtractor();
        
        int pageIndex; //  = Integer.parseInt( javax.swing.JOptionPane.showInputDialog("Index: ") );

        for( int i = 0; i < pages.length ; i++ ) {
            pageIndex=i;
            nc.doit( pageIndex );
        }
        
    }
    
    
    public void grabData() {
        
        bis.clear();
        bis.set(year_MAX, 11, 31, 59, 59);  
        
        von.clear();
        von.set(year_MIN, 0, 1, 0, 0);        
        
        String part = year_MIN + "_" + year_MAX + "_LN";
                              
        CN = new Hashtable<Integer, WikiNode>();
        IWL = new Hashtable<Integer, WikiNode>();
        AL = new Hashtable<Integer, WikiNode>();
        BL = new Hashtable<Integer, WikiNode>();
 
        try {
            
            String fn = "/Volumes/MyExternalDrive/CALCULATIONS/editevents/edits_" + part + "_" + wn.getKeySAFEFILENAME() + "_" + version;
            
            bw2 = new BufferedWriter(new FileWriter(new File( fn + ".list" )));
            // bw2.write(Wiki2.getRevisionTabedHeader());
            bw2.write( "HEADER .... line 99. grabData() in CollabNetworkExtractor" );
            
            bw = new BufferedWriter(new FileWriter(new File( fn + ".csv")));
             
            // seed
            CN.put(i, wn);
            
            try {
                // load all pages into buffers and than collect edit ts for those pages.
                loadCore(wn.wiki, wn.page);
                
                loadLocalNeighbors();
                loadGlobalNeighbors();
            } 
            catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            
             
            System.out.println("#errors:" + parseErrors );
        } 
        catch (IOException ex) {
            Logger.getLogger(CollabNetworkExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * 
     * @param useThreads
     * @param useBacklinksss
     * @param ip - index of selected CN
     * 
     * 
     * Load data in a loop for 12 Months ...
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception 
     */
    public void doit(int ip) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
    
        String pn = pages[ip];
        String w = wikis[ip]; 

        WikiNode wn = new WikiNode(w, pn);

        CollabNetworkExtractor ndc2 = new CollabNetworkExtractor(wn);
            
        Thread t = new Thread( ndc2 );
        t.start();
 
    }
    
    
    public void run() {
        grabData();
    }

    
    int i;
    public void loadCore(String wikipedia, String pn) throws IOException, Exception {

        i = 0;
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        HashMap<String, String> map = wiki.getInterWikiLinks(pn);
        
        System.out.println("> # of interwiki links: " + map.size());

        int SUMIWL = 0;

        SUMIWL = map.size();
        
        WikiNode cnNODE = new WikiNode(wikipedia, pn);

        loadEditsHistory(cnNODE );

        int j = 0;
        for (String key : map.keySet()) {

            String pnIWL = (String) map.get(key);

            System.out.println(j + " : " + key + " ---> " + pnIWL);

            Wiki wikiIWL = new Wiki(key + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            WikiNode iwlNODE = new WikiNode(key, pnIWL);
                    
            i++;
            IWL.put(i, iwlNODE);
            
            ExtendedNodePairSFE l = new ExtendedNodePairSFE( cnNODE.getKey(), iwlNODE.getKey(), "IWL" );
            System.out.println( l.getStaticLinkLine() );
            
            writeLinkToStream(bw, l);
            
            loadEditsHistory(iwlNODE );

            j++;

            HashMap<String, String> map2 = wikiIWL.getInterWikiLinks(pnIWL);
            SUMIWL = SUMIWL + map2.size();
            
            for (String key22 : map2.keySet()) {
                i++;
                
                WikiNode il_B_NODE = new WikiNode( key22, map2.get(key22) );
                            
                ExtendedNodePairSFE l2 = new ExtendedNodePairSFE( iwlNODE.getKey(), il_B_NODE.getKey() , "IWLB" );
                System.out.println( l2.getStaticLinkLine() );
                
                writeLinkToStream(bw, l2);   
                
            }             
        }
        System.out.flush();
        System.out.println("=================================\n\n\n");
    }
    
    private void loadLocalNeighbors() throws IOException {
        
        
        for (WikiNode n : CN.values()) {
            
            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(n.page);

            for (String key : map) {
                i++;
                
                WikiNode alNODE = new WikiNode(n.wiki, key);
                AL.put(i, alNODE);
                
                ExtendedNodePairSFE l = new ExtendedNodePairSFE( n.getKey(), alNODE.getKey() , "AL" );
                System.out.println( l.getStaticLinkLine() );
           
                
                writeLinkToStream(bw, l);
                
                loadEditsHistory(alNODE );

            }


            if (useBacklinks) {
                String[] map2 = wiki.whatLinksHere(n.page);

                for (String key : map2) {
                    
                    i++;
                    WikiNode alNODE = new WikiNode(n.wiki, key);
                    //AL.put(i, alNODE );
                    
                    ExtendedNodePairSFE l = new ExtendedNodePairSFE( alNODE.getKey(), n.getKey() , "ALB" );
                    System.out.println( l.getStaticLinkLine() );
                    writeLinkToStream(bw, l);

                    loadEditsHistory(alNODE );
                }
            }
        }
    }

    private void loadGlobalNeighbors() throws IOException {
        for (WikiNode n : IWL.values()) {
            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(n.page);

            for (String key : map) {
                i++;
                WikiNode blNODE = new WikiNode(n.wiki, key);
                BL.put(i, blNODE);
                ExtendedNodePairSFE l = new ExtendedNodePairSFE( n.getKey(), blNODE.getKey() , "BL" );
                System.out.println( l.getStaticLinkLine() );
                
                writeLinkToStream(bw, l);
                
                loadEditsHistory(blNODE );
            }

            if (useBacklinks) {
                String[] map2 = wiki.whatLinksHere(n.page);
                for (String key : map2) {
                    i++;
                    WikiNode blNODE = new WikiNode(n.wiki, key);
                    BL.put(i, new WikiNode(n.wiki, key));
                    ExtendedNodePairSFE l = new ExtendedNodePairSFE( blNODE.getKey(), n.getKey() , "BLB" );
                    System.out.println( l.getStaticLinkLine() );
                    writeLinkToStream(bw, l);
                    loadEditsHistory( blNODE );
                }
            }
        }
    }

  

    private BufferedWriter bw2;
    private BufferedWriter bw = null;
     
    private WikiNode wn = null;
    
    private int lastId = 0;  // linkcounter
    
    private void writeLinkToStream( BufferedWriter bw , ExtendedNodePairSFE enp) throws IOException {

        String n1 = enp.s;
        String n2 = enp.t;
        String typ = enp.type;

        String linkid = "" + (lastId + 1);

        String label = "(" + n1 + ";" + n2 + ")";
        int weight = 1;

        bw.write(linkid + "\t" + n1 + "\t" + n2 + "\t" + typ + "\t" + label + "\t" + weight + "\n");
        lastId++; 
    }
     
    int parseErrors = 0;
    
    int editId = 0;
    private void loadEditsHistory(WikiNode n) throws IOException {
        try{
                        
            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org");  
            Wiki.Revision[] revs = wiki.getPageHistory3(n.page,von,bis);
            int j = 0;
            if (revs != null) {
                Calendar calFIRST = null;
                int z = 0;
                for (Wiki.Revision r : revs) {
                    z++;
                    // System.out.println("#############\t" + z + ")" + r.getTimestamp().getTime() + "(" + von.getTime() + " --- " + bis.getTime() + ")");
                    /// MAJOR CHANGE !!!!
                    //bw2.write( editId + "\t" + r.toStringTabed( n.wiki ) );
                    bw2.write( editId + "\t" + r.toString() );
                    editId++;
                }
                j++;


            }
            
            bw2.flush();
        }
        catch(Exception ex){ 
            parseErrors++;
        }
    }

}
