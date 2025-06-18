package de.hitec.nhplus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
/**
 *  The main class to start the program.
 * */
public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loginWindow();
    }

    public void loginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/de/hitec/nhplus/LoginView.fxml"));
            VBox pane = loader.load();
            Scene scene = new Scene(pane);
            this.primaryStage.setTitle("NHPlus Login");
            this.primaryStage.setScene(scene);
            this.primaryStage.setResizable(false);
            this.primaryStage.show();
            this.primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    /**
     * Launches the Program
     * */
    public static void main(String[] args) {
        launch(args);
    }
}