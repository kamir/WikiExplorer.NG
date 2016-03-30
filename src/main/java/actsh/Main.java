package actsh;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.io.StreamCopier;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;


public class Main {

    /**
     * Kopiert die Access-Zeitreihen der 100 meißt editierten ...
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // NodeGroups festlegen ...
        NodeGroup.pfad = "./data/in/node_groups/";
       
        String[] lang = { "52", "60", "62", "72", "197" };
        String[] anz = { "100" }; // , "200", "500", "1000", "5000" };

//        String folderIn = "/home/wikidb/WorkingDATA/RESULTS/40/32bit_VphMean/";
//        String folderOut = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/access_ts_h/";

        String folderIn = "G:/PHYSICS/32bit/32bit_VphMean/";
        String folderOut = "G:/PHYSICS/PHASE2/data/out/access_ts_h/";

        int c = 0;
//        for( String l : lang ) {
//
//            for( String z : anz ) {
                // für alle IDs der Gruppe die reihe kopieren ...
//                NodeGroup g = new NodeGroup( l +"_"+ z +"_most_active_by_edist.dat" );

                NodeGroup g = new VphMean32bit();
                g.load();

                // int i = g.getIdsAsVector().size();
                // System.out.println("="+ i +"  - " + z );
                
                Vector<Integer> ids = g.getIdsAsVector();
                for( int i : ids  ) {
                    String fn = "PageID_"+i+".txt";
                    File a = new File( folderIn + fn );
                    File b = new File( folderOut + i +"_vph.dat" );
                    
                    if ( !a.canRead() ) {
                        System.out.println( c+ "\t" + fn );
                        c++;
                    }
                    else {
                        StreamCopier.copy(a, b);

                    }
                }
//            }
//        }

    }

}
