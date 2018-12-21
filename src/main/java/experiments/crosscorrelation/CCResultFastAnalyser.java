/*
 *  Einlesen der CC-Results und Anzeige von statisischen Daten.
 *
 *  Es wird nur eine Art von CC-Result genutzt, alse NICHT
 *  mehrere Zeiten im Vergleich.
 *
 */

package experiments.crosscorrelation;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.TimeSeriesObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.jfree.ui.RefineryUtilities;
import research.wikinetworks.NodePair;
import org.apache.hadoopts.statistics.HaeufigkeitsZaehlerDouble;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.AllLinked32bit;
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;

/**
 * Based on the dataset "cc_edits" we constuct a *LinkGroup* for our preselected
 * *NodeGroupe*. The result is a Network related to a special time periode
 * the Crosscorrelation was calculated for.
 *
 * *CCResutls* is a kind of BaseData for *LinkGroup* construction.
 *
 * @author kamir
 */
public class CCResultFastAnalyser {

    static boolean singleCharts = true;
    static boolean groupedChart = false;
    
    static boolean createNamedList = false;
  

//    static String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_edits/";
//    static public String LABEL = "EDITS";

    static String[] lang = {  "-1" };// , "52", "60", "62", "72", "197" };
    static String[] anz = { "100" };

    

    /**
     * Wenn mehrere verglichen werden sollen dann sind hier die "SUBPFADE"
     * zu erfassen.
     *
     */
    //static String[] modes = { "T1_cut150", "T2_cut150" };
    static String[] modes = {
//        "allGroups_Supr_300days_Shuffling",
//        "allGroups_Supr_300days_noShuffling",
//        "allGroups_noSupr_300days_Shuffling",
//        "allGroups_noSupr_300days_noShuffling",
//        "32bit_ALL_links",
//        "32bit_ALL_links_SHUFFLED",
          "not_shuffled", // DO,SO
          "shuffled",     // DO,SO

    };

    /*
     * Hauptpfad zu den Eregebnissen
     */
    // static public String pfad = "G:/PHYSICS/RESULTS/SO 16.10.2011/";
    // static public String pfad = "G:/PHYSICS/RESULTS/MO 17.10.2011/";
    // static public String pfad = "G:/PHYSICS/RESULTS/MI 26.10.2011/blocked/";


    static public String pfad = "G:/PHYSICS/RESULTS/DO 27.10.2011/blocked/";       // do RUN IT !!!
//    static public String pfad = "G:/PHYSICS/RESULTS/SO 30.10.2011/";      // DO RUN IT !!!


    /**
     * offset zu den access- oder edits-Results
     */
    static public String extension = "/cc_access/";

    /*
     * Defaultsprache
     */
    static public int nrOfNodes = 100;
    static public int langID = -1;

    static public Hashtable<String,CCResults> results = new Hashtable<String,CCResults>();

    // die NodeGroup zur Zuordnung der Lang Id zur jeweiligen "Paar-Kombination]
    static NodeGroup ng = null;

    static public String getLangID( String name ) {
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        return s;
    };

    static public int getN( String name ) {
        boolean b = false;
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        s = st.nextToken();
        int n = Integer.parseInt(s);
        return n;
    };

    static public boolean isFor_N_Nodes( String name, int n ) {
        boolean b = false;
        StringTokenizer st = new StringTokenizer( name , "_");
        String s = st.nextToken();
        s = st.nextToken();
        int i = Integer.parseInt(s);
        if ( i == n ) b = true;
        return b;
    };
    
    
    
    
        // hier kommen die Zeilen an und werden in die TimeSeriesObjectn bzw.
    // Netzwerkdarstellungen überführt.
    static public NodePair parseLine2(String line, int mj, String lang) {
        NodePair np = null;

        StringTokenizer tok = new StringTokenizer(line);
        int i = tok.countTokens();
        if ( i < 5 ) {
            // System.out.println( i );
        }
        else {
           String pair = tok.nextToken();
           // ggf. entfernen
           String pair2 = tok.nextToken();
           String k = tok.nextToken();
           String stdDev = tok.nextToken();
           String maxY = tok.nextToken();
           String signLevel = tok.nextToken();

           np = new NodePair(pair, ng);
           np.setCCResults( k, stdDev, maxY, signLevel );
        }
        return np;
    }

