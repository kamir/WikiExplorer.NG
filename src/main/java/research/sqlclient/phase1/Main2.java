package research.sqlclient.phase1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * ... FILTER-Tabelle erstellen ...
 *
 * @author kamir
 *
 * Schreibe die IDs aus der Datei "./data/in/32bit.dat"
 * in die neue Tabelle Filter1, die dann in neuen kombinierten
 * Abfragen genutzt werden kann.
 *
 * Alle einzeln - sehr langsam bei sehr vielen Werten !!!
 *
 */
public class Main2 {

    public static Connection getMySqlConnection() throws Exception {
        String driver = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://localhost/wikipedia";
        String username = "root";
        String password = "";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
    static Hashtable<Double, String> ids = new Hashtable<Double, String>();

    public static void loadIdsForLargeCounts() {
        try {
            int counter = 0;
            BufferedReader br = new BufferedReader(new FileReader("./data/in/32bit2.dat"));
            while (br.ready()) {
                String line = br.readLine();
                Double i = new Double(line);
                ids.put(i, line);
                counter++;
            }
            System.out.println("Anzahl von IDs:" + counter);
        }
        catch (Exception ex) {
            Logger.getLogger(Main2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    ;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {

            TimeLog tl = new TimeLog();

            loadIdsForLargeCounts();

            tl.setStamp("IDs aus Datei geladen.");

            Connection con = getMySqlConnection();

            PreparedStatement prepstmt = con.prepareStatement("Insert into Filter2 (PageID) values (?)");

            Enumeration<Double> en = ids.keys();
            while( en.hasMoreElements() ) {

                Double key = en.nextElement();

                prepstmt.setString(1, ids.get( key ) );
                int count = prepstmt.executeUpdate();

            }

            tl.setStamp("Daten importiert.");

            prepstmt.close();

            String tab = "\t";
            String nl = "\n";

            System.out.println(tl.toString());


        } catch (SQLException ex) {
            Logger.getLogger(Main2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
