package research.sqlclient.phase2;

import org.apache.hadoopts.data.series.TimeSeriesObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessreiheTester {

    public static void main( String[] args) {
        try {
            TimeSeriesObject a = new TimeSeriesObject();
            a.setLabel("Reihe_a");
            for (int i = 0; i < 15; i++) {
                a.addValuePair(i, 7 + i % 5);
            }
            ;
            System.out.println(a);
            TimeSeriesObject b = a.setBinningX_sum(3);
            System.out.println(b);
            System.out.println(b.getStatusInfo());
            System.out.println("\n");
            double summeB = b.summeY();
            for (int i = 0; i < 10; i++) {
                int j = 5 + (i * 3 + 2);
                TimeSeriesObject c = b.scaleX(j);
                System.out.println("\t" + j + " \t" + (c.summeY() - summeB));
                // System.out.println(c.getStatusInfo() +"\n");
            }
            TimeSeriesObject d = b.cut(4);
            System.out.println(d);
            System.out.println(d.getStatusInfo());
            TimeSeriesObject x1 = a.shift(-3);
            TimeSeriesObject x2 = a.shift(8);
            System.out.println(x1);
            System.out.println(x2);
        }
        catch (Exception ex) {
            Logger.getLogger(MessreiheTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    };
}
