/*
 * Diese Tool-Klasse extrahiert aus der Datenbank die Liste
 * aller Links, die sich in der DB befinden und auch einen
 * Bezug zur übergebenen NodeGroup haben.
 */
package research.sqlclient;

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
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

public class JoinLinkExtractor {

        static DB db = null;
        static Connection con = null;
        static Statement st  = null;

        static public String pfad = "G:/PHYSICS/PHASE2/data/out/link_changes/";

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
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2009, 00, 01,0,0,0);

        // 0 = 6 h
        // 1 = 4 h
        // 2 = 4 h
        // 3 = ? h ==> FetchSite( Integer.MIN );
        // ABBRUCH
        // Ändern der FetchSize auf 1000 ...
        st.setFetchSize(Integer.MIN_VALUE);


        /*** Tages genaue Filterung der Links ***/

        /*  Schleife über 300 Tage ab dem 1.1.2009 0:00
        for ( int i = 0; i< 300 ; i++ ) {
            Date dFrom = cal.getTime();
            cal.add( Calendar.DAY_OF_WEEK, 1 );
            Date dTo = cal.getTime();
            //System.out.println( i+ " \t" + dFrom +" \t" + dTo );
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sdFrom = sdf.format( dFrom );
            String sdTo = sdf.format( dTo );

            System.out.println( i+ " \t" + sdFrom +" \t" + sdTo );
            File f = new File( pfad + i + ".dat" );
            BufferedWriter br = new BufferedWriter( new FileWriter( f.getAbsolutePath() ));

            extractData(sdFrom, sdTo, br);
        };
         */

        System.out.println( ">>> Extract full list of links ... for NodeGroup: " + ng.getName() );

        File f1 = new File( pfad + ng.fn + "_links_creation.dat" );
        File f2 = new File( pfad + ng.fn + "_links_destruction.dat" );
        
        BufferedWriter br1 = new BufferedWriter( new FileWriter( f1.getAbsolutePath() ));
        BufferedWriter br2 = new BufferedWriter( new FileWriter( f2.getAbsolutePath() ));

        extractCreationData(br1);
        
        br1.flush();
        br1.close();

//        extractDestructionData(br2);
//        br2.flush();
//        br2.close();

        st.close();
        con.close();

    }


    static String stmt1 = "SELECT links.Source, links.Dest, revisions.Time From links,revisions WHERE RevisionIDStart=RevisionID AND " ;
    // static String stmt2 = "SELECT * From links,revisions WHERE RevisionIDStart=RevisionID" ;
    static String stmt2 = "SELECT * From links,revisions WHERE RevisionIDEnd=RevisionID" ;

    static public void extractData( BufferedWriter br ) {

        try {

            TimeLog tl = new TimeLog();
            tl.setStamp("[Start] " + ng.getName() + " - extract full list of links." );

            String ngName = ng.getTabname();

            stmt1 = "SELECT links.Source, links.Dest, revisions.Time From links,revisions " +
                           "WHERE RevisionIDStart=RevisionID " +
                           "AND links.Source IN " +
                           "(SELECT DISTINCT " + ngName +".pageID " +
                           "FROM " + ngName + ")";

            System.out.println("[START]");
            System.out.println(".");
            System.out.println(".. " + stmt1 );
            System.out.println("...");
            System.out.println(".... CONNECTION valid=" + con.isValid(5));

            ResultSet rs = st.executeQuery( stmt1 );

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
//            while (rs.next()) {
//               c++;
//               d++;
//               if ( c==1000000 ) {
//                   da = new Date( System.currentTimeMillis() );
//                   System.out.println( d + "\t" +da );
//                   c=0;
//                }
//                // System.out.println( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) );
//                // br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) + "\n" );
//                // br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) + "\n");
//
//            }
            br.flush();
            tl.setStamp(">>> nr of links n=" + d);
            tl.setStamp("[END]");

            System.out.println(tl.toString());
        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    static public void extractData( String from, String to, BufferedWriter br ) {
//        try {
//
//            TimeLog tl = new TimeLog();
//
//            tl.setStamp("[Start] ... " + from + " ---> " + to );
//
//
// // Zähle alle Links die vor dem Datum vorhanden sind.
// String stmt1 = "SELECT * From links, revisions WHERE " +
//                "RevisionIDStart = RevisionID AND Time > DATE(\""+ from + "\") " +
//                "AND Time < DATE(\"" + to + "\")";
//
// // nun mal alle links in eine Liste schreiben ...
//  String stmt2 = "SELECT * From links";
//
//
//
//            DB db = new WikipediaDB();
//            Connection con = (Connection) db.getMySqlConnection();
//            Statement st  = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY );
//            //st.setFetchSize(Integer.MIN_VALUE);
//            st.setFetchSize( 1000 );
//
//            System.out.println("[START]");
//            System.out.println(".");
//
//            System.out.println("..");
//            System.out.println( stmt1 );
//
//            System.out.println("...");
//
//
//            System.out.println(".... CONNECTION valid=" + con.isValid(5));
//
//            ResultSet rs = st.executeQuery( stmt1 );
//
//            System.out.println(".....");
//            int c = 0;
//
//            int nrOfColumns = rs.getMetaData().getColumnCount();
//            System.out.println( nrOfColumns  + " Spalten ..." );
//
//            for( int j = 0; j < nrOfColumns ; j++ ) {
//                System.out.print( rs.getMetaData().getColumnLabel(j+1) + "\t" );
//            };
//
//
//
//            while (rs.next()) {
//                c++;
//                if ( c%100 == 0 ) System.out.print(".");
//                // System.out.println( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) );
//                // br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\t" + rs.getDate( "Time" ) + "\n" );
//                br.write( rs.getInt("Source") + "\t" + rs.getInt("Dest") + "\n" );
//
//            }
//            br.flush();
//            br.close();
//            st.close();
//            con.close();
//            System.out.println("..... #RS=" + c);
//
//
//            tl.setStamp("[END]");
//
//            System.out.println(tl.toString());
//
//
//        }
//        catch (Exception ex) {
//            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

    static public void extractCreationData( BufferedWriter bw ) {
        extractData( bw );
    }

//    static public void extractDestructionData( BufferedWriter bw ) {
//        extractData( bw );
//    }

}
