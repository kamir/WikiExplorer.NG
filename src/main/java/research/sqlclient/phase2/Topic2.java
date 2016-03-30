package research.sqlclient.phase2;

import org.apache.hadoopts.chart.simple.MultiBarChart;
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.series.Zeitreihe;
import org.apache.hadoopts.data.export.OriginProject;
import extraction.ExtractEditHistory;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehler;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import org.apache.hadoopts.statphys.ris.experimental.ReturnIntervallStatistik2;
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
public class Topic2 {

    
    public static boolean doTest = false;
    
    public static String folder1 = "2009";
    public static String folder2 = "alle";
    
    public static String folder = folder1;  // Was soll berechnet werden ...
    
    public static String request1 = "Select UserID, Time, TextLength, PageID"+
            " from revisions where revisions.PageID=? AND revisions.Time"+
            ">'2009-01-01 00:00:00' AND revisions.Time"+
            "<'2009-10-28 00:00:00' ORDER by Time ASC";
 
    
    public static String request = request1;
    
    private static boolean storeSingleDistributions = false;
    
    static FileWriter fw;
    
    static Vector<Messreihe> mrv = null;
    
    static int risBinning = 300*24;  // Auflösung in Stunden
    static int risScale = 1;

    static int maxB = 5; // anzahl der Input-Files
    static int maxROWS = -1; // default -1
    
    private static boolean debug = false;
    

    
    public static String logFileNames[] = null;
    public static OriginProject projekt = null;
    static int anzNullEdits[] = null;
    
        
    public static void initRqCollectors() {
        
        anzNullEdits = new int[maxB];
        ReturnIntervallStatistik2.debug1 = new HaeufigkeitsZaehlerDouble[grenzen.length];
                
        mrRQ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        mrANZ = new HaeufigkeitsZaehlerDouble[grenzen.length];
        for( int i = 0; i < grenzen.length; i++ ) { 
            mrRQ[i] = new HaeufigkeitsZaehlerDouble(); // "R_q " + labels[i] );
            mrRQ[i].intervalle = 300*24;
            mrRQ[i].max = 300*24;

            mrANZ[i] = new HaeufigkeitsZaehlerDouble();
            mrANZ[i].intervalle = 300*24;
            mrANZ[i].max = 300*24;
            
            ReturnIntervallStatistik2.debug1[i] = new HaeufigkeitsZaehlerDouble();
            ReturnIntervallStatistik2.debug1[i].label = "bis="+grenzen[i];
        }
    }
    
