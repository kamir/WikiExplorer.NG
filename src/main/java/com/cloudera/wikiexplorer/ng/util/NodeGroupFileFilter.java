/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util;

import java.io.File;
import java.io.FileFilter;
import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;

/**
 *
 * @author kamir
 */
class NodeGroupFileFilter extends javax.swing.filechooser.FileFilter {

    public NodeGroupFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        if ( f.getName().endsWith( EditActivityFilter.extension )) return true;
        else if ( f.getName().endsWith( EditActivityFilter.ext_ng ) ) return true;
        if ( f.isDirectory() ) return true;
        else return false;
    }

    @Override
    public String getDescription() {
        return "WikiPage Node Groups ...";
    }



}
