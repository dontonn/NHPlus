package de.hitec.nhplus.model;

import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * Caregiver model class representing a nurse/caregiver in the system
 */
public class Caregiver extends Person {
    // The unique identifier for the Caregiver
    private SimpleLongProperty pid;
    // The username of the Caregiver
    private final SimpleStringProperty username;
    // The telephone number of the Caregiver
    private final SimpleStringProperty telephoneNumber;
    // The date of birth of the Caregiver
    private final SimpleStringProperty dateOfBirth;
    // A flag indicating whether the Caregiver is an admin
    private final BooleanProperty isAdmin;
    // The hashed password of the Caregiver
    private final SimpleStringProperty password_hash;

    // A flag indicating whether the Caregiver is locked
    private boolean locked;
    // The date when the Caregiver was locked
    private String lockedDate;

    /**
     * Constructor for creating a new Caregiver (not yet persisted in database).
     * The password will be automatically hashed.
     *
     * @param username The username of the Caregiver.
     * @param firstName The first name of the Caregiver.
     * @param surname The surname of the Caregiver.
     * @param dateOfBirth The date of birth of the Caregiver.
     * @param telephoneNumber The telephone number of the Caregiver.
     * @param password The plain text password of the Caregiver (will be hashed).
     * @param isAdmin A flag indicating whether the Caregiver is an admin.
     * @param locked A flag indicating whether the Caregiver is locked.
     * @param lockedDate The date when the Caregiver was locked.
     */
    public Caregiver(String username, String firstName, String surname, LocalDate dateOfBirth,
                     String telephoneNumber, String password, boolean isAdmin, boolean locked, String lockedDate) {
        super(firstName, surname);
        this.username = new SimpleStringProperty(username);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.telephoneNumber = new SimpleStringProperty(telephoneNumber);
        this.password_hash = new SimpleStringProperty(PasswordUtil.generatePassword(password));
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
        this.locked = locked;
        this.lockedDate = lockedDate;
    }

    /**
     * Constructor for creating a Caregiver from database data.
     * The password is assumed to be already hashed.
     *
     * @param pid The unique identifier for the Caregiver.
     * @param username The username of the Caregiver.
     * @param firstName The first name of the Caregiver.
     * @param surname The surname of the Caregiver.
     * @param dateOfBirth The date of birth of the Caregiver.
     * @param telephoneNumber The telephone number of the Caregiver.
     * @param password_hash The already hashed password of the Caregiver.
     * @param isAdmin A flag indicating whether the Caregiver is an admin.
     */
    public Caregiver(long pid, String username, String firstName, String surname, LocalDate dateOfBirth,
                     String telephoneNumber, String password_hash, boolean isAdmin) {
        super(firstName, surname);
        this.pid = new SimpleLongProperty(pid);
        this.username = new SimpleStringProperty(username);
        this.dateOfBirth = new SimpleStringProperty(DateConverter.convertLocalDateToString(dateOfBirth));
        this.telephoneNumber = new SimpleStringProperty(telephoneNumber);
        this.password_hash = new SimpleStringProperty(password_hash);
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
    }

    // Getter and Setter methods
    public long getPid() {
        return pid.get();
    }

    public SimpleLongProperty pidProperty() {
        return pid;
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public SimpleStringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    public String getPassword_hash() {
        return password_hash.get();
    }

    public SimpleStringProperty password_hashProperty() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash.set(password_hash);
    }

    public String getTelephoneNumber() {
        return telephoneNumber.get();
    }

    public SimpleStringProperty telephoneNumberProperty() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber.set(telephoneNumber);
    }

    public boolean isAdmin() {
        return isAdmin.get();
    }

    public BooleanProperty isAdminProperty() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin.set(isAdmin);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(String lockedDate) {
        this.lockedDate = lockedDate;
    }

    @Override
    public String toString() {
        return "Caregiver\n" +
                "pid: " + pid.get() + "\n" +
                "firstName: " + getFirstName() + "\n" +
                "surname: " + getSurname() + "\n" +
                "dateOfBirth: " + dateOfBirth.get() + "\n" +
                "telephoneNumber: " + telephoneNumber.get() + "\n" +
                "username: " + username.get() + "\n" +
                "isAdmin: " + isAdmin.get() + "\n" +
                "password_hash: " + password_hash.get() +
                "\n";
    }
}