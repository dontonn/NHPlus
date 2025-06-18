package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * The <code>MainWindowController</code> contains the entire logic of the MainWindow view. It determines which data is
 * displayed and how to react to events.
 */
public class MainWindowController {

    @FXML
    private BorderPane mainBorderPane;

    /**
    * This method handles the events fired by the button to show all patients.
     * It collects the data from the Database, and loads the AllPatientView View.
     * */
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
     * */
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
     * */
    @FXML
    private void handleShowAllCareGivers(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/AllCaregiverView.fxml"));
        try {
            mainBorderPane.setCenter(loader.load());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
