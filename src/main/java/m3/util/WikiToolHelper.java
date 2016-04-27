package m3.util;

public class WikiToolHelper {
    
    /**
     * The ignoreUncleanPageNames() returns null if
     * the pagename contains strange symbols not allowed in filenames.
     *  
     * We just look for ", < and >" give back null if strange symbols are in.
     * 
     * @param link
     * @return 
     */
    public static String ignoreUncleanPageNames(String link) {

        if ( link.contains( "\"" ) || link.contains(">") || link.contains( "<") ) {
            return null;
        }
        else return link;

    }
    
}
