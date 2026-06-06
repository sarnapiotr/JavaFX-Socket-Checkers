package pl.edu.pk.checkers.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GameController {

    @FXML
    private GridPane boardGrid;

    @FXML
    private Label errorLabel;

    @FXML
    private BorderPane gameLayer;

    @FXML
    private VBox gameOverOverlay;

    @FXML
    private VBox leaderboardContainer;

    @FXML
    private Label myNameLabel;

    @FXML
    private Label opponentNameLabel;

    @FXML
    private Label turnLabel;

    @FXML
    private VBox waitingOverlay;

    @FXML
    private Label winnerLabel;

    @FXML
    void handlePlayAgain(ActionEvent event) {

    }

    @FXML
    void handleQuit(ActionEvent event) {

    }

}
