package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.TreatmentDao;
import de.hitec.nhplus.model.CareGiver;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.CareGiver;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.hitec.nhplus.datastorage.CareGiverDao;

/**
 * Controller class for creating and editing treatments.
 */
public class NewTreatmentController {

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelSurname;

    @FXML
    private TextField textFieldBegin;

    @FXML
    private TextField textFieldEnd;

    @FXML
    private TextField textFieldDescription;

    @FXML
    private TextArea textAreaRemarks;

    @FXML
    private DatePicker datePicker;

    /**
     * ComboBox for selecting a caregiver for the treatment.
     */
    @FXML
    private ComboBox<CareGiver> comboBoxCaregiver;

    @FXML
    private Button buttonAdd;

    private AllTreatmentController controller;
    private Patient patient;
    private CareGiver careGiver;
    private Stage stage;

    /**
     * Initializes the controller: sets references to parent controller, stage, patient, and caregiver;
     * configures input validation listeners, date picker converter, and loads patient data and combo box.
     */
    public void initialize(AllTreatmentController controller, Stage stage, Patient patient, CareGiver careGiver) {
        this.controller= controller;
        this.patient = patient;
        this.careGiver = careGiver;
        this.stage = stage;

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewPatientListener = (observableValue, oldText, newText) ->
                NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid());
        this.textFieldBegin.textProperty().addListener(inputNewPatientListener);
        this.textFieldEnd.textProperty().addListener(inputNewPatientListener);
        this.textFieldDescription.textProperty().addListener(inputNewPatientListener);
        this.textAreaRemarks.textProperty().addListener(inputNewPatientListener);
        this.datePicker.valueProperty().addListener((observableValue, localDate, t1) -> NewTreatmentController.this.buttonAdd.setDisable(NewTreatmentController.this.areInputDataInvalid()));
        this.datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                return (localDate == null) ? "" : DateConverter.convertLocalDateToString(localDate);
            }

            @Override
            public LocalDate fromString(String localDate) {
                return DateConverter.convertStringToLocalDate(localDate);
            }
        });
        this.showPatientData();
        this.createComboboxData();
    }

    /**
     * Displays the patient's first name and surname in the corresponding labels.
     */
    private void showPatientData(){
        this.labelFirstName.setText(patient.getFirstName());
        this.labelSurname.setText(patient.getSurname());
    }

    /**
     * Creates a new Treatment from input fields, saves it to the database,
     * refreshes the parent table view, and closes the window.
     */
    @FXML
    public void handleAdd(){
        LocalDate date = this.datePicker.getValue();
        LocalTime begin = DateConverter.convertStringToLocalTime(textFieldBegin.getText());
        LocalTime end = DateConverter.convertStringToLocalTime(textFieldEnd.getText());
        String description = textFieldDescription.getText();
        String remarks = textAreaRemarks.getText();
        Treatment treatment = new Treatment(patient.getPid(), careGiver.getCid(), date, begin, end, description, remarks);
        createTreatment(treatment);
        controller.readAllAndShowInTableView();
        stage.close();
    }

    /**
     * Persists the given Treatment object to the database using TreatmentDao.
     *
     * @param treatment the Treatment to save
     */
    private void createTreatment(Treatment treatment) {
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.create(treatment);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Cancels the treatment creation and closes the dialog without saving.
     */
    @FXML
    public void handleCancel(){
        stage.close();
    }

    /**
     * Validates input fields: checks non-null times, proper time order, non-blank description, and date selection.
     *
     * @return true if any input is invalid, false otherwise
     */
    private boolean areInputDataInvalid() {
        if (this.textFieldBegin.getText() == null || this.textFieldEnd.getText() == null) {
            return true;
        }
        try {
            LocalTime begin = DateConverter.convertStringToLocalTime(this.textFieldBegin.getText());
            LocalTime end = DateConverter.convertStringToLocalTime(this.textFieldEnd.getText());
            if (!end.isAfter(begin)) {
                return true;
            }
        } catch (Exception exception) {
            return true;
        }
        return this.textFieldDescription.getText().isBlank() || this.datePicker.getValue() == null;
    }

    /**
     * Populates the ComboBox with all available caregivers and sets the selected caregiver.
     */
    private void createComboboxData() {
        try {
            CareGiverDao dao = DaoFactory.getDaoFactory().createCareGiverDAO();
            ObservableList<CareGiver> caregivers = FXCollections.observableArrayList(dao.readAll());
            comboBoxCaregiver.setItems(caregivers);
            comboBoxCaregiver.setConverter(new StringConverter<CareGiver>() {
                @Override
                public String toString(CareGiver cg) {
                    return (cg == null) ? "" : cg.getFirstName() + " " + cg.getSurname();
                }
                @Override
                public CareGiver fromString(String string) {
                    return null;
                }
            });
            comboBoxCaregiver.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, selected) -> this.careGiver = selected);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}