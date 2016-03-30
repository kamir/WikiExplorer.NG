/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util.nodegroups;

import java.util.Vector;
import research.wikinetworks.NodePair;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class AllNodesGroup extends NodeGroup {


    public AllNodesGroup() {
        super();
        fn = "ng_all";
        name = "ng_all";
        //load();
    };
 

    
    
    @Override
    public boolean[] belongsNodeToGroup(NodePair p) {
        boolean[] b = new boolean[2];
        b[0] = true;
        b[1] = true;
        return b;
    }

    @Override
    public boolean belongsWithBothNodesToGroup(NodePair p) {
        return true;
    }

}
