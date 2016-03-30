/*
 * A Node Group to load the DAX financial data rows to handle them
 * by the NetworyToolbox
 *  
 */

package com.cloudera.wikiexplorer.ng.util.nodegroups;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.io.ColumnValueCalculator;
import org.apache.hadoopts.data.io.MessreihenLoader;
import org.apache.hadoopts.data.io.TSValueCalculator;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import research.wikinetworks.NodePairList;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import research.topics.networks.comparison.CC_Media_ResultMapper;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class DAXFinancialDataNodeGroup extends NodeGroup implements TSValueCalculator {

    String folderName = null;

    public DAXFinancialDataNodeGroup() throws IOException {
        folderName = "P:/DATA/dayly_stock_data/" + "dax"; 
        fn = "ng_FD_DAX";
        name = "ng_FD_DAX";
        initSize();
        this.ids = new int[defaulSize];
        for( int i = 0; i < defaulSize; i++ ) {
            ids[i] = i;
        }
        this._langID = "-1";
        this.setFull();
    };
    
    /**
     * ermittle wieviele Dateien da sind.
     * 
     */
    private void initSize() {
       f = new File( folderName );
       System.out.println(  ">>>  canRead( " + f.getAbsolutePath() + ") => " +  f.canRead());
       File[] all = f.listFiles();
       defaulSize = all.length;
       System.out.println( all.length );
    }
    File f = null;
    
    
    int defaulSize = 0;

 
//
//    public PeakedRandomNodeGroup(int nrids) throws IOException {
//        this.defaulSize = nrids;
//        this.ids = new int[defaulSize];
//        for( int i = 0; i < defaulSize; i++ ) {
//            ids[i] = i;
//        }
//        this.fn = "random_nodes" + PeakedRandomNodeGroup.getStateExtension();
//        this.langID = "-1";
//        this.setFull();
//    }

    boolean unchecked = true;
    
    int selectedColumn = 7;
    
    @Override
    public boolean checkAccessTimeSeries() {
        if ( unchecked ) {
            File[] files = f.listFiles();
            for( File f : files ) {

                TSValueCalculator mapper = (TSValueCalculator)this;
                Messreihe mr = this.loadRows( f, mapper );

                this.getAaccessReihen().add(mr);
            }
            unchecked = false;
        }
        return true;
    }
    
    private Messreihe loadRows(File f, TSValueCalculator mapper) {
        Messreihe mrA = null;
        System.out.println(">>> use network link-table : "
                + f.getAbsolutePath() + " -> " + f.exists());
        System.out.println(">>> use value mapper : " + mapper.getName()); 
        
        mrA = MessreihenLoader.getLoader()._loadMessreihe_For_FD( f, mapper, " ", false);
        return mrA;
        
    }


    @Override
    public boolean checkEditTimeSeries() {
        return false;
    }



    public static void main( String[] args ) throws IOException {

        stdlib.StdRandom.initRandomGen(0);

        DAXFinancialDataNodeGroup ng = new DAXFinancialDataNodeGroup();
        ng.checkAccessTimeSeries();
        ng.checkEditTimeSeries();

        ng._initWhatToUse();
        
        MultiChart.open(ng.getAaccessReihen(), true);

    }

    
    // blockieren von : neglecting weekends and bank holidays etc?
    
    
    // YEAR MONTH DAY OPEN HIGH LOW CLOSE VOLUME ADJ.CLOSE
    @Override
    public double getValue( String[] line) {
        return Double.parseDouble( line[selectedColumn] );
    }

    @Override
    public void setTimeIntervall(Date begin, Date end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getDate(String[] line) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
