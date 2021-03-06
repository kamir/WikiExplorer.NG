package m3.wikipedia.corpus.extractor.iwl;

import analysis.wikipagecorpus.PageCorpusAnalyser;
import com.cloudera.wikiexplorer.ng.app.WikipediaCorpusLoaderTool;
import m3.wikipedia.corpus.extractor.FileNameFilter;
import m3.io.CNResultManager2;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.*;
import m3.jstat.data.Corpus; 
import m3.wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import m3.wikipedia.explorer.gui.PageInfoView2;
import m3.wikipedia.explorer.data.WikiNode;
import hadoop.cluster.connector.SimpleClusterConnector;

import wikiapiclient.WikiORIGINAL;

import m3.wikipedia.corpus.extractor.*;
import m3.wikipedia.analysis.charts.RepresentationPlotBubbleChart;

/**
 * Extract a Wikipedia-Corpus for Interwiki-Linked data for a certain central
 * page CN and all linked pages with link depth ld=1 (just nearest neighbours).
 *
 * @author root
 */
public class ExtractIWLinkCorpus implements Runnable {
    
    private static void initStudie_PLOSONE() {
        studie = "PLOSONE";
        String[] _wiki = {"de", "de"};
//        String[] _page = {"Amoklauf_von_Erfurt", "Illuminati_(Buch)"};
        String[] _page = {"Stollberg", "Meiningen"};
        wiki = _wiki;
        page = _page;
    }
    

    public static String studie = null;
    public static String[] wiki = null;
    public static String[] page = null;

    public static void runFromGUITool(String _studie, String[] _wiki, String[] _page,
            boolean _withText, boolean runCluster,
            FileWriter fw, int _fm) throws IOException {

        studie = _studie;
        wiki = _wiki;
        page = _page;

        fwResults = fw;

        withText = _withText;
        /**
         *
         * all Einträge der Liste abarbeiten ... für jede CN wird ein eigener
         * Corpus geladen.
         *
         *
         */
        ExtractIWLinkCorpus tool = null;
        for (int i = 0; i < wiki.length; i++) {

            String w = wiki[i];
            String p = page[i];

            // define a center page to start data retrieval procedure ...
            WikiNode cp = new WikiNode(w, p);

            // create the extraction tool ...
            tool = new ExtractIWLinkCorpus();
            tool.centerPage = cp;
            tool.runOnCluster = runCluster;
            tool.fileMode = _fm;

            tool.crawlMode = true;

            tool._mergedMode = true;
            
            tool.run();

            System.out.println(" ***** ");
            System.out.println(" {" + cp.toString() + "} is done.");
            System.out.println(" ***** ");

        }

        tool.mergedClusterRun("merged_listfile_" + studie + ".lst", _studie);

        if (mns != null) {
            mns.close();
            mns.createWikiIDListe();
            mns.createNodeIDListe();
        };



    }

    /**
     *
     * Crawl und extract TS trennen ...
     *
     * @param studie
     * @throws IOException
     */
    public static void submit(String studie) throws IOException {

        /**
         *
         * Liste abarbeiten ...
         *
         */
        ExtractIWLinkCorpus tool = null;

        // create the extraction tool ...
        tool = new ExtractIWLinkCorpus();

        tool.runOnCluster = true;
        tool.fileMode = Corpus.mode_XML;
        tool._mergedMode = true;

        tool.mergedClusterRun("merged_listfile_" + studie + ".lst", studie);

    }
    public static FileWriter fwResults = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

//        initStudie_GERMAN_Cities();

        initStudie_PLOSONE();

