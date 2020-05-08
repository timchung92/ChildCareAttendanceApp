package GUI;

import database.Database;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Tests data in mysql database against expected data set
 * Also tests code used in other classes/methods to query data
 */
public class DatabaseTest {
    private Connection sqlConnect;
    int[] classIDs ;
    int[] studentIDs ;

    /**
     * Constructor
     */
    public DatabaseTest(){
        Database db = new Database();
        this.sqlConnect = db.sqlConnect;
        this.classIDs = getClassIDs();
        this.studentIDs = getStudentIDs();
    }

    /**
     * Retrieves list of student_ids. Same code used in CheckIn_New class
     * Validates all data is being retrieved
     * @return
     */
    public int[] getStudentIDs() {
        int idx = 0;
        int[] studentIDs = new int[37];
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Student_ID FROM student");
            while (rs.next()) {
                int studentID = rs.getInt("Student_ID");
                studentIDs[idx] = studentID;
                idx ++ ;
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return studentIDs;
    }

    /**
     * Retrieves list of class_ids. Same code used in CheckIn_New class
     * Validates all data is being retrieved
     * @return
     */
    public int[] getClassIDs() {
        int[] classIDs = new int[3];
        int index = 0;
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Class_ID FROM class");
            while (rs.next()) {
                int classID = rs.getInt("Class_ID");
                classIDs[index] = classID;
                index ++ ;
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return classIDs;
    }

    /**
     * Test that class_id list matches expected data
     */
    @Test
    public void testClassQuery () {
        int index = 0;
        int[] expectedClassIDs = {101,102,103};
        //Check is both arrays are equal
        Assert.assertArrayEquals(expectedClassIDs, this.classIDs);
    }

    /**
     * Test that student_id list matches expected data
     */
    @Test
    public void testStudentIDs() {
        int[] expectedStudentIDs = new int[37];
        int idx = 0;
        //all known student_ids
        for (int i = 1001; i <= 1037; i++) {
            expectedStudentIDs[idx] = i;
            idx ++;
        }
        //check if both arrays are equal
        Assert.assertArrayEquals(expectedStudentIDs, this.studentIDs);
    }

    /**
     * Checks if any unknown student_ids are found in attendance table
     * Expected to fail with student_id = 1038. Old student_id deleted from
     * student table but left on attendance data.
     */
    @Test
    public void testAttendanceDataStudentIDs() {
        HashMap<Integer, Integer> studentIdHash = new HashMap<>();
        HashMap<Integer, Integer> unknownStudentIds = new HashMap<>();
        int[] expectedEmptyArray =  new int[0];
        int idx = 0 ;
        for (int id : this.studentIDs) {
            studentIdHash.put(id, 1);
        }
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Student_ID FROM attendance");
            while (rs.next()) {
                int studentID = rs.getInt("Student_ID");
                if (studentIdHash.get(studentID) == null) {
                    if (unknownStudentIds.get(studentID)==null) {
                        unknownStudentIds.put(studentID,1);
                    }
                }
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        //fill out array with all found unknown student_ids
        int[] unknownStudentIDsArray = new int[unknownStudentIds.keySet().size()];

        for (int key: unknownStudentIds.keySet()) {
            unknownStudentIDsArray[idx] = key;
            idx ++;
            System.out.println("Unknown student_id " +key);
        }

        Assert.assertArrayEquals("Unknown Student IDs found in attendance data",expectedEmptyArray, unknownStudentIDsArray);
    }

    /**
     * Checks if any unknown class_ids are found in attendance table
     * Expected to fail with class_id = 104
     * Old class_id that was deleted from class table but still on
     * attendance data
     */
    @Test
    public void testAttendanceDataClassIDs() {
        HashMap<Integer, Integer> classIdHash = new HashMap<>();
        HashMap<Integer, Integer> unknownClassIds = new HashMap<>();
        int[] expectedEmptyArray =  new int[0];
        int idx = 0 ;
        for (int id : this.classIDs) {
            classIdHash.put(id, 1);
        }
        try {
            Statement stmt = sqlConnect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Class_ID FROM attendance");
            while (rs.next()) {
                int classID = rs.getInt("Class_ID");
                if (classIdHash.get(classID) == null) {
//                    System.out.println(classID);
                    unknownClassIds.put(classID,1);
                }
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        int[] unknownClassIDsArray = new int[unknownClassIds.keySet().size()];
        for (int key: unknownClassIds.keySet()) {
            unknownClassIDsArray[idx] = key;
            idx ++;
            System.out.println("Unknown class_id " +key);

        }
        Assert.assertArrayEquals("Unknown Class IDs found in attendance data",expectedEmptyArray, unknownClassIDsArray);
    }
    
}
