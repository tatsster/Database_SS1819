package app;

import java.sql.*;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

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

    private String exist = "SELECT decode(COUNT(PID), 0, 'False', 'True') FROM PATIENT WHERE PID = ";
    private String addQuery = "INSERT INTO PATIENT VALUES (?, ?, ?, TO_DATE(?, 'dd/mm/yyyy'), ?, ?, ?)";
    private String isINPATIENT = "SELECT decode(COUNT(PID_IN), 0, 'False', 'True') FROM TREATMENT WHERE = ";
    private String INPATIENT_info = "SELECT PID, PFNAME, PLNAME, PPHONE, TRSTART, TREND, TRRESULT FROM PATIENT  INNER JOIN TREATMENT ON PID = PID_IN WHERE PID = ";
    private String OUTPATIENT_info = "SELECT PID, PFNAME, PLNAME, PPHONE, EXDATE, EXSECONDEXAMINATIONDATE, EXDIAGNOSIS FROM PATIENT INNER JOIN EXAMINATION ON PID = PID_OUT WHERE PID = ";

    public Add_Search(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
        try {
            this.statement = this.connection.createStatement();
        } catch (SQLException e) {
            // pass
        }
    }

    public void Search(String pid) {
        // Search by pid
        try {
            ResultSet checkType = statement.executeQuery(isINPATIENT + pid);
            if (checkType.next() && checkType.getString(1) == "True") {
                // INPATIENT
                ResultSet inpatient = statement.executeQuery(INPATIENT_info + pid);
                String line = "";
                for (int i = 0; i < 141; i++)
                    line += '-';

                System.out.println(line);
                System.out.format("|%s|%s|%s|%s|%s|%s|%s|\n", StringUtils.center("PID", 11),
                        StringUtils.center("First Name", 22), StringUtils.center("Last Name", 22),
                        StringUtils.center("Phone number", 12), StringUtils.center("Start date", 11),
                        StringUtils.center("End date", 11), StringUtils.center("Result", 52));
                System.out.println(line);

                while (inpatient.next()) {
                    System.out.format("|%s|%s|%s|%s|%s|%s|%s|\n", inpatient.getString(1), inpatient.getString(2),
                            inpatient.getString(3), inpatient.getString(4), inpatient.getString(5),
                            inpatient.getString(6), inpatient.getString(7));
                    System.out.println(line);
                }
            } else {
                // OUTPATIENT
                ResultSet outpatient = statement.executeQuery(OUTPATIENT_info + pid);
                if (outpatient.next() == false) {
                    System.out.println("Empty Result: The given Patient ID does not match to any Patients.");
                    return;
                }

                String line = "";
                for (int i = 0; i < 141; i++)
                    line += '-';

                System.out.println(line);
                System.out.format("|%s|%s|%s|%s|%s|%s|%s|\n", StringUtils.center("PID", 11),
                        StringUtils.center("First Name", 22), StringUtils.center("Last Name", 22),
                        StringUtils.center("Phone number", 12), StringUtils.center("Exam date", 11),
                        StringUtils.center("Visit date", 11), StringUtils.center("Diagnosis", 52));
                System.out.println(line);

                do {
                    System.out.format("|%s|%s|%s|%s|%s|%s|%s|\n", outpatient.getString(1), outpatient.getString(2),
                            outpatient.getString(3), outpatient.getString(4), outpatient.getString(5),
                            outpatient.getString(6), outpatient.getString(7));
                    System.out.println(line);
                } while (outpatient.next());
            }
        } catch (SQLException e) {
            // Pass exception
        }
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

        // Insert PATIENT TABLE
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

        // ? MUST Insert into OUTPATIENT or INPATIENT
        // TODO: Group discussion
        return;
    }
}