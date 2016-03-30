/**
 *
 * Node Groups sind die Ergebnisse der Knoten-Filterungen.
 *
 * Es gibt vollständige und partielle NodeGroups
 *
 * - vollständig: aus einer Liste werden alle Paare später verarbeitet
 *
 * - partiell: aus einer PaarListe werden nur die darin verwendeten Paare
 *             zur Berechnung im NetworComparator benutzt.
 *
 **/

package com.cloudera.wikiexplorer.ng.util;

import org.apache.hadoopts.chart.simple.MultiChart;
import org.apache.hadoopts.chart.statistic.HistogramChart;
import org.apache.hadoopts.data.series.Messreihe;
import extraction.TimeSeriesFactory;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import org.jfree.ui.RefineryUtilities;
import research.wikinetworks.NodePairList;
import research.wikinetworks.NodePair;
import research.wikinetworks.PageLanguageChecker;
import experiments.linkstrength.CheckInfluenceOfSingelPeaks;

/**
 *
 * @author kamir
 */
public class NodeGroup {
    
    public static boolean useStockData = true;

    public static boolean supressFirstPart = false;
    public static boolean supressLastPart = false;
    public static boolean doShuffle = false;

    public static int offsetAtEnd = 150;
    public static int offsetAtStart = 150;

    private static FileFilter getFileFilter() {
        return (javax.swing.filechooser.FileFilter)new NodeGroupFileFilter();
    }

    public static String getStateExtension() {
        String ccmode = ""+CheckInfluenceOfSingelPeaks.mode;
        String b = "NOTblocked";
        String s = "NOTshuffeled";
        String f = "NOTfiltered";
        String split = "" + splitIndex;

        if ( NodeGroup.doShuffle ) s="shuffeled";
        if ( TimeSeriesFactory.doBlock ) b="blocked";
        if ( TimeSeriesFactory.doFilter ) f="filtered";
        String state_ext = "_"+ccmode +"_"+s+"_"+b+"_"+f+"_split_" + split;
        
        return state_ext;
    }

    // alle Paarungen oder nur Teile davon nutzen ?
    public boolean isPartial = false;
    public boolean isFull = true;

    public void setFull() {
        isFull = true;
        isPartial = false;
    }
    public void setPartial() {
        isFull = false;
        isPartial = true;
    }


    public int[] ids = null;

    public Object[] idsO = null;

    public int[] accessActivity = null;
    public int[] accessMAX = null;
    
    public int[] editActivity = null;

    public String tabname;

    public NodeGroup(NodePairList liste) {
        this( liste.getPairs() );
        this.fn = liste.getFn();
    }

    public String getTabname() {
        if ( tabname == null) tabname = fn;
        tabname = tabname.replace("-", "_");
        tabname = tabname.replace(".", "_");
        return tabname;
    }

    public void setTabname(String tabname) {
        this.tabname = tabname;
    }

    boolean[][] _used = null;

    Vector<NodePair> paare = null;
    //public HashSet<String> puffer2 = null;
    HashMap<Integer,Integer> map = null;

    /**
     * Aus einer PaarListe wird die partielle NodeGroup erzeugt.
     *
     * @param v
     */
    public NodeGroup(Vector<NodePair> v) {
        initNodePairs(v);
    }

    HashMap<NodePair,NodePair> puffer2 = new HashMap<NodePair,NodePair>();

    public void initNodePairs(Vector<NodePair> v) {

        this.isPartial = true;
        this.isFull = false;
        paare = v;

        // Dedublizierung ...
        map = new HashMap();
        for( NodePair p : v ) {

            if ( !map.containsKey( p.pageIDA ) ) map.put( p.pageIDA, p.pageIDA );
            if ( !map.containsKey( p.pageIDB ) ) map.put( p.pageIDB, p.pageIDB );

//            String s = p.getHashKey();
//            System.out.println( p );

            if ( !puffer2.containsKey( p ) ) puffer2.put( p, p );
        }

        // HIER PASST DIE ZUORDNUNG ZW PageID und id NICHT !!!

        System.out.println("PUFFER2=" + puffer2.size() );

        // einlagern in der ID Liste ...
        ids = new int[map.size()];
        int c = 0;
        Set s = map.keySet();
        Iterator it = s.iterator();
        while( it.hasNext() ) {
            Integer val = (Integer) it.next();
            ids[c] = (int)val;
            c++;
        }
        System.out.println("IDS=" + c );
    }

