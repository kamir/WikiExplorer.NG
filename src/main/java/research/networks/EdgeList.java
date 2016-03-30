/*
 *
 *
 */

package research.networks;

import java.util.*;

/**
 *
 * @author kamir
 */
public class EdgeList {

    Hashtable<Integer,Vector<Edge>> byDest = new Hashtable<Integer,Vector<Edge>>();
    Hashtable<Integer,Vector<Edge>> bySource = new Hashtable<Integer,Vector<Edge>>();

    public void addEdge( Edge e ) {
        
        boolean v1IsNew = false;
        Vector v1 = byDest.get( e.dest );
        if ( v1 == null ) {
            v1 = new Vector();
            v1IsNew = true;
        }
        if ( !v1.contains(e) ) v1.add( e );
        if ( v1IsNew ) byDest.put( e.dest , v1 );


        boolean v2IsNew = false;
        Vector v2 = bySource.get( e.source );
        if ( v2 == null ) {
            v2 = new Vector();
            v2IsNew = true;
        }
        if ( !v2.contains(e) ) v2.add( e );
        if ( v2IsNew ) bySource.put( e.source , v2 );


    };


}
