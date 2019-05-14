package app;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    // Random ID
    private String DOCTOR_rand = "SELECT * FROM (SELECT * FROM DOCTOR ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1";
    private String NURSE_rand = "SELECT * FROM (SELECT * FROM NURSE ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1";
    private String MID_rand = "SELECT MID FROM (SELECT * FROM MEDICATION ORDER BY DBMS_RANDOM.VALUE) WHERE ROWNUM = 1";

    // New INpatient
    private String addINPATIENT = "INSERT INTO INPATIENT (PID_IN, PADMISSIONDATE, PDISCHARGEDATE, PSICKROOM, PFEE, EID_DOC, EID_NUR) VALUES (?, TO_DATE(?, 'dd/mm/yyyy'), TO_DATE(?, 'dd/mm/yyyy'), ?, ?, ?, ?)";
    private String addTREATMENT = "INSERT INTO TREATMENT (EID_DOC, PID_IN, TRID) VALUES (?, ?, ?)";
    private String addUSES_TREAT = "INSERT INTO USES_TREAT VALUES (?, ?, ?, ?)";
    // New OUTpatient
    private String addOUTPATIENT = "INSERT INTO OUTPATIENT (PID_OUT, EID_DOC) VALUES (?, ?)";
    private String addEXAMINATION = "INSERT INTO EXAMINATION (EID_DOC, PID_OUT, EXID, EXDATE, EXFEE) VALUES (?, ?, ?, TO_DATE(?, 'dd/mm/yyyy'), ?)";
    private String addUSES_EXAM = "INSERT INTO USES_EXAM VALUES (?, ?, ?, ?)";

    // Constructor
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

    private void INPATIENT(String pid) throws SQLException {
        // ! Inpatient -> INPATIENT, TREATMENT, USE_TREAT
        // TODO: Get random DOC_ID, NURSE_ID, MID
        ResultSet doctor = statement.executeQuery(DOCTOR_rand);
        ResultSet nurse = statement.executeQuery(NURSE_rand);
        ResultSet medication = statement.executeQuery(MID_rand);
        doctor.next();
        nurse.next();
        medication.next();
        String DOC_ID = doctor.getString(1);
        String NURSE_ID = nurse.getString(1);
        int MID = medication.getInt(1);

        // Admision Date - Today
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/mm/yyyy");
        LocalDate today = LocalDate.now();
        String admissionDate = dtf.format(today);

        // Discharge Date
        LocalDate nextday = today.plusDays(30);
        String dischargeDate = dtf.format(nextday);

        System.out.println("------------------- Required Information -------------------");
        System.out.print("Sick room: ");
        String room = sc.next();
        System.out.print("Fee: ");
        int fee = sc.nextInt();
        System.out.print("Treatment ID: ");
        int TRID = sc.nextInt();
        System.out.println("------------------------------------------------------------");

        // add INPATIENT - update DIAGNOSIS later
        PreparedStatement inpatient = connection.prepareStatement(addINPATIENT);
        inpatient.setString(1, pid);
        inpatient.setString(2, admissionDate);
        inpatient.setString(3, dischargeDate);
        inpatient.setString(4, room);
        inpatient.setInt(5, fee);
        inpatient.setString(6, DOC_ID);
        inpatient.setString(7, NURSE_ID);
        int check = inpatient.executeUpdate();
        if (check == 1)
            inpatient.close();
        else {
            System.out.print("ERROR while adding new Inpatient !!!");
            return;
        }

        // add TREATMENT - update TRSTART, TREND, TRRESULT later
        PreparedStatement treatment = connection.prepareStatement(addTREATMENT);
        treatment.setString(1, DOC_ID);
        treatment.setString(2, pid);
        treatment.setInt(3, TRID);
        check = treatment.executeUpdate();
        if (check == 1)
            treatment.close();
        else {
            System.out.println("ERROR while update Treatment information for Inpatient !!!");
            return;
        }

        // add USES_TREAT
        PreparedStatement uses_treat = connection.prepareStatement(addUSES_TREAT);
        uses_treat.setString(1, DOC_ID);
        uses_treat.setString(2, pid);
        uses_treat.setInt(3, TRID);
        uses_treat.setInt(4, MID);
        check = uses_treat.executeUpdate();
        if (check == 1)
            uses_treat.close();
        else {
            System.out.println("ERROR while update Treatment Medication !!!");
            return;
        }
    }

    private void OUTPATIENT(String pid) throws SQLException {
        // ! Outpatient -> OUTPATIENT, EXAMINATION, USE_EXAM
        ResultSet doctor = statement.executeQuery(DOCTOR_rand);
        ResultSet medication = statement.executeQuery(MID_rand);
        doctor.next();
        medication.next();
        String DOC_ID = doctor.getString(1);
        int MID = medication.getInt(1);

        // Examinate Date - Today
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/mm/yyyy");
        LocalDate today = LocalDate.now();
        String EXDATE = dtf.format(today);

        // Form
        System.out.flush();
        System.out.println("------------------- Required Information -------------------");
        System.out.print("Examination ID: ");
        int EXID = sc.nextInt();
        System.out.print("Fee: ");
        int fee = sc.nextInt();
        System.out.println("------------------------------------------------------------");

        // add OUTPATIENT
        PreparedStatement outpatient = connection.prepareStatement(addOUTPATIENT);
        outpatient.setString(1, pid);
        outpatient.setString(2, DOC_ID);
        int check = outpatient.executeUpdate();
        if (check == 1)
            outpatient.close();
        else {
            System.out.println("ERROR while adding new Outpatient !!!");
            return;
        }

        // add EXAMINATION
        PreparedStatement exam = connection.prepareStatement(addEXAMINATION);
        exam.setString(1, DOC_ID);
        exam.setString(2, pid);
        exam.setInt(3, EXID);
        exam.setString(4, EXDATE);
        exam.setInt(5, fee);
        check = exam.executeUpdate();
        if (check == 1)
            exam.close();
        else {
            System.out.println("ERROR while updating new Examination !!!");
            return;
        }

        // add USES_EXAM
        PreparedStatement uses_exam = connection.prepareStatement(addUSES_EXAM);
        uses_exam.setString(1, DOC_ID);
        uses_exam.setString(2, pid);
        uses_exam.setInt(3, EXID);
        uses_exam.setInt(4, MID);
        check = uses_exam.executeUpdate();
        if (check == 1)
            uses_exam.close();
        else {
            System.out.println("ERROR while update Examination Medication !!!");
            return;
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

        // Get ID from ALL DOCTOR AND NURSE

        // ? MUST Insert into OUTPATIENT or INPATIENT
        // TODO: Group discussion
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------------ Patient Type -------------------------");
        System.out.println("-> 1. Inpatient  - Take Medical Care");
        System.out.println("-> 2. Outpatient - Examinate Condition");
        System.out.println("---------------------------------------------------------------");
        System.out.print("Type: ");
        int type = sc.nextInt();
        System.out.flush();
        System.out.println("---------------------------------------------------------------");

        if (type == 1) {
            INPATIENT(pid);
        } else {
            OUTPATIENT(pid);
        }
        return;
    }
}