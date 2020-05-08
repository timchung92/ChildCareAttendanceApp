/**
 * This class is not finalized yet.
 * We tested the functionality of Checkin_new class by manual testing.
 */

package GUI;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class CheckIn_NewTest {

    CheckIn_New testCheckIn_New = new CheckIn_New();

    /**
     * Test whether the panel is correct
     */
    @Test
    public void panel() {
        Assert.assertEquals(testCheckIn_New.panel(), testCheckIn_New.CheckInPanel);
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

        Assert.assertEquals(testCheckIn_New.classIDBox.getItemCount(), initialNum);

        testCheckIn_New.FillClassDropDown();
        Assert.assertEquals(testCheckIn_New.classIDBox.getItemCount(), initialNum+classNum);
    }

    /**
     * Test whether the class name is returned correctly associated
     * with the class number in the Check In tab.
     */
    @Test
    public void fillClassName() {
        String className = "";
        Assert.assertEquals(testCheckIn_New.classNameField.getText(), className);

        testCheckIn_New.classIDBox.setSelectedIndex(1);
        className = "Morning Class 1";
        Assert.assertEquals(testCheckIn_New.classNameField.getText(), className);
    }
}