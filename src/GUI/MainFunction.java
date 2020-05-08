package GUI;

import javax.swing.*;

/**
 * combine the JtabbedPane with each JPanel.
 */
public class MainFunction extends JFrame{

    JPanel firstPanel = new JPanel();
    JPanel secondPanel = new JPanel();
    JPanel thirdPanel = new JPanel();
    JPanel fourthPanel = new JPanel();


    JTabbedPane tabbedPane = new JTabbedPane();
    CheckIn_New checkIn;
    CheckOut checkOut;
    Registration register;
    Report report;

    public MainFunction(){
        //Add the key label for each tab

        checkIn = new CheckIn_New();
        tabbedPane.add("Check In", checkIn.panel());
        checkIn.panel().setSize(600,400);

        checkOut = new CheckOut();
        tabbedPane.add("Check Out", checkOut.panel());
        checkIn.panel().setSize(600,400);

        register = new Registration();
        tabbedPane.add("Register Student",register.panel());
        register.panel().setSize(600,400);

        report = new Report();
        tabbedPane.add("Report",report.panel());
        report.panel().setSize(600,400);
        add(tabbedPane);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        MainFunction tp = new MainFunction();
        tp.setTitle("Childcare Check in System");
        tp.setSize(800,600);
        tp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tp.setVisible(true);
    }

}
