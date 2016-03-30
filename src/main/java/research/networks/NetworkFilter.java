package research.networks;



import org.apache.hadoopts.data.series.Messreihe;

public class NetworkFilter { 

    public double ts = 0.5;
    
    public NetworkFilter( double _ts ) {
        ts = _ts;
    };
    
    public Messreihe[] applyFilter( Messreihe mr ) { 
        
        Messreihe[] mrs = new Messreihe[3];
        mrs[0] = mr;
        
        Messreihe used = new Messreihe();
        Messreihe unused = new Messreihe();
       
        for( Object key : mr.hashedValues.keySet() ) { 
            double value = (Double)mr.hashedValues.get(key);
            if( use( value ) ){ 
                used.addValue(value, keyArray( key ) );
                unused.addValue(0.0, keyArray( key ) );
            }
            else { 
                unused.addValue(value, keyArray( key ) );
                used.addValue(0.0, keyArray( key ) );
            }
        }
        
        mrs[1] = used;
        mrs[2] = unused;
        
        return mrs;
    }

    private String[] keyArray(Object o) {
        String key = (String)o;
        return key.split("_");
    }

    private boolean use(double value) {
        // System.out.println( "use: " + value + " :: " + ( ts < value ) );
        return ( ts < value );        
    }

}
