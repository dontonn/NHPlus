package de.hitec.nhplus.service;

import de.hitec.nhplus.model.User;

/**
 * Session management for the logged-in user
 */
public class Session {
    private static Session instance;
    private User currentUser;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }

    public String getCurrentUsername() {
        return isLoggedIn() ? currentUser.getUsername() : "Gast";
    }

    public String getCurrentUserFullName() {
        return isLoggedIn() ? currentUser.getFullName() : "Gast";
    }
}