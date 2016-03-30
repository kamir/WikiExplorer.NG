/**
 * Für die Analyse der Zeitreihen des Zugriffs und der Bearbeitung müssen
 * zunächst diese Reihen aus der SQL-DB extrahiert werden.
 *
 * Dateiformat:
 * ------------
 *
 * Zeit     textLänge      Wachstum     date   time
 *
 * Gespeichert werden die Dateien:
 *
 * 1.)
 * 2.)
 * 3.)
 */
package extraction;

import org.apache.hadoopts.data.series.Zeitreihe;
//import sqlclient.phase2.*;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehler;
import org.apache.hadoopts.data.series.Messreihe;
import com.cloudera.wikiexplorer.ng.db.DB;
import com.cloudera.wikiexplorer.ng.db.notebook.WikipediaDB;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import research.sqlclient.phase2.Topic2;
import org.apache.hadoopts.statphys.ris.experimental.ReturnIntervallStatistik2;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.VphMean32bit;

/**
 * Zur Berechnung im Topic6 werden hier die Edit-Zeitreihen aus der DB gelesen
 * und vorverarbeitet.
 *
 * @author kamir
 */
public class ExtractEditHistory {

    static String pfad = "G:/PHYSICS/PHASE2/data/out/edit_events/";
    static boolean loadUserActivity = false;
   
    public static HaeufigkeitsZaehler editCountHistogramm = new HaeufigkeitsZaehler();

    public static Vector<Long> dates;  // in Millisekunden
    public static StringBuffer sb = new StringBuffer();
    public static DB db;
    public static Connection con;
    static Vector<Integer> users = new Vector<Integer>();

    /**
     * Variante 1:
     *
     * SQL-Abfrage und Speichern der 3 Dateien
     * unter Benutzung der klasse "Zeitreihe".
     *
     * @param nodeID
     *
     * @return
     *
     * @throws Exception
     */
    public static Zeitreihe extractEditHistoryForID(int nodeID, boolean storeFiles )
            throws Exception {
        if (db == null) {
            db = new WikipediaDB();
        }

        if (con == null) {
            con = db.getMySqlConnection();
        }

        dates = new Vector<Long>();
        
        String request = Topic2.request;
//
//        String request = "Select UserID, Time, TextLength, " +
//            "PageID from revisions where revisions.PageID=? " +
//            "AND revisions.Time>'2009-01-01 00:00:00' ORDER by Time ASC";

        // System.out.println( "SQL : " + request );

        PreparedStatement statement = con.prepareStatement(request);
        statement.setInt(1, nodeID); // pro Request eine Page-ID

        Zeitreihe mr = new Zeitreihe();
        mr.setLabel("Revisions of NodeID=" + nodeID);

        ResultSet rs;
        rs = statement.executeQuery();

        StringBuffer sb1 = new StringBuffer();

        String nl1 = "\n";
        int editsCounter = 0;
        int lastLength = 0;
        int userID = 0;

        String line ="";

        while (rs.next()) {
      
            editsCounter++;

            Date d = rs.getDate(2);
            Time t = rs.getTime(2);

            Integer textLength = rs.getInt(3);

            userID = rs.getInt(1);

            int wachstum = textLength - lastLength;
            lastLength = textLength;

            // GLOBALE VARIABLE FÜR DIE WEITERVERARBEITUNG ...
            //
            // Zählung der Events in den Zeitintervallen ...
            dates.add(t.getTime() + d.getTime());

            if (storeFiles ) {
                line = d.getTime() + "\t" +
                          textLength + "\t" +
                          wachstum + "\t" +
                          d + "\t" +
                          t + "\t" +
                          userID + nl1;

                sb1.append(line);
            }

            if ( loadUserActivity ) {

                if ( !users.contains( userID ) ) {
                    users.add( userID );
                    // System.out.println( "\t" + users.size() );
                    ExtractUserActivity.extractUserActivityForUserID2(
                    userID );
                }

            }
            if ( ReturnIntervallStatistik2.debug ) {
                System.out.println( t.getTime() + "\t" + d.getTime() + "\tsum=" + (t.getTime() + d.getTime()));
            }
            mr.addValuePair((t.getTime() + d.getTime()), wachstum);
        }
        statement.close();



//        if (editsCounter > 0 && storeFiles) {
        if ( storeFiles) {
            System.out.println( pfad + nodeID + ".revisions.dat" );
//            File f = new File( pfad );
//            f.mkdirs();
//
//            mr.countEventsPerDay();
//            mr.countEventsPerHour();
//            
            FileWriter fwID1 = new FileWriter( pfad +
                                nodeID + ".revisions.dat");
            fwID1.write("# Zeit \ttextLänge \tWachstum \tuserID\n#\n");
            fwID1.write(sb1.toString());
            fwID1.flush();
            fwID1.close();


//            FileWriter fwID2 = new FileWriter( pfad +
//                                nodeID + ".nrOfEvents_over_t.revisions.dat");
//            fwID2.write(mr.countedEvents_h.toString());
//            fwID2.flush();
//            fwID2.close();
//
//            FileWriter fwID4 = new FileWriter( pfad  +
//                                nodeID + ".countedEvents.h.revisions.dat");
//            fwID4.write(mr.countedEvents_h.toString());
//            fwID4.flush();
//            fwID4.close();

//
//            FileWriter fwID3 = new FileWriter(pfad +
//                                nodeID + ".countedEvents.d.revisions.dat");
//            fwID3.write(mr.countedEvents.toString());
//            fwID3.flush();
//            fwID3.close();
//
//            // Hier wird die Zahl der Edits der nodeID=nodeID in die
//            // Histogramm-Logdatei geschrieben.
//            sb.append(nodeID + "\t" + editsCounter + nl1);
//            editCountHistogramm.addData((long) editsCounter);
//
//            
//            File fT = new File( pfad + nodeID + ".countedEvents.d.revisions.dat" );
//            System.out.println( fT.getAbsolutePath() + " => " + ( fT.canRead() && fT.exists() ) );
        }

      return mr;
    }


