package com.elitedesk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        // Add the stylesheet
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

        primaryStage.setTitle("EliteDesk - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}