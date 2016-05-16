/*
 * Copyright 2016 kamir.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudera.wikiexplorer.ng.app;

import java.io.File;

/**
 *
 * @author kamir
 */
public class DataSetGroup {
    
    public static void main( String[] ARGS ) {
    
        File fSeries = new File( "/ETOSHA.WS/TSDB/raw/wikipedia/corpus" );
        
        File[] fDS = fSeries.listFiles();
        
        for(File f : fDS ) {
            
            if ( f.isDirectory() )
                getMetadataFor( f );
                
        
        }
        
    }

    /**
     * For a DataFolder with TS-Bucket data we try to locate the 
     * location of MD file.
     * 
     * @param f
     * @return 
     */
    private static String getMetadataFor(File f) {
    
        File fMetadata = new File( "/ETOSHA.WS/TSDB/raw/wikipedia/metadata" );
        
        
        String fn = f.getName();
        int l = fn.length();
        int max = l-9;
        
        String sfn = "?";
        
        try {
        
            sfn = fn.substring(0,max);
        
            File mdf = new File( "/ETOSHA.WS/TSDB/raw/wikipedia/metadata/merged_listfile_" + sfn + ".lst");
            
            boolean exist = mdf.exists();
            
            sfn = exist + " \n\t" + mdf.getAbsolutePath();
            
            if( exist ) System.out.println( mdf.getAbsolutePath() );
            
        }
        catch(Exception eX) {
            sfn = "{"+ fn +"}";
        }
        
        return sfn;
    }
    
}
