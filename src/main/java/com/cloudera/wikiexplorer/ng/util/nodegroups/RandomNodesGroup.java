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
public class RandomNodesGroup extends NodeGroup {
    
    public static int length = 2;

    int defaulSize = 100;

    public RandomNodesGroup() throws IOException {
        this( 100 );
    };

    public RandomNodesGroup(int nrids) throws IOException {
        this.defaulSize = nrids;
        this.ids = new int[defaulSize];
        for( int i = 0; i < defaulSize; i++ ) {
            ids[i] = i;
        }
        this.fn = "random_nodes" + RandomNodesGroup.getStateExtension();
        this._langID = "-1";
        this.setFull();
    }

    boolean unchecked = true;
    @Override
    public boolean checkAccessTimeSeries() {
        if ( unchecked ) {
            for( int id : ids ) {
                Messreihe mr = CheckInfluenceOfSingelPeaks.createRandomSeries_A_Peaks( id +  "_rand_access" );
                //System.err.println( mr.yValues.size() );

                // mr.doFilter(3);

                this.getAaccessReihen().add(mr);
            }
            unchecked = false;
        }
        return true;
    }

    @Override
    public boolean checkEditTimeSeries() {
        for( int id : ids ) {
            Messreihe mr = CheckInfluenceOfSingelPeaks.createRandomSeries_A_Peaks( id +  "_rand_edit" );
            this.editReihen.add(mr);
        }
        return true;
    }


    public static void main( String[] args ) throws IOException {

        stdlib.StdRandom.initRandomGen(0);

        RandomNodesGroup ng = new RandomNodesGroup( 10 );
        ng.checkAccessTimeSeries();
        ng.checkEditTimeSeries();

        ng._initWhatToUse();
        
        MultiChart.open(ng.getAaccessReihen(), false);
        MultiChart.open(ng.editReihen, false);

    }



}
