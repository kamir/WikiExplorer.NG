package research.wikinetworks;

import com.mysql.jdbc.Statement;
// hier wird die DB ausgewählt ...
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * Der Pagename Loader ermittelt zur ID den Seitennamen und
 * öffnet den Artikel im Browser.
 *
 * Jeder ermittelte Name wird im Cache gepuffert.
 *
 * @author kamir
 */
public class PageNameLoader {
    
    private PageNameLoader() {};

    public static void init() {
        db = new WikipediaDB();
        db.setDB_to_wiki1();
        isInitialized = true;
    };

    static public boolean isInitialized = false;
    static public boolean debug = false;
    static public boolean verbose = false;

    static WikipediaDB db = null;
    static Connection con = null;

    public static Connection getMySqlConnection() throws Exception {
        if (!isInitialized) init();
        return db.getMySqlConnection();
    }

    public static void main(String[] agrs ) {
        getPagenameForId( 12745725);
        getPagenameForId( 3357599);
    };

    public static Hashtable<Integer, String> cache = new Hashtable<Integer,String>();
    

    /**
     * @param args the command line arguments
     */
    public static String getPagenameForId( int id ) {
        
        if ( verbose ) System.out.print(">>> lookup PageID id=" + id );
        String name = "?";

        if ( cache.containsKey( id ) ) {
            name = cache.get(id);
        }
        else {
            try {

                    TimeLog tl = new TimeLog();
                    int counter1 = 0;

                    con = getMySqlConnection();

                    Statement prepstmt1 = (Statement) con.createStatement();
                    boolean ok = prepstmt1.execute("select PageName from pagenames where PageID=" + id );

                    ResultSet rs = prepstmt1.getResultSet();

                    while (rs.next()) {
                        counter1++;
                        name = rs.getString(1);

                    }
                    prepstmt1.close();

            } catch (SQLException ex) {
                Logger.getLogger(PageNameLoader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(PageNameLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if ( cache.containsKey( id ) ) {

        }
        else {
            cache.put(id, name);
        }
        if ( verbose ) System.out.println( " Name: " + name );
        return name;
    }


}
