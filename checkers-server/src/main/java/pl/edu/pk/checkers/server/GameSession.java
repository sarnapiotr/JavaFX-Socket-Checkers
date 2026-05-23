package pl.edu.pk.checkers.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameSession implements Runnable {
    private Socket client1Socket;
    private int client1Id;
    private Socket client2Socket;
    private int client2Id;

    public GameSession(Socket client1Socket, int client1Id, Socket client2Socket, int client2Id) {
        this.client1Socket = client1Socket;
        this.client1Id = client1Id;
        this.client2Socket = client2Socket;
        this.client2Id = client2Id;
    }

    @Override
    public void run() {
        System.out.println("Clients: " + client1Id + ", " + client2Id + " succesfully connected, recieved GameSession Thread");

        try ( PrintWriter client1Out = new PrintWriter(client1Socket.getOutputStream(), true);
              BufferedReader client1In = new BufferedReader(new InputStreamReader(client1Socket.getInputStream()));
              PrintWriter client2Out = new PrintWriter(client2Socket.getOutputStream(), true);
              BufferedReader client2In = new BufferedReader(new InputStreamReader(client2Socket.getInputStream())) ) {

            client1Out.println("Succesful connection between Client" + client1Id + " and Client" + client2Id);
            client2Out.println("Succesful connection between Client" + client1Id + " and Client" + client2Id);

            client1Out.println("---TERMINATE---");
            client2Out.println("---TERMINATE---");

        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }
}
