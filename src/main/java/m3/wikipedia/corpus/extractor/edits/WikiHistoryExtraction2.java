/** 
 * 
 * We extract Wikipedia Edit-Event-TS from a set of CNs using the WIKI-API.
 *
 * 
 * Punkte der Diskussion mit Holger Motzkau:
 - Änderung bei schwedischen Zugriffen mit Einrichten der englischen Seite:
 Stichproben (Fritiof_Nilsson_Piraten, Gustav_III) zeigen, dass die
 Edit-Zeitpunkte
 in der Liste nicht oder nur ungefähr stimmen; manche sind doppelt
 (Statistik)
 - Änderungen auch umgekehrt (schwedische Seite wird erzeugt) und für andere
 Sprachen
 zum Vergleich
 - An wichtigsten sind ihm die Qualitätsbewertungen der Artikel in
 Korrelation mit den
 Zugriffszahlen (schwedisch, zwei Cluster?)
 - Aufteilen der englischen Wikipedia-Zugriffe im Monat der hebräischen
 Extra-Zeit und
 davor -- Unterschiede von ein Prozent oder weniger?
 - Anomalie der japanischen Edit-Häufigkeit im Zusammenhang mit Bots möglich?
 - Aus den Edit-Minima kann man obere Grenzen für Edits außerhalb des Landes
 gewinnen.
 - Seite http://reportcard.wmflabs.org/ zu den Langzeittrends (nicht
 größenabhängig)
 - Research Newsletter:
 - Research Newsletter:

 http://meta.wikimedia.org/wiki/Research:Newsletter/2012/November#cite_note-12

 * 
 * MAIL vom 27.12.2012 von Jan an Mirko
 */
package m3.wikipedia.corpus.extractor.edits;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import javax.security.auth.login.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData; 
import m3.wikipedia.explorer.data.WikiNode;

/**
 * Usage :
 *
 *
 *
 *
 *
 * @author root
 */
public class WikiHistoryExtraction2 extends WikiHistoryExtractionBASE {

    /**
     * TestRun ...
     *
     * main( ... )
     *
     * run( Vector<WikiNode> ... ) run( WikiNode[] ... )
     *
     * work( ... )
     *
     * => TimeSeriesObjectn liegen vor ... und können gepuffert werden ...
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FailedLoginException
     * @throws Exception
     */
    
    
    
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException, FailedLoginException, Exception {
        
        String path1 = "."; 
        String path2 = "."; 
         
       
        WikiStudieMetaData wd = WikiStudieMetaData.initStudie();
        File ff = wd.selectFile( "/Users/kamir/DEMO");
//        File ff = new File( "/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia/WikiPaper_und_BA_2012/WikiPaper_und_BA_2012.xml");
        wd = wd.load(ff);
        
        // NAME extrahieren und Liste LADEN
        File f = wd.getRealPathToProjectFile( ff );
        
    
        von = new GregorianCalendar();
        von.clear();
        von.set(2009, 0, 1, 0, 0);


        java.util.GregorianCalendar bis = new java.util.GregorianCalendar();
        bis.clear();
//      processStudie(wd, von, bis, f);
        bis.add(GregorianCalendar.DAY_OF_YEAR, 250);
        
        System.out.println( "Time Range:" );
        System.out.println( von.getTime().toString() );
        System.out.println( bis.getTime().toString() );
        
        // HIER FOLLOWS more code .... 
        
        
        
    }    
    
    public static void processStudie( WikiStudieMetaData wd , Calendar von, Calendar bis, File f ) throws FileNotFoundException, ClassNotFoundException, IOException, FailedLoginException, Exception  { 
    
        long t0 = System.currentTimeMillis();
        
        boolean operate_LOCALY = true;
        boolean operate_DEV = false;
        
        if ( von == null ) System.exit(-1);
        if ( bis == null ) System.exit(-1);
        
        showChart = false;
    
        loadFromLocalCACHE = false;
        loadSPLITS = false;

        storeToLocalCACHE = false;
        storeToLocal_SPLIT_PER_CN = false;

        if (f.exists() ) {
            
            System.out.println(">>> Loading CN   : " + f.getAbsolutePath());
            
            if( loadFromLocalCACHE ) {
                if ( loadSPLITS ) { 
                    _loadLocalCacheDumpSPLITS( wd.path2, wd);
                }
                else {
                    _loadLocalCacheDump( wd.path2, wd);
                }
            }
            
            System.out.println(">>> Info: \n" + wd.description);

//            final Vector<WikiNode> wnCN = wd.getWn();
//            System.out.println(">>> Nr of CN-nodes : " + wnCN.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnCN, von, bis, true);
//            }

//            final Vector<WikiNode> wnIWL = wd.getIWL();
//            System.out.println(">>> Nr of IWL-nodes : " + wnIWL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnIWL, von, bis, true);
//            }


//            final Vector<WikiNode> wnAL = wd.getAL();
//            System.out.println(">>> Nr of AL-nodes : " + wnAL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnAL, von, bis, true);
//            }

//            final Vector<WikiNode> wnBL = wd.getBL();
//            System.out.println(">>> Nr of BL-nodes : " + wnBL.size());
//
//            if (!von.before(bis)) {
//                System.err.println("< ENDE muss nach ANFANG liegen !!! >");
//                System.exit(-1);
//            } 
//            else {
//                run(wnBL, von, bis, true, ".", "BL");
//            }

        } 
        else {
            System.out.println(">>> " + f.getAbsolutePath() + " not available.");
        }

        if ( showChart ) {
//            MultiBarChart.open(rows1, "1 Event-Zeitreihen", "t", "nr", false);
//            MultiBarChart.open(rowsBINARY1, "1 Event-Reihen", "t", "nr", false);
        }
        
        /**
         *    
         */ 
        if( storeToLocalCACHE ) {
            if( storeToLocal_SPLIT_PER_CN ) { 
                int max = wd.getWn().size();
                // für jede CN eine eigene Hashtable anlegen
                for( int i = 0; i < max; i++) {
                    int CN = i+1;
                    _storeLocalCacheDump_SPLIT_PER_CN(wd.path2, wd.selectedName, CN, wd );
                }   
            }
            else {
                storeLocalCacheDump(wd.path2, wd.selectedName);
            }
        }    
        
        wd.logExtraction( von, bis );
        
        Vector<TimeSeriesObject> mrCN = null;
        Vector<TimeSeriesObject> mrAL = null;
        Vector<TimeSeriesObject> mrBL = null;
        Vector<TimeSeriesObject> mrIWL = null;
        
        
        System.out.println( ">>> " + wd.name );
        System.out.println( ">>> INFO : \n" + wd.getDescription() );

        System.out.println( ">>> von : " + von.getTime() );
        System.out.println( ">>> bis : " + bis.getTime() );
     
        System.out.println( ">>> Done.");
        
        long t1 = System.currentTimeMillis();
        
        System.out.println(">>> " +new Date( t1 - t0 ) );
        

    }

 
 


}
