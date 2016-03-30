package research.wikinetworks;

import org.apache.hadoopts.data.series.Messreihe;
import java.util.Date;
import java.util.Vector;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;
import experiments.crosscorrelation.DailyCrossCorrelationMapper;
import experiments.crosscorrelation.KreuzKorrelation;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 * @author kamir
 */
public class NodePair {

    public double[] advancedResults = null;
    public int pageIDA = 0;
    public int pageIDB = 0;

    public String dateOfCreation = "";

    public NodePair() { };
    public NodePair(String pair, NodeGroup ng) {
        String a = pair.substring( 1, pair.length()-1 );
        String[] ids = a.split(",");
        id_A = Integer.parseInt( ids[0] );
        id_B = Integer.parseInt( ids[1] );
        pageIDA = ng.ids[id_A];
        pageIDB = ng.ids[id_B];
    };
    
        public NodePair(int id1, int id2, NodeGroup ng) {
        id_A = getIndexForPageID(id1 , ng);
        id_B = getIndexForPageID(id2 , ng);
        pageIDA = id1;
        pageIDB = id2;
    };

    public static Vector<NodePair> wrongPairsE = new Vector<NodePair>();
    public static Vector<NodePair> wrongPairsA = new Vector<NodePair>();

    public int id_A;
    public int id_B;

    public void setCCResults(String _k, String _stdDev, String _maxY, String _signLevel) {
        k = Double.parseDouble(_k);
        stdDev = Double.parseDouble(_stdDev );
        maxY = Double.parseDouble(_maxY);
        signLevel = Double.parseDouble(_signLevel);
    }

    public double k =0;
    public double stdDev = 0.0;
    public double maxY = 0.0;
    public double xForMaxY = 0.0;
    public double signLevel = 0.0;

    public Vector getDataRow() {
        Vector<String> row = new Vector<String>();
        row.add( id_A + " : " + id_B);
        row.add( k+"" );
        row.add( stdDev + "" );
        row.add(maxY +"");
        row.add(signLevel+"");
        return row;
    };

    static public boolean debug = false;

    public KreuzKorrelation kr = null;

