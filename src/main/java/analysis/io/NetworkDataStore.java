package analysis.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author kamir
 */
public class NetworkDataStore {
    
    String pathBase = "P:/DATA/";
            
    public NetworkDataStore( String _pathBase ) { 
        pathBase = _pathBase;
    }
        
    public NetworkDataStore() { 
    }
    
    public File getAccessCCResultFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/LOG_Datei" + analyseLabel + ".dat" );
    }
    public File getEditESResultFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/editCorrelationResult.dat" );
    }
    public File getNGFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/ng_" + analyseLabel + ".dat" );
    }
    public File getNodeGroupStatisticsFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/"+ analyseLabel +"_node_group_statistics.dat" );
    }

    public File getMergedStatPropsFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/mergedNodeGroupStatistics.dat" );
    }
    public File getKStatPropsFile( String analyseLabel ) { 
        return new File( pathBase + "/" + analyseLabel + "/node_group_k_statistics.dat" );
    }
    public File getStatNet(String analyseLabel) {
        return new File( pathBase + "/" + analyseLabel + "/stat_net_"+ analyseLabel+".dat" );
    }

    Properties getProperties(String analyseLabel) throws IOException {
        String fnprop =  pathBase + "/" + analyseLabel + "/info.properties";
        Properties props = new Properties();
        props.load( new FileReader( fnprop ) );
        return props;
    }

    
}
