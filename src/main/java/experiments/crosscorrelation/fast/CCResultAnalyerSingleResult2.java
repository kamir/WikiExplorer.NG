/*
 *  Einlesen der CC-Results und Anzeige von statisischen Daten.
 *
 *  Es wird nur eine Art von CC-Result genutzt, alse NICHT
 *  mehrere Zeiten im Vergleich.
 *
 */

package experiments.crosscorrelation.fast;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;
import org.jfree.ui.RefineryUtilities;
import research.wikinetworks.NodePair;
import research.wikinetworks.NodePairListViewer;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
import com.cloudera.wikiexplorer.ng.util.nodegroups.AllLinked32bit;
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;
import experiments.crosscorrelation.CCResults;

/**
 * Based on the dataset "cc_edits" we constuct a *LinkGroup* for our preselected
 * *NodeGroupe*. The result is a Network related to a special time periode
 * the Crosscorrelation was calculated for.
 *
 * *CCResutls* is a kind of BaseData for *LinkGroup* construction.
 *
 * @author kamir
 */
public class CCResultAnalyerSingleResult2 {

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
          "not_shuffled", // DO
          "shuffled"       // DO
    };

    /*
     * Hauptpfad zu den Eregebnissen
     */
    // static public String pfad = "G:/PHYSICS/RESULTS/SO 16.10.2011/";
    // static public String pfad = "G:/PHYSICS/RESULTS/MO 17.10.2011/";
    // static public String pfad = "G:/PHYSICS/RESULTS/MI 26.10.2011/blocked/";


    static public String pfad = "G:/PHYSICS/RESULTS/DO 27.10.2011/blocked/";       // DO RUN IT !!!
