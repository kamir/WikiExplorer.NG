/**
 * Several tasks have to be submitted to the cluster from the UI.
 * 
 * This is the cluster connector.
 * 
 * @author Mirko
 */
package hadoop.cluster.connector;

import org.apache.hadoopts.data.series.Messreihe;
import m3.io.CNResultManager2; 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import m3.jstat.data.Corpus;
import org.crunchts.store.TSB;
import org.openide.util.Exceptions;
import m3.terms.TermCollectionTools;
import m3.wikipedia.corpus.extractor.FileNameFilter;
import m3.wikipedia.corpus.extractor.category.ExtractCategorieCorpus;
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;

public class SimpleClusterConnector {
    
        // static String hadoopPath = "/usr/bin/hadoop";
    
    static String hadoopPath = "/Users/kamir/hadoop-2.0.0-cdh4.7.0/bin/hadoop";

    static String hadoopPathSSH = "/usr/bin/ssh";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

//        Wiki.debug = true;
//
//        // do just a single ANALYSIS for one extracted corpus to get the measures for one single page
//        String page = "Stollberg";
//        String wiki = "de";
//        
//        String studie = "finance_DAX";
//
//        CNResultManager2 cnrm = new CNResultManager2();
//
//        FileWriter writer = RepresentationPlotBubbleChart.getWriter( "REP-plot-bubbles-GC-" + studie + ".html");
//        
//        work(wiki, page, studie, null, Corpus.mode_XML, "", cnrm, 3, writer );
//
//        writer.flush();
//        writer.close();
//        
//        cnrm.printResults();
        
    }
    
    public static boolean export = true;

    /**
     * Returns the points for REPPLOT ...
     * 
     * listFile2Return is in a static variable
     *
     * @param w
     * @param page
     * @param studie
     * @param i       index of the element in the list.
     * 
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static String runPerPageCorpusTextAnalysis(String w, String page, String studie, FileWriter fw, int FILEMODE, String ext, CNResultManager2 rm, int i, FileWriter fwChartBuble) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        // ext war hier der erste Edit der Datei


        Vector<WikiNode> ACN = new Vector<WikiNode>();
        Vector<WikiNode> AL = new Vector<WikiNode>();

        Vector<WikiNode> BIWL = new Vector<WikiNode>();
        Vector<WikiNode> BL = new Vector<WikiNode>();

        // wikipedia.explorer.ExtractCategorieCorpus.extractCorpus(wiki, page);

        page = FileNameFilter.cleanWikinameForFilename(page);

        String fileRelMapData = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.JS.data";

        StringBuffer sbRelMapData = new StringBuffer();


        String file = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.seq";

        String file2 = "iwl_corpus_RELMAP_" + studie + "_" + w + "_" + page + ".dat.corpus.seq.ts";

        String listFILE = "listfile_" + studie + "_" + w + "_" + page + ".lst";

        // Corpus2 c = CorpusFile2.loadFromLocalFS("/home/kamir/bin/WikiExplorer/WikiExplorer/" + file);
        Corpus c = null;
        
        String point = "";
        
        try {

            if (FILEMODE == Corpus.mode_XML) {
                file = "iwl_corpus_" + studie + "_" + w + "_" + page + ".dat.corpus.xml";
            }

            String f = Corpus.getCorpusFilePath();
            if ( f == null ) f = TSB.getAspectFolder( "corpus.xml", studie ).getAbsolutePath();
            
            c = Corpus.loadCorpus( f + "/" + file, FILEMODE);

//            if ( export ) { 
//                c.exportCorpusToSequenceFile();
//            }

            ACN = c._getWikiNodes("CN");
            AL = c._getWikiNodes("A.L");

            BIWL = c._getWikiNodes("IWL");
            BL = c._getWikiNodes("B.L");
            
            double colE = BIWL.size();
            double colF = AL.size();
            double colG = BL.size();
            
            double volCN = getSummeVolume(ACN);
            double volIWL = getSummeVolume(BIWL);
            double volBL = getSummeVolume(BL);
            double volAL = getSummeVolume(AL);
            
            double colH = volCN; 
            double colI = volIWL;
            double colJ = volAL;
            double colK = volBL;
            
            double colR = colE + colF;
            
            double colS = colG/colF;
            
            double colT = colR / ( ( colG/colF) + colE );
            
            double colU = colH * colE / colI;
            
            double colW = ( colH / 1.0 ) * ( colK/colG );

            double colX = ( colI / colE ) * ( colJ/colF );
            
            double colV = colW/colX; 

            //  ['A',   80,  167,      120],
            String pageLabel = ACN.elementAt(0).wiki + "." + ACN.elementAt(0).page;
            
            DecimalFormat df1 = new DecimalFormat("0.000");
            
            // create lines for the BubbleChart ...
            
            point = "['" + pageLabel + "'," + 
                           df1.format( colU ).replace(",", ".") + "," + df1.format(colV).replace(",", ".") + ",'" + pageLabel + "'," + df1.format( colT ).replace(",", ".") + "],\n"  ; 

            // DATA
            // ['fr',6.760,3.295,'fr',14.212],
            // ['en',3.029,0.815,'en',11.397],
            // ['it',0.961,1.023,'it',6.362],

            
            fwChartBuble.write( point + "\n" );
 
            sbRelMapData.append("var dataA = google.visualization.arrayToDataTable([ ['Country', 'REP v']" );
            sbRelMapData.append(getRelMapLinesVOL(ACN));
            sbRelMapData.append(getRelMapLinesVOL(BIWL));
            sbRelMapData.append("]);\n\n" );
            
            
            sbRelMapData.append("var dataB = google.visualization.arrayToDataTable([ ['Country', 'REP k']" );
            sbRelMapData.append(getRelMapLinesK(ACN));
            sbRelMapData.append(getRelMapLinesK(BIWL));
            sbRelMapData.append("]);" );
                                  
            
            FileWriter fw2 = new FileWriter( new File(fileRelMapData)  );
            fw2.write( sbRelMapData.toString() );
            fw2.flush();
            fw2.close();

//            rm.setResult( i +".txt.vol.CN" , volCN );
//            rm.setResult( i +".txt.vol.IWL" , volIWL );
//            rm.setResult( i +".txt.vol.B.L" , volBL );
//            rm.setResult( i +".txt.vol.A.L" , volAL );
//            
//            rm.setResult( i +".z.CN" , ACN.size() );
//            rm.setResult( i +".z.IWL" ,BIWL.size() );
//            rm.setResult( i +".z.B.L" , BL.size() );
//            rm.setResult( i +".z.A.L" , AL.size() );

            c.writeWikiNodeKeyFile(ACN.elementAt(0), studie);

            System.out.println("CN   : " + ACN.size());
            System.out.println("IWL  : " + BIWL.size());

            System.out.println("A.L  : " + AL.size());
            System.out.println("B.L  : " + BL.size());

            double r1 = 100 * volCN / volAL;  // CORE
            double r2 = 100 * volIWL / volBL; // HULL
            double r3 = 100 * (volCN + volIWL) / (volAL + volBL); // ALL



//            rm.setResult( i +".txt.r1" , r1 );
//            rm.setResult( i +".txt.r2" , r2 );
//            rm.setResult( i +".txt.r3" , r3 );


            System.out.println("A local ratio   r1: [CORE] = " + r1);

            System.out.println("B global ratio  r2: [HULL] = " + r2);
            System.out.println("B global ratio  r3: [ALL]  = " + r3);


            DecimalFormat df = new DecimalFormat("0.00000");

            if (fw != null) {

                StringBuffer line = new StringBuffer();

                line.append(studie + "\t" + ACN.elementAt(0).wiki + "\t" + ACN.elementAt(0).page + "\t");
                line.append(ACN.size() + "\t");
                line.append(BIWL.size() + "\t");
                line.append(AL.size() + "\t");
                line.append(BL.size() + "\t");
                line.append(df.format(volCN) + "\t");
                line.append(df.format(volIWL) + "\t");
                line.append(df.format(volAL) + "\t");
                line.append(df.format(volBL) + "\t");
                line.append(df.format(r1) + "\t");
                line.append(df.format(r2) + "\t");
                line.append(df.format(r3) + "\t");
                line.append(ext + "\t");


                //                clalc LRI_(k) hier !!! 


                String l = line.toString().replace('.', ',');
                fw.write(l);
                fw.write("\n");
                fw.flush();
            }

//            String args2[] = new String[3];
//            args2[0] = "/user/kamir/wikipedia/raw/2007/2007-12/page*";
//            args2[1] = "/user/kamir/wikipedia/corpus/" + page;
//            args2[2] = listFILE;

        } 
        catch (URISyntaxException ex) {
            Logger.getLogger(SimpleClusterConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* lets make our own string to render */

        listFile2Return = Corpus.getListfilePath() + "/" + listFILE;
        
        return point;
    }
    
    static String listFile2Return = null;

    public static void initHeader(FileWriter fw) throws IOException {
        fw.write("#  double r1 = 100 * volCN / volAL; \n");
        fw.write("#  double r2 = 100 * volIWL / volBL;\n");
        fw.write("#  double r3 = 100 * (volCN + volIWL) / (volAL + volBL);\n");
        fw.write("#\n");
        fw.write("#\n");
        fw.write("#\n#studie\tACN.elementAt(0).wiki\tACN.elementAt(0).page\t");
        fw.write("ACN.size()\t");
        fw.write("IWL.size()\t");
        fw.write("AL.size()\t");
        fw.write("BL.size()\t");
        fw.write("text.vol.CN\t");
        fw.write("text.vol.IWL\t");
        fw.write("text.vol.AL\t");
        fw.write("text.vol.BL\t");
        fw.write("r1\t");
        fw.write("r2\t");
        fw.write("r3\t");
        fw.write("ext\t");
        fw.write("\n#\n");
        fw.flush();
    }

    private static Vector<Messreihe> createGlobalOrder(Vector<Messreihe> mrsTermDist) {

        Vector<Messreihe> mrsTermDistT = new Vector<Messreihe>();
        // determine all terms of all rows
        HashSet<String> terms = new HashSet<String>();
        for (Messreihe mr : mrsTermDist) {
            for (String a : mr.xLabels2) {
                if (!terms.contains(a)) {
                    terms.add(a);
                }
            };
            System.out.println("Nr of terms : [" + mr.getLabel() + "] " + terms.size());
        }

        for (Messreihe mr : mrsTermDist) {
            int sVor = mr.getXValues().size();
            for (String term : terms) {
                if (!mr.xLabels2.contains(term)) {
                    mr.addValue(0, term);
                }
            };
            System.out.println("expandet : " + mr.getLabel() + " from: " + sVor + " => " + mr.xValues.size());
            Messreihe r = TermCollectionTools.getTermVector(mr);
            mrsTermDistT.add(r.getYLogData());
        }

        return mrsTermDistT;
    }


    public static void uploadFile(String base, String listFile, String dest) throws IOException {

        File lf = new File(listFile);
      
        
        base = "/."; 
        System.out.println(">>> DELETE : " + dest + listFile);
        System.out.println( hadoopPath + " fs -rm " + listFile);

        int go = javax.swing.JOptionPane.showConfirmDialog(null, "Go on?");

        System.out.println(go);

        if (go == 1) {
            System.exit(0);
        }

        ProcessBuilder builder = new ProcessBuilder( hadoopPath, "fs", "-rm", dest);
        builder.directory(new File(base));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleClusterConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
          
        listFile = lf.getAbsolutePath();
        
        System.out.println(hadoopPath + " fs -copyFromLocal " + listFile + " " + dest);

        builder = new ProcessBuilder(hadoopPath, "fs", "-copyFromLocal", listFile, dest);
        builder.directory(new File(base));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleClusterConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param listFile
     * @param pattern
     * @param wiki
     * @param page
     * @param studie
     * @return
     * @throws IOException
     */
    public static String runExtraction(
            String listFile, String pattern, String wiki, String page, String studie) throws IOException {

        File f = checkFile(listFile);

        String lf = f.getName();
        String out = studie + "_" + wiki + "_" + page;

//        checkFile(hadoopPathSSH);
//        checkFile("store/EXTS4Corpus.jar");
//        checkFile("/home/kamir/bin/WikiExplorer/ExtractWikipediaTS/");

        System.out.println(hadoopPathSSH + "kamir@master2 hadoop jar /home/kamir/bin/WikiExplorer/ExtractWikipediaTS/store/EXTS4Corpus.jar "
                + "/user/kamir/wikipedia/raw/" + pattern
                + " /user/kamir/wikipedia/corpus/" + out
                + " " + lf);

        ProcessBuilder builder = new ProcessBuilder(
                hadoopPathSSH, "kamir@master2 hadoop jar", "/home/kamir/bin/WikiExplorer/ExtractWikipediaTS/store/EXTS4Corpus.jar",
                "/user/kamir/wikipedia/raw/" + pattern,
                "/user/kamir/wikipedia/corpus/" + out,
                lf);

//        builder.directory(new File("/home/kamir/bin/WikiExplorer/ExtractWikipediaTS/"));
        builder.directory(new File("/Users/kamir/DEMO/DATA")); 
        Process p = builder.start();

        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
        System.out.println(s.next());

        return out;
    }

    // Critical Issue ...
    public static String runExtraction2(
            String listFile, String pattern, String studie, String ext) throws IOException {
        File f = new File(listFile);
        String lf = f.getName();
        String out = studie + "_" + ext + "_merged";

        System.out.println(">>> BASE-FOLDER : /home/kamir/bin/WikiExplorer/ExtractWikipediaTS/");

        System.out.println(hadoopPathSSH + " kamir@master2 hadoop jar /home/kamir/bin/WikiExplorer/ExtractWikipediaTS/store/EXTS4Corpus.jar "
                + "/user/kamir/wikipedia/raw/" + pattern
                + " /user/kamir/wikipedia/corpus/" + out
                + " " + lf);

        ProcessBuilder builder = new ProcessBuilder(
                hadoopPathSSH, "kamir@master2", "hadoop jar /home/kamir/bin/WikiExplorer/ExtractWikipediaTS/store/EXTS4Corpus.jar " + 
                "/user/kamir/wikipedia/raw/" + pattern + " " +
                "/user/kamir/wikipedia/corpus/" + out + " " +
                lf);

//        builder.directory(new File("/home/kamir/bin/WikiExplorer/ExtractWikipediaTS/"));
        builder.directory(new File("/Users/kamir/DEMO/DATA"));
        Process p = builder.start();

//        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
//        System.out.println(s.next());

        return out;
    }

    public static void downloadFile(String src, String dest) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                hadoopPath, "fs", "-copyToLocal", src, dest);
        builder.directory(new File("/Users/kamir/DEMO/DATA"));
        try {
            //        Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\Z");
            //        
            Process p = builder.start();
            int i = p.waitFor();
            System.out.println(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleClusterConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static double getSummeVolume(Vector<WikiNode> ACN) {
        int s = 0;
        for (WikiNode wn : ACN) {
            if (wn.pageVolume != -1) {
                s = s + wn.pageVolume;
            }

        }
        System.out.println(ACN.size() + "  => " + s);
        return (double) s;
    }

    public static File checkFile(String listFile) {
        File f = new File(listFile);
        System.out.println(">>> listfile.exists() = " + f.exists());
        return f;
    }

    private static Object getRelMapLinesVOL(Vector<WikiNode> ACN) {
        StringBuffer sb = new StringBuffer();

        for (WikiNode wn : ACN) {

            try {
                wn = initNodeDetails(wn);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //['pt',1.053 ],
            sb.append(",['" + wn.wiki + "'," + wn.pageVolume + "]\n");

        }

        return sb.toString();
    }
    private static Object getRelMapLinesK(Vector<WikiNode> ACN) {
        StringBuffer sb = new StringBuffer();

        for (WikiNode wn : ACN) {

            try {
                wn = initNodeDetails(wn);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            //['pt',1.053 ],
            sb.append(",['" + wn.wiki + "'," + wn.kOut + "]\n");

        }

        return sb.toString();
    }
    
    private static WikiNode initNodeDetails(WikiNode wn) {
        Wiki wiki1 = new Wiki(wn.wiki + ".wikipedia.org");



        String url = ExtractCategorieCorpus.getUrl(wn.wiki, wn.page);
        try {
            wn.kOut = wiki1.getLinksOnPage(wn.page).length;
            wn.kIWL = wiki1.getInterWikiLinks(wn.page).size();
            System.out.println("\n>[***PAGE] : " + wn.page + "\n");

        } catch (Exception ex) {
            System.err.println("\n>[***PAGE] : " + wn.page + "\n");
        }
        return wn;

    }

    public static String getListFile() {
        return listFile2Return;
    }
}
