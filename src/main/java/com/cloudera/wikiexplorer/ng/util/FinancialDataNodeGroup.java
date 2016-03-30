/*
 * A Node Group to load financial data, i.e. DAX or sp500 rows to handle 
 * them by the NetworxToolbox
 *  
 */

package com.cloudera.wikiexplorer.ng.util;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.io.ColumnValueCalculator;
import org.apache.hadoopts.data.io.MessreihenLoader;
import org.apache.hadoopts.data.io.TSValueCalculator;
import org.apache.hadoopts.data.series.Messreihe;
import extraction.TimeSeriesFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import research.wikinetworks.NodePairList;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import research.topics.networks.comparison.CC_Media_ResultMapper;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class FinancialDataNodeGroup extends NodeGroup implements TSValueCalculator {

    String folderName = null;
    String basefolder = "/home/kamir/DATA/dayly_stock_data/";
    
    public FinancialDataNodeGroup( String label, String label2 ) throws IOException {
        
        folderName = basefolder + label; 
        fn = "list_DAX.dat.ids.dat";
                
        fn = label2;
        name = fn;
        
        // access Data
        // load();
        
        for( int i = 0; i < defaulSize; i++ ) {
            ids[i] = i;
        }
        
        // stockData
        initSize();
        
        this._langID = "-1";
        this.setFull();
    };
    
    /**
     * ermittle Wieviele Dateien da sind.
     * 
     */
    private void initSize() {
       f = new File( folderName );
       System.out.println(  ">>>  canRead( " + f.getAbsolutePath() + ") => " +  f.canRead());
       System.out.flush();
       File[] all = f.listFiles();
       if ( all != null ) {
           defaulSize = all.length;
           System.out.println( ">>> " + all.length );
       }    
    }
    
    File f = null;
    int defaulSize = 0;

 
//
//    public PeakedRandomNodeGroup(int nrids) throws IOException {
//        this.defaulSize = nrids;
//        this.ids = new int[defaulSize];
//        for( int i = 0; i < defaulSize; i++ ) {
//            ids[i] = i;
//        }
//        this.fn = "random_nodes" + PeakedRandomNodeGroup.getStateExtension();
//        this.langID = "-1";
//        this.setFull();
//    }

    boolean unchecked = true;
    
    //
    public static int selectedColumn = 7;
    
    /**
     * 
     * Lade die Daten mit Tages-Summen aus den Rohen clicks, ohne Detrending ...
     * 
     * 
     * @return 
     */
    public boolean checkAccessTimeSeries() {

        if ( useBuffer ) return true;

        System.out.println(">>> load now: " + this.ids.length + " rows. (checkAccess())");
        int length = 299*24;

        StringBuffer sb = new StringBuffer(">>> ACCESS Error-Ids: " );
        
        boolean b = true;

        int x = 0;
        int y = 0;

        int counter = 0;
        int errors = 0;

        if ( hourlyAccessData == null ) {
            hourlyAccessData = new Vector<Messreihe>();
        }

        if ( accessActivity == null ) {
            accessActivity = new int[ids.length];
        }

        if ( accessMAX == null ) {
            accessMAX = new int[ids.length];
        }

        int z = 0;
        for( int i : ids ) {
            try {
                    Messreihe mr = TimeSeriesFactory.prepareAccessDataTAG2(i, length );

                    // System.out.println( i + "\t " + mr.getLabel() + "\t" + mr.yValues.size() );
                    mr = mr.setBinningX_sum(24);
                    mr.calcAverage();

                    accessActivity[ counter ] = (int)mr.summeY();
                    accessMAX[ counter ] = (int)mr.getMaxY();

                    if ( TimeSeriesFactory.doBlock ) mr = TimeSeriesFactory.blockSpecialValues(mr);

                    if ( doShuffle ) mr.shuffleYValues();

                    if ( supressFirstPart ) mr.supressAtStart( offsetAtStart , 0);
                    if ( supressLastPart ) mr.supressAtEnd( offsetAtEnd , 0);

                    // System.out.println( counter + "\t" + i + "\t " + mr.getLabel() + "\t" + mr.yValues.size() );
                    accessReihen.add(mr);

                    if ( y == 1000) {
                        System.out.print("\n");
                        y = 0;
                    }

                    if ( x == 100) {
                        System.out.print(".");
                        x = 0;
                    }
                    x++;
                    y++;
                    z++;
                 


            }
            catch (Exception ex) {
                sb.append( i + " " );
                System.err.println( ex.getMessage() );
                errors++;
                b = false;
            }
            counter++;
        }
        if ( errors > 0 ) {
            System.err.println( sb.toString() );
            System.out.println( "[#of missing rows = " + errors + "]");
            System.out.println( "[#of available rows = " + z + "]");            
        }
        return b;
    }

    public static boolean doUseLogReturn = false;
    public static boolean doUseAbsLogReturn = false;
    
    public boolean checkStockTimeSeries() {
        if ( doUseLogReturn ) {
                System.out.println( ">>> calc logreturn for stock prices ...");
        } 
        if ( doUseAbsLogReturn ) {
                System.out.println( ">>> calc also abs logreturn for stock prices ...");
        } 
         
        if ( unchecked ) {
            File[] files = f.listFiles();
            for( File f : files ) {

                TSValueCalculator mapper = (TSValueCalculator)this;
                
                Messreihe mr = this.loadRows( f, mapper );
                // System.err.println( mr.yValues.size() );
                        
                stockDataReihen.add( mr );  
                
            }
            unchecked = false;
            
            
        }
        
        int i = 0;
            for( Messreihe mr : stockDataReihen ) { 
                System.out.println( "# ("+i+") company: " + mr.getLabel() + "  nr of datapoints:=" + mr.yValues.size() );
                i++;               
            }
        return true;
    }
    
    public void showRawData() { 
        MultiChart.yRangDEFAULT_MIN = 0;
        MultiChart.yRangDEFAULT_MAX = 100;
        
        int n = stockDataReihen.size();
        MultiChart.open( stockDataReihen, "financial data (" + n + " series)", "t", "y(t)" , false );
    }
    
    public Vector<Messreihe> bufferStockDataReihen = null;
    public List<Date> initShortRows() { 
    
        // Buffer anlegen....
        if ( bufferStockDataReihen == null ) bufferStockDataReihen = stockDataReihen;
    
        System.out.println( "[EXTRACT]  " + calBegin.getTime() + " bis: " + calEnd.getTime());
        
        // Liste ermitteln ...
        Messreihe ref = stockDataReihen.elementAt(0);
        System.out.println( ref );
        
        List l = ref.getOrderedDateKeys( calBegin.getTime() , calEnd.getTime() );
        System.out.println( l );
        
        Vector<Messreihe> mrN = new Vector<Messreihe>();
        
        // Filtern aller Reihen
        for( Messreihe mr: bufferStockDataReihen) { 
            
            if ( doUseLogReturn ) { 
                Messreihe mrNEU = calcLogReturn(l, mr);
                mrN.add(mrNEU); 
            }
            else { 
                mr.filterAndMapBack( l );
                mrN.add(mr); 
            }
            
        }
        
        // stezne der gilterten Reihen in stockDataReihen ...
        stockDataReihen = mrN;
        
        return l;
        
    }
    
    /** Analogie zur Methode filterAndMapBack() in Messreihe ... */
    public Messreihe calcLogReturn( List<Date> liste, Messreihe mr ) {
        // System.out.println( l );
        mr.yValues = new Vector<Double>();
        mr.xValues = new Vector<Double>();
        
        Iterator it = liste.iterator();
        Date d = (Date)it.next();
        Double v0 = mr.dateHash.get( d );
        while( it.hasNext() ) { 
            
            d = (Date)it.next();
            Double v1 = mr.dateHash.get( d );
            
            if ( v1 == null ) v1 = 0.0;
            if ( v0 == null ) v0 = 0.0;
            
            double ln_pt1 = 0.0;
            double ln_pt = 0.0;
            
            if ( v1 != 0.0 ) {
               ln_pt1 = Math.log( v1 );
            } 
                               
            if ( v0 != 0.0 ) {
               ln_pt = Math.log( v0 );
            } 
              
            double logret = ln_pt1 - ln_pt;
                           
            double value = logret;
            if ( doUseAbsLogReturn ) {
                value = Math.abs( logret );                 
            }
            
            v0 = v1;
            
            mr.addValue( value );
        }
        return mr;
    }    
    
    private Messreihe loadRows(File f, TSValueCalculator mapper) {
        Messreihe mrA = null;
        System.out.println(">>> use network link-table : "
                + f.getAbsolutePath() + " -> " + f.exists());
        System.out.println(">>> use value mapper : " + mapper.getName()); 
        
        mrA = MessreihenLoader.getLoader()._loadMessreihe_For_FD( f, mapper, " ", doUseLogReturn);
        return mrA;
    }


    @Override
    public boolean checkEditTimeSeries() {
        return false;
    }
    

    
    
//    public Vector<Messreihe> _calcLogReturns( Vector<Messreihe> mr ) { 
//        System.out.println( mr.size() );
//        Vector<Messreihe> mrs = new Vector<Messreihe>();
//        for( Messreihe m : mr ) { 
//            Messreihe mlogret = calcLogReturns(m);
//            mrs.add(mlogret);
//        }
//        System.out.println( mrs.size() );
//        MultiChart.open(mrs, false, "Logarithmic Returns");
//        return mrs;
//    }
//
//    
//    
//    public Messreihe calcLogReturns( Messreihe mr ) { 
//        int i = 0;
//        Messreihe m = new Messreihe();
//        m.setLabel( mr.getLabel() + "_logreturn" );
//        Enumeration en = mr.yValues.elements();
//        double v0 = (Double)en.nextElement();
//        double v1 = 0.0;
//        while( en.hasMoreElements() ) {
//            
//            v1 = (Double)en.nextElement();
//
//            double ln_pt1 = 0.0;
//            double ln_pt = 0.0;
//            
//            if ( v1 != 0.0 ) {
//               ln_pt1 = Math.log( v1 );
//            } 
//                
//               
//            if ( v0 != 0.0 ) {
//               ln_pt = Math.log( v0 );
//            } 
//              
//            double logret = ln_pt1 - ln_pt;
//            double value = logret;
//            if ( doUseAbsLogReturn ) {
//                value = Math.abs( logret );
//                System.err.println( "~~~~~~~~~~" );
//            }
//            
//            m.addValuePair( i , value );
//            v0 = v1;
//            i++;
//        }
//        return m;
//    }

    public static void main( String[] args ) throws IOException {

        stdlib.StdRandom.initRandomGen(0);
        
        

        FinancialDataNodeGroup ng = new FinancialDataNodeGroup( "dax" , "DAXTEST" );
        
        ng.calBegin.set( 2008, 11, 31 );
        ng.calEnd.set( 2009, 10, 1 );  
    
        ng.checkStockTimeSeries();
        
        ng.checkAccessTimeSeries();
        ng.checkEditTimeSeries();

        ng._initWhatToUse();
        
     
        ng.prepareRows( ng.getAaccessReihen(), ng.getStockDataReihen() );
        
        MultiChart.open(ng.getAaccessReihen(),  "Wikipedia Access Data - FILTERED" , "t [days]", "# nr of page clicks per day",false);
        MultiChart.open(ng.getStockDataReihen(),  "Stock Data - Volume" , "t [days]", "traded volume",false);
        
        
//        FinancialDataNodeGroup ng2 = new FinancialDataNodeGroup( "sp500" );
//        ng2.checkAccessTimeSeries();
//        ng2.checkEditTimeSeries();
//
//        ng2._initWhatToUse();
//        
//        MultiChart.open(ng2.getAaccessReihen(), false);

    }
    

    
        /**
     * Merge two different types of datarow to a comparable set.
     * 
     * 1.) an access time serie is loaded - hourly resolution
     * 2.) a date-hashed value row with dayly resolution is used as a refernces
     * 
     * 3.) we aggregate and sort and filter row 1 to match row 2 structure
     * 
     * 
     * 
     * REIHE 1 muss die absolute Zahl sein ... oder ?
     * 
     * @param args
     * @throws IOException 
     */
    public Calendar calBegin = new GregorianCalendar();
    public Calendar calEnd = new GregorianCalendar();
    public void prepareRows( Vector<Messreihe> accessRows, Vector<Messreihe> stockRows ) { 
        
//        MultiChart.open( accessRows,  "Wikipedia Access Data - RAW" , "t [days]", "# nr of page clicks per day",false);
//        
//        javax.swing.JOptionPane.showInputDialog("HI");
        
        Vector<Messreihe> cutStockRows = new Vector<Messreihe>();
        Vector<Messreihe> cleanedAccessRows = new Vector<Messreihe>();
         
        
        for( Messreihe mr : stockRows ) { 
            Messreihe neu = new Messreihe();
            
            for( Date d : mr.dateHash.keySet() ) { 
                if ( d.after(calBegin.getTime()) && d.before( calEnd.getTime() )) {
                    //System.out.println( d.toString() );
                    neu.hashValueByDate(d, mr.dateHash.get(d) ); 
                }
            }
            neu.mapDateHashedValuesToRow();
            cutStockRows.add(neu);
        }
        this.stockDataReihen = cutStockRows;
        

        
        List<Date> l = stockDataReihen.elementAt(0).getOrderedDateKeys();
        for( Messreihe mr2 : accessRows ) { 
            Calendar fd = new GregorianCalendar();
            fd.clear();
            fd.set( 2009, 0, 1 );
            
            mr2.mapDataToDateHash( fd );
            mr2.filterAndMapBack( l );
            
            cleanedAccessRows.add( mr2 );
        }
        this.accessReihen = cleanedAccessRows;
        
        if ( useStockData ) { 
            accessRows = stockDataReihen;
        } 
        else { 
            accessRows = cleanedAccessRows;
        }
        
        MultiChart.open( cleanedAccessRows,  "Wikipedia Access Data - filtered" , "t [days]", "# nr of page clicks per day",false);
        
    }

    
    // blockieren von : neglecting weekends and bank holidays etc?
    
    
    // YEAR MONTH DAY OPEN HIGH LOW CLOSE VOLUME ADJ.CLOSE
    @Override
    public double getValue( String[] line) {
        return Double.parseDouble( line[selectedColumn] );
    }
    
    public Date getDate( String[] line) {
        
        Date date = null;
        int y = Integer.parseInt( line[0] );
        int m = Integer.parseInt( line[1] );
        int d = Integer.parseInt( line[2] );
        
        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set( y, m-1, d );
        date = cal.getTime();
        
        return date;
    }
    
    @Override
    public void setTimeIntervall(Date begin, Date end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createRandomSample() {
        
        for( int i = 0; i < 30; i++ ) {
             Messreihe mr = Messreihe.getGaussianDistribution(8000, 100.0, 15.0);
             mr.setLabel( i + ".a) " + mr.getLabel() );
             accessReihen.add( mr );  
        }
        
        for( int i = 0; i < 30; i++ ) {
             Messreihe mr = Messreihe.getGaussianDistribution(8000, 80.0, 5.0);
             mr.setLabel( i + ".e) " + mr.getLabel() );
             editReihen.add( mr );  
        }
        
        for( int i = 0; i < 30; i++ ) {
             Messreihe mr = Messreihe.getGaussianDistribution(8000, 150.0, 25.0);
             mr.setLabel( i + ".s) " + mr.getLabel() );
             stockDataReihen.add( mr );  
        }
        
        unchecked = false;
        
//        MultiChart.open(accessReihen);
//        MultiChart.open(editReihen);
//        MultiChart.open(stockDataReihen);
    }

}
