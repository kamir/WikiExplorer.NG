/*
 * Die Zahl der Edits einer Reihe musste manuell ermittelt werden.
 * Dann schreibe ich die Daten in dir DB zurück, um mit einfachen
 * SQL Befehlen auf die Inhalte zurück zu greifen.
 *
 * Vorselection erfolgte mit:
 * G:/PHYSICS/PHASE2/data/out/node_groups/32bit_VphMean.dat
 *
 * Zwischenresultate:
 * G:/PHYSICS/PHASE2/data/out/node_activity/all_activity_by_edits.dat
 */

package extraction;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.io.BufferedReader;
import java.io.File; 
import java.io.FileReader;
import java.util.StringTokenizer;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

/**
 *
 * @author kamir
 */
public class NodeActivityCountUpdater {


    static File in = new File("G:/PHYSICS/PHASE2/data/out/node_activity/all_nodes_activity_by_edits.dat");

    public static DB db;
    public static Connection con;

    static String request = "INSERT INTO activitycounts (PageID,LangID,edits) " +
            "VALUES";

    static boolean doOnlyCheck = false;

    public static void main( String[] args ) throws Exception {

        String info = "Wollen Sie die Editcounts-DB wirklich ändern?\n\n Das ist nicht nötig, außer wenn"
                + "Sie neue Zeitreihen eingefügt haben.\n\n";

        int s = javax.swing.JOptionPane.showConfirmDialog(null, info, "Hinweis", javax.swing.JOptionPane.OK_CANCEL_OPTION);

        if ( s == 2 ) {
            doOnlyCheck = true;
            System.out.println(">>> Führe nur Überpüfung der Konsistenz durch ... ");
        }

        if (db == null) {
            db = new WikipediaDB();
        }

        if (con == null) {
            con = (Connection) db.getMySqlConnection();
        }

        

        // System.out.println( "SQL : " + request );

        BufferedReader br = new BufferedReader( new FileReader( in.getAbsolutePath() ) );

        StringBuffer sb = new StringBuffer();

        int c1 = 0;
        int c2 = 0;

        int counter = 0;
        while( br.ready() ) {
            String line = br.readLine();
            if ( line.length()<1 || line.startsWith("#") || line.startsWith("null")) {
                if ( line.startsWith("null") ) c1++;
            }
            else {
                //System.out.println( line );
                StringTokenizer st = new StringTokenizer(line);
                int lang = Integer.parseInt( st.nextToken() );
                int page = Integer.parseInt( st.nextToken() );
                int edits = Integer.parseInt( st.nextToken() );

                String sd = "("+page+","+lang+","+edits+")";
                if ( sb.toString().length()>0 ) sb.append(",");
                sb.append(sd);
                counter++;
                c2++;

                if ( counter > 1000 ) {
                    // System.out.println(sb.toString());

                    putDoDB( sb.toString() );
                    sb = new StringBuffer();
                    counter = 0;

                    
                    
                }
            }
        }
        if ( counter != 0 ) putDoDB(sb.toString());

        System.out.println( "\n"+c1 + " no edits" );
        System.out.println( c2 + " edits found" );
        System.out.println( (c1+c2) + " all" );

        NodeGroup bg = new VphMean32bit();
        int cs = bg.ids.length - ( c1+c2);
        System.out.println( "\nGroup: [" + bg.fn +"]\n" + bg.ids.length + " nrOfIds\n" + cs + " checksum" );
    };

    private static void putDoDB(String v) throws Exception {
        System.out.print( "." );
        String dataRequest = request +v+";";
        // System.out.println( dataRequest );

        PreparedStatement statement = (PreparedStatement)con.prepareStatement(dataRequest);
        if (!doOnlyCheck) statement.execute();
    }

}