    /**
     * Bei einer PARTIELLEN LISTE werden nicht alle Paare betrachtet.
     * 
     * @param np
     * @return
     */
    public boolean doWorkWithPair( NodePair np ) {
//        System.out.println(np.id_A + "\t" + np.id_B );
        return true;
//        return _used[np.id_A][np.id_B];
    }

    // das Array aller NodeIds
    public Object[] getIdsArray() {
        int c = 0;
        Object[] d = new Object[ids.length];
        for( int i: ids) {
            d[c] = i;
            c++;
        }
        return d;
    };


    // Default Speicherort
    public static String pfad = "G:/PHYSICS/PHASE2/data/out/node_groups/";

    // die drei im Paper erwähnten
    public String fn = "sample_nodes.dat";

    // Sample Nodes for our Phys-A paper ...
    public NodeGroup() {  //BERLIN, Illuminati , DPG, , 
        int[] tempIDs = { 217128 }; //, 164280, 291044, 473539, 505040 };  // , ,  164280  };
        ids = tempIDs;
    };
  
    /**
     * string = NAME der Datei relativ zu "pfad" ...4
     * @param string
     */
    public NodeGroup(String string, NodeGroup org ) {
        
        // lese die Indizes                 
        fn = string;
        _langID = null;
        _langID = getLangID();
        load();
        
        // nun stehen in ids die indizes ...
        ids = mapIndizesToIds( ids , org );
        
    }
    
 
    
    /**
     * string = NAME der Datei relativ zu "pfad" ...4
     * @param string
     */
    public NodeGroup(String string) {
        fn = string;
        _langID = null;
        _langID = getLangID();
        load();
    }
    
 
    /**
     * 
     * @param f
     */
    public NodeGroup(File f) {
        fn = f.getName();
        pfad = f.getParent() + File.separator;
        _langID = null;
        _langID = getLangID();
        load();
    }
    
    public NodeGroup(File f, int lid) {
        fn = f.getName();
        pfad = f.getParent() + File.separator;
        _langID = lid+"";
        load();
    }

    Vector<Integer> v = null;
    public Vector<Integer> getIdsAsVector() {
        if ( v == null ) {
            v = new Vector<Integer>();
            for( int i : ids ) {
                v.add(i);
            }
        }
        return v;
    }

    /**
     *
     * @param i
     * @param time_scale
     * @return
     * @throws Exception
     */
    public Messreihe loadEditsForOneID(int i, int time_scale) throws Exception {
        // liste laden ...
        Vector<Long> data = loadEditeTimesForID(i);
        Messreihe mr = TimeSeriesFactory.prepareEditDataSTUNDE(data, 300, time_scale);
        mr.setLabel( "["+getLangID()+"] "+ i + "_" + mr.getLabel());
        if (debug) {
            System.out.println(i + "\t " + mr.getLabel() + "\t" + mr.get_Y_StatisticsData_Line());
        }
        return mr;
    }

    /**
     *
     * @throws Exception
     */
    public String comment = "";
    public void store() throws Exception {
        File f = new File( pfad );
        if ( !f.exists() ) f.mkdirs();
        
        System.out.println( pfad + fn);

        FileWriter fw = new FileWriter( pfad + fn );
        fw.write( "# wikipedia Node-IDs ["+ fn +"]\n# " + comment + " \n#\n");
        for( int i : ids ) {
          fw.write( i + "\n" );
        }
        fw.flush();
        fw.close();
    };

    public static int _limitIds = 100;
    /**
     * Laden der Liste
     */
    boolean notLoaded = true;
    
