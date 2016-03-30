/*
 * Diese Tool-Klasse extrahiert aus der Datenbank die Liste
 * aller Links, die sich in der DB befinden und auch einen
 * Bezug zur übergebenen NodeGroup haben.
 */
package research.sqlclient;

import research.networks.*;
import com.mysql.jdbc.Connection;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.TimeLog;
import com.cloudera.wikiexplorer.ng.util.nodegroups.AllNodesGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

public class JoinLinkExtractor2012 {

        static DB db = null;
        static Connection con = null;
        static Statement st  = null;

        static public String pfad = "G:/PHYSICS/PHASE2/data/out/links/";

        static boolean debug = true;
        
    static NodeGroup ng = new AllNodesGroup();
    
    public static void main(String[] args) throws Exception {

        db = new WikipediaDB();
        con = (Connection) db.getMySqlConnection();
        st  = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY );
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2009, 00, 01,0,0,0);

        // 0 = 6 h
        // 1 = 4 h
        // 2 = 4 h
        // 3 = ? h ==> FetchSite( Integer.MIN );
        // ABBRUCH
        // Ändern der FetchSize auf 1000 ...
        st.setFetchSize(Integer.MIN_VALUE);

        System.out.println( ">>> Extract full list of all links ... for NodeGroup: " + ng.getName() );

        File f1 = new File( pfad + ng.fn + "_links_table.dat" );
        
        BufferedWriter br1 = new BufferedWriter( new FileWriter( f1.getAbsolutePath() ));

        extractData(br1);
        
        br1.flush();

        st.close();
        con.close();

    }


    static String stmt1 = "SELECT links.Source, links.Dest, revisions.Time From links,revisions WHERE RevisionIDStart=RevisionID " ;
    static String stmt2 = "SELECT links.Source, links.Dest FROM links" ;

    static public void extractData( BufferedWriter br ) {

        try {

            TimeLog tl = new TimeLog();
            tl.setStamp("[Start] " + ng.getName() + " - extract full list of links." );

            System.out.println("[START]");
            System.out.println(".");
            System.out.println(".. " + stmt1 );
            System.out.println("...");
            System.out.println(".... CONNECTION valid=" + con.isValid(5));

//            ResultSet rs = st.executeQuery( stmt1 );  // only created
            ResultSet rs = st.executeQuery( stmt2 );  // all

            System.out.println(".....");

            if ( debug ) {
                int nrOfColumns = rs.getMetaData().getColumnCount();

                System.out.print("...... " + nrOfColumns  + " Spalten : [" );

                for( int j = 0; j < nrOfColumns ; j++ ) {
                    System.out.print( rs.getMetaData().getColumnLabel(j+1) + "\t" );
                }
            }
            System.out.println("]\n.......");
            int d = 0;
            int c = 0;
            Date da = new Date( System.currentTimeMillis() );
            System.out.println( "\n"+ d + "\t" +da );
            while (rs.next()) {
               c++;
               d++;
               if ( c==1000000 ) {
                   da = new Date( System.currentTimeMillis() );
                   System.out.println( d + "\t" +da );
                   c=0;
                }
                // System.out.println( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) );
                // br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) + "\n" );
                br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\n" );

            }
            br.flush();
            tl.setStamp(">>> nr of links n=" + d);
            tl.setStamp("[END]");

            System.out.println(tl.toString());
        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




}
