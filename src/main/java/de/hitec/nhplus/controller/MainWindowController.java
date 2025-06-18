package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.service.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The <code>MainWindowController</code> contains the entire logic of the MainWindow view. It determines which data is
 * displayed and how to react to events.
 */
public class MainWindowController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button adminButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set welcome message with current user
        if (Session.getInstance().isLoggedIn()) {
            welcomeLabel.setText("Willkommen, " + Session.getInstance().getCurrentUserFullName() + "!");
        } else {
            welcomeLabel.setText("Willkommen im NHPlus System!");
        }

        // Show/hide admin button based on user role
        if (Session.getInstance().isAdmin()) {
            adminButton.setVisible(true);
            adminButton.setManaged(true);
        } else {
            adminButton.setVisible(false);
            adminButton.setManaged(false);
        }
    }

    /**
     * This method handles the events fired by the button to show all patients.
     * It collects the data from the Database, and loads the AllPatientView View.
     */
    @FXML
    private void handleShowAllPatient(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllPatientView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles the events fired by the button to show all treatments.
     * It collects the data from the Database, and loads the AllTreatmentView View.
     */
    @FXML
    private void handleShowAllTreatments(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllTreatmentView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method handles the events fired by the button to show all CareGivers.
     * It collects the data from the Database, and loads the AllCaregiverView View.
     */
    @FXML
    private void handleShowAllCareGivers(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * NEU: This method handles the events fired by the admin button.
     * Opens the AdminPanel for user management (only visible for admins).
     */
    @FXML
    private void handleShowAdminPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AdminPanel.fxml"));
            BorderPane pane = loader.load();
            Scene scene = new Scene(pane);

            Stage adminStage = new Stage();
            adminStage.setTitle("NHPlus - Benutzerverwaltung");
            adminStage.setScene(scene);
            adminStage.initModality(Modality.APPLICATION_MODAL);
            adminStage.setResizable(true);
            adminStage.showAndWait();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * NEU: This method handles logout functionality.
     * Logs out the current user and returns to login screen.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        // Clear session
        Session.getInstance().logout();

        // Return to login window
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            Scene scene = new Scene(loader.load());

            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.setTitle("NHPlus Login");
            currentStage.setScene(scene);
            currentStage.setResizable(false);
            currentStage.centerOnScreen();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}