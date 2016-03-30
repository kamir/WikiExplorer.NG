/*
 *  Aus der Link-Change DB-Tabelle wurden zwei Textfiles extrahiert,
 *  in denen zu allen Linkpaaren die NodeID und die Zeit der Erstellung steht.
 *
 *  Volumen: derzeit 22 GB
 * 
 *  Erzeugt für jede SEED id ein eigenes File mit der lokalen Link-Umgebung.
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
public class StaticLinkManager {

    public static void main( String[] args ) throws FileNotFoundException, IOException {

        StaticLinkManager lm = new StaticLinkManager();
        
        // die Nodes aus dem Paper
        // lm.ng = new NodeGroup();
        
        
        
        // Deutsche Städte
        lm.ng = new NodeGroup();
        lm.ng.fn = "ng_cities_de.dat";
        lm.ng.load();
        
        
        lm.run();
    }

    public static NodeGroup ng;
    int nrOfIterations = 3;
    
    
//     static String fn = LinkCreationAndDestructionDetector.pfad + "all_links_creation.dat";
//     static String fn = "G:\\PHYSICS\\PHASE2\\data\\out\\links\\ng_all_links_creation.dat";
    static String fn = "G:\\PHYSICS\\PHASE2\\data\\out\\links\\ng_all_links_table.dat";
 
    static public Vector<NodePair> paareBoth = new Vector<NodePair>();
    static public Vector<NodePair> paareOne = new Vector<NodePair>();

    public void run() throws FileNotFoundException, IOException  {

        if ( ng == null ) {
            System.exit(0);
        }

        System.out.println(">>> Extract from        : " + fn );
        System.out.println(">>> Lookup in NodeGroup : " + ng.getName() + " with n=" + ng.ids.length + " nodes.");

        javax.swing.JOptionPane.showMessageDialog(null, "weiter ..." );

        TimeLog tl = new TimeLog();
        tl.setStamp("START");

        int iteration = 1;
        
        System.out.println(">>> Start to lookup links ... [" + iteration + "]");
        
        int ids[] = ng.ids;
        
        // diese Liste wächst mit jeder Iteration ...
        
        
        for( int id : ids ) {
                        
            System.out.println(">>> * New SEED-Id " + id );
               
            paareBoth = new Vector<NodePair>();
            paareOne = new Vector<NodePair>();
            NodeGroup ng2 = new NodeGroup();
                        
            iteration = 1;
            
            while( iteration <= nrOfIterations ) {

                if( iteration == 1) {
                    // mit einer einzelnen ID als seed-Value starten
                    int[] ids2 = new int[1];
                    ids2[0] = id;
                    ng2.ids = ids2;
                }

                System.out.println(">>> Iteration: " + iteration );
                parseFullNodeList( fn, paareBoth, paareOne, ng2 );            

                // nach einer Iteration kommt hier an: 
                
                NodePairList npList_ONE = new NodePairList( paareOne );
                NodePairList npList_BOTH = new NodePairList( paareBoth );

                File f1 = new File( ng.fn + "_" + id + "_" + iteration + "___LINKED_both.pairs.dat" );
                File f2 = new File( ng.fn + "_" + id + "_" + iteration + "___LINKED_one.pairs.dat" );

                npList_BOTH.store(f1);
                npList_ONE.store(f2);

                System.out.println("\t> both=" + paareBoth.size() );
                System.out.println("\t> one =" + paareOne.size() );

                NodeGroup fBoth = new NodeGroup(paareBoth);
                NodeGroup fOne = new NodeGroup(paareOne);

                fBoth.fn = ng.fn + "_" + id + "_" + iteration + "___LINKED_both.ng";
                fOne.fn =  ng.fn + "_" + id + "_" + iteration + "___LINKED_one.ng";

                try {
                    fBoth.store();
                    fOne.store();
                }
                catch (Exception ex) {
                    Logger.getLogger(StaticLinkManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ng2 = fOne;
                
                
                
                tl.setStamp("END of iteration " + iteration + " for seed-id=" + id );
                
                iteration++;
            }
        }
        
        tl.setStamp("END");

        System.out.println( tl.toString() );
    };

    
    static int doppelte = 0;


    private void parseFullNodeList( String fn, Vector<NodePair> paareBoth, Vector<NodePair> paareOne, NodeGroup ng ) throws FileNotFoundException, IOException {
        
        System.out.println( ">>> *" + ng.ids.length + " IDs sind in Gruppe enthalten ... " );
        if ( ng.ids.length == 0 ) return;

        int lines = 0;
        int nr = 0;
        
        BufferedReader br = new BufferedReader( new FileReader( fn ) );
        while( br.ready() ) {
            String line = br.readLine();
            if ( line.startsWith("#") ) {
                System.out.println( line );
            }
            else {
                if ( nr < 10 ) {
                    // System.out.println( line );
                }
                nr++;
                StringTokenizer st = new StringTokenizer( line );
                String s1 = st.nextToken();
                String s2 = st.nextToken();
                
                // String d = st.nextToken();
                
                NodePair p = new NodePair();
                p.pageIDA = Integer.parseInt(s1);
                p.pageIDB = Integer.parseInt(s2);
                
                // p.dateOfCreation = d;

                boolean[] b = ng.belongsNodeToGroup(p);

                if ( b[1] ) {
                    if ( !paareBoth.contains(p) ) { 
                        paareBoth.add(p);
                    }
                    else { 
                        doppelte++;
                    };
                }
                if ( b[0] && !b[1] ) {  // nur wenn NICHT beide !!!
                     
                    paareOne.add(p);
                }

                // if ( lines < max ) System.out.println( p.toString() );

                lines++;
            }
            
    
        }
        System.out.println(">>> Finished after n=" + lines + " links.  Doppelte:=" + doppelte );
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