    public static void createHistogramm( TimeSeriesObject mr ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, 100, 0, 10 );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };

    public static void createHistogramm( TimeSeriesObject mr, TimeSeriesObject mr2, TimeSeriesObject mr3) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );

        demo.useLegend = true;

        demo.addSerieWithBinning( mr, 100, 0, 10 );
        demo.addSerieWithBinning( mr2, 100, 0, 10 );
        demo.addSerieWithBinning( mr3, 100, 0, 10 );
        


        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };

    static HaeufigkeitsZaehlerDouble zaehler1 = new HaeufigkeitsZaehlerDouble();
    static HaeufigkeitsZaehlerDouble zaehler2 = new HaeufigkeitsZaehlerDouble();


    public static void main(String[] args) throws Exception {


        int c=0;
        boolean noBreake = true;

        int mj = 0;

        int max = lang.length;
        int min = 0;

        mr___t_vs_s = new TimeSeriesObject[lang.length];
        goodOnes = new Vector<NodePair>();

        for ( int mode = 0; mode < modes.length; mode++ ) {

            mj = mode;

            z=0;

            histS_k_EQUAL_0 = new TimeSeriesObject( modes[mj]+"  tau=0");
            histS_k_GT_0 = new TimeSeriesObject( modes[mj]+"  tau != 0");
            histS_k_ALL = new TimeSeriesObject( modes[mj]+"  all");


             zaehler1 = new HaeufigkeitsZaehlerDouble();
             zaehler2 = new HaeufigkeitsZaehlerDouble();


            for ( int i = min; i < max ; i++ ) {
                mr___t_vs_s[i] = new TimeSeriesObject("lang="+i);
                /**  USE CALCULATED FILENAME  **/

//                String ngfn = lang[i] +"_"+nrOfNodes+EditActivityFilter.extension;
//                ng = new NodeGroup( ngfn );

                /**  USE PREDEFINED GROUP  **/
                ng = new AllLinked32bit();
                String ngfn = ng.fn;

                File f = new File( pfad + modes[mj] + extension + ngfn +".cc.log" );
                if (!f.exists() ) {
                    System.out.println( ">>> File: " + f.getAbsolutePath() + " is not there ... ");
                    System.exit(0);
                }
   
                BufferedReader br = new BufferedReader( new FileReader( f.getAbsolutePath() ));
                while( br.ready() && noBreake ) {
                    String line = br.readLine();
                    if ( line.startsWith( "#") ) {
                        System.out.println( line );
                    }
                    else{
                        c++;
                        NodePair np = parseLine2( line, mj, lang[i] );
                        processNodePair( np, mj ,i);


                    }
                }

                /*
                 * REPORTING ...
                 */
                if ( singleCharts ) {
//                    DecimalFormat df = new DecimalFormat( "0.00");
//                    NodeGroup gp = new NodeGroup(goodOnes);
//                    gp.fn = "filtered2/" + lang[i]+"_NodesFromPairs_strength_gt_" + df.format(level) +"_at_tau=0.ng";
//                    gp.store();

                    System.out.println(">>> Scatterplot ... ");
//                    MyXYPlot.xRangDEFAULT_MIN = -15 ;
//                    MyXYPlot.xRangDEFAULT_MAX = 15 ;
//                    MyXYPlot.yRangDEFAULT_MIN = 0 ;
//                    MyXYPlot.yRangDEFAULT_MAX = 8 ;
//
//                    MyXYPlot.rowONEDefualtColor = Color.BLACK;
//
//                    MyXYPlot.open(  mr___t_vs_s, modes[mj]+" strength vs. tau", "tau", "strength", true);


//                    Muss beim PROCESSIEREN ERLEDIGT WERDEN !!!
//                    FileWriter fw = new FileWriter( gp.pfad + "filtered2/" + lang[i]+"_Pairs_strength_gt_" + df.format(level) +"_at_tau=0.dat" );
//                    fw.write( sb.toString() );
//                    fw.flush();
//                    fw.close();

                    System.out.println(">>> Histogramme ... ");
//                    createHistogramm(histS_k_EQUAL_0);
//                    createHistogramm(histS_k_GT_0);
//                    createHistogramm(histS_k_ALL);
                    createHistogramm(histS_k_GT_0,histS_k_EQUAL_0,histS_k_ALL );
                    
                     System.out.println(">>> Confidence Check ... ");



                    zaehler1.calcWS();
                    zaehler2.calcWS();

                    System.out.println( zaehler1.getConfidence().toString() );
                    TimeSeriesObject fmr = zaehler1.getConfidence();
                    fmr.setLabel( modes[mj] + " conf");
                    Vector<TimeSeriesObject> conf = new Vector<TimeSeriesObject>();
                    conf.add( fmr );
                    MyXYPlot.xRangDEFAULT_MIN = 0 ;
                    MyXYPlot.xRangDEFAULT_MAX = 8 ;
                    MyXYPlot.yRangDEFAULT_MIN = 0 ;
                    MyXYPlot.yRangDEFAULT_MAX = 1.2 ;
        
                    MyXYPlot.open(conf, "Confidence","strength","% of confidence",true);


//                    if ( createNamedList ) {
//                        /** aus der NodeGroup "goodOnes" **/
//                        NodePairListViewer v = new NodePairListViewer();
//                        v.createClearLists( new File(gp.pfad + "filtered2/" + lang[i]+"_Pairs_strength_gt_" + df.format(level) +"_at_tau=0.dat") );
//                    }
        }  // END OF READING CC-Files ...



        }
    }
//        if ( groupedChart ) {
//                DecimalFormat df = new DecimalFormat( "0.00");
//                NodeGroup gp = new NodeGroup(goodOnes);
//                gp.fn = "filtered2/all_selectionNodesOfPairs_strength_gt_" + df.format( level ) +"_at_tau=0.ng";
//                gp.store();
//
//                MyXYPlot.xRangDEFAULT_MIN = -15 ;
//                MyXYPlot.xRangDEFAULT_MAX = 15 ;
//                MyXYPlot.yRangDEFAULT_MIN = 0 ;
//                MyXYPlot.yRangDEFAULT_MAX = 8 ;
//
//                MyXYPlot.rowONEDefualtColor = Color.BLACK;
//
//                MyXYPlot.open(  mr___t_vs_s, modes[mj]+" strength vs. tau", "tau", "strength", true);
//
//                FileWriter fw = new FileWriter( gp.pfad + "filtered2/all_selectionOfPairs_strength_gt_" + df.format( level ) +"_at_tau=0.dat" );
//                fw.write( sb.toString() );
//                fw.flush();
//                fw.close();
//            }

    }

    static TimeSeriesObject[] mr___t_vs_s = null;
    /** gut sind die wo tau=0 && signLevel > level .... */
    static Vector<NodePair> goodOnes = null;

//    static StringBuffer sb = new StringBuffer();

    static TimeSeriesObject histS_k_EQUAL_0 = null;
    static TimeSeriesObject histS_k_GT_0 = null;
    static TimeSeriesObject histS_k_ALL = null;

    static double level = 3;

    static int z = 0;
    int zz = 0;
    public static void processNodePair( NodePair p1, int mj, int i) {

        z++;
        double s1 = p1.signLevel;
        double t1 = p1.k;

                    if ( p1.signLevel > level   ) {
//        if ( p1.signLevel > level && p1.k == 0  ) {
            mr___t_vs_s[i].addValuePair(t1,s1);
            goodOnes.add(p1);
            // sb.append( p1.pageIDA + "\t" + p1.pageIDB +"\n");
    
        }

        // in Blatt 13
        if ( p1.k == 0 ) {
            histS_k_EQUAL_0.addValue(s1);
            zaehler1.addData( s1 );
        }
        if ( p1.k != 0  ) {
            histS_k_GT_0.addValue(s1);
            zaehler2.addData( s1 );
        }
        histS_k_ALL.addValue(s1);
        if ( z > 100000 ) {
            System.out.print(".");
            z=0;
        }
    }

}