    public void load() {
        if ( !notLoaded ) return;
        System.out.print(">>> LOAD ... " );
        try {
            Vector<Integer> v = loadIdsFromFile(fn);
            System.out.println("> " + v.size() );
            int idsT[] = new int[ v.size() ];
            int c = 0;
            for( int i : v ) {
                idsT[c] = i;
                c++;
            }
            ids = idsT; // alle in der Datei

            // NUN SIND NUR DIE IDS geladen !!!

            if( doLimitIds ) {
                if ( ids.length > limitOfNodesToProcess ) {

                    int d[] = new int[limitOfNodesToProcess];
                    for ( int cc =0; cc < limitOfNodesToProcess ;cc++ ) {
                        d[cc] = ids[cc];
                    }
                    ids = d; // limitierte Anzahl
                }
            }
            
            notLoaded = false;

            // doUse ARRAY ist noch leer !!!
        }
        catch (Exception ex) {

            Logger.getLogger(NodeGroup.class.getName()).log(Level.SEVERE, null, ex);
            ids = null;
            notLoaded = true;
        }
        System.out.println( "> ids in memory ... ");
    }

    int nrOfPairsToCalcCC = 0;

    boolean accessModeInitialized = false;
    public void _initWhatToUse(){

        if ( accessModeInitialized ) return;

        System.out.println(">>> init NodeGroup fields ... " + ids.length);
        this.accessActivity = new int[ ids.length ];
        this.editActivity = new int[ ids.length ];
               
        this.accessMAX = new int[ ids.length ];

        this.editActivity = new int[ ids.length ];

        _used = new boolean[ids.length][ids.length];

        int doUse = 0;

        if ( this.isFull && !this.isPartial ) {

            // HASHPUFFER füllen ...
            int max = ids.length;
            for ( int i=0; i < max ; i++ ) {
                for ( int j=0; j < max ; j++ ) {
                    NodePair np = new NodePair();
                    np.pageIDA = ids[i];
                    np.pageIDB = ids[j];
                    np.id_A=i;
                    np.id_B=j;

                    NodePair old = new NodePair();
                    old.pageIDA = ids[j];
                    old.pageIDB = ids[i];
                    old.id_A=j;
                    old.id_B=i;

                    //System.out.print( np.toString() + " " + (np.equals(old)) );

                    if (  !(i==j) ) {
                        if ( _used[j][i] ) {
    //                        System.out.println("-");
                        }
                        else {
                            _used[i][j] = true;
                            doUse++;
    //                        System.out.println("+");
                        }
                    }
                    else {
                        //System.out.println("-");

                    }
                }
            }
        }
        else {  // PARTIELLE NUTZUNG ...
            // HASHPUFFER füllen ...
            int max = ids.length;
            for ( int i=0; i < max ; i++ ) {
                for ( int j=0; j < max ; j++ ) {
                    NodePair np = new NodePair();

                    np.pageIDA = ids[i];
                    np.pageIDB = ids[j];
                    np.id_A=i;
                    np.id_B=j;

                    if ( this.puffer2.containsKey( np ) ) {
                        if( !np.isSelfLinked() && !(_used[j][i]) ) {
                            // System.out.println( " #####>>> "  + np.getHashKey() );
                            _used[i][j] = true;
                            doUse++;
                        }
                    }
                    else {
                        _used[i][j] = false;
                    }
                }
            }

        }
        this.nrOfPairsToCalcCC = doUse;
        System.out.println(">>> Selected what Nodes to use ...   (" + doUse + ") pairs.");

        // checkAccessTimeSeries();
        
        accessModeInitialized = true;
        notLoaded = false;
    };

    // in der ersten Spalte steht die ID und # sind Kommentare ...
    public Vector<Integer> loadIdsFromFile( String filename ) throws Exception {
        Vector<Integer> ids = new Vector<Integer>();
        File f = new File( pfad + filename );
        
        System.out.println("*** " + f.getAbsolutePath() + " " + f.exists() );
         
        FileReader fr = new FileReader( pfad + filename);

        BufferedReader br = new BufferedReader(fr);
        int counter = 0;
        int id = 0;
        while (br.ready()) {

            String line = br.readLine();

            if (!(line.startsWith("#"))) {
                //System.out.println(line);
                StringTokenizer st = new StringTokenizer(line);
                counter++;
                id = Integer.parseInt((String) st.nextElement());
                ids.add(id);
            }
        }
        br.close();
        return ids;
    }

