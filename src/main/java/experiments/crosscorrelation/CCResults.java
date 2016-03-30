/*
 */

package experiments.crosscorrelation;

import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.awt.Container;
import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import research.wikinetworks.NodePair;

/**
 *
 * @author kamir
 */
public class CCResults {

    public int nrOfNodes = 0;
    public String lang = "";

    public Vector<NodePair> paare = new Vector<NodePair>();

    public void addPaar(NodePair np) {
        if ( !paare.contains(np)) paare.add(np);
        
    }

    void showStatus() {
        System.err.println( lang + "_" + nrOfNodes + " => " + paare.size() );
        CCResultsPanel.open(this);
    }

    Messreihe histMaxY = null;
    Messreihe histSigLevel = null;
    Messreihe histMaxYO = null;
    Messreihe histSigLevelO = null;

    public void store() throws FileNotFoundException {
//        FileOutputStream os = new FileOutputStream("./" + lang + "_" + nrOfNodes + "CCResults.xml" );
//        XMLEncoder encoder = new XMLEncoder(os);
//        encoder.writeObject(this);
//        encoder.close();
    };


    Vector<NodePair> selected = new Vector<NodePair>();

    public void createHistogrammDataSets( double t_low, double t_high ) {

        selected = new Vector<NodePair>();

        histMaxY = new Messreihe();
        histMaxY.setLabel( lang + "_" + nrOfNodes + " Hist (maxY at \u03C4)");

        histSigLevel = new Messreihe();
        histSigLevel.setLabel( lang + "_" + nrOfNodes + " Hist (stength)");

        histMaxYO = new Messreihe();
        histMaxYO.setLabel( lang + "_" + nrOfNodes + " Hist (maxY \u03C4)");

        histSigLevelO = new Messreihe();
        histSigLevelO.setLabel( lang + "_" + nrOfNodes + " Hist (sigLevel) \u03C4=["+t_low+","+t_high+"]");


        for ( NodePair np : paare) {
            if ( np.k >= t_low && np.k <= t_high ) {
                histSigLevelO.addValue( np.signLevel );
                selected.add(np);
            }
            else histSigLevel.addValue( np.signLevel );
 
            histMaxY.addValue( np.k );
        }
    }

    public Container getHistK() {
        return createHistogramm(
                histMaxY ,
                2 * KreuzKorrelation._defaultK + 1,
                -1 * KreuzKorrelation._defaultK-1, KreuzKorrelation._defaultK+1 );
    };

    boolean s1 = true;

    public Vector<NodePair> getValuesForIntervallOf_tau() {
        return selected;
    };
    
    public Container  getHistSigLevel() {
        Vector<Messreihe> histSigLevels = new Vector<Messreihe>();

        double max1 = histSigLevel.getMaxY();
        double max2 = histSigLevelO.getMaxY();

        if ( s1 ) {
            histSigLevels.add(histSigLevelO);
            histSigLevels.add(histSigLevel);
        }
        else {
            histSigLevels.add(histSigLevel);
            histSigLevels.add(histSigLevelO);
        }


        return createHistogramm(
                histSigLevels,
                100,
                -10,
                10 );
    };

    public Container createHistogramm( Vector<Messreihe> mrs, int bins, int min, int max ) {


        HistogramChart demo = new HistogramChart( mrs.elementAt(0).getLabel()  );
        demo.useLegend = true;
        
        for( Messreihe mr : mrs ) {
            demo.addSerieWithBinning( mr, bins, min, max );
        }

        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        //demo.setVisible(true);
        return demo.getContentPane();
    };

    public Container createHistogramm( Messreihe mr, int bins, int min, int max ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, bins, min, max );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        //demo.setVisible(true);
        return demo.getContentPane();
    };

    public DefaultTableModel getTableModel() {
        DefaultTableModel model = new DefaultTableModel(colNames, 0 );

        for( NodePair np : paare ) {
            model.addRow( np.getDataRow() );
        }
        return model;
    };

    public String[] colNames = {
            "node pair",
            "\u03C4",
            "stdDev",
            "y_max",
            "srength"};


    public Messreihe getMessreiheForColumn(int i ) {
        Messreihe model = new Messreihe(colNames[i+1]);
        for( NodePair np : paare ) {
            switch(i) {
                case 0: {
                    model.addValue( np.k );
                    break;
                }
                case 1: {
                    model.addValue( np.stdDev );
                    break;
                }
                case 2: {
                    model.addValue( np.maxY );
                    break;
                }
                case 3: {
                    model.addValue( np.signLevel );
                    break;
                }

            }
        }
        return model;
    };



 

}
