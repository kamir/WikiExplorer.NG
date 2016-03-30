/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package m3.wikipedia.corpus.extractor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.openide.util.Exceptions;

/**
 *
 * @author kamir
 */
public class FileNameFilter {

    /**
     * Convert back from save mode ...
     * 
     * @param netn_
     * @return 
     */
    public static String cleanWikinameForFilename(String netn_) {
        
        if ( netn_.contains( "/" ) ) {
           netn_ = netn_.replaceAll("/", "_");
        }    
        if ( netn_.contains( "'" ) ) {
           netn_ = netn_.replaceAll("'", "_");
        }    
        if ( netn_.contains( "&" ) ) {
           netn_ = netn_.replaceAll("&", "_");
        }    
        if ( netn_.contains( "Í" ) ) {
           netn_ = netn_.replaceAll("Í", "_");
        }
        if ( netn_.contains( "%28" ) ) {
           netn_ = netn_.replaceAll("%28", "(");
        }
        if ( netn_.contains( "%29" ) ) {
           netn_ = netn_.replaceAll("%29", ")");
        }
        if ( netn_.contains( "%21" ) ) {
           netn_ = netn_.replaceAll("%21", "!");
        }
        
        return netn_;
    }

    /**
     * Make page name save for local file representations ...
     * 
     * @param page
     * @return 
     */
    public static String encodePagenNameWebsave(String page) {

        try {
            return URLEncoder.encode(page, "UTF-8");
        } 
        catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return page;
        
    }
    
}
