package pl.edu.pk.checkers.server;

import java.io.IOException;

public class GameSession implements Runnable {
    private ClientHandler client1Handler;
    private ClientHandler client2Handler;

    public GameSession(ClientHandler client1Handler, ClientHandler client2Handler) {
        this.client1Handler = client1Handler;
        this.client2Handler = client2Handler;
    }

    @Override
    public void run() {
        System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " succesfully connected, recieved GameSession Thread");

        try {
            client1Handler.getOut().println("Succesful connection between Client " + client1Handler.getUsername() + " and Client " + client2Handler.getUsername());
            client2Handler.getOut().println("Succesful connection between Client " + client1Handler.getUsername() + " and Client " + client2Handler.getUsername());

            client1Handler.getOut().println("---TERMINATE---");
            client2Handler.getOut().println("---TERMINATE---");
        } finally {
            try { client1Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            try { client2Handler.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            System.out.println("Clients: " + client1Handler.getUsername() + ", " + client2Handler.getUsername() + " session terminated");
        }

    }
}
