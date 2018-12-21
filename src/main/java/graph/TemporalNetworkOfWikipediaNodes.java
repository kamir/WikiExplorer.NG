/*
 * This is a mapper class to convert data structures from Jung2 
 * to my own toolset.
 *
 * 
 */

package graph;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import research.networks.LinkCreationAndDestructionDetector;
import research.networks.data.WikiEdge;
import research.networks.data.WikiVertice;
import research.wikinetworks.NodePair;
import research.wikinetworks.PageNameLoader;
import experiments.crosscorrelation.CCResults;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class TemporalNetworkOfWikipediaNodes {
    
        /**
     * Create a full linked network for test.
     * 
     * @return 
     */
    public static UndirectedGraph<WikiVertice,WikiEdge> createNetworkFromHashedTimeSeriesObject(
            TimeSeriesObject mr) {

//                UndirectedGraph<String,Number> g = 
//                        new UndirectedSparseMultigraph<String,Number>();
                
                UndirectedGraph<WikiVertice,WikiEdge> g = 
                        new UndirectedSparseMultigraph<WikiVertice,WikiEdge>();

                for( Object o : mr.hashedValues.keySet() ) { 
                    String key = (String)o;
                    String[] ids = key.split("_");

                    double v = (Double)mr.hashedValues.get(o);
                    
                    String i1 = ids[0];
                    String i2 = ids[1];
//                    g.addEdge(v , i1, i2);
                    
                    WikiEdge we = new WikiEdge( i1 + " " + i2 );
                    WikiVertice wv1 = new WikiVertice( Integer.parseInt(i1) );
                    WikiVertice wv2 = new WikiVertice( Integer.parseInt(i2) );
                    g.addEdge( we , wv1, wv2);
		
                }


		List<WikiVertice> index = new ArrayList<WikiVertice>();
		index.addAll(g.getVertices());

		return g;
    };

    /**
     * Create a full linked network for test.
     * 
     * @return 
     */
    public static Graph<String,Number> createSampleNetwork() {

                UndirectedGraph<String,Number> g = 
                        new UndirectedSparseMultigraph<String,Number>();

		// let's throw in a full linked network
		for (int i = 0; i < 10; i++) {
			for (int j = 0 ; j < 10; j++) {
				String i1 = "" + i;
				String i2 = "" + j;
				g.addEdge( i*10+j, i1, i2);
			}
		}

		List<String> index = new ArrayList<String>();
		index.addAll(g.getVertices());

		return g;
    };

    /**
     * load the netowrks form the CCResultfile. Version: 11-2011 (Tel Aviv)
     */
    CCResults r = null;
    double level = 0.0;
    public TemporalNetworkOfWikipediaNodes(CCResults _r, double _level) {
        r = _r;
        level = _level;
    }

    /**
     * Load the EDIT Network ... based on the data in the Object
     * r.paare (which is an instance of CCResults) 
     * 
     * @return 
     */
    public UndirectedGraph<WikiVertice,WikiEdge> getCC_EDIT_Graph() {
                UndirectedGraph<WikiVertice,WikiEdge> g = 
                        new UndirectedSparseMultigraph<WikiVertice,WikiEdge>();

                for ( NodePair np : r.paare ) {
//                    int i1 = np.id_A;
//	            int i2 = np.id_B;

                    int i1 = np.pageIDA;
	            int i2 = np.pageIDB;

     System.out.println( i1 + " " + PageNameLoader.getPagenameForId(i1) );

                    // Labels setzen für die EDGE????

                    if ( np.signLevel > level ) {
                        WikiEdge we = new WikiEdge( i1 + " " + i2 );
                        WikiVertice wv1 = new WikiVertice(i1);
                        WikiVertice wv2 = new WikiVertice(i2);


                        g.addEdge( we , wv1, wv2);
                        // g.addEdge( np.id_A*r.nrOfNodes+np.id_B, i1, i2);
                    }
		}

		List<WikiVertice> index = new ArrayList<WikiVertice>();
		index.addAll(g.getVertices());

		return g;
    }


    /**
     * Es kann hier einen Konflikt mit NodeGroup und CCResults geben !!!
     */
    String pfad = LinkCreationAndDestructionDetector.pfad;
    String label = "-1_1000";


    UndirectedGraph<String,Number> g = null;
    public UndirectedGraph<String,Number> getGraph() {
        return g;
    };

    /**
     * loads a static network representation from LINK_Lists to study 
     * time evolution of the networks properties.
     * 
     * @param from
     * @param to
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException 
     */
    public TemporalNetworkOfWikipediaNodes(Date from, Date to) 
            throws FileNotFoundException, IOException, ParseException {
        
   String fCREATED = pfad + "/"+ label + "/" + label + "_created_links.dat";
   String fDESTROYED = pfad + "/"+ label + "/" + label + "_deleted_links.dat";

   BufferedReader br = new BufferedReader( new FileReader( fCREATED ));
   
        int c = 0;

        // what nodegroup will be used ???
        String fn = label + "_most_active_by_edist.dat";
        
        NodeGroup ng = new NodeGroup( fn );

        Vector<Integer> ids = ng.getIdsAsVector();

        g = new UndirectedSparseMultigraph<String,Number>();

        int cAll_LINKS = 0;
        int cEXTERNAL_LINKS = 0;
        int cINTERNAL_LINKS = 0;

        while( br.ready() ) {

            String line = br.readLine();
            String[] cols = line.split("\t");

            Integer src = Integer.parseInt( cols[0] );
            Integer dest = Integer.parseInt( cols[1] );
            //Date time = DateFormat.getInstance().parse( cols[2] );

            boolean srcIsIn = ids.contains( src );
            boolean destIsIn = ids.contains( dest );

            if ( srcIsIn && destIsIn ) {
                // let's throw in all created edges ...
                String i1 = "" + src;
                String i2 = "" + dest;
                g.addEdge( c*10, i1, i2);
                cINTERNAL_LINKS++;
            }
            if ( !destIsIn ) cEXTERNAL_LINKS++;
            c++;
            cAll_LINKS++;
        }
        br.close();

        List<String> index = new ArrayList<String>();
        index.addAll(g.getVertices());

        System.out.println( "INT " + cINTERNAL_LINKS );
        System.out.println( "EXT " + cEXTERNAL_LINKS );
        System.out.println( "ALL " + cAll_LINKS );
        System.out.println( "CHECK ==> " + 
                (cAll_LINKS - cINTERNAL_LINKS - cEXTERNAL_LINKS) );


    };

}
