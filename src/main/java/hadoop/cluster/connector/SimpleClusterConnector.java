/**
 * 
 * A Cluster-Connector allows submission of jobs to Hadoop.
 * The core APIs are sometimes not appropriate. Here we offer a 
 * simple API which abstracts away low level calls.
 * 
 * @author Mirko Kämpf
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
    
    public static boolean export = true;
    



    /**
     * A simple cluster connectore.
     * 
     * The main function is for testing functionality.
     * 
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


    /**
     * 
     * @param base
     * @param listFile
     * @param dest
     * @throws IOException 
     */
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

  

    public static File checkFile(String listFile) {
        File f = new File(listFile);
        System.out.println(">>> listfile.exists() = " + f.exists());
        return f;
    }

    
    

    
}
