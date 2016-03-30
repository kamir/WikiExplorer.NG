package m3.wikipedia.corpus.extractor.iwl;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
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

import wikiapiclient.WikiORIGINAL;
import wikiapiclient.WikiORIGINAL.Revision;

 
import m3.wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import m3.wikipedia.explorer.gui.PageInfoView2;
import m3.wikipedia.explorer.data.WikiNode;
import hadoop.cluster.connector.SimpleClusterConnector;
import java.io.BufferedWriter;
import java.io.File;
import org.crunchts.store.TSB;
import org.openide.util.Exceptions;
import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;
import m3.wikipedia.corpus.extractor.*;
import m3.wikipedia.analysis.charts.RepresentationPlotBubbleChart; 

/**
 * Extract a Wikipedia-Corpus for Interwiki-Linked data for a certain central
 * page CN and all linked pages with link depth ld=2 (secondary neighbours).
 *
 * @author root
 */
public class ExtractIWLinkAdvanced implements Runnable {
  
    public static String studie = null;
    
    public static String[] wiki = null;
    public static String[] page = null;

    /**
     * 
     * Here we prepare one thread for each CN to run parallel ...
     * 
     * @param _studie
     * @param _wiki
     * @param _page
     * @param folderInTSB
     * @throws IOException 
     */
    public static void runFromGUITool(String _studie, 
            String[] _wiki, String[] _page ) throws IOException {

        studie = _studie;
        wiki = _wiki;
        page = _page;
        
        /**
         *
         * all Einträge der Liste abarbeiten ... für jede CN wird ein eigener
         * Corpus geladen.
         *
         *
         */
        ExtractIWLinkAdvanced tool = null;
        for (int i = 0; i < wiki.length; i++) {

            String w = wiki[i];
            String p = page[i];

            // define a center page to start data retrieval procedure ...
            WikiNode cp = new WikiNode(w, p);
            
            // create the extraction tool ...
            tool = new ExtractIWLinkAdvanced();
            tool.centerPage = cp;
            
            tool.run();

            System.out.println(" ***** ");
            System.out.println(" {" + cp.toString() + "} is done.");
            System.out.println(" ***** ");

        }
    }
    
    MyNetworkStreamMode mns = null;

