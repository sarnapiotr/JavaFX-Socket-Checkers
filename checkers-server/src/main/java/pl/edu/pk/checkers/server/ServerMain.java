package pl.edu.pk.checkers.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Server started");
        DatabaseManager databaseManager = new DatabaseManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT);
              ExecutorService pool = Executors.newCachedThreadPool()) {

            System.out.println("Server listening on port " + PORT);
            ServerLobby serverLobby = new ServerLobby(pool, databaseManager);
            pool.execute(serverLobby);

            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new LoginSession(socket, databaseManager, serverLobby));
            }
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }

        System.out.println("Server terminated");
    }
}