/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdata.explorer.nutch;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.nutch.scoring.webgraph.LinkDatum;
import org.apache.nutch.scoring.webgraph.Node;

/**
 *
 * @author kamir
 */
public class NutchGraphLoader {
    
    static DirectedSparseGraph graph = null;
    static String fnOffset = "webgraph";

    public static DirectedSparseGraph<String,Number> loadFullGraph( String webgraphPath ) throws IOException {
        graph = new DirectedSparseGraph<String, Number>();
        return loadFullGraph( webgraphPath, graph );
    }
    
    // TREE starts in a selected NODE !!!
    
//    public static Forest<String, Number> loadTreeGraph(String webgraphPath, Forest<String, Number> graph) throws IOException {
//        DirectedSparseGraph<String, Number> g = (DirectedSparseGraph<String, Number>)graph;
//       
//        DirectedSparseGraph<String, Number> gg = loadFullGraph(webgraphPath, g);
//        
//        return (Forest<String, Number>)gg;
//    }

    
    static DirectedSparseGraph<String,Number> loadFullGraph( String webgraphPath, DirectedSparseGraph<String, Number> graph ) throws IOException { 

        
        String nodeFile = webgraphPath + "/" + fnOffset + "/nodes/part-00000/data";
        String inLinkFile = webgraphPath + "/" + fnOffset + "/inlinks/part-00000/data";
        String outLinkFile = webgraphPath + "/" + fnOffset + "/outlinks/current/part-00000/data";
        
        File f1 = new File(nodeFile);
        File f2 = new File(inLinkFile);
        File f3 = new File(outLinkFile);
        
        System.out.println( f1.canRead() + " " + f2.canRead() + " " + f3.canRead() );
        
        SequenceFileLoader sfl2 = new SequenceFileLoader( f2 );
        SequenceFileLoader sfl3 = new SequenceFileLoader( f3 );
                
        
        
        // load NODES => Vertices
        SequenceFileLoader sfl1 = new SequenceFileLoader( f1 );
        sfl1.getMetaData();
        Vector<org.apache.nutch.scoring.webgraph.Node> nodes = null;
        nodes = sfl1.loadNodes( Integer.MAX_VALUE, "part-00000");
        
        // Node Data is mapped to LABELS
        String[] v = createVertices( nodes );

        
        
        // load edges between vertices - In-LINKS
        sfl2.getMetaData();
        Vector<MyLinkDatum> inlinks = null;
        inlinks = sfl2.loadInLinks( Integer.MAX_VALUE, "part-00000");
        
        // load edges between vertices - Out-LINKS
        sfl3.getMetaData();
        Vector<MyLinkDatum> outlinks = null;
        outlinks = sfl3.loadOutLinks( Integer.MAX_VALUE, "part-00000");

        createEdges( inlinks, outlinks, v);

        return graph;
    }
    
    
        /**
     * create edges for this demo graph
     * @param v an array of Vertices to connect
     */
    static int edgeNr = 0;
    static void createEdges( Vector<MyLinkDatum> inlinks, 
            Vector<MyLinkDatum> outlinks, String[] v ) {
        
        // graph.addEdge(new Double(Math.random()), v[0], v[1], EdgeType.DIRECTED);
        int noInl = 0;
        int doubleLinkIN = 0;
        for( MyLinkDatum inl : inlinks ) { 
            
            // System.out.println( inl.getLinkType() + " " + inl.getUrl() + " " + inl.getAnchor() );
            String url = inl.ld.getUrl();
            
            String dest = url;
            String src = inl.sourc;
            // System.out.println( "IN  : " + src + "--->" + dest );
            
            Integer s = invNameHash.get(src);
            Integer d = invNameHash.get(dest);
            
//            Integer s = invNameHash.get(src);
//            Integer d = invNameHash.get(dest);
            
            if (s != null && d != null) { 
                System.out.println( "IN  : " + s + "--->" + d );
                try {
                    graph.addEdge(edgeNr, s, d, EdgeType.DIRECTED);
                    edgeNr++;
                }
                catch(Exception ex) { 
                    doubleLinkIN++;
                    System.out.println(ex.getMessage());
                }
            }
            else { 
                noInl ++;
            }
        }
        
        int noOutl = 0;
        int doubleLinkOUT = 0;
        for( MyLinkDatum outl : outlinks ) { 
            
            // System.out.println( inl.getLinkType() + " " + inl.getUrl() + " " + inl.getAnchor() );
            String url = outl.ld.getUrl();
            
            String dest = url;
            String src = outl.sourc;
            System.out.println( "OUT : " + src + "--->" + dest );
            
            
            Integer s = invNameHash.get(src);
            Integer d = invNameHash.get(dest);
            
            if (s != null && d != null) { 
                System.out.println( "OUT : " + s + "--->" + d );
                try {
                    graph.addEdge(edgeNr, s, d, EdgeType.DIRECTED);
                    edgeNr++;
                }
                catch(Exception ex) { 
                    doubleLinkOUT++;
                }
            }
            else { 
                noOutl ++;
            }
            // graph.addEdge(new Double(1.0), src, dest, EdgeType.DIRECTED);

        }
        
        System.out.println("E: InL  =" + noInl );
        System.out.println("E: OutL =" + noOutl );
        System.out.println("D: InL  =" + doubleLinkIN );
        System.out.println("D: OutL =" + doubleLinkOUT );
        
    }
    
    static Hashtable<String,Integer> invNameHash = new Hashtable<String,Integer>();
    static Hashtable<Integer,String> nameHash = new Hashtable<Integer,String>();
    
    /**
     * create some vertices
     * @param count how many to create
     * @return the Vertices in an array
     */
    static private String[] createVertices(Vector<Node> nodes) {
        String[] v = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            
            org.apache.nutch.scoring.webgraph.Node n = nodes.elementAt(i);
            v[i] = ""+i;
            
            nameHash.put( i, n.getMetadata().get("name") );
            invNameHash.put( n.getMetadata().get("name"),i );
            
            // temporaryly not used ...
            String descr = "(" + getNodeDescription( n, i ) + ")";

            graph.addVertex(v[i]);
        }
        return v;
    }

    private static String getNodeDescription(Node n, int i) {
        String name = n.getMetadata().get("name");
        String st = name; // + " k=("+n.getNumInlinks() +","+n.getNumOutlinks()+")";
        return st;
    }


}
