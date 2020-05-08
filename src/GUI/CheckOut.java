package GUI;

import database.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CheckOut {
    public JPanel checkOutPanel;
    private JLabel checkOutDate;
    public JComboBox classIDBox;
    public JLabel classNameField;
    private JComboBox studentNameBox;
    private JSpinner checkOutHourSpinner;
    private JSpinner checkOutMinSpinner;
    private JTextField pickUpParent;
    private JSpinner performanceSpinner;
    private JButton submitButton;
    private Connection sqlConnect;
    private int classIDNumber;
    private Date date;
    private ArrayList<Integer> studentIds;
    private int checkOutHour;
    private int checkOutMin;
    private int classPerformance;

    public JPanel panel() {
        return checkOutPanel;
    }


    public CheckOut() {

        studentIds = new ArrayList<Integer>();
        Database db = new Database();
        this.sqlConnect = db.sqlConnect;
        classIDBox.insertItemAt("", 0);
        FillClassDropDown();
        date = Calendar.getInstance().getTime();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        checkOutDate.setText(dateFormat.format(date));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Submit();
            }
        });


        /**
         * This method initializes class ID + class Name + Student Name
         */

        classIDBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String classIDString = classIDBox.getSelectedItem().toString();
                if (!classIDString.equals("")) {
                    classIDNumber = Integer.parseInt(classIDString);
                    FillStudentDropDown(classIDNumber);
                    FillClassName(classIDNumber);
                } else {
                    //teacherName.setText("");
                    classNameField.setText("");
                }

            }
        });

        /**
         * This method initializes check out hour Spinner
         */
        checkOutHourSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel hourValue = new SpinnerNumberModel(1, 1, 24, 1);
                checkOutHourSpinner.setModel(hourValue);
            }
        });

        /**
         * This method initializes check out min Spinner
         */
        checkOutMinSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel minValue = new SpinnerNumberModel(0, 0, 59, 5);
                checkOutMinSpinner.setModel(minValue);
            }
        });

        /**
         * This method initializes performance rating Spinner
         */
        performanceSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel performanceValue = new SpinnerNumberModel(1, 1, 10, 1);
                performanceSpinner.setModel(performanceValue);
            }
        });
    }

    /**
     * This method fills in the classbox drop down with all the valid class ids
     */
    public void FillClassDropDown() {
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Class_ID FROM class");
            while (rs.next()) {
                int classID = rs.getInt("Class_ID");
                classIDBox.addItem(classID);
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }


    /**
     * this method returns all the student within the selected class id.
     */
    private void FillStudentDropDown(int classID) {
        try {
            PreparedStatement stmt = sqlConnect.prepareStatement("SELECT * FROM student where Enrolled_Class_ID = ?");
            stmt.setInt(1, classID);
            ResultSet rs = stmt.executeQuery();
            studentNameBox.removeAllItems();
            studentIds.clear();
            while (rs.next()) {
                //Format the student name in "Last Name, First Name"
                String firstName = rs.getString("First_Name");
                String lastName = rs.getString("Last_Name");
                studentIds.add(rs.getInt("Student_ID"));
                StringBuilder fullName = new StringBuilder(lastName + " , " + firstName);
                studentNameBox.addItem(fullName.toString());
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }


    /**
     * this method returns the class description based on the selected class id.
     */
    public void FillClassName(int classID) {

        try {
            PreparedStatement stmt = sqlConnect.prepareStatement("SELECT * FROM class where Class_ID = ?");
            stmt.setInt(1, classID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                String classTeacherLast = rs.getString("Teacher_Last_Name");
                //teacherName.setText(classTeacherLast);
                classNameField.setText(name);
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }



    /**
     *  the submit button writes the data into the database.
     */
    private void Submit() {
        try {
            //Initializing data for sql statement
            java.sql.Date checkInDate = new java.sql.Date(date.getTime());
            int studentId = studentIds.get(studentNameBox.getSelectedIndex());
            int attendanceId;
            //Preparing SQL statement
            PreparedStatement stmt;
            PreparedStatement checkIfExists;

            checkIfExists = sqlConnect.prepareStatement("SELECT * FROM attendance where Student_ID = ? AND date(Check_In_Dt_Tm) = ? AND Attendance_Type = ?");
            checkIfExists.setInt(1, studentId);
            checkIfExists.setDate(2, checkInDate);
            checkIfExists.setString(3, "Present");
            ResultSet rs = checkIfExists.executeQuery();

            if (rs.next()) {
                attendanceId = rs.getInt("Attendance_ID");

                stmt = sqlConnect.prepareStatement(
                        "UPDATE `attendance` SET `Student_ID`= ?, `Class_ID`= ?, `Pick_Up_Parent_First_Name`= ?, `Check_Out_Dt_Tm`= ?, `Class_Performance_Rating`=? WHERE Attendance_ID = ?;\n");

                stmt.setInt(1, studentId);

                stmt.setInt(2, classIDNumber);
                stmt.setString(3, pickUpParent.getText());


                Calendar cal = Calendar.getInstance();
                cal.setTime(checkInDate);

                checkOutHour = Integer.parseInt(checkOutHourSpinner.getModel().getValue().toString());
                checkOutMin = Integer.parseInt(checkOutMinSpinner.getModel().getValue().toString());
                cal.set(Calendar.HOUR_OF_DAY, checkOutHour);
                cal.set(Calendar.MINUTE, checkOutMin);
                cal.set(Calendar.SECOND, 0);
                stmt.setTimestamp(4, new java.sql.Timestamp(cal.getTime().getTime()));

                classPerformance = Integer.parseInt(performanceSpinner.getModel().getValue().toString());
                stmt.setInt(5, classPerformance);
                stmt.setInt(6, attendanceId);

                //Executing SQL statement
                stmt.execute();
                stmt.close();

                JOptionPane.showMessageDialog(checkOutPanel, "Check out successfully!");
            }
            else
            {
                JOptionPane.showMessageDialog(checkOutPanel, "Fail to check out! This student is absent or not checking in today.");
            }




            pickUpParent.setText("");
            checkOutHourSpinner.setValue(0);
            checkOutMinSpinner.setValue(0);
            performanceSpinner.setValue(1);




            } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * main function for CheckOut class
     */

    public static void main(String[] args) {
        JFrame frame = new JFrame("CheckOut");
        frame.setContentPane(new CheckOut().checkOutPanel);
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
