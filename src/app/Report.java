package app;
import java.sql.*;

public class Report {
	
	Connection connection;			// Database Connection.
	Statement qr; 		   			// Query Statement.
	ResultSet rs;					// The record we select.
	String report = "";				// The report we want.
	String leftAlignFormat;			// The string format we want.
	String leftAlignFormat_String;
	String leftAlignFormat_Final;
	
	public Report(Connection connection) {
		this.connection = connection;
		try {
			this.qr = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Statement Failed! Check your connection");
	        e.printStackTrace();
		}  
	}
	
	public String toString(String inputPID) throws SQLException {
		try {
			rs = qr.executeQuery("SELECT * FROM  PATIENT, (SELECT * FROM OUTPATIENT WHERE PID_OUT =" + inputPID + ") O WHERE PID = O.PID_OUT");
		} catch (SQLException e) {
			return "Wrong Patient ID !!!";
		}
		
		if(rs.next() == true) {
			
			leftAlignFormat = "| %-30s | %-72s |%n";
			System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
			System.out.format("|                                                                                                           |%n");
			System.out.format("| ---------------------------------------  INFORMATION OF PATIENT ------------------------------------------|%n");
			System.out.format("|                                                                                                           |%n");
			System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
			
			try {
				do {
					String EID = rs.getString("EID_DOC");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
					if(EID== null){// if you fetched null value then initialize output with blank string
					  EID= "";
					}
					System.out.format(leftAlignFormat," Patient's ID: " , rs.getString("PID"));
					System.out.format(leftAlignFormat," Patient's First Name: " , rs.getString("PFNAME"));
					System.out.format(leftAlignFormat," Patient's Last Name: " , rs.getString("PLNAME"));
					System.out.format(leftAlignFormat," Patient's Date Of Birth: " , rs.getString("PDOB"));
					System.out.format(leftAlignFormat," Patient's Gender: " , rs.getString("PGENDER"));
					System.out.format(leftAlignFormat," Patient's Phone: ",rs.getString("PPHONE"));
					System.out.format(leftAlignFormat," Patient's Examination Doctor:", EID);
					System.out.format(leftAlignFormat," Patient's Address: " ,(rs.getString("PADDRESS").replace('\n', '\0')).replace('\r', '\0'));		
				} 
				while(rs.next());
			} catch (SQLException e) {
				System.out.println("Query Failed! Check your connection or the valid of your query statement");
	            e.printStackTrace();
	            return "NULL";
			}
			try {
				rs = qr.executeQuery("SELECT EXID , EXDATE, EXDiAGNOSIS, MNAME, MEFFECTS, EXSECONDEXAMINATIONDATE, EXFEE, MPRICE FROM (SELECT * FROM  EXAMINATION E, "
								 + "(SELECT EID_DOC AS USES_EID_DOC, EXID AS USES_EXID, PID_OUT AS USES_PID_OUT, MNAME, MEFFECTS, MPRICE  FROM USES_EXAM U, "
								 + "MEDICATION M WHERE U.MID = M.MID ) A WHERE E.EID_DOC = A.USES_EID_DOC AND E.PID_OUT = A.USES_PID_OUT AND E.EXID = A.USES_EXID) F "
								 + "WHERE F.PID_OUT = " + inputPID);
			} catch (SQLException e) {
				System.out.println("Query Failed! Check your connection or the valid of your query statement");
	            e.printStackTrace();
	            return "NULL";
			}
			
			leftAlignFormat_String = "| %-15s | %-21s | %-40s | %-20s | %-50s | %-20s | %20s | %20s | %20s |%n";
				   leftAlignFormat = "| %-15s | %-21s | %-40s | %-20s | %-50s | %-23s | %20d | %20d | %20d |%n";
			 leftAlignFormat_Final = "| %-15s | %-21s | %-40s | %-20s | %-50s | %-23s | %20s | %20s | %20d |%n";
			 
			System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
							+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
			System.out.format("|                                                                                                                                                                     "
							+ "                                                                                          |%n");
			System.out.format("| ------------------------------------------------------------------------------------------------------------------  PAYMENT REPORT OF THE PATIENT ------------------"
							+ "------------------------------------------------------------------------------------------|%n");
			System.out.format("|                                                                                                                                                                     "
							+ "                                                                                          |%n");
			System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
							+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
			System.out.format(leftAlignFormat_String,"Examination ID", "Examination Date", "Examination Diagnosis", "Medication Name", "Medication Effects", 
					"Second Examination Date", "Examination Fee", "Medication Price", "Total");
			Long total = (long) 0;	
			
			try {
				while(rs.next()) {
					Long EXFEE = rs.getLong("EXFEE");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
					Long MPRICE = rs.getLong("MPRICE");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
					total = total +  EXFEE + MPRICE;
					System.out.format(leftAlignFormat,rs.getString("EXID"), rs.getString("EXDATE"), rs.getString("EXDIAGNOSIS"), rs.getString("MNAME"), 
							rs.getString("MEFFECTS"), rs.getString("EXSECONDEXAMINATIONDATE"), rs.getLong("EXFEE"), rs.getLong("MPRICE"),
							(EXFEE + MPRICE));
				}
				System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
				System.out.format(leftAlignFormat_Final," ", " ", " ", " "," ", " ", " "," " ,total);
				System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
			} catch (SQLException e) {
				System.out.println("Query Failed! Check your connection or the valid of your query statement");
	            e.printStackTrace();
	            return "NULL";
			}
		}
		else {		
			try {
				rs = qr.executeQuery("SELECT * FROM  PATIENT, (SELECT * FROM INPATIENT WHERE PID_IN =" + inputPID + ") I WHERE PID = I.PID_IN");
			} catch (SQLException e) {
				System.out.println("Query Failed! Check your connection or the valid of your query statement");
	            e.printStackTrace();
	            return "NULL";
			}
			
			if(rs.next() == true) {
				String leftAlignFormat = "| %-30s | %-72s |%n";
				System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
				System.out.format("|                                                                                                           |%n");
				System.out.format("| ---------------------------------------  INFORMATION OF PATIENT ------------------------------------------|%n");
				System.out.format("|                                                                                                           |%n");
				System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
				
				try {
					do {
						String EID = rs.getString("EID_DOC");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
						if(EID== null){// if you fetched null value then initialize output with blank string
						  EID= "";
						}			
						String NUR = rs.getString("EID_NUR");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
						if(NUR== null){// if you fetched null value then initialize output with blank string
						  NUR= "";
						}
						System.out.format(leftAlignFormat," Patient's ID: " , rs.getString("PID"));
						System.out.format(leftAlignFormat," Patient's First Name: " , rs.getString("PFNAME"));
						System.out.format(leftAlignFormat," Patient's Last Name: " , rs.getString("PLNAME"));
						System.out.format(leftAlignFormat," Patient's Date Of Birth: " , rs.getString("PDOB"));
						System.out.format(leftAlignFormat," Patient's Gender: " , rs.getString("PGENDER"));
						System.out.format(leftAlignFormat," Patient's Phone: ",rs.getString("PPHONE"));
						System.out.format(leftAlignFormat," Patient's Treatment Doctor:", EID);
						System.out.format(leftAlignFormat," Patient's Treatment Nurse:", NUR);
						System.out.format(leftAlignFormat," Patient's Address: " ,(rs.getString("PADDRESS").replace('\n', '\0')).replace('\r', '\0'));		
					} 
					while(rs.next());
				} catch (SQLException e) {
					System.out.println("Query Failed! Check your connection or the valid of your query statement");
		            e.printStackTrace();
		            return "NULL";
				}
				
				try {
					rs = qr.executeQuery("SELECT TRID , TRSTART, TREND, TRRESULT, PADMISSIONDATE, PDISCHARGEDATE, PDIAGNOSIS, PSICKROOM, MNAME, MEFFECTS, PFEE, MPRICE "
							 + "FROM INPATIENT, (SELECT * FROM  TREATMENT T, (SELECT EID_DOC AS USES_EID_DOC, TRID AS USES_TRID, PID_IN AS USES_PID_IN, MNAME, MEFFECTS, "
							 + "MPRICE  FROM USES_TREAT U, MEDICATION M WHERE U.MID = M.MID ) A WHERE T.EID_DOC = A.USES_EID_DOC AND "
							 + "T.PID_IN = A.USES_PID_IN AND T.TRID = A.USES_TRID) F WHERE F.PID_IN = " + inputPID);
				} catch (SQLException e) {
					System.out.println("Query Failed! Check your connection or the valid of your query statement");
		            e.printStackTrace();
		            return "NULL";
				}	
				
				leftAlignFormat_String = "| %-15s | %-21s | %-40s | %-21s | %-50s | %-23s | %-21s | %-20s | %-20s | %-40s | %20s | %20s | %20s |%n";
					   leftAlignFormat = "| %-15s | %-21s | %-40s | %-21s | %-50s | %-23s | %-21s | %-20s | %-20s | %-40s | %20d | %20d | %20d |%n";
				 leftAlignFormat_Final = "| %-15s | %-21s | %-40s | %-21s | %-50s | %-23s | %-21s | %-20s | %-20s | %-40s | %20s | %20s | %20d |%n";
				
				System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++%n");
				System.out.format("|                                                                                                                                                                 "
								+ "                                                                                                                                                                  "
								+ "                                              |%n");
				System.out.format("| ----------------------------------------------------------------------------------------------------------------------------------------------------------------"
								+ "-----------------  PAYMENT REPORT OF THE PATIENT -----------------------------------------------------------------------------------------------------------------"
								+ "----------------------------------------------|%n");
				System.out.format("|                                                                                                                                                                 "
								+ "                                                                                                                                                                  "
								+ "                                              |%n");
				System.out.format("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
								+ "+++++++++++++++++++++++++++++++++++++++++++++++%n");
				System.out.format(leftAlignFormat_String,"Treatment ID", "Treatment Start", "Treatment End", "Treatment Result", "Admission Date", "Discharge Date", 
						"Treatment Diagnosis", "Treatment Sickroom", "Medication Name", "Medication Effects", "Treatment Fee", "Medication Price", "Total");
				Long total = (long) 0;
				
				try {
					while(rs.next()) {				
						Long PFEE = rs.getLong("PFEE");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
						Long MPRICE = rs.getLong("MPRICE");// if data is null `output` would be null, so there is no chance of NPE unless `rs` is `null`
						total = total +  PFEE + MPRICE;
						System.out.format(leftAlignFormat,rs.getString("TRID"), rs.getString("TRSTART"), rs.getString("TREND"), rs.getString("TRRESULT"), 
								rs.getString("PADMISSIONDATE"), rs.getString("PDISCHARGEDATE"),  rs.getString("PDIAGNOSIS"), rs.getString("PSICKROOM"), 
								rs.getString("MNAME"), rs.getString("MEFFECTS"), rs.getLong("PFEE"), rs.getLong("MPRICE"), (PFEE + MPRICE));
					}
					
					System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
									+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
									+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
					System.out.format(leftAlignFormat_Final," ", " ", " ", " "," ", " ", " "," "," "," "," "," ",total);
					System.out.format("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
									+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
									+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++%n");
				} catch (SQLException e) {
					System.out.println("Query Failed! Check your connection or the valid of your query statement");
		            e.printStackTrace();
		            return "NULL";
				}
			}
			else
				return "Wrong Patient ID !!!";
		}
		return "\n";
	}
}	
