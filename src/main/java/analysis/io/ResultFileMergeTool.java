/**
 *
 */
package analysis.io;

import java.io.*;
import java.util.*;
import research.wikinetworks.NodePair;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 * Wir lesen die Liste einer Kreuzkorrelations-Rechnung ein
 * ermitteln die zugehörige Node-Group
 * und führen dazu die Werte k_in k_out als neue Spalte an.
 * 
 * @author kamir
 */
public class ResultFileMergeTool {
    
    public static void main( String[] args ) throws IOException { 
    
        Vector<File> files = new Vector<File>();
        NetworkDataStore store = new NetworkDataStore();
        
        String label = "015"; 
        
        File kJoinedLIST = new File( "P:/ARBEIT/WIKI/degree distribution/Workbook_Out_vs_In_DEGREE_Joined.csv" );
        File k_IN_LIST = new File( "D:/WIKI/count_links/indegree_all_1.dat" );
        File k_OUT_LIST = new File( "D:/WIKI/count_links/outdegree_all_1.dat" );
        
        File accessResult = store.getAccessCCResultFile(label);
        File editResult = store.getEditESResultFile(label);
        File statNet = store.getStatNet(label);
        
        File ngData = store.getNGFile(label);
        
        File mergedNodegroupStatData = store.getMergedStatPropsFile(label);
        File nodegroupStatData = store.getNodeGroupStatisticsFile(label);
        File kStatData = store.getMergedStatPropsFile(label);
        
        Properties props = store.getProperties( label );
        System.out.println( props.toString() );
        
        if ( !editResult.exists() ) { 
            System.err.println( ">>> NG-File missing ... \n\t" + editResult.getAbsolutePath() );
        }
             
        if ( !accessResult.exists() ) { 
            System.err.println( ">>> Access-Result missing ... \n\t" + accessResult.getAbsolutePath() );
        }

        if ( !editResult.exists() ) { 
            System.err.println( ">>> Edit-Result missing ... \n\t" + editResult.getAbsolutePath() );
        }

        if ( !statNet.exists() ) { 
            System.err.println( ">>> StatNet missing ... \n\t" + statNet.getAbsolutePath() );
        }
        
        if ( !nodegroupStatData.exists() ) { 
            System.err.println( ">>> NodeGroupStatData missing ... \n\t" + nodegroupStatData.getAbsolutePath()  );
        }
        
        if ( !kStatData.exists() ) { 
            System.err.println( ">>> Merging von kStat und NG_File notwendig ... " );
            
            NodeGroup ng = new NodeGroup(ngData);
            ng.load();
            
            // merge_kJOIN_data_with_nodeGroup( ng , kJoinedLIST );
            
            merge_k_data_with_nodeGroup( ng , k_IN_LIST, pageID_k_IN );
            merge_k_data_with_nodeGroup( ng , k_OUT_LIST, pageID_k_OUT );
            
            BufferedWriter bw = new BufferedWriter( new FileWriter( kStatData.getAbsolutePath() ) );
            
            int i = 0;
            Enumeration en = pageID_k_OUT.keys();
            while( en.hasMoreElements() ) { 
                String key = (String)en.nextElement();
                String data1 = (String)pageID_k_IN.get(key);
                String data2 = (String)pageID_k_OUT.get(key);
                String lineOut = ( key + "\t" + data1 + "\t" + data2 + "\n");
                bw.write( lineOut );
            }
            bw.flush();
        }        
    }
    
    public static void merge_kJOIN_data_with_nodeGroup( NodeGroup ng, File kLIST ) throws FileNotFoundException, IOException { 
           
        Hashtable pageID_k = new Hashtable();
        
        BufferedReader br = new BufferedReader( new FileReader(kLIST) );
        while( br.ready() ) { 
            String line = br.readLine();
            line = line.replaceAll("\"", "");
            // System.out.println( line );
            
            StringTokenizer st = new StringTokenizer( line, "," );
            int i = 0;
            int idA = 0;
            String tok3 = "?";
            String tok4 = "?";
            
            String id = "?";
            while( st.hasMoreTokens() ) { 
                String tok = st.nextToken();
                if ( i == 0 ) { 
                    id = tok;
                    idA = Integer.parseInt(id);
                }
                if ( ng.belongsWithOneNodeToGroup( new NodePair( idA, idA, ng ))) { 
                    pageID_k.put( id, tok3+"\t"+tok4 );
                };
                
                i++;
            }
        }
        
        int i = 0;
        Enumeration en = pageID_k.keys();
        while( en.hasMoreElements() ) { 
            String key = (String)en.nextElement();
            String data = (String)pageID_k.get(key);
            System.out.println( i + "  " + key + "\t" + data);
            i++;
        }
        
    }

    static Hashtable pageID_k_IN = new Hashtable();
    static Hashtable pageID_k_OUT = new Hashtable();
         
    private static void merge_k_data_with_nodeGroup(NodeGroup ng , File kLIST, Hashtable tab) throws FileNotFoundException, IOException {
        
        Hashtable pageID_k = tab;
        
        BufferedReader br = new BufferedReader( new FileReader(kLIST) );
        while( br.ready() ) { 
            String line = br.readLine();
            line = line.replaceAll("\"", "");
            // System.out.println( line );
            
            StringTokenizer st = new StringTokenizer( line );
            int i = 0;
            int idA = 0;
            String k = "?";
                        
            String id = "?";
            while( st.hasMoreTokens() ) { 
                String tok = st.nextToken();
                if ( i == 0 ) { 
                    id = tok;
                    idA = Integer.parseInt(id);
                }
                k = tok;
                if ( ng.belongsWithOneNodeToGroup( new NodePair( idA, idA, ng ))) { 
                    pageID_k.put( id, k);
                };
                
                i++;
            }
        }
        
        int i = 0;
        Enumeration en = pageID_k.keys();
        while( en.hasMoreElements() ) { 
            String key = (String)en.nextElement();
            String data = (String)pageID_k.get(key);
            System.out.println( i + "  " + key + "\t" + data);
            i++;
        }
        
    }
}
