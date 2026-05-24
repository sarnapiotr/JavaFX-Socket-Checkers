package pl.edu.pk.checkers.server;

import com.google.gson.Gson;
import pl.edu.pk.checkers.common.Message;
import pl.edu.pk.checkers.common.MessageType;

import java.io.IOException;

public class GameSession implements Runnable {
    private ClientHandler client1Handler;
    private ClientHandler client2Handler;
    private static final Gson gson = new Gson();

    public GameSession(ClientHandler client1Handler, ClientHandler client2Handler) {
        this.client1Handler = client1Handler;
        this.client2Handler = client2Handler;
    }

    @Override
    public void run() {
        Message clientMessage = null;
        Message serverMessage = null;

        System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " succesfully connected, recieved GameSession Thread");

        try {
            clientMessage = new Message(MessageType.GAME_START, "Succesful connection between Client " + client1Handler.getUsername() + " and Client " + client2Handler.getUsername());
            client1Handler.getOut().println(gson.toJson(clientMessage));
            client2Handler.getOut().println(gson.toJson(clientMessage));

            clientMessage = new Message(MessageType.GAME_OVER, "");
            client1Handler.getOut().println(gson.toJson(clientMessage));
            client2Handler.getOut().println(gson.toJson(clientMessage));
        } finally {
            try { client1Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            try { client2Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " session terminated");
        }

    }
}
