/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extraction;

/**
 *
 * @author kamir
 */
public class UserCounter {

    public UserCounter( int id ) {
        userID = id;
    }

    int userID = 0;
    int counts = 0;

    public void count() {
        counts++;
    };

}
