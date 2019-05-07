package app;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] argv) {
        System.out.println("-------- Oracle JDBC Connection Testing ------");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }
        System.out.println("Oracle JDBC Driver Registered!");
        
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@50anhoi.ddns.net:1521:orcl", "Manager", "123456789");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        try {
        connection.close();
        } catch (SQLException e) {
            System.out.println("Failed to logout");
        }
    }
}