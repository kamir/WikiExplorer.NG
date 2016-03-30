/*
 *  The EdgeFilter goes through the lists of created and deleted links
 *  and selects all within an special ID-List (NodeGroup)
 * 
 *  There is no Time selection!
 *
 *
 */

package research.networks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

/**
 *
 * @author kamir
 */
public class EdgeFilter {
    
    private static boolean verbose = false;

    public static void main( String args[] ) throws FileNotFoundException, IOException {

        String langLabel = "?";
        langLabel = javax.swing.JOptionPane.showInputDialog("LANG_ID",langLabel);

        doExtract(langLabel);

    }

    public static void doExtract( String langLabel ) throws FileNotFoundException, IOException {
        String pfad = LinkCreationAndDestructionDetector.pfad;

        // NodeGroup liste = new NodeGroup( langLabel + EditActivityFilter.extension );
        NodeGroup liste = new VphMean32bit();

        verbose = true;
        /**
         * Für eine NodeGroup wird hier der Eingabe und Ausgabestrom
         * vorbereitetder ...
         **/
        Vector<Integer> ids = liste.getIdsAsVector();

        // Eingabedaten
        File f1 = new File( LinkCreationAndDestructionDetector.pfad + "all_links_creation.dat" );
        System.out.println( f1.getAbsolutePath() );
        File f2 = new File( LinkCreationAndDestructionDetector.pfad + "all_links_destruction.dat" );
        System.out.println( f2.getAbsolutePath() );

        BufferedReader brCREATION = new BufferedReader( new FileReader( f1.getAbsolutePath() ) );
        BufferedReader brDESTRUCTION = new BufferedReader( new FileReader( f2.getAbsolutePath() ) );

        String newPfad = pfad + "/"+ langLabel + "/";
        File f3 = new File( newPfad );
        if ( !f3.exists() ) f3.mkdirs();
        
        BufferedWriter bwOUT_CREA = new BufferedWriter( new FileWriter( newPfad + langLabel + "_created_links.dat" ));
        BufferedWriter bwOUT_DEST = new BufferedWriter( new FileWriter( newPfad + langLabel + "_deleted_links.dat" ));

        /** Hier erfolgt die eigentliche Extraction **/
        filterCreatedLinks( brCREATION, bwOUT_CREA, ids );
        filterDestoyedLinks( brDESTRUCTION, bwOUT_DEST, ids );
    };


    
    public static void filterDestoyedLinks( BufferedReader brDESTRUCTION, BufferedWriter bwOUT_DEST, Vector<Integer> ids) throws IOException {
        long t1 = System.currentTimeMillis();
        
        int c = 0;
        int d = 0;
        String line = null;

        // Alle erzeugten Links filtern ...
        while ( brDESTRUCTION.ready() ) {

            line = brDESTRUCTION.readLine();

            c++;
            d++;

            Date da = new Date( System.currentTimeMillis() );

            if( d == 100000 ) {
                d=0;
                if (verbose) System.out.println( c + "\t" + da );
            };

            String[] cols = line.split("\t");

            Integer src = Integer.parseInt( cols[0] );
            Integer dest = Integer.parseInt( cols[1] );

            boolean containsSRC = ids.contains( src );
            boolean containsDEST = ids.contains( dest );

            if (  containsSRC || containsDEST ) {
                bwOUT_DEST.write(line);
                bwOUT_DEST.write("\n");
            }
        }
        long t2 = System.currentTimeMillis();

        bwOUT_DEST.flush();
        bwOUT_DEST.close();

        System.out.println( c );

        System.out.println( (t2 -t2) / 1000 / 60 );
    };

    public static void filterCreatedLinks( BufferedReader brCREATION, BufferedWriter bwOUT_CREA, Vector<Integer> ids) throws IOException {

        long t1 = System.currentTimeMillis();

        int c = 0;
        int d = 0;
        String line = null;

        // Alle erzeugten Links filtern ...
        while ( brCREATION.ready() ) {

            line = brCREATION.readLine();

            c++;
            d++;

            Date da = new Date( System.currentTimeMillis() );

            if( d == 100000 ) {
                d=0;
                if ( verbose ) System.out.println( c + "\t" + da );
            };

            String[] cols = line.split("\t");

            Integer src = Integer.parseInt( cols[0] );
            Integer dest = Integer.parseInt( cols[1] );

            boolean containsSRC = ids.contains( src );
            boolean containsDEST = ids.contains( dest );

            if (  containsSRC || containsDEST ) {
                bwOUT_CREA.write(line);
                bwOUT_CREA.write("\n");

            }
        }
        long t2 = System.currentTimeMillis();

        bwOUT_CREA.flush();
        bwOUT_CREA.close();

        System.out.println( c );

        System.out.println( (t2 -t2) / 1000 / 60 );
    };


}
