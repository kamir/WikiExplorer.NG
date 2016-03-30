package research.networks;

import actsh.*;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.io.StreamCopier;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.TimeLog;


public class MainAccessTimeSeriesImporter {

    /**
     * Kopiert die Access-Zeitreihen der 100
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        TimeLog tl = new TimeLog();
        tl.quite = false;
        
        tl.setStamp("Start");

        // NodeGroups festlegen ...
        NodeGroup.pfad = "./data/in/node_groups/";

        //
        // "52", "60"

        String[] lang = { "62", "72", "197" };
        String[] anz = { "100", "200", "500" }; //, "1000", "5000" };

        String folderIn = "/home/wikidb/WorkingDATA/RESULTS/40/32bit_VphMean/";
        String folderOut = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/access_ts_h/";

        int c = 0;
        for( String l : lang ) {

        //String l = lang[0];

            
            for( String z : anz ) {
                String langLabel = l +"_"+ z ;

                tl.setStamp( "Starte: " + langLabel );

                EdgeFilter.doExtract(langLabel);

                tl.setStamp( "Beende: " + langLabel );

            }
        }
        tl.setStamp(folderIn);
        tl.setStamp("ENDE");

    }

}
