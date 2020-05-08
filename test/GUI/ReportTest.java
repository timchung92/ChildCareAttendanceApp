package GUI;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ReportTest {

    Report testReport = new Report();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /***
     * Test whether the generate button is enabled correctly
     */
    @Test
    public void generateReport() {

        boolean enabled = true;
        boolean notEnabled = false;
        testReport.reportTypeBox.setSelectedIndex(0);
        testReport.classIDBox.setSelectedIndex(0);
        testReport.monthBox.setSelectedIndex(0);
        testReport.generateReport();

        Assert.assertEquals(testReport.saveToPDFButton.isEnabled(), notEnabled);

        testReport.reportTypeBox.setSelectedIndex(1);
        testReport.classIDBox.setSelectedIndex(1);
        testReport.monthBox.setSelectedIndex(1);
        testReport.generateReport();

        Assert.assertEquals(testReport.saveToPDFButton.isEnabled(), enabled);
    }

    /**
     * Test the save to pdf function.
     * The save window will pop up while running the test.
     */
    @Test
    public void saveToPDF() throws IOException, NullPointerException {
        String input = "test";
        testReport.textPane1.setText(input);
        testReport.saveToPDF();
        String filePath = testReport.pdfPath;
        if (filePath != null)
        {
            PdfReader reader = new PdfReader(filePath);
            // pageNumber = 1
            String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

            Assert.assertEquals(textFromPage, input);
        }
        else
        {
            Assert.assertEquals(filePath, null);
        }

    }

    /***
     *Test whether the database connection is good and see whether the query was run correctly
     */

    @Test
    public void executeQuery() throws SQLException {
        String averagePerformanceSQL = "SELECT Class_ID, avg(Class_Performance_Rating) as ave, DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS month\n" +
                "From childcaresystem.attendance\n" +
                "WHERE Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=?\n" +
                "ORDER BY ave asc";

        boolean existing = true;
        boolean notExisting = false;
        ResultSet averagePerformanceSet = testReport.executeQuery(averagePerformanceSQL, 101, "12", "2019");
        Assert.assertEquals(averagePerformanceSet.next(), existing);

        averagePerformanceSet = testReport.executeQuery(averagePerformanceSQL, 101, "1", "2017");
        averagePerformanceSet.next();
        Assert.assertEquals(averagePerformanceSet.next(), notExisting);
    }

    /***
     * Test whether the average is round to 2 digits.
     */
    @Test
    public void round() {
        double pi = 3.1415926;
        double roundToTwo = 3.14;
        double roundToOne = 3.1;

        Assert.assertEquals(testReport.round(pi, 2), roundToTwo);
        Assert.assertEquals(testReport.round(pi, 1), roundToOne);
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

        Assert.assertEquals(testReport.classIDBox.getItemCount(), initialNum);

        testReport.FillClassDropDown();
        Assert.assertEquals(testReport.classIDBox.getItemCount(), initialNum+classNum);
    }
}