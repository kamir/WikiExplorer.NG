package m3.research;

/**
 *
 * Show a time series with +/- sigma ranges
 *
 * An instance of the SigmaBandTool is created ...
 *
 * SigmaBandTool sf = new SigmaBandTool();
 *
 * Individual time series are collected in the tool.
 *
 * sf.addCollect(mr,false);
 *
 * A container can also be added.
 *
 * sf.addCollect(mr,false);
 *
 * Finally we have to aggregate and to plot the result:
 *
 * sf.aggregate(); sf.plot();
 *
 */
import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.io.File;
import java.util.Vector;

/**
 *
 * @author kamir
 */
public class SigmaBandTool extends Messreihe {
    
    public static String exportFolder = "/Volumes/MyExternalDrive/CALCULATIONS/data/export";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int z = 10;

        stdlib.StdRandom.initRandomGen(1);

        SigmaBandTool sf = new SigmaBandTool();

        // here we do some tests 
        Messreihe[] rows = new Messreihe[z + 50];

        int LENGTH = 24;  // * 365

        double sigma = 2.0;

        for (int i = 0; i < z; i++) {

            double mw = 0.0;

            System.out.println("======== " + sigma + " " + mw);

            // one year with one point per hour ...  
            // Messreihe mr = Messreihe.getGaussianDistribution( sf.scale * 365, 10.0, 5.0 );
            Messreihe mr = Messreihe.getGaussianDistribution(LENGTH * 365, mw, sigma);
            rows[i] = mr;

            sf.addCollect(mr, false);
        }

        sf.aggregate();

        sf.plotRawRows();
        sf.plotAndStore( "DEMO" );

    }
    
    /**
     * 
     * @param s 
     */
    public static void setExportFolder(String s) {

        exportFolder = s;
        
        File f = new File( exportFolder );
        if ( f.exists() ) System.out.println( ">>> SBT exportFolder : " + f.getAbsolutePath() + " is ready.");
        else {
            f.mkdir();
            System.out.println( ">>> SBT exportFolder :" + f.getAbsolutePath() + " was created.");
        }
    }

    /**
     * Collection of raw time series ...
     * 
     */
    Vector<Messreihe> rows = new Vector<Messreihe>();
    
    /**
     * Collection of trend series ...
     * Only available if trend calculation was applied to the raw series. 
     */
    Vector<Messreihe> trends = new Vector<Messreihe>();
    
    // factor to define the width of the sigma band
    double upperTS = 1.0;
    double lowerTS = -1.0;

    /**
     * Group statistics
     */
    Messreihe averageRows = null;
    Messreihe sigmaRows = null;

    Messreihe averageTrends = null;
    Messreihe sigmaTrends = null;

    Messreihe sum = null;

    /**
     * Add a collection to the tool ...
     * 
     * @param mrs
     * @param aggregateNow 
     */
    private void addCollection(Messreihe[] mrs, boolean aggregateNow) {

        System.out.println( ">>> added " + mrs.length + " rows to SBT: " + getLabel() );

        for (Messreihe mr : mrs) {
            rows.add(mr);

            if (aggregateNow) {
                aggregate();
            }
        }
    }

    boolean debug = true;
    public void addCollect(Vector<Messreihe> vmr, boolean aggregateNow) {
        
        if ( debug ) System.out.println( "\n\n\n\n### added " + vmr.size() + " rows to SBT: " + getLabel() );

        for (Messreihe mr : vmr) {
            addCollect(mr, aggregateNow);
        }
    }

    public void addCollect(Messreihe mr, boolean aggregateNow) {

        if ( debug ) System.out.println( ">>> added one row to SBT: " + getLabel() );

        rows.add(mr);

        if (aggregateNow) {
            aggregate();
        }
    }

    private void plotRows(Vector<Messreihe> rawRows, String label) {

        MultiChart.yRangDEFAULT_MAX = 1;
        MultiChart.yRangDEFAULT_MIN = 0;
        MultiChart.xRangDEFAULT_MAX = 1;
        MultiChart.xRangDEFAULT_MIN = 0;
        MultiChart.setDefaultRange = true;

        MultiChart.setDefaultColors();

        MultiChart.open(rawRows, label, "H", "#", true);
    }

    private void plotRawRows() {
        Vector<Messreihe> rawRows = rows;

        MultiChart.yRangDEFAULT_MAX = 1;
        MultiChart.yRangDEFAULT_MIN = -1;
        MultiChart.setDefaultRange = true;

        MultiChart.open(rawRows, "raw rows", "t", "y(t)", false);
    }

