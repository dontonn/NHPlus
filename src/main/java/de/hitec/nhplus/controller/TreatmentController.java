package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CareGiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.PatientDao;
import de.hitec.nhplus.datastorage.TreatmentDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import de.hitec.nhplus.model.Patient;
import de.hitec.nhplus.model.Treatment;
import de.hitec.nhplus.utils.DateConverter;
import de.hitec.nhplus.model.CareGiver;

import java.sql.SQLException;
import java.time.LocalDate;



public class TreatmentController {

    @FXML
    private Label labelPatientName;

    @FXML
    private Label labelCareLevel;

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

    @FXML
    private Label labelCaregiverName;

    @FXML
    private Label labelCaregiverPhone;

    private AllTreatmentController controller;
    private Stage stage;
    private Patient patient;
    private Treatment treatment;

    /**
     * Initializes the controller: sets the parent controller, stage, patient, and treatment;
     * loads patient and treatment data from the database and displays it in the UI.
     *
     * @param controller the parent AllTreatmentController
     * @param stage the Stage for this dialog
     * @param treatment the Treatment to display and edit
     * @throws SQLException if an error occurs reading patient or caregiver data
     */
    public void initializeController(AllTreatmentController controller, Stage stage, Treatment treatment) {
        this.stage = stage;
        this.controller= controller;
        PatientDao pDao = DaoFactory.getDaoFactory().createPatientDAO();
        try {
            this.patient = pDao.read((int) treatment.getPid());
            this.treatment = treatment;
            showData();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Populates UI controls with treatment and patient data,
     * including date conversion and caregiver information.
     */
    private void showData(){
        this.labelPatientName.setText(patient.getSurname()+", "+patient.getFirstName());
        this.labelCareLevel.setText(patient.getCareLevel());
        LocalDate date = DateConverter.convertStringToLocalDate(treatment.getDate());
        this.datePicker.setValue(date);
        this.textFieldBegin.setText(this.treatment.getBegin());
        this.textFieldEnd.setText(this.treatment.getEnd());
        this.textFieldDescription.setText(this.treatment.getDescription());
        this.textAreaRemarks.setText(this.treatment.getRemarks());

        // Load and display caregiver information
        CareGiverDao cDao = DaoFactory.getDaoFactory().createCareGiverDAO();
        CareGiver cg = null;
        try {
            cg = cDao.read((int) treatment.getCid());
        } catch (SQLException e) {
            cg = null;
        }

        if (cg != null) {
            // Combined name display: "Firstname Lastname"
            this.labelCaregiverName.setText(cg.getFirstName() + " " + cg.getSurname());
            this.labelCaregiverPhone.setText(cg.getTelephoneNumber());
        } else {
            this.labelCaregiverName.setText("-");
            this.labelCaregiverPhone.setText("-");
        }
    }

    /**
     * Reads input values from UI fields into the Treatment object,
     * updates the database, refreshes the parent table view, and closes the window.
     */
    @FXML
    public void handleChange(){
        this.treatment.setDate(this.datePicker.getValue().toString());
        this.treatment.setBegin(textFieldBegin.getText());
        this.treatment.setEnd(textFieldEnd.getText());
        this.treatment.setDescription(textFieldDescription.getText());
        this.treatment.setRemarks(textAreaRemarks.getText());
        doUpdate();
        controller.readAllAndShowInTableView();
        stage.close();
    }

    /**
     * Persists the current Treatment object updates to the database using TreatmentDao.
     */
    private void doUpdate(){
        TreatmentDao dao = DaoFactory.getDaoFactory().createTreatmentDao();
        try {
            dao.update(treatment);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Closes the dialog without saving any changes to the Treatment.
     */
    @FXML
    public void handleCancel(){
        stage.close();
    }
}