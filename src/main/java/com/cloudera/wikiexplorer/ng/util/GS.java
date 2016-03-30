/*
 * General System Setup Class
 *
 */

package com.cloudera.wikiexplorer.ng.util;

import java.io.File;

/**
 *
 * @author kamir
 */
public class GS {

    /*
     * There everything comes in
     */
    public static String BASE_FOLDER_IN = "./in";

        /*
     * There everything goes out
     */
    public static String BASE_FOLDER_OUT = "./out";

    /**
     * Where are GNU-Plot data stored?
     */
    public static String GNU_PLOT_DATA_BASE = "G:/PHYSICS/GNUPLOT/";

    public static String getPathForGnuplotProject( String projectname ) throws Exception {
        String fn = GNU_PLOT_DATA_BASE + projectname;
        File f = new File( fn );
        if ( !f.exists() ) {
            try {
                f.mkdirs();
            }
            catch( Exception ex) { 
                throw new Exception("To the project: [" + fn +"] file can not be written!" );        
            }
        }
        return f.getAbsolutePath() + File.separator;
    };
    

}
