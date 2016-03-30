/**
 * 
 * Just a ChartFrame to show some TimeSeries plots ...
 * 
 * a grid with 6 tiles is used to view 6 plots in one screen.
 * 
 */
package com.cloudera.wikiexplorer.ng.gui;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import javax.swing.JComponent;

/**
 *
 * @author kamir
 */
public class ChartFrame extends javax.swing.JFrame {

    /**
     * Creates new form ChartFrame
     */
    public ChartFrame() {
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chart Frame");
        setBackground(java.awt.Color.white);
        getContentPane().setLayout(new java.awt.GridLayout(2, 3, 5, 5));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static ChartFrame open(JComponent args[][], String title) {
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
            java.util.logging.Logger.getLogger(ChartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        ChartFrame cf = new ChartFrame();
        cf.setTitle( "Wikipedia relevance studies : {CN=" + title + "}" );
        cf.setBoxes( args );
        cf.setVisible(true);
        
        return cf;
 
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private void setBoxes(JComponent[][] args) {
        this.getContentPane().add( args[0][0] );
        this.getContentPane().add( args[0][1] ); 
        this.getContentPane().add( args[0][2] ); 
        this.getContentPane().add( args[1][0] );
        this.getContentPane().add( args[1][1] ); 
        this.getContentPane().add( args[1][2] ); 
    }
}