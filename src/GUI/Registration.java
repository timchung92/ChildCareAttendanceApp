package GUI;

import database.Database;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Registration {
    private JTextField fieldFirstName;
    private JButton registerStudentButton;
    public JPanel panelMain;
    private JTextField fieldLastName;
    private JTextField fieldPhone;
    private JTextField fieldParentFirstName;
    public JComboBox classIDBox;
    private JRadioButton femaleRadioButton;
    private JRadioButton maleRadioButton;
    private JSpinner DOBYear;
    private JSpinner DOBMonth;
    private JSpinner DOBDay;
    private JTextField fieldParentLastName;
    private Connection sqlConnect;
    private int enrolledClassID;
    private String gender;

    public JPanel panel() {
        return panelMain;
    }



    public Registration() {

        Database db = new Database();
        this.sqlConnect = db.sqlConnect;
        classIDBox.insertItemAt("", 0);
        FillClassDropDown();

        /**
         * Listener method for student registration. Inserts new row in
         * student table with data populated in fields
         */
        registerStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Initializing data for sql statement
                    String firstName = fieldFirstName.getText();
                    String lastName = fieldLastName.getText();

                    if (femaleRadioButton.isSelected()) {
                        gender = femaleRadioButton.getText();
                    } else{
                        gender = maleRadioButton.getText();
                    }


                    String phone = fieldPhone.getText();
                    String parentFirstName = fieldParentFirstName.getText();
                    String parentLastName = fieldParentLastName.getText();
                    Calendar calendar = Calendar.getInstance();



                    //Preparing SQL statement
                    PreparedStatement stmt = sqlConnect.prepareStatement(
                            "INSERT INTO `childcaresystem`.`student` (`Last_Name`, `First_Name`, `Birth_Date`, `Gender`, `Phone_Number`, `Parent_First_Name`, `Enrolled_Class_ID`, `Attendance_Status`, `Parent_Last_Name`) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?);\n");
                    stmt.setString(2, firstName);
                    stmt.setString(1, lastName);

                    stmt.setString(4, gender);
                    stmt.setString(5, phone);
                    stmt.setString(6, parentFirstName);
                    stmt.setInt(7, enrolledClassID);
                    stmt.setString(8, "Absent"); //initializ all data to absent first

                    stmt.setString(9, parentLastName);



                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(DOBYear.getModel().getValue().toString()));
                    cal.set(Calendar.MONTH, Integer.parseInt(DOBMonth.getModel().getValue().toString())-1);
                    cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(DOBDay.getModel().getValue().toString()) );
                    stmt.setDate(3, new java.sql.Date(cal.getTime().getTime()));


                    //Executing SQL statement
                    stmt.execute();
                    stmt.close();

                    JOptionPane.showMessageDialog(panelMain, "Student registration is successful!");


                    //empty filled data
                   fieldFirstName.setText("");
                   fieldLastName.setText("");
                   fieldPhone.setText("");
                   fieldParentFirstName.setText("");
                    fieldParentLastName.setText("");
                   DOBYear.setValue(2017);
                   DOBMonth.setValue(1);
                   DOBDay.setValue(1);





                } catch (SQLException ex) {
                    Logger.getLogger(Registration.class.getName()).log(Level.SEVERE,null,ex);
                }
            }
        });

        /**
         * This method initializes class ID
         */
        classIDBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String classIDString = classIDBox.getSelectedItem().toString();
                if (!classIDString.equals("")) {
                    enrolledClassID = Integer.parseInt(classIDString);
                }

            }
        });

        /**
         * This method initializes date of birth year Spinner
         */
        DOBYear.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel yearValue = new SpinnerNumberModel(2017, //initial value
                        2000, //min
                        2020 , //max
                        1);
                DOBYear.setModel(yearValue);
            }
        });

        /**
         * This method initializes date of birth month Spinner
         */
        DOBMonth.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel monthValue = new SpinnerNumberModel(1, //initial value
                        1, //min
                        12 , //max
                        1);
                DOBMonth.setModel(monthValue);
            }
        });

        /**
         * This method initializes date of birth day Spinner
         */
        DOBDay.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                SpinnerModel dayValue = new SpinnerNumberModel(1, //initial value
                        1, //min
                        31 , //max
                        1);
                DOBDay.setModel(dayValue);
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
     * main function for Registration class
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Registration");
        frame.setContentPane(new Registration().panelMain);
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
