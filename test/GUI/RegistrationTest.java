/**
 * This class is not finalized yet.
 * We tested the functionality of Checkin_new class by manual testing.
 */

package GUI;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegistrationTest {

    Registration testRegistration = new Registration();

    @Before
    public void setUp() throws Exception {
    }

    /***
     * Test whether it goes to the registration panel
     */

    @Test
    public void panel() {
        Assert.assertEquals(testRegistration.panel(), testRegistration.panelMain);
    }

    /***
     * Attention:
     * This test will be impacted by the records in the class table in the database.
     * By the time of testing (12.04) we have 3 classes numbers.
     * Therefore, we tested whether the dropdown menu returns all of the options.
     *
     */

    @Test
    public void fillClassDropDown() {
        int initialNum = 4;
        int classNum = 3;

        Assert.assertEquals(testRegistration.classIDBox.getItemCount(), initialNum);

        testRegistration.FillClassDropDown();
        Assert.assertEquals(testRegistration.classIDBox.getItemCount(), initialNum+classNum);
    }
}