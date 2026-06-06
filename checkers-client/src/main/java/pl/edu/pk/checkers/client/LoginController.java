package pl.edu.pk.checkers.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import pl.edu.pk.checkers.common.message.AuthData;
import pl.edu.pk.checkers.common.message.MessageType;

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
                        // Load GameView.fxml
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
}
