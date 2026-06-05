package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.board.Board;
import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.IOException;

public class GameSession implements Runnable {
    private final ClientHandler client1Handler;
    private final ClientHandler client2Handler;
    private final DatabaseManager databaseManager;
    private final Board board = new Board();

    public GameSession(ClientHandler client1Handler, ClientHandler client2Handler, DatabaseManager databaseManager) {
        this.client1Handler = client1Handler;
        this.client2Handler = client2Handler;
        this.databaseManager = databaseManager;
    }

    @Override
    public void run() {
        System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " successfully connected, received GameSession Thread");

        try {
            String startMessage = "Succesful connection between Client " + client1Handler.getUsername() + " and Client " + client2Handler.getUsername() + "\nBoard: \n" + board;
            client1Handler.getMessageHandler().sendMessage(MessageType.GAME_START, startMessage);
            client2Handler.getMessageHandler().sendMessage(MessageType.GAME_START, startMessage);

            boolean isClient1Turn = true; // Client1 is WhitePlayer, Client2 is BlackPlayer
            Message clientMessage = null;
            while (true) {
                ClientHandler activeClientHandler = isClient1Turn ? client1Handler : client2Handler;

                activeClientHandler.getMessageHandler().sendMessage(MessageType.YOUR_TURN, "");

                if ((clientMessage = activeClientHandler.getMessageHandler().receiveMessage()) == null) break;

                if (clientMessage.getContentAs(String.class).equals("---TERMINATE---")) {
                    break;
                }

                if (clientMessage.getType() == MessageType.MOVE) {
                    String moveMessage = "Client " + activeClientHandler.getUsername() + " move: " + clientMessage.getContentAs(String.class);
                    client1Handler.getMessageHandler().sendMessage(MessageType.BOARD_UPDATE, moveMessage);
                    client2Handler.getMessageHandler().sendMessage(MessageType.BOARD_UPDATE, moveMessage);

                    isClient1Turn = !isClient1Turn;
                }
            }

            client1Handler.getMessageHandler().sendMessage(MessageType.GAME_OVER, "");
            client2Handler.getMessageHandler().sendMessage(MessageType.GAME_OVER, "");
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        } finally {
            try { client1Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            try { client2Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " session terminated");
        }
    }
}
