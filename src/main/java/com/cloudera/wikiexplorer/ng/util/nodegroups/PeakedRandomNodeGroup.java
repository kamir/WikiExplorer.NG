package com.cloudera.wikiexplorer.ng.util.nodegroups;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.IOException;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class PeakedRandomNodeGroup extends NodeGroup {

    int defaulSize = 100;

    public PeakedRandomNodeGroup() throws IOException {
        this( 100 );
    };

    public PeakedRandomNodeGroup(int nrids) throws IOException {
        this.defaulSize = nrids;
        this.ids = new int[defaulSize];
        for( int i = 0; i < defaulSize; i++ ) {
            ids[i] = i;
        }
        this.fn = "random_nodes" + PeakedRandomNodeGroup.getStateExtension();
        this._langID = "-1";
        this.setFull();
    }

    boolean unchecked = true;
    @Override
    public boolean checkAccessTimeSeries() {
        if ( unchecked ) {
            for( int id : ids ) {
//                Messreihe mr = CheckInfluenceOfSingelPeaks.createRandomSeries_A_Peaks(id +  "_rand_access" );

                // mr.doFilter(3);

//                this.getAaccessReihen().add(mr);
            }
            unchecked = false;
        }
        return true;
    }

    @Override
    public boolean checkEditTimeSeries() {
        for( int id : ids ) {
//            Messreihe mr = CheckInfluenceOfSingelPeaks.createRandomSeries_A_Peaks( id +  "_rand_edit" );
//            this.editReihen.add(mr);
        }
        return true;
    }


    public static void main( String[] args ) throws IOException {

        stdlib.StdRandom.initRandomGen(0);

        PeakedRandomNodeGroup ng = new PeakedRandomNodeGroup( 10 );
        ng.checkAccessTimeSeries();
        ng.checkEditTimeSeries();

        ng._initWhatToUse();
        
        MultiChart.open(ng.getAaccessReihen(), false);
        MultiChart.open(ng.editReihen, false);

    }



}
