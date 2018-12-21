package research.sqlclient.phase2;

import extraction.TimeSeriesFactory;
import extraction.ExtractEditHistory;
import experiments.crosscorrelation.KreuzKorrelationStunde;
import experiments.crosscorrelation.KreuzKorrelationTag;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import org.apache.hadoopts.data.export.MeasurementTable;
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
 *    die Edits nach dem 1.1.2009
 *
 *          c) auf Stundenbasis und mit variabler Auflösung.
 *
 * @author kamir
 */
public class Topic6 {

    static boolean showCharts = false;
    static boolean testOnly = false;

    public static DecimalFormat df = new DecimalFormat("0.0000000");
    public static DecimalFormat df2 = new DecimalFormat("0.0");
    static int maxNode = 16930327;
    //public static String folderIN_X = "/home/wikidb/WorkingDATA/RESULTS/40/32bit_VphMean/";
    public static String folderIN_X = "W:/RESULTS/40/32bit_VphMean/";
    public static String folderOUT = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic6/";
    public static String requestY = "Select UserID, Time, TextLength, " +
            "PageID from revisions where revisions.PageID=? " +
            "AND revisions.Time>'2009-01-01 00:00:00' ORDER by Time ASC";
    static File indexFile = new File("./data/in/topic6/index.dat");  //OK
    public static int timeResolution = 3;
    static File resultFile = new File(folderOUT +
            "CrossCcorrel_Vph_32bit_" + timeResolution + ".dat");


    public static Vector<String> loadIdsFromFile( String filename ) throws Exception {
        Vector<String> ids = new Vector<String>();
        FileReader fr = new FileReader(filename);

        BufferedReader br = new BufferedReader(fr);
        int counter = 0;
        int id = 0;
        while (br.ready()) {

            String line = br.readLine();

            if (!(line.startsWith("#"))) {
                // System.out.println(line);
                StringTokenizer st = new StringTokenizer(line);
                counter++;
                id = Integer.parseInt((String) st.nextElement());

                if (id < maxNode) {
                    // ZUM schnelleren TEST !!!
                   // if(counter > 49 )
                   ids.add(id + "");
                }
            }

//            if (counter == 100) {
//                break;
//            }
        }
        br.close();
        return ids;
    }


    public static Vector<String> getIdsToHandle_a() {
        Vector<String> ids = new Vector<String>();
        int[] id = ExtractEditHistory.getIDS_to_extract_a();

        for (int i = 0; i < id.length; i++) {
            ids.add(new String("" + id[i]));
        }
        return ids;
    };

    public static Vector<String> getIdsToHandle_b() throws Exception {
        String fn = indexFile.getAbsolutePath();
        return loadIdsFromFile(fn);
    };

    public static Vector<String> getIdsToHandle_EN() throws Exception {
        Vector<String> ids = new Vector<String>();
        String language = "en";
        String code = "60";
        String filename = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/filter1/lang-"+code+"-"+language+".ids.dat";
        return loadIdsFromFile(filename);
    };
    
    public static Vector<String> getIdsToHandle_DE() throws Exception {
        Vector<String> ids = new Vector<String>();
        String language = "de";
        String code = "52";
        String filename = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/filter1/lang-"+code+"-"+language+".ids.dat";
        return loadIdsFromFile(filename);
    };

    public static Vector<String> getIdsToHandle_JA() throws Exception {
        Vector<String> ids = new Vector<String>();
        String language = "ja";
        String code = "109";
        String filename = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/filter1/lang-"+code+"-"+language+".ids.dat";
        return loadIdsFromFile(filename);
    };

