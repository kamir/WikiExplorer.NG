package research.results;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import com.cloudera.wikiexplorer.ng.gui.NodeIDSelection;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import research.wikinetworks.PageNameLoader;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class NodeGroupAnalyser {

    public static void main(String args[]) throws Exception {
        stdlib.StdRandom.initRandomGen(1);
        NodeGroupAnalyser analyser = new NodeGroupAnalyser();
        analyser.init();
    }
    
    public void init() { 
        
        // Kontrollanzeige der AccessTS
        /*
         * TimeSeriesObject[] rows = getAccessRowsForAllIds();
         * MultiChart.open( rows, rows.length + " " + ng.fn.substring(0, 11) + " access time series", "h", "#of clicks per hour", false);
         */
        
        ng._initWhatToUse();
        
        try {
            extraction.ExtractEditHistory.work(ng);
        } 
        catch (IOException ex) {
            Logger.getLogger(NodeGroupAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ng.checkAccessTimeSeries();
        ng.checkEditTimeSeries();

        NodeIDSelection.ng = ng;
        NodeIDSelection.open();
    }
    
    NodeGroup ng = null;
    StringBuffer sbCurrentLabels = null;

    public TimeSeriesObject[] getAccessRowsForAllIds() {
        
        sbCurrentLabels = new StringBuffer();
        
        TimeSeriesObject[] mr = new TimeSeriesObject[ng.ids.length];
        boolean mrNA[] = new boolean[ng.ids.length];
        
        int x = 0;
        for (int id : ng.ids) {
            TimeSeriesObject m = ng.loadAccessForOneID(new Integer(id));
            if ( m !=  null ) {
                String l = m.getLabel();
                String name = PageNameLoader.getPagenameForId(id);
                sbCurrentLabels.append( name + "\n");
                m.setLabel(l + " " + name);
                mr[x] = m;
                mrNA[x] = false;
            }
            else { 
                mrNA[x] = true;
            }
            
            x++;
        }
        ng.setNotAvailableAccessTS( mrNA );
        return mr;
    }
    
    
    int lang = 60;
    int nrOfNodes = 500;

    public static String referenceNG = "G:\\DEV\\MLU\\Wiki\\TimeSeriesToolbox\\";
    public static String refNG_file = "page_names_financial2.dat.ids.dat";
     
    
    static NodeGroup ref = null;

    public NodeGroupAnalyser() throws Exception {
        NodeGroup.pfad = referenceNG;
        ref = new NodeGroup(refNG_file);
        ref.load();
        ng = ref;
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
