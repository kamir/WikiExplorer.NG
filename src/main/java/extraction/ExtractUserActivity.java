/**
 * Für die Analyse der Zeitreihen des Zugriffs und der Bearbeitung müssen
 * zunächst diese Reihen aus der SQL-DB extrahiert werden.
 *
 * Dateiformat:
 * ------------
 *
 * Zeit     textLänge      Wachstum
 *
 */
package extraction;

import org.apache.hadoopts.data.series.Zeitreihe;
import org.apache.hadoopts.data.series.Messreihe;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.mlu3.WikipediaDB;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author kamir
 */
public class ExtractUserActivity {

    public static StringBuffer master = new StringBuffer();

    public static DB db;
    public static Connection con;

    public static int z = 0;

    synchronized public static Zeitreihe extractUserActivityForUserID2(int userID) throws Exception {
        master.append(userID+"\n");
        z++;
        return null;
    };


    synchronized public static Zeitreihe extractUserActivityForUserID(int userID) throws Exception {


        if ( db == null ) db = new WikipediaDB();

        if ( con == null ) con = db.getMySqlConnection();

        String request = "Select UserID, Time, TextLength, RevisionID, OriginalRevisionID, PageID from revisions where revisions.UserID=? ORDER by Time ASC";
        PreparedStatement statement = con.prepareStatement(request);
        statement.setInt(1, userID);

        Zeitreihe mr = new Zeitreihe();
        mr.setLabel( "RevisionLog - UserID=" + userID );

        ResultSet rs;
        rs = statement.executeQuery();

        StringBuffer sb1 = new StringBuffer();

        String nl1 = "\n";
        int counter = 0;
        int lastLength = 0;

        Integer nodeID = 0;

        while (rs.next()) {

            counter++;

            // sb1.append(rs1.getInt(1) + nl1);
            Date d =  rs.getDate(2);
            Integer textLength = rs.getInt(3);
            nodeID = rs.getInt(6);

            int wachstum = textLength - lastLength;


            String line = d + "\t" + nodeID + "\t" + textLength + "\t" + wachstum + nl1 ;
            
            sb1.append( line );         

            //mr.addValuePair( d.getTime() , textLength );
        };
        statement.close();

        if ( counter > 0 ) {

            FileWriter fwID1 = new FileWriter( "/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/zr/" + nodeID + ".useractivity.dat" );
            fwID1.write("# Zeit \tnodeID \ttextLaenge \tWachstum\n#\n");

            fwID1.write( sb1.toString() );
            fwID1.flush();
            fwID1.close();

            master.append( userID + "\t" + counter + nl1 );


        }
    
        return mr;
    }

    public static void main( String args[] ) throws Exception {
        extractUserActivityForUserID( 697 );
        System.exit(0);
    }

}
