/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.results;

import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 * Overlapping Clusters are identified here ...
 * 
 * @author kamir
 */
class CombinedClusterResult {

    int TSC = 1; // Mindestgröße des Cluster 
    
    NodeGroup[] ngs = null;
    
    StringBuffer b = new StringBuffer();
    CombinedClusterResult(ClusteringResults cr1, ClusteringResults cr2,  ClusteringResults cr3 ) {
    
        ngs = new NodeGroup[ cr1.ngs.length ];
    
        int x = 0;
        for( NodeGroup ng : cr1.ngs ) { 
                      
            // look if the nodes is in any Cluster of the other Method
            int[] filtered = checkForOverlap( ng.ids , cr2, cr3 );
            
            if ( filtered.length > 0) {
                NodeGroup ngN = new NodeGroup();
                ngN.ids = filtered;
                ngN.checkAccessTimeSeries();
                b.append( "# " + ng.ids.length + "\t " + filtered.length + "\t " + x +"\n" );
                ngs[x] = ngN;
            }
            x++;
        }
        
        
    }

 


    NodeGroup[] getNodeGroups() {
//        for( NodeGroup ng : ngs ) { 
//            ng.checkAccessTimeSeries();
//        }
        System.out.println( b.toString() );
        return ngs;
    }

    private int[] checkForOverlap(int[] nodeIds, ClusteringResults cr2, ClusteringResults cr3) {
        Vector<Integer> temp = new Vector<Integer>();
        
        for ( int i : nodeIds ) { 
            boolean b1 = isInClusterBiggerThan( TSC , i, cr2 );
            if ( b1 ) {
                b1 = isInClusterBiggerThan( TSC , i, cr3 );
            }
            if ( b1 ) temp.add(i);
        }
        
        int[] res = new int[temp.size()];
        int x = 0;
        for( Integer i : temp ) { 
            res[x] = i;
            x++;
        }
        return res;
    }

    private boolean isInClusterBiggerThan(int TSC, int i, ClusteringResults cr2) {
        boolean v = false;
        for( NodeGroup ng : cr2.getNodeGroups() ) {
            if ( ng.ids.length > TSC ) {
                if ( isIn( i, ng.ids ) ) v = true;
            }
        }
        return v;
    }

    private boolean isIn(int i , int[] ids) {
        boolean back = false;
        for( int v : ids ) { 
            if ( v == i ) back = true;
        }
        return back;
    }
    
}
