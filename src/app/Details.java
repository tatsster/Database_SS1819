package app;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.apache.commons.lang3.*;

public class Details {
    private Connection conn;
    private Statement statement;
    private ResultSet docIDset;
    private ResultSet patientList;
    private String docQuery = "SELECT EID_Doc FROM DOCTOR WHERE EID_Doc = ";
    private String patientQuery = "SELECT * FROM PATIENT, INPATIENT, TREATMENT WHERE PATIENT.PID = INPATIENT.PID_In AND INPATIENT.PID_In = TREATMENT.PID_In AND TREATMENT.EID_Doc = ";

    public Details(Connection conn) {
        this.conn = conn;
    }

    public int getPatientList(String docID) throws SQLException {
        statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        docIDset = statement.executeQuery(docQuery + docID);
        if (docIDset.next() == false) {
            System.out.println("Query failed: The given Doctor ID does not exist.");
            return -1;
        }

        patientList = statement.executeQuery(patientQuery + docID);
        if (patientList.next() == false) {
            System.out.println("Empty Result Relation: The given Doctor ID does not have any corresponding Patients.");
            return 0;
        } else {
            // ! Funny lines - Coded by Hai
            // ! Now i give up
            String line = "";
            for (int i = 0; i < 408; i++) {
                line += "-";
            }

            System.out.println(line);
            System.out.format("|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n", StringUtils.center("PID", 15),
                    StringUtils.center("First Name", 15), StringUtils.center("Last Name", 15),
                    StringUtils.center("Day of Birth", 20), StringUtils.center("Gender", 10),
                    StringUtils.center("Phone no.", 15), StringUtils.center("Address", 60),
                    StringUtils.center("Admission Date", 20), StringUtils.center("Discharge Date", 20),
                    StringUtils.center("Diagnosis", 30), StringUtils.center("Sickroom", 15),
                    StringUtils.center("Fee", 10), StringUtils.center("Caring Nurse ID", 20),
                    StringUtils.center("Treatment ID", 15), StringUtils.center("Treatment Start Date", 25),
                    StringUtils.center("Treatment End Date", 20), StringUtils.center("Result", 65));

            System.out.println(line);

            patientList.previous();
            while (patientList.next()) {
                System.out.format("|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|\n",
                        StringUtils.center(patientList.getString(1), 15),
                        StringUtils.center(patientList.getString(2), 15),
                        StringUtils.center(patientList.getString(3), 15),
                        StringUtils.center(patientList.getString(4).substring(0, 10), 20),
                        StringUtils.center(patientList.getString(5), 10),
                        StringUtils.center(patientList.getString(6), 15),
                        StringUtils.center(patientList.getString(7), 60),
                        StringUtils.center(patientList.getString(9).substring(0, 10), 20),
                        StringUtils.center(patientList.getString(10).substring(0, 10), 20),
                        StringUtils.center(patientList.getString(11), 30),
                        StringUtils.center(patientList.getString(12), 15),
                        StringUtils.center(patientList.getString(13), 10),
                        StringUtils.center(patientList.getString(15), 20),
                        StringUtils.center(patientList.getString(18), 15),
                        StringUtils.center(patientList.getString(19).substring(0, 10), 25),
                        StringUtils.center(patientList.getString(20).substring(0, 10), 20),
                        StringUtils.center(patientList.getString(21), 65));

                System.out.println(line);
            }
        }

        return 0;
    }
}
