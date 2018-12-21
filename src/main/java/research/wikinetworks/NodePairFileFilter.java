/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package research.wikinetworks;

import java.io.File;

import com.cloudera.wikiexplorer.ng.util.nodegroups.EditActivityFilter;

/**
 *
 * @author kamir
 */
class NodePairFileFilter extends javax.swing.filechooser.FileFilter {

    public NodePairFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        if ( f.getName().endsWith( ".dat" )) return true;
        else if ( f.isDirectory() ) return true;
        else return false;
    }

    @Override
    public String getDescription() {
        return "WikiPage NodePairs ...";
    }



}