    public static void createHistogramm( Messreihe mr ) {
        HistogramChart demo = new HistogramChart( mr.getLabel()  );
        demo.addSerieWithBinning( mr, 100, 0, 200 );
        demo.setContentPane( demo.createChartPanel() );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    };

    public static void main( String[] args ) throws Exception {
        stdlib.StdRandom.initRandomGen(0);

//        NodeGroup ng = new NodeGroup();
//        ng.store();

        System.out.println( ">>> START" );

//        System.out.println( "-----------------------------------------------------" );
//        System.out.println( ">>> VphMean32bit ..." );
//        VphMean32bit g2 = new VphMean32bit();
//        g2.load();
//        System.out.println( g2.ids.length + " nodes in file: " + g2.fn );
//        System.out.println( ">>> nr of edits pairs  : " + g2.getNrOfPairsE());
//        System.out.println( ">>> nr of access pairs : " + g2.getNrOfPairsA());
//        System.out.println( "-----------------------------------------------------" );

        TimeLog tl = new TimeLog();
//        tl.setStamp("Beginne den Test [AllLinked32bit] ... ");
//        System.out.println( "-----------------------------------------------------" );
//        System.out.println( ">>> AllLinked32bit ..." );
//        AllLinked32bit g3 = new AllLinked32bit();
//        g3.setPartial();
//
//        g3._initWhatToUse();
//        tl.setStamp(">>> starte laden der Reihen ... ");
//        g3.checkAccessTimeSeries();
//        tl.setStamp(">>> ENDE laden der Reihen ... ");
//        System.out.println( "\n"+g3.countSelfConnections() + " self-links in file: " + g3.fn );
//
//        System.out.println( g3.ids.length + " not doubled node ids in file: " + g3.fn );
//        System.out.println( ">>> nr of edits pairs  : " + g3.getNrOfPairsE());
//        System.out.println( ">>> nr of access pairs : " + g3.getNrOfPairsA());
//        System.out.println( tl.toString() );
//        System.out.println( "-----------------------------------------------------" );
//




//                tl.setStamp("Beginne den Test [AllLinked32bit] ... ");
//        System.out.println( "-----------------------------------------------------" );
//        System.out.println( ">>> AllLinked32bit ..." );
//        AllLinked32bit g4 = new AllLinked32bit();
//        g4.setFull();


//        g4._initWhatToUse();
//        tl.setStamp(">>> starte laden der Reihen ... ");
//        g4.checkAccessTimeSeries();
//        tl.setStamp(">>> ENDE laden der Reihen ... ");
//        System.out.println( "\n"+g4.countSelfConnections() + " self-links in file: " + g4.fn );
//
//        System.out.println( g4.ids.length + " not doubled node ids in file: " + g4.fn );
//        System.out.println( ">>> nr of edits pairs  : " + g4.getNrOfPairsE());
//        System.out.println( ">>> nr of access pairs : " + g4.getNrOfPairsA());
//        System.out.println( tl.toString() );
//        System.out.println( "-----------------------------------------------------" );
//
//
//
//
//




//        System.out.println( "\n-----------------------------------------------------" );
//        System.out.println( ">>> [-1_10_TEST_FULL.dat] ... " );
//        NodeGroup fullGroup = new NodeGroup("-1_10_TEST_FULL.dat");
//        fullGroup._initWhatToUse();
//
//        fullGroup.checkAccessTimeSeries();
//        fullGroup.checkEditTimeSeries();
//
//        System.out.println( fullGroup.ids.length + " nodes in file: " + fullGroup.fn );
//        System.out.println( ">>> nr of edits pairs  : " + fullGroup.getNrOfPairsE());
//        System.out.println( ">>> nr of access pairs : " + fullGroup.getNrOfPairsA());
//        System.out.println( "-----------------------------------------------------" );



        System.out.println( "\n-----------------------------------------------------" );
        System.out.println( ">>> [-1_10_TEST_PART.dat] ... " );
        NodePairList liste = new NodePairList();
        liste.read( new File( NodeGroup.pfad + "-1_10_TEST_PART.dat") );
        //NodeGroup partialGroup = new NodeGroup( liste.getPairs() );
        NodeGroup partialGroup = null;


        /**
         * test the access of the splits ...
         */
        NodeGroup.doSplitRows = true;
        NodeGroup.maxSplitIndex = 10;

        if ( NodeGroup.doSplitRows ) {
            for ( int i = 0; i < NodeGroup.maxSplitIndex ; i++ ) {

                NodeGroup.splitIndex = i;


                if ( i==0 ) {
                    NodeGroup.useBuffer = false;
                    partialGroup = new NodeGroup( liste );
                    partialGroup._initWhatToUse();
                }
                else {
                    NodeGroup.useBuffer = true;
                }

                partialGroup.checkAccessTimeSeries();
                MultiChart.open( partialGroup.getAaccessReihen() , NodeGroup.splitIndex+"","t","clicks",true);

                
            }
        }
        partialGroup.checkEditTimeSeries();


        System.out.println( partialGroup.ids.length + " nodes in file: " + partialGroup.fn );
        System.out.println( ">>> nr of edits pairs  : " + partialGroup.getNrOfPairsE() );
        System.out.println( ">>> nr of access pairs : " + partialGroup.getNrOfPairsA() + "\n" );
        System.out.println( "-----------------------------------------------------" );
//
//
//        System.out.println( "\n-----------------------------------------------------" );
//        System.out.println( ">>> [-1_10_TEST_PART_MIT_DUBLETTEN.dat] ... " );
//        NodePairList liste2 = new NodePairList();
//        liste2.read( new File( NodeGroup.pfad + "-1_10_TEST_PART_MIT_DUBLETTEN.dat") );
//        //NodeGroup partialGroup = new NodeGroup( liste.getPairs() );
//        NodeGroup partialGroup2 = new NodeGroup( liste2 );
//        partialGroup2._initWhatToUse();
//
//        partialGroup2.checkAccessTimeSeries();
//        partialGroup2.checkEditTimeSeries();
//
//        System.out.println( partialGroup2.ids.length + " nodes in file: " + partialGroup2.fn );
//        System.out.println( ">>> nr of edits pairs  : " + partialGroup2.getNrOfPairsE() );
//        System.out.println( ">>> nr of access pairs : " + partialGroup2.getNrOfPairsA() + "\n" );
//        System.out.println( "-----------------------------------------------------" );


    };