    public static void main(String args[]) throws Exception {


        int id = 0;



        int soll = 0;
        int counter = 0;

        Vector<String> ids = getIdsToHandle_EN();
        String lang = "EN";

        soll = ids.size();

        System.out.println("===> " + soll + " nodes werden bearbeitet.\n\n");

        TimeLog tl = new TimeLog(false);
        tl.setStamp("Go...");

        TimeSeriesObject c = null;

        TimeSeriesObject t1 = null;
        TimeSeriesObject t2 = null;
        TimeSeriesObject t3 = null;
        TimeSeriesObject t4 = null;
        TimeSeriesObject t5 = null;

        TimeSeriesObject k1 = null;
        TimeSeriesObject k2 = null;
        TimeSeriesObject k3 = null;
        TimeSeriesObject k4 = null;
        TimeSeriesObject k5 = null;

        TimeSeriesObject rr2 = null;
        TimeSeriesObject rr3 = null;
        TimeSeriesObject rr4 = null;
        TimeSeriesObject rr5 = null;


        TimeSeriesObject d1 = null;
        TimeSeriesObject d2 = null;
        TimeSeriesObject d3 = null;
        TimeSeriesObject d4 = null;
        TimeSeriesObject d5 = null;


        TimeSeriesObject sum1 = new TimeSeriesObject();
        TimeSeriesObject sum2 = new TimeSeriesObject();
        TimeSeriesObject sum3 = new TimeSeriesObject();
        TimeSeriesObject sum4 = new TimeSeriesObject();
        TimeSeriesObject sum5 = new TimeSeriesObject();

        TimeSeriesObject maxK1 = new TimeSeriesObject("maxK1");
        TimeSeriesObject maxK2 = new TimeSeriesObject("maxK2");
        TimeSeriesObject maxK3 = new TimeSeriesObject("maxK3");
        TimeSeriesObject maxK4 = new TimeSeriesObject("maxK4");
        TimeSeriesObject maxK5 = new TimeSeriesObject("maxK5");

        TimeSeriesObject d = null;
        KreuzKorrelationStunde kr = null;
        KreuzKorrelationStunde kr2 = null;
        KreuzKorrelationStunde kr3 = null;
        KreuzKorrelationStunde kr4 = null;
        KreuzKorrelationStunde kr5 = null;

        int ist = 0;
        Enumeration<String> en = ids.elements();
        int cc = 0; // Begrenzung
        int ce = 0; // Zähler für Fehler


        FileWriter fw = new FileWriter(resultFile);
        fw.write("# nodeID\tedits\t\taccess\t\tk_max\t\tk\t" + "\n");

        StringBuffer sberror = new StringBuffer();

        
        
        Vector<TimeSeriesObject> sumKR = new Vector<TimeSeriesObject>();
                    
        while (en.hasMoreElements()) { // && ist < 100

            ist++;

            id = Integer.parseInt(en.nextElement());
            if (!testOnly) {
                try {

                    TimeSeriesObject puffer = ExtractEditHistory.extractEditHistoryForID(id, false ); // NICHT SPEICHERN

                    c = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 1);  // Stundenbasis ..

                    t1 = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 1);   // geänderte Auflösung
                    t2 = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 3);   // geänderte Auflösung
                    t3 = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 6);
                    t4 = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 12);
                    t5 = ExtractEditHistory.extractEditHistoryForID2(puffer, id, 299, 24);  // auf Tagesbasis

                    k1 = c.setBinningX_sum(1);
                    k2 = c.setBinningX_sum(3);
                    k3 = c.setBinningX_sum(6);
                    k4 = c.setBinningX_sum(12);
                    k5 = c.setBinningX_sum(24);


//                    k2 = c.setBinningX_average(3);
//                    k3 = c.setBinningX_average(6);
//                    k4 = c.setBinningX_average(12);
//                    k5 = c.setBinningX_average(24);




                    int maxX = 7176;

//                    k2 = k2.scaleX( maxX );
//                    k3 = k3.scaleX( maxX );
//                    k4 = k4.scaleX( maxX );
//                    k5 = k5.scaleX( maxX );

                    d = TimeSeriesFactory.prepareAccessDataSTUNDE(
                            id, 299 * 24);

                    d1 = d.setBinningX_sum(1);
                    d2 = d.setBinningX_sum(3);
                    d3 = d.setBinningX_sum(6);
                    d4 = d.setBinningX_sum(12);
                    d5 = d.setBinningX_sum(24);

