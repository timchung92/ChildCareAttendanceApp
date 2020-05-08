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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckIn_New {
    public JComboBox classIDBox;
    private JComboBox studentNameBox;
    private JCheckBox presentCheckBox;
    private JTextField parentWhoDropOffField;
    private JTextField absentReasonField;
    private JButton submitButton;
    public JLabel classNameField;
    private JLabel CheckInDate;
    private JLabel classID;
    private JLabel className;
    private JLabel studentName;
    private JLabel absentReason;
    public JPanel CheckInPanel;
    private JLabel checkInDate;
    private JTextField checkInHourText;
    private JTextField checkInMinText;
    private JRadioButton presentRadioButton;
    private JRadioButton absentRadioButton;
    private JSpinner checkInHourSpinner;
    private JSpinner checkInMinSpinner;
    private JSpinner temperatureIntSpinner;
    private JSpinner temperatureDeciSpinner;
    private Connection sqlConnect;
    private int classIDNumber;
    private Date date;
    private ArrayList<Integer> studentIds;
    private int checkInHour;
    private int checkInMin;
    private int temperatureInt;
    private int temperatureDeci;
    private double bodyTemperature;

    public JPanel panel() {
        return CheckInPanel;
    }


    public CheckIn_New() {
        studentIds = new ArrayList<Integer>();
        Database db = new Database();
        this.sqlConnect = db.sqlConnect;
        classIDBox.insertItemAt("", 0);
        FillClassDropDown();
        date = Calendar.getInstance().getTime();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        checkInDate.setText(dateFormat.format(date));

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
                    classNameField.setText("");
                }
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Submit();


            }
        });

        /**
         * This method initializes check in hour Spinner
         */
        checkInHourSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel hourValue = new SpinnerNumberModel(1, 1, 24, 1);
                checkInHourSpinner.setModel(hourValue);
            }
        });

        /**
         * This method initializes check in min Spinner
         */
        checkInMinSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel minValue = new SpinnerNumberModel(0, 0, 59, 5);
                checkInMinSpinner.setModel(minValue);

            }
        });

        /**
         * This method initializes body temperature integer Spinner
         */

        temperatureIntSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel temperatureIntValue = new SpinnerNumberModel(97, 80, 120, 1);
                temperatureIntSpinner.setModel(temperatureIntValue);
            }
        });

        /**
         * This method initializes body temperature decimal Spinner
         */
        temperatureDeciSpinner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel temperatureDecimalValue = new SpinnerNumberModel(0, 0, 9, 1);
                temperatureDeciSpinner.setModel(temperatureDecimalValue);
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

            checkIfExists = sqlConnect.prepareStatement("SELECT * FROM attendance where Student_ID = ? AND date(Check_In_Dt_Tm) = ?");
            checkIfExists.setInt(1, studentId);
            checkIfExists.setDate(2, checkInDate);
            ResultSet rs = checkIfExists.executeQuery();

            if (rs.next()) {
                //present
                attendanceId = rs.getInt("Attendance_ID");
                if (presentRadioButton.isSelected()) {
                    stmt = sqlConnect.prepareStatement(
                            "UPDATE `attendance` SET `Student_ID`= ?, `Class_ID`= ?, `Attendance_Type`= ?, `Drop_Off_Parent_First_Name`= ?, `Temperature`= ?, `Check_In_Dt_Tm`= ?, `Absent_Reason`= ? WHERE Attendance_ID = ?;\n");
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, classIDNumber);
                    stmt.setString(3, "Present");
                    stmt.setString(4, parentWhoDropOffField.getText());

                    temperatureInt = Integer.parseInt(temperatureIntSpinner.getModel().getValue().toString());
                    temperatureDeci = Integer.parseInt(temperatureDeciSpinner.getModel().getValue().toString());
                    bodyTemperature = temperatureInt + 0.1 * (double) temperatureDeci;
                    stmt.setFloat(5, (float) bodyTemperature);
                    //stmt.setFloat(5, Float.parseFloat(temperatureField.getText()));


                    Calendar cal = Calendar.getInstance();
                    cal.setTime(checkInDate);

                    checkInHour = Integer.parseInt(checkInHourSpinner.getModel().getValue().toString());
                    checkInMin = Integer.parseInt(checkInMinSpinner.getModel().getValue().toString());
                    cal.set(Calendar.HOUR_OF_DAY, checkInHour);
                    cal.set(Calendar.MINUTE, checkInMin);
                    cal.set(Calendar.SECOND, 0);
                    stmt.setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()));

                    stmt.setNull(7, 0);
                    stmt.setInt(8, attendanceId);

                } else { //absent
                    attendanceId = rs.getInt("Attendance_ID");
                    stmt = sqlConnect.prepareStatement(
                            "UPDATE `attendance` SET `Student_ID`= ?, `Class_ID`= ?, `Attendance_Type`= ?, `Absent_Reason`= ?, `Drop_Off_Parent_First_Name`= ?, `Temperature`= ?, `Class_Performance_Rating`= ?  WHERE Attendance_ID = ?;\n");
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, classIDNumber);
                    stmt.setString(3, "Absent");
                    stmt.setString(4, absentReasonField.getText());
                    stmt.setNull(5, 0);
                    stmt.setNull(6, 0);
                    stmt.setInt(7, 0);

                    stmt.setInt(8, attendanceId);
                }

                //Executing SQL statement
                stmt.execute();
                stmt.close();

                JOptionPane.showMessageDialog(CheckInPanel, "Check in updating successfully!");
            }
            else
            {
                //present
                if (presentRadioButton.isSelected()) {
                    stmt = sqlConnect.prepareStatement(
                            "INSERT INTO `attendance` (`Student_ID`, `Class_ID`, `Attendance_Type`, `Drop_Off_Parent_First_Name`, `Temperature`, `Check_In_Dt_Tm`) VALUES (?, ?, ?, ?, ?, ?);\n");
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, classIDNumber);
                    stmt.setString(3, "Present");
                    stmt.setString(4, parentWhoDropOffField.getText());

                    temperatureInt = Integer.parseInt(temperatureIntSpinner.getModel().getValue().toString());
                    temperatureDeci = Integer.parseInt(temperatureDeciSpinner.getModel().getValue().toString());
                    bodyTemperature = temperatureInt + 0.1 * (double) temperatureDeci;
                    stmt.setFloat(5, (float) bodyTemperature);
                    //stmt.setFloat(5, Float.parseFloat(temperatureField.getText()));

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(checkInDate);

                    checkInHour = Integer.parseInt(checkInHourSpinner.getModel().getValue().toString());
                    checkInMin = Integer.parseInt(checkInMinSpinner.getModel().getValue().toString());
                    cal.set(Calendar.HOUR_OF_DAY, checkInHour);
                    cal.set(Calendar.MINUTE, checkInMin);
                    cal.set(Calendar.SECOND, 0);
                    stmt.setTimestamp(6, new java.sql.Timestamp(cal.getTime().getTime()));


                } else { //absent
                    stmt = sqlConnect.prepareStatement(
                            "INSERT INTO `attendance` (`Student_ID`, `Class_ID`, `Attendance_Type`, `Absent_Reason`, `Check_In_Dt_Tm`, `Class_Performance_Rating` ) VALUES (?, ?, ?, ?, ?, ?);\n");
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, classIDNumber);
                    stmt.setString(3, "Absent");
                    stmt.setString(4, absentReasonField.getText());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(checkInDate);

                    checkInHour = Integer.parseInt(checkInHourSpinner.getModel().getValue().toString());
                    checkInMin = Integer.parseInt(checkInMinSpinner.getModel().getValue().toString());
                    cal.set(Calendar.HOUR_OF_DAY, checkInHour);
                    cal.set(Calendar.MINUTE, checkInMin);
                    cal.set(Calendar.SECOND, 0);
                    stmt.setTimestamp(5, new java.sql.Timestamp(cal.getTime().getTime()));
                    stmt.setInt(6, 0);
                }

                //Executing SQL statement
                stmt.execute();
                stmt.close();

                JOptionPane.showMessageDialog(CheckInPanel, "Check in successfully!");
            }



            //empty filled data
            temperatureIntSpinner.setValue(98);
            temperatureDeciSpinner.setValue(0);
            checkInHourSpinner.setValue(0);
            checkInMinSpinner.setValue(0);
            parentWhoDropOffField.setText("");
            absentReasonField.setText("");



        } catch (SQLException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

    /**
     *
     * main function for CheckIn class
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("CheckIn");
        frame.setContentPane(new CheckIn_New().CheckInPanel);
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}


