package research.sqlclient.phase2;

import extraction.TimeSeriesFactory;
import extraction.ExtractEditHistory;
import experiments.crosscorrelation.KreuzKorrelationStunde;
import experiments.crosscorrelation.KreuzKorrelationTag;
import org.apache.hadoopts.data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cloudera.wikiexplorer.ng.util.TimeLog;

/**
 * Extraction der Zeitreihen der Edits für
 *    die Kreuzkorrelation ...
 *      die Edits nach 2009
 *
 *          b) auf Stundenbasis
 *
 * @author kamir
 */
public class Topic5 {

    public static DecimalFormat df = new DecimalFormat("0.0000000");
    public static DecimalFormat df2 = new DecimalFormat("0.0");
    static int maxNode = 16930327;
    public static String folderIN_X = "./data/in/topic5/32bit_VphMean/";
    public static String folderOUT = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic5/";
    public static String requestY = "Select UserID, Time, TextLength, "+
            "PageID from revisions where revisions.PageID=? "+
            "AND revisions.Time>'2009-01-01 00:00:00' ORDER by Time ASC";
    static File indexFile = new File("./data/in/topic5/index.dat");
    static File resultFile = new File(folderOUT + "kk_Vph_32bit.dat");

    public static void main(String args[]) throws Exception {

        int id = 0;

        boolean testOnly = true;

        Vector<String> ids = new Vector<String>();
        FileReader fr = new FileReader(indexFile);

        BufferedReader br = new BufferedReader(fr);

        FileWriter fw = new FileWriter(resultFile);

        fw.write("# nodeID\tedits\t\taccess\t\tk_max\t\tk\t" + "\n");

        int soll = 0;
        while (br.ready()) {

            String line = br.readLine();

            if (!(line.startsWith("#"))) {
                // System.out.println(line);
                StringTokenizer st = new StringTokenizer(line);

                id = Integer.parseInt((String) st.nextElement());

                if (id < maxNode) {
                    ids.add(id + "");
                }
            }
        }

        soll = ids.size();

        System.out.println("###" + soll + " werden bearbeitet.\n\n");

        br.close();

        TimeLog tl = new TimeLog(false);


        Messreihe c = null;
        Messreihe d = null;

    

        int ist = 0;
        Enumeration<String> en = ids.elements();
        int cc = 0; // Begrenzung
        int ce = 0; // Zähler für Fehler

        StringBuffer sberror = new StringBuffer();

        while (en.hasMoreElements()) { // && ist < 100

            ist++;

            id = Integer.parseInt(en.nextElement());
            if (!testOnly) {
                try {

                    c = null; // ExtractEditHistory.extractEditHistoryForID2(id,300,1);
                    //System.out.println( c.toString() );

                    d = TimeSeriesFactory.prepareAccessDataSTUNDE(
                            id, 299 * 24);
                    //System.out.println( d.toString() );

                    KreuzKorrelationStunde kr2=new KreuzKorrelationStunde();
                    kr2.mr_x = c;
                    kr2.mr_y = d;

                    df = new DecimalFormat("0.00000000");

                    String result = "error";

                    try {
                        kr2.calcKR();
                        System.out.println("###" + kr2.getResultLine());
                        result = kr2.getResultLine();

                        double anzahlEdits = summiere(c.getYValues());
                        double anzahlAcces = summiere(d.getYValues());
                        

                        if (anzahlEdits > 0) {
                            fw.write(id + "\t\t" + df.format(anzahlEdits) + 
                                    "\t" + df.format(anzahlAcces) + "\t" +
                                    result + "\n");
                        }

//                System.gc();

                    } catch (Exception ex) {
                        // Fehlerhafte ID merken ...
                        //ex.printStackTrace();
                        //sberror.append(id + "\n");
                        ce++;
                    }




                } catch (Exception ex) {
                    Logger.getLogger(
                            KreuzKorrelationTag.class.getName()).log(
                            Level.SEVERE, null, ex);
                }



                // System.out.println( zr );
                tl.setStamp(id + " bearbeitet ... (" +
                        ((double) ist / (double) soll) + ")");

            } else {
                String fn = Topic5.folderIN_X + "PageID_" + id + ".txt";
                File f = new File(fn);
                if (f.canRead()) {
                    cc++;
                    System.out.println(fn + " -- " + cc);
                } else {
                    //sberror.append(id + "\n");
                    ce++;
                }
            }

            // Thread.currentThread().sleep(2);
        }
        tl.setStamp("READY");

        FileWriter fw2 = new FileWriter("ERRORS.dat");
        fw2.write("# FEHLER: \n");
        fw2.write("# soll=" + soll + "\n");
        fw2.write("# error=" + ce + "\n");
        fw2.write("# ist=" + cc + "\n");

        fw2.write(sberror.toString() + "\n");
        fw2.flush();

        fw.write(ExtractEditHistory.sb.toString());
        fw.flush();
        fw.close();


    }

    private static double summiere(Vector yValues) {
        double d = 0.0;
        Enumeration en = yValues.elements();
        while (en.hasMoreElements()) {
            Double v = (Double) en.nextElement();
            d = d + v;
        }
        
        return d;
    }
}
