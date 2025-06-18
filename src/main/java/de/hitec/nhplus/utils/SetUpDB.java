package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.datastorage.CareGiverDao;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.model.CareGiver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalTime;

/**
 * Call static class provides to static methods to set up and wipe the database. It uses the class ConnectionBuilder
 * and its path to build up the connection to the database. The class is executable. Executing the class will build
 * up a connection to the database and calls setUpDb() to wipe the database, build up a clean database and fill the
 * database with some test data.
 */
public class SetUpDB {

    /**
     * This method wipes the database by dropping the tables. Then the method calls DDL statements to build it up from
     * scratch and DML statements to fill the database with hard coded test data.
     */
    public static void setUpDb() {
        Connection connection = ConnectionBuilder.getConnection();
        SetUpDB.wipeDb(connection);
        SetUpDB.setUpTablePatient(connection);
        SetUpDB.setUpTableCareGiver(connection);
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpTableUsers(connection); // NEU: Users Tabelle
        SetUpDB.setUpPatients();
        SetUpDB.setUpCareGivers();
        SetUpDB.setUpTreatments();
        SetUpDB.setUpUsers(); // NEU: Standard Users erstellen
    }

    /**
     * This method wipes the database by dropping the tables.
     * @param connection The DB connection to use.
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS patient");
            statement.execute("DROP TABLE IF EXISTS treatment");
            statement.execute("DROP TABLE IF EXISTS care_giver");
            statement.execute("DROP TABLE IF EXISTS users"); // NEU: Users Tabelle auch löschen
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Sets up the patient table
     * @param connection The DB connection to use.
     * */
    private static void setUpTablePatient(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS patient (" +
                "   pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   dateOfBirth TEXT NOT NULL, " +
                "   carelevel TEXT NOT NULL, " +
                "   roomnumber TEXT NOT NULL " +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Sets up the CareGiver table.
     * @param connection The DB connection to use.
     * */
    private static void setUpTableCareGiver(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS care_giver (" +
                "   cid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   telephone_number TEXT NOT NULL " +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Sets up the Treatment Table
     * @param connection The DB connection to use.
     * */
    private static void setUpTableTreatment(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS treatment (" +
                "   tid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   pid INTEGER NOT NULL, " +
                "   caregiver_id INTEGER, " +
                "   treatment_date TEXT NOT NULL, " +
                "   begin TEXT NOT NULL, " +
                "   end TEXT NOT NULL, " +
                "   description TEXT NOT NULL, " +
                "   remark TEXT NOT NULL," +
                "   FOREIGN KEY (pid) REFERENCES patient (pid) ON DELETE CASCADE, " +
                "   FOREIGN KEY (caregiver_id) REFERENCES care_giver (cid) ON DELETE CASCADE " +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * NEU: Sets up the Users Table for Login System
     * @param connection The DB connection to use.
     */
    private static void setUpTableUsers(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS users (" +
                "   uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   username TEXT UNIQUE NOT NULL, " +
                "   password_hash TEXT NOT NULL, " +
                "   first_name TEXT NOT NULL, " +
                "   last_name TEXT NOT NULL, " +
                "   is_admin INTEGER DEFAULT 0, " +
                "   is_active INTEGER DEFAULT 1, " +
                "   created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "   last_login TEXT " +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
            System.out.println("Users Tabelle erfolgreich erstellt!");
        } catch (SQLException exception) {
            System.out.println("Fehler beim Erstellen der Users Tabelle: " + exception.getMessage());
        }
    }

    /**
     * Creates dummy data for the patients table
     * */
    private static void setUpPatients() {
        try {
            PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
            dao.create(new Patient("Seppl", "Herberger", convertStringToLocalDate("1945-12-01"), "4", "202"));
            dao.create(new Patient("Martina", "Gerdsen", convertStringToLocalDate("1954-08-12"), "5", "010"));
            dao.create(new Patient("Gertrud", "Franzen", convertStringToLocalDate("1949-04-16"), "3", "002"));
            dao.create(new Patient("Ahmet", "Yilmaz", convertStringToLocalDate("1941-02-22"), "3", "013"));
            dao.create(new Patient("Hans", "Neumann", convertStringToLocalDate("1955-12-12"), "2", "001"));
            dao.create(new Patient("Elisabeth", "Müller", convertStringToLocalDate("1958-03-07"), "5", "110"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Creates dummy data for the CareGivers table
     * */
    private static void setUpCareGivers() {
        try {
            CareGiverDao dao = DaoFactory.getDaoFactory().createCareGiverDAO();
            dao.create(new CareGiver("Max","Musterman","503"));
            dao.create(new CareGiver("Jannik","Vogler","504"));
            dao.create(new CareGiver("Ibrahim","Qedirli","505"));
            dao.create(new CareGiver("Christian","Tonn","506"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Creates dummy data for the treatments table
     * */
    private static void setUpTreatments() {
        try {
            TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
            dao.create(new Treatment(1, 1, convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Der Patient hat enorme Angstgefühle und glaubt, er sei überfallen worden. Ihm seien alle Wertsachen gestohlen worden.\nPatient beruhigt sich erst, als alle Wertsachen im Zimmer gefunden worden sind."));
            dao.create(new Treatment(1, 1, convertStringToLocalDate("2023-06-05"), convertStringToLocalTime("11:00"), convertStringToLocalTime("12:30"), "Gespräch", "Patient irrt auf der Suche nach gestohlenen Wertsachen durch die Etage und bezichtigt andere Bewohner des Diebstahls.\nPatient wird in seinen Raum zurückbegleitet und erhält Beruhigungsmittel."));
            dao.create(new Treatment(2, 1, convertStringToLocalDate("2023-06-04"), convertStringToLocalTime("07:30"), convertStringToLocalTime("08:00"), "Waschen", "Patient mit Waschlappen gewaschen und frisch angezogen. Patient gewendet."));
            dao.create(new Treatment(1, 1, convertStringToLocalDate("2023-06-06"), convertStringToLocalTime("15:10"), convertStringToLocalTime("16:00"), "Spaziergang", "Spaziergang im Park, Patient döst  im Rollstuhl ein"));
            dao.create(new Treatment(1, 2, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("16:00"), "Spaziergang", "Parkspaziergang; Patient ist heute lebhafter und hat klare Momente; erzählt von seiner Tochter"));
            dao.create(new Treatment(2, 2, convertStringToLocalDate("2023-06-07"), convertStringToLocalTime("11:00"), convertStringToLocalTime("11:30"), "Waschen", "Waschen per Dusche auf einem Stuhl; Patientin gewendet;"));
            dao.create(new Treatment(5, 3, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("15:30"), "Physiotherapie", "Übungen zur Stabilisation und Mobilisierung der Rückenmuskulatur"));
            dao.create(new Treatment(4, 3, convertStringToLocalDate("2023-08-24"), convertStringToLocalTime("09:30"), convertStringToLocalTime("10:15"), "KG", "Lympfdrainage"));
            dao.create(new Treatment(6, 4, convertStringToLocalDate("2023-08-31"), convertStringToLocalTime("13:30"), convertStringToLocalTime("13:45"), "Toilettengang", "Hilfe beim Toilettengang; Patientin klagt über Schmerzen beim Stuhlgang. Gabe von Iberogast"));
            dao.create(new Treatment(6, 4, convertStringToLocalDate("2023-09-01"), convertStringToLocalTime("16:00"), convertStringToLocalTime("17:00"), "KG", "Massage der Extremitäten zur Verbesserung der Durchblutung"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * NEU: Creates initial users for the login system
     */
    private static void setUpUsers() {
        Connection connection = ConnectionBuilder.getConnection();
        try {
            // Standard Admin-Benutzer erstellen
            String adminUsername = "admin";
            String adminPassword = "Admin123+";
            String adminPasswordHash = hashPassword(adminPassword);

            String insertAdminSQL = "INSERT OR IGNORE INTO users (username, password_hash, first_name, last_name, is_admin, is_active) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertAdminSQL)) {
                stmt.setString(1, adminUsername);
                stmt.setString(2, adminPasswordHash);
                stmt.setString(3, "System");
                stmt.setString(4, "Administrator");
                stmt.setInt(5, 1); // is_admin = true
                stmt.setInt(6, 1); // is_active = true
                stmt.executeUpdate();
                System.out.println("✅ Standard Admin-Benutzer erstellt: " + adminUsername + " / " + adminPassword);
            }

            // Standard Pfleger-Benutzer erstellen
            String userUsername = "pfleger1";
            String userPassword = "Pfleger123+";
            String userPasswordHash = hashPassword(userPassword);

            String insertUserSQL = "INSERT OR IGNORE INTO users (username, password_hash, first_name, last_name, is_admin, is_active) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertUserSQL)) {
                stmt.setString(1, userUsername);
                stmt.setString(2, userPasswordHash);
                stmt.setString(3, "Max");
                stmt.setString(4, "Mustermann");
                stmt.setInt(5, 0); // is_admin = false
                stmt.setInt(6, 1); // is_active = true
                stmt.executeUpdate();
                System.out.println("✅ Standard Pfleger-Benutzer erstellt: " + userUsername + " / " + userPassword);
            }

        } catch (SQLException exception) {
            System.out.println("❌ Fehler beim Erstellen der Standard-Benutzer: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Erstellt einen SHA-256 Hash für Passwörter
     * @param password Das zu hashende Passwort
     * @return Der SHA-256 Hash als String
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithmus nicht verfügbar", e);
        }
    }

    /**
     * main method to start db setup
     * */
    public static void main(String[] args) {
        SetUpDB.setUpDb();
    }
}