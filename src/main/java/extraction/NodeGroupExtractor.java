/*
 * Zu einer Sprache werden nun alle NodeIds ermittelt und
 * extrahiert.
 *
 */

package extraction;

import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

/**
 *
 * @author kamir
 */
public class NodeGroupExtractor {

    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_groups/";

    public static StringBuffer sb = new StringBuffer();
    public static WikipediaDB db;
    public static Connection con;

    public static void main( String[] args ) throws Exception {

        int lang[] = { 52, 60, 62, 72, 197 };

//         String group = "52-de";
//       String group = "62-es";
//       String group = "72-fr";
//       String group = "60-en";
//       String group = "197-ru";

        int i = 0;

        String fn = "all/lang-" + lang[i] + ".ids.dat";
        String path = pfad + fn;
        BufferedWriter br = new BufferedWriter( new FileWriter( path ) );

        if (db == null) {
            db = new WikipediaDB();
            db.setDB_to_wiki1();
        }

        if (con == null) {
            con = db.getMySqlConnection();
        }


        String request = "Select PageID, LanguageID " +
            "from pagenames where pagenames.LanguageID=? ";

        

        PreparedStatement statement = con.prepareStatement(request);
        statement.setInt(1, lang[i] ); // pro Request eine Page-ID

        System.out.println( ">>> SQL : " + statement.toString() );

        ResultSet rs;
        rs = statement.executeQuery();

        String nl = "\n";
        Integer pageID = 0;
        int counter = 0;
        while (rs.next()) {
            pageID = rs.getInt(1);
            br.write( pageID + nl );
            counter++;
        }
        statement.close();
        br.flush();
        System.out.println(">>> Lang=" + lang[i] + "  counter=" + counter + ". ");
    }

}
