package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the login view
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Clear status label initially
        statusLabel.setText("");

        // Focus on username field
        Platform.runLater(() -> usernameField.requestFocus());
    }

    /**
     * Handle key press events (Enter key for login)
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Bitte Benutzername und Passwort eingeben.");
            return;
        }

        // Disable button and show loading
        loginButton.setDisable(true);
        loginButton.setText("🔄 Überprüfe...");
        statusLabel.setText("");

        // Perform login in background thread
        Task<Boolean> loginTask = new Task<Boolean>() {
            private User authenticatedUser = null;

            @Override
            protected Boolean call() throws Exception {
                try {
                    UserDao userDao = DaoFactory.getDaoFactory().createUserDAO();
                    User user = userDao.findByUsername(username);

                    if (user != null && user.isActive() && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                        // Update last login
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        userDao.updateLastLogin(user.getUid(), timestamp);

                        authenticatedUser = user;
                        return true;
                    }
                    return false;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Anmelden");

                    if (getValue()) {
                        // Login successful
                        Session.getInstance().login(authenticatedUser);
                        showSuccess("✅ Anmeldung erfolgreich! Weiterleitung...");

                        // Redirect after short delay
                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(1500);
                                redirectToMainWindow();
                            } catch (InterruptedException e) {
                                redirectToMainWindow();
                            }
                        });
                    } else {
                        // Login failed
                        showError("❌ Ungültiger Benutzername oder Passwort.");
                        passwordField.clear();
                        passwordField.requestFocus();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    loginButton.setDisable(false);
                    loginButton.setText("Anmelden");
                    showError("❌ Verbindungsfehler. Bitte versuchen Sie es erneut.");
                });
            }
        };

        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
    }

    /**
     * Redirect to main window after successful login
     */
    private void redirectToMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainWindowView.fxml"));
            BorderPane pane = loader.load();
            Scene scene = new Scene(pane);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("NHPlus - Hauptbereich");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (IOException exception) {
            exception.printStackTrace();
            showError("❌ Fehler beim Laden des Hauptfensters.");
        }
    }
}