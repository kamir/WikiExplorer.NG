package m3.wikipedia.corpus.extractor;

import com.thoughtworks.xstream.XStream; 
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.hadoopts.hadoopts.topics.wikipedia.LocalWikipediaNetwork2;
import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * This class describes the Metadata of a single analysis 
 * for Wikipedia time series.
 * 
 * @author kamir
 */
public class WikiStudieMetaData {
    
    private Date dateTo;
    private Date dateFrom;

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }
    
    private WikiStudieMetaData() { };

    static WikiStudieMetaData currentS = null;
    
    public static WikiStudieMetaData initStudie() {
        WikiStudieMetaData s = new WikiStudieMetaData();
        
        init( s );
        
        return s;
    }

    private static void init(WikiStudieMetaData s) {
        if ( currentS == null )  { 
            currentS = s;                    
        }
        else { 
            // javax.swing.JOptionPane.showMessageDialog( new JFrame(), "Create new STUDY METADATA ... \n\n (later also in your HDGS repository!!!)" );
        }
    }
     

    public String name = "?";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        String ret = description + "\n" + getExtractionAttempts();
        return ret;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the set of Core-WN, collected in this studie.
     * 
     * @return 
     */
    public Vector<WikiNode> getWn() {
        return wn;
    }

    public void setWn(Vector<WikiNode> wn) {
        this.wn = wn;
    }
    
    public String description = "...";
    public Vector<WikiNode> wn = new Vector<WikiNode>();
    
    public void addNewNode( String w, String p ) { 
        wn.add( new WikiNode( w,p ) );
    }

    /**
     * 
     */
    public String toString() { 
        StringBuffer sb = new StringBuffer();
        sb.append("NAME    : " + this.name + "\n");
        sb.append("PATH-1  : " + this.path1 + "\n" );
        sb.append("PATH-2  : " + this.path2 + "\n" );
        sb.append("Z-Nodes : " + this.wn.size() + "\n" );
        return sb.toString();
    }
    
    public static void store( File f, WikiStudieMetaData data ) throws FileNotFoundException, IOException { 
                
        FileWriter os = new FileWriter( f );
        
        XStream xstream = new XStream();
        String s = xstream.toXML( data );
        os.write( s );
        os.flush();
        os.close(); 
        
    }
    
    public static WikiStudieMetaData load( File f ) throws FileNotFoundException {  
       
        System.out.println(">>> LOAD XML-file ... " + f.getAbsolutePath());
        FileInputStream os = new FileInputStream( f );
        XStream xstream = new XStream();
        Object o = xstream.fromXML(os); 
        WikiStudieMetaData d = (WikiStudieMetaData)o;
        
        if ( d.extractions_attempts == null ) d.extractions_attempts = new Vector<String>();
        
        System.out.println(">>> #nodes=" + d.getWn().size() );
        
        init( d );
        
        return d;
    }

    public LocalWikipediaNetwork2 net = null;

    public Vector<WikiNode> getAL() {
        return extract("A.L");
    } 

    public Vector<WikiNode> getAL(int nr) { 
        Vector<WikiNode> nrBL = extract("A.L", nr);
        return nrBL;
    }
    
    public Vector<WikiNode> getBL(int nr) { 
        Vector<WikiNode> nrBL = extract("B.L", nr);
        return nrBL;
    }
    
    public Vector<WikiNode> getBL() {
        return extract("B.L");
    }


    public Vector<WikiNode> getIWL() {
        return extract("IWL");
    }

    public Vector<WikiNode> getIWL(int nr) { 
        Vector<WikiNode> nrBL = extract("IWL", nr);
        return nrBL;
    }
    
    public Vector<WikiNode> getCN() {
        return extract("CN");
    }

    public Vector<WikiNode> getCN(int nr) { 
        Vector<WikiNode> nrBL = extract("CN", nr);
        return nrBL;
    }

    File LISTFILE = null;
    public void initNetFromListFile(String path, String name) {
        if ( net != null ) return;
        net = new LocalWikipediaNetwork2();
        File f = new File( path + "/" + "merged_listfile_" +name + ".lst" );
        try {
            net.loadListFile2( f );
            LISTFILE = f;
        } 
        catch (FileNotFoundException ex) {
            System.exit(-1);
            Logger.getLogger(WikiStudieMetaData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikiStudieMetaData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * 
     * extract the key names for a particular CN.GROUP
     * 
     * @param keyPart
     * @param nr
     * @return 
     */
    public Vector<WikiNode> extract(String keyPart, int CNid) {
        Vector<WikiNode> l = new Vector<WikiNode>();
        Iterator<String> en = net.nodeListeHASHED.keySet().iterator();
        while( en.hasNext() ) { 
            String code = en.next();
            // only if the right CN-group is here ...
            if( code.startsWith(CNid+"") ) {
                if( code.endsWith( keyPart ) ) { 
                    Vector<String[]> wns =  net.nodeListeHASHED.get( code );
                    for( String[] s : wns ) { 
                        l.add( new WikiNode( s ) );
                    }
                }
            }
        }
        return l;
    }
    
    public Vector<WikiNode> extract(String keyPart) {
        Vector<WikiNode> l = new Vector<WikiNode>();
        Iterator<String> en = net.nodeListeHASHED.keySet().iterator();
        while( en.hasNext() ) { 
            String code = en.next();
            if( code.endsWith( keyPart ) ) { 
                Vector<String[]> wns =  net.nodeListeHASHED.get( code );
                for( String[] s : wns ) { 
                    l.add( new WikiNode( s ) );
                }
            }
        }
        return l;
    }
    
    public Vector<WikiNode> extractByCN(int CN) {
        String CNid = CN+"";
        Vector<WikiNode> l = new Vector<WikiNode>();
        Iterator<String> en = net.nodeListeHASHED.keySet().iterator();
        while( en.hasNext() ) { 
            String code = en.next(); 
            if( code.startsWith(CNid+"") ) {
                Vector<String[]> wns =  net.nodeListeHASHED.get( code );
                for( String[] s : wns ) { 
                    l.add( new WikiNode( s ) );
                }
        
            }
        }    
        System.out.println(">>> SUB-List extracted ... " );
        return l;
    }
    
    public Vector<WikiNode> extract_ALL() {
        Vector<WikiNode> l = new Vector<WikiNode>();
        Iterator<String> en = net.nodeListeHASHED.keySet().iterator();
        while( en.hasNext() ) { 
            String code = en.next();
                Vector<String[]> wns =  net.nodeListeHASHED.get( code );
                for( String[] s : wns ) { 
                    l.add( new WikiNode( s ) );
                }
        }
        return l;
    }

    Vector<String> extractions_attempts = new Vector<String>();
    
    public void logExtraction(Calendar von, Calendar bis) {
        String el = "("+von.getTimeInMillis()+", ... ,"+ bis.getTimeInMillis() + " um: " + new Date( System.currentTimeMillis()  );
        extractions_attempts.add(el);
    }

    private String getExtractionAttempts() {
        String d = "";
        for(String s : extractions_attempts ) { 
           d = d.concat(s) + "\n";
        }
        return d;
    }

    public int getNrOfNodes_ALL( String p, String n ) {
        initNetFromListFile(p, n);
         Vector<WikiNode> nn = extract_ALL();
         return nn.size();
    }

    public File selectFile( String path1 ) {
        File ff = new File( path1 );
        javax.swing.JFileChooser jfc = new JFileChooser(ff);
        int sel = jfc.showOpenDialog( new JFrame() );
        ff = jfc.getSelectedFile();
        return ff;
    }

    boolean operate_LOCALY = true;
    boolean operate_DEV = false;
    
    public String selectedName = null;
    
    public String path2 = "/home/kamir/bin/WikiExplorer/WikiExplorer/";
    public String path1 = "/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia";
            
    public File getRealPathToProjectFile(File ff ) {
        String na ="???";
        if ( operate_DEV ) {
            path2 = "/home/kamir/bin/WikiExplorer/WikiExplorer/";
            path1 = "/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia";
        }    
        else { 
            path2 = ff.getParent();
            path1 = ff.getParent();
            na = ff.getName().substring( 0, ff.getName().length() - 4 );
        } 
        
        
        File f = new File( path1 + "/" + na + ".xml");
        
        initNetFromListFile(path2, na);
        selectedName = na;

        System.out.println(">>> path1 : " + path1 );
        System.out.println(">>> path2 : " + path2 );
        System.out.println(">>> name  : " + selectedName );
        
        name = selectedName;

        return f; 
    }

    public String getTSExtractionLocationFileName() {
        
        String ext = "2008";
        return "/user/kamir/wikipedia/corpus/" + this.name + "_" + ext +"_merged/part-r-00000";
  
        // this.jtLF.setText("/home/kamir/bin/WikiExplorer/WikiExplorer/merged_listfile_" + this.jtf_Studie.getText() + ".lst");
    }

    public String getLISTFILE() {
        return LISTFILE.getAbsolutePath();
    }

    public void resetReloadBuffer() {
        wnRELOAD = new Vector<WikiNode>();
    }

    public Vector<WikiNode> wnRELOAD = new Vector<WikiNode>();
    public void addForReload(String w, String p) {
        wnRELOAD.add( new WikiNode( w, p ) );
    }

    public Date getTimeRangeTo() {
        return dateTo;
    }

    public Date getTimeRangeFrom() {
        return dateFrom;
    }


    

}