//    static public String pfad = "G:/PHYSICS/RESULTS/SO 30.10.2011/filtered/";      // DO RUN IT !!!


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

    public static void main(String[] args) throws Exception {

        int c=0;
        boolean noBreake = true;

        int mj = 0;

        int max = lang.length;
        int min = 0;

        for ( int mode = 0; mode < modes.length; mode++ ) {

            mj = mode;

            for ( int i = min; i < max ; i++ ) {

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

                /** load Resultset into a Hash ... **/
                CCResults ccr = new CCResults();
                ccr.lang = lang[i];
                ccr.nrOfNodes = nrOfNodes;
                String key = getKey( lang[i] , mj );

                results.put(key, ccr);

                System.out.println("> key=" + key + f.getAbsolutePath() + " " + f.canRead() );

                BufferedReader br = new BufferedReader( new FileReader( f.getAbsolutePath() ));
                while( br.ready() && noBreake ) {
                    String line = br.readLine();
                    if ( line.startsWith( "#") ) {
                        System.out.println( line );
                    }
                    else{
                        c++;
                        parseLine( line, mj, lang[i] );
                    }
                }                
        }  // END OF READING CC-Files ...


        //
        Messreihe[] mr___t_vs_s = new Messreihe[lang.length];
       
        CCResults[] ccr = new CCResults[lang.length];

        // eine Messreihe anlegen für tau vs. s Diagramm
        for ( int i = min; i < max ; i++ ) {
            mr___t_vs_s[i] = new Messreihe();
            mr___t_vs_s[i].setLabel( "lang=" + lang[i] );
            for( int j = 0; j < 2; j++ ) {
                String key = getKey( lang[i] ,mj );
                ccr[i] = results.get(key);
            }
        }

        /** gut sind die wo tau=0 && signLevel > level .... */
        Vector<NodePair> goodOnesA = new Vector<NodePair>();

        StringBuffer sbA = new StringBuffer();

        // für die Wiederhoilung zw. 3 und 5
        double strength_min = 5;
        boolean noBreak = true;

        Messreihe histS_k_EQUAL_0 = new Messreihe( modes[mj]+"  tau=0");
        Messreihe histS_k_GT_0 = new Messreihe( modes[mj]+"  tau != 0");
        Messreihe histS_k_ALL = new Messreihe( modes[mj]+"  all");


        histS_k_EQUAL_0.setLabel("Hist. strength (tau=0)");

        while ( noBreak ) {
            double level = strength_min;
            goodOnesA = new Vector<NodePair>();

            // for every language separate ...
            for ( int i = min; i < max ; i++ ) {

                Vector<NodePair> goodOnes = new Vector<NodePair>();
                StringBuffer sb = new StringBuffer();

                System.out.println( ccr[i].lang );
                System.out.println( ccr[i] );

                int z = 0;
                for ( NodePair p1 : ccr[i].paare ) {

    //                System.out.print( p1.pageIDA+"_"+p1.pageIDB + " :: " );

                    double s1 = p1.signLevel;
                    double t1 = p1.k;

//                    if ( p1.signLevel > level   ) {
                    if ( p1.signLevel > level && p1.k == 0  ) {
                        mr___t_vs_s[i].addValuePair(t1,s1);
                        goodOnes.add(p1);
                        goodOnesA.add(p1);
                        sb.append( p1.pageIDA + "\t" + p1.pageIDB +"\n");
                        sbA.append( p1.pageIDA + "\t" + p1.pageIDB +"\n");
                    }

                    // in Blatt 13
                    if ( p1.k == 0 ) {
                        histS_k_EQUAL_0.addValue(s1);
                    }
                    if ( p1.k != 0  ) {
                        histS_k_GT_0.addValue(s1);
                    }
                    histS_k_ALL.addValue(s1);
                    z++;
                }


                /*
                 * REPORTING ...
                 */
                if ( singleCharts ) {
                    DecimalFormat df = new DecimalFormat( "0.00");
                    NodeGroup gp = new NodeGroup(goodOnes);
                    gp.fn = "filtered2/" + lang[i]+"_NodesFromPairs_strength_gt_" + df.format(level) +"_at_tau=0.ng";
                    gp.store();

                    MyXYPlot.xRangDEFAULT_MIN = -15 ;
                    MyXYPlot.xRangDEFAULT_MAX = 15 ;
                    MyXYPlot.yRangDEFAULT_MIN = 0 ;
                    MyXYPlot.yRangDEFAULT_MAX = 8 ;

                    MyXYPlot.rowONEDefualtColor = Color.BLACK;

                    MyXYPlot.open(  mr___t_vs_s, modes[mj]+" strength vs. tau", "tau", "strength", true);

                    FileWriter fw = new FileWriter( gp.pfad + "filtered2/" + lang[i]+"_Pairs_strength_gt_" + df.format(level) +"_at_tau=0.dat" );
                    fw.write( sb.toString() );
                    fw.flush();
                    fw.close();

                    createHistogramm(histS_k_EQUAL_0);
                    createHistogramm(histS_k_GT_0);
                    createHistogramm(histS_k_ALL);

                    if ( createNamedList ) {
                        /** aus der NodeGroup "goodOnes" **/
                        NodePairListViewer v = new NodePairListViewer();
                        v.createClearLists( new File(gp.pfad + "filtered2/" + lang[i]+"_Pairs_strength_gt_" + df.format(level) +"_at_tau=0.dat") );
               }
                }
            }

            if ( groupedChart ) {
                DecimalFormat df = new DecimalFormat( "0.00");
                NodeGroup gp = new NodeGroup(goodOnesA);
                gp.fn = "filtered2/all_selectionNodesOfPairs_strength_gt_" + df.format( level ) +"_at_tau=0.ng";
                gp.store();

                MyXYPlot.xRangDEFAULT_MIN = -15 ;
                MyXYPlot.xRangDEFAULT_MAX = 15 ;
                MyXYPlot.yRangDEFAULT_MIN = 0 ;
                MyXYPlot.yRangDEFAULT_MAX = 8 ;

                MyXYPlot.rowONEDefualtColor = Color.BLACK;

                MyXYPlot.open(  mr___t_vs_s, modes[mj]+" strength vs. tau", "tau", "strength", true);

                FileWriter fw = new FileWriter( gp.pfad + "filtered2/all_selectionOfPairs_strength_gt_" + df.format( level ) +"_at_tau=0.dat" );
                fw.write( sbA.toString() );
                fw.flush();
                fw.close();
            }
            if ( strength_min < 5.0 ) {
                strength_min = strength_min + 0.5;
            }
            else {
                noBreak = false;
            }
        }

       
        } // end of loop for all MODES ...


        

    };

    public static void createHistogramm( Messreihe mr ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, 100, 0, 10 );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };



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

    // hier kommen die Zeilen an und werden in die Messreihen bzw.
    // Netzwerkdarstellungen überführt.
    static int counter = 0;
    static public NodePair parseLine(String line, int mj, String lang) {
        NodePair np = null;
        CCResults r = results.get( getKey(lang, mj));
        counter++;
        if ( counter % 10000 == 0 ) System.out.print(".");

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

           if ( r != null ) r.addPaar(np);
        }
        return np;
    }

    static public String getKey(String lang, int mode) {
        return lang + "_" + modes[mode];
    }


}