    public static File selectNodegroupFile() {
        javax.swing.JFileChooser jfc = new javax.swing.JFileChooser( new File( pfad ) );
        jfc.setFileFilter( NodeGroup.getFileFilter() );
        
        jfc.setDialogTitle("Select a prefiltered NodeGroup ...");
        jfc.setSize( 800,600 );
        
        jfc.showOpenDialog(null);

        File f = jfc.getSelectedFile();
        if ( f == null ) {
            System.err.println(">>> KEINE NodeGroup gewählt !");
        }
        else {
            System.err.println(">>> NodeGroup : " + f.getAbsolutePath() + " gewählt !");
        }
        return f;
    };

    /**
     * Einlesen aller ACCESS TS und prüfen auf Verfügbarkeit.
     *
     * Voreingestellte Filter werden benutzt!
     *
     * @return - true, falls alle Reihen da sind.
     */
    static public boolean useBuffer = false;
    public Vector<Messreihe> hourlyAccessData = null;
    
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
                Messreihe mr = TimeSeriesFactory.prepareAccessDataSTUNDE(i, length );

          
                    if ( CheckInfluenceOfSingelPeaks.mode == CheckInfluenceOfSingelPeaks.mode_ADVANCED ) {
                        Messreihe orig = mr.copy();
                        if ( doShuffle ) orig.shuffleYValues();
                        hourlyAccessData.add(orig);
                    }

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
                //System.err.println( ex.getMessage() );
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

    public Vector<Messreihe> editReihen = new Vector<Messreihe>();
    public Vector<Messreihe> accessReihen = new Vector<Messreihe>();
    public Vector<Messreihe> stockDataReihen = new Vector<Messreihe>();
    
    public Vector<Messreihe> shortStockDataReihen = new Vector<Messreihe>();

    static public boolean debug = false;

