package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.board.Board;
import pl.edu.pk.checkers.common.board.CheckerMove;
import pl.edu.pk.checkers.common.message.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSession implements Runnable {
    private static final Logger logger = Logger.getLogger(GameSession.class.getName());
    private final ClientHandler client1Handler; // Always WhitePlayer
    private final ClientHandler client2Handler; // Always BlackPlayer
    private final DatabaseManager databaseManager;
    private final ServerLobby serverLobby;
    private final Board board = new Board();

    public GameSession(ClientHandler client1Handler, ClientHandler client2Handler, DatabaseManager databaseManager, ServerLobby serverLobby) {
        this.client1Handler = client1Handler;
        this.client2Handler = client2Handler;
        this.databaseManager = databaseManager;
        this.serverLobby = serverLobby;
    }

    @Override
    public void run() {
        logger.info("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " successfully connected, received GameSession Thread");

        try {
            Thread.sleep(100);
            client1Handler.getMessageHandler().sendMessage(MessageType.GAME_START, new GameStartData(true, client2Handler.getUsername(), board.getGrid()));
            client2Handler.getMessageHandler().sendMessage(MessageType.GAME_START, new GameStartData(false, client1Handler.getUsername(), board.getGrid()));

            boolean isClient1Turn = true; // Client1 is WhitePlayer, Client2 is BlackPlayer
            ClientHandler winnerClientHandler = null;
            ClientHandler loserClientHandler = null;

            while (true) {
                ClientHandler activeClientHandler = isClient1Turn ? client1Handler : client2Handler;
                ClientHandler passiveClientHandler = isClient1Turn ? client2Handler : client1Handler;

                List<CheckerMove> availableMoves = board.getAvailableMoves(isClient1Turn);
                if (availableMoves.isEmpty()) {
                    winnerClientHandler = passiveClientHandler;
                    loserClientHandler = activeClientHandler;
                    break;
                }

                activeClientHandler.getMessageHandler().sendMessage(MessageType.YOUR_TURN, availableMoves);
                passiveClientHandler.getMessageHandler().sendMessage(MessageType.OPPONENT_TURN, "");

                Message clientMessage = activeClientHandler.getMessageHandler().receiveMessage();

                if (clientMessage == null) {
                    winnerClientHandler = passiveClientHandler;
                    loserClientHandler = activeClientHandler;
                    break;
                }

                if (clientMessage.getType() == MessageType.MOVE) {
                    CheckerMove clientMove = clientMessage.getContentAs(CheckerMove.class);
                    CheckerMove executableClientMove = null;

                    for (CheckerMove availableMove : availableMoves) {
                        if (availableMove.getStartPosition().equals(clientMove.getStartPosition()) && availableMove.getEndPosition().equals(clientMove.getEndPosition())) {
                            executableClientMove = availableMove;
                            break;
                        }
                    }

                    if (executableClientMove != null) {
                        board.executeMove(executableClientMove);

                        client1Handler.getMessageHandler().sendMessage(MessageType.BOARD_UPDATE, board.getGrid());
                        client2Handler.getMessageHandler().sendMessage(MessageType.BOARD_UPDATE, board.getGrid());

                        isClient1Turn = !isClient1Turn;
                    } else {
                        activeClientHandler.getMessageHandler().sendMessage(MessageType.ERROR, "Incorrect move");
                    }
                }
            }

            if (winnerClientHandler != null && loserClientHandler != null) {
                databaseManager.updateStats(winnerClientHandler.getClientId(), true);
                databaseManager.updateStats(loserClientHandler.getClientId(), false);

                List<ClientStats> leaderboard = databaseManager.getClientStats();

                winnerClientHandler.getMessageHandler().sendMessage(MessageType.GAME_OVER, new GameOverData(true, leaderboard));
                loserClientHandler.getMessageHandler().sendMessage(MessageType.GAME_OVER, new GameOverData(false, leaderboard));
            }

            handlePostGameDecision(client1Handler);
            handlePostGameDecision(client2Handler);
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error caught", e);
            try { client1Handler.getSocket().close(); } catch (IOException ex) { logger.log(Level.SEVERE, "Error caught", ex); }
            try { client2Handler.getSocket().close(); } catch (IOException ex) { logger.log(Level.SEVERE, "Error caught", ex); }
        }
    }

    private void handlePostGameDecision(ClientHandler clientHandler) {
        new Thread(() -> {
            try {
                Message clientMessage = clientHandler.getMessageHandler().receiveMessage();
                if (clientMessage != null && clientMessage.getType() == MessageType.PLAY_AGAIN) {
                    serverLobby.addClientToQueue(clientHandler);
                } else {
                    clientHandler.getSocket().close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error caught", e);
                try { clientHandler.getSocket().close(); } catch (IOException ex) { logger.log(Level.SEVERE, "Error caught", ex); }
            }
        }).start();
    }
}