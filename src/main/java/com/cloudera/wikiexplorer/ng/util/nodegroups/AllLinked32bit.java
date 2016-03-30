/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util.nodegroups;

import java.io.File;
import java.io.IOException;
import research.wikinetworks.NodePairList;
import com.cloudera.wikiexplorer.ng.util.NodeGroup;

/**
 *
 * @author kamir
 */
public class AllLinked32bit extends NodeGroup {

    public AllLinked32bit() throws IOException {
        NodePairList liste = new NodePairList();
        liste.read( new File( NodeGroup.pfad + "filtered/32bit_VphMean.dat.ngLINKED_both.pairs.dat") );
        //NodeGroup partialGroup = new NodeGroup( liste.getPairs() );
        initNodePairs( liste.getPairs() );
        this.fn = liste.getFn();
    };





}
