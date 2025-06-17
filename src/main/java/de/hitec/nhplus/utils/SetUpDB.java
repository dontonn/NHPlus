package de.hitec.nhplus.utils;

import de.hitec.nhplus.datastorage.ConnectionBuilder;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.model.Caregiver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalDate;
import static de.hitec.nhplus.utils.DateConverter.convertStringToLocalTime;

/**
 * Extended SetUpDB - minimal changes to add Caregiver support
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
        SetUpDB.setUpTableTreatment(connection);
        SetUpDB.setUpTableCaregiver(connection);  // NEW: Add caregiver table
        SetUpDB.setUpPatients();
        SetUpDB.setUpTreatments();
        SetUpDB.setUpCaregivers();  // NEW: Add caregiver data
    }

    /**
     * This method wipes the database by dropping the tables.
     */
    public static void wipeDb(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS patient");
            statement.execute("DROP TABLE IF EXISTS treatment");
            statement.execute("DROP TABLE IF EXISTS caregiver");  // NEW
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTablePatient(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS patient (" +
                "   pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   dateOfBirth TEXT NOT NULL, " +
                "   carelevel TEXT NOT NULL, " +
                "   roomnumber TEXT NOT NULL, " +
                "   assets TEXT NOT NULL" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpTableTreatment(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS treatment (" +
                "   tid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   pid INTEGER NOT NULL, " +
                "   treatment_date TEXT NOT NULL, " +
                "   begin TEXT NOT NULL, " +
                "   end TEXT NOT NULL, " +
                "   description TEXT NOT NULL, " +
                "   remark TEXT NOT NULL," +
                "   FOREIGN KEY (pid) REFERENCES patient (pid) ON DELETE CASCADE " +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * NEW: Create caregiver table
     */
    private static void setUpTableCaregiver(Connection connection) {
        final String SQL = "CREATE TABLE IF NOT EXISTS caregiver (" +
                "   pid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "   username TEXT NOT NULL UNIQUE, " +
                "   firstname TEXT NOT NULL, " +
                "   surname TEXT NOT NULL, " +
                "   dateOfBirth TEXT NOT NULL, " +
                "   telephoneNumber TEXT NOT NULL, " +
                "   isAdmin BOOLEAN NOT NULL DEFAULT 0, " +
                "   password_hash TEXT NOT NULL, " +
                "   locked BOOLEAN DEFAULT 0, " +
                "   lockedDate TEXT" +
                ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void setUpPatients() {
        try {
            PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
            dao.create(new Patient("Seppl", "Herberger", convertStringToLocalDate("1945-12-01"), "4", "202", "vermögend"));
            dao.create(new Patient("Martina", "Gerdsen", convertStringToLocalDate("1954-08-12"), "5", "010", "arm"));
            dao.create(new Patient("Gertrud", "Franzen", convertStringToLocalDate("1949-04-16"), "3", "002", "normal"));
            dao.create(new Patient("Ahmet", "Yilmaz", convertStringToLocalDate("1941-02-22"), "3", "013", "normal"));
            dao.create(new Patient("Hans", "Neumann", convertStringToLocalDate("1955-12-12"), "2", "001", "sehr vermögend"));
            dao.create(new Patient("Elisabeth", "Müller", convertStringToLocalDate("1958-03-07"), "5", "110", "arm"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void setUpTreatments() {
        try {
            TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
            dao.create(new Treatment(1, 1, convertStringToLocalDate("2023-06-03"), convertStringToLocalTime("11:00"), convertStringToLocalTime("15:00"), "Gespräch", "Der Patient hat enorme Angstgefühle und glaubt, er sei überfallen worden. Ihm seien alle Wertsachen gestohlen worden.\nPatient beruhigt sich erst, als alle Wertsachen im Zimmer gefunden worden sind."));
            dao.create(new Treatment(2, 1, convertStringToLocalDate("2023-06-05"), convertStringToLocalTime("11:00"), convertStringToLocalTime("12:30"), "Gespräch", "Patient irrt auf der Suche nach gestohlenen Wertsachen durch die Etage und bezichtigt andere Bewohner des Diebstahls.\nPatient wird in seinen Raum zurückbegleitet und erhält Beruhigungsmittel."));
            dao.create(new Treatment(3, 2, convertStringToLocalDate("2023-06-04"), convertStringToLocalTime("07:30"), convertStringToLocalTime("08:00"), "Waschen", "Patient mit Waschlappen gewaschen und frisch angezogen. Patient gewendet."));
            dao.create(new Treatment(4, 1, convertStringToLocalDate("2023-06-06"), convertStringToLocalTime("15:10"), convertStringToLocalTime("16:00"), "Spaziergang", "Spaziergang im Park, Patient döst  im Rollstuhl ein"));
            dao.create(new Treatment(8, 1, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("16:00"), "Spaziergang", "Parkspaziergang; Patient ist heute lebhafter und hat klare Momente; erzählt von seiner Tochter"));
            dao.create(new Treatment(9, 2, convertStringToLocalDate("2023-06-07"), convertStringToLocalTime("11:00"), convertStringToLocalTime("11:30"), "Waschen", "Waschen per Dusche auf einem Stuhl; Patientin gewendet;"));
            dao.create(new Treatment(12, 5, convertStringToLocalDate("2023-06-08"), convertStringToLocalTime("15:00"), convertStringToLocalTime("15:30"), "Physiotherapie", "Übungen zur Stabilisation und Mobilisierung der Rückenmuskulatur"));
            dao.create(new Treatment(14, 4, convertStringToLocalDate("2023-08-24"), convertStringToLocalTime("09:30"), convertStringToLocalTime("10:15"), "KG", "Lympfdrainage"));
            dao.create(new Treatment(16, 6, convertStringToLocalDate("2023-08-31"), convertStringToLocalTime("13:30"), convertStringToLocalTime("13:45"), "Toilettengang", "Hilfe beim Toilettengang; Patientin klagt über Schmerzen beim Stuhlgang. Gabe von Iberogast"));
            dao.create(new Treatment(17, 6, convertStringToLocalDate("2023-09-01"), convertStringToLocalTime("16:00"), convertStringToLocalTime("17:00"), "KG", "Massage der Extremitäten zur Verbesserung der Durchblutung"));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * NEW: Create test caregivers for login system
     */
    private static void setUpCaregivers() {
        try {
            CaregiverDao dao = DaoFactory.getDaoFactory().createCaregiverDAO();

            // Admin User (password: Admin123+)
            dao.create(new Caregiver("admin", "Admin", "Administrator",
                    convertStringToLocalDate("1990-01-01"), "0123-456-789", "Admin123+", true, false, null));

            // Regular User (password: User123-)
            dao.create(new Caregiver("user", "Test", "User",
                    convertStringToLocalDate("1985-05-15"), "0987-654-321", "User123-", false, false, null));

            System.out.println("✅ Caregiver test data created!");
            System.out.println("📋 Login credentials:");
            System.out.println("   🔑 Admin: admin / Admin123+");
            System.out.println("   👤 User: user / User123-");

        } catch (SQLException exception) {
            System.err.println("❌ Error creating caregivers:");
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("🚀 Setting up NHPlus database...");
        SetUpDB.setUpDb();
        System.out.println("✅ Database setup complete!");
    }
}