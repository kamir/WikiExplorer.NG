package m3.wikipedia.corpus.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JTextArea; 
import research.ETH.ExtendedNodePairSFE;
import m3.util.WikiToolHelper;
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;
import org.etosha.core.sc.connector.external.Wiki.Revision;


/**
 * Wir laden hier alle Gruppen und ermittlen sowohl die tatsächliche als auch
 * die theoretische LINK-Anzahl um die LINK Dichte zu messen.
 *
 *
 * @author kamir
 */
public class NetworkCollaboratorsExtractor implements Runnable {
    
    private NetworkCollaboratorsExtractor() {   }
    
    String version = "v6";
    boolean verbose = false;
    boolean _useBacklinks = false;

    public NetworkCollaboratorsExtractor(WikiNode wn,   boolean verbose) {
        this.wn = wn;
        this.verbose = verbose;
    }
    
    
    static int[] years = { 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 };//
    
        
    static String[] pages = { "Amoklauf_von_Erfurt", "Lehman_Brothers"  }; //, "Illuminati_(Buch)" , "Sulingen", "Amoklauf_von_Erfurt"  }; // , "Stollberg"}; // {"Daimler_AG", "Sulingen", "Bad Harzburg"}; // , "Fritiof_Nilsson_Piraten"};
    static String[] wikis = {"de", "en"};//
        
    int MONTH = 0;
    int partsMAX = 12;
 
    Hashtable<Integer, WikiNode> CN = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> IWL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> AL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> BL = new Hashtable<Integer, WikiNode>();
    
    static java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
    static java.util.GregorianCalendar von = new java.util.GregorianCalendar();

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
        NetworkCollaboratorsExtractor nc = new NetworkCollaboratorsExtractor();
        
        int z; //  = Integer.parseInt( javax.swing.JOptionPane.showInputDialog("Index: ") );

        z=1;
        boolean useBackLinks = false;
        
