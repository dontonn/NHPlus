package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.CaregiverDao;
import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.model.Caregiver;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.AuditLog;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * AdminPanelController - Manages user accounts
 * Features:
 * - Create new users with validation
 * - View existing users in table
 * - Username uniqueness check
 * - Password validation
 * - Admin rights management
 */
public class AdminPanelController implements Initializable {

    // Form Controls
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthdayPicker;
    @FXML private CheckBox adminCheckBox;
    @FXML private Button createButton;
    @FXML private Label statusLabel;

    // Table Controls
    @FXML private TableView<Caregiver> usersTable;
    @FXML private TableColumn<Caregiver, Long> idColumn;
    @FXML private TableColumn<Caregiver, String> usernameColumn;
    @FXML private TableColumn<Caregiver, String> firstNameColumn;
    @FXML private TableColumn<Caregiver, String> lastNameColumn;
    @FXML private TableColumn<Caregiver, String> phoneColumn;
    @FXML private TableColumn<Caregiver, String> birthdayColumn;
    @FXML private TableColumn<Caregiver, Boolean> adminColumn;
    @FXML private TableColumn<Caregiver, Void> actionsColumn;

    // Other Controls
    @FXML private Label userCountLabel;
    @FXML private Button refreshButton;

    private final ObservableList<Caregiver> usersList = FXCollections.observableArrayList();
    private CaregiverDao caregiverDao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupValidation();
        loadUsers();
    }

    /**
     * Sets up the users table with columns and data
     */
    private void setupTable() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        // Admin column with custom cell factory
        adminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
        adminColumn.setCellFactory(column -> new TableCell<Caregiver, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item ? "✅ Ja" : "❌ Nein");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });

        // Actions column with edit and delete buttons
        actionsColumn.setCellFactory(column -> new TableCell<Caregiver, Void>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox actionBox = new HBox(5);

            {
                editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 30;");
                editButton.setOnAction(event -> {
                    Caregiver caregiver = getTableView().getItems().get(getIndex());
                    handleEditUser(caregiver);
                });

                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 30;");
                deleteButton.setOnAction(event -> {
                    Caregiver caregiver = getTableView().getItems().get(getIndex());
                    handleDeleteUser(caregiver);
                });

                actionBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        // Set table data
        usersTable.setItems(usersList);
    }

    /**
     * Sets up form validation
     */
    private void setupValidation() {
        // Real-time username validation
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                checkUsernameAvailability(newValue.trim());
            }
        });
    }

    /**
     * Checks if username is available in real-time
     */
    private void checkUsernameAvailability(String username) {
        try {
            caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
            boolean exists = caregiverDao.usernameExists(username);

            if (exists) {
                usernameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                showErrorStatus("❌ Benutzername bereits vergeben!");
                createButton.setDisable(true);
            } else {
                usernameField.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                hideStatus();
                createButton.setDisable(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles user creation
     */
    @FXML
    private void handleCreateUser(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            // Create new caregiver
            Caregiver newCaregiver = new Caregiver(
                    usernameField.getText().trim(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    birthdayPicker.getValue(),
                    phoneField.getText().trim(),
                    passwordField.getText(),
                    adminCheckBox.isSelected(),
                    false, // not locked
                    null   // no locked date
            );

            // Save to database
            caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
            caregiverDao.create(newCaregiver);

            // Log the action
            AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(),
                    "User created: " + newCaregiver.getUsername() + " (Admin: " + newCaregiver.isAdmin() + ")");

            showSuccessStatus("✅ Benutzer erfolgreich erstellt!");
            clearForm();
            loadUsers();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorStatus("❌ Fehler beim Erstellen des Benutzers: " + e.getMessage());
        }
    }

    /**
     * Validates the form input
     */
    private boolean validateForm() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String phone = phoneField.getText();
        LocalDate birthday = birthdayPicker.getValue();

        // Check required fields
        if (username == null || username.trim().isEmpty()) {
            showErrorStatus("❌ Benutzername ist erforderlich!");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            showErrorStatus("❌ Passwort ist erforderlich!");
            return false;
        }

        // Validate password requirements
        String passwordError = PasswordUtil.getPasswordValidationError(password);
        if (passwordError != null) {
            showErrorStatus("❌ " + passwordError);
            return false;
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            showErrorStatus("❌ Vorname ist erforderlich!");
            return false;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            showErrorStatus("❌ Nachname ist erforderlich!");
            return false;
        }

        if (phone == null || phone.trim().isEmpty()) {
            showErrorStatus("❌ Telefonnummer ist erforderlich!");
            return false;
        }

        // Validate phone format
        if (!phone.matches("^[0-9\\-\\s]{4,15}$")) {
            showErrorStatus("❌ Ungültige Telefonnummer! (4-15 Zeichen, Zahlen, -, Leerzeichen)");
            return false;
        }

        if (birthday == null) {
            showErrorStatus("❌ Geburtstag ist erforderlich!");
            return false;
        }

        // Check username uniqueness again
        try {
            caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
            if (caregiverDao.usernameExists(username.trim())) {
                showErrorStatus("❌ Benutzername bereits vergeben!");
                return false;
            }
        } catch (SQLException e) {
            showErrorStatus("❌ Fehler bei der Validierung: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Clears the form after successful creation
     */
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        birthdayPicker.setValue(null);
        adminCheckBox.setSelected(false);
        usernameField.setStyle(""); // Reset border color
        hideStatus();
    }

    /**
     * Loads all users from database
     */
    @FXML
    private void handleRefreshUsers(ActionEvent event) {
        loadUsers();
    }

    /**
     * Loads users from database and updates table
     */
    private void loadUsers() {
        try {
            caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
            ArrayList<Caregiver> caregivers = (ArrayList<Caregiver>) caregiverDao.readAll();

            usersList.clear();
            usersList.addAll(caregivers);

            userCountLabel.setText("Anzahl Benutzer: " + caregivers.size());

            System.out.println("✅ Loaded " + caregivers.size() + " users");

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorStatus("❌ Fehler beim Laden der Benutzer: " + e.getMessage());
        }
    }

    /**
     * Handles user editing
     */
    private void handleEditUser(Caregiver caregiver) {
        // Create edit dialog
        Dialog<Caregiver> dialog = new Dialog<>();
        dialog.setTitle("Benutzer bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie die Benutzerdaten von: " + caregiver.getUsername());

        // Set the button types
        ButtonType saveButtonType = new ButtonType("💾 Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create edit form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField editFirstNameField = new TextField(caregiver.getFirstName());
        TextField editLastNameField = new TextField(caregiver.getSurname());
        TextField editPhoneField = new TextField(caregiver.getTelephoneNumber());
        PasswordField editPasswordField = new PasswordField();
        CheckBox editAdminCheckBox = new CheckBox();
        editAdminCheckBox.setSelected(caregiver.isAdmin());

        grid.add(new Label("Vorname:"), 0, 0);
        grid.add(editFirstNameField, 1, 0);
        grid.add(new Label("Nachname:"), 0, 1);
        grid.add(editLastNameField, 1, 1);
        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(editPhoneField, 1, 2);
        grid.add(new Label("Neues Passwort:"), 0, 3);
        grid.add(editPasswordField, 1, 3);
        grid.add(new Label("(Leer lassen = nicht ändern)"), 1, 4);
        grid.add(new Label("Administrator:"), 0, 5);
        grid.add(editAdminCheckBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a caregiver when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate input
                    if (editFirstNameField.getText().trim().isEmpty() ||
                            editLastNameField.getText().trim().isEmpty() ||
                            editPhoneField.getText().trim().isEmpty()) {
                        showErrorStatus("❌ Alle Felder müssen ausgefüllt sein!");
                        return null;
                    }

                    // Validate phone format
                    if (!editPhoneField.getText().matches("^[0-9\\-\\s]{4,15}$")) {
                        showErrorStatus("❌ Ungültige Telefonnummer!");
                        return null;
                    }

                    // Update caregiver data
                    caregiver.setFirstName(editFirstNameField.getText().trim());
                    caregiver.setSurname(editLastNameField.getText().trim());
                    caregiver.setTelephoneNumber(editPhoneField.getText().trim());
                    caregiver.setIsAdmin(editAdminCheckBox.isSelected());

                    // Update password if provided
                    String newPassword = editPasswordField.getText();
                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        String passwordError = PasswordUtil.getPasswordValidationError(newPassword);
                        if (passwordError != null) {
                            showErrorStatus("❌ " + passwordError);
                            return null;
                        }
                        caregiver.setPassword_hash(PasswordUtil.generatePassword(newPassword));
                    }

                    // Save to database
                    caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
                    caregiverDao.update(caregiver);

                    // Log the action
                    AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(),
                            "User edited: " + caregiver.getUsername());

                    showSuccessStatus("✅ Benutzer erfolgreich aktualisiert!");
                    loadUsers();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorStatus("❌ Fehler beim Aktualisieren: " + e.getMessage());
                }
                return caregiver;
            }
            return null;
        });

        dialog.showAndWait();
    }
    private void handleDeleteUser(Caregiver caregiver) {
        // Prevent deletion of current user
        if (caregiver.getPid() == Session.getInstance().getLoggedInCaregiver().getPid()) {
            showErrorStatus("❌ Sie können sich selbst nicht löschen!");
            return;
        }

        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Benutzer löschen");
        alert.setHeaderText("Möchten Sie diesen Benutzer wirklich löschen?");
        alert.setContentText("Benutzer: " + caregiver.getUsername() + " (" + caregiver.getFirstName() + " " + caregiver.getSurname() + ")");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                caregiverDao = DaoFactory.getDaoFactory().createCaregiverDAO();
                caregiverDao.deleteById(caregiver.getPid());

                AuditLog.writeLog(Session.getInstance().getLoggedInCaregiver(),
                        "User deleted: " + caregiver.getUsername());

                showSuccessStatus("✅ Benutzer gelöscht!");
                loadUsers();

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorStatus("❌ Fehler beim Löschen: " + e.getMessage());
            }
        }
    }

    /**
     * Shows success status message
     */
    private void showSuccessStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        statusLabel.setVisible(true);
    }

    /**
     * Shows error status message
     */
    private void showErrorStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        statusLabel.setVisible(true);
    }

    /**
     * Hides status message
     */
    private void hideStatus() {
        statusLabel.setVisible(false);
    }

    /**
     * Closes the admin panel
     */
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }
}