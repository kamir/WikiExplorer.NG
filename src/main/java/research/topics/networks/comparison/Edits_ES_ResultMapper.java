/*
 *
 *
 */
package research.topics.networks.comparison;

import org.apache.hadoopts.data.io.ColumnValueCalculator;
import java.math.BigDecimal;

/**
 *
 * @author kamir
 */
public class Edits_ES_ResultMapper implements ColumnValueCalculator {
  
    // Calculation modes ...
    public int min_z_edits = 1;     // min($3,$4)
    public int ratio_z_edits = 2;   // max($3,$4) / min($3,$4)
    public int q_by_Q = 3;          // $7 / $5
    public int Q = 4;               // $5
    public int q_by_Q_ST = 5;       // $8 / $6
    public int Q_ST = 6;            // $6

    @Override
    public double getValue(int mode, String[] line) {
        double v = 0.0;
        switch ( mode ) { 
            case 1 : { 
                
                
                
                break;                
            }
            case 2 : { 
                
                
                break;                
            }
            case 3 : { 
                v = getDouble( line[6] ) / getDouble( line[4] );
                break;                
            }
            case 4 : { 
                v = getDouble( line[4] );
                break;                
            }
            case 5 : { 
                v = getDouble( line[7] ) / getDouble( line[5] );
                break;                
            }
            case 6 : { 
                v = getDouble( line[5] );
                break;                
            }    
        }
        return v;
    }
    
    double getDouble( String s ) { 
        // System.out.println(" s= " + s );
        BigDecimal bd = new BigDecimal( s );
        double val = bd.longValue();
        return val;
    }

    @Override
    public double calcLinkStrength( String[] line) {
        double v = 0.0;
        
        double v1 = getValue( 4 , line);
        double v2 = getValue( 5 , line);
        double q = v2 * v1;
        double f = 0.7 / Math.pow( (5.0 + q) , 0.6 );
        
        return v1 / f;
    }

    @Override
    public String getName() {
        return "Edits_ES_ResultMapper";
    }
    
    
}
