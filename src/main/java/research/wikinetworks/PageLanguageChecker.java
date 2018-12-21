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
public class PageLanguageChecker {

    public static boolean skipCheck= false;

    public static void init() { 
        db = new WikipediaDB();
        db.setDB_to_wiki1();
        isInitialized = true;
    };

    static public boolean isInitialized = false;
    static public boolean debug = false;

    static WikipediaDB db = null;
    static Connection con = null;

    public static Connection getMySqlConnection() throws Exception {
        if (!isInitialized) init();
        if ( con == null ) con = db.getMySqlConnection();
        return con;
    }

    public static void main(String[] agrs ) {

        System.out.println( check( 125943 , "52" ) );
        System.out.println( check( 6 , "52" ) );
        System.out.println( check( 7 , "52" ) );


    };

    public static Hashtable<Integer, String> cache = new Hashtable<Integer,String>();
    

    /**
     * @param args the command line arguments
     */
    public static boolean check( int id , String lang) {
        boolean back = false;

        if ( skipCheck ) return true;

        if ( debug ) System.out.print(">>> lookup LangID for PageID id=" + id + " (" + lang +")\n" );
        String name = "?";


            try {

                    TimeLog tl = new TimeLog();
                    int counter1 = 0;

                    con = getMySqlConnection();
                    

                    Statement prepstmt1 = (Statement) con.createStatement();
                    String req = "select PageID, LanguageID from pagenames where PageID=" + id;
                    if (debug) System.out.println( req );
                    boolean ok = prepstmt1.execute( req );

                    ResultSet rs = prepstmt1.getResultSet();

                    while (rs.next()) {
                        counter1++;
                        name = rs.getString(2);
                        // System.out.println( name );
                        int l = Integer.parseInt(name);
                        if ( l == Integer.parseInt( lang ) ) back = true;
                    }
                    prepstmt1.close();

            } catch (SQLException ex) {
                Logger.getLogger(PageLanguageChecker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(PageLanguageChecker.class.getName()).log(Level.SEVERE, null, ex);
            }




        return back;
    }


}
