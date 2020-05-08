package DataGenerate;

import GUI.Registration;
import database.Database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOT part of workflow. For TESTING purposes.
 * Generates attendance data for each class and each student on a specified date.
 * Randomly selects if student is absent or present that day
 * Randomly selects performance score for the day
 * Randomly selects absent reason if applicable
 * Randomly selects student's temperature
 */
public class DataGenerator {
    public String[] absentReasons = {"Flu", "Doctor Appointment","Sick", "Chicken Pox",
            "Injured","Out of town", "Headache","Family Emergency", "No ride" };
    public Database db = new Database();
    public Connection sqlConnect = db.sqlConnect;
    public Date checkInDate ;

    /**
     * Constructor
     * @param date - specified date for attendance data
     */
    public DataGenerator (String date) {
        this.checkInDate = Date.valueOf(date);
    }

    /**
     * Initiate and executes data generation.
     * Retrieves class_id's then student_id's.
     * Iterates through each student and prepares
     * applicable data.
     * Executes SQL statement.
     */
    public void generateAttendanceData() {
        //Call getClassIDs() to retrieve arraylist of all class_id's
        ArrayList<Integer> classIDs = getClassIDs();
        PreparedStatement stmt;
        try {
            //Begin with iterating all class_id's
            for (int classID : classIDs) {
                //Call getStudentIDs() to retrieve arraylist of all student_id's
                ArrayList<Integer> studentIDs = this.getStudentIDs(classID);
                Random rand = new Random();
                // Iterate through each student_id
                for (int studentID : studentIDs) {
                    //Call presentAbsent() to generate random choice of present/absent
                    String presentAbsent = presentAbsent();
                    // Determine data needed if present or absent
                    if (presentAbsent.equals("Present")) {
                        String parentName = getParentName(studentID);
                        //random score bt 5-10
                        int performance_score = rand.nextInt(6) + 5;
                        //random temp bt 97.5-100
                        double temperature = 97.5 + (100.0 - 97.5) * rand.nextDouble();
                        temperature = Math.round(temperature * 100);
                        temperature = temperature/100;
                        //Date data for SQL insert
                        Calendar checkInCal = Calendar.getInstance();
                        checkInCal.setTime(checkInDate);
                        checkInCal.set(Calendar.HOUR, 7);
                        checkInCal.set(Calendar.MINUTE, 55);
                        checkInCal.set(Calendar.SECOND, 0);
                        Calendar checkOutCal = Calendar.getInstance();
                        checkOutCal.setTime(checkInDate);
                        checkOutCal.set(Calendar.HOUR_OF_DAY, 14);
                        checkOutCal.set(Calendar.MINUTE, 55);
                        checkOutCal.set(Calendar.SECOND, 0);
                        // Prepare the SQL insert statement
                        stmt = sqlConnect.prepareStatement("insert into attendance values (null, ?, ?, ?, null ,?, ? , null, ? ,? ,null , ?, ? );\n");
                        stmt.setInt(1, studentID);
                        stmt.setInt(2, classID);
                        stmt.setString(3, "Present");
                        stmt.setString(4, parentName);
                        stmt.setFloat(5, (float) temperature);
                        stmt.setTimestamp(6, new java.sql.Timestamp(checkInCal.getTime().getTime()));
                        stmt.setTimestamp(7, new java.sql.Timestamp(checkOutCal.getTime().getTime()));
                        stmt.setString(8, parentName);
                        stmt.setInt(9, performance_score);
                    // Data if student is absent
                    } else {
                        // Date data
                        Calendar checkInCal = Calendar.getInstance();
                        checkInCal.setTime(checkInDate);
                        checkInCal.set(Calendar.HOUR, 7);
                        checkInCal.set(Calendar.MINUTE, 55);
                        checkInCal.set(Calendar.SECOND, 0);
                        Calendar checkOutCal = Calendar.getInstance();
                        // Randomly select absent reason
                        String absReason = this.absentReasons[rand.nextInt(this.absentReasons.length)];
                        // Prepare SQL insert statement
                        stmt = sqlConnect.prepareStatement("insert into attendance values (null, ?, ?, ?, null , null, null , ?, ? ,null ,null , null, null );\n");
                        stmt.setInt(1, studentID);
                        stmt.setInt(2, classID);
                        stmt.setString(3, "Absent");
                        stmt.setString(4, absReason);
                        stmt.setTimestamp(5, new java.sql.Timestamp(checkInCal.getTime().getTime()));
                    }

                    //Executing SQL statement
                    stmt.execute();
                    stmt.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Helper method: Query mySQL db for all class_id's
     * @return ArrayList of class_id's
     */
    public ArrayList<Integer> getClassIDs() {
        ArrayList<Integer> classIDs = new ArrayList<>();
        try {
            Statement stmt = this.sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Class_ID FROM class");
            while (rs.next()) {
                int classID = rs.getInt("Class_ID");
                classIDs.add(classID);
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return classIDs;
    }

    /**
     * Helper method: Query mySQL db for all student_id's
     * @return ArrayList of student_id's
     */
    public ArrayList<Integer> getStudentIDs(int classID) {
        ArrayList<Integer> studentIDs = new ArrayList<>();
        try {
            PreparedStatement stmt = this.sqlConnect.prepareStatement("SELECT * FROM student where Enrolled_Class_ID = ?");
            stmt.setInt(1, classID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                studentIDs.add(rs.getInt("Student_ID"));

            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return studentIDs;
    }

    /**
     * Helper method: randomly selects if student is present or absent
     * 10% chance of absent
     * @return String "Absent" or "Present"
     */
    public String presentAbsent() {
        ArrayList<String> presentAbsent = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            presentAbsent.add("Present");
        }
        presentAbsent.add("Absent");
        Random rand = new Random();
        String randSelect = presentAbsent.get(rand.nextInt(presentAbsent.size()));
        return randSelect;
    }

    /**
     * Helper method: query for student's parents name to insert as the pick-up/drop-off parent
     * @param studentID
     * @return String of parent name
     */
    public String getParentName(int studentID) {
        StringBuilder parentName = new StringBuilder();
        try {
            PreparedStatement stmt = this.sqlConnect.prepareStatement("SELECT * FROM student where Student_ID = ?");
            stmt.setInt(1, studentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                parentName.append(rs.getString("Parent_First_Name"));
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return parentName.toString();
    }


    /**
     * Main method to specify date and run DataGenerator methods
     */
    public static void main(String[] args) {
        DataGenerator addData = new DataGenerator("2019-12-05");
        addData.generateAttendanceData();
    }
}
