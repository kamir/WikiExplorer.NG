/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cloudera.wikiexplorer.ng.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author kamir
 */
public class NodeGroupFileTool {

    /** Lesen aus der größeren Datei und kopieren in die kürzere */
    static public void extractIdsToNewFile( String pfad,
            String filenameBIG,
            String filenameSMALL , int limit) throws IOException {

        FileReader fr = new FileReader( pfad + filenameBIG);
        FileWriter fw = new FileWriter( pfad + filenameSMALL);

        BufferedWriter bw = new BufferedWriter( fw );
        BufferedReader br = new BufferedReader(fr);
        int counter = 0;
        int id = 0;
        boolean noBreak = true;
        while (br.ready() && noBreak) {

            String line = br.readLine();

            if (!(line.startsWith("#"))) {
                // System.out.println(line);
                StringTokenizer st = new StringTokenizer(line);
                counter++;
                id = Integer.parseInt((String) st.nextElement());
                if ( counter == limit ) {
                    noBreak = false;
                }
                fw.write(line + "\n" );

            }
        }
        br.close();
        bw.close();

    };

    public static void main( String[] args ) throws IOException {
        String path = NodeGroup.pfad;
        String fnB = "62_1000_most_active_by_access.dat";
        String fnS = "62_100_most_active_by_access.dat";
        NodeGroupFileTool.extractIdsToNewFile( path , fnB, fnS, 100 );

    };

}
