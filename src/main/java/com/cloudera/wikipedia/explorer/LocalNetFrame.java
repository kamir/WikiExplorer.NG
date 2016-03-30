/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloudera.wikipedia.explorer;

import com.cloudera.wikiexplorer.ng.app.SimpleSFE;
import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.hadoopts.topics.wikipedia.LocalWikipediaNetwork2;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

/**
 *
 * @author kamir
 */
public class LocalNetFrame extends javax.swing.JDialog {
    
        Vector<Messreihe> mrCN = null;
        Vector<Messreihe> mrIWL = null;
        Vector<Messreihe> mrAL = null;
        Vector<Messreihe> mrBL = null;
        
    public static void showNetworkForID( int i , LocalWikipediaNetwork2 net, SimpleSFE gui ) { 
      
        i=i+1;
        
        Vector<Messreihe> mrCN = gui.hash.get( i+".CN" );
        Vector<Messreihe> mrIWL = gui.hash.get( i+".IWL" );
        Vector<Messreihe> mrAL = gui.hash.get( i+".A.L" );
        Vector<Messreihe> mrBL = gui.hash.get( i+".B.L" );
        
        final LocalNetFrame dialog = new LocalNetFrame(new javax.swing.JFrame(), false);
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
           public void windowClosing(java.awt.event.WindowEvent e) {
                        dialog.dispose();
                    }
        });
        
        
        dialog.initReihen( mrCN, mrIWL, mrAL, mrBL );
        
        dialog.setVisible(true);

    }

    /**
     * Creates new form LocalNetFrame
     */
    public LocalNetFrame(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpCenter = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jpNorth = new javax.swing.JPanel();
        jpSouth = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpCenter.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(1, 4));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("CN"));

        jList4.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList4);

        jPanel1.add(jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("IWL"));

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList3);

        jPanel1.add(jScrollPane2);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("A.L"));

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList2);

        jPanel1.add(jScrollPane3);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("B.L"));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList1);

        jPanel1.add(jScrollPane4);

        jTabbedPane1.addTab("local net data", jPanel1);

        jpCenter.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jpCenter, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jpNorthLayout = new javax.swing.GroupLayout(jpNorth);
        jpNorth.setLayout(jpNorthLayout);
        jpNorthLayout.setHorizontalGroup(
            jpNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jpNorthLayout.setVerticalGroup(
            jpNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        getContentPane().add(jpNorth, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout jpSouthLayout = new javax.swing.GroupLayout(jpSouth);
        jpSouth.setLayout(jpSouthLayout);
        jpSouthLayout.setHorizontalGroup(
            jpSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jpSouthLayout.setVerticalGroup(
            jpSouthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        getContentPane().add(jpSouth, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LocalNetFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LocalNetFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LocalNetFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LocalNetFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LocalNetFrame dialog = new LocalNetFrame(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel jpCenter;
    private javax.swing.JPanel jpNorth;
    private javax.swing.JPanel jpSouth;
    // End of variables declaration//GEN-END:variables

    private void initReihen(Vector<Messreihe> mrCN, Vector<Messreihe> mrIWL, Vector<Messreihe> mrAL, Vector<Messreihe> mrBL) {
        
        this.mrAL = mrAL;
        this.mrBL = mrBL;
        this.mrIWL = mrIWL;
        this.mrCN = mrCN;
        
        setDataToListe( mrCN , jList4 );
        setDataToListe( mrIWL , jList3 );
        setDataToListe( mrAL , jList2 );
        setDataToListe( mrBL , jList1 );
        
    }

    private void setDataToListe(Vector<Messreihe> m, JList jL) {
        DefaultListModel lm = new DefaultListModel();
        for( Messreihe mr : m ) {
            String n = mr.getLabel() + "#" + mr.getDescription() + "#" + mr.getIdentifier();
            lm.addElement(n);
        }
        jL.setModel(lm);
        this.repaint();
    }
}