package de.hitec.nhplus.controller;

import de.hitec.nhplus.datastorage.DaoFactory;
import de.hitec.nhplus.datastorage.UserDao;
import de.hitec.nhplus.model.User;
import de.hitec.nhplus.service.Session;
import de.hitec.nhplus.utils.PasswordUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for AdminPanel - User Management
 */
public class AdminPanelController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox adminCheckBox;
    @FXML private Button createButton;
    @FXML private Button refreshButton;
    @FXML private Button closeButton;
    @FXML private Label statusLabel;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, String> lastLoginColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDao userDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userDao = DaoFactory.getDaoFactory().createUserDAO();
        setupTable();
        setupValidation();
        loadUsers();
    }

    /**
     * Setup table columns and data
     */
    private void setupTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Custom role column
        roleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

        // Custom date formatting
        createdAtColumn.setCellValueFactory(cellData -> {
            String createdAt = cellData.getValue().getCreatedAt();
            if (createdAt != null && !createdAt.isEmpty()) {
                try {
                    // Format: 2025-06-18 14:30:00 -> 18.06.2025
                    String formatted = createdAt.substring(0, 10);
                    String[] parts = formatted.split("-");
                    return new javafx.beans.property.SimpleStringProperty(parts[2] + "." + parts[1] + "." + parts[0]);
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty(createdAt);
                }
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        lastLoginColumn.setCellValueFactory(cellData -> {
            String lastLogin = cellData.getValue().getLastLogin();
            if (lastLogin != null && !lastLogin.isEmpty()) {
                try {
                    // Format: 2025-06-18 14:30:00 -> 18.06.2025 14:30
                    String[] parts = lastLogin.split(" ");
                    String[] dateParts = parts[0].split("-");
                    String formattedDate = dateParts[2] + "." + dateParts[1] + "." + dateParts[0];
                    String formattedTime = parts[1].substring(0, 5); // HH:mm
                    return new javafx.beans.property.SimpleStringProperty(formattedDate + " " + formattedTime);
                } catch (Exception e) {
                    return new javafx.beans.property.SimpleStringProperty(lastLogin);
                }
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        // Actions column with edit and delete buttons
        actionsColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button editButton = new Button("✏️");
                    private final Button deleteButton = new Button("🗑️");
                    private final HBox actionBox = new HBox(5, editButton, deleteButton);

                    {
                        editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 3;");
                        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 3;");

                        editButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditUser(user);
                        });

                        deleteButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());
                            // Prevent admin from deleting themselves
                            if (Session.getInstance().getCurrentUsername().equals(user.getUsername())) {
                                deleteButton.setDisable(true);
                                deleteButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 3;");
                            }
                            setGraphic(actionBox);
                        }
                    }
                };
            }
        });

        userTable.setItems(userList);
    }

    /**
     * Setup input validation
     */
    private void setupValidation() {
        // Real-time username validation
        usernameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.trim().isEmpty()) {
                checkUsernameAvailability(newText.trim());
            }
        });

        // Real-time password validation
        passwordField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                String validation = PasswordUtil.checkPasswordRequirements(newText);
                if ("OK".equals(validation)) {
                    showStatus("✅ Passwort erfüllt alle Anforderungen", "#28a745");
                } else {
                    showStatus("⚠️ " + validation, "#ffc107");
                }
            }
        });

        // Enable/disable create button
        createButton.setDisable(true);
        usernameField.textProperty().addListener((obs, oldText, newText) -> updateCreateButtonState());
        firstNameField.textProperty().addListener((obs, oldText, newText) -> updateCreateButtonState());
        lastNameField.textProperty().addListener((obs, oldText, newText) -> updateCreateButtonState());
        passwordField.textProperty().addListener((obs, oldText, newText) -> updateCreateButtonState());
    }

    /**
     * Check if username is available
     */
    private void checkUsernameAvailability(String username) {
        Task<Boolean> checkTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userDao.usernameExists(username);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        showStatus("❌ Benutzername bereits vergeben", "#dc3545");
                    } else {
                        showStatus("✅ Benutzername verfügbar", "#28a745");
                    }
                    updateCreateButtonState();
                });
            }
        };

        Thread checkThread = new Thread(checkTask);
        checkThread.setDaemon(true);
        checkThread.start();
    }

    /**
     * Update create button state based on form validation
     */
    private void updateCreateButtonState() {
        boolean isValid = !usernameField.getText().trim().isEmpty() &&
                !firstNameField.getText().trim().isEmpty() &&
                !lastNameField.getText().trim().isEmpty() &&
                !passwordField.getText().isEmpty() &&
                PasswordUtil.isValidPassword(passwordField.getText());

        createButton.setDisable(!isValid);
    }

    /**
     * Load all users from database
     */
    private void loadUsers() {
        Task<ObservableList<User>> loadTask = new Task<ObservableList<User>>() {
            @Override
            protected ObservableList<User> call() throws Exception {
                return FXCollections.observableArrayList(userDao.readAll());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    userList.clear();
                    userList.addAll(getValue());
                    showStatus("✅ " + userList.size() + " Benutzer geladen", "#28a745");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("❌ Fehler beim Laden der Benutzer", "#dc3545");
                });
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Handle create user button
     */
    @FXML
    private void handleCreateUser(ActionEvent event) {
        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();

        // Final validation
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            showStatus("❌ Alle Felder müssen ausgefüllt werden", "#dc3545");
            return;
        }

        if (!PasswordUtil.isValidPassword(password)) {
            showStatus("❌ Passwort erfüllt nicht die Anforderungen", "#dc3545");
            return;
        }

        createButton.setDisable(true);
        createButton.setText("Erstelle...");

        Task<Boolean> createTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Check username availability again
                if (userDao.usernameExists(username)) {
                    return false;
                }

                // Create user
                String passwordHash = PasswordUtil.hashPassword(password);
                User newUser = new User(username, passwordHash, firstName, lastName, isAdmin, true);
                userDao.create(newUser);
                return true;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    createButton.setText("Benutzer erstellen");

                    if (getValue()) {
                        showStatus("✅ Benutzer '" + username + "' erfolgreich erstellt", "#28a745");
                        clearForm();
                        loadUsers();
                    } else {
                        showStatus("❌ Benutzername bereits vergeben", "#dc3545");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    createButton.setDisable(false);
                    createButton.setText("Benutzer erstellen");
                    showStatus("❌ Fehler beim Erstellen des Benutzers", "#dc3545");
                });
            }
        };

        Thread createThread = new Thread(createTask);
        createThread.setDaemon(true);
        createThread.start();
    }

    /**
     * Handle edit user
     */
    private void handleEditUser(User user) {
        // Create edit dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Benutzer bearbeiten");
        dialog.setHeaderText("Benutzer: " + user.getUsername());

        // Create edit form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField editFirstNameField = new TextField(user.getFirstName());
        TextField editLastNameField = new TextField(user.getLastName());
        PasswordField editPasswordField = new PasswordField();
        CheckBox editAdminCheckBox = new CheckBox();
        editAdminCheckBox.setSelected(user.isAdmin());

        grid.add(new Label("Vorname:"), 0, 0);
        grid.add(editFirstNameField, 1, 0);
        grid.add(new Label("Nachname:"), 0, 1);
        grid.add(editLastNameField, 1, 1);
        grid.add(new Label("Neues Passwort:"), 0, 2);
        grid.add(editPasswordField, 1, 2);
        grid.add(new Label("(Leer lassen = nicht ändern)"), 1, 3);
        grid.add(new Label("Administrator:"), 0, 4);
        grid.add(editAdminCheckBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                user.setFirstName(editFirstNameField.getText().trim());
                user.setLastName(editLastNameField.getText().trim());
                user.setAdmin(editAdminCheckBox.isSelected());

                // Update password if provided
                String newPassword = editPasswordField.getText();
                if (!newPassword.isEmpty()) {
                    if (PasswordUtil.isValidPassword(newPassword)) {
                        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                    } else {
                        showStatus("❌ Neues Passwort erfüllt nicht die Anforderungen", "#dc3545");
                        return null;
                    }
                }

                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(editedUser -> {
            try {
                userDao.update(editedUser);
                showStatus("✅ Benutzer '" + editedUser.getUsername() + "' aktualisiert", "#28a745");
                loadUsers();
            } catch (SQLException e) {
                showStatus("❌ Fehler beim Aktualisieren des Benutzers", "#dc3545");
                e.printStackTrace();
            }
        });
    }

    /**
     * Handle delete user
     */
    private void handleDeleteUser(User user) {
        // Prevent admin from deleting themselves
        if (Session.getInstance().getCurrentUsername().equals(user.getUsername())) {
            showStatus("❌ Sie können sich nicht selbst löschen", "#dc3545");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Benutzer löschen");
        confirmDialog.setHeaderText("Benutzer löschen: " + user.getUsername());
        confirmDialog.setContentText("Sind Sie sicher, dass Sie den Benutzer '" + user.getFullName() + "' löschen möchten?\n\nDiese Aktion kann nicht rückgängig gemacht werden.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDao.deleteById(user.getUid());
                showStatus("✅ Benutzer '" + user.getUsername() + "' gelöscht", "#28a745");
                loadUsers();
            } catch (SQLException e) {
                showStatus("❌ Fehler beim Löschen des Benutzers", "#dc3545");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
    }

    /**
     * Handle close button
     */
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Clear form fields
     */
    private void clearForm() {
        usernameField.clear();
        firstNameField.clear();
        lastNameField.clear();
        passwordField.clear();
        adminCheckBox.setSelected(false);
        statusLabel.setText("");
    }

    /**
     * Show status message
     */
    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
}