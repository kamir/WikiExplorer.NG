package m3.wikipedia.corpus.extractor;

import com.cloudera.wikipedia.explorer.ResultManager;
import com.itextpdf.text.PageSize;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JTextArea;
import org.apache.hadoopts.hadoopts.topics.wikipedia.LocalWikipediaNetwork2;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import m3.util.WikiToolHelper;
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import research.ETH.ExtendedNodePairSFE;
import experiments.crosscorrelation.KreuzKorrelation;
import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.labelAdjust.LabelAdjust;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.plugin.NodeColorTransformer;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractColorTransformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Exceptions;


/**
 * Wir laden hier alle Gruppen und ermittlen sowohl die tatsächliche als auch
 * die theoretische LINK-Anzahl um die LINK Dichte zu messen.
 *
 *
 * @author kamir
 */
public class NetDensityCalc implements Runnable {

    private LocalWikipediaNetwork2 network = null;

    String BASE = "/__A__/networks/gephi_";

    public NetDensityCalc() {

    }

    public NetDensityCalc(String BASEFOLDER) {
        BASE = BASEFOLDER;
    }

    boolean verbose = false;

    public NetDensityCalc(WikiNode wn, boolean verbose) {
        this.wn = wn;
        this.verbose = verbose;
    }

    String version = "v4";

    static String[] pages = {"Gaels", "Surströmming", "Sorben", "Joachim_Gauck", "Stollberg/Erzgeb."}; //, "Illuminati_(Buch)" , "Sulingen", "Amoklauf_von_Erfurt"  }; // , "Stollberg"}; // {"Daimler_AG", "Sulingen", "Bad Harzburg"}; // , "Fritiof_Nilsson_Piraten"};
    static String[] wikis = {"en", "sv", "de", "de", "de"};//

    int i = 0;
    Hashtable<Integer, WikiNode> CN = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> IWL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> AL = new Hashtable<Integer, WikiNode>();
    Hashtable<Integer, WikiNode> BL = new Hashtable<Integer, WikiNode>();
    boolean _useBacklinks = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        NetDensityCalc nc = new NetDensityCalc();

//        nc.doit(true, true);
        int z; //= Integer.parseInt( javax.swing.JOptionPane.showInputDialog("Index: ") );

        z = 1;

        boolean useBackLinks = false;

        boolean threads = false;
        nc._doit(threads, useBackLinks, z);
    }

    int[] A = null;
    int[] B = null;
    int[] I = null;
    String n = "\n";

