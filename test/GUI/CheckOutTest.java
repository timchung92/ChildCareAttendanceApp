package GUI;

import junit.framework.Assert;
import org.junit.Test;

public class CheckOutTest {

    CheckOut testCheckOut = new CheckOut();

    /**
     * Test whether the panel is returned correctly.
     */
    @Test
    public void panel() {
        Assert.assertEquals(testCheckOut.panel(), testCheckOut.checkOutPanel);
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

        Assert.assertEquals(testCheckOut.classIDBox.getItemCount(), initialNum);

        testCheckOut.FillClassDropDown();
        Assert.assertEquals(testCheckOut.classIDBox.getItemCount(), initialNum+classNum);
    }

    /***
     * Test whether the class name is returned correctly associated with the class number
     * in the check out tab
     */

    @Test
    public void fillClassName() {
        String className = "";
        Assert.assertEquals(testCheckOut.classNameField.getText(), className);

        testCheckOut.classIDBox.setSelectedIndex(1);
        className = "Morning Class 1";
        Assert.assertEquals(testCheckOut.classNameField.getText(), className);
    }
}