    /**
     * Variante 1:
     *
     * SQL-Abfrage und Speichern der 3 Dateien
     * unter Benutzung der klasse "Zeitreihe".
     *
     * @param nodeID
     *
     * @return
     *
     * @throws Exception
     */
    public static int countEditsForID( int nodeID ) throws Exception {
        if (db == null) {
            db = new WikipediaDB();
        }

        if (con == null) {
            con = db.getMySqlConnection();
        }
 

        String request = "Select count(revisions.Time) " +
            "from revisions where revisions.PageID=? " +
            "AND revisions.Time>'2009-01-01 00:00:00'";

        // System.out.println( "SQL : " + request );
        int editsCounter = 0;

        PreparedStatement statement = con.prepareStatement(request);
        statement.setInt(1, nodeID); // pro Request eine Page-ID

        ResultSet rs;
        rs = statement.executeQuery();
        rs.next();
        
        editsCounter = rs.getInt(1);
        statement.close();

        return editsCounter;
    }


    // hier wird nun nicht die klaase ZEITREIHE sondern die
    // TimeSeriesFactory für die Aufarbeitugn benutzt ...
    //
    // es werden die Daten verarbeitet, die per SQL-Abfrage schon beschafft wurden.
    public static Messreihe extractEditHistoryForID2(Messreihe mr , int nodeID, int days, int timeScale)
            throws Exception {
         
        int counter = mr.getXValues().size();

        if (counter > 0) {
            mr = TimeSeriesFactory.prepareEditDataSTUNDE( dates, days, timeScale );
        }

        return mr;
    }

    // hier wird nun nicht die klaase ZEITREIHE sondern die
    // TimeSeriesFactory für die Aufarbeitugn benutzt ...
    //
    // eine SQL-Abfrage wird zuerst durchgeführt ...
//    public static Messreihe extractEditHistoryForID2(int nodeID, int days, int timeScale)
//            throws Exception {
//
//        Messreihe mr = extractEditHistoryForID(nodeID, true);
//        int counter = mr.getXValues().size();
//
//        if (counter > 0) {
//            mr = TimeSeriesFactory.prepareEditDataSTUNDE( dates, days, timeScale );
//        }
//
//        return mr;
//    }