        boolean threads = false;
        nc._doit( threads, useBackLinks, z);
    }
    
    
    public void grabData(WikiNode wn, int month, int year ) throws Exception {
        
//        bis.clear();
//        bis.set(2008, 0, 1, 0, 0);        
//        von.clear();
//        von.set(2008, 3, 1, 0, 0);        
//        String part = "Q1_LN";
        
//        bis.clear();
//        bis.set(2008, 3, 1, 0, 0);        
//        von.clear();
//        von.set(2008, 6, 1, 0, 0);        
//        String part = "Q2_LN";
        
//                bis.clear();
//        bis.set(2008, 6, 1, 0, 0);        
//        von.clear();
//        von.set(2008, 9, 1, 0, 0);        
//        String part = "Q3_LN";
//        
//        bis.clear();
//        bis.set(2008, 9, 1, 0, 0);        
//        von.clear();
//        von.set(2008, 12, 1, 0, 0);        
//        String part = "Q4_LN";
        
        MONTH = month;
        
        bis.clear();
        bis.set(year, month + 3, 1, 0, 0);  
        
        von.clear();
        von.set(year, (month), 1, 0, 0);        
        String part = "M_" + (month+1) + "_GN";
                              
        CN = new Hashtable<Integer, WikiNode>();
        IWL = new Hashtable<Integer, WikiNode>();
        AL = new Hashtable<Integer, WikiNode>();
        BL = new Hashtable<Integer, WikiNode>();

        _pageEdits = new Hashtable<WikiNode, Vector<String>>();
        
        try {
            
            String fn = "/Volumes/MyExternalDrive/CALCULATIONS/networks/colab/colabnet_"+year+"_part_" + part + "." + wn.getKeySAFEFILENAME() + "." + _useBacklinks + "."+version+".stat.net";
            
            bw = new BufferedWriter(new FileWriter(new File( fn + ".csv" )));
             
            CN.put(i, wn);
        
            loadCore(wn.wiki, wn.page);
            loadLocalNeighbors();
            loadGlobalNeighbors();
            
            addEditsLinksToGraph( bw);
             
            System.out.println("#errors:" + parseErrors );
        } 
        catch (IOException ex) {
            Logger.getLogger(NetworkCollaboratorsExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * Load for all pages in the array only the first partition ...
     * 
     * @param useThreads
     * @param useBacklinksss
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception 
     */
    public void _doit(boolean useThreads, boolean useBacklinksss) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int i = 0;

        for (int ip = 0; ip < pages.length; ip++) {
            String pn = pages[ip];
            String w = wikis[ip];
            i++;

            WikiNode wn = new WikiNode(w, pn);

            NetworkCollaboratorsExtractor ndc2 = new NetworkCollaboratorsExtractor(wn, false);
            ndc2._useBacklinks = useBacklinksss;
            if (useThreads) {
                Thread tr = new Thread(ndc2);
                tr.start();
            } else {
                ndc2.grabData(wn,0, 2009 );
            }
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
    public void _doit(boolean useThreads, boolean useBacklinksss, int ip) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

            String pn = pages[ip];
            String w = wikis[ip]; 

            WikiNode wn = new WikiNode(w, pn);

            NetworkCollaboratorsExtractor ndc2 = new NetworkCollaboratorsExtractor(wn, false);
            ndc2._useBacklinks = useBacklinksss;
            
            if (useThreads) {
//                Thread tr = new Thread(ndc2);
//                tr.start();
            } 
            else {
                for( int year : years )
                    for( int i = 0; i < partsMAX; i=i+3 ) ndc2.grabData(wn,i, year);
            }
    }
    
    
    double rhoCORE = 0.0;
    double rhoAL = 0.0;
    double rhoBL = 0.0;

    
    int i;
    public void loadCore(String wikipedia, String pn) throws IOException, Exception {

        i = 0;
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        HashMap<String, String> map = wiki.getInterWikiLinks(pn);
        
        System.out.println("> # of interwiki links: " + map.size());

        int SUMIWL = 0;

        SUMIWL = map.size();
        
        WikiNode cnNODE = new WikiNode(wikipedia, pn);

        addEditorsFor( cnNODE );

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
            
            addEditorsFor( iwlNODE );

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

        DecimalFormat df = new DecimalFormat("0.000");

        int n = map.size() + 1; // (n+1) muss hier her, um die Central node in den Kern zu nehmen.
        double z = n * (n - 1);
        double rho = SUMIWL / z;

        rhoCORE = rho;

        System.out.println("---------------------------------\n\n\n");
        System.out.println("n        : " + n);
        System.out.println("z        : " + z);
        System.out.println("SUMIWL   : " + SUMIWL);
        System.out.println("rho      : " + rho);
   
        System.out.println( wikipedia + "\t" + pn + "\t" + n + "\t" + z + "\t" + SUMIWL + "\t" + df.format(rho));

        System.out.flush();
        System.out.println("=================================\n\n\n");
    }

    private static boolean isLinkInLangsAvailable(String link, String[] langs, JTextArea b) throws IOException {
        boolean bo = true;
        for (String l : langs) {
            Wiki wiki = new Wiki(l + ".wikipedia.org");

            link = WikiToolHelper.isCleanPagename(link);

            if (link == null) {
                return false;
            }

            HashMap<String, Object> map = wiki.getPageInfo(link);
            Integer i = (Integer) map.get("size");
            b.append(l + " : " + link + " => " + i + "\n");
            System.out.println(l + " : " + link + " => " + i + "\n");
            bo = bo && (i > 0);
        }
        System.out.flush();
        return bo;
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
                
                addEditorsFor( alNODE );

            }


//            if (_useBacklinks) {
//                String[] map2 = wiki.whatLinksHere(n.page);
//
//                for (String key : map2) {
//                    
//                    i++;
//                    WikiNode alNODE = new WikiNode(n.wiki, key);
//                    //AL.put(i, alNODE );
//                    
//                    ExtendedNodePairSFE l = new ExtendedNodePairSFE( alNODE.getKey(), n.getKey() , "ALB" );
//                    System.out.println( l.getStaticLinkLine() );
//                    enps.add(l);
//                }
//            }
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
                
                addEditorsFor( blNODE );
            }

//            if (_useBacklinks) {
//                String[] map2 = wiki.whatLinksHere(n.page);
//                for (String key : map2) {
//                    i++;
//                    WikiNode blNODE = new WikiNode(n.wiki, key);
//                    BL.put(i, new WikiNode(n.wiki, key));
//                    ExtendedNodePairSFE l = new ExtendedNodePairSFE( blNODE.getKey(), n.getKey() , "BLB" );
//                    System.out.println( l.getStaticLinkLine() );
//                    enps.add(l);
//                }
//            }
        }
    }

    private int[] _getIntraGroupDENS(Hashtable<Integer, WikiNode> AL) throws IOException {

        int SUMINT = 0;
        int SUMEXT = 0;

        HashSet hash = new HashSet();
        for (WikiNode wn : AL.values()) {
            hash.add(wn.getKey());
        }

        int n = hash.size();


        for (WikiNode wn : AL.values()) {

            Wiki wiki = new Wiki(wn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(wn.page);

            for (String s : map) {

                WikiNode w = new WikiNode(wn.wiki, s);

                if (hash.contains(w.getKey())) {
                    SUMINT++;
                } else {
                    SUMEXT++;
                }
            }
        }

        int[] sums = new int[3];

        sums[0] = SUMINT;

        sums[1] = n;

        sums[2] = SUMEXT;

        return sums;
    }

    private void calcTheorieValues(BufferedWriter bwNL) {
        StringBuffer sb = new StringBuffer();
        String n = "\n";
        sb.append(" #nodes\t#links" + n);
        sb.append("CN =" + CN.size() + "\t" + getN_Links(CN) + n);
        sb.append("IWL=" + IWL.size() + "\t" + getN_Links(CN) + n);
        sb.append("AL =" + AL.size() + "\t" + getN_Links(CN) + n);
        sb.append("BL =" + BL.size() + "\t" + getN_Links(CN) + n);
        sb.append("O  =" + getOmmitted() + n);
        sb.append("LI =" + getLocalInteraction() + n);
        sb.append("GI =" + getGlobalInteraction() + n);

        System.out.println(sb.toString());
    }

    private int getN_Links(Hashtable<Integer, WikiNode> CN) {
        return CN.size() * (CN.size() - 1);
    }
    
    int CNs = 0;
    int IWLs = 0;
    int ALs = 0;
    int BLs = 0;
    int OM = 0;
    int LI = 0;
    int GI = 0;

    private int getOmmitted() {
        int n = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        OM = (CNs * IWLs + IWLs * ALs + CNs * BLs + ALs * BLs) * 2;

        return n;
    }

    private int getLocalInteraction() {
        LI = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        LI = (CNs * ALs) * 2;

        return LI;
    }

    private int getGlobalInteraction() {
        GI = 0;
        CNs = CN.size();
        IWLs = IWL.size();
        ALs = AL.size();
        BLs = BL.size();

        GI = (IWLs * BLs) * 2;

        return GI;
    }
    double R1 = 0;
    double R2 = 0;
    double R3 = 0;
    double R4 = 0;
    double RIWL = 0;
    double RAL = 0;
    double RBL = 0;
    double RLI = 0;
    double RGI = 0;
    double ROM = 0;
    double S1 = 0;
    double S2 = 0;
    double S3 = 0;
    double S4 = 0;
    int sum = 0;
    int S = 0;
    int LIWL;
    int LAL;
    int LBL;

    private void calcRatios() {

        getOmmitted();
        getLocalInteraction();
        getGlobalInteraction();

        LIWL = IWLs * (IWLs - 1);
        LAL = ALs * (ALs - 1);
        LBL = BLs * (BLs - 1);

        S1 = LIWL + LAL + LI;
        S2 = LIWL + LAL + LI + GI;
        S3 = LIWL + LAL + LI + GI + LBL;

        sum = CNs + IWLs + BLs + ALs;

        S = sum * (sum - 1);

        R1 = S1 / S;
        R2 = S2 / S;
        R3 = S3 / S;

        ROM = OM / S;

        RLI = LI / S;
        RGI = GI / S;

        RIWL = IWLs / S;
        RAL = ALs / S;
        RBL = BLs / S;

    }
    int sumLINKS_AL[] = null;
    int sumLINKS_BL[] = null;
    int sumLINKS_IWL[] = null;

    private double[][] _calcAverageDegree() throws IOException {

        sumLINKS_AL = _getIntraGroupDENS(AL);
        sumLINKS_BL = _getIntraGroupDENS(BL);
        sumLINKS_IWL = _getIntraGroupDENS(IWL);

        double[][] k = new double[3][3];

        k[0][0] = (double) sumLINKS_AL[0] / (double) AL.size();
        k[1][0] = (double) sumLINKS_BL[0] / (double) BL.size();
        k[2][0] = (double) sumLINKS_AL[0] / (double) IWL.size();

        k[0][1] = (double) sumLINKS_AL[1] / (double) AL.size();
        k[1][1] = (double) sumLINKS_BL[1] / (double) BL.size();
        k[2][1] = (double) sumLINKS_AL[1] / (double) IWL.size();

        k[0][2] = (double) sumLINKS_AL[2] / (double) AL.size();
        k[1][2] = (double) sumLINKS_BL[2] / (double) BL.size();
        k[2][2] = (double) sumLINKS_AL[2] / (double) IWL.size();

        return k;
    }

    private int getSumLinks(Hashtable<Integer, WikiNode> AL) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    BufferedWriter bwNL;
    
    public int COUNTS = 0;

    
    
    WikiNode wn = null;

    /**
     * 
     * Interface RUNNABLE() ... wird in doIt( ... ) aufgerufen !!!
     * 
     */
    public void run() {

        if (wn == null) {
            return;
        }

        javax.swing.JOptionPane.showMessageDialog(null, wn.getKey() + " ... started!  YAER: 2009");
        try {

            grabData(wn,0,2009);

        } catch (Exception ex) {
            Logger.getLogger(NetworkCollaboratorsExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("[ColabNE.Process] ... {" + wn.toString() + "} is done ...");
    }

 

    
    BufferedWriter bw = null;
    
    int lastId = 0;
    
    Hashtable<WikiNode, Vector<String>> _pageEdits = null;
    
    private void writeLinkToStream( BufferedWriter bw , ExtendedNodePairSFE enp) throws IOException {

        String n1 = enp.s;
        String n2 = enp.t;
        String typ = enp.type;

        String linkid = "" + (lastId + 1);

        String label = "(" + n1 + ";" + n2 + ")";
        int weight = 1;

        bw.write(linkid + "\t" + n1 + "\t" + n2 + "\t" + typ + "\t" + label + "\t" + weight + "\t" + MONTH + "\n");
        lastId++; 
    }

 

    private void addEditsLinksToGraph( BufferedWriter bw ) throws IOException {
 
            
            for( WikiNode page : _pageEdits.keySet() ) {            
               Vector<String> usersPerPage = _pageEdits.get(page);
               addUserLinksToGraph( usersPerPage, page, bw );
            }
      
    }
    
 

    

 

    private void addUserLinksToGraph(Vector<String> usersPerPage, WikiNode page,  BufferedWriter bw) throws IOException {
 
        Vector<String> usersPerPage2 = (Vector<String>) usersPerPage.clone();
        
        for (String id : usersPerPage) {
        
//            // Source ... the user ...
            String sourceUser = id;
       
//            // link 1 : to page:
            String targetPage = page.getKey();
             
            ExtendedNodePairSFE pair = new ExtendedNodePairSFE( sourceUser , targetPage, "edit" );
            writeLinkToStream(bw, pair);
            
            
            for (String targetUser : usersPerPage2) {
 
                ExtendedNodePairSFE pair2 = new ExtendedNodePairSFE( sourceUser , targetUser, "colab" );
                writeLinkToStream(bw, pair2);
 
            } 
        } 
    }

//    private void _loadEditorsFor(WikiNode l2, WikiNode l1) {
//        try {
//            addEditorsFor( l1 ); 
//            addEditorsFor( l2 ); 
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//             
//    }

    
    int totalEditors = 0;    
    int parseErrors = 0;
    
    private void addEditorsFor(WikiNode n) throws IOException {
        try{
            
            Vector<String> user = _pageEdits.get( n.getKey() );

            if ( user != null ) return;
            else user = new Vector<String>();

            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            Wiki.Revision[] revs = wiki.getPageHistory3(n.page,von,bis);
            int j = 0;
            if (revs != null) {
                Calendar calFIRST = null;
                int z = 0;
                for (Wiki.Revision r : revs) {
                    z++;
                    System.out.println("#############\t" + z + ")" + r.getTimestamp().getTime() + "(" + von.getTime() + " --- " + bis.getTime() + ")");
                    Calendar cal = r.getTimestamp();
                    if (calFIRST == null) {
                        calFIRST = cal;
                    } else {
                        if (cal.before(calFIRST)) {
                            calFIRST = cal;
                        }
                    }

                    String u = r.getUser();
                    if( !user.contains( u ) ) user.add( u );
                }
                j++;


            }
            _pageEdits.put( n  , user);
            totalEditors = totalEditors + user.size() ;
            System.out.println( ">>> " + n.getKey() + " : " + user.size() + " editors. (" + totalEditors + ")");
        }
        catch(Exception ex){ 
            parseErrors++;
        }
    }
}