    public static void main(String args[]) throws Exception {
        
//        String arg = javax.swing.JOptionPane.showInputDialog("offset=");
       
        int ofs = Integer.parseInt( args[0] );
//        int ofs = Integer.parseInt( arg );
        
        projekt = new OriginProject();
        projekt.initBaseFolder("./TEST/P1/MO_FINAL2/");
        
        logFileNames = new String[grenzen.length - 1];
        for( int i=1; i < grenzen.length ; i++ ) { 
            logFileNames[i-1] = "log_"+ofs+"_gruppe_"+i+"_"+grenzen[i-1]+"_"+grenzen[i]+".dat";
            projekt.createLogFile( logFileNames[i-1] );
        }
        projekt.createLogFile("LENGTH");
        
        projekt.createLogFile("RzuGROS");
        projekt.createLogFile("RAW");
        
        // 24
        // 12
        //  0
        ReturnIntervallStatistik2.offset = ofs;
        
        ReturnIntervallStatistik2.debug = false;
        ReturnIntervallStatistik2.verbose = false;
        
        int variante = 2;
        maxB = 1;
//        maxROWS = 1;
   
        //int maxB = labels.length;
        
        pfade = new String[5];
        labels = new String[5];
        
        storeSingleDistributions = false;
        
        // -------------------------------------------------------------------
        // hier muessen alle Parameter definiert sein ....
        initRqMIN();
        
        initRqCollectors();
        
        switch( variante ) {
            case 1 : init1000MostActiveByEdits();
                     break;
            case 2 : initAll32bitByLanguage();
                     break;
        }    
                            
        mrv = new Vector<Messreihe>();
        
        TimeLog tl = new TimeLog(false);
        /**
         * Schleife über die NodeGroups ...
         */
        for ( int b = 0; b < maxB; b++ ) {
            String log1_key = b + "_" + ReturnIntervallStatistik2.offset + "_Row_Filter_log.dat";
            projekt.createLogFile( log1_key );
        
            
            // für jede Sprache ist der Collector erneut zu erstellen
            // dort werden die Daten nach der Anzahl der COUNTS sortiert
            // abgelegt.
            initRISCollectors(); 
            
            
            // die ID-Liste neu erstellen ...
            Vector<String> ids = new Vector<String>();
            
            // aktuellen Pfad ermitteln
            String fn = pfade[b];
            String label = labels[b];
            
            //
            // Node-Groupe auswählen ...
            //
            //FileReader fr = new FileReader("/home/kamir/NetBeansProjects/
            //                SQLClient/data/out/filter1/lang-60-en.ids.dat");
            //
            FileReader fr = new FileReader( fn );
            BufferedReader br = new BufferedReader(fr);

            int soll = 0;
            int idCounter = 0;
            boolean noStop = true;
            
            // Lesen den IDs der NodeGroup ...
            while (br.ready()) {  // aus der Node-Group-Liste lesen ...

                String line = br.readLine();

                idCounter++;
                if (!(line.startsWith("#")) && noStop ) {

                    //System.out.println(line);
                    StringTokenizer st = new StringTokenizer(line);

                    int id = Integer.parseInt((String) st.nextElement());

                    
                    if ( maxROWS > 0 ) { // begrenzen auf Max-Rows
                        if ( idCounter <= maxROWS ){
                            ids.add(id + "");
                            soll++;
                        }
                    }
                    else { // default ... alles was im File steht ... 
                        ids.add(id + "");
                        soll++;
                    }
                }
            }
            br.close();
            System.out.println(
                    ">>> NrOfNodes=" + soll + " werden bearbeitet.");
            //
            // Hier sind nun die IDs bekannt ...
            //
            // ---------------------------------------------------------------
            
            // 
//            ReturnIntervallStatistik2 risALL = new ReturnIntervallStatistik2(
//                    "ALL", risBinning,risScale);
//            risALL.isContainerInstanz = true;
//
//            risALL.label = label + "RIS_ALL_BINNING_" + 
//                           risBinning + ".verteilung.dat";
//            risALL.folder = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2A/RIS/"+label;
//
//            File f = new File( risALL.folder );
//            if ( !f.exists() ) f.mkdirs();

            int errors = 0;

            int ii = 0;
            int ist = 0;
            boolean stop = false;
            // alle IDs durchgehen ...
            Enumeration<String> en = ids.elements();
            while (en.hasMoreElements() && !stop ) {
                ist++;
                int id = Integer.parseInt(en.nextElement());

                // Zeitreihe extrahieren
                Zeitreihe zr = null;
                Vector<Double> data = null;
                
                if ( doTest ) {
//                    zr = Zeitreihe.getTestZeitreihe_X_RANDOM();
                    zr = Zeitreihe.getTestZeitreihe_EINZELTEST();
//                    zr = ExtractEditHistory.extractEditHistoryForID( id,true );
                    data = zr.xValues;
                    Collections.sort(data);
                    
                }
                else {
                    zr = ExtractEditHistory.extractEditHistoryForID( id,true );
                    data = zr.xValues;
                }
                
                // if( debug ) System.out.println( ((Messreihe)zr).toString() );
                
                // x = Zeitpunkt, y = Wachstum
                
                
  
                int anzahlEdits = data.size();

                if (anzahlEdits > 0) {
                     
                    
                    // RIS errechnen
       ReturnIntervallStatistik2 risEinzeln = new ReturnIntervallStatistik2(
       id+"", risBinning,risScale);

       risEinzeln.label = "PageID_" + id + "_BINNING_" + risBinning + 
                          ".verteilung.dat";
       risEinzeln.folder = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/topic2A/RIS/rows/"+label+"/";
                    
                    File f2 = new File( risEinzeln.folder );
                    if ( !f2.exists() ) f2.mkdirs();

//                    Vector<Long> d2 = new Vector<Long>();
//                    for( double d : data ) { 
//                        // Daten in Stunden !!!
//                        d2.add( (long)( d / (60.0 * 60.0 * 1000.0) )); 
//                    }
//                    
                    // ind d2 liegen die ROHDATEN vor
                    risEinzeln.setLofFileNameKey_1( log1_key );
                    risEinzeln._addData( data, 1.0 );

                    
                    /**
                     * anzahl Edits wurde hier überschrieben ...
                     */
                    anzahlEdits = risEinzeln.anzFiltered; 
                    
                    if ( storeSingleDistributions ) {
//                        risEinzeln.calcMessreihen();   
//                        risEinzeln.showData();
//                        risEinzeln.store();
                    }
                    try {
                        // risALL.add(risEinzeln);
//                        _collectRq( risEinzeln.getRq() , b, id);
                        collectForGroups( risEinzeln, anzahlEdits);
                        
                    }
                    catch( Exception ex) { 
                        if (ReturnIntervallStatistik2.debug) {
                            System.out.println( ex.getMessage() );
                        }
                        errors++;
                    }
                }
                else {
                    anzNullEdits[b]++;
                }
//
//                if ( ii == 5 ) {
//                    stop = true;
//                }

                if ( ii == 1000 ) {
                    System.out.println( 
                      "FORTSCHRITT: " + ( 100.0 * (double)ist/(double)soll) );
                  tl.setStamp(id + " ... ("+((double)ist/(double)soll) + ")");
                  ii = 0;
                }    
                ii = ii+1;

                
                // Thread.currentThread().sleep(2);
            }

//            ReturnIntervallStatistik2.verbose = true;
//            risALL.calcMessreihen();
            // risALL.store();
            
            // mrv.add( risALL.mrHaeufigkeit );
//            Messreihe mrA = risALL.mrVerteilungSkaliert;
            // mrA = mrA.calcMR_Ln_for_Y();
//            mrv.add( mrA );
            
            tl.setStamp("READY");

    //        fw.write(ExtractEditHistory.sb.toString());
    //        fw.flush();
    //        fw.close();

    //        fw2.write("# " + ExtractUserActivity.z + "\n");
    //        fw2.write(ExtractUserActivity.master.toString());
    //        fw2.flush();
    //        fw2.close();
            int maxEdits = 0;
            int minEdits = 0;
            
            System.out.println(">>> errors=" + errors );
            Vector<Messreihe> mrv2 = new Vector<Messreihe>();
            for( int i = 0; i < grenzen.length - 1; i++ ) {
                
                risContainer[i].calcMessreihen();
                
                Messreihe mr = risContainer[i].mrVerteilungSkaliert;
                // mr = mr.calcMR_Ln_for_Y();
                debugMessreihe( mr );

                rawRows.add( risContainer[i].mrVerteilung);
                
                SimpleRegression sr = mr.linFit( 1.0, 4.0);
                double m=sr.getSlope();
                double R=sr.getR();
                
                mr.setLabel( grenzen[i] + "..." + grenzen[i+1] + "_"+ risContainer[i].offset+"_m=" + 
                             m + "_R=" + R+"_#=" + mrRQ[i].dists.size() );
                mr.setFileName( labels[b] + "_" + grenzen[i] + "_"+ 
                                risContainer[i].offset + ".dat");
                
                mrv2.add( mr );
                // System.out.println( mr.toString() );
            }
            MultiChart.open(mrv2, "(V:"+variante + ") " + 
                                  label + " offset="+
                                  ReturnIntervallStatistik2.offset, 
                            "r/Rq", "log_10(Pq(r)*Rq)", 
                            true );
            projekt.addMessreihen( mrv2 , 
                    b+"_"+ReturnIntervallStatistik2.offset+"_", true );

            // showRqHistogramme( variante, b );
            
            checkRqCollectors( b );
             
//            // Verteilung der Länder der Reihen vor und nach dem Filtern anzeigen
//            Vector<Messreihe> rows = new Vector<Messreihe>();
//            rows.add( ReturnIntervallStatistik2.anzRowsFILTERED.getDistributionMR() );
//            rows.add( ReturnIntervallStatistik2.anzRowRAW.getDistributionMR() );
//            String labelX = "length of rows";
//            HaeufigkeitsZaehler.showDistribution(rows, labelX);
            
            
            
//            MultiChart.open(rawRows, "rawdata (V:"+variante + ") alle offset="+
//                             ReturnIntervallStatistik2.offset, 
//                        "r/Rq", "Pq(r)*Rq", 
//                        true);
            
            initRISCollectors();
        }        
        

        
        
        
        projekt.closeAllWriter();
        
        System.out.println( "Errors LÄNGE=" + ReturnIntervallStatistik2.errosLENGTH );
    }
    