    static public int time_scale = 24; // 24=Tag, 1=Stunde

    public boolean checkEditTimeSeries() {
        
        
        int length = 299;

        StringBuffer sb = new StringBuffer(">>> EDIT Error-Ids: " );

        boolean b = true;

        int errors = 0;
        for( int i : ids ) {
            try {
                Messreihe mr = loadEditsForOneID( i , time_scale);

                this.editActivity[ i ] = (int)mr.summeY();
                
                if ( TimeSeriesFactory.doBlock ) mr = TimeSeriesFactory.blockSpecialValues(mr);
                if ( doShuffle ) mr.shuffleYValues();

                if ( supressFirstPart ) mr.supressAtStart( offsetAtStart , 0);
                if ( supressLastPart ) mr.supressAtEnd( offsetAtEnd , 0);
                

                editReihen.add(mr);
            }
            catch (Exception ex) {
                sb.append( i + " " );
                errors++;
                b = false;
            };
        }

        if ( errors > 0 ) {
            System.err.println( sb.toString() );
            System.out.println( "[#errors = " + errors + "]");
        }
        if ( editReihen.size() < 2 ) {
            editReihen.add( Messreihe.getGaussianDistribution( 299*24 ));
            editReihen.add( Messreihe.getGaussianDistribution( 299*24 ));
        }
        return b;
    }

    private Vector<Long> loadEditeTimesForID(int id) throws Exception {
        Vector<Long> data = null;
        String fn = id +".revisions.dat";
        File f = new File( "G:/PHYSICS/PHASE2/data/out/edit_events/" + fn );
        if ( f.exists() ) data = loadEditsDatesFromFile( f );
        else throw new Exception( f.getAbsolutePath() + " not available !!!");
        if ( doShuffle ) Collections.shuffle(data);
        return data;
    }

    private Vector<Long> loadEditsDatesFromFile(File f) throws Exception {
        String line = null;
        Vector<Long> data = new Vector<Long>();
        BufferedReader br = new BufferedReader( new FileReader( f ));
        while( br.ready() ) {
            line = br.readLine();
            if ( line.startsWith("#")) {

            }
            else {
                StringTokenizer st = new StringTokenizer(line);
                String date = st.nextToken();
                Long d = Long.parseLong(date);
                data.add(d);
            }
        }
        br.close();
        return data;
    }


