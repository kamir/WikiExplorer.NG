/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graph.jung2;

/*
 * InteractiveGraphView3.java
 *
 * Created on March 20, 2007, 7:49 PM; Updated May 29, 2007
 *
 * Copyright March 20, 2007 Grotto Networking
 */

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.event.GraphEvent.Vertex;
import research.networks.Edge;


import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import research.networks.data.WikiEdge;

/**
 * This class shows how to create you own customized graph mouse
 * just by adding the desired plugins to a PluggableGraphMouse object.
 * This was done to show how to create a mouse with less functionality
 * than a DefaultModalGraph mouse and to illustrate the power of
 * the plugin concept.
 * @author Dr. Greg M. Bernstein
 */
public class InteractiveGraphView {

    public Graph g;
    private static Transformer<Integer, Paint> vertexPaint;
    private static Transformer edgeLabels;

    /** Creates a new instance of SimpleGraphView */
    public InteractiveGraphView( Graph<Vertex, Edge> _g ) {
        g = _g;
        if ( g == null ) {
            // Graph<V, E> where V is the type of the vertices and E is the type of the edges
            g = new SparseMultigraph<Integer, String>();
            // Add some vertices and edges
            g.addVertex((Integer)1);
            g.addVertex((Integer)2);
            g.addVertex((Integer)3);
            g.addEdge( new WikiEdge( "Edge-A" ) , 1, 2);
            g.addEdge( new WikiEdge( "Edge-B" ), 2, 3);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InteractiveGraphView sgv = new InteractiveGraphView(null); // Creates the graph...
        // Layout<V, E>, VisualizationViewer<V,E>
        Layout<Integer, String> layout = new CircleLayout(sgv.g);
        layout.setSize(new Dimension(300,300));
        VisualizationViewer<Integer,String> vv = new VisualizationViewer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(350,350));

        // Show vertex and edge labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

        sgv.vertexPaint = new Transformer<Integer, Paint>() {
                    private final Color[] palette = {Color.GREEN, Color.BLUE, Color.RED};

                    public Paint transform(Integer i) {
                        return palette[i.intValue() % palette.length];
                    }
                };

        sgv.edgeLabels = new Transformer() {
            
            @Override
            public Object transform(Object i) {
                WikiEdge e = (WikiEdge)i;
                return (e.toString() + " " + e.getWeight() + "/" + e.getCapacity());
            }
        };

        vv.getRenderContext().setEdgeLabelTransformer( sgv.edgeLabels );
        vv.getRenderContext().setVertexFillPaintTransformer( sgv.vertexPaint );

        // Create our "custom" mouse here. We start with a PluggableGraphMouse
        // Then add the plugins you desire.
        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
        gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));

        vv.setGraphMouse(gm);
        JFrame frame = new JFrame("Interactive Graph View ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }



}