    /**
     * 
     * Legt Resultate in den Variablen und im StringBuferr ab.
     *
     * @param ng
     * @param histMaxY
     * @param histSigLevel
     * 
     * @return
     * 
     */
    public boolean _calcCrossCorrelation(NodeGroup ng, Vector<Messreihe> input, Messreihe histMaxY, Messreihe histSigLevel, Vector<NodePair> wrong) throws Exception {
        
        boolean back = true;
        int delay = 0;

        if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED ) {

            Messreihe a = input.elementAt(id_A);
            Messreihe b = input.elementAt(id_B);

            KreuzKorrelation._defaultK = 0;
            if ( debug) System.out.println("Monat: " + (NodeGroup.splitIndex+1) );

            //double value = DailyCrossCorrelationMapper.calcLinkStrength(a, b);
            double[] value = DailyCrossCorrelationMapper.calcLinkStrength2(a, b);

            this.signLevel = value[0];

            this.advancedResults = value;

            kr = new KreuzKorrelation();

            kr.addValuePair( delay , value[0] );

            return initKRResults(wrong, histMaxY, histSigLevel );
        }
        else {
            if ( debug)  System.out.println("{ ID_A : ID_B +++ } " + id_A + " : " + id_B);
            
            boolean bb = false; 
            Messreihe a = null;
            Messreihe b = null;
            
            try{
                a = input.elementAt(id_A).limitTo( 300 );
                b = input.elementAt(id_B).limitTo( 300 );
           
            // System.out.println( "### *** ###" );
            
            kr = KreuzKorrelation.calcKR(a, b, KreuzKorrelation.debug, false);
            
            // kr = KreuzKorrelation.calcKR(a, b, true, false);
            // System.out.println( "*** ### ***" );
            
            bb = initKRResults(wrong, histMaxY, histSigLevel);
            
            
            
                        
         }
         catch(Exception ex) { 
                ex.printStackTrace();
            } 
            return  bb;
        }
    }

    public boolean initKRResults( Vector<NodePair> wrong, Messreihe histMaxY, Messreihe histSigLevel ) throws Exception {
        
        boolean back = false;

        stdDev = 0.0;
        maxY = 0.0;
        xForMaxY = 0.0;
        signLevel = 0.0;

        if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED ) {
            back = true;
            stdDev = 0.0;            
            xForMaxY = 0.0;

            signLevel = CheckInfluenceOfSingelPeaks.calcStrength(kr);

            maxY = signLevel;

            if ( histSigLevel != null ) histSigLevel.addValue( signLevel );
            if ( signLevel > 1.0 || signLevel < -1.0 ) {
                System.err.println( this.toString() + " ===> " + signLevel );
                throw new Exception( "CC-ERROR : " + this.toString() + " => " + signLevel );
            }
            if ( signLevel > 0.9 || signLevel < -0.9 ) System.err.println( this.toString() +  "{ initKRResults()  } => " + signLevel );
        }

        if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_CC_TAU_0 ) {
            back = true;
            stdDev = 0.0;            
            xForMaxY = 0.0;

            signLevel = CheckInfluenceOfSingelPeaks.calcStrength(kr);

            maxY = signLevel;

            if ( histSigLevel != null ) histSigLevel.addValue( signLevel );
            if ( signLevel > 1.0 || signLevel < -1.0 ) System.err.println( this.toString() + " => " + signLevel );
        }
        
        boolean noError = true;
        if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_NORMALIZED) {
            if (new Double(stdDev).isNaN()) {
                noError = false;
            }
            else {
                back = true;
                try {
                    stdDev = kr.getStddev();
                    maxY = kr.getMaxY();
                
                    xForMaxY = kr.getX_for_Y2( maxY, 0.01 );

                    signLevel = CheckInfluenceOfSingelPeaks.calcStrength(kr);

                    /*
                    * nicht immer ein sinnvoller Wert => nur in Modus 1
                    */
                    if ( histMaxY != null ) histMaxY.addValue( xForMaxY );

                    if ( histSigLevel != null ) histSigLevel.addValue( signLevel );
                }
                catch(Exception ex){
                    // ex.printStackTrace();
                    noError = false;
                };
                
                

            }
            
            if ( noError ) { 
            
            } 
            else { 
                stdDev = 0;
                maxY = 0;
                xForMaxY = -5;
                signLevel = -5;
                if ( wrong != null ) wrong.add( this );
                back = false;
            }
            
            
        }


        return back;
    }

    public String toString() {
        
        return "("+pageIDA+","+pageIDB+")";
    };
    
    private static String s = "";

    public String getResultOfCC() {
        Date d = new Date( System.currentTimeMillis() );
        String s = ""; // ew StringBuffer();
           s = s.concat(xForMaxY + "\t");
           s = s.concat(stdDev + "\t" + maxY + "\t");
           s = s.concat(signLevel+"\t");
           s = s.concat(d+"");
        return s;
    };

    public String appendAdvancdeResults(String ccLine) {
        if ( advancedResults != null ) {
            for ( double v : advancedResults ) {
                ccLine = ccLine.concat( "\t" + v );
            }
            return ccLine;
        }
        else {
            return ccLine;
        }
    }
    @Override
    public boolean equals(Object obj) {
        int has1 = obj.hashCode();
        if ( has1 == hashCode() ) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        String key = "";
        if ( pageIDA > pageIDB ) key = pageIDA+"_"+pageIDB;
        else key = pageIDB+"_"+pageIDA;
        return key.hashCode();
    }

    public String getHashKey() {
        return pageIDA +"_" + pageIDB;
    };

    public static void main( String[] args ) {

        Vector<NodePair> vAll = new Vector<NodePair>();

        NodeGroup ng = new NodeGroup();
        ng.load();
        System.out.println(">>> " + ng.fn + " is loaded ... ");

        NodePair p1 = new NodePair();
        p1.pageIDA = 1;
        p1.pageIDB = 473539;

        NodePair p2 = new NodePair();
        p2.pageIDA = 2;
        p2.pageIDB = 1;
        
        NodePair p3 = new NodePair();
        p3.pageIDA = 505040;
        p3.pageIDB = 473539;
        
        NodePair p4 = new NodePair();
        p4.pageIDA = 473539;
        p4.pageIDB = 505040;
        
        NodePair p5 = new NodePair();
        p5.pageIDA = 164280;
        p5.pageIDB = 2;

        vAll.add(p1);
        vAll.add(p2);
        vAll.add(p3);
        vAll.add(p4);
        vAll.add(p5);

        Vector<NodePair> vFilteres = new Vector<NodePair>();

        for( NodePair np : vAll ) {
            if ( vFilteres.contains(np) ) System.out.println( np.toString() + " is in the collection.");
            else {
                vFilteres.add( np );
            }
        }
        System.out.println( "\n" + vFilteres.size() + " link(s) in filtered collection ...");
        System.out.println( "\n" );

        for( NodePair n : vAll ) {
            System.out.println( n.toString() + " is full in NodeGroup: " + ng.fn + " => " + ng.belongsWithBothNodesToGroup(n) );
        }
        System.out.println( "\n" );

        for( NodePair n : vAll ) {
            System.out.println( n.toString() + " is half in NodeGroup: " + ng.fn + " => " + ng.belongsWithOneNodeToGroup(n) );
        }
    }

    public static javax.swing.filechooser.FileFilter getFileFilter() {
        return (javax.swing.filechooser.FileFilter) new NodePairFileFilter();
    }

    public boolean isSelfLinked() {
        boolean back = false;
        if ( this.pageIDA == this.pageIDB ) back = true;
        return back;
    }

    private int getIndexForPageID(int id1 , NodeGroup ng) {
        int x = 0;
        for( int i : ng.ids ) { 
            if ( id1 == i ) return x;
            x++;
        }
        return -1;
    }

}
