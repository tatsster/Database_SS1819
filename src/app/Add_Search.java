// SELECT pid, pfname, plname, pphone, eid_doc, trid, trstart, trend, trresult
// FROM account1.patient
// INNER JOIN account1.treatment ON patient.PID = TREATMENT.PID_IN
// WHERE plname = 'Smith';

package app;

import java.sql.*;
import java.util.*;

public class Add_Search {
    private Connection connection;
    private Scanner sc;

    public Add_Search(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    public void Search(String name) {
        // Search by Name (Check in both LName and FName)
        // Result: Name, Phone, Treatment info (Inpatient)

    }

    public void Add() 
    {
        System.out.println("Patient ID: ");
        String pid = sc.next();
        
    }
}