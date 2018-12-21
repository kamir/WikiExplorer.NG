/*
 *  Einlesen der CC-Results und Anzeige von statisischen Daten.
 *
 */

package experiments.crosscorrelation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import javax.swing.JFileChooser;

import research.wikinetworks.NodePair;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 * Based on the dataset "cc_edits" we constuct a *LinkGroup* for our preselected
 * *NodeGroupe*. The result is a Network related to a special time periode
 * the Crosscorrelation was calculated for.
 *
 * *CCResutls* is a kind of BaseData for *LinkGroup* construction.
 *
 * @author kamir
 */
public class CCResultViewer {
  

//    static String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_edits/";
//    static public String LABEL = "EDITS";

    static public String LABEL = "ACCESS";
    static public String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_access/";

    static public int nrOfNodes = 100;
    static public int langID = -1;

    static public Hashtable<String,CCResults> results = new Hashtable<String,CCResults>();

    static public NodeGroup ng = null;

    public static void selectNodeGroup() {
        File f = NodeGroup.selectNodegroupFile();
        ng = new NodeGroup(f);
        ng.checkForDoubleIds();
    }


    public static void main(String[] args) throws Exception {

        KreuzKorrelation._defaultK = 15;

        selectNodeGroup();

        langID = Integer.parseInt( ng.getLangID() );
        nrOfNodes = getN( ng.fn );

        File fi = new File("C:/Users/kamir/Desktop/SO 16.10.2011");
        if ( !fi.exists() ) System.exit(0);

        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.showOpenDialog(null);
    
        File af = jfc.getSelectedFile();

        // StaticLinkManager.pfadEDITS = af.getAbsolutePath();
        // StaticLinkManager.initPreselctedFile(langID, nrOfNodes);

        File folder = new File(pfadEDITS);

        folder = af;
        File[] files = folder.listFiles();

        for( File f : files ) {
            //if ( !f.isDirectory() ) {
                System.out.println( ">> " +  f.getAbsolutePath() );
                int i = 0;
                boolean noBreake = true;
                String lang = getLangID( f.getName() );

                int l = Integer.parseInt(lang);

                if ( isFor_N_Nodes( f.getName() , nrOfNodes ) && l==langID ) {

                    CCResults ccr = new CCResults();
                    ccr.lang = lang;
                    ccr.nrOfNodes = nrOfNodes;
                    String key = getKey( lang, nrOfNodes );
                    results.put(key, ccr);

                    BufferedReader br = new BufferedReader( new FileReader( f.getAbsolutePath() ));
                    while( br.ready() && noBreake ) {
                        String line = br.readLine();
                        if ( line.startsWith( "#") ) {
                            System.out.println( line );
                        }
                        else{
                            i++;
                            System.out.println( line );
                            parseLine( line, nrOfNodes, lang );
                        }
                        // if ( i > 10000 ) noBreake = false;
                    }
                    ccr.store();
                    ccr.showStatus();
                }
//            }
        };
    };


    static public String getLangID( String name ) {
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        return s;
    };

    static public int getN( String name ) {
        boolean b = false;
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        s = st.nextToken();
        int n = Integer.parseInt(s);
        return n;
    };

    static public boolean isFor_N_Nodes( String name, int n ) {
        boolean b = false;
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        s = st.nextToken();
        int i = Integer.parseInt(s);
        if ( i == n ) b = true;
        return b;
    };

    // hier kommen die Zeilen an und werden in die TimeSeriesObjectn bzw.
    // Netzwerkdarstellungen überführt.
    static int counter = 0;
    static public NodePair parseLine(String line, int nrOfNodes, String lang) {
        NodePair np = null;
        CCResults r = results.get( getKey(lang, nrOfNodes));
        counter++;
        if ( counter % 10000 == 0 ) System.out.print(".");
        StringTokenizer tok = new StringTokenizer(line);
        int i = tok.countTokens();
        if ( i < 5 ) {
            System.out.println( i );
        }
        else {
           String pair = tok.nextToken();
           String k = tok.nextToken();
           String stdDev = tok.nextToken();
           String maxY = tok.nextToken();
           String signLevel = tok.nextToken();

           np = new NodePair(pair, ng);
           np.setCCResults( k, stdDev, maxY, signLevel );

           if ( r != null ) r.addPaar(np);
        }
        return np;
    }

    static public String getKey(String lang, int nrOfNodes) {
        return lang + "_" + nrOfNodes;
    }


}