    static Vector<Messreihe> rawRows = new Vector<Messreihe>();
    
    
    
//    // Hier geht es um die Zusammenfassung der PageIDs mit gleicher Edit-Zahl
//    static HaeufigkeitsZaehler[] wksAll = new HaeufigkeitsZaehler[10];
//    static int[] borders = new int[10];

//  /**
//   * Für die Wiederkehrstatistik sollen die NodeIDs mit gleicher Edit-Anzahl
//   * in einer Gruppe Zusammengaefasst werden. Innerhalb solcher Gruppen gibt
//   * es dann die Verteilungsstatistik der Wiederkehrintervalle.
//   */
//    public static void initBorders() {
//
//        int last = 1;
//        borders[0] = 0;
//        borders[1] = 5;
//        last = borders[1];
//        for (int i = 2; i < 10; i++) {
//
//            borders[i] = last * 2;
//            last = borders[i];
//        }
//        for (int i = 0; i < 10; i++) {
//            wksAll[i] = new HaeufigkeitsZaehler();
//            System.out.println(borders[i]);
//        }
//
//        try {
//            fw = new FileWriter("/Volumes/MyExternalDrive/CALCULATIONS/data/out/edits/topic2A/" #
//                 + folder + "/WKS_Groups.dat");
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(Topic2.class.getName()).log(
//               Level.SEVERE, null, ex);
//        }
//
//    }


//    public static void storeFrequncy(int id, int anzahlEdits) {
//        int index = 9;
//        boolean goOn = true;
//
//        for (int i = 0; i < 9; i++) {
//            if (goOn && anzahlEdits < borders[i]) {
//                index = i - 1;
//                goOn = false;
//            }
//        }
//
//        System.out.println(index + " - " + anzahlEdits);
//
//        try {
//            fw.write(id + "\t" + anzahlEdits + "\t" + index + "\n");
//            // wksAll[index].addData(d);
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(Topic2.class.getName()).log(
//              Level.SEVERE, null, ex);
//        }
//
//    }

    
    static String[] pfade = null;
    static String[] labels = null;
        
    private static void init1000MostActiveByEdits() {
        
        labels[0] = "197_1000"; //OK
        labels[1] = "52_1000";
        labels[2] = "60_1000";
        labels[3] = "62_1000";
        labels[4] = "72_1000";
              
        for( int i = 0; i < labels.length; i++ ) { 
            pfade[i] = "G:/DEV/MLU/Wiki/TimeSeriesToolbox/data/in/node_groups/" 
                       + labels[i] + "_most_active_by_edist.dat";
        }
    }

    private static void initAll32bitByLanguage() {
        
//        labels[0] = "197-ru"; //OK
        labels[0] = "2000-old"; //OK - zum Vergleich der Daten von 2010
        labels[1] = "52-de";
        labels[2] = "72-fr";
        labels[3] = "62-sp";
        labels[4] = "60-en";
        
        for( int i = 0; i < labels.length; i++ ) { 
            pfade[i] = "/Volumes/MyExternalDrive/CALCULATIONS/data/out/filter1/lang-" + labels[i] + ".ids.dat"; 
        }
 
    }

    private static void initRISCollectors() {
        
        risContainer = new ReturnIntervallStatistik2[ grenzen.length ];
        
        rawRows = new Vector<Messreihe>();
        
        for( int i = 0; i < grenzen.length; i++ ) { 
            System.out.println(">>> erzeuge RIS-Cont.: " + grenzen[i]+"_ris" );
            risContainer[i] = new ReturnIntervallStatistik2( grenzen[i]+"_ris", 
                              risBinning, risScale );
            risContainer[i].isContainerInstanz = true;
       }
        
    }
    
    private static void checkRqCollectors( int b ) {
        
        System.out.println( ">>>> Reihen ohne Edits: " + anzNullEdits[b] ); 
        
        System.out.println( ">>>> RQ" ); 
        int sum = 0;
        for( int i = 0; i < grenzen.length; i++ ) { 
            System.out.println( ">> " + mrRQ[i].dists.size() );
            sum = sum + mrRQ[i].dists.size();
        }
        System.out.println( "   summe=" + sum + "\n>>>> ANZ" );
        sum = 0;
        for( int i = 0; i < grenzen.length; i++ ) { 
            System.out.println( ">> " + mrANZ[i].dists.size() );
            sum = sum + mrRQ[i].dists.size();
            

            ReturnIntervallStatistik2.debug1[i].calcWS();
            System.out.println( ReturnIntervallStatistik2.debug1[i].getHistogram().toString() );

        }
        System.out.println( "   summe=" + sum + "\n ");
        
 
        
        
            
        
    }
    


    static public int[] grenzen = { 1,8,16,32,64,128,256,512,1024 };
    static ReturnIntervallStatistik2[] risContainer = null; 

    // Rq collector für die einzelnen NodeGroups ... 
    static HaeufigkeitsZaehlerDouble[] mrRQ = null;
//    static public void _collectRq( double _rq, int i, int id) { 
//        double rq = _rq / 24.0;
//        if ( Double.isNaN(rq) ) {
//            System.out.println( "> rq=" + rq ); 
//        }
//        else {
//            if ( debug ); 
//                
//            System.out.println( "> rq=" + rq + 
//                                "\ti=" + i + "\tid=" + id );
//            mrRQ[i].addData( rq);
//        }
//    }
    
    //
    // einsortieren in die Gruppen muss noch in der einzelnen 
    // RIS Instanz erfoglen.
    //
    static HaeufigkeitsZaehlerDouble hzANZ = new HaeufigkeitsZaehlerDouble();
    static HaeufigkeitsZaehlerDouble[] mrANZ = null;
    
    
    public static double rqMIN[] = null;
    public static void initRqMIN() { 
       
        
        rqMIN = new double[grenzen.length];
        
        for( int i = 1; i < grenzen.length; i++ ) {
             double Rq_max = (double)( 300 * 24 )/ (double) grenzen[i-1];
             
             rqMIN[i] = ReturnIntervallStatistik2.offset / Rq_max;
             
             System.out.println( grenzen[i-1] + " ... " + grenzen[i] + "\tRq_max=" + Rq_max + "\t" + rqMIN[i] );
        }
//        javax.swing.JOptionPane.showMessageDialog(null, "go");
    }    
    /**
     * anhand der Anzahl von Edits wird die Gruppe ermittelt, in die 
     * eine RIS eingefügt wird. Dabei gilt, dass eine RIS in alle wei
     * @param risEinzeln
     * @param anzahlEdits 
     */
    private static void collectForGroups(
            ReturnIntervallStatistik2 risEinzeln, int anzahlEdits) {

        int cc = 0;
        try {
            // für jede Grenze ...
            for ( int i = 1; i < grenzen.length ; i++ ) {
                if ( anzahlEdits <= grenzen[i] && anzahlEdits > grenzen[i-1] ) { 
                    
                   int indexGRUPPE = i-1; 
                   try {
                        risContainer[indexGRUPPE].add(risEinzeln); 
                        if( debug ) System.out.println( "i) " + i + "\t anzahl Edits=" + anzahlEdits + " indexGruppe:=" + indexGRUPPE + " ris.Rr=" + risEinzeln.getRq() );
                        mrANZ[indexGRUPPE].addData( anzahlEdits*1.0 );    
                        mrRQ[indexGRUPPE].addData( risEinzeln.getRq() );
                   }
                   catch (Exception ex) {
                                Logger.getLogger(Topic2.class.getName()).log(
                                Level.SEVERE, null, ex);
                   }   
                }
                else { 

                }
            }
        } 
        catch (Exception ex) {
            Logger.getLogger(Topic2.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        hzANZ.addData( anzahlEdits*1.0 );
    }

    private static void showRqHistogramme( int variante, int nodeGrupID ) {
        
     Vector<Messreihe> mrv = new Vector<Messreihe>();
     Vector<Messreihe> mrv3 = new Vector<Messreihe>();
     
     double maxRQ = 0.0;
     /**
      * Rg für alle "NodeGroups" ermitteln ...
      */
     for( int i = 1; i < grenzen.length; i++ ) {
         
        HaeufigkeitsZaehlerDouble zaehler = Topic2.mrRQ[i]; 
 
        zaehler.calcWS(); 
        
        Messreihe mr = zaehler.getHistogram();
        
        System.out.println(mr);
        double _maxRQ = mr.getMaxX();
        if ( _maxRQ > maxRQ ) maxRQ = _maxRQ;
//            try {
//                mr.addToX(i);
//            } catch (Exception ex) {
//                Logger.getLogger(Topic2.class.getName
//            }
        
        mr.setLabel( "#R_q|["+grenzen[i-1]+"..."+grenzen[i]+" ");
        mrv.add( mr ); 
     }
     
     /*
      * Anzahlen für alle "GRUPPEN" ermitteln ...
      */
     for( int i = 0; i < grenzen.length; i++ ) {
          
        HaeufigkeitsZaehlerDouble zaehler2 = Topic2.mrANZ[i];
  
        zaehler2.calcWS();

        Messreihe mr2 = zaehler2.getHistogram();
        mr2.setLabel(grenzen[i]+"_ANZ_Distr");
        mrv3.add( mr2 );
     }
     MyXYPlot.xRangDEFAULT_MIN = 0 ;     
     MyXYPlot.xRangDEFAULT_MAX = maxRQ + 5 ;
     
     MyXYPlot.yRangDEFAULT_MIN = 0 ;
     MyXYPlot.yRangDEFAULT_MAX = 200 ;

     MyXYPlot.rowONEDefualtColor = Color.BLACK;
     
     MyXYPlot.open(mrv,  
        labels[nodeGrupID] + "(V:"+variante+", offset="+
        ReturnIntervallStatistik2.offset+") distr( R_q ) " , "R_q","#",true );
//     MultiChart.open(mrv3,  
//        labels[nodeGrupID] + "(V:"+variante+", offset="+
//        ReturnIntervallStatistik2.offset+") distr( Anz ) " , 
//     "AnzEdits","#",true);
     

     
    }

    private static void debugMessreihe(Messreihe mr) {
        System.out.println( mr.label + "\t" + mr.getMaxY() + "\t" + mr.yValues.size() );
    }
    
    
    public static void logLineToFile( String key, String line ) { 
        try {
            projekt.logLine(key, line);
        } 
        catch (IOException ex) {
            Logger.getLogger(Topic2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     private static boolean _preCheck(Zeitreihe zr) {
        
        
        
        boolean b = true;
        
        double sum = 0.0;

        Enumeration en = zr.xValues.elements();
        
        double tvor = 0.0;
        if ( en.hasMoreElements() ) tvor =(Double)en.nextElement();
        
        while( en.hasMoreElements() ) {
            double tnach = (Double) (Double)en.nextElement();
            double v = (tnach - tvor) / (60.0 * 60.0 * 1000.0);
            sum = sum + v;
            tvor=tnach;
        }
        
       
        
        
        if ( sum > (300 * 24) ) {  
            System.out.println(  "*** " + sum + " \n " + zr.toString() );
        javax.swing.JOptionPane.showMessageDialog(null, "go" );
            b = false;
            try {
                projekt.logLine("RAW", sum + " \t " + zr.toString() +"\n" );
            } catch (IOException ex) {
                Logger.getLogger(ReturnIntervallStatistik2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return b;
    }
              
  
}
