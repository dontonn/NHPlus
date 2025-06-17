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
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

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

    /**
     * Column showing the first name of the assigned caregiver.
     */
    @FXML
    private TableColumn<Treatment, String> columnCaregiverFirstName;

    @FXML
    private ComboBox<String> comboBoxPatientSelection;

    @FXML
    private Button buttonDelete;

    private TreatmentDao dao;
    private final ObservableList<String> patientSelection = FXCollections.observableArrayList();
    private final ObservableList<Treatment> treatments = FXCollections.observableArrayList();
    private ArrayList<Patient> patientList = new ArrayList<>();
    private ArrayList<CareGiver> careGiverList = new ArrayList<>();

    // Flag to prevent duplicate loading during ComboBox updates
    private boolean isUpdatingComboBox = false;


    /**
     * Initializes the controller: loads all treatments and caregivers,
     * configures table columns, combo box data, and selection listeners.
     */
    public void initialize() {
        readAllAndShowInTableView();
        loadCareGivers();
        comboBoxPatientSelection.setItems(patientSelection);
        comboBoxPatientSelection.getSelectionModel().select(0);

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("tid"));
        this.columnPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.columnBegin.setCellValueFactory(new PropertyValueFactory<>("begin"));
        this.columnEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set cell value for caregiver first name - FIXED
        this.columnCaregiverFirstName.setCellValueFactory(cellData -> {
            CareGiver careGiver = findCareGiverById(cellData.getValue().getCid());
            return new SimpleStringProperty(
                    careGiver != null ? careGiver.getFirstName() : ""
            );
        });

        this.tableView.setItems(this.treatments);

        // Disabling the button to delete treatments as long, as no treatment was selected.
        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldTreatment, newTreatment) ->
                        AllTreatmentController.this.buttonDelete.setDisable(newTreatment == null));

        this.createComboBoxData();
    }

    /**
     * Loads all caregivers from the database to be used for displaying caregiver names in the table.
     */
    private void loadCareGivers() {
        try {
            CareGiverDao dao = DaoFactory.getDaoFactory().createCareGiverDAO();
            this.careGiverList = (ArrayList<CareGiver>) dao.readAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
            // Initialize as empty list if loading fails
            this.careGiverList = new ArrayList<>();
        }
    }

    /**
     * Finds a caregiver by their ID from the loaded caregiver list.
     *
     * @param cid The caregiver ID to search for.
     * @return The CareGiver object if found, null otherwise.
     */
    private CareGiver findCareGiverById(long cid) {
        if (careGiverList != null) {
            for (CareGiver cg : careGiverList) {
                if (cg.getCid() == cid) {
                    return cg;
                }
            }
        }
        return null;
    }

    /**
     * Reads all treatments and shows them in the table view.
     * This method prevents duplicate loading by temporarily disabling ComboBox events.
     */
    public void readAllAndShowInTableView() {
        this.treatments.clear();

        // Prevent ComboBox event from triggering during update
        try {
            this.isUpdatingComboBox = true;
            comboBoxPatientSelection.getSelectionModel().select(0);
        } finally {
            this.isUpdatingComboBox = false;
        }

        this.dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            this.treatments.addAll(dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Populates the patient selection combo box with all patients from the database,
     * prefixed by an option for showing all treatments.
     */
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
            // Initialize as empty list if loading fails
            this.patientList = new ArrayList<>();
        }
    }

    /**
     * Formats a patient's display name as "surname, firstName" for the combo box.
     *
     * @param patient the Patient to format
     * @return the formatted display name
     */
    private String formatPatientDisplayName(Patient patient) {
        return String.format("%s, %s", patient.getSurname(), patient.getFirstName());
    }

    /**
     * Handles ComboBox selection changes.
     * Now includes check to prevent duplicate loading during programmatic updates.
     */
    @FXML
    public void handleComboBox() {
        // Skip if we're updating the ComboBox programmatically
        if (isUpdatingComboBox) {
            return;
        }

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

    /**
     * Retrieves a Patient object corresponding to the given display name.
     *
     * @param displayName the formatted patient name from the combo box
     * @return the matching Patient, or null if not found
     */
    private Patient getPatientFromDisplayName(String displayName) {
        if (patientList == null || patientList.isEmpty()) {
            return null;
        }

        for (Patient patient : patientList) {
            if (displayName.equals(formatPatientDisplayName(patient))) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Searches the loaded patient list for a patient by surname.
     *
     * @param surname the surname to search for
     * @return the matching Patient, or null if none matches
     */
    private Patient searchPatientInList(String surname) {
        if (this.patientList == null || this.patientList.isEmpty()) {
            return null;
        }

        for (Patient patient : this.patientList) {
            if (patient.getSurname().equals(surname)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Searches the loaded caregiver list for a caregiver by surname.
     *
     * @param surname the surname to search for
     * @return the matching CareGiver, or null if none matches
     */
    private CareGiver searchCareGiverInList(String surname) {
        if (this.careGiverList == null || this.careGiverList.isEmpty()) {
            return null;
        }

        for (CareGiver caregiver : this.careGiverList) {
            if (caregiver.getSurname().equals(surname)) {
                return caregiver;
            }
        }
        return null;
    }

    /**
     * Deletes the selected treatment both from the table view and the database.
     */
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

    /**
     * Sets up a double-click listener on the table view to open the treatment details window.
     */
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

    /**
     * Opens the New Treatment dialog for the specified patient and optional caregiver.
     *
     * @param patient the Patient for whom to create a treatment
     * @param careGiver the CareGiver to preselect, or null
     */
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

    /**
     * Opens the Treatment details window for the given treatment.
     *
     * @param treatment the Treatment to display
     */
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