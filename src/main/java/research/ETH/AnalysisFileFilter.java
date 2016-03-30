package research.ETH;

import java.io.File;
import java.io.FileFilter;


/**
 *
 * @author kamir
 */
public class AnalysisFileFilter {
    
    public static boolean shuffle = false;
 
    LinksStrengthFileFilter filter = null;
    
    public AnalysisFileFilter(String inputset, String keysC, int i, int j) {
        filter = new LinksStrengthFileFilter(inputset, keysC, i, j );
    }

    File[] getFiles( File base ) {
        System.out.println( "Look for files in: " + base );
        return base.listFiles(filter);
    }
    
    
    
}
class LinksStrengthFileFilter implements FileFilter {
  
  String inputSet;
  String keyC;
  int x;
  int y;   
    
  public LinksStrengthFileFilter(String inputSet, String keyC, int x, int y ) {
      this.inputSet = inputSet;
      this.keyC = keyC;
      this.x = x;
      this.y = y;
  }    
  
  
  
  public boolean accept(File file) {
      // SP500_tv_5_170_20_WIKI_CC_shuffle=false.dat
      
      String start = inputSet + "_" + keyC + "_" + y;
      String end = x + "_WIKI_CC_shuffle="+ AnalysisFileFilter.shuffle +".dat";
            
      String fn = file.getName().toLowerCase();
      
      //System.out.println("START: [" + start + "] END: [" + end + "] File: " + fn );
      
      if (fn.endsWith( end.toLowerCase() ) && fn.startsWith( start.toLowerCase() ) ) {
        return true;
      }
      return false;
  }
  
}