        /**
         *
         * Kombinatorik ...
         *
         */
        for (int i = 0; i < wiki.length; i++) {
        //     for (int j = 0; j < page.length; j++) {
                String w = wiki[i];
                String p = page[i];

                // define a center page to start data retrieval procedure ...
                WikiNode cp = new WikiNode(w, p);

                // create the extraction tool ...
                ExtractIWLinkCorpus tool = new ExtractIWLinkCorpus();

                tool.crawlMode = false;
                tool.runOnCluster = false;
                tool.showAnalyseFrame = false;

                tool.centerPage = cp;

                Thread t = new Thread(tool);
                t.start();
//            }
        }
    }

    public static void initStudie_core() {
        studie = "core";
        String[] _wiki = {"de"};
        String[] _page = {"Barack Obama", "Angela Merkel"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_finance() {
        studie = "finance_DAX";
        String[] _wiki = {"de"};
        String[] _page = {"Bayerische_Hypotheken-_und_Wechsel-Bank",
            "Feldmühle_Nobel", "Babcock_Borsig", "Continental", "Bayerische_Vereinsbank",
            "MAN", "Mannesmann", "Nixdorf", "Schering", "Veba", "Viag", "Daimler-Benz", "Deutsche Bank",
            "Deutsche Lufthansa", "Dresdner Bank", "Henkel", "Hoechst", "Karstadt", "Kaufhof", "Linde", "RWE",
            "Siemens", "Degussa", "Commerzbank", "Bayer", "Volkswagen", "RWE", "BMW", "BASF", "Thyssen", "Allianz"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_GERMAN_Cities() {
        studie = "de.cities";
        String[] _wiki = {"de"};
        String[] _page = {"Sulingen", "Meiningen"};
        wiki = _wiki;
        page = _page;
    }

    public static void initStudie_finance2() {
        studie = "dax.finance";
        String[] _wiki = {"de"};
        String[] _page = {"Volkswagen"};
        wiki = _wiki;
        page = _page;
    }
    public boolean runOnCluster;
    public int fileMode;
    public String pattern = "2007/2007-12/page*";
    public boolean _mergedMode;
    public static boolean crawlMode;
    public boolean showAnalyseFrame; 

    public ExtractIWLinkCorpus() {
        tempCOLlinksA = new Vector<WikiNode>();
        tempCOLiwl = new Vector<WikiNode>();
        tempCOLlinksB = new Vector<WikiNode>();
        tempCOLcatMembA = new Vector<WikiNode>();
        tempCOLcatMembB = new Vector<WikiNode>();
    }
    public Vector<WikiNode> tempCOLlinksA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLiwl = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLlinksB = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembB = new Vector<WikiNode>();

    public void run() {
        try {

            // load data from WIKIPEDIA-API ... 
            System.out.println(">>> Crawlmode=" + crawlMode);
            if (crawlMode) {
                extractCorpusInfos(centerPage);
            }

            FileWriter fw = RepresentationPlotBubbleChart.getWriter( studie );
            
            // load corpus-file and show simple group statistics ...
            PageCorpusAnalyser.runPerPageCorpusTextAnalysis(centerPage.wiki, centerPage.page, studie, fwResults, fileMode, "", new CNResultManager2(), 1, fw);
            
            String listFile = PageCorpusAnalyser.getListFile();
                    
            fw.close();
            
            
            System.out.println(">>>(1) LISTFILE : " + listFile);

            // extract TS on the cluster
            if (runOnCluster) {
                System.out.println("1.) Copy listfile to cluster ...");
                SimpleClusterConnector.uploadFile(Corpus.getListfilePath(), listFile, "/");

                System.out.println("2.) Run Extract-JOB on the cluster ...");
                String out = SimpleClusterConnector.runExtraction(listFile, pattern, centerPage.wiki, centerPage.page, studie);

                System.out.println("3.) Copy resultfolder from cluster ...");
                SimpleClusterConnector.downloadFile("/user/kamir/wikipedia/corpus/" + out, "/user/kamir/wikipedia/corpus/");

            }

            // 
            if (showAnalyseFrame) {
                System.out.println("4.) Start local TS-Analysis-Tool ...");

                String[] args2 = null;
                
//                SequenceFileExplorer.main(args2);
            }



        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mergedClusterRun(String fn, String _studie) {
        if (runOnCluster && _mergedMode) {
            try {
                
                String the_corpus_listfile_pfad = Corpus.getListfilePath();

                System.out.println("1.) Copy listfile to cluster ...");
                System.out.println("    listfile in path : " + the_corpus_listfile_pfad);


                SimpleClusterConnector.uploadFile(the_corpus_listfile_pfad, fn, "/");

                pattern = javax.swing.JOptionPane.showInputDialog("pattern=", pattern);
                String ext = javax.swing.JOptionPane.showInputDialog("ext=", "a");

                System.out.println("2.) Run Extract-JOB on the cluster ...");
                String out = SimpleClusterConnector.runExtraction2(fn, pattern, _studie, ext);

//                System.out.println("3.) Copy resultfolder from cluster ...");
//                SimpleClusterConnector.downloadFile("/user/kamir/wikipedia/corpus/" + out, "/user/kamir/wikipedia/corpus/");

                System.out.println("4.) Start local TS-Analysis-Tool ...");

                /**
                 * CMD : /usr/bin/hadoop jar store/EXTS4Corpus.jar INPUT :
                 * /user/kamir/wikipedia/raw/2007/2007-12/page* PUTPUT :
                 * /user/kamir/wikipedia/corpus/CCAA_a_merged LIST :
                 * merged_listfile_CCAA.lst
                 */
                String[] args2 = new String[5];
                args2[0] = _studie;
                args2[1] = Corpus.getListfilePath() + "/" + fn;
                args2[2] = out;
                args2[3] = ext;
                args2[4] = pattern;

                WikipediaCorpusLoaderTool.setArgs(args2);

                // SequenceFileExplorer.main(args2);

            } catch (IOException ex) {
                Logger.getLogger(ExtractIWLinkCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    WikiNode centerPage = null;
    static boolean withText = false;
    /**
     * Load Corpus Data from WEB ...
     *
     * @param wn
     * @throws IOException
     *
     */
    static MyNetworkStreamMode mns = null;

    public void extractCorpusInfos(WikiNode wn) throws IOException {

        mns = MyNetworkStreamMode.getMyNetworkStreamMode(studie);

        Corpus corpus = new Corpus( studie );

        int count = 0;
        int countERR = 0;
        
        String netn_ = wn.page;
        
        /**
         * 
         * We have to encode the slash and the apostrophes
         * 
         */
        netn_ = FileNameFilter.cleanWikinameForFilename( netn_ );

        // Datei zum Speichern des CORPUS
        String fname = "iwl_corpus_" + studie + "_" + wn.wiki + "_" + netn_;
        String fn = fname + ".dat";

        String netn = "net." + fname + ".tab.csv";

        Vector<String> v = new Vector<String>();

        // GLOBAL statefull network stream data handler ...

        mns.nextCN(); // count on ...
        mns.init(netn);

        int wrong = 0;
        int sum = 0;

        System.out.println("\n>[PAGE] CN: " + wn.page + "\n");

        try {

            // 
            MyLink2 linkCN = new MyLink2();
            linkCN.source = wn.page;
            linkCN.wikiSRC = wn.wiki;

            String a[] = new String[2];
            a[0] = wn.wiki;
            a[1] = wn.page;

//             (LN = A.L)
//             lade direkte Links 
            tempCOLlinksA = getLinksVector(wn);
            for (WikiNode wnnA : tempCOLlinksA) {
                MyLink2 Al = linkCN.clone();
                Al.wikiDEST = wnnA.wiki;
                Al.dest = wnnA.page;
                Al.iwl = 0;
                Al.direct = 1;
                mns.addLink(Al);
            }

//            // lade InterwikiLinks 
            tempCOLiwl = getInterWikiLinksVector(wn);
            tempCOLcatMembA = getCatMembers(wn);

            for (WikiNode wnn : tempCOLiwl) {
                MyLink2 iwl = linkCN.clone();
                iwl.wikiDEST = wnn.wiki;
                iwl.dest = wnn.page;
                iwl.iwl = 1;
                iwl.direct = 0;
                mns.addLink(iwl);
            }

            // lade Links zu den Interwikilinks gelinkten
            for (WikiNode iwlCN : tempCOLiwl) {
                MyLink2 linkCN2 = new MyLink2();
                linkCN2.source = iwlCN.page;
                linkCN2.wikiSRC = iwlCN.wiki;
                final Vector<WikiNode> linksVector = getLinksVector(iwlCN);
                for (WikiNode wnn : linksVector) {
                    MyLink2 Bl = linkCN2.clone();
                    Bl.wikiDEST = wnn.wiki;
                    Bl.dest = wnn.page;
                    Bl.iwl = 0;
                    Bl.direct = 1;
                    mns.addLink(Bl);
                }

                tempCOLlinksB.addAll(linksVector);
                tempCOLcatMembB.addAll(getCatMembers(iwlCN));
            }

            PageInfoView2 piv = new PageInfoView2();
            piv.open(wn, this);
            piv.initContent();


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        corpus._addWikiNodes(tempCOLlinksA, "A.L");
        corpus._addWikiNodes(tempCOLlinksB, "B.L");
//        
        corpus._addWikiNodes(tempCOLiwl, "IWL");
        
        corpus.addWikiNode(wn, "CN");
        
        
        //Wiki2 wikiCN = new Wiki2(wn.wiki + ".wikipedia.org");
        WikiORIGINAL wikiCN = getWikiForLang( wn.wiki );
        
        try {
            
            int vol = getPageSize(wikiCN, wn.page);
            if( wn != null ) wn.pageVolume = vol;
            
        }
        catch (Exception ex) { 
            System.err.println("PROBLEM in wiki page:" + wn.wiki + ".wikipedia.org");    
            System.err.println( ex.getCause() );                
        }    
        

        System.out.println("#****************************");
        System.out.println("# Loading pages now ...");
        System.out.println("#****************************");

        try {

            if (netn.contains("/")) {
                netn = netn.replaceAll("/", "_");
            }
            
            Corpus.storeCorpus(corpus, fn, Corpus.mode_XML);
            
        } catch (Exception ex) {
            System.out.println("###  " + ex.getCause());
        }
        System.out.println("*** DONE ***");

    }

    public static String getUrl(String wiki, String page) {
        return "http://" + wiki + ".wikipedia.org/wiki/" + page;
    }

    public static String getHTML(WikiNode wn) throws IOException {
        String n = "";
        // Wiki2 wiki1 = new Wiki2(wn.wiki + ".wikipedia.org");
        WikiORIGINAL wiki1 = getWikiForLang( wn.wiki );

        try { 
            n = wiki1.getPageText(wn.page);
        }
        catch( Exception ex) { 
            System.err.println( ex.getCause() );
        }
        return n;
        
    }
    
    public static WikiORIGINAL getWikiForLang( String lang ) {
        WikiORIGINAL wiki = new WikiORIGINAL( lang + ".wikipedia.org" );
        return wiki;
    }

    boolean debug = true;
    private Vector<WikiNode> getLinksVector(WikiNode wn) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        
        // Wiki2 wiki1 = new Wiki2(wn.wiki + ".wikipedia.org");
        WikiORIGINAL wiki1 = getWikiForLang( wn.wiki );

        
        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        String[] n = wiki1.getLinksOnPage(wn.page);

        if (debug) {
            for (String l : n) {
                System.out.println("###" + l + "###");
            }
            // System.exit(0);
        };

        for (String s : n) {
            WikiNode wn2 = new WikiNode(wn.wiki, s);

            int vol = getPageSize(wiki1, s);
            wn2.pageVolume = vol;

            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private Vector<WikiNode> getInterWikiLinksVector(WikiNode wn) throws IOException, Exception {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        //Wiki2.debug = true;
        // Wiki2 wiki1 = new Wiki2(wn.wiki + ".wikipedia.org");
        WikiORIGINAL wiki1 = getWikiForLang( wn.wiki );
        
        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        HashMap<String, String> map = wiki1.getInterWikiLinks(wn.page);

        for (String k : map.keySet()) {
            WikiNode wn2 = new WikiNode(k, map.get(k));
            // VOL
            int vol = getPageSize(wiki1, wn2.page);
            wn2.pageVolume = vol;
            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private Vector<WikiNode> getCatMembers(WikiNode wn) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        //Wiki2 wiki1 = new Wiki2(wn.wiki + ".wikipedia.org");
        WikiORIGINAL wiki1 = getWikiForLang( wn.wiki );

        System.out.println("\n>[PAGE] : " + wn.page + "\n");
        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);

        String[] n = wiki1.getCategories(wn.page);

        for (String s : n) {
            WikiNode wn2 = new WikiNode(wn.wiki, s);

            int vol = getPageSize(wiki1, wn2.page);
            wn2.pageVolume = vol;

            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private int getPageSize(WikiORIGINAL wiki, String p) {
        HashMap<String, Object> t;
        Integer vol = 0;
        try {
            if (!p.contains(">")) {
                t = wiki.getPageInfo(p);

                System.out.println("<..." + p + " ...> ");

                if (t != null) {
                    System.out.println("<<  " + p + " ...> ");
                    vol = (Integer) t.get("size");
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage() + " : " + ex.getCause());
            vol = 0;
        }
        return vol;
    }
}
