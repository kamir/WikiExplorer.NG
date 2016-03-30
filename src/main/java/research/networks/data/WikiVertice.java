/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package research.networks.data;

import edu.uci.ics.jung.graph.event.GraphEvent;
import research.wikinetworks.PageNameLoader;

/**
 *
 * @author kamir
 */
public class WikiVertice {

    int id;
    
    public WikiVertice() {
    };

    public WikiVertice( int _id ) {
        id = _id;
    };

    @Override
    public String toString() {
        return id + ": ["+ PageNameLoader.getPagenameForId(id) +"]";
    };

}
