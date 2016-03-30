/*
 *  Ein Daten-Container, in dem Messreihen abgelegt werden,
 *  die dann direkt in einem Excel-File gespeichert werden.
 * 
 */
package com.cloudera.wikiexplorer.ng.util.io;

import org.apache.hadoopts.data.series.Messreihe;
import org.apache.hadoopts.data.export.MesswertTabelle;
import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author kamir
 */
public class ExcelProject {
    
    /**
     * creates an lokal Folder [./excel_data]
     */
    public ExcelProject() { 
        folderName = "./excel_data";
        initFolder( folderName );
        initBook();
        initSheets();
    }
    
    /**
     * creates the folder fn
     * 
     * @param fn 
     */
    public ExcelProject(String fn) { 
        folderName = fn;
        initFolder( folderName );
        initBook();
        initSheets();
    }
    
    Workbook wb = null;
    Hashtable<String,Workbook> sheets = null;
    
    public void initSheets() { 
        sheets = new Hashtable<String,Workbook>();
    }
    
    public void initBook() { 
        wb = new HSSFWorkbook();
    }
    
    /**
     * absoluter Name im File-System
     */
    public String folderName = null;
    
    public void initFolder(String folder) { 
        if ( folder == null ) {
            javax.swing.JFileChooser jfc = new javax.swing.JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();  
            //File p = f.getParentFile();
            folderName = f.getAbsolutePath();
        }
        else { 
            folderName = folder;
        }
        System.out.println(">>> Excel-Project: " +
                           "    Folder: " + folderName );
        File f = new File( folderName );
        f.mkdirs();
    }
    
    public Messreihe[] sortRosByLabel_INTEGER( Messreihe[] raw ) { 
        
        Hashtable keyedHash = new Hashtable<Integer,Messreihe>();
        for( Messreihe m : raw ) { 
            if ( m == null ) m = new Messreihe("empty");
            Integer i = new Integer( m.getLabel() );
            keyedHash.put( i , m);
            System.out.println("KEY: " + i );
        }   
 
        int maxLength = 0;
        for( Messreihe m : raw ) {
            int l = m.yValues.size();
            if ( l > maxLength ) maxLength = l;
        }
      
        Set<Integer> s = keyedHash.keySet();
        List l = new ArrayList();
        for( Integer lab: s) { 
            l.add( lab );
        };
        
        System.out.println( l );
        Collections.sort( l ); 
        System.out.println( l );
        
        Messreihe[] sorted = new Messreihe[ raw.length ];
        Iterator it = l.iterator();
        int i = 0;
        while( it.hasNext() ) { 
            Integer ki = (Integer)it.next();
            Messreihe m = (Messreihe)keyedHash.get( ki  );
            sorted[i] = m;
            i++;
        }
        
        return sorted;
    
    }

    
    
    public void addMessreihen( Messreihe[][] mrv2, String pre) {
        int i = mrv2[0].length;
        int j = mrv2.length;
        
        for( int a=0; a<j; a++ ) { 
            Vector<Messreihe> r = new Vector<Messreihe>();
            for( int b=0; b<i; b++ ) {
                r.add( mrv2[a][b]);
            }    
            addMessreihen(r, pre+"_"+a+"_", true);
        }    
    }
    
    public void storeNow( String filename ) throws FileNotFoundException, IOException { 
        String file = this.folderName + File.separator + filename + ".xls"; 
        System.out.println( "STORE:" + file );
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
    };
    