    public Messreihe loadAccessForOneID(Integer id) {
        Messreihe mr = null;
        try {
            mr = TimeSeriesFactory.prepareAccessDataSTUNDE(id, 299 * 24);
            mr = mr.setBinningX_sum( 24 );

            if ( doShuffle ) mr.shuffleYValues();

        }
        catch (Exception ex) {
            if ( debug ) Logger.getLogger(NodeGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return mr;
    }

    public String _langID = "-1";
    public String getLangID() {
        String l = "";

        if ( _langID == null ) {
            StringTokenizer st = new StringTokenizer( fn, "_");
            l = st.nextToken();
        }
        else l = _langID;
        
        return l;
    }

    // wir kürzen hier nicht die Access-Reihe, wir setzen die Werte nur auf 0;
    public void cutOffAtStart(int START_CUTTOF_LENGTH) {
        for( Messreihe mr : this.accessReihen ) {
            mr.supressAtStart(START_CUTTOF_LENGTH, 0.0);
        }
    }

    // wir kürzen hier nicht die Access-Reihe, wir setzen die Werte nur auf 0;
    public void cutOffAtEnd(int START_CUTTOF_LENGTH) {
        for( Messreihe mr : this.accessReihen ) {
            mr.supressAtEnd(START_CUTTOF_LENGTH, 0.0);
        }
    }


    /**
     * Konsistenzprüfungen
     */
    Vector<Integer> idsEinzeln = new Vector<Integer>();
    Vector<Integer> idsDoppelte = new Vector<Integer>();

    boolean allLangIDsOK = true;

    public void checkForDoubleIds() {
        for( int i : ids ) {
            if ( idsEinzeln.contains(i) )
                idsDoppelte.add(i);
            else
                idsEinzeln.add(i);

            

            // System.out.println( "Language ID: " + this.getLangID() );
            if ( Integer.parseInt( this.getLangID() ) != -1 ) { 
                  allLangIDsOK = allLangIDsOK && PageLanguageChecker.check(i, this.getLangID() );
            }
        }
        int d = idsDoppelte.size();
        
        for ( Integer i : idsDoppelte ) {
            // System.out.println( ">>> " + i );
        }
        if ( d > 0 ) System.err.println( ">>> in " + this.fn + " " + d +  " doppelte gefunden ...");
        else {
            System.out.println( ">>> in " + this.fn + " wurden keine doppelten Ids gefunden!");
        }
        if ( !allLangIDsOK ) System.err.println("WRONG LANGUNAGE !!! ");
        else System.out.println( ">>> all langIds OK: " + allLangIDsOK );
    }


    HashMap idsToLookup = null;
    HashMap getIdsAsHashMapToLookUp() {
        if ( idsToLookup == null ) {
            idsToLookup = new HashMap();
            for( int id : ids ) {
                idsToLookup.put(id, id);
            }
        }
        return idsToLookup;
    };

    public boolean belongsWithBothNodesToGroup( NodePair p ) {
        HashMap map = getIdsAsHashMapToLookUp();
        boolean bA = map.containsKey(p.pageIDA);
        boolean bB = map.containsKey(p.pageIDB);
        boolean b = bA && bB;
        return b;
    };
    
    public boolean belongsWithOneNodeToGroup( NodePair p ) {
//        boolean b = this.getIdsAsVector().contains( p.pageIDA );
//        b = b || this.getIdsAsVector().contains( p.pageIDB );
//        return b;
        HashMap map = getIdsAsHashMapToLookUp();
        boolean bA = map.containsKey(p.pageIDA);
        boolean bB = map.containsKey(p.pageIDB);
        boolean b = bA || bB;
        return b;
    };

    /**
     * b[0] =>  ||
     * b[1] =>  &&
     *
     * @param p
     * @return
     */
    public boolean[] belongsNodeToGroup( NodePair p ) {
        HashMap map = getIdsAsHashMapToLookUp();
        boolean bA = map.containsKey(p.pageIDA);
        boolean bB = map.containsKey(p.pageIDB);
        boolean[] b = new boolean[2];
        b[0] = bA || bB;
        b[1] = bA && bB;
        return b;
    };


    public String name = null;
    public String getName() {
        if ( name == null ) return "FN:"+fn ;
        else return name;
    }

    public int getNrOfPairsE() {
        int nr = 0;

        // doppelte zu unterdrücken ...
        Vector<String> keysOfPairs = new Vector<String>();
        int max = editReihen.size();
        for ( int i=0; i < max ; i++ ) {
            for ( int j=0; j < max ; j++ ) {
                String oldKey = j+"_"+i;
                if (  !(i==j) &&   // keine "Selbst-Referenz"
                      !(keysOfPairs.contains(oldKey)) )  // keine Dopplungen
                {
                    String realKey = i+"_"+j;
                    keysOfPairs.add(realKey);

                    System.out.println( realKey );

                    NodePair np = new NodePair();
                    np.id_A = i;
                    np.id_B = j;

                    if ( isFull ) nr++;
                    else if ( isPartial ) {
                        if ( doWorkWithPair(np) ) {
                            nr++;
                        }
                    }
                }
            }
        }
        return nr;
    }

    public int getNrOfPairsA() {
       int nr = 0;

        // doppelte zu unterdrücken ...
        HashSet<String> keysOfPairs = new HashSet<String>();
        int max = accessReihen.size();
        for ( int i=0; i < max ; i++ ) {
            for ( int j=0; j < max ; j++ ) {
                String oldKey = j+"_"+i;
                if (  !(i==j) &&   // keine "Selbst-Referenz"
                      !(keysOfPairs.contains(oldKey)) )  // keine Dopplungen
                {
                    String realKey = i+"_"+j;
                    keysOfPairs.add(realKey);

                    // System.out.println( realKey );

                    NodePair np = new NodePair();
                    np.id_A = i;
                    np.id_B = j;

                    if ( isFull ) nr++;
                    else if ( isPartial ) {
                        if ( doWorkWithPair(np) ) {
                            nr++;
                        }
                    }
                }
            }
        }
        return nr;
    }


    public int countSelfConnections() {
        int i = 0;
        for( NodePair np : paare ) {
            if ( np.pageIDA == np.pageIDB ) i++;
        }
        return i;
    }

    public static boolean doSplitRows = false;
    public static int splitIndex = 0;
    public static int splitLength = 30;
    public static int maxSplitIndex = 10;
    
    public Vector<Messreihe> getStockDataReihen() {
        if ( !doSplitRows ) return stockDataReihen;
        if ( CheckInfluenceOfSingelPeaks.mode_ADVANCED == CheckInfluenceOfSingelPeaks.mode ) return null;
        else return getNextSplit_DAY( stockDataReihen );
    }

    public Vector<Messreihe> getAaccessReihen() {
        if ( !doSplitRows ) return accessReihen;
        if ( CheckInfluenceOfSingelPeaks.mode_ADVANCED == CheckInfluenceOfSingelPeaks.mode ) return getNextSplit_HOUR();
        else return getNextSplit_DAY( accessReihen ); 
    }

    private Vector<Messreihe> getNextSplit_HOUR() {
        if ( debug ) System.out.println("--> next hour ... " + this.hourlyAccessData.elementAt(0).yValues.size() );
        Vector<Messreihe> split = new Vector<Messreihe>();

        Enumeration<Messreihe> en = this.hourlyAccessData.elements();

        int begin = splitIndex * splitLength * 24;
        int ende  = begin + splitLength * 24;

        while( en.hasMoreElements() ) {
            Messreihe original = en.nextElement();
            split.add( original.cutOut( begin, ende ) );
        }
        System.out.println(">>> SPLIT-SIZE=" + split.size() );
        
        return split;
    }


    private Vector<Messreihe> getNextSplit_DAY( Vector<Messreihe> row ) {
        Vector<Messreihe> split = new Vector<Messreihe>();

        Enumeration<Messreihe> en = row.elements();

        int begin = splitIndex * splitLength;
        int ende  = begin + splitLength;

        while( en.hasMoreElements() ) {
            Messreihe original = en.nextElement();
            split.add( original.cutOut( begin, ende ) );
        }

        System.out.println(">>> SPLIT-SIZE=" + split.size() );
        return split;

    }

    int limitOfNodesToProcess = -1;
    boolean doLimitIds = false;
    public void load(int i) {
        doLimitIds = true;
        limitOfNodesToProcess = i;
        load();
    }

    private int[] mapIndizesToIds(int[] indizes, NodeGroup org) {
        int[] _ids = new int[ids.length];
        int x = 0;
        for( int id : indizes ) { 
            _ids[x] = org.ids[id];
            x++;
        }
        return _ids;
    }

    boolean[] mrAccessNA = null;
    public void setNotAvailableAccessTS(boolean[] mrNA) {
        mrAccessNA = mrNA;
    }

    /**
     * if we do add TS via the Vector directly, we have to init the 
     * id array .
     */
    public void postInit() {
        ids = new int[ getIdsAsVector().size() ];
        int i = 0;
        for( Integer id : getIdsAsVector() ) {
            ids[ i ] = id;
            i = i + 1;
        }
    }

    boolean isShuffled = false;
    public void doShuffleAllNow() {
        if( !isShuffled ) {
            for( Messreihe mr : this.accessReihen ) { 
                mr.shuffleYValues();
            }
            isShuffled = true;
            Toolkit.getDefaultToolkit().beep();
            System.err.println( ">>> is SHUFFLED now " );
        }
    }

    boolean isRandomized = false;
    public void doRandomizeAllNow() {
        Vector<Messreihe> v = new Vector<Messreihe>();
        for( Messreihe mr : this.accessReihen ) {
                int s = mr.yValues.size();
                mr = Messreihe.getGaussianDistribution(s, 15, 4);
                v.add(mr);
        }
        this.accessReihen = v;
        isRandomized = true;
        Toolkit.getDefaultToolkit().beep();
        System.err.println( ">>> is Randomized now " );

    }
    

}
