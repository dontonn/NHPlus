package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.model.LoginUser;
import de.hitec.nhplus.service.LoginUserService;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * LoginController with redirect to MainDashboard
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final LoginUserService loginUserService = new LoginUserService();

    /**
     * Enhanced login method that redirects to MainDashboard after successful login
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple input validation
        if (username == null || username.trim().isEmpty()) {
            showErrorPopup("Bitte geben Sie einen Benutzernamen ein.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showErrorPopup("Bitte geben Sie ein Passwort ein.");
            return;
        }

        // A_2: Password validation (number + letter + special character)
        String passwordError = PasswordUtil.getPasswordValidationError(password);
        if (passwordError != null) {
            clearPasswordAndShowError(passwordError);
            return;
        }

        LoginUser loginUser = null;

        try {
            loginUser = loginUserService.authenticate(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            clearPasswordAndShowError("Anmeldung fehlgeschlagen. Bitte versuchen Sie es erneut.");
            return;
        }

        if (loginUser != null) {
            // A_3: Success popup on successful login
            showSuccessPopup();

            // Set the logged in caregiver in the session
            Session.getInstance().setLoggedInCaregiver(loginUser);

            // NEW: Redirect to MainDashboard (our new main window)
            redirectToMainDashboard();

        } else {
            // A_5: Error message for wrong credentials
            clearPasswordAndShowError("Anmeldung fehlgeschlagen. Benutzername oder Passwort ist falsch.");
        }
    }

    /**
     * NEW: Redirects to MainDashboard by replacing the current scene
     */
    private void redirectToMainDashboard() {
        try {
            // Load the MainDashboard FXML (our new main window)
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/MainDashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Replace scene with MainDashboard
            Scene dashboardScene = new Scene(dashboardRoot);
            stage.setScene(dashboardScene);
            stage.setTitle("NHPlus - Hauptbereich");
            stage.centerOnScreen();

            System.out.println("✅ Redirected to MainDashboard successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            showErrorPopup("Fehler beim Laden des Hauptbereichs.");
        }
    }

    /**
     * A_3: Shows success popup with confirmation message
     */
    private void showSuccessPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anmeldung erfolgreich");
        alert.setHeaderText("Willkommen!");
        alert.setContentText("Sie haben sich erfolgreich angemeldet!");
        alert.showAndWait();
    }

    /**
     * A_4: Clears password field and shows error popup
     */
    private void clearPasswordAndShowError(String message) {
        passwordField.clear(); // A_4: Automatically clear password field
        showErrorPopup(message); // A_5: Show error popup instead of label
    }

    /**
     * Shows error popup instead of using errorLabel
     */
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Fehler");
        alert.setHeaderText("Anmeldung fehlgeschlagen");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method handles the event when the Enter key is pressed.
     */
    public void handleEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            handleLogin(new ActionEvent());
        }
    }
}