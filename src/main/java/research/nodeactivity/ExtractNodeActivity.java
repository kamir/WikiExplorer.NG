/**
 * Diese Tool-Klasse extrahiert aus der SQL-Datenbank zu jeder
 * Node-ID aus einer NodeGroup-Datei die Anzahl der edits.
 *
 **/
package research.nodeactivity;

import com.mysql.jdbc.Connection;
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
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

public class ExtractNodeActivity {

    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_activity/";

    private static Connection con;
    private static Statement st;
    private static WikipediaDB db;


     public static void main(String[] args) throws Exception {

        /**
         *   Verbindung zur DB
         *
         *   beachte:
         *      import db.notebook.WikipediaDB;
         *   ist ggf. anzupassen.
         *
         */
        db = new WikipediaDB();
        con =(Connection)db.getMySqlConnection();
        st  = con.createStatement();
        st.setFetchSize( 1000 );

        /*
         * ab wann wollen wir edits wissen?
         */
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2009, 00, 01,0,0,0);

        // 0 = 6 h
        // 1 = 4 h
        // 2 = 4 h
        // 3 = ? h ==> FetchSite( Integer.MIN );
        // ABBRUCH
        // Ändern der FetchSize auf 1000 ...
        //    http://download.oracle.com/docs/cd/B25221_04/web.1013/b13593/optimiz011.htm#BEEBHBBG


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
            File fOutFolder = new File( pfad + i + ".dat" );
            BufferedWriter br = new BufferedWriter( new FileWriter( fOutFolder.getAbsolutePath() ));

            extractData(sdFrom, sdTo, br);
        };

         */

        System.out.println( ">>> Work with a list of links ... " );

        File fOutFolder = new File( pfad );
        fOutFolder.mkdirs();

        File fEDITS = new File( pfad + EditActivityFilter.extension );
        BufferedWriter br = new BufferedWriter( new FileWriter( fEDITS.getAbsolutePath() ));
        br.write( "# ");

        /** Node GROUP , zu der die Zahl von EDITS ermittelt werden soll **/
        // VphMean32bit nodeGroup = new VphMean32bit();

        NodeGroup nodeGroup = loadNodeGroup( 72 );

        br.write( "# " + nodeGroup.pfad + "/" + nodeGroup.fn );

        // Für jede ID wird die Zahl der edits nach dem 1.1.2009 ermittelt ...
        for( int i : nodeGroup.ids ) {
            extractData( br, i );
        }

        br.flush();
        br.close();            
        st.close();
        con.close();
    }

    static boolean debug = false;


    static public void extractData( BufferedWriter br, int n ) {
        try {

            // alle Edits ab dem 2009-01-01
            String stmt1 = "SELECT * , count(*) From revisions WHERE Time > Date('2009-01-01') AND PageID = " + n;
            
//            Statement st  = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY );
//            st.setFetchSize(Integer.MIN_VALUE);

            ResultSet rs = st.executeQuery( stmt1 );

            int c = 0;
            int k = 0;

//            int nrOfColumns = rs.getMetaData().getColumnCount();
//            System.out.println( "\nMETADATEN:\n\t" + nrOfColumns  + " Spalten ..." );
//
//            for( int j = 0; j < nrOfColumns ; j++ ) {
//                System.out.print( rs.getMetaData().getColumnLabel(j+1) + "\t" );
//            };
            
            while (rs.next()) {
                if ( c == 100 ) {
                    System.out.print(".");
                    c=0;
                }
                if ( k == 10000 ) {
                    Date d = new Date(System.currentTimeMillis());
                    System.out.println( d );
                    k=0;
                }
                k++;
                c++;
                String line = rs.getObject("LanguageID") + "\t" +
                              rs.getObject("PageID") + "\t" +
                              rs.getObject("count(*)") + "\n" ;

                if (debug) System.out.print(line);

                br.write( line );
            }
        }
        catch (Exception ex) {
            Logger.getLogger(WikipediaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param group
     * @return
     */
    public static NodeGroup loadNodeGroup(int group) {
        NodeGroup ng = new NodeGroup();

        /**
         * only some of them ...
         */
        // ng.fn = "all32bit/lang-" + group + ".ids.dat";

        // all
        ng.fn = "all/lang-" + group + ".ids.dat";

        System.out.println( ">>> use all of ids: [" + ng.fn +"]" );
        ng.load();
        return ng;
    }


    
}