    public static void store() {
        editCountHistogramm.calcWS();
        try {
            editCountHistogramm.store();
        } catch (IOException ex) {
            Logger.getLogger(
                    ExtractEditHistory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /*
     * Zur Extraktion der Edit-History unserer MUSTER-Nodes ...
     */
    static public int[] getIDS_to_extract_a() {
        // NodeGroup ng = new NodeGroup();

        VphMean32bit ng = new VphMean32bit();
        ng.load();

        return ng.ids;
    };
    
    
    public static void work( NodeGroup ng ) throws IOException {

        /**
         * Auswahl der NodeGroup ...
         */
        int ids[] = ng.ids;
        
        int k = 0;
        int m = 0;
        int l = 0;
        for (int i = 0; i < ids.length; i++) {
            try {
                if  ( k == 10 ) {
                    System.out.print( "." );
                    k = 0;
                }
                if ( m == 100 ) {
                    m=0;
                    System.out.println( "#" );
                }
                k++;
                m++;
                l++;

                extractEditHistoryForID( ids[i], true );
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws IOException {

        /**
         * Auswahl der NodeGroup ...
         */

        // vorerst nicht alle  ...
        // int[] ids = getIDS_to_extract_C();
        
//       String group = "52-de";        
//       String group = "62-es";
//       String group = "72-fr";
//       String group = "60-en";
//       String group = "197-ru";

        int lang[] = { 52, 60, 62, 72, 197 };
        int j = 1;

        int ids[] = loadNodeGroup( lang[j] );
        
        

        String fn = pfad + "all_nodes_" + lang[j] + ".edits.dat";
        File f = new File( fn );

        BufferedWriter br = new BufferedWriter( new FileWriter( f.getAbsolutePath() ) );
        String line = "# " + fn;
        br.write( line );

        System.out.println("> start to load: " + ids.length + " series from: " + f.getAbsolutePath() );

        
        int k = 0;
        int m = 0;
        int l = 0;
        for (int i = 0; i < ids.length; i++) {
            try {
                if  ( k == 1000 ) {
                    System.out.print( "." );
                    k = 0;
                }
                if ( m == 10000 ) {
                    m=0;
                    System.out.println( l / ids.length );
                }
                k++;
                m++;
                l++;

                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int[] getIDS_to_extract_C() {
        String liste = "2994856 21992 2000114 493376 5372233 16386915 12747130 47734 2743394 1999993 2404571 3841982 66250 47659 2338462 3049201 113040 15258519 3150762 3734556 10744775 462641 14776841 1549763 1157494 9851295 43521 2956145 1615955 87544 9843920 2602067 5368351 15732044 7411270 7395798 4029515 2392530 2722761 5108400 43309 100725 5593308 1435167 2097432 3355912 1061180 15077593 100422 1157927 5248966 9222519 1682338 7407326 12871188 12651084 5524846 5495588 11036711 3174585 5173764 2956728 584543 5577248 90174 5528704 2813307 2497338 51210 1682723 5130742 5495316 2799880 2051636 48956 3285134 49019 14075 12871641 5338382 2522952 10741024 9856276 4109311 2597252 4664876 3255322 1322254 4219925 2110987 3003201 10720212 5594409 3240330 5577215 40849 11561663 4955519 5494845 17358 7413852 3424100 3659343 2657453 5585684 4531218 80328 1177054 5525727 2208276 5595556 1918788 3633911 2173616 1191916 10746462 3958266 103088 5185631 13745621 103120 7409015 15210487 2860912 5606932 2146 3183568 5370188 1072490 5285750 1433139 10366608 41476 89649 2433931 12249550 50250 41714 5521418 46030 32956 2371790 41310 5401961 72078 5485319 4493216 4883337 14295392 13815531 2667164 1649432 1187418 13746148 45190 1072806 7417096 1523994 5225111 24507 2165651 3288751 5576036 2775212 4225054 31765 16362968 2892715 6802984 2944640 50874 16079855 5545631 1107217 5498695 437252 3434017 3273852 42814 1529476 5186711 3071703 2360926 5371254 1175907 13480182 46754 3494009 38061 3846446 5596485 674834 333361 2084819 1952664 2407766 2010106 73210 3732300 651778 49956 81722 4738587 4933688 2499317 50009 4185969 81765 7408350 4089444 5371605 2144016 5596802 3283340 348283 7412552 845871 42206 2415005 2209239 27042 13970068 3245989";

        StringTokenizer st = new StringTokenizer(liste);
        int[] ids = new int[ st.countTokens() ];
        int i = 0;
        while( st.hasMoreTokens() ) {
            ids[i] = Integer.parseInt( (String)st.nextElement() );
            i++;
        }
        return ids;
    }



    /**
     *
     * @param group
     * @return
     */
    public static int[] loadNodeGroup(int group) {
        NodeGroup ng = new NodeGroup();
        
        /**
         * only some of them ...
         */
        // ng.fn = "all32bit/lang-" + group + ".ids.dat";

        // all
        ng.fn = "all/lang-" + group + ".ids.dat";
        
        System.out.println( ">>> use all of ids: [" + ng.fn +"]" );
        ng.load();
        return ng.ids;
    }


};
