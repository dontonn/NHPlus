package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.model.LoginUser;
import de.hitec.nhplus.service.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * MainDashboardController - Controls the main dashboard after login
 * Features:
 * - Navigation to Patients, Treatments
 * - Admin panel (only visible for admins)
 * - Logout functionality
 * - Dynamic content loading
 */
public class MainDashboardController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox adminArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label userInfoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserInterface();
    }

    /**
     * Sets up the user interface based on the logged-in user
     */
    private void setupUserInterface() {
        LoginUser loggedInLoginUser = Session.getInstance().getLoggedInCaregiver();

        if (loggedInLoginUser != null) {
            // DEBUG: Print user details
            System.out.println("🔍 DEBUG - Logged in user details:");
            System.out.println("   Username: " + loggedInLoginUser.getUsername());
            System.out.println("   First Name: " + loggedInLoginUser.getFirstName());
            System.out.println("   Last Name: " + loggedInLoginUser.getSurname());
            System.out.println("   Is Admin: " + loggedInLoginUser.isAdmin());
            System.out.println("   PID: " + loggedInLoginUser.getPid());

            // Update welcome message with user's name
            welcomeLabel.setText("Willkommen, " + loggedInLoginUser.getFirstName() + " " + loggedInLoginUser.getSurname());

            // Update user info label
            String userInfo = "Angemeldet als: " + loggedInLoginUser.getUsername();
            if (loggedInLoginUser.isAdmin()) {
                userInfo += " (Administrator)";
                System.out.println("✅ User is ADMIN - showing admin area");
            } else {
                System.out.println("❌ User is NOT admin - hiding admin area");
            }
            userInfoLabel.setText(userInfo);

            // Show admin area only for administrators
            boolean isAdmin = loggedInLoginUser.isAdmin();
            adminArea.setVisible(isAdmin);
            adminArea.setManaged(isAdmin);

            System.out.println("🎯 Admin area visible: " + adminArea.isVisible());
            System.out.println("🎯 Admin area managed: " + adminArea.isManaged());

        } else {
            // Fallback if no user is logged in
            System.out.println("❌ ERROR: No user logged in!");
            welcomeLabel.setText("Willkommen im NHPlus System");
            userInfoLabel.setText("Kein Benutzer angemeldet");
            adminArea.setVisible(false);
            adminArea.setManaged(false);
        }
    }

    /**
     * Navigate to Patients view
     */
    @FXML
    private void handleShowPatients(ActionEvent event) {
        loadContentView("/de/hitec/nhplus/AllPatientView.fxml", "Patienten/innen");
    }

    /**
     * Navigate to Treatments view
     */
    @FXML
    private void handleShowTreatments(ActionEvent event) {
        loadContentView("/de/hitec/nhplus/AllTreatmentView.fxml", "Behandlungen");
    }

    /**
     * Open Admin Panel (only available for administrators)
     */
    @FXML
    private void handleShowAdminPanel(ActionEvent event) {
        System.out.println("🔧 Opening Admin Panel...");
        openAdminPanel();
    }

    /**
     * Logout and return to login screen
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // Clear session
        Session.getInstance().logout();
        System.out.println("🚪 User logged out");

        // Return to login screen
        redirectToLogin();
    }

    /**
     * Loads a content view into the center content area
     */
    private void loadContentView(String fxmlPath, String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent content = loader.load();

            // Replace the content in the center area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);

            System.out.println("✅ Loaded view: " + viewName);

        } catch (IOException e) {
            System.err.println("❌ Error loading view: " + viewName);
            e.printStackTrace();

            // Show error message in content area
            Label errorLabel = new Label("Fehler beim Laden der Ansicht: " + viewName);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

    /**
     * Opens the Admin Panel in a new window
     */
    private void openAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AdminPanel.fxml"));
            Parent adminRoot = loader.load();

            // Create new stage for admin panel
            Stage adminStage = new Stage();
            adminStage.setTitle("NHPlus - Verwaltung");
            adminStage.setScene(new Scene(adminRoot));
            adminStage.setResizable(false);

            // Show admin panel
            adminStage.show();

            System.out.println("✅ Admin Panel opened");

        } catch (IOException e) {
            System.err.println("❌ Error opening Admin Panel");
            e.printStackTrace();
        }
    }

    /**
     * Redirects back to login screen
     */
    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            Parent loginRoot = loader.load();

            // Get current stage
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();

            // Replace scene with login
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.setTitle("NHPlus - Anmeldung");
            stage.centerOnScreen();

            System.out.println("✅ Redirected to Login");

        } catch (IOException e) {
            System.err.println("❌ Error redirecting to login");
            e.printStackTrace();
        }
    }
}