/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdata.explorer.views;

/**
 *
 * @author root
 */
/*
 * This example is from the book "Java Foundation Classes in a Nutshell".
 * Written by David Flanagan. Copyright (c) 1999 by O'Reilly & Associates.  
 * You may distribute this source code for non-commercial purposes only.
 * You may study, modify, and use this example for any purpose, as long as
 * this notice is retained.  Note that this example is provided "as is",
 * WITHOUT WARRANTY of any kind either expressed or implied.
 */

import bigdata.explorer.models.CrawlFileTreeModel;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.File;


public class CfgFileTree {

  public JTree tree = null;
  public JScrollPane scrollpane = null;
    
  
  public void init(String[] args) {
    // Figure out where in the filesystem to start displaying
    File root;
    if (args.length > 0) root = new File(args[0]);
    else root = new File(System.getProperty("user.home"));

    // Create a TreeModel object to represent our tree of files
    CrawlFileTreeModel model = new CrawlFileTreeModel(root);

    // Create a JTree and tell it to display our model
    tree = new JTree();
    tree.setModel(model);

    // The JTree can get big, so allow it to scroll.
    scrollpane = new JScrollPane(tree);
    
    // Display it all in a window and make the window appear
//    JFrame frame = new JFrame("FileTreeDemo");
//    frame.getContentPane().add(scrollpane, "Center");
//    frame.setSize(400,600);
//    frame.setVisible(true);
  }
}
