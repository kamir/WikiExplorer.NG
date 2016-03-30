package research.sqlclient.phase2;

/**
 *
 * @author kamir
 */
public class Topic2Runner {
    
    public static void main(String args[]) throws Exception {
        int[] ofsets = { 0, 12, 24, 36  };
//         int[] ofsets = { 48, 60, 72, 2, 6  };
        for( int i : ofsets ) { 
            args = new String[1];
            args[0] = ""+i;
            Topic2.main( args );
        }
    }
    
}
