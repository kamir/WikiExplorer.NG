/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package research.results;

import java.io.File;
 
import java.io.FileFilter;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
class ClusteringResults implements FileFilter {
    
    public static String baseFolder = ".";
    
    NodeGroup[] ngs = null;

    String algorithm = "DEM";
    String ts = "1.0";
    
    ClusteringResults(String algo, double d, NodeGroup ref) {

        // for the filter 
        algorithm = algo;
        ts = d +"";
        
        File f = new File( baseFolder );
        
        File[] goupsFiles = f.listFiles( (FileFilter)this );
        
        ngs = new NodeGroup[ goupsFiles.length ];
                
        int x = 0;
        for( File fi : goupsFiles ) { 
            
            System.out.println( "LOAD:" + fi.getAbsolutePath() );
            
            NodeGroup n = new NodeGroup(fi.getName(),ref);
            n.load();
            n.checkAccessTimeSeries();
            
            ngs[x] = n;
            
            x++;
        }
    }

    NodeGroup[] getNodeGroups() {
        initNodeGroups();
        return ngs;
    }

    boolean isLoaded = false;
    
    private void initNodeGroups() {
        if ( !isLoaded ) {
           for( NodeGroup ng: ngs ) { 
                ng.checkAccessTimeSeries();
           }
           isLoaded = true;
        };   
    }

    @Override
    public boolean accept(File pathname) {
        
        // System.out.println( "(F) > " + pathname.getAbsolutePath() );
        if( pathname.getAbsolutePath().endsWith(".in_clusters.dat") ) {
            
            String file = pathname.getName();
            String fn = pathname.getName();
            String p2 = "_60_1000_most_active_by_access.dat_0_NOTshuffeled_blocked_NOTfiltered_split_0.cc.log_500_"+ts+".in_clusters.dat";
            
//            System.out.println( algorithm+"_"  );
        
            
            if ( file.startsWith( algorithm+"_" ) && file.endsWith(p2) ) {
                //System.out.println( "use : >>> " + pathname.getAbsolutePath() );
                return true;
            }
            return false;
        }
        else
            return false;
    }
 
    
}
