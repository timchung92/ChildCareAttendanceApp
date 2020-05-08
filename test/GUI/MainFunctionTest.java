/**
 * This is a unit test for the main class.
 * It tests whether the button returns the right title.
 */
package GUI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainFunctionTest {

    @Before
    public void setUp() throws Exception {
    }

    /***
     * Test to see whether the four tabs in the UI are returned correctly.
     */

    @Test
    public void main() {
        MainFunction test = new MainFunction();

        String firstPaneTitle = "Check In";
        String secondPaneTitle = "Check Out";
        String thirdPaneTitle = "Register Student";
        String fourthPaneTitle = "Report";

        Assert.assertEquals(test.tabbedPane.getTitleAt(0), firstPaneTitle);
        Assert.assertEquals(test.tabbedPane.getTitleAt(1), secondPaneTitle);
        Assert.assertEquals(test.tabbedPane.getTitleAt(2), thirdPaneTitle);
        Assert.assertEquals(test.tabbedPane.getTitleAt(3), fourthPaneTitle);
    }
}