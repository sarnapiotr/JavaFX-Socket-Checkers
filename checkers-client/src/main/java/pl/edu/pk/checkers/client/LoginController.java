package pl.edu.pk.checkers.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pl.edu.pk.checkers.common.message.AuthData;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label serverMessageLabel;

    private SocketService socketService;

    public void setSocketService(SocketService socketService) {
        this.socketService = socketService;

        this.socketService.setHandleServerMessage((serverMessage) -> {
            Platform.runLater(() -> {
                switch (serverMessage.getType()) {
                    case LOGIN_SUCCESS:
                        serverMessageLabel.setTextFill(Color.GREEN);
                        serverMessageLabel.setText("Logged in");
                        loadGameView();
                        break;
                    case REGISTER_SUCCESS:
                        serverMessageLabel.setTextFill(Color.GREEN);
                        serverMessageLabel.setText("Registered");
                        break;
                    case ERROR:
                        serverMessageLabel.setTextFill(Color.RED);
                        serverMessageLabel.setText(serverMessage.getContentAs(String.class));
                        break;
                    default:
                        break;
                }
            });
        });
    }

    @FXML
    void handleSignIn(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            serverMessageLabel.setTextFill(Color.RED);
            serverMessageLabel.setText("Empty username/password field");
            return;
        }

        AuthData authData = new AuthData(username, password);
        socketService.getMessageHandler().sendMessage(MessageType.LOGIN, authData);
    }

    @FXML
    void handleSignUp(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            serverMessageLabel.setTextFill(Color.RED);
            serverMessageLabel.setText("Empty username/password field");
            return;
        }

        AuthData authData = new AuthData(username, password);
        socketService.getMessageHandler().sendMessage(MessageType.REGISTER, authData);
    }

    private void loadGameView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/edu/pk/checkers/client/GameView.fxml"));
            Parent gameRoot = loader.load();

            GameController gameController = loader.getController();
            gameController.initData(socketService, usernameField.getText());

            Stage stage = (Stage) serverMessageLabel.getScene().getWindow();
            stage.getScene().setRoot(gameRoot);
            stage.setTitle("JavaFX-Socket-Checkers Game");
            stage.sizeToScene();
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }
}
