package m3.wikipedia.explorer.data;

import java.util.Calendar;

/**
 *
 * @author root
 */
public class WikiNode extends WebNode {

    public static WikiNode createFromKeyName(String name) {
        String[] s = WikiNode.splitNameParts(name);
        WikiNode wn = new WikiNode();
        wn.wiki = s[0];
        wn.page = s[1];
        return wn;
    }
    
    public String groupKey;
    
    public int pageId;
    public String page;
    public int pageVolume;
    public int kOut;
    public int kIWL;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getPageVolume() {
        return pageVolume;
    }

    public void setPageVolume(int pageVolume) {
        this.pageVolume = pageVolume;
    }

    public int getkOut() {
        return kOut;
    }

    public void setkOut(int kOut) {
        this.kOut = kOut;
    }

    public int getkIWL() {
        return kIWL;
    }

    public void setkIWL(int kIWL) {
        this.kIWL = kIWL;
    }
        
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }
    public String wiki;

    public WikiNode() {
        
    }
    
    public WikiNode(String aa) {
        String[] a = splitNameParts( aa );
        page = a[1];
        wiki = a[0];
    }
        
    public WikiNode(String[] a) {
        page = a[1];
        wiki = a[0];
    }

    public WikiNode(String w, String l) {
        page = l;
        wiki = w;
    }
    
    public String toString() { 
        return "[" + wiki + "] " + page;  
    }

    public static String[] splitNameParts(String aa) {
        String[] a = aa.split("___");
        return a;
    }

    public String getKey() {
        return wiki + "___" + page;
    }

    public String getKey_TIME_DEPENDENT( Calendar von, Calendar bis) {
        return m3.tscache.TSCache.getKey( this, von, bis);
    }

    public String getKeySAFEFILENAME() {
        return getKey().replace( "/", "_");
    }

    
            
}
