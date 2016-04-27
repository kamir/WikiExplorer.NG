/*
 * This Generator creates Time-Series for an experiment to analyze the influence 
 * LTM on correlation strength.
 *   
 *    5 different betas are used.
 * 
 *    1 chart to check individual series is created for valiation
 */
package experiments;

import com.cloudera.wikipedia.explorer.ResultManager;
import experiments.crosscorrelation.CCProzessor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.hadoopts.data.series.Messreihe;

import java.util.Vector;

import org.apache.hadoopts.chart.simple.MultiChart;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JButton;

import m3.wikipedia.corpus.extractor.NetDensityCalc;

import metadata.ExperimentDescriptor;
import org.apache.hadoopts.app.bucketanalyser.ICorrelator;

import org.apache.hadoopts.app.bucketanalyser.TSOperationControlerPanel;
import org.apache.hadoopts.hadoopts.buckets.BucketLoader;
import org.apache.hadoopts.hadoopts.core.TSBucket;

import statistics.HaeufigkeitsZaehlerDoubleSIMPLE;

/**
 *
 * @author kamir
 */
@ExperimentDescriptor(
        author = "Mirko Kämpf",
        date = "23/02/2016",
        currentRevision = 2,
        lastModified = "30/03/2016",
        lastModifiedBy = "Mirko Kämpf",
        // Note array notation
        contributors = {"n.a."},
        topics = {"correlation properties", "link strength calculation"},
        tags = {"calibration"}
)
public class Correlator implements ICorrelator, ActionListener {

    String label = "CCCorrelator";
    StringBuffer log = null;

    Vector<Messreihe> testsA = null;
    
    TSOperationControlerPanel p = null;

    public static Correlator getInstance() {
        Correlator c = new Correlator();
        return c;
    }

    @Override
    public void calcSingleBucketCorrelations(Vector<Messreihe> testsA, String label) {

        try {
            /**
             * TODO:
             *
             * Refactor this component ...
             */
            NetDensityCalc ndc = new NetDensityCalc();

            File f = new File(
                            "./" + this.label + "/" 
                            + TSOperationControlerPanel.label_of_EXPERIMENT + ".LINKS.tsv");
            
            if ( !f.getParentFile().exists() )
                f.getParentFile().mkdirs();
            
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter( f ));

            double ts = -1000;

            boolean showLegend = true;

            // SLICE Nr
            int runID = 0;

            ResultManager.mode = 1;

            HaeufigkeitsZaehlerDoubleSIMPLE r1 = CCProzessor.getPartial(testsA, testsA, false, ts, null, ndc, label + "_RAW", bw, runID, false);

            HaeufigkeitsZaehlerDoubleSIMPLE r2 = CCProzessor.getPartial(testsA, testsA, true, ts, null, ndc, label + "_SHUFFLE", bw, runID, false);

            Vector<Messreihe> vr = new Vector<Messreihe>();
            vr.add(r1.getHistogramNORM());
            vr.add(r2.getHistogramNORM());

            MultiChart.xRangDEFAULT_MIN = -1;
            MultiChart.xRangDEFAULT_MAX = 1;
            MultiChart.open(vr, true, TSOperationControlerPanel.label_of_EXPERIMENT);

            System.out.println(">>> Link-Creation-Mode:  " + ResultManager.mode);

            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public JButton getButton() {
        JButton jb = new JButton( this.label );
        jb.addActionListener((ActionListener) this);
        return jb;
    }

    @Override
    public void showParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        calcSingleBucketCorrelations( this.testsA , "DEMO_1" );
    }

    @Override
    public void setTSOperationControlerPanel(TSOperationControlerPanel pnl) {

        p = pnl;
        this.testsA = p.rows;
        
        p.addCorrelationButton( this.getButton() );
        
    }

}