//    public void plot() {
//        plot("random test data", null);
//    }

    /**
     * Prepare a set of for rows to describe the trends. 
     *   
     *     average and +/- sigma
     * 
     * @return 
     */
    private Vector<Messreihe> getTrendRows() {

        Vector<Messreihe> trendRows = new Vector();

        aggregate(); // populate the rows averageRows and mwBINNED

        Messreihe upper = averageTrends.add(sigmaTrends.scaleY_2(upperTS));
        Messreihe lower = averageTrends.add(sigmaTrends.scaleY_2(lowerTS));

        trendRows.add(averageTrends);
        trendRows.add(upper);
        trendRows.add(lower);
        trendRows.add(sigmaTrends);

        return trendRows;
    }
        
    /**
     * Prepare a set of for rows to describe the raw series. 
     *   
     *     average and +/- sigma
     * 
     * @return 
     */
    public Vector<Messreihe> getRows() {

        Vector<Messreihe> plotRows = new Vector();

        aggregate(); // populate the rows averageRows and mwBINNED

        Messreihe upper = averageRows.add(sigmaRows.scaleY_2(upperTS));
        Messreihe lower = averageRows.add(sigmaRows.scaleY_2(lowerTS));

        plotRows.add(averageRows);
        plotRows.add(upper);
        plotRows.add(lower);
        plotRows.add(sigmaRows);
//        plotRows.add(sum);

        return plotRows;
    }

    public void plotAndStore(String fn) {

        Vector<Messreihe> plotRows = getRows();

        MultiChart.yRangDEFAULT_MAX = 10000;
        MultiChart.yRangDEFAULT_MIN = 0;
        MultiChart.setDefaultRange = false; // AUTO 

        String comment = "ROWS";
        String dir = exportFolder;
        
        File f = new File( exportFolder );
        if ( !f.exists() )
            f.mkdir();

        MultiChart.openAndStore(plotRows, "SBT_ROWS_" + fn, "t", "<y(t)> , sigma(t)", true, dir, "SBT_ROWS_" + fn, comment);
        
        MesswertTabelle tab = new MesswertTabelle();
        File f2 = new File( exportFolder + "/SBT_ROWS_SINGLE_" + fn + ".dat");
        tab.setMessReihen( rows );
        tab.writeToFile(f2);
    }
    
    public void _plotAndStoreTrends( String fn) {

        Vector<Messreihe> plotRows = getTrendRows();

//        MultiChart.yRangDEFAULT_MAX = 100;
//        MultiChart.yRangDEFAULT_MIN = 0;
//        MultiChart.xRangDEFAULT_MAX = 6;
//        MultiChart.xRangDEFAULT_MIN = 0;
//        MultiChart.setDefaultRange = true;

        String comment = "" + this.rows.size();
        String dir = exportFolder;

        File f = new File( exportFolder );
        if ( !f.exists() )
            f.mkdir();
        
        MultiChart.openAndStore(plotRows, "SBT_TRENDS_GROUPS_" + fn , "t", "y(t)", true, dir, "SBT_TRENDS_GROUPS_" + fn, comment);
        
//        MesswertTabelle tab = new MesswertTabelle();
//        File f2 = new File(exportFolder + "/SBT_TRENDS_GROUPS" + fn + ".dat");
//        tab.setMessReihen( rows );
//        tab.writeToFile(f2);
    
    }


    public void plot(String l, int[] labels) {

        Vector<Messreihe> plotRows = getRows();

        if (labels != null) {
            plotRows = relabelRows(plotRows, labels);
        }

        MultiChart.yRangDEFAULT_MAX = 100;
        MultiChart.yRangDEFAULT_MIN = -100;
        MultiChart.setDefaultRange = true;

        MultiChart.open(plotRows, l + " (no binning)", "t", "<y(t)> , sigma(t)", true);
    }

    boolean isAggregated = false;

    public void aggregate() {
 
        if (isAggregated) {
            System.out.println(">>> SigmaBandTool is aggregated. (Label: " + getLabel() + ")" );
            return;
        }
        
        System.out.println("--> SigmaBandTool aggregates now ... (Label: " + getLabel() + ")" );
       
        sum = new Messreihe();
       
        trends = new Vector<Messreihe>();
        
        // Extract the trends and calculate the sum ...
        for (Messreihe m : rows) {
            trends.add( m.getTrendRow() );
            sum = sum.add(m);
        }
 
        averageRows = Messreihe.averageForAll(rows);
        averageRows.setLabel(getLabel() + "_" + averageRows.getLabel() );
        
        sigmaRows = Messreihe.sigmaForAll(rows);
        sigmaRows.setLabel(getLabel() + "_" + sigmaRows.getLabel() );
        
        averageTrends = Messreihe.averageForAll(trends);
        averageTrends.setLabel(getLabel() + "_" + averageTrends.getLabel() );

        sigmaTrends = Messreihe.sigmaForAll(trends);
        sigmaTrends.setLabel( getLabel() + "_" + sigmaTrends.getLabel() );

        isAggregated = true;
    }
    
    

    private Vector<Messreihe> relabelRows(Vector<Messreihe> plotRows, int[] labels) {

        for (Messreihe m : plotRows) {
            for (int i = 0; i < m.xValues.size(); i++) {
                m.xValues.set(i, labels[i]);
            }
        }

        return plotRows;
    }

    public void storeRawAndSigmaBandTables(String ln, String tsRange, String pn, int i ) {

        MesswertTabelle tab = new MesswertTabelle();
        File f = new File( exportFolder + "/TS_ROWS_" + tsRange + "_" + pn + "_" + i + "_" + ln + ".dat");
        tab.setMessReihen( rows );
        tab.writeToFile(f);

        MesswertTabelle tab2 = new MesswertTabelle();
        File f2 = new File( exportFolder + "/TS_ROWS_SIGMABANDS_" + tsRange + "_" + pn + "_" + i + "_" + ln + ".dat");
        tab2.setMessReihen(getRows());
        tab2.writeToFile(f2);
        
        MesswertTabelle tab3 = new MesswertTabelle();
        File f3 = new File( exportFolder + "/TS_TRENDS_SIGMABANDS_" + tsRange + "_" + pn + "_" + i + "_" + ln + ".dat");
        tab3.setMessReihen(getTrendRows());
        tab3.writeToFile(f3);

    }


}