//                    d2 = d2.scaleX(maxX);
//                    d3 = d3.scaleX(maxX);
//                    d4 = d4.scaleX(maxX);
//                    d5 = d5.scaleX(maxX);


                    kr = new KreuzKorrelationStunde();
                    kr.setScale(1);
                    kr.mr_x = c;
                    kr.mr_y = d1;

                    kr2 = new KreuzKorrelationStunde();
                    kr2.setScale(3);
                    kr2.mr_x = k2;
                    kr2.mr_y = d2;

                    kr3 = new KreuzKorrelationStunde();
                    kr3.setScale(6);
                    kr3.mr_x = k3;
                    kr3.mr_y = d3;

                    kr4 = new KreuzKorrelationStunde();
                    kr4.setScale(12);
                    kr4.mr_x = k4;
                    kr4.mr_y = d4;

                    kr5 = new KreuzKorrelationStunde();
                    kr5.setScale(24);
                    kr5.mr_x = k5;
                    kr5.mr_y = d5;

                    df = new DecimalFormat("0.00000000");

                    String result = "error";

                    try {

                        // Das sind nun die KORRELATIONSFUNKTIONEN
                        kr.calcKR();
                        kr2.calcKR();
                        kr3.calcKR();
                        kr4.calcKR();
                        kr5.calcKR();
   
                        maxK1.addValue( (long)kr.get_k_MAX() );
                        maxK2.addValue( (long)kr2.get_k_MAX() );
                        maxK3.addValue( (long)kr3.get_k_MAX() );
                        maxK4.addValue( (long)kr4.get_k_MAX() );
                        maxK5.addValue( (long)kr5.get_k_MAX() );

                        rr2 = kr2.scaleX_2(3);
                        rr3 = kr3.scaleX_2(6);
                        rr4 = kr4.scaleX_2(12);
                        rr5 = kr5.scaleX_2(24);

                        sum1 = sum1.add( kr );
                        sum2 = sum2.add( kr2 );
                        sum3 = sum3.add( kr3 );
                        sum4 = sum4.add( kr4 );
                        sum5 = sum5.add( kr5 );




                        // System.out.println("[CrossCorrelation] ==> " + kr2.getResultLine());
                        result = kr.getResultLine();

//                        double anzahlEdits = summiere(c.getYValues());
//                        double anzahlAcces = summiere(d.getYValues());
//
//                        if (anzahlEdits > 0) {
//                            fw.write(id + "\t\t" + df.format(anzahlEdits) +
//                                    "\t" + df.format(anzahlAcces) + "\t" +
//                                    result + "\n");
//                        }

                    }
                    catch (Exception ex) {
                        // Fehlerhafte ID merken ...
                        // ex.printStackTrace();
                        sberror.append( "["+id +"] :" );
                        sberror.append( ex.getMessage() +"\n" );
                        // sberror.append( ex.getCause() +"\n");

                        ce++;
                    }




                } catch (Exception ex) {
                    Logger.getLogger(
                            KreuzKorrelationTag.class.getName()).log(
                            Level.SEVERE, null, ex);
                }

                df = new DecimalFormat("0.00 ");
                double proc = ((double) ist / (double) soll) * 100.0;
                tl.setStamp(id + " bearbeitet ... (" +
                        df.format(proc) + "% )");


                if (showCharts) {
                    // ORIGINAL-Daten
                    Vector<TimeSeriesObject> vt1 = new Vector<TimeSeriesObject>();
                    Vector<TimeSeriesObject> vt2 = new Vector<TimeSeriesObject>();
                    Vector<TimeSeriesObject> vt3 = new Vector<TimeSeriesObject>();
                    Vector<TimeSeriesObject> vt4 = new Vector<TimeSeriesObject>();
                    Vector<TimeSeriesObject> vt5 = new Vector<TimeSeriesObject>();
                    Vector<TimeSeriesObject> vtKR = new Vector<TimeSeriesObject>();
                    
                    vtKR.add(kr);
                    vtKR.add(rr2);
                    vtKR.add(rr3);
                    vtKR.add(rr4);
                    vtKR.add(rr5);




                    vt1.add(c);
                    vt1.add(d);

                    vt2.add(k2);
                    vt2.add(d2);

                    vt3.add(k3);
                    vt3.add(d3);

                    vt4.add(k4);
                    vt4.add(d4);

                    vt5.add(k5);
                    vt5.add(d5);

                    MultiChart mc1 = new MultiChart(null, true);
                    MultiChart mc2 = new MultiChart(null, true);
                    MultiChart mc3 = new MultiChart(null, true);
                    MultiChart mc4 = new MultiChart(null, true);
                    MultiChart mc5 = new MultiChart(null, true);

                    MultiChart mcKR = new MultiChart(null, true);
                  
                    mcKR.openNormalized(vtKR, "Korrelationsfunktion R(k) - " + id, "k", "R(k)", true);
                    mc1.openNormalized(vt1, "Access time series 1"+ id, "t", "y(t)", true);
                    mc2.openNormalized( vt2, "Access time series 2"+ id , "t" , "y(t)", true );
                    mc3.openNormalized( vt3, "Access time series 3"+ id , "t" , "y(t)", true );
                    mc4.openNormalized( vt4, "Access time series 4"+ id , "t" , "y(t)", true );
                    mc5.openNormalized( vt5, "Access time series 5"+ id , "t" , "y(t)", true );


                    //int goOn = javax.swing.JOptionPane.showConfirmDialog( mc1, "Weiter ...");

                }
            }
            else {

                // Testen, zu wievielen PageIDs auch Acces-Daten vorliegen ...
                String fn = Topic6.folderIN_X + "PageID_" + id + ".txt";
                File f = new File(fn);
                if (f.canRead()) {
                    cc++;
                    //System.out.println(fn + " -- " + cc);
                } else {
                    sberror.append(id + "\n");
                    ce++;
                }
                //System.out.println("\n\nFehlende Dateien : " + ce);
            }

        }
        System.out.println("\n\nFehlende Dateien : " + ce);
        tl.setStamp("READY");

        sum2 = sum2.scaleX_2(3);
        sum3 = sum3.scaleX_2(6);
        sum4 = sum4.scaleX_2(12);
        sum5 = sum5.scaleX_2(24);

        sumKR.add( sum1 );
        sumKR.add( sum2 );
        sumKR.add( sum3 );
        sumKR.add( sum4 );
        sumKR.add( sum5 );

        MultiChart mcSUM = new MultiChart(null, true);
        mcSUM.openNormalized(sumKR, "SUMME der Korrelationsfunktion R(k)" + lang, "k", "R(k)", true);

        MeasurementTable mwt = new MeasurementTable();
        mwt.setMessReihen(sumKR ); 
        mwt.setLabel( "SUMME der Korrelationsfunktion R(k) " + lang );
        mwt.writeToFile(new File(Topic6.folderOUT + "MW_R(k)_" + lang +".dat") );

        maxK1.writeToFile( new File(Topic6.folderOUT + "maxK1_" + lang +".dat"));
        maxK2.writeToFile( new File(Topic6.folderOUT + "maxK2_" + lang +".dat"));
        maxK3.writeToFile( new File(Topic6.folderOUT + "maxK3_" + lang +".dat"));
        maxK4.writeToFile( new File(Topic6.folderOUT + "maxK4_" + lang +".dat"));
        maxK5.writeToFile( new File(Topic6.folderOUT + "maxK5_" + lang +".dat"));

        FileWriter fw2 = new FileWriter( Topic6.folderOUT + "ERRORS" + lang +".dat");
        fw2.write("# FEHLER: \n");
        fw2.write("# soll=" + soll + "\n");
        fw2.write("# error=" + ce + "\n");
        fw2.write("# ist=" + cc + "\n");

        fw2.write(sberror.toString() + "\n");
        fw2.flush();

        fw.write(ExtractEditHistory.sb.toString());
        fw.flush();
        fw.close();

        System.out.println( tl.toString() );


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
