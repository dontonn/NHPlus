package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.CareGiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.CareGiver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Treatment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The <code>AllTreatmentController</code> contains the entire logic of the AllTreatment view. It determines which data is displayed and how to react to events.
 */

public class AllTreatmentController {

    @FXML
    private TableView<Treatment> tableView;

    @FXML
    private TableColumn<Treatment, Integer> columnId;

    @FXML
    private TableColumn<Treatment, Integer> columnPid;

    @FXML
    private TableColumn<Treatment, String> columnDate;

    @FXML
    private TableColumn<Treatment, String> columnBegin;

    @FXML
    private TableColumn<Treatment, String> columnEnd;

    @FXML
    private TableColumn<Treatment, String> columnDescription;

    @FXML
    private ComboBox<String> comboBoxPatientSelection;

    @FXML
    private Button buttonDelete;

    private TreatmentDao dao;
    private final ObservableList<String> patientSelection = FXCollections.observableArrayList();
    private final ObservableList<Treatment> treatments = FXCollections.observableArrayList();
    private ArrayList<Patient> patientList;
    private ArrayList<CareGiver> careGiverList;


    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */

    public void initialize() {
        readAllAndShowInTableView();
        comboBoxPatientSelection.setItems(patientSelection);
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("tid"));
        this.columnPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        this.columnEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        this.tableView.setItems(this.treatments);

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldTreatment, newTreatment) ->
                        AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null));

        this.createComboBoxData();
    }
    /**
    *   Reads and Shows all Treatments in the Table
     **/
    public void readAllAndShowInTableView() {
        this.treatments.clear();
        comboBoxPatientSelection.getSelectionModel().select(0);
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            this.treatments.addAll(dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void createComboBoxData() {
        patientSelection.clear();
        patientSelection.add("alle");

        PatientDao dao = DaoFactory.getDaoFactory().createPatientDAO();
        try {
            patientList = (ArrayList<Patient>) dao.readAll();
            for (Patient patient: patientList) {
                this.patientSelection.add(formatPatientDisplayName(patient));
            }
            comboBoxPatientSelection.setItems(patientSelection);
            comboBoxPatientSelection.getSelectionModel().selectFirst(); // "alle" wird vorausgewählt
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private String formatPatientDisplayName(Patient patient) {
        return String.format("%s, %s", patient.getSurname(), patient.getFirstName());
    }

    @FXML
    public void handleComboBox() {
        String selectedPatient = this.comboBoxPatientSelection.getSelectionModel().getSelectedItem();
        this.treatments.clear();
        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();

        if (selectedPatient == null || selectedPatient.equals("alle")) {
            try {
                this.treatments.addAll(this.dao.readAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        else {
            Patient patient = getPatientFromDisplayName(selectedPatient);
            if (patient != null) {
                try {
                    this.treatments.addAll(this.dao.readTreatmentsByPid(patient.getPid()));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private Patient getPatientFromDisplayName(String displayName) {
        for (Patient patient : patientList) {
            if (displayName.equals(formatPatientDisplayName(patient))) {
                return patient;
            }
        }
        return null;
    }

    private Patient searchPatientInList(String surname) {
        for (Patient patient : this.patientList) {
            if (patient.getSurname().equals(surname)) {
                return patient;
            }
        }
        return null;
    }

    private CareGiver searchCareGiverInList(String surname) {
        for (CareGiver caregiver : this.careGiverList) {
            if (caregiver.getSurname().equals(surname)) {
                return caregiver;
            }
        }
        return null;
    }

    @FXML
    public void handleDelete() {
        int index = this.tableView.getSelectionModel().getSelectedIndex();
        Treatment t = this.treatments.remove(index);
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.deleteById(t.getTid());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Handles the action to create a new Treatment.
     * Validates that a patient is selected before opening the new Treatment dialog.
     * Shows an alert in German if no patient is selected.
     */
    @FXML
    public void handleNewTreatment() {
        String selectedDisplayName = comboBoxPatientSelection.getValue();
        Patient patient = getPatientFromDisplayName(selectedDisplayName);
        if (patient == null) {
            showNoPatientAlert();
            return;
        }
        // Caregiver assignment will be managed in the new treatment dialog
        newTreatmentWindow(patient, null);
    }

    /**
     * Shows an information alert indicating that no patient has been selected.
     */
    private void showNoPatientAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Patient für die Behandlung fehlt!");
        alert.setContentText("Wählen Sie über die Combobox einen Patienten aus!");
        alert.showAndWait();
    }

    @FXML
    public void handleMouseClick() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (tableView.getSelectionModel().getSelectedItem() != null)) {
                int index = this.tableView.getSelectionModel().getSelectedIndex();
                Treatment treatment = this.treatments.get(index);
                treatmentWindow(treatment);
            }
        });
    }

    public void newTreatmentWindow(Patient patient, CareGiver careGiver) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/NewTreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();

            NewTreatmentController controller = loader.getController();
            controller.initialize(this, stage, patient, careGiver);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void treatmentWindow(Treatment treatment){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/TreatmentView.fxml"));
            AnchorPane pane = loader.load();
            Scene scene = new Scene(pane);

            // the primary stage should stay in the background
            Stage stage = new Stage();
            TreatmentController controller = loader.getController();
            controller.initializeController(this, stage, treatment);

            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
