package extraction;

//import charts.MultiBarChart;
import experiments.crosscorrelation.KreuzKorrelationTag;
import org.apache.hadoopts.data.series.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import experiments.crosscorrelation.KreuzKorrelation;
import experiments.linkstrength.CCFunction;

public class TimeSeriesFactory {
//    public static String folderIn = "C:/Users/kamir/Documents/DATA raw Wiki an Financial Markets/FinanceNetworks_v1/021_DAX/accessTS/";
    public static String folderIn = "C:/Users/kamir/Documents/DATA raw Wiki an Financial Markets/FD2/SuP500/";

    //public static String folderIn2 = "P:/PHYSICS/32bit/32bit_VphMean/";
    //public static String folderIn3 = "P:/PHYSICS/PHASE2/data/out/p3/extract/";
    
    public static int offset = 3;
    public static boolean doFilter = false;
    
    private static boolean DO_USE_RAW_DATA = false;
    
    public static Messreihe prepareAccessDataTAG2(int nodeID, int length) {
        Messreihe r = null;
        try {
            DO_USE_RAW_DATA = true;
            r = prepareAccessDataSTUNDE(nodeID, length);
            
            
        } catch (Exception ex) {
            System.err.println( ex.getMessage() );
        }
        return r;
    }
    
    /**
     * legth muss die Stunden angeben, denn erst am Ende wird gebinnt ...
     * 
     * @param nodeID
     * @param length
     * @return
     */
    private static Messreihe prepareAccessDataTAG(int nodeID, int length) {
        BufferedReader br = null;
        Messreihe mr = null;
        try {
            mr = new Messreihe();
            mr.setLabel("Access NodeID=" + nodeID);
            String fn = folderIn + nodeID + "_vph.dat";
            br = new BufferedReader(new FileReader(fn));
            int i = 0;
            while (br.ready() && i < length ) {
                
                String line = br.readLine();
                double d =  Double.parseDouble( line );

//                if( blockValue(i) ) {
//                     mr.addValuePair( i , 0);  // MW abziehen
//                     System.out.println( "... blocked ... " + nodeID   );
//                }
//                else {
                    mr.addValuePair( i , d - 24);  // MW abziehen
//                }
                i++;
            }

            if ( doFilter ) {
                mr.doFilter( 10.0 );
            }
           

            return mr;
        }
        catch (Exception ex) {
            Logger.getLogger(TimeSeriesFactory.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        finally {
            try {
                br.close();
            }
            catch (IOException ex) {
                Logger.getLogger(TimeSeriesFactory.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
        return mr;
    }

    public static Messreihe prepareAccessDataSTUNDE(int nodeID, int length) throws IOException, Exception {
        BufferedReader br = null;
        Messreihe mr = null;
        mr = new Messreihe();
        mr.setLabel("Access NodeID=" + nodeID);

        /**
         *   Nach dem Import in die lokale Arbeits-Dateiablage heisst
         *   die Datei so:
         */
        // String fn = folderIn + nodeID + "_vph.dat";
        // String fn2 = folderIn2 + "PageID_" + nodeID + ".txt";
        String fn2 = folderIn + "PageID_" + nodeID + ".wmd.h.dat";
        String fn3 = folderIn + "PageID_" + nodeID + ". nrv.h.dat";

        boolean clearValues = false;
        boolean detrendetValues = true;

/**
 *   Select, what file to load ...
 */
        File f1 = new File( fn2 );
        // System.out.println( fn2 + " \t" + f1.exists() );

        if ( !f1.exists() ) {
            
            
            f1 = new File( fn3 );
            // System.out.println( fn2 + " \t" + f1.exists() );
            
            if ( !f1.exists() ) {
                    Exception e = new Exception( "Access TS for PageID=" + nodeID + " is not in [" + fn2 +"; " + fn3 +"]");
                    // System.err.println( "Access TS for PageID=" + nodeID + " is not in [" + fn2 +"; " + fn3 +"]" );
                    throw e;
  
            }
            else {
                clearValues = true;
                detrendetValues = false;
            }
        }
        else {
            clearValues = false;
            detrendetValues = true;
        }
        

        if ( DO_USE_RAW_DATA ) { 
             f1 = new File( fn3 );
             clearValues = true;
             detrendetValues = false;
        }
        
        


        System.out.println( "*** LOAD : " + f1.getAbsolutePath() + " => " + f1.canRead() );

        br = new BufferedReader(new FileReader( f1.getAbsolutePath() ));
        int i = 0;
        while (br.ready() && i < length ) {

            String line = br.readLine();
            double d =  Double.parseDouble( line );
            if ( detrendetValues ) {
                mr.addValuePair( i , d   );  // MW abziehen
            }
            else {
                mr.addValuePair( i , d   );  // MW abziehen
            }
            i++;
        }

//        if ( doFilter ) {
//            mr.doFilter( 10.0 );
//        }

        return mr;
    }


    /**
     * De Vector mit den Long Werten der Edit-Zeiten wird in
     * eine Messreihe umgewandelt.
     *
     * @param mr
     * @param dates
     * @param length
     * @return
     */
    public static Messreihe prepareEditDataTAG(Messreihe mr,
            Vector<Long> dates, int length) {
        double[] data = new double[length];
        for (int i = 0; i < length; i++) {
            data[i] = 0.0;
        };

        int teiler = 1000 * 60 * 60 * 24;

        Calendar c = Calendar.getInstance();
        c.set( 2009, 0, 1, 0, 0, 0);
        Date d1 = c.getTime();

        Enumeration<Long> en = dates.elements();
        while (en.hasMoreElements()) {
            Long d2 = en.nextElement();

            Long dl = d2 - d1.getTime();

            int index = (int)(dl / (teiler) );

            if ( index < length ) {
                 data[index]++;
            }
        }

        for (int i = 0; i < length; i++) {
            mr.addValuePair(i, data[i] );
        };

        return mr;
    };




    /**
     * Auf Stundenbasis berechnete Anzahl von Edits in einem bestimmten
     * Zeitraum "days".
     *
     * "Dates" enthält die Zeitstempel an denen etwas bearbeitet werden.
     *
     * "TimeScale" dient der Variablen Einstellung auf Basis von
     * vielfachen von Stunden.
     *
     * @param dates
     * @param days
     * @param timeScale
     * @return
     */
    public static Messreihe prepareEditDataSTUNDE(
            Vector<Long> dates, int days, int timeScale) {

        Messreihe mr = new Messreihe();
        // length ist die Zahl der Felder ....
        // z.B.  300 Tage in Stundenauflösung
        //
        //    length = 300 * 24
        int teiler = 1000 * 60 * 60 * timeScale;
        int length = days * 24 / timeScale;

        double[] data = new double[length];
        for (int i = 0; i < length; i++) {
            data[i] = 0.0;
        };

        

        Enumeration<Long> en = dates.elements();
        
        Calendar c = Calendar.getInstance();
        // Offset bezüglich des ersten Tages ....
        // ... hängt von der SQL-Abfrage ab !
        c.set( 2009, 0, 1, 0, 0, 0);
        long date1 = c.getTimeInMillis();

        while (en.hasMoreElements()) {
            Long date2 = en.nextElement();            

            int index = (int)( (date2 - date1) / (teiler) );
         
            if ( index >= 0 && index < length ) {
                data[index]++;
//                System.out.println( "[+]" + date2 + "\t" + date1 + "\t" +
//                        index + "\t" + new Date( date2) );
            }
            else { 
//                System.out.println( "[-]" + date2 + "\t" + date1 + "\t" +
//                        index);
            };


        }
        // System.out.println( "[INFO]  Länge der Reihe: " + length );

        for (int i = 0; i < length; i++) {
            mr.addValuePair(i, data[i] );
        };
        mr.setLabel("EDIT_" + timeScale );

        return mr;
    };

    /**
     * Auf Stundenbasis berechnete Anzahl von Edits in einem bestimmten
     * Zeitraum "days".
     *
     * "Dates" enthält die Zeitstempel an denen etwas bearbeitet werden.
     *
     * "TimeScale" dient der Variablen Einstellung auf Basis von
     * vielfachen von Stunden.
     *
     * @param dates
     * @param days
     * @param timeScale
     * @return
     */
    public static Messreihe prepareCountDataSTUNDE(
            Vector<Long> dates, Date begin, int days, int timeScale) {

        Messreihe mr = new Messreihe();
        // length ist die Zahl der Felder ....
        // z.B.  300 Tage in Stundenauflösung
        //
        //    length = 300 * 24
        int teiler = 1000 * 60 * 60 * timeScale;
        int length = days * 24 / timeScale;

        double[] data = new double[length];
        for (int i = 0; i < length; i++) {
            data[i] = 0.0;
        }

        Enumeration<Long> en = dates.elements();

        Calendar c = Calendar.getInstance();
        // Offset bezüglich des ersten Tages ....
        // ... hängt von der SQL-Abfrage ab !
        c.setTime(begin);

        long date1 = c.getTimeInMillis();

        while (en.hasMoreElements()) {
            Long date2 = en.nextElement();

            int index = (int)( (date2 - date1) / (teiler) );

            if ( index >= 0 && index < length ) {
                data[index]++;
                System.out.println( "[+]" + date2 + "\t" + date1 + "\t" +
                        index + "\t" + new Date( date2) );
            }
            else {
                System.out.println( "[-]" + date2 + "\t" + date1 + "\t" +
                        index);
            };


        }
        System.out.println( "[INFO]  Länge der Reihe: " + length );

        for (int i = 0; i < length; i++) {
            mr.addValuePair(i, data[i] );
        };
        mr.setLabel("COUNT_" + timeScale );

        return mr;
    };




    /**
     * Beliebige Testdaten ...
     */
    private static Vector<Long> getReiheA() {
        Vector<Long> dates = new Vector<Long>();

        Calendar c = Calendar.getInstance();
        c.set( 2009, 0, 1, 1, 0, 0);
        Date d1 = c.getTime();

        c.set( 2009, 0, 5, 1, 0, 0);
        Date d2 = c.getTime();

        c.set( 2009, 0, 7, 1, 0, 0);
        Date d3 = c.getTime();

        c.set( 2009, 3, 7, 0, 0, 0);
        Date d4 = c.getTime();

        dates.add( d1.getTime() );
        dates.add( d2.getTime() );
        dates.add( d3.getTime() );
        dates.add( d4.getTime() );

        return dates;
    }


    /**
     * Beliebige Testdaten ...
     */
    private static Vector<Long> getReiheB() {
        Vector<Long> dates = new Vector<Long>();

        Calendar c = Calendar.getInstance();
        c.set( 2009, 0, 1 + offset, 1, 0, 0);
        Date d1 = c.getTime();

        c.set( 2009, 0, 5 + offset, 1, 0, 0);
        Date d2 = c.getTime();

        c.set( 2009, 0, 7 + offset, 1, 0, 0);
        Date d3 = c.getTime();

        c.set( 2009, 3, 7, 0, 0, 0);
        Date d4 = c.getTime();

        dates.add( d1.getTime() );
        dates.add( d2.getTime() );
        dates.add( d3.getTime() );
        dates.add( d4.getTime() );

        return dates;
    }

    // Testprogram ....
    //
    public static void main(String[] args) throws Exception {

        // Vergelich von Tages und Stunden-Extraction
        Vector<Long>  datesA = getReiheA();
        
        Vector<Long>  datesB = getReiheB();
        
        Messreihe a = TimeSeriesFactory.prepareEditDataSTUNDE( datesA, 300, 24 );
        Messreihe b = TimeSeriesFactory.prepareEditDataSTUNDE( datesB, 300, 24 );

        a.setLabel("a");
        b.setLabel("b");

        // ORIGINAL-Daten
        Vector<Messreihe> v = new Vector<Messreihe>();
        v.add(a);
        v.add(b);

        //MultiBarChart.open( v, "Edit time series " , "t" , "y(t)", true );

        KreuzKorrelation._defaultK = 45;

//        CheckInfluenceOfSingelPeaks._debug = true;
        // CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_NORMALIZED;
        CheckInfluenceOfSingelPeaks.mode = CheckInfluenceOfSingelPeaks.mode_CC_TAU_0;

        KreuzKorrelation kr = KreuzKorrelation.calcKR( a, b, true, false );

        double sv = CCFunction.calcStrength_VERSION_B( kr );

        System.out.println( "\n>>> sv=" + sv );

    }

    public static boolean doBlock = true;
    public static Messreihe blockSpecialValues( Messreihe r ) {
        if ( !doBlock ) return r;
        
        Messreihe mr = null;

        mr = new Messreihe();
        mr.setLabel(r.getLabel() +"(b)" );

        Enumeration en = r.yValues.elements();
        int i = 0;
        while ( en.hasMoreElements() ) {
            double d =  (Double)en.nextElement();

            if( blockValue(i) ) {
                 mr.addValuePair( i , 0);  // MW abziehen
                 if ( debug ) System.out.println( "... blocked ... " + i + " " + r.getLabel() );
            }
            else {
                mr.addValuePair( i , d);  // MW abziehen
            }
            i++;
        }

        return mr;
    };

            /**
     * An bestimmten Tagen gab es enorme Überhöhungen, daher sollen diese
     * Werte ausgeschlossen werden.
     *
     * die Methode ermittelt, ob ein Tag geblockt werden muss oder nicht.
     * Die Liste dieser Tage ist vorgegeben mit dem Array "tageZumBlocken".
     *
     * Es wird dann die "locale Variable" doBlock benutzt.
     *
     * @param tagID
     * @return
     */
    static public boolean blockValue(int tagID ) {
        boolean doBlockIT = false;
        for ( int i = 0; i < tageZumBlocken.length; i++ ) {
            if ( tagID == ( tageZumBlocken[i] - 1 ) ) {
                doBlockIT = true;
            }
        }
        return doBlockIT;
    };

    public static int[] tageZumBlocken = {
        30,31,32,91,152,244
    };

    public static boolean debug = false;

}
