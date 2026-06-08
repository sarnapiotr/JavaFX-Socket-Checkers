package pl.edu.pk.checkers.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import pl.edu.pk.checkers.common.board.CheckerMove;
import pl.edu.pk.checkers.common.board.CheckerType;
import pl.edu.pk.checkers.common.board.Position;
import pl.edu.pk.checkers.common.message.ClientStats;
import pl.edu.pk.checkers.common.message.GameOverData;
import pl.edu.pk.checkers.common.message.GameStartData;
import pl.edu.pk.checkers.common.message.MessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameController {

    @FXML private GridPane boardGrid;
    @FXML private Label errorLabel;
    @FXML private BorderPane gameLayer;
    @FXML private VBox gameOverOverlay;
    @FXML private VBox leaderboardContainer;
    @FXML private Label myNameLabel;
    @FXML private Label opponentNameLabel;
    @FXML private Label turnLabel;
    @FXML private VBox waitingOverlay;
    @FXML private Label winnerLabel;

    private static final int SIZE = 8;
    private static final Color HIGHLIGHT_GREEN = Color.rgb(0, 255, 0, 0.5);
    private static final Color HIGHLIGHT_LIGHT_GREEN = Color.rgb(128, 255, 128, 0.5);

    private SocketService socketService;
    private String myUsername;
    private String opponentUsername;
    private boolean isWhitePlayer;

    private boolean isMyTurn = false;
    private CheckerType[][] grid;
    private List<CheckerMove> availableMoves = new ArrayList<>();
    private Position selectedPosition = null;

    private ImageView[][] checkerViews = new ImageView[SIZE][SIZE];
    private Rectangle[][] highlightRects = new Rectangle[SIZE][SIZE];

    private final Image bgBlack = new Image(getClass().getResourceAsStream("/images/bg_black.png"));
    private final Image bgWhite = new Image(getClass().getResourceAsStream("/images/bg_white.png"));
    private final Image checkerWhite = new Image(getClass().getResourceAsStream("/images/checker_white.png"));
    private final Image checkerWhiteKing = new Image(getClass().getResourceAsStream("/images/checker_white_king.png"));
    private final Image checkerBlack = new Image(getClass().getResourceAsStream("/images/checker_black.png"));
    private final Image checkerBlackKing = new Image(getClass().getResourceAsStream("/images/checker_black_king.png"));

    public void initData(SocketService socketService, String myUsername) {
        this.socketService = socketService;
        this.myUsername = myUsername;
        this.myNameLabel.setText("You: " + myUsername);

        setupSocketService();
    }

    private void setupSocketService() {
        this.socketService.setHandleServerMessage((serverMessage) -> {
            Platform.runLater(() -> {
                switch (serverMessage.getType()) {
                    case WAITING:
                        waitingOverlay.setVisible(true);
                        break;

                    case GAME_START:
                        GameStartData gameStartData = serverMessage.getContentAs(GameStartData.class);
                        opponentUsername = gameStartData.getOpponentUsername();
                        isWhitePlayer = gameStartData.isWhitePlayer();
                        opponentNameLabel.setText("Opponent: " + opponentUsername);
                        grid = gameStartData.getGrid();

                        buildBoard();
                        updateBoard();
                        waitingOverlay.setVisible(false);
                        break;

                    case YOUR_TURN:
                        isMyTurn = true;
                        turnLabel.setText(myUsername + (isWhitePlayer ? " (White)'s " : " (Black)'s ") + "turn");
                        turnLabel.setTextFill(Color.GREEN);

                        CheckerMove[] availableMovesArr = serverMessage.getContentAs(CheckerMove[].class);
                        availableMoves = Arrays.asList(availableMovesArr);
                        showStartPositionHighlights();
                        break;

                    case OPPONENT_TURN:
                        isMyTurn = false;
                        turnLabel.setText(opponentUsername + (isWhitePlayer ? " (Black)'s " : " (White)'s ") + "turn");
                        turnLabel.setTextFill(Color.RED);

                        clearHighlights();
                        selectedPosition = null;
                        break;

                    case BOARD_UPDATE:
                        grid = serverMessage.getContentAs(CheckerType[][].class);
                        updateBoard();
                        break;

                    case ERROR:
                        errorLabel.setText(serverMessage.getContentAs(String.class));
                        break;

                    case GAME_OVER:
                        GameOverData gameOverData = serverMessage.getContentAs(GameOverData.class);
                        showGameOver(gameOverData);
                        break;

                    default:
                        break;
                }
            });
        });
    }

    @FXML
    void handlePlayAgain(ActionEvent event) {
        gameOverOverlay.setVisible(false);
        waitingOverlay.setVisible(true);
        socketService.getMessageHandler().sendMessage(MessageType.PLAY_AGAIN, "");
    }

    @FXML
    void handleQuit(ActionEvent event) {
        socketService.getMessageHandler().sendMessage(MessageType.QUIT, "");
        Platform.exit();
        System.exit(0);
    }

    private void buildBoard() {
        boardGrid.getChildren().clear();
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        boardGrid.setMinSize(640, 640);
        boardGrid.setMaxSize(640, 640);

        for (int i = 0; i < SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(12.5);
            boardGrid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(12.5);
            boardGrid.getRowConstraints().add(row);
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                int playerRow = isWhitePlayer ? row : (7 - row);
                int playerCol = isWhitePlayer ? col : (7 - col);

                StackPane tilePane = new StackPane();
                boolean isBlackTile = (row + col) % 2 != 0;

                ImageView tileView = new ImageView(isBlackTile ? bgBlack : bgWhite);
                tileView.setFitWidth(80);
                tileView.setFitHeight(80);
                tileView.setPreserveRatio(true);

                Rectangle highlight = new Rectangle(80, 80);
                highlight.setFill(Color.TRANSPARENT);
                highlightRects[row][col] = highlight;

                ImageView checkerView = new ImageView();
                checkerView.setFitWidth(80);
                checkerView.setFitHeight(80);
                checkerView.setPreserveRatio(true);
                checkerViews[row][col] = checkerView;

                tilePane.getChildren().addAll(tileView, highlight, checkerView);

                final int finalRow = row;
                final int finalCol = col;
                tilePane.setOnMouseClicked(event -> handleTileClick(new Position(finalRow, finalCol)));

                boardGrid.add(tilePane, playerCol, playerRow);
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                CheckerType type = grid[row][col];
                switch (type) {
                    case WHITE: checkerViews[row][col].setImage(checkerWhite); break;
                    case WHITE_KING: checkerViews[row][col].setImage(checkerWhiteKing); break;
                    case BLACK: checkerViews[row][col].setImage(checkerBlack); break;
                    case BLACK_KING: checkerViews[row][col].setImage(checkerBlackKing); break;
                    default: checkerViews[row][col].setImage(null); break;
                }
            }
        }
    }

    private void handleTileClick(Position clickedPosition) {
        if (!isMyTurn) return;
        errorLabel.setText("");

        if (hasMovesFrom(clickedPosition)) {
            selectedPosition = clickedPosition;
            showHighlightsFor(selectedPosition);
            return;
        }

        if (selectedPosition != null) {
            CheckerMove chosenMove = getValidMove(selectedPosition, clickedPosition);

            if (chosenMove != null) {
                CheckerMove simplifiedMove = new CheckerMove(chosenMove.getStartPosition(), new ArrayList<>(), new ArrayList<>(), chosenMove.getEndPosition());
                socketService.getMessageHandler().sendMessage(MessageType.MOVE, simplifiedMove);

                isMyTurn = false;
                clearHighlights();
                selectedPosition = null;
            } else {
                errorLabel.setText("Incorrect move");
                selectedPosition = null;
                showStartPositionHighlights();
            }
        }
    }

    private boolean hasMovesFrom(Position position) {
        for (CheckerMove checkerMove : availableMoves) {
            if (checkerMove.getStartPosition().equals(position)) return true;
        }
        return false;
    }

    private CheckerMove getValidMove(Position startPosition, Position endPosition) {
        for (CheckerMove move : availableMoves) {
            if (move.getStartPosition().equals(startPosition) && move.getEndPosition().equals(endPosition)) {
                return move;
            }
        }
        return null;
    }

    private void clearHighlights() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                highlightRects[row][col].setFill(Color.TRANSPARENT);
            }
        }
    }

    private void showStartPositionHighlights() {
        clearHighlights();
        for (CheckerMove move : availableMoves) {
            Position startPosition = move.getStartPosition();
            highlightRects[startPosition.getRow()][startPosition.getCol()].setFill(HIGHLIGHT_GREEN);
        }
    }

    private void showHighlightsFor(Position startPosition) {
        clearHighlights();

        highlightRects[startPosition.getRow()][startPosition.getCol()].setFill(HIGHLIGHT_GREEN);

        for (CheckerMove checkerMove : availableMoves) {
            if (checkerMove.getStartPosition().equals(startPosition)) {
                if (checkerMove.getLandingPositions() != null) {
                    for (Position landingPosition : checkerMove.getLandingPositions()) {
                        highlightRects[landingPosition.getRow()][landingPosition.getCol()].setFill(HIGHLIGHT_LIGHT_GREEN);
                    }
                }

                if (checkerMove.getCapturedPositions() != null) {
                    for (Position capturedPosition : checkerMove.getCapturedPositions()) {
                        highlightRects[capturedPosition.getRow()][capturedPosition.getCol()].setFill(HIGHLIGHT_LIGHT_GREEN);
                    }
                }

                Position endPosition = checkerMove.getEndPosition();
                highlightRects[endPosition.getRow()][endPosition.getCol()].setFill(HIGHLIGHT_GREEN);
            }
        }
    }

    private void showGameOver(GameOverData gameOverData) {
        gameOverOverlay.setVisible(true);
        if (gameOverData.isWinner()) {
            winnerLabel.setText(myUsername + (isWhitePlayer ? " (White) " : " (Black) ") + "won");
        } else {
            winnerLabel.setText(opponentUsername + (isWhitePlayer ? " (Black) " : " (White) ") + "won");
        }

        leaderboardContainer.getChildren().clear();
        Label title = new Label("Leaderboard");
        title.setTextFill(Color.WHITE);
        title.setFont(new Font("System Bold", 18));
        leaderboardContainer.getChildren().add(title);

        int rank = 1;
        for (ClientStats clientStats : gameOverData.getLeaderboard()) {
            Label statLabel = new Label(rank + ". " + clientStats.getUsername() + " | Games won: " + clientStats.getGamesWon() + " | Games played: " + clientStats.getGamesPlayed());
            statLabel.setTextFill(Color.WHITE);
            statLabel.setFont(new Font("System", 14));
            leaderboardContainer.getChildren().add(statLabel);
            rank++;
        }
    }
}
