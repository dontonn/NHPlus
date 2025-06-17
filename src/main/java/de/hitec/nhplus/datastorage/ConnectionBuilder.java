package de.hitec.nhplus.datastorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;
/**
 *  Handles the Connection to the Database.
 *  Open/Closes the Connection to the Database and is used to Configure the connection.
 * */
public class ConnectionBuilder {

    private static final String DB_NAME = "nursingHome.db";
    private static final String URL = "jdbc:sqlite:db/" + DB_NAME;

    private static Connection connection;


    /**
     * Handles Opening the Connection to the Database.
     * Opens and Configures the Connection to the Database
     * @return Returns the Connection.
     * */
    synchronized public static Connection getConnection() {
        try {
            if (ConnectionBuilder.connection == null) {
                SQLiteConfig configuration = new SQLiteConfig();
                configuration.enforceForeignKeys(true);
                ConnectionBuilder.connection = DriverManager.getConnection(URL, configuration.toProperties());
            }
        } catch (SQLException exception) {
            System.out.println("Verbindung zur Datenbank konnte nicht aufgebaut werden!");
            exception.printStackTrace();
        }
        return ConnectionBuilder.connection;
    }

    /**
     * Handles closing the connection to the Database.
     * Trys to close the connection to the Database.
     * */
    synchronized public static void closeConnection() {
        try {
            if (ConnectionBuilder.connection != null) {
                ConnectionBuilder.connection.close();
                ConnectionBuilder.connection = null;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
