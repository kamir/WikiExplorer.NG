/*
 *
 *
 */
package research.topics.networks.comparison;

import org.apache.hadoopts.data.io.ColumnValueCalculator;
/**
 *
 * @author kamir
 */
public class CC_Media_ResultMapper implements ColumnValueCalculator {
  
    // Calculation modes ...
    public int cc = 1;                 // $80
    public int stdev = 2;              // $81
    public int cc_ST = 3;              // $83
    public int stdev_ST = 4;               // $84
    public int min_Z_access = 5;       // ...
    public int ratio_Z_access = 6;     // ...

    @Override
    public double getValue(int mode, String[] line) {
        double v = 0.0;
        switch ( mode ) { 
            case 1 : { 
                v = Double.parseDouble( line[79] );
                break;                
            }
            case 2 : { 
                v = Double.parseDouble( line[80] );
                break;                
            }
            case 3 : { 
                v = Double.parseDouble( line[82] );
                break;                
            }
            case 4 : { 
                v = Double.parseDouble( line[83] );
                break;                
            }
            case 5 : { 
                break;                
            }
            case 6 : { 
                break;                
            }    
        }
        return v;
    }
    
    public double calcLinkStrength( String[] line ) { 
        double v = 0.0;
        
        double v1 = getValue( 1 , line);
        double v2 = getValue( 4 , line);
        
        return v1 / v2;
    };

    @Override
    public String getName() {
        return "CC_Media_ResultMapper";
    }

 
}
