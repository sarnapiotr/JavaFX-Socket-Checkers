package pl.edu.pk.checkers.server;

import java.io.IOException;

public class GameSession implements Runnable {
    private ClientData client1Data;
    private ClientData client2Data;

    public GameSession(ClientData client1Data, ClientData client2Data) {
        this.client1Data = client1Data;
        this.client2Data = client2Data;
    }

    @Override
    public void run() {
        System.out.println("Clients: " + client1Data.getUsername() + ", " + client2Data.getUsername() + " succesfully connected, recieved GameSession Thread");

        try {
            client1Data.getOut().println("Succesful connection between Client " + client1Data.getUsername() + " and Client " + client2Data.getUsername());
            client2Data.getOut().println("Succesful connection between Client " + client1Data.getUsername() + " and Client " + client2Data.getUsername());

            client1Data.getOut().println("---TERMINATE---");
            client2Data.getOut().println("---TERMINATE---");
        } finally {
            try { client1Data.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            try { client2Data.getSocket().close(); } catch (IOException e) { System.err.println("Error caught: " + e.getMessage()); }
            System.out.println("Client " + client1Data.getUsername() + " and Client " + client2Data.getUsername() + " session terminated");
        }

    }
}
