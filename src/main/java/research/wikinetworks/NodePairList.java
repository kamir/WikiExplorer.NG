package research.wikinetworks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class NodePairList {

    Vector<NodePair> pairs = null;

    public Vector<NodePair> getPairs() {
        return pairs;
    }

    public NodeGroup ng = null;
    
    static HashMap<Integer,Vector<Integer>> map = null;

    public NodePairList() {    }

    public String getNodeGroupsFn() {
        String state_ext = NodeGroup.getStateExtension();
        return this.f.getName() + state_ext +".ng";
    };

    public String getFn() {
        return this.f.getName();
    };


    public void read( File _f ) throws IOException {
        f = _f;
        int i = 0;
        pairs = new Vector<NodePair>();
        map = new HashMap<Integer,Vector<Integer>>();
        BufferedReader br = new BufferedReader( new FileReader( f.getAbsolutePath() ) );
        int lc = 0;
        while( br.ready() ) {
            String line = br.readLine();
            if ( line.startsWith("#") ){

            }
            else {
                lc++;
                int[] pids = getIdsForNodePair(line);
                NodePair np = new NodePair();
                np.pageIDA = pids[0];
                np.pageIDB = pids[1];

                if ( pids[0] == pids[1] ) i++;

                pairs.add(np);

                Vector<Integer> v = map.get(pids[0]);
                if ( v == null ) {
                    v = new Vector<Integer>();
                    map.put(pids[0], v);
                }
                v.add(pids[1]);


                Vector<Integer> v2 = map.get(pids[1]);
                if ( v2 == null ) {
                    v2 = new Vector<Integer>();
                    map.put(pids[1], v2);
                }
                v2.add(pids[0]);
            }
        }
        System.out.println(">>> NodePairList:line count=" + lc );
        System.out.println(">>> NodePairList:pairs size=" + pairs.size() );
        System.out.println(">>> NodePairList:self-links=" + i );
        System.out.println(">>> NodePairList:real-links=" + (pairs.size() - i) );

        ng = new NodeGroup( pairs );

    };


    public NodePairList( Vector<NodePair> _pairs ) {
        pairs = _pairs;
    }

    File f = null;

    public void store( File _f ) throws IOException {
        f = _f;
        System.out.println(">>> REAL LINKED NODES: " + f.getAbsolutePath() + "[" + pairs.size() + "]" );
        BufferedWriter bw = new BufferedWriter( new FileWriter( f.getAbsolutePath() ) );
        for( NodePair p : pairs ) {
            bw.write( p.pageIDA + "\t" + p.pageIDB + "\t" + "1" + "\n");
        }
        bw.flush();
        bw.close();
    }

    public static int[] getIdsForNodePair( String s) {
        int[] ids = new int[2];

        String[] a = s.split("\t");
        ids[0] = Integer.parseInt( a[0] );
        ids[1] = Integer.parseInt( a[1] );

        return ids;
    }

    public static String[] getNamesForNodePair( String s ) {
        String[] names = new String[2];

        return names;
    }

    public void writeClearList() throws IOException {
        FileWriter fw = new FileWriter( f.getAbsolutePath() + ".clear" );

        for ( int i : ng.ids ) {
            String name = PageNameLoader.getPagenameForId(i);
            String line = i + "\t" + name + getListOfLinked( i );

            fw.write( line );
            fw.flush();
        }
        fw.close();
    };

    private String getListOfLinked(int i) {

        StringBuffer sb = new StringBuffer();

        Vector<Integer> v = map.get(i);
        Enumeration<Integer> en = v.elements();
        sb.append( " ["+v.size()+"]" + "\n");
        while( en.hasMoreElements() ) {
            int idL = en.nextElement();
            String link = " - ";
            if ( toCompare != null ) {
                link = getHasLink(i,idL);
            }
            sb.append( "\t" + link + " " + idL + "\t" + PageNameLoader.getPagenameForId( idL ) + "\n");
        }
        return sb.toString();
    }


    public Hashtable<Integer,Integer> getDegreeList() {

        Hashtable<Integer,Integer> data = new Hashtable<Integer,Integer>();
       

        Set keys = map.keySet();
        Iterator it = keys.iterator();

        while( it.hasNext() ) {
            Integer id = (Integer) it.next();
            Vector links = map.get(id);
            data.put(id, links.size() );
        }
       
        return data;
    }

    NodePairList toCompare = null;
    public void writeClearList(NodePairList statischesNetz) throws IOException {
        toCompare = statischesNetz;
        writeClearList();
    }

    private String getHasLink(int i, int idL) {
        String back = " - ";
        NodePair np = new NodePair();
        np.pageIDA = i;
        np.pageIDB = idL;

        if ( toCompare.pairs.contains(np ) ) back = "[+]";
        return back;
    }

}
