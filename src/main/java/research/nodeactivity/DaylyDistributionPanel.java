/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DaylyDistributionPanel.java
 *
 * Created on 16.10.2011, 09:23:43
 */

package research.nodeactivity;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Vector;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author kamir
 */
public class DaylyDistributionPanel extends javax.swing.JDialog {

    /** Creates new form DaylyDistributionPanel */
    public DaylyDistributionPanel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize( 800, 600);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jSlider1 = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jTMaxX = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTBINS = new javax.swing.JTextField();
        chartPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel3.setText("day");
        jPanel2.add(jLabel3);

        jSpinner1.setMinimumSize(new java.awt.Dimension(29, 32));
        jSpinner1.setPreferredSize(new java.awt.Dimension(49, 32));
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });
        jPanel2.add(jSpinner1);

        jSlider1.setMajorTickSpacing(28);
        jSlider1.setMaximum(300);
        jSlider1.setMinorTickSpacing(7);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setToolTipText("select day");
        jSlider1.setPreferredSize(new java.awt.Dimension(350, 45));
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        jPanel2.add(jSlider1);

        jLabel1.setText("maxX");
        jPanel2.add(jLabel1);

        jTMaxX.setText("100");
        jTMaxX.setPreferredSize(new java.awt.Dimension(48, 20));
        jTMaxX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTMaxXActionPerformed(evt);
            }
        });
        jTMaxX.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTMaxXKeyReleased(evt);
            }
        });
        jPanel2.add(jTMaxX);

        jLabel2.setText("bins");
        jPanel2.add(jLabel2);

        jTBINS.setText("100");
        jTBINS.setPreferredSize(new java.awt.Dimension(48, 20));
        jTBINS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTBINSKeyReleased(evt);
            }
        });
        jPanel2.add(jTBINS);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        chartPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        chartPanel.setLayout(new java.awt.GridLayout(1, 1));

        jPanel3.setLayout(new java.awt.BorderLayout());
        chartPanel.add(jPanel3);

        getContentPane().add(chartPanel, java.awt.BorderLayout.CENTER);

        jButton1.setText("close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        // TODO add your handling code here:
        this.jSpinner1.setValue(this.jSlider1.getValue());
        createChart();
    }//GEN-LAST:event_jSlider1StateChanged

    private void jTMaxXKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTMaxXKeyReleased
        // TODO add your handling code here:
        this.maxX = Integer.parseInt(this.jTMaxX.getText());
        createChart();
    }//GEN-LAST:event_jTMaxXKeyReleased

    private void jTMaxXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTMaxXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTMaxXActionPerformed

    private void jTBINSKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTBINSKeyReleased
        // TODO add your handling code here:
        this.bins = Integer.parseInt(this.jTBINS.getText());
        createChart();
    }//GEN-LAST:event_jTBINSKeyReleased

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        // TODO add your handling code here:
        v=(Integer)this.jSpinner1.getValue();
        this.jSlider1.setValue( v );
        this.createChart();
    }//GEN-LAST:event_jSpinner1StateChanged

    static Vector<TimeSeriesObject> mrs = null;
    /**
    * @param args the command line arguments
    */
    public static void open( Vector<TimeSeriesObject> _mrs) {
        mrs = _mrs;
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DaylyDistributionPanel dialog = new DaylyDistributionPanel(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.jSlider1.setValue(0);
                dialog.createChart(0);
                RefineryUtilities.centerFrameOnScreen(dialog);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTBINS;
    private javax.swing.JTextField jTMaxX;
    // End of variables declaration//GEN-END:variables

    int maxX = 25;
    int bins = 100;
    int v = 0;

    private void createChart(int _v) {
        v = _v;

        if ( v < 0 ) v = 0;
        
        TimeSeriesObject cut = mrs.elementAt(v); //DaylyDistribution.getHorizontalCut( mrs , v );
        Container c = DaylyDistribution.createHistogramm(DaylyDistribution.ng, cut, bins, 0, maxX);
        Dimension d = jPanel3.getSize();
        System.out.println( d );
        c.setSize(d);
        c.validate();
        c.repaint();
        
        this.jPanel3.removeAll();
        this.jPanel3.add( c , BorderLayout.CENTER );

        this.validate();
        this.repaint();
    }

    private void createChart() {
        createChart(v);
    }

}