//    public void doit(WikiNode wn) {
//        pages[0] = wn.page;
//        wikis[0] = wn.wiki;
//        try {
//            doit(false, false); // no threads ... no backlinks
//        } catch (IOException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FailedLoginException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    public void doit(WikiNode wn, boolean thread, boolean back) {
//        try {
//            doit(thread, back); // no threads ... no backlinks
//        } catch (IOException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FailedLoginException ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public void _doit(boolean useThreads, boolean useBacklinksss) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        int i = 0;

        for (int ip = 0; ip < pages.length; ip++) {
            String pn = pages[ip];
//            for (String w : wikis) {
            String w = wikis[ip];
            i++;

            WikiNode wn = new WikiNode(w, pn);

            NetDensityCalc ndc2 = new NetDensityCalc(wn, false);
            ndc2._useBacklinks = useBacklinksss;
            if (useThreads) {
                Thread tr = new Thread(ndc2);
                tr.start();
            } else {
                ndc2._grabData(wn);
            }
//            }
        }
    }

    public void _doit(boolean useThreads, boolean useBacklinksss, int ip) throws IOException, ClassNotFoundException, FailedLoginException, Exception {

        String pn = pages[ip];
        String w = wikis[ip];
        i++;

        WikiNode wn = new WikiNode(w, pn);

        NetDensityCalc ndc2 = new NetDensityCalc(wn, false);
        ndc2._useBacklinks = useBacklinksss;
        if (useThreads) {
            Thread tr = new Thread(ndc2);
            tr.start();
        } else {
            ndc2._grabData(wn);
        }
    }

    double rhoCORE = 0.0;
    double rhoAL = 0.0;
    double rhoBL = 0.0;

    public void loadCore(String wikipedia, String pn, BufferedWriter fw) throws IOException, Exception {

        i = 0;
        Wiki wiki = new Wiki(wikipedia + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

        HashMap<String, String> map = wiki.getInterWikiLinks(pn);

        System.out.println("> # of interwiki links: " + map.size());

        int SUMIWL = 0;

        SUMIWL = map.size();

        WikiNode cnNODE = new WikiNode(wikipedia, pn);

        int j = 0;
        for (String key : map.keySet()) {

            String pnIWL = (String) map.get(key);

            System.out.println(j + " : " + key + " ---> " + pnIWL);

            Wiki wikiIWL = new Wiki(key + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            WikiNode iwlNODE = new WikiNode(key, pnIWL);

            i++;
            IWL.put(i, iwlNODE);

            ExtendedNodePairSFE l = new ExtendedNodePairSFE(cnNODE.getKey(), iwlNODE.getKey(), "IWL");
            System.out.println(l.getStaticLinkLine());
            enps.add(l);

            j++;

            HashMap<String, String> map2 = wikiIWL.getInterWikiLinks(pnIWL);
            SUMIWL = SUMIWL + map2.size();

            for (String key22 : map2.keySet()) {
                i++;

                WikiNode il_B_NODE = new WikiNode(key22, map2.get(key22));

                ExtendedNodePairSFE l2 = new ExtendedNodePairSFE(iwlNODE.getKey(), il_B_NODE.getKey(), "IWLB");
                System.out.println(l2.getStaticLinkLine());
                enps.add(l2);

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

        if (fw != null) {
            fw.write(wikipedia + "\t" + pn + "\t" + n + "\t" + z + "\t" + SUMIWL + "\t" + df.format(rho));
            fw.newLine();
            fw.flush();
        } else {
            System.out.println(wikipedia + "\t" + pn + "\t" + n + "\t" + z + "\t" + SUMIWL + "\t" + df.format(rho));
        }

        System.out.flush();
        System.out.println("=================================\n\n\n");

    }

    private static boolean isLinkInLangsAvailable(String link, String[] langs, JTextArea b) throws IOException {
        boolean bo = true;
        for (String l : langs) {
            Wiki wiki = new Wiki(l + ".wikipedia.org");

            link = WikiToolHelper.ignoreUncleanPageNames(link);

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

    private static void lookupRevisions(String pn, Wiki wiki) throws IOException {
        Wiki.Revision[] revs = wiki.getPageHistory(pn);
        int j = 0;
        if (revs != null) {
            Calendar calFIRST = null;
            int z = 0;
            for (Wiki.Revision r : revs) {
                z++;
                System.out.println("\t" + z + ")" + r.getTimestamp().getTime());
                Calendar cal = r.getTimestamp();
                if (calFIRST == null) {
                    calFIRST = cal;
                } else {
                    if (cal.before(calFIRST)) {
                        calFIRST = cal;
                    }
                }
            }
            j++;

        }
        Wiki.Revision r = wiki.getFirstRevision(pn);
        System.out.println("*****" + r.getTimestamp().getTime());

    }

    private void loadLocalNeighbors() throws IOException {

        for (WikiNode n : CN.values()) {

            Wiki wiki = new Wiki(n.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

            String[] map = wiki.getLinksOnPage(n.page);

            for (String key : map) {
                i++;

                WikiNode alNODE = new WikiNode(n.wiki, key);
                AL.put(i, alNODE);

                ExtendedNodePairSFE l = new ExtendedNodePairSFE(n.getKey(), alNODE.getKey(), "AL");
                System.out.println(l.getStaticLinkLine());
                enps.add(l);

            }

            if (_useBacklinks) {
                String[] map2 = wiki.whatLinksHere(n.page);

                for (String key : map2) {

                    i++;
                    WikiNode alNODE = new WikiNode(n.wiki, key);
                    AL.put(i, alNODE);

                    ExtendedNodePairSFE l = new ExtendedNodePairSFE(alNODE.getKey(), n.getKey(), "ALB");
                    System.out.println(l.getStaticLinkLine());
                    enps.add(l);
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
                ExtendedNodePairSFE l = new ExtendedNodePairSFE(n.getKey(), blNODE.getKey(), "BL");
                System.out.println(l.getStaticLinkLine());
                enps.add(l);
            }

            if (_useBacklinks) {
                String[] map2 = wiki.whatLinksHere(n.page);
                for (String key : map2) {
                    i++;
                    WikiNode blNODE = new WikiNode(n.wiki, key);
                    BL.put(i, new WikiNode(n.wiki, key));
                    ExtendedNodePairSFE l = new ExtendedNodePairSFE(blNODE.getKey(), n.getKey(), "BLB");
                    System.out.println(l.getStaticLinkLine());
                    enps.add(l);
                }
            }
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

    public void _grabData(WikiNode wn) throws Exception {

        CN = new Hashtable<Integer, WikiNode>();
        IWL = new Hashtable<Integer, WikiNode>();
        AL = new Hashtable<Integer, WikiNode>();
        BL = new Hashtable<Integer, WikiNode>();

        try {
            bwNL = new BufferedWriter(new FileWriter(new File("/Volumes/MyExternalDrive/CALCULATIONS/networks/" + wn.getKeySAFEFILENAME() + "." + _useBacklinks + "." + version + ".NET.log.csv")));

            bwNL.write("# \n");
            bwNL.write("# \n");
            bwNL.write("# \n");
            bwNL.write("# \n");
            CN.put(i, wn);

//                IWL.put( i , wn );
//                AL.put( i , wn );
//                BL.put( i , wn );
            loadCore(wn.wiki, wn.page, null);

            loadLocalNeighbors();
            loadGlobalNeighbors();

            calcTheorieValues(bwNL);
            calcRatios();

            storeStaticNetAsCSV(new BufferedWriter(new FileWriter(new File("/Volumes/MyExternalDrive/CALCULATIONS/networks/" + wn.getKeySAFEFILENAME() + "." + _useBacklinks + "." + version + ".stat.net.csv"))));

            String fn = "/Volumes/MyExternalDrive/CALCULATIONS/networks/" + wn.getKeySAFEFILENAME() + "." + _useBacklinks + "." + version + ".stat.net";

//            storeStaticNetAsGEPHIFile(new BufferedWriter(new FileWriter(new File(fn + ".gexf"))), fn);
//            double k[][] = calcAverageDegree();
//
//            A = getIntraGroupDENS(AL);
//            B = getIntraGroupDENS(BL);
//            rhoAL = (double) A[0] / (double) A[1] * (A[1] - 1);
//            rhoBL = (double) B[0] / (double) B[1] * (B[1] - 1);
//            StringBuffer sb = new StringBuffer();
//
//            sb.append("\n\n" + CN.elements().nextElement().getKey());
//            sb.append("\n");
//            sb.append("CN  " + CN.size());
//            sb.append("\n");
//            sb.append("IWL " + IWL.size() + "\t: " + rhoCORE);
//            sb.append("\n");
//            sb.append("AL  " + AL.size() + "\t: " + rhoAL + "\t" + A[0] + "\t" + A[1] + "\t" + A[2]);
//            sb.append("\n");
//            sb.append("BL  " + BL.size() + "\t: " + rhoBL + "\t" + B[0] + "\t" + B[1] + "\t" + B[2]);
//            sb.append("\n");
//
//            DecimalFormat df = new DecimalFormat("0.0000");
//
//            sb.append("\n\n" + CN.elements().nextElement().getKey() + n);
//            sb.append("CN  " + CN.size() + n);
//            sb.append("IWL " + IWL.size() + "\t: rhoCORE=" + rhoCORE + n);
//            sb.append("AL  " + AL.size() + "\t: rhoAL=" + rhoAL + "\t#l_int=" + A[0] + "\t#l_total=" + A[1] + "\t#l_ext=" + A[2] + n);
//            sb.append("BL  " + BL.size() + "\t: rhoBL=" + rhoBL + "\t#l_int=" + B[0] + "\t#l_total" + B[1] + "\t#l_ext=" + B[2] + n);
//
//            sb.append("\nOM  " + OM + "\t: " + df.format(ROM) + n);
//            sb.append("LI  " + LI + "\t: " + df.format(RLI) + n);
//            sb.append("GI  " + GI + "\t: " + df.format(RGI) + n);
//
//            sb.append("\nS1  " + S1 + "\t R1 : " + df.format(R1) + n);
//            sb.append("S2  " + S2 + "\t R2 : " + df.format(R2) + n);
//            sb.append("S3  " + S3 + "\t R3 : " + df.format(R3) + n);
//            sb.append("Sum " + sum + "\t SUM²" + (sum * sum) + "\t : " + S + n);
//            sb.append("\n<k>_internal_IWL " + k[0][0] + n);
//            sb.append("<k>_internal_AL  " + k[1][0] + n);
//            sb.append("<k>_internal_BL  " + k[2][0] + n);
//
//            sb.append("\n<k>_total_IWL " + k[0][1] + n);
//            sb.append("<k>_total_AL  " + k[1][1] + n);
//            sb.append("<k>_total_BL  " + k[2][1] + n);
//
//            sb.append("\n<k>_external_IWL " + k[0][2] + n);
//            sb.append("<k>_external_AL  " + k[1][2] + n);
//            sb.append("<k>_external_BL  " + k[2][2] + n);
//            bwNL.write(sb.toString());
            bwNL.flush();
            bwNL.close();

        } catch (IOException ex) {
            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

        javax.swing.JOptionPane.showMessageDialog(null, wn.getKey() + " ... started!");
        try {

            _grabData(wn);

        } catch (Exception ex) {
            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("[NDC.Process] ... {" + wn.toString() + "} is done ...");
    }

    /**
     * Erstelle die Linkstärken-Datei ...
     *
     * @param label
     * @return
     * @throws IOException
     */
    public BufferedWriter logToCCFile(String label) throws IOException {

        String fn = label + wn.getKey() + ".json";

        File fold = new File("/Volumes/MyExternalDrive/CALCULATIONS/CC/");
        fold.mkdir();

        System.out.println(">>> LOG label        : " + label);
        System.out.println(">>> Output location  : " + fold + " e:" + fold.exists() + " w:" + fold.canWrite());
        System.out.println(">>> CREATE ResultLog : " + fn);

        BufferedWriter bwNL2 = new BufferedWriter(new FileWriter(new File(fn)));
        return bwNL2;

    }

    public void logToAnalysisFile(String data, String label) throws IOException {
        BufferedWriter bwNL2 = new BufferedWriter(new FileWriter(new File("/Volumes/MyExternalDrive/CALCULATIONS/" + wn.getKey() + "." + label + ".csv")));

        bwNL2.write(data);
        bwNL2.close();
    }
    Hashtable<String, Vector> links = new Hashtable<String, Vector>();

    public void _flushNetworks(int runID) {

        String kkType = KreuzKorrelation.getCalcTypeLabel();

        BufferedWriter bwNL2;
        try {

            bwNL2 = new BufferedWriter(new FileWriter(new File(BASE + "_" + kkType + "." + runID + ".tsv")));

            // (s + "\t" + t + "\t" + typ + "\t" + linkid + "\t" + label + "\t" + getLinkA() + "\t" + getLinkB() + "\t" + getLinkC() + "\t" + getLinkD() );
            bwNL2.write("Source\tTarget\tType\tId\tLabel\tWeightA\tWeightB\tWeightC\tWeightD\n");

            for (String key : links.keySet()) {

                int id = 0;
                Vector v = links.get(key);
                for (Object o : v) {
                    id++;
                    ExtendedNodePairSFE l = (ExtendedNodePairSFE) o;
                    String type = key;
                    bwNL2.write(l._toString2(type, id) + "\n");
                };

            }
            bwNL2.close();
        } catch (IOException ex) {
            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void storeStaticNetAsCSV(BufferedWriter bw) throws IOException {

        writeLinksToStream(bw);

    }

    private void storeStaticNetAsGEPHIFile(BufferedWriter bw, String fn) throws Exception {

        BufferedWriter bwNL2;

        int errorsZ = 0;
        int goodZ = 0;

        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Generate a new random graph into a container
        Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel model = ac.getModel();

        AttributeColumn testCol4 = model.getNodeTable().addColumn("nT", AttributeType.STRING);

        Graph undirectedGraph = graphModel.getGraph();

        int lid = addLinksToGraph(CN, bw, "CN", 0, graphModel);
        lid = addLinksToGraph(IWL, bw, "IWL", lid, graphModel);
        lid = addLinksToGraph(AL, bw, "AL", lid, graphModel);
        lid = addLinksToGraph(BL, bw, "BL", lid, graphModel);

        //Export to Writer
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        Exporter exporterGraphML = ec.getExporter("graphml");     //Get GraphML exporter
        exporterGraphML.setWorkspace(workspace);
        StringWriter stringWriter = new StringWriter();
        ec.exportWriter(stringWriter, (CharacterExporter) exporterGraphML);
        bw.write(stringWriter.toString());
//System.out.println(stringWriter.toString());   //Uncomment this line

//PDF Exporter config and export to Byte array
        PDFExporter pdfExporter = (PDFExporter) ec.getExporter("pdf");
        pdfExporter.setPageSize(PageSize.A0);
        pdfExporter.setWorkspace(workspace);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ec.exportStream(baos, pdfExporter);
        byte[] pdf = baos.toByteArray();

        FileOutputStream outs = new FileOutputStream(new File(fn + ".pdf"));
        outs.write(pdf);
        outs.flush();
        outs.close();

        bw.close();

    }

    private void writeLinksToStreamOLD(Hashtable<Integer, WikiNode> CN, BufferedWriter bw, String key) throws IOException {
        for (Integer id : CN.keySet()) {

            // Source ...
            WikiNode wn = CN.get(id);
            bw.write("\"" + wn.getKey() + "\"\t\"");

            // Abruf aktueller Links
            Wiki wiki = new Wiki(wn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            String[] map = wiki.getLinksOnPage(wn.page);

            for (String pn : map) {
                WikiNode wl = new WikiNode(wn.wiki, pn);
                bw.write("\";\"" + wl.getKey());

            }
            bw.write("\"\n");
            bw.flush();

            if (_useBacklinks) {
                String[] map2 = wiki.whatLinksHere(wn.page);
                for (int i = 0; i < map2.length; i++) {
                    WikiNode wl = new WikiNode(map2[0], map2[1]);
                    bw.write(wl.getKey() + ";" + wn.getKey() + "\n");
                }
                bw.flush();
            }
        }
    }

    Vector<ExtendedNodePairSFE> enps = new Vector<ExtendedNodePairSFE>();

    private int writeLinksToStream(BufferedWriter bw) throws IOException {

        int lastId = 0;

        for (ExtendedNodePairSFE enp : enps) {

            String n1 = enp.s;
            String n2 = enp.t;
            String typ = enp.type;

            String linkid = "" + (lastId + 1);

            String label = "(" + n1 + ";" + n2 + ")";
            int weight = 1;

            bw.write(linkid + "\t" + n1 + "\t" + n2 + "\t" + typ + "\t" + label + "\t" + weight + "\n");
            lastId++;

            bw.flush();

        }
        return lastId;
    }

//    private int writeLinksToStreamOLD(Hashtable<Integer, WikiNode> CN, BufferedWriter bw, String key, int lastId) throws IOException {
//        for (Integer id : CN.keySet()) {
//
//            // Source ...
//            WikiNode wn = CN.get(id);
//
//            String n1 = "\"" + wn.getKey() + "\"";
//
//            // Abruf aktueller Links
//            Wiki2 wiki = new Wiki2(wn.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
//            String[] map = wiki.getLinksOnPage(wn.page);
//
//            String s = n1;
////            
//            String typ = "stat";
//
//
//            for (String pn : map) {
//
//
//                WikiNode wl = new WikiNode(wn.wiki, pn);
//                String n2 = "\"" + wl.getKey() + "\"";
//
//                String t = n2;
//
//                String linkid = "" + (lastId + 1);
//
//                String label = "(" + n1 + ";" + n2 + ")";
//                int weight = 1;
//                bw.write(s + "\t" + t + "\t" + typ + "\t" + linkid + "\t" + label + "\t" + weight + "\n");
//                lastId++;
//            }
//            
//            bw.flush();
//
////            if (useBacklinks) {
////                String[] map2 = wiki.whatLinksHere(wn.page);
////                for (int i = 0; i < map2.length; i++) {
////                    WikiNode wl = new WikiNode(map2[0], map2[1]);
////                    bw.write(wl.getKey() + ";" + wn.getKey() + "\n");
////                }
////                bw.flush();
////            }
//
//
//        }
//        return lastId;
//    }
    private int addLinksToGraph(Hashtable<Integer, WikiNode> CN, BufferedWriter bw, String key, int lastId, GraphModel graphModel) throws IOException {

        Graph undirectedGraph = graphModel.getGraph();

        for (Integer id : CN.keySet()) {

            // Source ...
            WikiNode wnSource = CN.get(id);
            String[] map2 = null;

            // Abruf aktueller Links
            Wiki wiki = new Wiki(wnSource.wiki + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org
            String[] map = wiki.getLinksOnPage(wnSource.page);

//            if (useBacklinks) {
//                try { 
//                    map2  = wiki.whatLinksHere(wnSource.page);
//                } 
//                catch(Exception ex) {
//                    ex.printStackTrace();
//                    map2 = null;
//                }    
//            }    
//            else { 
//                System.out.println( "NO BACKLINKS");
//            }
            String source = wnSource.getKey();
            String typ = "stat";

            Node node1 = graphModel.factory().newNode(source);
            node1.getNodeData().setLabel(source);

            node1.getNodeData().getAttributes().setValue("nT", key);
            undirectedGraph.addNode(node1);

            for (String pn : map) {

                WikiNode wl = new WikiNode(wn.wiki, pn);
                String target = wl.getKey();
                lastId++;

                String linkid = "" + lastId;

                String label = "(" + source + ";" + target + ")";
                int weight = 1;

                Node node2 = graphModel.factory().newNode(target);
                node2.getNodeData().setLabel(target);

                node2.getNodeData().getAttributes().setValue("nT", key + ".LINK");
                undirectedGraph.addNode(node2);

                Edge e1 = graphModel.factory().newEdge(node1, node2);

                e1.getEdgeData().getAttributes().setValue("gK", typ);
                e1.getEdgeData().getAttributes().setValue("Id", linkid);

                e1.getEdgeData().getAttributes().setValue("Weight", weight);

                undirectedGraph.addEdge(e1);

            }
//
//            if (useBacklinks && map2 != null) {
//                for (String pn : map2) {
//
//
//                    WikiNode wl = new WikiNode(wn.wiki, pn);
//                    String target = wl.getKey();
//                    lastId++;
//
//                    String linkid = "" + lastId;
//
//                    String label = "(" + source + ";" + target + ")";
//                    int weight = 1;
//
//                    
//                if ( isInternal( wl ) ) {
//
//
//                    Node node2 = graphModel.factory().newNode(target);
//                    node2.getNodeData().setLabel(target);
//
//                    node2.getNodeData().getAttributes().setValue("nT", key + ".LINK");
//                    undirectedGraph.addNode(node2);
//
//                    Edge e1 = graphModel.factory().newEdge(node2, node1);
//
//                    e1.getEdgeData().getAttributes().setValue("gK", typ);
//                    e1.getEdgeData().getAttributes().setValue("Id", linkid);
//
//                    e1.getEdgeData().getAttributes().setValue("Weight", weight);
//
//                    undirectedGraph.addEdge(e1);
// }
//
//
//                };
//            }

        }

        return lastId;
    }

    public void collectLink(String groupKEY, ExtendedNodePairSFE np) {

        Vector v = links.get(groupKEY);

        if (v == null) {
            v = new Vector();
            links.put(groupKEY, v);
            System.out.println("CREATED: " + groupKEY);
        }

        v.add(np);

    }

    AttributeModel model = null;
    GraphModel graphModel = null;

    public void _flushGephiNetwork(int runID, boolean shuffle, double TS) {

        BufferedWriter bwNL2;

        int errorsZ = 0;
        int goodZ = 0;

        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Generate a new random graph into a container
        Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();

        
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);

        model = ac.getModel();

        AttributeColumn testCol1 = model.getEdgeTable().addColumn("Weight0", AttributeType.DOUBLE);
        AttributeColumn testCol2 = model.getEdgeTable().addColumn("Weight1", AttributeType.DOUBLE);
        AttributeColumn testCol3 = model.getEdgeTable().addColumn("Weight2", AttributeType.DOUBLE);
        AttributeColumn testCol4 = model.getEdgeTable().addColumn("Weight3", AttributeType.DOUBLE);
        AttributeColumn testCol5 = model.getEdgeTable().addColumn("gK", AttributeType.STRING);
        AttributeColumn testCol6 = model.getNodeTable().addColumn("nT", AttributeType.STRING);

        Graph undirectedGraph = graphModel.getGraph();

        String kkType = KreuzKorrelation.getCalcTypeLabel();

        try {

            String wnk = "FINANCE";

            if (wn != null) {
                wnk = wn.getKey();
            }

//            File f =  new File( BASE + "_" + kkType + "." + runID + ".graphml");
            File f2 = new File(BASE + "_" + kkType + "." + runID + ".gexf");
//            File f3 = new File( BASE + "_" + kkType + "." + runID + ".pdf");

            if (!f2.getParentFile().exists()) {
                f2.getParentFile().mkdirs();
            }

//            bwNL2 = new BufferedWriter(new FileWriter(f));
            for (String key : links.keySet()) {

                int id = 0;
                Vector v = links.get(key);

                for (Object o : v) {

                    id++;

                    ExtendedNodePairSFE l = (ExtendedNodePairSFE) o;

                    String type = key;

                    String s = l.s;
                    String t = l.t;

//                    System.out.println("s : " + s );
//                    System.out.println("t : " + t );
                    System.out.println("np: " + l.toString());
                    System.out.println("------");

                    Node node1 = undirectedGraph.getNode(s);

                    if (node1 == null) {

                        node1 = graphModel.factory().newNode(s);

                        node1.getNodeData().setLabel(s);

                        node1.getNodeData().getAttributes().setValue("nT", "nT");

                        undirectedGraph.addNode(node1);
                    }

                    Node node2 = undirectedGraph.getNode(t);

                    if (node2 == null) {

                        node2 = graphModel.factory().newNode(t);

                        node2.getNodeData().setLabel(t);

                        node2.getNodeData().getAttributes().setValue("nT", "nT");

                        undirectedGraph.addNode(node2);
                    }

                    String typ = type;
                    String linkid = "" + id;
                    String label = "(" + s + ";" + t + ")";

                    double strength0 = ResultManager.getStaerke(l, 0);
                    double strength1 = ResultManager.getStaerke(l, 1);
                    double strength2 = ResultManager.getStaerke(l, 2);
                    double strength3 = ResultManager.getStaerke(l, 3);

                    Edge e1 = graphModel.factory().newEdge(node1, node2);

                    e1.getEdgeData().getAttributes().setValue("gK", key);
                    e1.getEdgeData().getAttributes().setValue("Weight0", strength0);
                    e1.getEdgeData().getAttributes().setValue("Weight1", strength1);
                    e1.getEdgeData().getAttributes().setValue("Weight2", strength2);
                    e1.getEdgeData().getAttributes().setValue("Weight3", strength3);

                    double st = 0;

                    switch ( ResultManager.mode ) {
                        case 0  : {
                            st = strength0;
                            break;
                        } 
                        case 1  : {
                            st = strength1;
                            break;
                        } 
                        case 2  : {
                            st = strength2;
                            break;
                        } 
                        case 3  : {
                            st = strength3;
                            break;
                        } 
                        
                    }
                    
                    e1.getEdgeData().getAttributes().setValue("Weight", st);

                    undirectedGraph.addEdge(e1);

                };

            }
            
            //Export full graph
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            try {
                ec.exportFile(f2);
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }

            //Init a project - and therefore a workspace
            pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.getCurrentProject();

            workspace = pc.getCurrentWorkspace();

            ec = Lookup.getDefault().lookup(ExportController.class);

            System.out.println("P  : " + pc.getCurrentProject());
            System.out.println("WS : " + workspace);

            
            /***
             * 
             * 
             * 
             * BUILD IN PROFILER ...
             * 
             * 
             * 
             * 
             * 
             */
            
            
            
//            if ( !shuffle )
//                storeImage(new File(BASE + "_" + kkType + "." + runID), "gephi_" + kkType + "." + runID + "_" + TS, TS );

        } catch (Exception ex) {
            Logger.getLogger(NetDensityCalc.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void storeImage(File folderOut, String label, double TS ) {

        System.out.println(">>> SNAProfiler ### Write profile to folder:" + folderOut.getAbsolutePath());

        BufferedWriter bw = null;
        try {

            folderOut.mkdirs();

            bw = new BufferedWriter(new FileWriter(folderOut.getAbsolutePath() + "/" + label + "_MST.csv"));

            
            
            
            
            
            
            
//        //Filter, remove degree < 10
//        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
//        
//        EdgeWeightFilter eFilter = new EdgeWeightFilter();
//        
//        eFilter.init(graphModel.getGraph());
//        
//        Range r = new Range( 1 , 10 );
//        
//        eFilter.setRange( r );
//        
//        //Remove nodes with degree < 10
//        Query query = filterController.createQuery(eFilter);
//        
//        GraphView view = filterController.filter(query);
//        
//        
//        graphModel.setVisibleView(view);    //Set the filter result as the visible view
            
            
            
            
            //Rank color by Degree
            RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
            Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
            AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
            /**
             * The following example lets colors change from blue over white
             * over green to red for the intervals from 0.0..0.33..0.66..1.0
             */
            float[] positions = {0f, 0.33f, 0.66f, 1f};
            colorTransformer.setColorPositions(positions);
            Color[] colors = new Color[]{new Color(0x0000FF), new Color(0xFFFFFF), new Color(0x00FF00), new Color(0xFF0000)};
            colorTransformer.setColors(colors);
            rankingController.transform(degreeRanking, colorTransformer);

            /**
             *
             * Partitioning
             *
             */
//            dumpModelSchema(aModel);

            System.out.println( "g:" + graphModel );
            System.out.println( "m:" + model );


            //Get Centrality
            GraphDistance distance = new GraphDistance();
            distance.setDirected(true);
            distance.execute(graphModel, model);
            
            
            
            System.out.println("### SNAProfiler ### Run Distances analysis. " + graphModel + " " + model);

//            dumpModelSchema(aModel);
            Degree degree = new Degree();
            degree.execute(graphModel, model);
            System.out.println("### SNAProfiler ### Run Degree analysis. " + graphModel + " " + model);

//            dumpModelSchema(aModel);
            //Run modularity algorithm - community detection
            Modularity modularity = new Modularity();
            modularity.execute(graphModel, model);
            System.out.println("### SNAProfiler ### Run Modularity analysis. " + graphModel + " " + model);

//            dumpModelSchema(aModel);
            //Partition with 'modularity_class', just created by Modularity algorithm
            AttributeColumn modColumn = model.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
            PartitionController partitionController = Lookup.getDefault().lookup(PartitionController.class);
            Partition p2 = partitionController.buildPartition(modColumn, graphModel.getDirectedGraph());
            System.out.println("### SNAProfiler ### Run PARTITIONER. " + graphModel + " " + model);

            System.out.println(">>> " + p2.getPartsCount() + " partitions found");
            NodeColorTransformer nodeColorTransformer2 = new NodeColorTransformer();
            nodeColorTransformer2.randomizeColors(p2);
            partitionController.transform(p2, nodeColorTransformer2);

            /**
             * RANKING
             *
             */
            //Rank size by centrality
            Ranking degreeRanking2 = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
            AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
            sizeTransformer.setMinSize(1f);
            sizeTransformer.setMaxSize(5f);
            rankingController.transform(degreeRanking2, sizeTransformer);

            //Rank label size - set a multiplier size
            AttributeColumn centralityColumn = model.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
            Ranking centralityRanking2 = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
            AbstractSizeTransformer labelSizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.LABEL_SIZE);
            labelSizeTransformer.setMinSize(22f);
            labelSizeTransformer.setMaxSize(48f);
            rankingController.transform(centralityRanking2, labelSizeTransformer);
            //Layout for 1 minute

            int t = 5;

            System.out.println(">>> AutoLayout uses: " + t + " seconds for rendering.");

            AutoLayout autoLayout = new AutoLayout(t, TimeUnit.SECONDS);
            autoLayout.setGraphModel(graphModel);
            YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
            ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
            LabelAdjust thirdLayout = new LabelAdjust(null);
            AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
            AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
            autoLayout.addLayout(firstLayout, 0.1f);
            autoLayout.addLayout(secondLayout, 0.25f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
            autoLayout.addLayout(thirdLayout, 0.65f);
            autoLayout.execute();
            PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
            previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
            previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.TRUE);

//            // SpanningTree analysis
//            /**
//             *
//             * Problem: Our current edges are usefull if the highest values are
//             * used, but SpanningTree works on smallest values.
//             *
//             */
//            SpanningTree st = new SpanningTree();
//            st.execute(graphModel, aModel);
//
//            //Iterate over edges
//            for (Edge e : directedGraph.getEdges().toArray()) {
//
//                int z = (int) e.getAttributes().getValue("Spanning Tree");
//
//                if (z == 1) {
//                    System.out.println(e.getSource().getNodeData().getId() + "\t" + e.getTarget().getNodeData().getId() + "\t" + e.getWeight());
//                    bw.write(e.getSource().getNodeData().getId() + "\t" + e.getTarget().getNodeData().getId() + "\t" + e.getWeight());
//                    bw.write("\n");
//                }
//
//            }
//            bw.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

//        dumpModelSchema(aModel);
        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File(folderOut.getAbsolutePath() + "/" + label + ".pdf"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ExportController ec2 = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec2.exportFile(new File(folderOut.getAbsolutePath() + "/" + label + ".gexf"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        //Export
//        ExportController ec3 = Lookup.getDefault().lookup(ExportController.class);
//        PNGExporter exporter = (PNGExporter) ec3.getExporter("png");
//        exporter.setHeight(900);
//        exporter.setHeight(1400);
//        exporter.setMargin(50);
//        
//        try {
//            ec3.exportFile(new File(folderOut.getAbsolutePath() + "/" + label + ".png"),exporter);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

    }

    public void setNetwork(LocalWikipediaNetwork2 network) {
        this.network = network;
    }

    private boolean isInternal(WikiNode wl) {
        if (CN.containsValue(wl)) {
            return true;
        }
        if (IWL.containsValue(wl)) {
            return true;
        }
        if (AL.containsValue(wl)) {
            return true;
        }
        if (BL.containsValue(wl)) {
            return true;
        }
        return false;
    }

    public void profile() {

    }
}
