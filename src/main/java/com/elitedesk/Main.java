package com.elitedesk;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10); // 10 is the spacing between elements
        root.setStyle("-fx-padding: 10;");

        Label welcomeLabel = new Label("Welcome to EliteDesk");
        welcomeLabel.setStyle("-fx-font-size: 24px;");

        Button button = new Button("Click Me");
        button.setOnAction(e -> {
            System.out.println("Button clicked!");
        });

        root.getChildren().addAll(welcomeLabel, button);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("EliteDesk");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}