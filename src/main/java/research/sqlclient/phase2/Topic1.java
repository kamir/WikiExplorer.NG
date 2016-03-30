package research.sqlclient.phase2;

import org.apache.hadoopts.data.series.Zeitreihe;
import extraction.ExtractEditHistory;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger; 
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * Extraction der Zeitreihen der Edits für
 *    alle
 *    die Edits nach 2009
 *
 * ===> Datenreihe je NodeID
 *
 *
 * Verteilung der Edit-Zahl
 *    alle
 *    Edits nach 2009
 *
 * ===> Histogramm-Daten je Gruppe
 *
 * @author kamir
 */
public class Topic1 {

    public static String folder1 = "2009";
    public static String folder2 = "alle";

    public static String folder = folder2;  // Was soll berechnet werden ...
    
    public static String request1 = "Select UserID, Time, TextLength, PageID from revisions where revisions.PageID=? AND revisions.Time>'2009-01-01 00:00:00' ORDER by Time ASC";
    public static String request2 = "Select UserID, Time, TextLength, PageID from revisions where revisions.PageID=? ORDER by Time ASC";

    public static String request = request2;






    static HaeufigkeitsZaehler wks = null;

    public static void main(String args[]) throws Exception {

        // initBorders();

        Vector<String> ids = new Vector<String>();
        FileReader fr = new FileReader("/home/kamir/NetBeansProjects/SQLClient/data/out/filter1/lang-60-en.ids.dat");
        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter("/home/kamir/NetBeansProjects/SQLClient/data/out/edits/topic1/" + folder + "/all.review.counts.dat");
        fw.write("# nodeID\tedits\n");

//        FileWriter fw2 = new FileWriter("/home/kamir/NetBeansProjects/SQLClient/data/out/edits/zr/topic1/" + folder + "/all.user.editcounts.dat");
//        fw2.write("# userID\tedits\n");

        int soll = 0;
        while (br.ready()) {
            ids.add(br.readLine());
            soll++;
        }
        ;
        br.close();

        TimeLog tl = new TimeLog(false);

        int ist = 0;
        Enumeration<String> en = ids.elements();
        while (en.hasMoreElements()) {
            ist++;
            int id = Integer.parseInt(en.nextElement());

            Zeitreihe zr = ExtractEditHistory.extractEditHistoryForID(id,false);

            int anzahlEdits = zr.getXValues().size();

            if (anzahlEdits > 0) {

//                wks = new WiederkehrStatistik();
//                wks.intervalle = 50;
//                wks.label = "PageID_" + id + "_Int_" + wks.intervalle + ".hist.dat";
//                wks.folder = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/zr/wks/";
//
//                wks.addData(ExtractEditHistory.dates);
//
//                wks.calcWS();
//
//                wks.store();
//
//                storeFrequncy( id , anzahlEdits);
            }

            System.gc();

            // System.out.println( zr );
            tl.setStamp(id + " bearbeitet ... (" + ((double) ist / (double) soll) + ")");

            // Thread.currentThread().sleep(2);
        }
        tl.setStamp("READY");


        fw.write(ExtractEditHistory.sb.toString());
        fw.flush();
        fw.close();

//        fw2.write("# " + ExtractUserActivity.z + "\n");
//        fw2.write(ExtractUserActivity.master.toString());
//        fw2.flush();
//        fw2.close();

    }

    // Hier geht es um die Zusammenfassung der PageIDs mit gleicher Edit-Zahl
    static HaeufigkeitsZaehler[] wksAll = new HaeufigkeitsZaehler[10];
    static int[] borders = new int[10];


    /**
     * Für die Wiederkehrstatistik sollen die NodeIDs mit gleicher Edit-Anzahl
     * in einer Gruppe Zusammengaefasst werden. Innerhalb solcher Gruppen gibt
     * es dann die Verteilungsstatistik der Wiederkehrintervalle.
     */
    public static void initBorders() {

        int last = 1;
        borders[0] = 0;
        borders[1] = 5;
        last = borders[1];
        for (int i = 2; i < 10; i++) {

            borders[i] = last * 2;
            last = borders[i];
        }
        for (int i = 0; i < 10; i++) {
            wksAll[i] = new HaeufigkeitsZaehler();
            System.out.println(borders[i]);
        }

        try {
            fw = new FileWriter("/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/topic1/"+folder + "/WKS_Groups.dat");
        }
        catch (IOException ex) {
            Logger.getLogger(Topic1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static FileWriter fw;

    public static void storeFrequncy(int id, int anzahlEdits) {
        int index = 9;
        boolean goOn = true;

        for (int i = 0; i < 9; i++) {
            if (goOn && anzahlEdits < borders[i]) {
                index = i - 1;
                goOn = false;
            }
            
        }

        

        System.out.println(index + " - " + anzahlEdits);

        try {
            fw.write(id + "\t" + anzahlEdits + "\t" + index + "\n");
            // wksAll[index].addData(d);
        }
        catch (IOException ex) {
            Logger.getLogger(Topic1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void saveWKS() {
        try {
            //        for (int i = 0; i < 10; i++) {
            //            WiederkehrStatistik wks = wksAll[i];
            //
            //            wks.intervalle = 50;
            //            wks.label = "AccessCounts_" + i + "_Int_" + wks.intervalle + ".hist.dat";
            //            wks.folder = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/zr/wks/";
            //
            //            wks.calcWS();
            //            try {
            //                wks.store();
            //            }
            //            catch (IOException ex) {
            //                Logger.getLogger(Topic1.class.getName()).log(Level.SEVERE, null, ex);
            //            }
            //
            //        }
            fw.flush();
            fw.close();
        }
        catch (IOException ex) {
            Logger.getLogger(Topic1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
