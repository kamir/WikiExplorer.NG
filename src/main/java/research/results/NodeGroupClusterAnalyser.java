/*
 *  Calculate the results of the Cluster Calculation From 
 *  AGH
 * 
 * 
 */
package research.results;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import com.cloudera.wikiexplorer.ng.gui.NodeIDSelection;
import java.io.File;
import java.util.Vector;
import research.wikinetworks.PageNameLoader;
import org.apache.hadoopts.statphys.detrending.DetrendingMethodFactory;
import org.apache.hadoopts.statphys.detrending.MultiDFATool;
import org.apache.hadoopts.statphys.detrending.methods.IDetrendingMethod;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class NodeGroupClusterAnalyser {

    public static void main(String args[]) throws Exception {
  
        NodeGroupClusterAnalyser analyser = new NodeGroupClusterAnalyser();
        
        double ts = 3.8;
        
        NodeGroup.pfad = "C:\\Users\\kamir\\Documents\\A_" + 60 + "_" + 500 + "\\results\\";
        
        ClusteringResults cr3 = new ClusteringResults("NM", ts, ref );
        ClusteringResults cr2 = new ClusteringResults("SM", ts, ref );
        ClusteringResults cr1 = new ClusteringResults("DEM", ts, ref );
        
        CombinedClusterResult result = new CombinedClusterResult( cr1 , cr3, cr2 );
                        
        // based on the first method a cross-check to all other methods is applyed
        // all nodes, which are not clustered in an other method are removed.
        NodeGroup[] clusters = result.getNodeGroups();

    
            
            // now we go through all cluseters detected by first Algorithm
            for( NodeGroup ng : clusters ) {

                analyser.ng = ng;
                Messreihe[] rows = analyser.getAccessRowsForAllIds();
                MultiChart.open( rows, rows.length + " " + ng.fn.substring(0, 11) + " access time series", "h", "#of clicks per hour", false);
                
                System.out.println( ">>> DFA für anz=" + rows.length + " Reihen berechnen ... ");
                
                Vector<Messreihe> k = new Vector<Messreihe>();
                
                if ( rows.length > 3 ) {
                    for( Messreihe d1 : rows ) { 
                        
                        int N = 299;
                        
                        double[] zr = new double[N];
                        zr = d1.getData()[1];
                        
                        IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(DetrendingMethodFactory.DFA2);
                        int order = dfa.getPara().getGradeOfPolynom();
                        dfa.getPara().setzSValues( 150 );


                        // Anzahl der Werte in der Zeitreihe
                        dfa.setNrOfValues(N);

                        // die Werte für die Fensterbreiten sind zu wählen ...
                        dfa.initIntervalS();
                        dfa.showS();



                        // nun wird das Array mit den Daten der ZR übergeben
                        dfa.setZR(zr);

                        // Start der Berechnung
                        dfa.calc();


                        // Kontrolle
                        
//                        k.add(dfa.getZeitreiheMR());
//                        k.add(dfa.getProfilMR());
                        //k.addAll(dfa.getMRFit());

                        // Übergabe der Ergebnisse ...
                        double[][] results = dfa.getResults();
                        Vector<Messreihe> v = new Vector<Messreihe>();
                        Messreihe mr1 = dfa.getResultsMRLogLog();
                        mr1.setLabel( d1.getLabel() );
                        k.add(mr1);

        
                    }       
                    
                    MultiChart.open( k, rows.length + " DFA", "log(s)", "#log( F(s) )", false); 
                }
                else {
                    System.out.println( "> " + rows.length + " Reihen sind zu wenige!");
                }
                System.out.println(analyser.sbCurrentLabels.toString());
            
            }    

    }
    
    NodeGroup ng = null;
    StringBuffer sbCurrentLabels = null;

    public Messreihe[] getAccessRowsForAllIds() {
        sbCurrentLabels = new StringBuffer();
        Messreihe[] mr = new Messreihe[ng.ids.length];
        int x = 0;
        for (int id : ng.ids) {
            Messreihe m = ng.loadAccessForOneID(new Integer(id));
            String l = m.getLabel();
            String name = "?"; // PageNameLoader.getPagenameForId(id);
            sbCurrentLabels.append( name + "\n");
            m.setLabel(l + " " + name);
            mr[x] = m;
            x++;
        }
        return mr;
    }
    
    
    int lang = 60;
    int nrOfNodes = 500;

    String referenceNG = "G:\\DEV\\MLU\\Wiki\\TimeSeriesToolbox\\";
    String refNG_file = "60_1000_most_active_by_access.dat";
    
    static NodeGroup ref = null;

    public NodeGroupClusterAnalyser() throws Exception {

        NodeGroup.pfad = referenceNG;
        ref = new NodeGroup(refNG_file);
        ref.load();
        ng = ref;

//        NodeGroup.pfad = "C:\\Users\\kamir\\Documents\\A_" + lang + "_" + nrOfNodes + "\\results\\";
//        ClusteringResults.baseFolder = NodeGroup.pfad;

    }

    public static File selectNodeGroupFile() {
        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser(new File(NodeGroup.pfad));
        // jfc.setFileFilter( NodeGroup.getFileFilter() );

        jfc.setDialogTitle("Select a NodeGroup File ...");
        jfc.setSize(800, 600);

        jfc.showOpenDialog(null);

        File f = jfc.getSelectedFile();
        if (f == null) {
            System.err.println(">>> KEINE NodeGruppe gewählt !");
        } else {
            System.err.println(">>> NodeGroup : " + f.getAbsolutePath() + " gewählt !");
        }
        return f;
    }

}
