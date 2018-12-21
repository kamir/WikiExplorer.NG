/*
 *  Einlesen der CC-Results und Anzeige von statisischen Daten.
 *
 */

package experiments.crosscorrelation;

import org.apache.hadoopts.chart.simple.MyXYPlot;
import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import research.wikinetworks.NodePair;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;
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
public class CCResultAnalyerPairOfResults {
  

//    static String pfadEDITS = "G:/PHYSICS/PHASE2/data/out/node_groups/cc_edits/";
//    static public String LABEL = "EDITS";

    static String[] lang = { "-1", "52",  "60" , "62", "72", "197" };
    static String[] anz = { "100" };

    

    /**
     * Wenn mehrere verglichen werden sollen dann sind hier die "SUBPFADE"
     * zu erfassen.
     *
     */
//    static String[] modes = { "T1_cut150", "T2_cut150" };

//    static String[] modes = {
//        "allGroups_Supr_300days_Shuffling",
//        //"allGroups_Supr_300days_noShuffling",
//        "allGroups_noSupr_300days_Shuffling",
//        //"allGroups_noSupr_300days_noShuffling"
//    };

    static String[] modes = { "T1_SUPR", "T2_SUPR" };
    /*
     * Hauptpfad zu den Eregebnissen
     */
//    static public String pfad = "G:/PHYSICS/RESULTS/SO 16.10.2011/";

//    static public String pfad = "G:/PHYSICS/RESULTS/MO 17.10.2011/";
    static public String pfad = "G:/PHYSICS/RESULTS/DI 18.10.2011/";

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
        int max = lang.length;

        for( int j = 0; j < 2; j++ ) {
            for ( int i = 0; i < max ; i++ ) {

                String ngfn = lang[i] +"_"+nrOfNodes+EditActivityFilter.extension;
                ng = new NodeGroup( ngfn );

                File f = new File( pfad + modes[j] + extension + ngfn +".cc.log" );
                if (!f.exists() ) System.exit(0);

                CCResults ccr = new CCResults();
                ccr.lang = lang[i];
                ccr.nrOfNodes = nrOfNodes;
                String key = getKey( lang[i] , j );

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
                        parseLine( line, j, lang[i] );
                    }
                    // if ( c > 100 ) noBreake = false;
                }
                
            }
        }


        TimeSeriesObject[] r = new TimeSeriesObject[lang.length];
        TimeSeriesObject[] tau = new TimeSeriesObject[lang.length];
        
        CCResults[][] ccr = new CCResults[lang.length][2];

        for ( int i = 0; i < max ; i++ ) {
            r[i] = new TimeSeriesObject();
            r[i].setLabel( "lang=" + lang[i] );

            tau[i] = new TimeSeriesObject();
            tau[i].setLabel( "lang=" + lang[i] );

            for( int j = 0; j < 2; j++ ) {
                String key = getKey( lang[i] ,j );
                ccr[i][j] = results.get(key);
            }
        }

        Vector<NodePair> goodOnes = new Vector<NodePair>();

        StringBuffer sb = new StringBuffer();

        
        for ( int i = 0; i < max ; i++ ) {
            System.out.println( ccr[i][0].lang + " " + ccr[i][1].lang );
            System.out.println( ccr[i][0].paare.size() + " " + ccr[i][1].paare.size() );

            int z = 0;
            for ( NodePair p1 : ccr[i][0].paare ) {
                NodePair p2 = ccr[i][1].paare.elementAt(z);
//                System.out.print( p1.pageIDA+"_"+p1.pageIDB + " :: " );
//                System.out.println( p2.pageIDA+"_"+p2.pageIDB );

                double s1 = p1.signLevel;
                double s2 = p2.signLevel;

                double t1 = p1.k;
                double t2 = p2.k;

                if ( p1.maxY > 0.5 || p2.maxY > 0.5 ) {
                    r[i].addValuePair(s1,s2);
                    tau[i].addValuePair(t1,t2);
                    //goodOnes.add(p2);
                    //sb.append( p1.pageIDA + "\t" + p1.pageIDB +"\n");
                }
                z++;
            }
        }

//        NodeGroup gp = new NodeGroup(goodOnes);
//        gp.fn = "goocPairs_cc_gt_08.ng";
//        gp.store();

        MyXYPlot.xRangDEFAULT_MIN = -15 ;
        MyXYPlot.yRangDEFAULT_MAX = 10 ;
        MyXYPlot.yRangDEFAULT_MIN = 0 ;
        MyXYPlot.xRangDEFAULT_MAX = 15 ;

        MyXYPlot.rowONEDefualtColor = Color.BLACK;

        for( TimeSeriesObject m: r) {
            SimpleRegression linFit = m.linFit(0.0, 10.0);
            System.out.println( m.getLabel()+"\n\tR=" + linFit.getR() );
            System.out.println( "\tm=" + linFit.getSlope() );
            System.out.println( "\tn=" + linFit.getIntercept() );
        }

        MyXYPlot.open(  r, "strength T1 vs. strength T2", "strength T1", "strength T2", true);

        MyXYPlot.xRangDEFAULT_MIN = -15 ;
        MyXYPlot.yRangDEFAULT_MAX = 15 ;
        MyXYPlot.yRangDEFAULT_MIN = -15 ;
        MyXYPlot.xRangDEFAULT_MAX = 15 ;

        MyXYPlot.open(  tau, "tau T1 vs. tau T2", "tau T1", "tau T2", true);

//        FileWriter fw = new FileWriter( gp.pfad + "goodPairs_cc_gt_08.dat" );
//        fw.write( sb.toString() );
//        fw.flush();
//        fw.close();
        

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

    // hier kommen die Zeilen an und werden in die TimeSeriesObjectn bzw.
    // Netzwerkdarstellungen überführt.
    static int counter = 0;
    static public NodePair parseLine(String line, int j, String lang) {
        NodePair np = null;
        CCResults r = results.get( getKey(lang, j));
        counter++;
        if ( counter % 10000 == 0 ) System.out.print(".");

        StringTokenizer tok = new StringTokenizer(line);
        int i = tok.countTokens();
        if ( i < 5 ) {
            // System.out.println( i );
        }
        else {
           String pair = tok.nextToken();
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
