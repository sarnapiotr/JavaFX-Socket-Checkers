package pl.edu.pk.checkers.server;

import com.google.gson.Gson;
import pl.edu.pk.checkers.common.board.Board;
import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.IOException;

public class GameSession implements Runnable {
    private ClientHandler client1Handler;
    private ClientHandler client2Handler;
    private Board board = new Board();
    private static final Gson gson = new Gson();

    public GameSession(ClientHandler client1Handler, ClientHandler client2Handler) {
        this.client1Handler = client1Handler;
        this.client2Handler = client2Handler;
    }

    @Override
    public void run() {
        String jsonInput = null;
        Message clientMessage = null;
        Message serverMessage = null;

        System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " succesfully connected, recieved GameSession Thread");

        try {
            serverMessage = new Message(MessageType.GAME_START, gson.toJsonTree("Succesful connection between Client " + client1Handler.getUsername() +
                    " and Client " + client2Handler.getUsername() + "\nBoard: \n" + board.toString()));
            client1Handler.getOut().println(gson.toJson(serverMessage));
            client2Handler.getOut().println(gson.toJson(serverMessage));

            boolean isClient1Turn = true; // Client1 is WhitePlayer, Client2 is BlackPlayer

            while (true) {
                ClientHandler activeClientHandler = isClient1Turn ? client1Handler : client2Handler;

                serverMessage = new Message(MessageType.YOUR_TURN, gson.toJsonTree(""));
                activeClientHandler.getOut().println(gson.toJson(serverMessage));

                jsonInput = activeClientHandler.getIn().readLine();
                if (jsonInput == null) break;
                clientMessage = gson.fromJson(jsonInput, Message.class);

                if (gson.fromJson(clientMessage.getContent(), String.class).equals("---TERMINATE---")) {
                    break;
                }

                if (clientMessage.getType() == MessageType.MOVE) {
                    serverMessage = new Message(MessageType.BOARD_UPDATE, gson.toJsonTree("Client " + activeClientHandler.getUsername() + " move: " + clientMessage.getContent()));
                    client1Handler.getOut().println(gson.toJson(serverMessage));
                    client2Handler.getOut().println(gson.toJson(serverMessage));

                    isClient1Turn = !isClient1Turn;
                }
            }

            serverMessage = new Message(MessageType.GAME_OVER, gson.toJsonTree(""));
            client1Handler.getOut().println(gson.toJson(serverMessage));
            client2Handler.getOut().println(gson.toJson(serverMessage));
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        } finally {
            try { client1Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            try { client2Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " session terminated");
        }
    }
}
