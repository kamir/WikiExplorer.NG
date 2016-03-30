/*

 */
package research.topics;

import java.util.logging.Level;
import java.util.logging.Logger;
import research.results.NodeGroupAnalyser;

/**
 *
 * @author kamir
 */
public class WikiNodeGroupAnalyser {
    
    public static String referenceNG = "G:\\DEV\\MLU\\Wiki\\TimeSeriesToolbox\\";
    
    public static String refNG_file = "60_1000_most_active_by_access.dat";
    
    public static String refNG_fileA = "list_DAX.dat.ids.dat";
    public static String refNG_fileB = "list_sup500.dat.ids.dat";
    
    public static String refNG_fileC = "page_names_financial2.dat.ids.dat";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            NodeGroupAnalyser.referenceNG = referenceNG;
            NodeGroupAnalyser.refNG_file = refNG_fileC;
            NodeGroupAnalyser analyzer = new NodeGroupAnalyser();
            
            analyzer.init();
            
        } 
        catch (Exception ex) {
            Logger.getLogger(WikiNodeGroupAnalyser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
