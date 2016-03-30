/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.kw5;

import org.apache.hadoopts.data.series.Messreihe;
import java.io.File;
import java.util.Vector;
import research.wikinetworks.NodePair;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import experiments.crosscorrelation.KreuzKorrelation;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class CheckPairsForSymmetricCC {
    
    public static void main( String[] args ) throws Exception {
        
        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
        
        // File fn = NodeGroup.selectNodegroupFile();
        File fn = new File("G:\\DEV\\MLU\\Wiki\\TimeSeriesToolbox\\60_1000_most_active_by_access.dat");
        NodeGroup ng = new NodeGroup( fn );
        ng.load();
        ng.checkAccessTimeSeries();
        
//        NodePair p1 = new NodePair( "[999,961]" , ng );
//        NodePair p2 = new NodePair( "[961,999]" , ng );

        NodePair p1 = new NodePair( 2077655, 2646305 , ng );
        NodePair p2 = new NodePair( 1638040, 1484539 , ng );
        
//G:/PHYSICS/PHASE2/data/out/access_ts_h/PageID_2077655. nrv.h.dat 	true
//G:/PHYSICS/PHASE2/data/out/access_ts_h/PageID_2646305. nrv.h.dat 	true
//G:/PHYSICS/PHASE2/data/out/access_ts_h/PageID_1638040. nrv.h.dat 	true
//G:/PHYSICS/PHASE2/data/out/access_ts_h/PageID_1484539. nrv.h.dat 	true

         
        System.out.println( p1 );
        System.out.println( p2 );
        
        Vector<Messreihe> kksE = new Vector<Messreihe>();

        KreuzKorrelation._defaultK = 14;
        
        KreuzKorrelation.debug = true;
        Messreihe histMaxY = new Messreihe();
        histMaxY.setLabel( " edits Hist (tau)");

        Messreihe histSigLevel = new Messreihe();
        histSigLevel.setLabel( " edits Hist (strength)");

        Vector<String> keysOfPairs = new Vector<String>();
        
        boolean v1 = p1._calcCrossCorrelation( ng, ng.accessReihen, histMaxY, histSigLevel, NodePair.wrongPairsE );
        
        boolean v2 = p2._calcCrossCorrelation( ng, ng.accessReihen, histMaxY, histSigLevel, NodePair.wrongPairsE );
        
        String ccResult1 = p1.getResultOfCC();
        String ccResult2 = p2.getResultOfCC();
        
        System.out.println( ccResult1 );
        System.out.println( ccResult2 );
    };
    
}
