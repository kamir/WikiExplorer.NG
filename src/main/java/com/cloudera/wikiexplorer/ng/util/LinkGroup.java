/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import research.networks.StaticLinkManager;
import research.wikinetworks.NodePair;

/**
 *
 * @author kamir
 */
public class LinkGroup {

    Vector<NodePair> possibleLinks = null;
    
    Vector<NodePair> staticLinks = null;

    String name = "_";
    public LinkGroup( Vector<NodePair> l , String _name) {
        possibleLinks = l;
        name = _name;
    };

    public void lookupStaticLinks() {
        System.out.println( ">>> statische links werden geladen ... ");
        
        StaticLinkManager slm = new StaticLinkManager();

        NodeGroup ng = new NodeGroup( possibleLinks );
        ng.name = name;
        slm.ng = ng;
        try {
            slm.run();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LinkGroup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LinkGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
    };

}
