package pl.edu.pk.checkers.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain extends Application {
    private SocketService socketService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        socketService = new SocketService();
        socketService.startSocketService();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/edu/pk/checkers/client/LoginView.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setSocketService(socketService);

            Scene scene = new Scene(root);
            primaryStage.setTitle("JavaFX-Socket-Checkers Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String args[]) {
        launch(args);
    }
}
