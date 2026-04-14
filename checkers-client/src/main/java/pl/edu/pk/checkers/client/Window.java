package pl.edu.pk.checkers.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Window extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("Hello world!");

        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Checkers Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
