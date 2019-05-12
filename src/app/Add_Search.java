// SELECT pid, pfname, plname, pphone, eid_doc, trid, trstart, trend, trresult
// FROM account1.patient
// INNER JOIN account1.treatment ON patient.PID = TREATMENT.PID_IN
// WHERE plname = 'Smith';

package app;

import java.sql.*;
import java.util.*;

class Patient_Info {
    private String pid;
    private String FName;
    private String LName;
    private String DOB;
    private String Gender;
    private String Phone;
    private String Address;

    Patient_Info(String pid, String FName, String LName, String DOB, String Gender, String Phone, String Address) {
        this.pid = pid;
        this.FName = FName;
        this.LName = LName;
        this.DOB = DOB;
        this.Gender = Gender;
        this.Phone = Phone;
        this.Address = Address;
    }

    public String toString() {
        String answer;
        answer = String.format("('%s', '%s', '%s', TO_DATE('%s', 'DD/MM/YYYY'), '%s', '%s', '%s')", pid, FName, LName,
                DOB, Gender, Phone, Address);
        return answer;
    }
}

public class Add_Search {
    private Connection connection;
    private Scanner sc;
    private Statement statement;
    private ResultSet check_exist;

    private String exist = "SELECT decode(COUNT(PID), 0, 'False', 'True') FROM ACCOUNT1.PATIENT WHERE PID = ";
    private String addQuery = "INSERT INTO ACCOUNT1.PATIENT VALUES (?, ?, ?, TO_DATE(?, 'dd/mm/yyyy'), ?, ?, ?)";

    public Add_Search(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
        try {
            this.statement = this.connection.createStatement();
        } catch (SQLException e) {
            // pass
        }
    }

    public void Search(String name) {
        // Search by Name (Check in both LName and FName)
        // Result: Name, Phone, Treatment info (Inpatient)

    }

    public void Add() throws SQLException {
        System.out.println("---------------------------------------------------------------");
        System.out.print("Patient ID:         ");
        String pid = sc.next();
        // Check for exist PID

        check_exist = statement.executeQuery(exist + pid);
        if (check_exist.next())
            if (check_exist.getString(1) == "True") {
                System.out.println("Existing Patient ID...");
                return;
            }

        sc.nextLine();
        System.out.print("Full name:          ");
        String name = sc.nextLine();
        String[] NamePart = name.split(" ");
        System.out.print("Birth (dd/mm/yyyy): ");
        String dob = sc.next();
        System.out.print("Gender (M/F):       ");
        String gender = sc.next();
        System.out.print("Phone:              ");
        String phone = sc.next();
        sc.nextLine();
        System.out.print("Address:            ");
        String addr = sc.nextLine();
        System.out.println("---------------------------------------------------------------");

        PreparedStatement update = connection.prepareStatement(addQuery);
        update.setString(1, pid);
        update.setString(2, NamePart[0]);
        update.setString(3, NamePart[1]);
        update.setString(4, dob);
        update.setString(5, gender);
        update.setString(6, phone);
        update.setString(7, addr);
        int result = update.executeUpdate();
        if (result == 1)
            System.out.println("Insert succeed");
        else
            System.out.println("There are some error while insert...");
        update.close();
        return;
    }
}