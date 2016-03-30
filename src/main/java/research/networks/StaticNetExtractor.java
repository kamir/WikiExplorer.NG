/*
 *  Aus der Link-Change DB-Tabelle wurden zwei Textfiles extrahiert,
 *  in denen zu allen Linkpaaren die NodeID und die Zeit der Erstellung steht.
 *
 *  Volumen: derzeit 22 GB
 * 
 *  Speicherort: G:/PHYSICS/PHASE2/data/out/link_changes/
 * 
 *  Nov 20211
 *
 *
 */

package research.networks;

import research.wikinetworks.NodePairList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import research.wikinetworks.NodePair;
import experiments.crosscorrelation.CCResultViewer;
import experiments.crosscorrelation.CCResults;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.TimeLog;
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;

/**
 *
 * @author kamir
 */
public class StaticNetExtractor {

    public static void main( String[] args ) throws FileNotFoundException, IOException {

        StaticNetExtractor lm = new StaticNetExtractor();
        lm.selectNodeGroup();
//        
//        initPreselctedFile( Integer.parseInt( ng.getLangID() ), ng.ids.length );
//        lm.prepareCCResult();
//
        lm.run();
    }

    static String fn = LinkCreationAndDestructionDetector.pfad + "all_links_creation.dat";

    static int langID = 52;
    static int nrOfNodes = 100;

    //    static String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_edits/";
    //    static public String LABEL = "EDITS";

    static public String LABEL = "ACCESS";
    static public String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_access/";

    public static void initPreselctedFile( int _langID, int _nrOfNodes ) {
        langID = _langID;
        nrOfNodes = _nrOfNodes;
        String code = CCResultViewer.getKey( langID+"", nrOfNodes );
        fn = LinkCreationAndDestructionDetector.pfad + code + File.separator + code + "_created_links.dat";
    };

    public void prepareCCResult() throws FileNotFoundException, IOException {
        CCResults ccr = new CCResults();
        ccr.lang = langID+"";
        ccr.nrOfNodes = nrOfNodes;
        String key = CCResultViewer.getKey( langID+"", nrOfNodes );

        int i = 0;
        boolean noBreake = true;

        File f = new File( pfadEDITS + key + EditActivityFilter.extension + ".cc.log" );

        BufferedReader br = new BufferedReader( new FileReader( f.getAbsolutePath() ));
        while( br.ready() && noBreake ) {
            String line = br.readLine();
            if ( line.startsWith( "#") ) {
                System.out.println( line );
            }
            else{
                i++;
                NodePair p = CCResultViewer.parseLine( line, nrOfNodes, langID+"" );
                if ( p != null ) ccr.addPaar(p);
            }
            // if ( i > 10000 ) noBreake = false;
        }
    }

    public void run() throws FileNotFoundException, IOException  {


        if ( ng == null ) selectNodeGroup();

        System.out.println(">>> Extract from        : " + fn );
        System.out.println(">>> Lookup in NodeGroup : " + ng.getName() + " with n=" + ng.ids.length + " nodes.");

        javax.swing.JOptionPane.showMessageDialog(null, "weiter ..." );

        TimeLog tl = new TimeLog();
        tl.setStamp("START");

        int lines = 0;
        int both = 0;
        int one = 0;       

        int max = 100000000;
        
        System.out.println(">>> Start to look values up ... ");

        Vector<NodePair> paareBoth = new Vector<NodePair>();
        Vector<NodePair> paareOne = new Vector<NodePair>();
        
        BufferedReader br = new BufferedReader( new FileReader( fn ) );
        while( br.ready() ) {
//        while( br.ready() && lines < max ) {
            String line = br.readLine();
            if ( line.startsWith("#") ) {


            }
            else {
                StringTokenizer st = new StringTokenizer( line );
                String s1 = st.nextToken();
                String s2 = st.nextToken();
                String d = st.nextToken();
                NodePair p = new NodePair();
                p.pageIDA = Integer.parseInt(s1);
                p.pageIDB = Integer.parseInt(s2);
                p.dateOfCreation = d;

                boolean[] b = ng.belongsNodeToGroup(p);

                if ( b[1] ) {
                    both++;
                    paareBoth.add(p);
                }
                if ( b[0] && !b[1] ) {  // nur wenn NICHT beide !!!
                    one++;
                    paareOne.add(p);
                }

                // if ( lines < max ) System.out.println( p.toString() );

                lines++;
            }
        }
        System.out.println(">>> Finished after n=" + lines + " links.");

        NodePairList npList_ONE = new NodePairList( paareOne );
        NodePairList npList_BOTH = new NodePairList( paareBoth );

        File f1 = new File( ng.fn + "LINKED_both.pairs.dat" );
        File f2 = new File( ng.fn + "LINKED_one.pairs.dat" );
        npList_BOTH.store(f1);
        npList_ONE.store(f2);
        

        System.out.println("> both=" + both );
        System.out.println("> one =" + one );
        int total = (ng.ids.length * (ng.ids.length - 1) ) / 2;
        System.out.println("> " + total + " potential links in this CC-group." );

        NodeGroup fBoth = new NodeGroup(paareBoth);
        NodeGroup fOne = new NodeGroup(paareOne);

        fBoth.fn = ng.fn + "LINKED_both.ng";
        fOne.fn = ng.fn + "LINKED_one.ng";

        try {
            fBoth.store();
            fOne.store();
        }
        catch (Exception ex) {
            Logger.getLogger(StaticNetExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        tl.setStamp("END");

        System.out.println( tl.toString() );
    };
    public static NodeGroup ng;

    /**
     * afterwards, the propertie ng is defined or null
     */
    public void selectNodeGroup() {
        File f = NodeGroup.selectNodegroupFile();
        ng = new NodeGroup(f);
        
        // for langid coded it si OK
        //ng.checkForDoubleIds();
        CCResultViewer.ng = ng;
    }
}

/*

1. Set up a sorted table of all IDs of 32-bit nodes (we only focus on
   them, don't we?).  Or even just the 100 or 200 nodes you are cur-
   rently focussing on.  This table will surely fit into the fast
   memory; let us say its length is N.

2. For each link from the large table, check if both nodes appear in
   the sorted table of 32-bit nodes.  The corresponding searches can
   be very fast, if you compare first with element N/2 of the sorted
   table, then with element N/4 or 3 N/4 (depending on the result of
   the first comparison), etc. for N/8, ...  This will take 2 log_2 N
   comparisons within main memory for each link.

This way you get a revised table of links, where only nodes with large
access appear.  The large table of links will have to be read only
once.  The program should not take more than several minutes.  There-
fore, it could even be repeated for different initial tables of desired
IDs.  It may be interesting to just count the number of (disregarded)
incoming and outgoing links of the considered group, which can be done
at practically no extra effort.

 */