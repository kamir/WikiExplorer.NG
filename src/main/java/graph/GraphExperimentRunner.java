/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;


import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.visualization.transform.*;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JFrame;
import graph.jung2.InteractiveGraphView;
import experiments.crosscorrelation.CCResultViewer;
import experiments.crosscorrelation.CCResults;
import experiments.crosscorrelation.KreuzKorrelation;
import java.awt.event.MouseEvent;
import research.networks.Edge;

/**
 *
 * @author kamir
 */
public class GraphExperimentRunner extends CCResultViewer {

    public static void main( String[] args ) throws Exception {

        selectNodeGroup();

        langID = Integer.parseInt( ng.getLangID() );
        nrOfNodes = getN( ng.fn );

        File folder = new File(pfadEDITS);
        File[] files = folder.listFiles();

        for( File f : files ) {
            
            int i = 0;
            boolean noBreake = true;
            String lang = getLangID( f.getName() );
            int l = Integer.parseInt(lang);

            if ( isFor_N_Nodes( f.getName() , nrOfNodes ) && l==langID ) {
                System.out.println( ">>> " + f.getAbsolutePath() );
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
                        parseLine( line, nrOfNodes, lang );
                    }
                    // if ( i > 10000 ) noBreake = false;
                }
                // ccr.store();

                TemporalNetworkOfWikipediaNodes nw = new TemporalNetworkOfWikipediaNodes(ccr, 3.2 );
                Graph g = nw.getCC_EDIT_Graph();
                simpleGraph(g, "Wikipedia netzwerk experimente ...");
            }
        }

        
    }

    public static void simpleGraph(Graph<Vertex, Edge> graph, String name) {

        InteractiveGraphView sgv = new InteractiveGraphView( graph ); // Creates the graph...
        // Layout<V, E>, VisualizationViewer<V,E>
        Layout<Integer, String> layout = new CircleLayout(sgv.g);
        //Layout<Integer, String> layout = new ISOMLayout(sgv.g);


        layout.setSize(new Dimension(300,300));
        VisualizationViewer<Integer,String> vv = new VisualizationViewer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(350,350));
        // Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        // Create our "custom" mouse here. We start with a PluggableGraphMouse
        // Then add the plugins you desire.
        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
        gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));

        vv.setGraphMouse(gm);
 

        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