    public void addMessreihenToBook( String sheetName, Messreihe[] mrv2, String[] labels) {
        int j = mrv2.length;
        
        Sheet sheet = wb.createSheet( sheetName );
                
        Row r = sheet.createRow(0);
        for( int a=0; a<j; a++ ) { 
            r.createCell(a+1).setCellValue( labels[a] );
        }
        
        for( int a=0; a<j; a++ ) { 
            Messreihe m = mrv2[a];
            int lines = m.xValues.size();
            for( int line = 0; line < lines; line++ ) { 
                r = sheet.getRow(line + 1);
                if ( r == null ) r = sheet.createRow(line+1);
                if ( a == 0) r.createCell(a*2).setCellValue( (Double)m.xValues.elementAt(line) );
                r.createCell(a+1).setCellValue( (Double)m.yValues.elementAt(line) );
            }
        }    
    }

        
    public void addMessreihen( Messreihe[] mrv2, String pre) {
        Vector<Messreihe> rows = new Vector<Messreihe>();
        for( Messreihe mr : mrv2) {
            rows.add(mr);
        }
        addMessreihen(rows, pre, true);
     }
    
     public void addMessreihen(Vector<Messreihe> mrv2, String prefix, boolean writeTab ) {
        if ( writeTab) { 
            MesswertTabelle tab = new MesswertTabelle("DEMO");
            tab.setLabel(folderName + File.separator + "tab_"+prefix+".dat");
            tab.setMessReihen(mrv2);
            tab.writeToFile();
        }    
            
        for( Messreihe mr : mrv2) {
            File f = new File( folderName + File.separator + prefix + "_" + mr.getFileName() );
            mr.writeToFile( f , '.' );            
        }
    }

    Hashtable<String, FileWriter> logger = new Hashtable<String, FileWriter>();
    public void createLogFile(String s) throws IOException {
        File f = new File(this.folderName + File.separator + s);
        
        if ( !f.getParentFile().exists() ) {
            f.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter( f.getAbsolutePath() );
        logger.put(s, fw);
    }
    
    public void logLine( String s, String line ) throws IOException { 
        FileWriter fw = logger.get(s);
        fw.write(line);
    };
    
    public void closeAllWriter() throws IOException { 
        for( FileWriter fw: logger.values() ) {
            fw.flush();
            fw.close();
        } 
    }

    public void storeMesswertTabelle(MesswertTabelle mwt) {
        File f = new File(this.folderName + File.separator + mwt.getLabel() );
        mwt.writeToFile( f );
    }
    
    
    
    
    
     public static void writeToNewTable(MesswertTabelle tabs, String[] headers ) throws Exception {
        
        Workbook wb = new HSSFWorkbook();
        
        Sheet sheet = wb.createSheet( tabs.getLabel() );
        
        sheet.setPrintGridlines(true);
        sheet.setDisplayGridlines(true);

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        sheet.setColumnWidth(0, 3*256);
        sheet.setColumnWidth(1, 3*256);
        sheet.setColumnWidth(2, 11*256);
        sheet.setColumnWidth(3, 14*256);
        sheet.setColumnWidth(4, 14*256);
        sheet.setColumnWidth(5, 14*256);
        sheet.setColumnWidth(6, 14*256);
         
 
        double[][] data = tabs.getDataArray();
        int lines = tabs.getNrLines();
        int cols = tabs.getNrCols();
        
        int z = 0;
        for (int i = 0; i < headers.length; i++) {
             Row r = sheet.createRow(i);
             r.getCell(0).setCellValue( headers[i] );
             z++;
        }
        
        for( int j = 0; j < lines; j++ ) { 
            Row r = sheet.createRow(z);
            for( int k = 0; k < cols; k++ ) { 
                r.getCell(k).setCellValue( "A"+k );
            }
        }
        
        
 
 
        // Write the output to a file
        String file = "loan-calculator.xls";
        if(wb instanceof XSSFWorkbook) file += "x";
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
    }

    public void storeMatrix(double[][] layer, String s1, int x, int y) throws FileNotFoundException, IOException {
        System.out.println( layer );
        Workbook wb = new HSSFWorkbook();
        
        Sheet sheet = wb.createSheet( s1 );
 
        for( int j = 0; j < x; j++ ) { 
            Row r = sheet.createRow(j);

            for( int k = 0; k < y; k++ ) { 
                Cell c = r.createCell(k);
                c.setCellValue( layer[j][k] );
            }
        }        
        
        // Write the output to a file
        String file = s1 + ".xls"; 
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
    }

   

     
    
    
}
