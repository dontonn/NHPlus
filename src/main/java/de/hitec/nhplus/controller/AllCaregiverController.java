package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CareGiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;

import de.hitec.nhplus.model.CareGiver;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;

/**
 * The <code>AllCareGiverController</code> contains the entire logic of the AllCaregiver view. It determines which data is displayed and how to react to events.
 */

public class AllCaregiverController {
    @FXML
    private TableView<CareGiver> tableView;

    @FXML
    private TableColumn<CareGiver, Integer> columnId;

    @FXML
    private TableColumn<CareGiver, String> columnFirstName;

    @FXML
    private TableColumn<CareGiver, String> columnSurname;

    @FXML
    private TableColumn<CareGiver, String> columnTelephoneNumber;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonAdd;

    @FXML
    private TextField textFieldSurname;

    @FXML
    private TextField textFieldFirstName;

    @FXML
    private TextField textFieldTelephoneNumber;

    private final ObservableList<CareGiver> careGivers = FXCollections.observableArrayList();
    private CareGiverDao dao;

    /**
     * When <code>initialize()</code> gets called, all fields are already initialized. For example from the FXMLLoader
     * after loading an FXML-File. At this point of the lifecycle of the Controller, the fields can be accessed and
     * configured.
     */

    public void initialize() {
        this.readAllAndShowInTableView();

        this.columnId.setCellValueFactory(new PropertyValueFactory<>("cid"));

        // CellValueFactory to show property values in TableView
        this.columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // CellFactory to write property values from with in the TableView
        this.columnFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnSurname.setCellFactory(TextFieldTableCell.forTableColumn());

        this.columnTelephoneNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        this.columnTelephoneNumber.setCellFactory(TextFieldTableCell.forTableColumn());
        
        //Anzeigen der Daten
        this.tableView.setItems(this.careGivers);

        this.buttonDelete.setDisable(true);
        this.tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CareGiver>() {
            @Override
            public void changed(ObservableValue<? extends CareGiver> observableValue, CareGiver oldCareGiver, CareGiver newCareGiver) {;
                AllCaregiverController.this.buttonDelete.setDisable(newCareGiver == null);
            }
        });

        this.buttonAdd.setDisable(true);
        ChangeListener<String> inputNewCareGiverListener = (observableValue, oldText, newText) ->
                AllCaregiverController.this.buttonAdd.setDisable(!AllCaregiverController.this.areInputDataValid());
        this.textFieldSurname.textProperty().addListener(inputNewCareGiverListener);
        this.textFieldFirstName.textProperty().addListener(inputNewCareGiverListener);
        this.textFieldTelephoneNumber.textProperty().addListener(inputNewCareGiverListener);
    }
    
    /**
     * Clears all contents from all <code>TextField</code>s.
     */
    private void clearTextfields() {
        this.textFieldFirstName.clear();
        this.textFieldSurname.clear();
        this.textFieldTelephoneNumber.clear();
    }

    /**
     * This method handles the events fired by the button to add a CareGiver. It collects the data from the
     * <code>TextField</code>s, creates an object of class <code>CareGiver</code> of it and passes the object to
     * {@link CareGiverDao} to persist the data.
     */
    @FXML
    public void handleAdd() {
        String surname = this.textFieldSurname.getText();
        String firstName = this.textFieldFirstName.getText();
        String telephoneNumber = this.textFieldTelephoneNumber.getText();
        try {
            this.dao.create(new CareGiver(firstName, surname, telephoneNumber));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        readAllAndShowInTableView();
        clearTextfields();
    }

    /**
     * This method handles events fired by the button to delete CareGivers. It calls {@link CareGiverDao} to delete the
     * CareGiver from the database and removes the object from the list, which is the data source of the
     * <code>TableView</code>.
     */
    @FXML
    public void handleDelete() {
        CareGiver selectedItem = this.tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                DaoFactory.getDaoFactory().createCareGiverDAO().deleteById(selectedItem.getCid());
                this.tableView.getItems().remove(selectedItem);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * When a cell of the column with firstnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditFirstname(TableColumn.CellEditEvent<CareGiver, String> event) {
        event.getRowValue().setFirstName(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * When a cell of the column with surnames was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditSurname(TableColumn.CellEditEvent<CareGiver, String> event) {
        event.getRowValue().setSurname(event.getNewValue());
        this.doUpdate(event);
    }
    
    /**
     * When a cell of the column with telephone number was changed, this method will be called, to persist the change.
     *
     * @param event Event including the changed object and the change.
     */
    @FXML
    public void handleOnEditTelephoneNumber(TableColumn.CellEditEvent<CareGiver, String> event){
        event.getRowValue().setTelephoneNumber(event.getNewValue());
        this.doUpdate(event);
    }

    /**
     * Updates a CareGiver by calling the method <code>update()</code> of {@link CareGiverDao}.
     *
     * @param event Event including the changed object and the change.
     */
    private void doUpdate(TableColumn.CellEditEvent<CareGiver, String> event) {
        try {
            this.dao.update(event.getRowValue());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Reads and puts all CareGivers in the Table View
     */

    private void readAllAndShowInTableView() {
        this.careGivers.clear();
        this.dao = DaoFactory.getDaoFactory().createCareGiverDAO();
        try {
            this.careGivers.addAll(this.dao.readAll());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Checks if the InputData is Valid and Present
     *
     * @return true, if the InputData is valid, else false.
     */
    private boolean areInputDataValid() {
        return !this.textFieldFirstName.getText().isBlank() && !this.textFieldSurname.getText().isBlank() &&
                !this.textFieldTelephoneNumber.getText().isBlank();
    }

}
    