/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdata.explorer.nutch.corpus.creator;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.cloudera.wikiexplorer.ng.app.WikipediaCorpusLoaderTool;
import m3.wikipedia.explorer.data.WebNode;

/**
 *
 * @author kamir
 */
public class NutchCrawlCFGInputFrame extends javax.swing.JFrame   {

    /**
     * Creates new form CNInputFrame
     */
    public NutchCrawlCFGInputFrame() {
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        selectionTF = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jrbSEQ = new javax.swing.JRadioButton();
        jrbXML = new javax.swing.JRadioButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jtfDIR = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtfDEPTH = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtfTOPN = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtfURLS = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Web Ressource Quality Analysis - METADATA Editor");

        jLabel1.setText("Crawl : ");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "URI"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jScrollPane2.setViewportView(jTextArea1);

        jButton1.setText("Start crawl ...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("save");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setText("new");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("load");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton3.setText("+");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setText("-");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        selectionTF.setText("0");

        jCheckBox1.setText("run on cluster");

        buttonGroup1.add(jrbSEQ);
        jrbSEQ.setText("SEQ-files");

        buttonGroup1.add(jrbXML);
        jrbXML.setSelected(true);
        jrbXML.setText("XML-files");

        jButton7.setText("Create seed.txt");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Submit");
        jButton8.setEnabled(false);
        jButton8.setSelected(true);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Analyse TS");
        jButton9.setEnabled(false);
        jButton9.setSelected(true);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel2.setText("-dir ");

        jtfDIR.setText("data");

        jLabel3.setText("-depth");

        jtfDEPTH.setText("5");

        jLabel4.setText("-topN");

        jtfTOPN.setText("25");

        jLabel5.setText("${urls}/seed.txt");

        jtfURLS.setText("urls");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(39, 39, 39)
                        .addComponent(jtfDIR))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jrbSEQ)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jrbXML))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(22, 22, 22)
                                .addComponent(jtfDEPTH, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtfTOPN, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtfURLS)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(152, 152, 152)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(selectionTF, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2)))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton6)
                            .addComponent(jCheckBox1)
                            .addComponent(selectionTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jtfDIR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtfDEPTH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jtfTOPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jtfURLS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jrbSEQ)
                    .addComponent(jrbXML)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton1)
                    .addComponent(jButton9))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        newStudie();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        CrawlMetaData sdm = initMetaData();
        saveStudie( sdm );
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        loadStudieMetaData();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        Vector v = new Vector();
        v.add( new WebNode().getUri() );
        dtm.addRow(v);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        javax.swing.JOptionPane.showMessageDialog(this, "Sammeln der Daten wird etwas dauern ..." );
        
        CrawlMetaData sdm = initMetaData();
        
        
        try {
            sdm.createSeedFile();
            sdm.runCrawl();
        } 
        catch (Exception ex) {
            Logger.getLogger(NutchCrawlCFGInputFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    int selection = 0;
    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        selection = Integer.parseInt( selectionTF.getText() );
        System.out.println( selection - 1 );
        dtm.removeRow( selection - 1 );
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1PropertyChange

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        CrawlMetaData sdm = initMetaData();
        try {
            sdm.createSeedFile();
        } catch (IOException ex) {
            Logger.getLogger(NutchCrawlCFGInputFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // SUBMIT
        CrawlMetaData sdm = initMetaData();
        mergeListFilesAndSubmit(sdm);
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        openExtractedResults( );
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //javax.swing.JOptionPane.showMessageDialog(null, "Moin ..." );
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
            java.util.logging.Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NutchCrawlCFGInputFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JRadioButton jrbSEQ;
    private javax.swing.JRadioButton jrbXML;
    private javax.swing.JTextField jtfDEPTH;
    private javax.swing.JTextField jtfDIR;
    private javax.swing.JTextField jtfTOPN;
    private javax.swing.JTextField jtfURLS;
    private javax.swing.JTextField selectionTF;
    // End of variables declaration//GEN-END:variables

    private void loadStudieMetaData() {
        File f1 = new File(".");
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(f1);
        jfc.showOpenDialog( this );
        
        File f = jfc.getSelectedFile();
        try {
            CrawlMetaData smd = CrawlMetaData.load(f);
            initSMD( smd );
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initSMD(CrawlMetaData smd) {
        
        this.jTextField1.setText( smd.name );
        
        this.jTextArea1.setText( smd.description );
        
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();

        int i = dtm.getRowCount();
        for( int j = 0; j < i; j++ ) {
            dtm.removeRow(0);
        }

        for( WebNode w : smd.wn ) { 
            Vector v = new Vector();
            v.add( w.getUri() );
            dtm.addRow( v );
        }
        
        this.jTable1.setModel( dtm );
    }

    private void newStudie() {
        CrawlMetaData smd = new CrawlMetaData();
        initSMD(smd);
    }

    private void saveStudie( CrawlMetaData sdm ) {
        
        File f1 = new File(".");
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(f1);

        jfc.showSaveDialog( this );
        
        File f = jfc.getSelectedFile();
        try {
                sdm.store( f , sdm);
                
        } 
        catch (Exception ex) {
               Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
    }

    private CrawlMetaData initMetaData() {
        
        CrawlMetaData sdm = new CrawlMetaData();
        
        sdm.name = this.jTextField1.getText();
        sdm.description = this.jTextArea1.getText();
        
        sdm.depth = jtfDEPTH.getText();
        sdm.dir = jtfDIR.getText();
        sdm.topN = jtfTOPN.getText();
        sdm.seed_text_dir = jtfURLS.getText();
        
        TableModel tm = this.jTable1.getModel();
        for( int i = 0; i < tm.getRowCount(); i++ ) { 
            
            Object o = tm.getValueAt(i,0);
            
            if ( o instanceof String ) {

                URI u;
                try {
                    u = new URI( (String)o );
                    sdm.addNewNode(u);
                } 
                catch (URISyntaxException ex) {
                    Logger.getLogger(NutchCrawlCFGInputFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
             
            }
            else if ( o instanceof URI ) {
                sdm.addNewNode((URI)o);
            }    
        }
        
        
        
        return sdm;
    }

    private void mergeListFiles(CrawlMetaData sdm) {
//        try {
//            // merge listfiles now
//            BufferedWriter bw = new BufferedWriter( new FileWriter( "merged_listfile_" + sdm.name + ".lst"));
//            int j = 1;
//            for( WebNode wn : sdm.wn ) { 
//                //  listfile_Congo_Examples_en_Hema_people.lst
//                 File in = new File( "listfile_" + sdm.name + "_" + wn.wiki + "_" + wn.page + ".lst" );
//                 BufferedReader br = new BufferedReader( new FileReader( in ) );
//                while( br.ready() ) { 
//                    String line = br.readLine();
//                    bw.write(j+"." + line + "\n");
//                }
//                bw.flush();
//                j++;
//            }
//            bw.flush();
//            bw.close();
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(CNInputFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private void mergeListFilesAndSubmit(CrawlMetaData sdm) {
//        try {
//            // merge listfiles now
//            BufferedWriter bw = new BufferedWriter( new FileWriter( "merged_listfile_" + sdm.name + ".lst"));
//            javax.swing.JOptionPane.showMessageDialog(this, "Submit: " + "merged_listfile_" + sdm.name + ".lst" );
//            int j = 1;
//            for( WikiNode wn : sdm.wn ) { 
//                //  listfile_Congo_Examples_en_Hema_people.lst
//                File in = new File( "listfile_" + sdm.name + "_" + wn.wiki + "_" + wn.page + ".lst" );
//                BufferedReader br = new BufferedReader( new FileReader( in ) );
//                while( br.ready() ) { 
//                    String line = br.readLine();
//                    bw.write(j+"." + line + "\n");
//                }
//                bw.flush();
//                j++;
//            }
//            bw.flush();
//            bw.close();
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(CNInputFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            ExtractIWLinkCorpus2.submit( sdm.name );
//        } 
//        catch (IOException ex) {
//            Logger.getLogger(CNInputFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

    private void openExtractedResults() {
        System.out.println(" SEQ-File Viewer hier öffnen ... " );
        // ist aber in höherem LAYER !!! => REFACTORING
    }

 
}