    int c = 0;
    @Override
    public void run() {

        FileWriter fw = null;
        try {
            
        
            fw = new FileWriter( new File("./NET_check_"+studie+".dat"));
            BufferedWriter bwr = new BufferedWriter( fw );
            HaeufigkeitsZaehlerDoubleSIMPLE hz = new HaeufigkeitsZaehlerDoubleSIMPLE();
            hz.min = 0.0;hz.max = 1000.0;
            hz.intervalle = 50;
            System.out.println(">>> Create NetworkStreamMode ... " + studie + "_" + centerPage.getKeySAFEFILENAME() );
            mns = MyNetworkStreamMode.getMyNetworkStreamMode(studie + "_" + centerPage.getKeySAFEFILENAME() );
            try {
                
                
                // GLOBAL statefull network stream data handler ...
                int wrong = 0;
                int sum = 0;
                
                System.out.println("\n>[PAGE] CN: " + centerPage.getKeySAFEFILENAME() + "\n");
                mns.nextCN(); // count on ...
                mns.init(studie + "_" + centerPage.getKeySAFEFILENAME() , "pagelink.network.secondary_links");
                
                
                int count = 0;
                int countERR = 0;
                
                
                
                Vector<String> v = new Vector<String>();
                
                int k = 1;
                try {
                    
                    //
                    MyLink2 linkCN = new MyLink2();
                    linkCN.source = centerPage.page;
                    linkCN.wikiSRC = centerPage.wiki;
                    
                    String a[] = new String[2];
                    a[0] = centerPage.wiki;
                    a[1] = centerPage.page;
                    
                    
                    
//             (LN = A.L)
//             lade direkte Links
                    tempCOLlinksA = getLinksVector(centerPage, hz);
                    
                    for (WikiNode wnnA : tempCOLlinksA) {
                        MyLink2 Al = linkCN.clone();
                        Al.wikiDEST = wnnA.wiki;
                        Al.dest = wnnA.page;
                        Al.iwl = 0;
                        Al.direct = 1;
                        mns.addLink(Al, k);
                        writeLink( Al, 1, bwr );
                                }
                    
//            // lade InterwikiLinks
                    tempCOLiwl = getInterWikiLinksVector(centerPage);
                    
                    // tempCOLcatMembA = getCatMembers(centerPage);
                    
                    for (WikiNode wnn : tempCOLiwl) {
                        MyLink2 iwl = linkCN.clone();
                        iwl.wikiDEST = wnn.wiki;
                        iwl.dest = wnn.page;
                        iwl.iwl = 1;
                        iwl.direct = 0;
                        mns.addLink(iwl,0);
                        writeLink( iwl, 0, bwr );
                    }
                    
                    // lade Links zu den Interwikilinks gelinkten
                    for (WikiNode iwlCN : tempCOLiwl) {
                        MyLink2 linkCN2 = new MyLink2();
                        linkCN2.source = iwlCN.page;
                        linkCN2.wikiSRC = iwlCN.wiki;
                        
                        final Vector<WikiNode> linksVector = getLinksVector(iwlCN, hz);
                        for (WikiNode wnn : linksVector) {
                            MyLink2 Bl = linkCN2.clone();
                            Bl.wikiDEST = wnn.wiki;
                            Bl.dest = wnn.page;
                            Bl.iwl = 0;
                            Bl.direct = 1;
                            mns.addLink(Bl,1);
                            writeLink( Bl, 1, bwr );
                        }
                        
                        tempCOLlinksB.addAll(linksVector);
                        // tempCOLcatMembB.addAll(getCatMembers(iwlCN));
                    }
                    
                    System.out.println( ">>> " + tempCOLlinksA + " more requests.");
                    System.out.println( ">>> " + tempCOLlinksB + " more requests.");
                    
                    // lade Links zur Gruppe AL ...
                    for (WikiNode wnA : tempCOLlinksA) {
                        MyLink2 linkCN2 = new MyLink2();
                        linkCN2.source = wnA.page;
                        linkCN2.wikiSRC = wnA.wiki;
                        final Vector<WikiNode> linksVector = getLinksVector(wnA, hz);
                        for (WikiNode wnn : linksVector) {
                            MyLink2 Bl = linkCN2.clone();
                            Bl.wikiDEST = wnn.wiki;
                            Bl.dest = wnn.page;
                            Bl.iwl = 0;
                            Bl.direct = 1;
                            mns.addLink(Bl,2);
                            writeLink( Bl, 2, bwr );
                        }
                        
                        tempCOLlinksA2.addAll(linksVector);
                        //tempCOLcatMembB.addAll(getCatMembers(wnA));
                    }
                    
                    // lade Links zur Gruppe BL ...
                    for (WikiNode wnB : tempCOLlinksB) {
                        MyLink2 linkCN2 = new MyLink2();
                        linkCN2.source = wnB.page;
                        linkCN2.wikiSRC = wnB.wiki;
                        final Vector<WikiNode> linksVector = getLinksVector(wnB, hz);
                        for (WikiNode wnn : linksVector) {
                            MyLink2 Bl = linkCN2.clone();
                            Bl.wikiDEST = wnn.wiki;
                            Bl.dest = wnn.page;
                            Bl.iwl = 0;
                            Bl.direct = 1;
                            mns.addLink(Bl,2);
                            writeLink( Bl, 2, bwr );
                        }
                        
                        tempCOLlinksB2.addAll(linksVector);
                        //tempCOLcatMembB.addAll(getCatMembers(wnA));
                    }
                    
                    
//            PageInfoView2 piv = new PageInfoView2();
//            piv.open(centerPage, this);
//            piv.initContent();
                    
                    hz.calcWS();
                    Messreihe mr = hz.getHistogram();
                    Vector<Messreihe> vmr = new Vector<Messreihe>();
                    vmr.add(mr);
                    MultiChart.open( vmr );
                    
                    System.out.println("ERRORS: " + ERRORS);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                bwr.close();
                
                
                mns.close();
                
            }
            catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
//      

        
        } 
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
//      
        
    }

    // used in a particular thread ...
    
    //public FileWriter fwNetwork = null;
    //public int fileMode;
    
    
    WikiNode centerPage = null;
    
    public ExtractIWLinkAdvanced() {
        tempCOLlinksA = new Vector<WikiNode>();
        tempCOLiwl = new Vector<WikiNode>();
        tempCOLlinksB = new Vector<WikiNode>();
        tempCOLcatMembA = new Vector<WikiNode>();
        tempCOLcatMembB = new Vector<WikiNode>();
        tempCOLlinksA2 = new Vector<WikiNode>();
        tempCOLlinksB2 = new Vector<WikiNode>();
    }
    
    public Vector<WikiNode> tempCOLlinksA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLiwl = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLlinksB = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembA = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLcatMembB = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLlinksB2 = new Vector<WikiNode>();
    public Vector<WikiNode> tempCOLlinksA2 = new Vector<WikiNode>();


 
//
//    public void extractCorpusInfos(WikiNode wn) throws IOException {
//
//        mns = MyNetworkStreamMode.getMyNetworkStreamMode(studie);
//
//        Corpus corpus = new Corpus( studie );
//
//        int count = 0;
//        int countERR = 0;
//        
//        String netn_ = wn.page;
//        
//        /**
//         * 
//         * We have to encode the slash and the apostrophes
//         * 
//         */
//        netn_ = FileNameFilter.cleanWikinameForFilename( netn_ );
//
//        // Datei zum Speichern des CORPUS
//        String fname = "iwl_corpus_" + studie + "_" + wn.wiki + "_" + netn_;
//        String fn = fname + ".dat";
//
//        String netn = "net." + fname + ".tab.csv";
//
//        Vector<String> v = new Vector<String>();
//
//        // GLOBAL statefull network stream data handler ...
//
//        mns.nextCN(); // count on ...
//        mns.init(netn);
//
//        int wrong = 0;
//        int sum = 0;
//
//        System.out.println("\n>[PAGE] CN: " + wn.page + "\n");
//
//        try {
//
//            // 
//            MyLink2 linkCN = new MyLink2();
//            linkCN.source = wn.page;
//            linkCN.wikiSRC = wn.wiki;
//
//            String a[] = new String[2];
//            a[0] = wn.wiki;
//            a[1] = wn.page;
//
////             (LN = A.L)
////             lade direkte Links 
//            tempCOLlinksA = getLinksVector(wn);
//            for (WikiNode wnnA : tempCOLlinksA) {
//                MyLink2 Al = linkCN.clone();
//                Al.wikiDEST = wnnA.wiki;
//                Al.dest = wnnA.page;
//                Al.iwl = 0;
//                Al.direct = 1;
//                mns.addLink(Al);
//            }
//
////            // lade InterwikiLinks 
//            tempCOLiwl = getInterWikiLinksVector(wn);
//            tempCOLcatMembA = getCatMembers(wn);
//
//            for (WikiNode wnn : tempCOLiwl) {
//                MyLink2 iwl = linkCN.clone();
//                iwl.wikiDEST = wnn.wiki;
//                iwl.dest = wnn.page;
//                iwl.iwl = 1;
//                iwl.direct = 0;
//                mns.addLink(iwl);
//            }
//
//            // lade Links zu den Interwikilinks gelinkten
//            for (WikiNode iwlCN : tempCOLiwl) {
//                MyLink2 linkCN2 = new MyLink2();
//                linkCN2.source = iwlCN.page;
//                linkCN2.wikiSRC = iwlCN.wiki;
//                final Vector<WikiNode> linksVector = getLinksVector(iwlCN);
//                for (WikiNode wnn : linksVector) {
//                    MyLink2 Bl = linkCN2.clone();
//                    Bl.wikiDEST = wnn.wiki;
//                    Bl.dest = wnn.page;
//                    Bl.iwl = 0;
//                    Bl.direct = 1;
//                    mns.addLink(Bl);
//                }
//
//                tempCOLlinksB.addAll(linksVector);
//                tempCOLcatMembB.addAll(getCatMembers(iwlCN));
//            }
//
//            PageInfoView2 piv = new PageInfoView2();
//            piv.open(wn, this);
//            piv.initContent();
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        corpus._addWikiNodes(tempCOLlinksA, "A.L");
//        corpus._addWikiNodes(tempCOLlinksB, "B.L");
////        
//        corpus._addWikiNodes(tempCOLiwl, "IWL");
//        
//        corpus.addWikiNode(wn, "CN");
//        
//        
//        Wiki wikiCN = new Wiki(wn.wiki + ".wikipedia.org");
//        try {
//            int vol = getPageSize(wikiCN, wn.page);
//            if( wn != null ) wn.pageVolume = vol;
//        }
//        catch (Exception ex) { 
//            System.err.println("PROBLEM in wiki page:" + wn.wiki + ".wikipedia.org");    
//            System.err.println( ex.getCause() );                
//        }    
//        
//
//        System.out.println("#****************************");
//        System.out.println("# Loading pages now ...");
//        System.out.println("#****************************");
//
//        try {
//
//            if (netn.contains("/")) {
//                netn = netn.replaceAll("/", "_");
//            }
//            Corpus.storeCorpus(corpus, fn, Corpus.mode_XML);
//        } catch (Exception ex) {
//            System.out.println("###  " + ex.getCause());
//        }
//        System.out.println("*** DONE ***");
//
//    }

    public static String getUrl(String wiki, String page) {
        return "http://" + wiki + ".wikipedia.org/wiki/" + page;
    }

//    public static String getHTML(WikiNode wn) throws IOException {
//        String n = "";
//        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");
//        try { 
//            n = wiki1.getPageText(wn.page);
//        }
//        catch( Exception ex) { 
//            System.err.println( ex.getCause() );
//        }
//        return n;
//        
//    }

    
    private Vector<WikiNode> getLinksVector(WikiNode wn, HaeufigkeitsZaehlerDoubleSIMPLE hz) throws IOException {
        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

        WikiORIGINAL wiki1 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");

        System.out.println("\n> " + c + " [PAGE] : " + wn.page + "\n");
        String url = getUrl(wn.wiki, wn.page);

        try {
            String[] n = wiki1.getLinksOnPage(wn.page);

            hz.addData( 1.0 * n.length );

            c++; // update the request counter ... 

            for (String s : n) {
                WikiNode wn2 = new WikiNode(wn.wiki, s);

                wn2.pageVolume = -1;

                linkedNodes.add(wn2);
            }
        }
        catch(Exception ex) {
            ERRORS++;
        }

        return linkedNodes;
    }

    int ERRORS = 0;
    /**
     * Get all InterWiki linked nodes and page size ...
     * 
     * @param wn
     * @return
     * @throws IOException
     * @throws Exception 
     */
    public static Vector<WikiNode> getInterWikiLinksVector(WikiNode wn) throws IOException, Exception {

        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();

                        WikiORIGINAL wiki1 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");
                WikiORIGINAL w2 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");
                
                System.out.print("\n>[PAGE] : " + wn.page + "\n");

                String text = wiki1.getPageText(wn.page) + "\n";
                
                HashMap<String,String> iwl = w2.getInterWikiLinks(wn.page);

        
        
        
      
        
        
        
        
        for (String k : iwl.keySet()) {
            WikiNode wn2 = new WikiNode(k, iwl.get(k));
            // VOL
            
            //int vol = getPageSize(wiki1, wn2.page);
            wn2.pageVolume = -1;
            
            linkedNodes.add(wn2);
        }

        return linkedNodes;
    }

    private void writeLink(MyLink2 Bl, int i, BufferedWriter bwr) {
        String line = Bl.source + "\t" + Bl.dest + "\t" + Bl.wikiSRC + "\t" + Bl.wikiDEST + "\t" + i + "\n";
        try {
            bwr.write(line);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

//    private Vector<WikiNode> getCatMembers(WikiNode wn) throws IOException {
//        Vector<WikiNode> linkedNodes = new Vector<WikiNode>();
//
//        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");
//
//
//        System.out.println("\n>[PAGE] : " + wn.page + "\n");
//        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);
//
//        String[] n = wiki1.getCategories(wn.page);
//
//        for (String s : n) {
//            WikiNode wn2 = new WikiNode(wn.wiki, s);
//
//            int vol = getPageSize(wiki1, wn2.page);
//            wn2.pageVolume = vol;
//
//            linkedNodes.add(wn2);
//        }
//
//        return linkedNodes;
//    }

    /**
     * From the getPageInfo( ) call we receive the details about the 
     * page volume.
     * 
     * @param wiki
     * @param p
     * @return 
     */
//    private static int getPageSize(Wiki wiki, String p) {
//        HashMap<String, Object> t;
//        Integer vol = 0;
//        try {
//            if (!p.contains(">")) {
//       
//                t = wiki.getPageInfo(p);
//
//                System.out.println("<..." + p + "...>");
//
//                if (t != null) {
//                    System.out.println("<<..." + p + "...>>");
//                    vol = (Integer) t.get("size");
//                }
//            }
//        } catch (Exception ex) {
//            System.err.println(ex.getMessage() + " : " + ex.getCause());
//            vol = 0;
//        }
//        return vol;
//    }


}
