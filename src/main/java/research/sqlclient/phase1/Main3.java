package research.sqlclient.phase1;

import com.mysql.jdbc.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * ... Filter erstellen - OPTIMIERT ...
 * @author kamir
 *
 * Schreibe die IDs aus der Datei "./data/in/32bit.dat"
 * in die neue Tabelle Filter1.
 *
 * Immer 1000 Werte auf einmal - bei 10.000 wird es dann wieder langsamer !!!
 *
 */
public class Main3 {

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

    static String dataString = null;

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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        try {

            TimeLog tl = new TimeLog();

            loadIdsForLargeCounts();

            tl.setStamp("IDs aus Datei geladen.");

            Connection con = getMySqlConnection();

            Statement stmt = (Statement) con.createStatement();

            StringBuffer sb = new StringBuffer();
            int c1=0;
            int c2=0;
            int count;

            Enumeration<Double> en = ids.keys();
            while( en.hasMoreElements() ) {
            
                Double key = en.nextElement();


                c1++;
                if ( c1 == 1000 ) {
                    sb.append("("+ ids.get(key)+")");
                    dataString = sb.toString();
                    System.out.print( "." );
                    count = stmt.executeUpdate( "Insert into Filter2 (PageID) values " + dataString  );
                    c2 = c2+count;
                    c1 = 0;
                    sb = new StringBuffer();
                }
                else {
                    sb.append("("+ ids.get(key)+"),");
                }
    
            }
            dataString = sb.toString();
            dataString = dataString.substring( 0, dataString.length()-1);
            System.out.println( dataString );
            count = stmt.executeUpdate( "Insert into Filter2 (PageID) values " + dataString  );
            c2 = c2+count;
            System.out.println(c2);

            tl.setStamp("Daten importiert.");

            stmt.close();

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
