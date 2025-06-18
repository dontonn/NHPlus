package de.hitec.nhplus.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model class representing a user in the login system
 */
public class User {
    private SimpleLongProperty uid;
    private final SimpleStringProperty username;
    private final SimpleStringProperty passwordHash;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleBooleanProperty isAdmin;
    private final SimpleBooleanProperty isActive;
    private final SimpleStringProperty createdAt;
    private final SimpleStringProperty lastLogin;

    /**
     * Constructor for creating a new user (without ID)
     */
    public User(String username, String passwordHash, String firstName, String lastName, boolean isAdmin, boolean isActive) {
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
        this.isActive = new SimpleBooleanProperty(isActive);
        this.createdAt = new SimpleStringProperty("");
        this.lastLogin = new SimpleStringProperty("");
    }

    /**
     * Constructor for existing user (with ID)
     */
    public User(long uid, String username, String passwordHash, String firstName, String lastName,
                boolean isAdmin, boolean isActive, String createdAt, String lastLogin) {
        this.uid = new SimpleLongProperty(uid);
        this.username = new SimpleStringProperty(username);
        this.passwordHash = new SimpleStringProperty(passwordHash);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
        this.isActive = new SimpleBooleanProperty(isActive);
        this.createdAt = new SimpleStringProperty(createdAt != null ? createdAt : "");
        this.lastLogin = new SimpleStringProperty(lastLogin != null ? lastLogin : "");
    }

    // Getters
    public long getUid() {
        return uid != null ? uid.get() : -1;
    }

    public String getUsername() {
        return username.get();
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public boolean isAdmin() {
        return isAdmin.get();
    }

    public boolean isActive() {
        return isActive.get();
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    public String getLastLogin() {
        return lastLogin.get();
    }

    // Property getters for TableView
    public SimpleLongProperty uidProperty() {
        return uid;
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public SimpleBooleanProperty isAdminProperty() {
        return isAdmin;
    }

    public SimpleBooleanProperty isActiveProperty() {
        return isActive;
    }

    public SimpleStringProperty createdAtProperty() {
        return createdAt;
    }

    public SimpleStringProperty lastLoginProperty() {
        return lastLogin;
    }

    // Setters
    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin.set(isAdmin);
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin.set(lastLogin != null ? lastLogin : "");
    }

    /**
     * Returns the full name of the user
     */
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    /**
     * Returns the role of the user
     */
    public String getRole() {
        return isAdmin.get() ? "Administrator" : "Pfleger";
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + (uid != null ? uid.get() : "new") +
                ", username='" + username.get() + '\'' +
                ", firstName='" + firstName.get() + '\'' +
                ", lastName='" + lastName.get() + '\'' +
                ", isAdmin=" + isAdmin.get() +
                ", isActive=" + isActive.get() +
                '}';
    }
}