package bigdata.explorer.views.xmleditor;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * http://java-sl.com/downloads.html
 * 
 * @author kamir
 */
public class XMLViewer extends JFrame {
    JEditorPane edit = new JEditorPane();
    public static String testXML="";
    
    public XMLViewer() {
        super("XML View");
    }
    
    public static void open( String fn ) { 
        XMLViewer m = new XMLViewer( fn );
        m.setVisible(true);
    }

    public static void main(String[] args) {
        XMLViewer m = new XMLViewer( "/etc/hadoop/conf/core-site.xml");
        m.setVisible(true);
    }
    
    String fn = null;
    private XMLViewer(String _fn) {
        super("XML View");
        fn = _fn;
        setTitle( "XML View [" + fn + "]" );
        try {
            initContent( fn );
        } 
        catch (Exception ex) {
            Logger.getLogger(XMLViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    String text = null;
    private void initContent(String fn) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader( new FileReader( fn ) );
        StringBuffer sb = new StringBuffer();
        while( br.ready() ) { 
            sb.append( br.readLine() );
        }
        text = sb.toString();
        
        testXML = text;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        edit.setEditorKit(new XMLEditorKit());
        edit.setText(testXML);
//        edit.setEditable(false);

        this.getContentPane().add(new JScrollPane(edit));

        this.setSize(620, 450);
        this.setLocationRelativeTo(null);
    }
}
