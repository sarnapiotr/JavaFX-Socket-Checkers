package pl.edu.pk.checkers.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 8080;

    public static void main(String args[]) {
        System.out.println("Server started");
        ExecutorService pool = Executors.newCachedThreadPool();

        try ( ServerSocket serverSocket = new ServerSocket(PORT) ) {
            System.out.println("Server listening on port " + PORT);

            int clientCounter = 1;
            while (true) {
                Socket client1Socket = serverSocket.accept();
                int client1Id = clientCounter++;
                System.out.println("[CLIENT " + client1Id + " CONNECTED]");

                Socket client2Socket = serverSocket.accept();
                int client2Id = clientCounter++;
                System.out.println("[CLIENT " + client2Id + " CONNECTED]");

                GameSession gameSession = new GameSession(client1Socket, client1Id, client2Socket, client2Id);
                pool.execute(gameSession);
            }
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        } finally {
            pool.shutdown();
            System.out.println("Server terminated");
        }
    }
}
