package com.cloudera.wikiexplorer.ng.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author kamir
 */
public class CodePagenamePageIDMapper {

    public static void main( String[] args ) throws FileNotFoundException, IOException {

        // input of two maps
        File fA = new File( "G:/Downloads/sup500mapping.txt.txt" );
        BufferedReader brA = new BufferedReader( new FileReader( fA ));

        File fB = new File( "in/list_sup500.dat.ids_and_names - Sheet 1.tsv" );
        BufferedReader brB = new BufferedReader( new FileReader( fB ));

        if ( !( fA.canRead() && fB.canRead() ) ) { 
            System.exit( 0 );
        }

        int i = 0;
        Hashtable<String,String> codeName = new Hashtable<String,String>();
        while ( brA.ready() ) {
            String code = brA.readLine();
            String name = brA.readLine();

            name = name.replace(" ", "_" );
            codeName.put(name, code);

            System.out.println( "(" + i + ") [" + code + "] = [" + name +"]");

            i++;
        }

                  
                    
        // output of mapped Liste    1 CODE -> n PageIDs
        File fC = new File( "out/sup500_mapper.dat" );
        FileWriter fw = new FileWriter( fC );

        int j = 0;
        while ( brB.ready() ) {
            String line = brB.readLine();

            String[] tokens = line.split("\t");

            String code = codeName.get( tokens[1] );
            String pageID = tokens[0];

            System.out.println( "{"+j+"} " + pageID + "\t" + code  );

            fw.write( pageID + "\t" + code +"\n" );

            j++;
        }

     

        fw.close();

    }

}
