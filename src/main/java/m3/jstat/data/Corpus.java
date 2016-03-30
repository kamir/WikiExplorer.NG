/**
 * The core data container for textanalysis.
 */
package m3.jstat.data;

import m3.io.CorpusFile2;
import m3.io.CorpusFilePlainContentPerPage;
import m3.io.CorpusFileXML;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.crunchts.store.TSB; 
import m3.wikipedia.corpus.extractor.iwl.ExtractIWLinkCorpus;
import m3.wikipedia.explorer.data.WikiNode;
import org.etosha.core.sc.connector.external.Wiki;

/**
 * @author root
 */
public class Corpus {

    private static String studie = "DEFAULT.Studie";

    public Corpus() {} 

    public Corpus(String studie) { 
            this.docs = new Vector<Document>();
            this.studie = studie;
            // corpusFilePath = TSB.getAspectFolder( "corpus" , studie ).getAbsolutePath();
    }
    
//    public Corpus(boolean loadPC) {
//        this("DEFAULT_ignoreLoadPC");
//    
//        //  this.loadPageContent = loadPC;
//        
//        System.out.println("##### WARNING ##### IGNORE loadPC PROP in constructor Corpus() !!! ");
//    }

    
    public static final int mode_XML = 0;
    public static final int mode_SEQ = 1;
    
    private static String _listfile_pfad = TSB.getFolderForTimeSeriesMetadata().getAbsolutePath();    
    private static String corpusFilePath = null; // TSB.getAspectFolder( "corpus.xml" , studie ).getAbsolutePath();

    public static String getCorpusFilePath() {
        return corpusFilePath;
    }

    public static String getListfilePath() {
        return _listfile_pfad;
    }

//    public static void setListfile_pfad(String _listfile_pfad) {
//        Corpus._listfile_pfad = _listfile_pfad;
//    }

    public static Corpus loadCorpus(String name, int mode) throws IOException, URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (mode == mode_SEQ) {
            return CorpusFile2.loadFromLocalFS(name);
        } else if (mode == mode_XML) {
            return CorpusFileXML.loadFromLocalFS(name);
        } else {
            return null;
        }
    }

    public static void storeCorpus(Corpus corpus, String name, int mode) throws IOException, URISyntaxException {
        System.out.println("*** " +  name + " *** mode:" + mode + " *** studie:" + studie );

        if (mode == mode_SEQ) {
            CorpusFile2.createCorpusFile(".", name, corpus);
        } else if (mode == mode_XML) {
            CorpusFileXML._createCorpusFile(".", name, corpus, studie);
        }
        
        CorpusFilePlainContentPerPage._createCorpusFile(".", name, corpus, studie);
    
    }
    
    
    
    final boolean loadPageContent = true;


 
    public void addDocument(Document doc) {
        docs.add(doc);
    }
    public Vector<Document> docs = null;

    public void _addWikiNodes(Vector<WikiNode> t, String acM) {

        System.out.println("*** " + t.size() + "=>" + acM + " loadPageContent: " + loadPageContent);

        long vol = 0;

        
        for (WikiNode wn : t) {
            String html = "";
            try {
                Document doc = new Document(ExtractIWLinkCorpus.getUrl(wn.wiki, wn.page), html);
                doc.group = acM;
                doc.wn = wn;
                if (loadPageContent) {
                    
                    html = ExtractIWLinkCorpus.getHTML(wn);
                    
                    vol = vol + html.length();

                    doc.wn.pageVolume = html.length();
                    doc.html = html;
                    
                }
                else { 
                    System.out.println( "###### SKIP LOAD PAGE #####");
                }
                
                System.out.println( doc.group + " : " + doc.wn.pageVolume );
               
                addDocument(doc);
                
            } catch (IOException ex) {
                Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("***" + t.size() + "=>" + acM + " volume: " + vol );

        
    }
    

    public void addWikiNode(WikiNode wn, String b) {
        String html;
        try {
            html = ExtractIWLinkCorpus.getHTML(wn);
            Document doc = new Document(ExtractIWLinkCorpus.getUrl(wn.wiki, wn.page), html);
            doc.group = b;
            doc.wn = wn;
            addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vector<WikiNode> _getWikiNodes(String cN) {
        Vector<WikiNode> d = new Vector<WikiNode>();
        for (Document doc : docs) {
            if (doc.group.equals(cN)) {
                d.add(doc.wn);
            }
        }
        return d;
    }
    public void writeWikiNodeKeyFile(WikiNode wn, String sdm_name) throws UnsupportedEncodingException {
        
        //
        
        String page = wn.page;

                if (page.contains("/")) {
                    page = page.replaceAll("/", "_");
                }
                if (page.contains("'")) {
                    page = page.replaceAll("'", "_");
                }
                
        String fnPart = URLEncoder.encode( page, "UTF8");
        
        String listFILE = "listfile_" + sdm_name + "_" + wn.wiki + "_" + fnPart + ".lst";
        try {
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(_listfile_pfad + "/"+ listFILE));

            int i = 0;
            for (Document doc : docs) {
                WikiDocumentKey k = new WikiDocumentKey(doc);
                bw.write(k + "\n");
                i++;
            }
            System.out.println(i + " Docs gespeichert.");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String ENCODE_FN(String wiki, String pn) throws IOException {
        Wiki wiki1 = new Wiki(wiki + ".wikipedia.org");
        String l = pn;
        try {
            l = URLEncoder.encode( wiki1.normalize(pn), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Corpus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }
    
    public String getTextStatistikLine() { 
        String hl = "STUDIE	LANG	Page	z.CN	z.IWL	z.AL	z.BL	vol.CN	vol.IWL	vol.A.L	vol.B.L	volCN / volAL	volIWL / volBL	 ( volCN + volIWL) / (volAL + volBL)";
        return hl;
    }
    
    public void exportCorpusToSequenceFile( SequenceFile.Writer writer ) throws IOException, URISyntaxException {

        int c = 0;
        for ( Document doc : docs ) {
            c++;
            Text key = new Text( new WikiDocumentKey( doc ).toString() );
            Text val = new Text( doc.html );
            writer.append( key, val );
        }
    }
}
