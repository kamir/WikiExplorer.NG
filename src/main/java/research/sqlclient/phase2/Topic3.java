package research.sqlclient.phase2;

import extraction.ExtractUserActivity;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * Analyse der USER-Activität ...
 *
 * im Moment nicht so wichtig ...
 *
 * @author kamir
 */
public class Topic3 {

    public static void main( String args[] )  {

        try {

            Vector<String> ids = new Vector<String>();
            FileReader fr = new FileReader( "/home/kamir/NetBeansProjects/SQLClient/data/out/edits/zr/topic1/all.user.editcounts.dat" );
            BufferedReader br = new BufferedReader( fr );
            br.readLine();
            br.readLine();  // Header übergehen ...

            int soll = 0;
            while( br.ready() ) {
                ids.add( br.readLine() );
                soll++;
            };
            br.close();

            TimeLog tl = new TimeLog( false );

            int ist = 0;
            Enumeration<String> en = ids.elements();
            while( en.hasMoreElements() ) {
                ist++;
                int id = Integer.parseInt( en.nextElement() );

                if ( !(id == 820 || id==333) ) {
                // System.out.println( zr );
                tl.setStamp( id + " bearbeitet ... (" + ( (double)ist/(double)soll ) + ")" );

                ExtractUserActivity.extractUserActivityForUserID( id );
                // Thread.currentThread().sleep(2);
            
                };
            };
            tl.setStamp("READY");


            FileWriter fw2 = new FileWriter( "/home/kamir/NetBeansProjects/SQLClient/data/out/edits/zr/all.user.editcounts.clean.dat" );
            fw2.write("# userID\tedits\n");
            fw2.write( ExtractUserActivity.master.toString());
            fw2.flush();
            fw2.close();

        }
        catch( Exception ex) {
            ex.printStackTrace();
        };

    };

}
