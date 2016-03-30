/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdata.explorer.nutch;

import org.apache.hadoop.io.Text;
import org.apache.nutch.scoring.webgraph.LinkDatum;

/**
 *
 * @author kamir
 */
public class MyLinkDatum {
    
    String sourc = null;
    String dest = null;
    
    LinkDatum ld = null;
    
    public MyLinkDatum() { }
    
    public void setLink(Text t, LinkDatum d) { 
        ld = d;
        sourc = t.toString();
        dest = d.getUrl();
    }
    
    
}
