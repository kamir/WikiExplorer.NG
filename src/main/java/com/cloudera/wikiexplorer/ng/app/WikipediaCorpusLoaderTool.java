package com.cloudera.wikiexplorer.ng.app;

import com.cloudera.wikiexplorer.ng.gui.DateTextField;
import m3.research.measures.CoreDensiteyCalculator;
import m3.io.CNResultManager2;
import m3.wikipedia.corpus.extractor.iwl.ExtractIWLinkCorpus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import m3.jstat.data.Corpus;
import org.apache.velocity.runtime.parser.ParseException;
import org.crunchts.store.TSB;
import org.openide.util.Exceptions;

//import org.wikipedia.Wiki2;
//import org.wikipedia.Wiki.Revision;

import wikiapiclient.WikiORIGINAL;
import m3.wikipedia.analysis.charts.RepresentationPlotTool;
import m3.wikipedia.corpus.extractor.FileNameFilter;
import m3.wikipedia.corpus.extractor.WikiStudieMetaData;


import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2;
import m3.wikipedia.corpus.extractor.edits.WikiHistoryExtractionBASE;
import m3.wikipedia.corpus.extractor.iwl.ExtractIWLinkAdvanced;

import m3.wikipedia.explorer.data.WikiNode;

/**
 *
 * @author kamir
 */
public class WikipediaCorpusLoaderTool extends javax.swing.JFrame {

    public static String[] lastARGS = null;
    public static void setArgs(String[] args2) {
        lastARGS = args2;
        initArgs();
    }

    static WikipediaCorpusLoaderTool fram = null;
    private static void initArgs() {
        fram.jl1.setText(lastARGS[0]);
        fram.jl2.setText(lastARGS[1]);
        fram.jl3.setText(lastARGS[2]);
        fram.jl4.setText(lastARGS[3]);
        fram.jl5.setText(lastARGS[4]);
    }

    /**
     * Creates new form CNInputFrame
     */
    public static WikipediaCorpusLoaderTool getCNIFRAME() {

        if (fram == null) {
            fram = new WikipediaCorpusLoaderTool();
        }
        fram.initComponents();

        fram.initTimeFrame();

        return fram;
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuItem1 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jtfStudie = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jrbSEQ = new javax.swing.JRadioButton();
        jrbXML = new javax.swing.JRadioButton();
        jButton11 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        datePanel = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        selectionTF = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        crawlMode = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jl3 = new javax.swing.JLabel();
        jl4 = new javax.swing.JLabel();
        jl1 = new javax.swing.JLabel();
        jl5 = new javax.swing.JLabel();
        jl2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jrbUseCache = new javax.swing.JRadioButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Etosha WikiExplorer :: tools for quantitative relevance studies");

        jLabel1.setText("Project : ");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CN lang", "CN pagename", "reload", "class"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
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
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
        }

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jScrollPane2.setViewportView(jTextArea1);

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

        buttonGroup1.add(jrbSEQ);
        jrbSEQ.setText("SEQ-files");

        buttonGroup1.add(jrbXML);
        jrbXML.setSelected(true);
        jrbXML.setText("XML-files");

