/*
 * Diese Tool-Klasse speichert die Ids der NodeGroup in einer DB unter
 * dem Namen NodeGroup.getTableName() in der Spalte PageID haben.
 */
package research.sqlclient;

import com.mysql.jdbc.Connection;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.TimeLog;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

public class NodeGroupDBTableWriter {

        static DB db = null;
        static Connection con = null;
        static Statement st  = null;

        static boolean debug = true;
        
    /**
     * Test der Verbindung zur DB.
     *
     * @param args
     */

    static NodeGroup ng = new VphMean32bit();

    public static void main(String[] args) throws Exception {

        db = new WikipediaDB();
        con = (Connection) db.getMySqlConnection();
        st  = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY );

        System.out.println( ">>> Insert NodeGroup list into DB. \n>>> NodeGroup: " + ng.fn );

        uploadNG( ng );
        
        st.close();
        con.close();

    }

    public static void selectNodeGroup() {
        File f = NodeGroup.selectNodegroupFile();
        ng = new NodeGroup(f);
        ng.checkForDoubleIds();
    }

    private static void uploadNG(NodeGroup ng) {
        try {

            TimeLog tl = new TimeLog();
            tl.setStamp("[Start] " + ng.getName() + " - insert List of Data ... " );
            
            String ngName = ng.getTabname();
            

            // Tabelle erzeugen ..
            createDBTable( ngName );

            String stmt = "INSERT INTO " + ngName + " VALUES ";

            System.out.println("[START]");
            System.out.println(".");
            System.out.println(".. " + stmt );
            System.out.println("...");
            System.out.println(".... CONNECTION valid=" + con.isValid(5));

            StringBuffer sb = new StringBuffer("(");
            int i = 0;
            for( int id : ng.ids ) {
                if ( i == 65 ) {
                    sendData( stmt + sb.toString() );
                    sb = new StringBuffer("(");
                    i=0;
                }
                if ( !(i==0) ) sb.append(",(");
                sb.append( id + ")");
                i++;
            }
            sendData( stmt + sb.toString() );

            tl.setStamp("[END]");

            System.out.println(tl.toString());
        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createDBTable(String tabname) {
      
      try{
          System.out.println("Try to create table: [" + tabname + "] ...");
          Statement st = con.createStatement();
          String table = "CREATE TABLE " + tabname + " (pageID integer)";
          st.executeUpdate(table);
          System.out.println("Table creation process successfully!");
      }
      catch(SQLException s){
          s.printStackTrace();
          System.out.println("Table all ready exists!");
      }

      try{
          System.out.println("Try to clear table: [" + tabname + "] ...");
          Statement st = con.createStatement();
          String table = "DELETE FROM " + tabname + " WHERE pageID > 0;";
          st.executeUpdate(table);
          System.out.println("Table creation process successfully!");
      }
      catch(SQLException s){
          s.printStackTrace();
      }
    }

    private static void sendData(String string) {
        System.out.println( string );
        try{
          Statement st = con.createStatement();
          st.executeUpdate(string);
      }
      catch(SQLException s){
          s.printStackTrace();
      }
    }
    
    private static void countData(String tab) {
        System.out.println( tab );
        try{
          Statement st = con.createStatement();
          st.executeUpdate("Select count(*) FROM "+tab+" WHERE pageID > 0;");
      }
      catch(SQLException s){
          s.printStackTrace();
      }
    }






}
