package GUI;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import database.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Report {
    private JPanel panelMain;
    private JLabel reportTypeLabel;
    public JComboBox reportTypeBox;
    private JLabel monthLabel;
    public JComboBox monthBox;
    private JLabel classLabel;
    public JButton saveToPDFButton;
    private JButton generateReportButton;
    private JPanel reportMain;
    public JComboBox classIDBox;
    public JTextPane textPane1;
    private Connection sqlConnect;
    public String pdfPath;

    public JPanel panel() {
        return reportMain;
    }

    public Report() {
        Database db = new Database();
        this.sqlConnect = db.sqlConnect;
        reportTypeBox.insertItemAt("", 0);
        reportTypeBox.addItem("Attendance");
        reportTypeBox.addItem("Performance");
        monthBox.insertItemAt("", 0);
        FillMonthDropDown();
        classIDBox.insertItemAt("", 0);
        FillClassDropDown();

        /**
         * Listener method for generate report button
         * in the report tab.
         * It generates report based on user options.
         */
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        /**
         * Listener method for Save to PDF report.
         * It saves the report to users' drive in pdf format.
         */
        saveToPDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToPDF();
            }
        });
    }

    public void FillClassDropDown() {
        //this method shows all the valid class id from the database in the class box
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Class_ID FROM class");
            while (rs.next()) {
                String classID = rs.getString("Class_ID");
                classIDBox.addItem(classID);
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * This method fills the drop down menu with months
     */
    public void FillMonthDropDown() {
        //this method shows all the valid class id from the database in the class box
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS sDate FROM attendance GROUP BY sDate ORDER BY sDate DESC");
            while (rs.next()) {
                String monthValue = rs.getString("sDate");
                monthBox.addItem(monthValue);
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * This method designs the report that will show up in the window
     */

    public void generateReport() {
        textPane1.setText("");
        textPane1.setContentType("text/html");
        String text = "<html>";
        try {
            //Initializing data for sql statement
            String reportType = reportTypeBox.getSelectedItem().toString();
            int selectedClass = Integer.parseInt(classIDBox.getSelectedItem().toString());
            String selectedMonth = monthBox.getSelectedItem().toString().split("-")[1];
            String selectedYear = monthBox.getSelectedItem().toString().split("-")[0];
            String summary = "";
            String mostAbsent = "";
            String leastAbsent = "";
            int mostAbsentTimes = 0;
            int leastAbsentTimes = 31;
            text += "<b>Report Type</b>: " + reportType + "<br>";
            text += "<b>Month</b>: " + monthBox.getSelectedItem().toString() + "<br>";
            text += "<b>Class</b>: " + selectedClass + "<br>";

            /**
             * Get the data for the attendance report
             */
            if (reportType == "Attendance") {
                String histogramSQL = "SELECT DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m-%d') AS sDate, count(Attendance_ID) as sum\n" +
                        "FROM childcaresystem.attendance \n" +
                        "where Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=? AND Attendance_Type = \"Absent\"\n" +
                        "Group by sDate \n" +
                        "ORDER BY sum asc";
                ResultSet absentSummarySet = this.executeQuery(histogramSQL, selectedClass, selectedMonth, selectedYear);
                while (absentSummarySet.next()){
                    if (absentSummarySet.getInt("sum")>= 0){
                        summary += absentSummarySet.getString("sDate");
                        summary += " There are ";
                        summary += absentSummarySet.getInt("sum");
                        summary += " kid(s) absent.<br>";
                    }
                }
                text += "<br><b>Absent Summary</b>: <br>" + summary;

                if (summary.length() == 0) {
                    text += "There were no absent kids in this month.<br>";
                }

                /**
                 * Get the kids that have most absence records
                 */

                String mostAbsentSQL = "SELECT att.Student_ID, DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS sDate, count(att.Student_ID) as sum, stu.*\n" +
                        "FROM childcaresystem.attendance att\n" +
                        "Left Join childcaresystem.student stu\n" +
                        "ON att.Student_ID=stu.Student_ID\n" +
                        "where Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=? AND Attendance_Type = \"Absent\"\n" +
                        "Group by att.Student_ID \n" +
                        "ORDER BY sum DESC";
                ResultSet mostAbsentSet = this.executeQuery(mostAbsentSQL, selectedClass, selectedMonth, selectedYear);
                while (mostAbsentSet.next()){
                    if (mostAbsentSet.getInt("sum")>= mostAbsentTimes){
                        mostAbsentTimes = mostAbsentSet.getInt("sum");
                        mostAbsent += mostAbsentSet.getString("First_Name");
                        mostAbsent += " ";
                        mostAbsent += mostAbsentSet.getString("Last_Name");
                        mostAbsent += "<br>";
                    }
                }
                text += "<br><b>Kids that have most absence</b>: <br>" + mostAbsent + "<br>";

                /**
                 * Get the kids that have least absence
                 */

                String leaseAbsentSQL = "SELECT stu.Student_ID, count(att.Absent_Reason) as sum, DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS sDate, stu.*\n" +
                        "FROM childcaresystem.student stu\n" +
                        "Left Join childcaresystem.attendance att\n" +
                        "ON att.Student_ID=stu.Student_ID\n" +
                        "WHERE stu.Enrolled_Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=?\n" +
                        "Group by stu.Student_ID\n" +
                        "ORDER BY sum asc";
                ResultSet leastAbsentSet = this.executeQuery(leaseAbsentSQL, selectedClass, selectedMonth, selectedYear);
                while (leastAbsentSet.next()){
                    if (leastAbsentSet.getInt("sum")<= leastAbsentTimes){
                        leastAbsentTimes = leastAbsentSet.getInt("sum");
                        leastAbsent += leastAbsentSet.getString("First_Name");
                        leastAbsent += " ";
                        leastAbsent += leastAbsentSet.getString("Last_Name");
                        leastAbsent += "<br>";
                    }
                }
                text += "<br><b>Kids that have least absence</b>: <br>" + leastAbsent + "<br>";
                text += "</html>";
                textPane1.setText(text);
            }

            /***
             * Pull the data regarding student performance.
             * The key metric for the performance is the rating.
             * We calculate the average rating, and count the number of kids that are above/below the average
             */

            if (reportType == "Performance") {
                double average = 0.0;
                double currentStudent = 0.0;
                int notBelowAve = 0;
                int belowAve = 0;
                /**
                 * This query calculates the average
                 */
                String averagePerformanceSQL = "SELECT Class_ID, avg(Class_Performance_Rating) as ave, DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS month\n" +
                        "From childcaresystem.attendance\n" +
                        "WHERE Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=?\n" +
                        "ORDER BY ave asc";
                ResultSet averagePerformanceSet = this.executeQuery(averagePerformanceSQL, selectedClass, selectedMonth, selectedYear);
                while (averagePerformanceSet.next()) {
                    average = averagePerformanceSet.getDouble("ave");
                    average = round(average,2);
                }
                text += "<br><b>Average Performance rating</b>: <br>" + average + "<br>";

                /**
                 * This query compares the rating against average
                 */
                String compareAverageSQL = "SELECT Student_ID, avg(Class_Performance_Rating) as ave, DATE_FORMAT(Check_In_Dt_Tm, '%Y-%m') AS month\n" +
                        "From childcaresystem.attendance\n" +
                        "WHERE Class_ID = ? AND month(Check_In_Dt_Tm) = ? AND year(Check_In_Dt_Tm)=?\n" +
                        "group by Student_ID\n" +
                        "ORDER BY ave desc";
                ResultSet compareAverageSet = this.executeQuery(compareAverageSQL, selectedClass, selectedMonth, selectedYear);
                while (compareAverageSet.next()) {
                    currentStudent = compareAverageSet.getDouble("ave");
                    if (currentStudent >= average) {
                        notBelowAve++;
                    } else {
                        belowAve++;
                    }
                }
                text += "<br><b>The number of kids that are higher than the average rating</b>: <br>" + notBelowAve + "<br>";
                text += "<br><b>The number of kids that are lower than the average rating</b>: <br>" + belowAve + "<br>";
                text += "</html>";
                textPane1.setText(text);
            }


            JOptionPane.showMessageDialog(panelMain, "Report generation is successful!");
            reportTypeBox.setSelectedIndex(0);
            classIDBox.setSelectedIndex(0);
            monthBox.setSelectedIndex(0);
            saveToPDFButton.setEnabled(true);


        } catch (Exception ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is the back-end code for the saveToPDF button.
     */
    public void saveToPDF() {
        Document document = new Document();
        try
        {
            JFrame parentFrame = new JFrame();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");

            int userSelection = fileChooser.showSaveDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                pdfPath = fileToSave.getAbsolutePath()+".pdf";
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                document.open();
                HTMLWorker htmlWorker = new HTMLWorker(document);
                htmlWorker.parse(new StringReader(textPane1.getText()));
                document.close();
                writer.close();
                JOptionPane.showMessageDialog(panelMain, "Report saves to PDF successfully!");
            }
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called in all the methods that require running queries.
     * It is pulled out to make the code DRYer.
     * @param sqlStatement
     * @param selectedClass
     * @param selectedMonth
     * @param selectedYear
     * @return
     */
    public ResultSet executeQuery(String sqlStatement, int selectedClass, String selectedMonth, String selectedYear) {
        try {
            PreparedStatement checkIfExists;
            checkIfExists = sqlConnect.prepareStatement(sqlStatement);
            checkIfExists.setInt(1, selectedClass);
            checkIfExists.setString(2, selectedMonth);
            checkIfExists.setString(3, selectedYear);
            ResultSet rs = checkIfExists.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * This method round the float into two digits.
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Report");
        frame.setContentPane(new Report().reportMain);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