        jButton11.setText("validate");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton16.setText("import clean list");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        datePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("time range"));
        datePanel.setLayout(new java.awt.GridLayout(2, 1));

        jButton17.setText("import category");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("import page links");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("manage CN list"));

        jButton3.setText("+");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        selectionTF.setText("0");

        jButton6.setText("-");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton19.setText("clear");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        crawlMode.setText("crawl mode");
        crawlMode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                crawlModeFocusLost(evt);
            }
        });

        jCheckBox1.setText("run on cluster");

        jCheckBox2.setText("load text");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(selectionTF, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jButton19)
                .addGap(176, 176, 176)
                .addComponent(crawlMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(selectionTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(jButton19)
                    .addComponent(crawlMode)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("job details"));

        jl3.setText("...");

        jl4.setText("...");

        jl1.setText("...");

        jl5.setText("...");

        jl2.setText("...");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jl3, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jl4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jl1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jl5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jl2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jl3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jl4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jl1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jl5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jl2))
        );

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jTabbedPane1.addTab("logs", jScrollPane3);

        jButton1.setText("load full corpus & network");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton7.setText("merge CN files");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("extract Access-TS (MR)");
        jButton8.setToolTipText("run MR on cluster");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton10.setText("extract Edit-ES (WikiAPI)");
        jButton10.setToolTipText("run locally in some Threads ...");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton13.setText("reload partial corpus & network");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jrbUseCache.setText("use TSCache");
        jrbUseCache.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbUseCacheActionPerformed(evt);
            }
        });

        jButton20.setText("load second level networks");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText("check representation of topic in UN languages ...");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                        .addComponent(jrbUseCache)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton21)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton13)
                    .addComponent(jButton7)
                    .addComponent(jrbUseCache))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton21))
        );

        jTabbedPane1.addTab("manage data", jPanel4);

        jButton12.setText("create Representation-Plot");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(585, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("analysis", jPanel2);

        jButton14.setText("calc core density");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton9.setText("analyze Access-TS");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton15.setText("check stock data");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton15)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton9)
                    .addComponent(jButton14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("experimental", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(datePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTabbedPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1262, 1262, 1262)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jtfStudie, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jrbXML)
                                    .addComponent(jrbSEQ))))
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(32, 32, 32)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(96, 96, 96))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtfStudie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton2)
                    .addComponent(jButton17)
                    .addComponent(jButton11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton18)
                            .addComponent(jrbSEQ))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton16)
                            .addComponent(jrbXML))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        newStudie();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        saveStudie();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        loadStudieMetaData();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        Vector v = new Vector();
        v.add("");
        v.add("");
        dtm.addRow(v);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        ExtractIWLinkCorpus.crawlMode = this.crawlMode.isSelected();

        javax.swing.JOptionPane.showMessageDialog(this, "Extraction of all CN node neighbors will last a long time." + ExtractIWLinkCorpus.crawlMode);

        WikiStudieMetaData sdm = initMetaData();

        String[] w = new String[sdm.wn.size()];
        String[] p = new String[sdm.wn.size()];

        int i = 0;
        for (WikiNode wn : sdm.wn) {
            w[i] = wn.wiki;
            p[i] = wn.page;
            i++;
        }

        try {

            int fm = 0;

            if (jrbXML.isSelected()) {
                fm = Corpus.mode_XML;
            } else if (jrbSEQ.isSelected()) {
                fm = Corpus.mode_SEQ;
            }

            File folder = TSB.getAspectFolder( "corpus.crawl.log", sdm.getName() );
            
            
            // hier werden die Studien-Resultate gesammelt
            FileWriter fw = new FileWriter(new File( folder + "/result_" + sdm.name + ".dat"));

            boolean loadText = this.jCheckBox2.isSelected();
            ExtractIWLinkCorpus.runFromGUITool(sdm.name, w, p, loadText, this.jCheckBox1.isSelected(), fw, fm);

        } catch (Exception ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }

        mergeListFiles(sdm);

    }//GEN-LAST:event_jButton1ActionPerformed
    int selection = 0;

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        selection = Integer.parseInt(selectionTF.getText());
        System.out.println(selection - 1);
        dtm.removeRow(selection - 1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        // TODO add your handling code here:

        int row = jTable1.getSelectedRow();
        selectionTF.setText("" + (row));

    }//GEN-LAST:event_jTable1PropertyChange

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        WikiStudieMetaData sdm = initMetaData();
        mergeListFiles(sdm);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // SUBMIT
        WikiStudieMetaData sdm = initMetaData();
        mergeListFilesAndSubmit(sdm);

    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        openExtractedResults();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        
       
        
        this.jProgressBar1.setValue(0);
        this.jProgressBar1.repaint();
        WikiStudieMetaData sdm = initMetaData();
        try {
            testWikipages(sdm, this.jProgressBar1);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    /**
     * 
     * Run the Representation-Plot-Analysis
     */
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        
        WikiStudieMetaData sdm = initMetaData();
        CNResultManager2 rm = null;
       
        try {
        
            RepresentationPlotTool.createTextStatisticFile( sdm , rm );
            
            // CorpusStatisticsMain2 is used from within RepresentationPlotTool
        
        } 
        catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        WikiStudieMetaData sdm = initMetaData();
        extractEditEventTS(sdm);
        javax.swing.JOptionPane.showMessageDialog(rootPane, "Collected Edit history for " + sdm.wn.size() +  " pages." );
    }//GEN-LAST:event_jButton10ActionPerformed

    private void crawlModeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_crawlModeFocusLost
        // TODO add your handling code here:
        ExtractIWLinkCorpus.crawlMode = this.crawlMode.isSelected();
    }//GEN-LAST:event_crawlModeFocusLost

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        // this is the reload button ...
        
        ExtractIWLinkCorpus.crawlMode = true;

        javax.swing.JOptionPane.showMessageDialog(this, "We reload only the selected pages." + ExtractIWLinkCorpus.crawlMode);

        WikiStudieMetaData sdm = initMetaData();

        String[] w = new String[sdm.wnRELOAD.size()];
        String[] p = new String[sdm.wnRELOAD.size()];

        int i = 0;
        for (WikiNode wn : sdm.wnRELOAD) {
            w[i] = wn.wiki;
            p[i] = wn.page;
            i++;
        }

        try {

            int fm = 0;

            if (jrbXML.isSelected()) {
                fm = Corpus.mode_XML;
            } else if (jrbSEQ.isSelected()) {
                fm = Corpus.mode_SEQ;
            }
            
            File folder = new File( WEConstants.corpuspath );
            if ( !folder.exists() ) folder.mkdir();
            
            // hier werden die Studien-Resultate gesammelt
            FileWriter fw = new FileWriter(new File( folder + "/result_" + sdm.name + ".dat"));

            ExtractIWLinkCorpus.runFromGUITool(sdm.name, w, p, false, this.jCheckBox1.isSelected(), fw, fm);

        } catch (Exception ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // javax.swing.JOptionPane.showMessageDialog(this, "We merge list files now." + ExtractIWLinkCorpus.crawlMode);
        // mergeListFiles(sdm);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        WikiStudieMetaData sdm = initMetaData();

        File fP = new File( WEConstants.baspath );
        if ( !fP.exists() )
            fP.mkdir();
        
        File f = new File( fP.getAbsolutePath() + "/core_dens_" + sdm.name.trim() + ".csv");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            bw.write("#\n# wikipedia\tpn\tn\tz\tSUMIWL\trho_CORE\n#\n");

            for (WikiNode wn : sdm.getWn()) {
                CoreDensiteyCalculator.loadCoreDensity(wn.wiki, wn.page, bw);

            }
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(">>> RESULT: " + WEConstants.baspath + "/core_dens_" + sdm.name.trim() + ".csv");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        BufferedReader br = null;
        File file = null;
        String content = "?";

        try {
            // TODO add your handling code here:
            File f1 = new File( WEConstants.baspath );
            JFileChooser jfc = new JFileChooser();
            jfc.setSelectedFile(f1);
            jfc.showOpenDialog(this);

            file = jfc.getSelectedFile();

            // SELECT FILENAME
            String fn = file.getAbsolutePath();
            br = new BufferedReader(new FileReader(file));
            javax.swing.JOptionPane.showMessageDialog(this, "Import file: " + fn + "(" + file.canRead() + ")");
            String lineOne = br.readLine();
            String lang = lineOne.trim();

            // for all non comment-lines .... 
            while (br.ready()) {
                String pagename = br.readLine().trim();

//                Wiki wiki = new Wiki(lang + ".wikipedia.org");
//                String content = wiki.getPageText(pagename);

                System.out.println(pagename + " : \n\t" + content);

                // add to the table 
                DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
                Vector v = new Vector();
                v.add(lang);
                v.add(pagename);
                dtm.addRow(v);
                this.jTable1.setModel(dtm);
            };
            // READY.
            br.close();

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed


        try {

            String lineOne = javax.swing.JOptionPane.showInputDialog("Language code:", "de");
            String lang = lineOne.trim();

            String lineTwo = javax.swing.JOptionPane.showInputDialog("Kategory pagename:");
            String page = lineTwo.trim();

            WikiNode wn = new WikiNode(lang, page);
            WikiORIGINAL wiki = new WikiORIGINAL(lang + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

//  HashMap<String, String> iwls = wiki.getInterwikiLinks(wn.page);
//
//System.out.println( iwls.size()); 



//            for (String key : UNLANG) {
//                if ( key.equals( lang ) ) loadCatMembers(key, page);
//                else loadCatMembers(key, iwls.get(key));
//            }

            loadCatMembers(lang, page );

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        
        try {

            String lineOne = javax.swing.JOptionPane.showInputDialog("Lang-Code:", "de");
            String lang = lineOne.trim();

            String lineTwo = javax.swing.JOptionPane.showInputDialog("Page:");
            String page = lineTwo.trim();

            WikiNode wn = new WikiNode(lang, page);
            WikiORIGINAL wiki = new WikiORIGINAL(lang + ".wikipedia.org"); // create a new wiki connection to en.wikipedia.org

//  HashMap<String, String> iwls = wiki.getInterwikiLinks(wn.page);
//
//System.out.println( iwls.size()); 



//            for (String key : UNLANG) {
//                if ( key.equals( lang ) ) loadCatMembers(key, page);
//                else loadCatMembers(key, iwls.get(key));
//            }

            loadPageMembers(lang, page );

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.getDataVector().removeAllElements();
        repaint();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jrbUseCacheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbUseCacheActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jrbUseCacheActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed

        javax.swing.JOptionPane.showMessageDialog(this, "We reload only data for the selected pages." );

        WikiStudieMetaData sdm = initMetaData();

        String[] w = new String[sdm.wnRELOAD.size()];
        String[] p = new String[sdm.wnRELOAD.size()];

        int i = 0;
        for (WikiNode wn : sdm.wnRELOAD) {
            w[i] = wn.wiki;
            p[i] = wn.page;
            i++;
        } 
        
       String studie = jtfStudie.getText();
       
       System.out.println(">>> Studie: " + studie + " # Load second-level-neighbors for one CN only!  " );
 
        
        try {
            ExtractIWLinkAdvanced.runFromGUITool(studie, w, p );
        } 
        catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed

        try {
            // NOT READY --- can fail ...
            this._checkForUNLangPages(null, null);
        } 
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }//GEN-LAST:event_jButton21ActionPerformed

    
   static WikipediaCorpusLoaderTool f = null;
    
    
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                f = WikipediaCorpusLoaderTool.getCNIFRAME();
                ExtractIWLinkCorpus.crawlMode = true;
                f.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox crawlMode;
    private javax.swing.JPanel datePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JLabel jl1;
    private javax.swing.JLabel jl2;
    private javax.swing.JLabel jl3;
    private javax.swing.JLabel jl4;
    private javax.swing.JLabel jl5;
    private javax.swing.JRadioButton jrbSEQ;
    private javax.swing.JRadioButton jrbUseCache;
    private javax.swing.JRadioButton jrbXML;
    private javax.swing.JTextField jtfStudie;
    private javax.swing.JTextField selectionTF;
    // End of variables declaration//GEN-END:variables

    private void loadStudieMetaData() {
        File f1 = new File("/Volumes/MyExternalDrive/CALCULATIONS/Wikipedia");
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(f1);
        jfc.showOpenDialog(this);

        File f = jfc.getSelectedFile();
        try {
            WikiStudieMetaData smd = WikiStudieMetaData.load(f);
            initSMD(smd);
        } 
        catch (FileNotFoundException ex) {
        }
        
    }
    

    private void initSMD(WikiStudieMetaData smd) {

        this.jtfStudie.setText(smd.name);

        this.jTextArea1.setText(smd.description);

        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();

        int i = dtm.getRowCount();
        for (int j = 0; j < i; j++) {
            dtm.removeRow(0);
        }

        for (WikiNode w : smd.wn) {
            Vector v = new Vector();
            v.add(w.wiki);
            v.add(w.page);
            dtm.addRow(v);
        }

        this.jTable1.setModel(dtm);
        
        this.initTimeRange(smd);
        
        
    }

    private void newStudie() {
        WikiStudieMetaData smd = WikiStudieMetaData.initStudie();
        initSMD(smd);
    }

    private void saveStudie() {

        WikiStudieMetaData sdm = initMetaData();

        File f1 = new File( WEConstants.baspath);
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(f1);

        jfc.showSaveDialog(this);

        File f = jfc.getSelectedFile();
        try {
            sdm.store(f, sdm);
        } catch (Exception ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        System.out.println( ">>> Saved study descriptor : " + f.getAbsolutePath());

    }

    private WikiStudieMetaData initMetaData() {

        WikiStudieMetaData sdm = WikiStudieMetaData.initStudie();

        sdm.name = this.jtfStudie.getText().trim();

        sdm.description = this.jTextArea1.getText();
        
        sdm.setDateFrom( vonP.getDate() );
        sdm.setDateTo( bisP.getDate() );

        sdm.resetReloadBuffer();

        TableModel tm = this.jTable1.getModel();
        for (int i = 0; i < tm.getRowCount(); i++) {
            String w = (String) tm.getValueAt(i, 0);
            String p = (String) tm.getValueAt(i, 1);
            Boolean b = (Boolean) tm.getValueAt(i, 2);

            if (b != null) {
                if (b) {
                    sdm.addForReload(w, p);
                }
            }

            sdm.addNewNode(w, p);
        }
        return sdm;
    }

    private void mergeListFiles(WikiStudieMetaData sdm2) {
        try {

            /**
             * This is the name of the studie ...
             */
            String netn = sdm2.name;

            if (netn.contains("/")) {
                netn = netn.replaceAll("/", "_");
            }
            if (netn.contains("'")) {
                netn = netn.replaceAll("'", "_");
            }

            /**
             * Location of the merged ListFile ...
             */
            File f = org.crunchts.store.TSB.getFolderForTimeSeriesMetadata();

            System.out.println(">>> Path to merged-list-file : " + f.getAbsolutePath());

            // merge listfiles now
            BufferedWriter bw = new BufferedWriter(new FileWriter( f.getAbsolutePath() + "/merged_listfile_" + netn + ".lst"));
            int j = 1;
            File in = null;
            for (WikiNode wn : sdm2.wn) {

                String page = wn.page;

                String cleanPage = FileNameFilter.encodePagenNameWebsave( page );
                
                

                try {

                    String filenameLabel = netn + "_" + wn.wiki + "_" + cleanPage + ".lst";
                    
                    in = TSB.getFileForTimeSeriesMetadata( "listfile", filenameLabel );
                    System.out.println( ">>> USE A LISTFILE : " + in + " : " + cleanPage );
                    
                    if (!in.canRead()) {
                        
                        // JFileChooser-Objekt erstellen
                        JFileChooser chooser = new JFileChooser();
                        chooser.setSelectedFile( TSB.getFolderForTimeSeriesMetadata() );
                        // Dialog zum Oeffnen von Dateien anzeigen
                        int rueckgabeWert = chooser.showDialog(null, in.getName() );

                        /* Abfrage, ob auf "Öffnen" geklickt wurde */
                        if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
                            // Ausgabe der ausgewaehlten Datei
                            in = chooser.getSelectedFile();
                        }

                    }



                    System.out.println("# " + in.getAbsolutePath());
                    BufferedReader br = new BufferedReader(new FileReader(in));
                    while (br.ready()) {
                        String line = br.readLine();
                        bw.write(j + "." + line + "\n");
                    }
                    bw.flush();
                    j++;
                } catch (Exception ex) {
                    System.out.println("ERROR !!! ******** >>> merged-list-file : " + in.getAbsolutePath());

                }
            }
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mergeListFilesAndSubmit(WikiStudieMetaData sdm2) {
        String netn = sdm2.name;
        if (netn.contains("/")) {
            netn = netn.replaceAll("/", "_");
        }
        if (netn.contains("'")) {
            netn = netn.replaceAll("'", "_");
        }

//        try {
//
//
//
//            // merge listfiles now
//            BufferedWriter bw = new BufferedWriter(new FileWriter("merged_listfile_" + netn + ".lst"));
//            javax.swing.JOptionPane.showMessageDialog(this, "Submit: " + "merged_listfile_" + netn + ".lst");
//            int j = 1;
//            for (WikiNode wn : sdm2.wn) {
//
//                String p = wn.page;
//
//p = FileNameFilter.cleanWikinameForFilename( p );
//
//
//                //  listfile_Congo_Examples_en_Hema_people.lst
//                File in = new File("listfile_" + netn + "_" + wn.wiki + "_" + p + ".lst");
//                BufferedReader br = new BufferedReader(new FileReader(in));
//                while (br.ready()) {
//                    String line = br.readLine();
//                    bw.write(j + "." + line + "\n");
//                }
//                bw.flush();
//                j++;
//            }
//            bw.flush();
//            bw.close();
//        } catch (IOException ex) {
//            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
//        }
        try {
//            ExtractIWLinkCorpus.submit(sdm2.name);
            ExtractIWLinkCorpus.submit(netn);
        } catch (IOException ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void openExtractedResults() {
        System.out.println(" SEQ-File Viewer hier öffnen ... ");
        // ist aber in höherem LAYER !!! => REFACTORING
    }

    private void initTableModel(String string, String[] _page, String name) {

        this.jtfStudie.setText(name);

        this.jTextArea1.setText("");

        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();

        int i = dtm.getRowCount();
        for (int j = 0; j < i; j++) {
            dtm.removeRow(0);
        }

        for (String s : _page) {
            Vector v = new Vector();
            v.add(string);
            v.add(s);
            dtm.addRow(v);
        }

        this.jTable1.setModel(dtm);


    }

    private void testWikipages(final WikiStudieMetaData sdm, javax.swing.JProgressBar jpb ) throws Exception {
        
      new Thread( new Runnable()
      {
        @Override public void run()
        {
            
            try {
           
            int errors = 0;
        
        FileWriter fw = new FileWriter( "check_" + sdm.name + ".log" );
        StringBuffer sb = new StringBuffer();
        
        int nrTotal = sdm.getWn().size();
        int i = 0;

        for (WikiNode wn : sdm.getWn()) {
            i++;
            try {
                
                WikiORIGINAL wiki1 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");
                WikiORIGINAL w2 = new WikiORIGINAL(wn.wiki + ".wikipedia.org");
                
                System.out.print("\n>[PAGE] : " + wn.page + "\n");

                String text = wiki1.getPageText(wn.page) + "\n";
                
                HashMap<String,String> iwl = w2.getInterWikiLinks(wn.page);
                
                 
                String iwlNR = "#IWL   : " + iwl.size() + "\n";
                
                String lNR = "#Links : " + wiki1.getLinksOnPage(wn.page).length+ "\n";

                String info = ">[PAGE] : " + wn.page + "\n";
                 info = info.concat("length (nr of characters): " + text.length()+ "\n");
                info = info.concat(iwlNR);
                info = info.concat(lNR);


                
                // javax.swing.JOptionPane.showMessageDialog(this, info);
                fw.write( info + "\n" );
                fw.flush();
                
                appendToLog(info + "\n");


                Boolean o = true;
                o = (Boolean) wiki1.getPageInfo(wn.page).get("exists");
                // System.out.println( o );
                if (!o) {
                    errors++;

                    System.out.println(wn.page + " NOT available");
                    sb.append( wn.page + " NOT available" + "\n" );
                    
                }
                progr = (int)(100.00 * (double)i/(double)nrTotal);
                SwingUtilities.invokeLater( new Runnable()
            {
              @Override public void run() {
                f.jProgressBar1.setValue((int) f.progr);
              }
            } );
                
            } catch (Exception ex) {
                Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getCause());
                errors++;

            }


        }
        System.out.println(">> " + errors + " errors detected.");
        
        appendToLog( ">> " + errors + " errors detected." );

        fw.write( ">> " + errors + " errors detected." + "\n");
        fw.write( sb.toString() );
        fw.close();
        }
        catch(Exception ex){ }
            
            
            

            
          
        }

       
      } ).start();
        
        
        
    }
    
    static double progr = 0.0;
    
    public static void appendToLog( String m ) {
        f.jTextArea2.append( m + "\n"  );
    };

    
    Calendar von = null;
    Calendar bis = null;

    private void extractEditEventTS(WikiStudieMetaData sdm) {
        
        System.out.println(">>> Extract Edit-Event-TS : " + sdm.name);

        initTimeRange();

        WikiHistoryExtraction2.setVon(von);
        WikiHistoryExtraction2.setBis(bis);

        System.out.println(">>>                   von : " + von.getTime().toString());
        System.out.println(">>>                   bis : " + bis.getTime().toString());

        boolean mode = true;
        try {
            
            WikiHistoryExtractionBASE.useTSCache = this.jrbUseCache.isSelected();
            
            String label = "EditEvents_" + von.getTime().getTime() + "_" + bis.getTime().getTime() + "_" + sdm.name;

            File folder = TSB.getAspectFolder( "editrate.tsb" , sdm.getName() );
        
            // only CN
            m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2.run(sdm.wn, von, bis, mode, folder.getAbsolutePath(), label );

            // also IWL
            m3.wikipedia.corpus.extractor.edits.WikiHistoryExtraction2._runWithIWLExpansionOfLists(sdm.wn, von, bis, mode, folder.getAbsolutePath(), label);

        } catch (Exception ex) {
            Logger.getLogger(WikipediaCorpusLoaderTool.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Load Date from UI
     */
    private void initTimeRange() {


        von.setTime(vonP.getDate());
        bis.setTime(bisP.getDate());

    }

    /**
     * set Data to UI...
     * 
     * @param sdm 
     */
    private void initTimeRange( WikiStudieMetaData sdm ) {


        try {
            vonP.setDate( sdm.getTimeRangeFrom() );
        }
        catch(Exception ex) {
            
        }
        
        try {
           bisP.setDate( sdm.getTimeRangeTo() );
        }
        catch(Exception ex) {
        
        }

    }
    
    DateTextField bisP = null;
    DateTextField vonP = null;

    private void initTimeFrame() {
        von = new GregorianCalendar();
        von.clear();
        von.set(2009, 0, 1, 0, 0);

        bis = new java.util.GregorianCalendar();
        bis.clear();
        bis.set(2009, 7, 28, 0, 1);

        vonP = new DateTextField("yyyy-MM-dd", von.getTime());
        bisP = new DateTextField("yyyy-MM-dd", bis.getTime());

        datePanel.add(vonP);
        datePanel.add(bisP);


    }
    Vector<String> UNLANG = new Vector<String>();

    private boolean _checkForUNLangPages(WikiNode wn, HashMap<String, String> iwl) throws IOException, Exception {
        
        boolean b = true;
        UNLANG = new Vector<String>();
        UNLANG.add("en");
        UNLANG.add("fr");
        UNLANG.add("ru");
        UNLANG.add("es");
        UNLANG.add("de");
        UNLANG.add("sv");


        WikiORIGINAL wiki = new WikiORIGINAL( wn.wiki+".wikipedia.org" );
//        System.out.println( wiki.toString() );
//        System.out.println( wn.toString() );
//        
//        System.out.println( iwl.size() );
        
        for( String k : iwl.keySet() ) { 
            System.out.println( k  + " ### " + iwl.get(k) );
        }

        for (String lang : UNLANG) {
            if ( lang.equals( wn.wiki )) b = true;
            else b = b && iwl.keySet().contains(lang);
            System.out.println(">>> " + lang + " " + b + " " + iwl.keySet().contains(lang));
        }

        return b;
    }

    private void loadPageMembers(String lang, String page) throws IOException {
        
        WikiORIGINAL wiki = new WikiORIGINAL( lang+".wikipedia.org" );
        System.out.println("a " + lang);
        System.out.println("b " + page);
        
        String[] map = wiki.getLinksOnPage(page);

        String s = ">>> " + wiki + "\n> # of links on page: ("+ lang + ")" + page + " # " + map.length;
        System.out.println(s);
        
        for (String pagename : map) {

            WikiNode wn2 = new WikiNode(lang, pagename);

            // add to the table 
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            Vector v = new Vector();
            v.add(lang);
            v.add(pagename);
            v.add(true);
            dtm.addRow(v);
            this.jTable1.setModel(dtm);
        };

        addImportLog( s );


    }
    private void loadCatMembers(String lang, String page) throws IOException {
        
        WikiORIGINAL wiki = new WikiORIGINAL( lang+".wikipedia.org" );
        System.out.println(">>>   loadCatMembers() " );
        System.out.println("  arg1=" + lang);
        System.out.println("  arg2=" + page);
        
        String[] map = wiki.getCategoryMembers(page);
        
        String s = ">>> " + wiki + "\n" +
                   ">>> category: " + page + "\n> # of category members: ("+ lang + ") = " + 
                   map.length;
                
        System.out.println(s);

        for (String pagename : map) {

            WikiNode wn2 = new WikiNode(lang, pagename);

            // add to the table 
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            Vector v = new Vector();
            v.add(lang);
            v.add(pagename);
            v.add(true);
            dtm.addRow(v);
            this.jTable1.setModel(dtm);
        }         
        addImportLog( s );
    }

    private void addImportLog(String s) {
        String s2 = this.jTextArea1.getText() + "\n" + s;
        this.jTextArea1.setText( s2 );
    }
}
